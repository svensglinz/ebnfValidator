package ebnfValidator;

import java.util.Stack;
import java.util.List;

import static ebnfValidator.TokenType.*;

/**
 * parses a single body of a rule
 */
class EBNFParser {
    private final Stack<GrammarElement> elementStack = new Stack<>();
    List<Token> tokens;
    GrammarElement rootElem = new Expression("group");
    int index = 0;

    public EBNFParser(List<Token> tokens){
        this.tokens = tokens;
        elementStack.push(rootElem);
    }

    // checks if the next parsing block is separated by a selector
    private boolean isNextSelect() {
        Token current = peekToken();
        int target;
        switch (current.type) {
            case LEFT_BRACE -> target = nextPosition(RIGHT_BRACE) + 1;
            case LEFT_BRACKET -> target = nextPosition(SELECT_CLOSE) + 1;
            case LEFT_PAREN -> target = nextPosition(RIGHT_PAREN) + 1;
            case RIGHT_BRACE, SELECT_CLOSE, RIGHT_PAREN -> {
                return false;
            }
            default -> target = index + 1;
        }
        if (target > 0 && target < tokens.size() - 1) {
            return tokens.get(target).type == TokenType.OPTION;
        }
        return false;
    }

    // returns whether previous statement was select
    private boolean isPreviousSelect() {
        return false;
    }
    // returns the next position where the tokenType is equal to `type`
    private int nextPosition(TokenType type) {
        for (int i = index; i < tokens.size(); i++) {
            if (tokens.get(i).type == type) {
                return i;
            }
        }
        return -1;
    }

    private Token nextToken() {
        return tokens.get(index++);
    }

    private Token peekToken() {
        return tokens.get(index);
    }

    // return the top element on the stack
    private GrammarElement currentElement() {
        return elementStack.peek();
    }

    private boolean hasNextToken() {
        return index < tokens.size();
    }

    private GrammarElement parseGroup() {

        GrammarElement group = new Expression("group");
        elementStack.push(group);

        while (hasNextToken()) {

            // if current group ends with a selector --> add a select element
            if (isNextSelect()) {
                GrammarElement e = parseSelect();
                currentElement().addElement(e);
            }

            // extremely messy. make this more readable !!!
            Token token;
            if (hasNextToken())
                token = nextToken();
            else
                return group;

            switch (token.type) {

                case RULE, LITERAL: currentElement().addElement(new Node(token)); break;
                case LEFT_PAREN: currentElement().addElement(parseGroup()); break;
                case LEFT_BRACE: {
                    GrammarElement e = parseMultiple();
                    currentElement().addElement(e);
                } break;
                case LEFT_BRACKET: currentElement().addElement(parseOption()); break;
                case OPTION: {} continue;
                case RIGHT_PAREN, RIGHT_BRACE, SELECT_CLOSE: {
                    elementStack.pop();
                    return group;
                }
            }
        }
        // closing tags
        elementStack.pop();
        return group;
    }

    // checks if the previous element was a selector
    private boolean prevSelect() {
        if (index - 1 >= 0) {
            return tokens.get(index - 1).type == TokenType.OPTION;
        } else
            return false;
    }

    private GrammarElement parseSelect() {
        GrammarElement select = new Expression("select");
        elementStack.push(select);

        // add groups to the current selection expression
        do {
            Token token = nextToken();
            switch (token.type) {
                case RULE, LITERAL: currentElement().addElement(new Node(token)); break;
                case LEFT_PAREN: {
                    GrammarElement g = parseGroup();
                    currentElement().addElement(g);
                } break;
                case LEFT_BRACE: {
                    GrammarElement e = parseMultiple();
                    currentElement().addElement(e);
                } break;
                case LEFT_BRACKET: currentElement().addElement(parseOption()); break;
                case RIGHT_PAREN, RIGHT_BRACE, SELECT_CLOSE: break;
            }
            // consume separator if present
            if (index < tokens.size() && peekToken().type == TokenType.OPTION) {
                nextToken();
            }
        } while (hasNextToken() && prevSelect());
        elementStack.pop();
        return select;
    }

    private GrammarElement parseOption(){
        GrammarElement option = new Expression("option");
        option.addElement(parseGroup());
        return option;
    }

    private GrammarElement parseMultiple() {
        GrammarElement multiple = new Expression("multiple");
        elementStack.push(multiple);
        GrammarElement group = parseGroup();
        multiple.addElement(group);
        elementStack.pop();
        return multiple;
    }

    // top element of any rule is always a group
    public GrammarElement parse() {
        return parseGroup();
    }
}