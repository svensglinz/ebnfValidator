package ebnfValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ebnfValidator.TokenType.*;
import static ebnfValidator.TokenType.*;

/**
 * DESCRIPTION
 */

class StatementLexer {
    Set<Token> EBNFTokens;
    List<Token> tokens = new ArrayList<>();
    String statement;
    int index;

    public StatementLexer(String statement, Set<Token> EBNFTokens) {
        this.statement = statement;
        this.EBNFTokens = extractLiterals(EBNFTokens);
    }

    private Set<Token> extractLiterals(Set<Token> EBNFTokens) {
        Set<Token> literals = new HashSet<>();
        for (Token t : EBNFTokens) {
            if (t.type == TokenType.LITERAL) {
                literals.add(t);
            }
        }
        return literals;
    }

    void scanToken() {

        // skip whitespace
        while (statement.charAt(index) == ' ') {
            index++;
        }

        // scanner ignores whitespace but two rules separated by whitespace can never be one
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