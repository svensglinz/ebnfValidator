package ebnfValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tokenizes a statement into different tokens (literals) given the set of possible
 * literals that occur in the Grammar Rules.
 */

class StatementLexer {
    Set<Token> EBNFTokens;
    List<Token> tokens = new ArrayList<>();
    String statement;
    int index;

    public StatementLexer(String statement, Set<Token> EBNFTokens) {
        this.statement = statement;
        this.EBNFTokens = EBNFTokens;
    }

    void scanToken() {
        // skip whitespace
        while (statement.charAt(index) == ' ') {
            index++;
        }

        // scanner ignores whitespace but two rules separated by whitespace can never be one
        // find longest word that matches a token in the set of allowed tokens
        Token match = new Token("", TokenType.EMPTY);
        for (Token t : EBNFTokens) {
            if (statement.startsWith(t.value, index)) {
                match = t.value.length() > match.value.length() ? t : match;
            }
        }
        // what if statement we want to match is empty ?
        if (match.type == TokenType.EMPTY) {
            throw new IllegalArgumentException("Illegal character " + statement.charAt(index));
        } else {
            addToken(match);
            index += match.value.length();
        }
    }

    boolean isAtEnd() {
        return index >= statement.length();
    }

    public List<Token> tokenize() {
        while (!isAtEnd()) {
            scanToken();
        }
        return tokens;
    }

    private void addToken(Token t) {
        tokens.add(t);
    }
}