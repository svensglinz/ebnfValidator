package ebnfValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;

import ebnfValidator.EBNFParser.*;
/**
 * Utility class that reads a grammar description and returns each line in an array list
 * for further processing
 */

public class Grammar {
    Map<String, String> unparsedRules;
    Map<String, List<Token>> tokenizedRules;
    Map<String, Expression> parsedRules;
    Set<Token> literals;
    GrammarValidator validator;

    public Grammar(String fileName) throws FileNotFoundException {
        // process Grammar step by steps
        unparsedRules = readRules(fileName);
        tokenizedRules = tokenizeRules(unparsedRules);
        parsedRules = parseRules(tokenizedRules);
        literals = getLiterals(tokenizedRules);
        validator = new GrammarValidator(this);
    }

    // returns a set of literals extracted from tokenized rules
    private Set<Token> getLiterals(Map<String, List<Token>> tokenizedRules) {
        Set<Token> literals = new HashSet<>();
        for (List<Token> tokenList : tokenizedRules.values()) {
            for (Token token : tokenList) {
                if (token.type == TokenType.LITERAL)
                    literals.add(token);
            }
        }
        return literals;
    }

    // returns a map of unparsed grammar rules with {rule name, rule description} pairs
    private Map<String, String> readRules(String fileName) throws FileNotFoundException {
        Map<String, String> rules = new HashMap<>();
        Scanner scanner;
        scanner = new Scanner(new File(fileName));
        int lineNr = 1;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            // replace excess whitespace
            line = line.replaceAll("\\s+", " ");

            String[] args = line.split("<=");

            if (args.length != 2) {
                System.err.println("Error in line " + lineNr + ": " + line);
            }

            args[0] = args[0].trim();
            args[0] = args[0].substring(1, args[0].length() - 1);
            // add rule name as key, rule description as value
            rules.put(args[0].trim(), args[1].trim());
        }
        return rules;
    }

    // turns a tokenized rule into a parsed rule that is represented by a tree of GrammarElement objects
    private Map<String, Expression> parseRules(Map<String, List<Token>> tokenizedRules) {
        Map<String, Expression> parsedRules = new HashMap<>();
        for (Map.Entry<String, List<Token>> rule : tokenizedRules.entrySet()) {
            EBNFParser parser = new EBNFParser(rule.getValue());
            parsedRules.put(rule.getKey(), parser.parse());
        }
        return parsedRules;
    }

    // return a map with { rule name, tokenized rule } key-value pairs
    private Map<String, List<Token>> tokenizeRules(Map<String, String> unparsedRules) {
        Map<String, List<Token>> tokenizedRules = new HashMap<>();
        for (Map.Entry<String, String> rule : unparsedRules.entrySet()) {

            EBNFLexer lexer = new EBNFLexer(rule.getValue());

            try {
                List<Token> tokens = lexer.tokenize();
                tokenizedRules.put(rule.getKey(), tokens);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }
        return tokenizedRules;
    }

    public boolean isValid(String expression) {
        return validator.isValid(expression);
    }
}
