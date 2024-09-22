package ebnfValidator;

import java.util.List;
import java.util.Stack;

import static ebnfValidator.TokenType.*;

/**
 * parses a single body of a rule
 */
class RuleParser {
    List<Token> tokens;
    int idx = 0;
    int lineNr;
    public RuleParser(List<Token> tokens, int lineNr) {
        this.tokens = tokens;
        this.lineNr = lineNr;
    }

    private void consumeToken(TokenType expected) {
        if (idx >= tokens.size() || !tokens.get(idx).type.equals(expected)) {
            String found = idx >= tokens.size() ? "EOF" : tokens.get(idx).value;
            Logger.error("Error while parsing line " + lineNr + "\n" + "Expected " + expected + " but found: " + found);
        }
        idx++;
    }

    private Token advance() {
        return tokens.get(idx++);
    }

    Expression parseExpression() {
        Expression expression = new Expression();
        while (idx < tokens.size()) {
            Token next = tokens.get(idx);
            switch (next.type) {
                case SELECT_OPEN -> expression.add(parseSelect());
                case LITERAL -> expression.add(new Terminal(advance().value));
                case GROUP_OPEN -> expression.add(parseGroup());
                case MULTIPLE_OPEN -> expression.add(parseMultiple());
                case OPTION_OPEN -> expression.add(parseOption());
                case RULE -> expression.add(new Rule(advance().value));
                default -> {
                    return expression;
                }
            }
        }
        return expression;
    }

    Expression parse() {
        preprocessSelect(tokens);
        return parseExpression();
    }

    private Select parseSelect() {
        consumeToken(SELECT_OPEN);
        Expression expression = parseExpression();
        consumeToken(SELECT_CLOSE);
        return new Select(expression);
    }

    private Group parseGroup() {
        consumeToken(GROUP_OPEN);
        Expression expression = parseExpression();
        consumeToken(GROUP_CLOSE);
        return new Group(expression);
    }

    private Multiple parseMultiple() {
        consumeToken(MULTIPLE_OPEN);
        Expression expression = parseExpression();
        consumeToken(MULTIPLE_CLOSE);
        return new Multiple(expression);
    }

    private Option parseOption() {
        consumeToken(OPTION_OPEN);
        Expression expression = parseExpression();
        consumeToken(OPTION_CLOSE);
        return new Option(expression);
    }

    private void preprocessSelect(List<Token> tokens) {
        Stack<Integer> startIndex = new Stack<>();

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (isOpen(t)) {
                startIndex.push(i);
            } else if (isClose(t)) {
                int sizeOld = tokens.size();
                int start = startIndex.pop();

                // if block is enclosed brackets, process only inner part
                if (isOpen(tokens.get(start)) && isClose(tokens.get(i)))
                    preprocessBlock(start + 1, i - 1);
                else
                    preprocessBlock(start, i);
                // advance index to new end position
                i += tokens.size() - sizeOld;
            }
        }
        // process entire expression
        preprocessBlock(0, tokens.size() - 1);
    }

    private void preprocessBlock(int start, int end) {
        int countOpen = 0;
        boolean hasSelect = false;
        for (int i = start; i <= end; i++) {

            Token t = tokens.get(i);
            if (isOpen(t))
                countOpen++;
            else if (isClose(t))
                countOpen--;
            else if (t.type.equals(SELECT) && countOpen == 0) {
                hasSelect = true;
                tokens.remove(i);
                tokens.add(i, new Token(SELECT_CLOSE));
                tokens.add(i + 1, new Token(SELECT_OPEN));

                // added 1 more token
                end++;
                i++;
            }
        }

        // opening and closing select of block
        if (hasSelect) {
            tokens.add(start, new Token(SELECT_OPEN));
            tokens.add(end + 2, new Token(SELECT_CLOSE));
        }
    }

    private boolean isOpen(Token token) {
        return token.type.equals(GROUP_OPEN) || token.type.equals(OPTION_OPEN) || token.type.equals(MULTIPLE_OPEN);
    }

    private boolean isClose(Token token) {
        return token.type.equals(GROUP_CLOSE) || token.type.equals(OPTION_CLOSE) || token.type.equals(MULTIPLE_CLOSE);
    }
}
