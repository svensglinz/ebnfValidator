package ebnfValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static ebnfValidator.TokenType.*;

/**
 * Utility class that reads a grammar description and returns each line in an array list
 * for further processing
 */

public class Grammar {
    List<String> file;
    Map<String, List<Token>> rules;
    Map<String, GrammarElement> parsedRules;
    Set<Token> literals;
    GrammarValidator validator;

    public Grammar(String fileName) throws FileNotFoundException {
        read(fileName);
        tokenize();
        parse();
        getLiterals();
        validator = new GrammarValidator(this);
    }

    private void getLiterals() {
        literals = new HashSet<>();
        for (List<Token> tokenList : rules.values()) {
            for (Token token : tokenList) {
                if (token.type == TokenType.LITERAL)
                    literals.add(token);
            }
        }
    }

    private void read(String fileName) throws FileNotFoundException {
        file = new ArrayList<>();
        Scanner scanner;
        scanner = new Scanner(new File(fileName));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            file.add(line);
        }
    }

    private void parse() {
        parsedRules = new HashMap<>();
        for (Map.Entry<String, List<Token>> rule : rules.entrySet()) {
            EBNFParser parser = new EBNFParser(rule.getValue());
            parsedRules.put(rule.getKey(), parser.parse());
        }
    }

    private void tokenize() {
        rules = new HashMap<>();
        for (String line : file) {

            line = line.replaceAll("\\s+", " ");
            int delim = line.indexOf("<=");
            String ruleName = line.substring(1, delim - 2).trim();
            String body = line.substring(delim + 2).trim();

            EBNFLexer lexer = new EBNFLexer(body);

            // IllegalArgumentException --> ebnfValidator.Grammar File contains syntax errors
            // print error to the console and exit
            try {
                List<Token> tokens = lexer.tokenize();
                rules.put(ruleName, tokens);
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public boolean isValid(String statement) {
        return validator.isValid(statement);
    }

    /*
    public String printTree(String statement) {
    }
     */
}
