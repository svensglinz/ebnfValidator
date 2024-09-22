package ebnfValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ebnfValidator.TokenType.EMPTY;

/**
 * Tokenizes a statement into different tokens (literals) given the set of possible
 * literals that occur in the Grammar Rules.
 */

class StatementLexer {
    boolean illegalCharacter = false;
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
        while (!isAtEnd() && statement.charAt(index) == ' ') {
            index++;
        }

        // scanner ignores whitespace but two rules separated by whitespace can never be one
        // find the longest word that matches a token in the set of allowed tokens
        Token match = null;
        for (Token t : EBNFTokens) {
            if (statement.startsWith(t.value, index)) {
                if (match == null)
                    match = t;
                else
                    match = t.value.length() > match.value.length() ? t : match;
            }
        }
        // if statement contains characters not in grammar, allow early breakout
        if (match == null) {
            illegalCharacter = true;
        } else {
            addToken(match);
            index += match.value.length();
        }
    }

    boolean isAtEnd() {
        return index >= statement.length();
    }

    public List<Token> tokenize() {
        while (!isAtEnd() && !illegalCharacter) {
            scanToken();
        }
        return tokens;
    }

    private void addToken(Token t) {
        tokens.add(t);
    }
}