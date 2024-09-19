package ebnfValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Lexer classifies an ebnfValidator.EBNF rule into its rules, which are the special elements {}()| as well
 * as rule names and terminal values (literals)
 */

// ebnfValidator.EBNF LEXER
class EBNFLexer {
    List<Token> tokens = new ArrayList<>();
    int index = 0;
    String string;

    public EBNFLexer(String string) {
        this.string = string;
    }

    void addToken(Token token) {
        tokens.add(token);
    }

    boolean isAtEnd() {
        return index >= string.length();
    }

    char advance() {
        return string.charAt(index++);
    }

    public List<Token> tokenize() {
        while (!isAtEnd()) {
            scanToken();
        }
        return tokens;
    }

    char peek() {
        if (isAtEnd()) {
            return '\0';
        } else {
            return string.charAt(index);
        }
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(new Token(TokenType.LEFT_PAREN));
                break;
            case ')':
                addToken(new Token(TokenType.RIGHT_PAREN));
                break;
            case '{':
                addToken(new Token(TokenType.LEFT_BRACE));
                break;
            case '}':
                addToken(new Token(TokenType.RIGHT_BRACE));
                break;
            case '[':
                addToken(new Token(TokenType.LEFT_BRACKET));
                break;
            case ']':
                addToken(new Token(TokenType.SELECT_CLOSE));
                break;
            case '|':
                addToken(new Token(TokenType.SEP));
                break;
            case ' ':
                break;
            case '"':
                addSpecialLiteral();
                break;
            case '<':
                addRule();
                break;
            default:
                addLiteral();
        }
    }

    private void addSpecialLiteral() {

        int start = index;

        while (peek() != '"' && !isAtEnd()) {
            advance();
        }

        if (peek() != '"') {
            throw new IllegalArgumentException("No closing \" detected");
        }

        if (index == start)
            addToken(new Token("", TokenType.EMPTY));
        else
            addToken(new Token(string.substring(start, index), TokenType.LITERAL));
        // consume closing "
        advance();
    }

    private void addLiteral() {
        int start = index;

        while (peek() != ' ' && !isAtEnd() && peek() != '"') {
            advance();
        }
        addToken(new Token(string.substring(start - 1, index), TokenType.LITERAL));
    }

    private void addRule() {
        int start = index;
        // add all forbidden characters !
        while (peek() != '>' && !isAtEnd()) {
            advance();
        }

        if (peek() != '>') {
            throw new IllegalArgumentException("No closing > detected");
        }

        addToken(new Token(string.substring(start, index), TokenType.RULE));
        // consume closing >
        advance();
    }

}
