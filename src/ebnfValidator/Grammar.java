package ebnfValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Scanner;
import java.util.HashSet;
import java.util.LinkedHashMap;
import static ebnfValidator.TokenType.RULE;

/**
 * Utility class that reads and parses a Grammar description such
 * that it can be validated by ebnfValidator.grammarValidator
 */

public class Grammar {
    Map<String, String> unparsedRules;
    Map<String, List<Token>> tokenizedRules;
    Map<String, Expression> parsedRules;
    Set<Token> literals;

    public Grammar(String fileName) throws FileNotFoundException {
        // process Grammar step by steps
        unparsedRules = readRules(fileName);
        tokenizedRules = tokenizeRules(unparsedRules);
        parsedRules = parseRules(tokenizedRules);
        literals = getLiterals(tokenizedRules);
    }

    // returns a set of literals extracted from tokenized rules
    private Set<Token> getLiterals(Map<String, List<Token>> tokenizedRules) {
        Set<Token> literals = new HashSet<>();
        for (List<Token> tokenList : tokenizedRules.values()) {
            for (Token token : tokenList) {
                if (token.type == TokenType.LITERAL || token.type == TokenType.EMPTY)
                    literals.add(token);
            }
        }
        return literals;
    }

    // returns a map of unparsed grammar rules with {rule name, rule description} pairs
    private Map<String, String> readRules(String fileName) throws FileNotFoundException {
        Map<String, String> rules = new LinkedHashMap<>();
        Scanner scanner = new Scanner(new File(fileName));
        int lineNr = 1;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.replaceAll("\\s+", " ");
            int pos = line.indexOf("<=");

            if (pos < 0) {
                Logger.error("Error while parsing Grammar. No <= detected in Line " + lineNr);
            }

            String[] args = {line.substring(0, pos), line.substring(pos + 2)};
            args[0] = args[0].trim();
            args[0] = args[0].substring(1, args[0].length() - 1);
            rules.put(args[0].trim(), args[1].trim());
        }
        return rules;
    }

    // turns a tokenized rule into a parsed rule that is represented by a tree of Expression objects
    private Map<String, Expression> parseRules(Map<String, List<Token>> tokenizedRules) {
        int lineNr = 1;
        Map<String, Expression> parsedRules = new LinkedHashMap<>();
        for (Map.Entry<String, List<Token>> rule : tokenizedRules.entrySet()) {
            RuleParser parser = new RuleParser(rule.getValue(), lineNr++);
            parsedRules.put(rule.getKey(), parser.parse());
        }
        return parsedRules;
    }

    // return a map with { rule name, tokenized rule } key-value pairs
    private Map<String, List<Token>> tokenizeRules(Map<String, String> unparsedRules) {
        Map<String, List<Token>> tokenizedRules = new LinkedHashMap<>();
        int lineNr = 1;

        for (Map.Entry<String, String> rule : unparsedRules.entrySet()) {
            RuleLexer lexer = new RuleLexer(rule.getValue(), lineNr++);
            List<Token> tokens = lexer.tokenize();
            tokenizedRules.put(rule.getKey(), tokens);
        }
        // check that each referenced rule exists
        Set<String> ruleNames = tokenizedRules.keySet();

        lineNr = 1;
        for (List<Token> tokens : tokenizedRules.values()) {
            for (Token token : tokens) {
                if (token.type.equals(RULE) && !ruleNames.contains(token.value)) {
                    Logger.error("Error while parsing line " + lineNr + "\n Rule " + token.value + " has not been declared");
                }
            }
            lineNr++;
        }

        // make sure that expression is declared (main entry point)
        if (!ruleNames.contains("expression")) {
            Logger.error("No rule called expression found in grammar. This is needed as the main entry point");
        }
        return tokenizedRules;
    }
}
