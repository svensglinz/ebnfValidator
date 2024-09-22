package ebnfValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizes an EBNF rule into its special elements as defined in TokenTypes
 */

// ebnfValidator.EBNF LEXER
class RuleLexer {
    List<Token> tokens = new ArrayList<>();
    int index = 0;
    String string;
    int line;

    public RuleLexer(String string, int line) {
        this.string = string;
        this.line = line;
    }

    void addToken(Token token) {
        tokens.add(token);
    }

    boolean isAtEnd() {
        return index >= string.length();
    }

    private void consume(char expected) {
        if (index >= string.length() || string.charAt(index) != expected) {
            String found = index >= string.length() ? "EOF" : string.substring(index, index + 1);
            Logger.error("Error while parsing line " + line + " expected " + expected + " but found: " + found);
        }
        index++;
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
            case '(' -> addToken(new Token(TokenType.GROUP_OPEN));
            case ')' -> addToken(new Token(TokenType.GROUP_CLOSE));
            case '{' -> addToken(new Token(TokenType.MULTIPLE_OPEN));
            case '}' -> addToken(new Token(TokenType.MULTIPLE_CLOSE));
            case '[' -> addToken(new Token(TokenType.OPTION_OPEN));
            case ']' -> addToken(new Token(TokenType.OPTION_CLOSE));
            case '|' -> addToken(new Token(TokenType.SELECT));
            case ' ' -> {
            }// skip whitespace
            case '"' -> addSpecialLiteral(); // literals []{}()|
            case '<' -> addRule();
            default -> addLiteral();
        }
    }

    private void addSpecialLiteral() {

        int start = index;

        while (peek() != '"' && !isAtEnd()) {
            advance();
        }

        // empty string
        if (index == start) addToken(new Token("\"\"", TokenType.EMPTY));
        else addToken(new Token(string.substring(start, index), TokenType.LITERAL));
        consume('"');
    }

    private void addLiteral() {
        int start = index;

        while (peek() != ' ' && !isAtEnd() && !isSpecial(peek())) {
            advance();
        }
        addToken(new Token(string.substring(start - 1, index), TokenType.LITERAL));
    }

    private boolean isSpecial(char c) {
        return c == '|' || c == '[' || c == ']' || c == '(' || c == ')' || c == '{' || c == '}';
    }

    private void addRule() {
        int start = index;
        // add all forbidden characters !
        while (peek() != '>' && !isAtEnd()) {
            advance();
        }
        addToken(new Token(string.substring(start, index), TokenType.RULE));
        // consume closing >
        consume('>');
    }
}
