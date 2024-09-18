package ebnfValidator;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class EBNF {
    public static void main(String[] args) {

        if (args.length > 3 || args.length < 1) {
            System.out.println("Usage: java ebnf <grammar> <statement> | <file>");
            return;
        }

        String command;
        Grammar grammar;

        if (args.length == 1) {
            command = "";
            grammar = loadGrammar(args[0]);
        }
        else {
            command = args[0];
            grammar = loadGrammar(args[1]);
        }

        switch (command) {
            case "help":
                printHelp();
                break;
            case "validate": {
                validateStatement(grammar, args[2]);
            }
            break;
            case "validate-file": {
                validateFile(grammar, args[2]);
            }
            break;
            case "":
                interactive(grammar);
                break;
            case "parse-tree":
                printParseTree();
                break;
            default:
                printUsage();
        }
    }

    private static Grammar loadGrammar(String path) {
        Grammar grammar = null;
        try {
            grammar = new Grammar(path);
        } catch (FileNotFoundException e) {
            System.err.println("File " + path + "not found");
            System.exit(1);
        } catch (IllegalArgumentException r) {
            System.out.println(r.getMessage());
            System.exit(1);
        }
        return grammar;
    }

    private static void printParseTree() {

    }

    private static void validateStatement(Grammar grammar, String statement) {
        System.out.println(grammar.isValid(statement));
    }

    private static void validateFile(Grammar grammar, String filePath) {
        // validate stuff in file to check if format is proper. Otherwise,
        // produce some new error messages!
    }


    private static void printUsage() {
        System.out.println("Usage: ebnf (validate | validat-file | parse-tree) [GRAMMAR-PATH] [STATEMENT]");
        System.out.println("For more information, type: ebnf help");
    }

    private static void printHelp() {
        System.out.println(
                """
                        usage: ebnf validate <grammar> <statement>  validate a single statement against a grammar\s
                        ebnf validate-file <grammar> <file>         validate multiple statements in a file against a grammar\s
                        ebnf <grammar>                              start interactive session\s
                        ebnf parse - tree[PATH][STATEMENT]          print the parse tree of the evaluation\s
                        """
        );
    }

    private static void interactive(Grammar grammar) {

        final String RESET = "\033[0m";
        final String BOLD = "\033[1m";

        printWelcome();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if (line == null || line.equals(".exit")) {
                System.out.println("Good bye!");
                break;
            }
            if (grammar.isValid(line)) {
                System.out.println("\u2714" + BOLD + " valid" + RESET);
            } else {
                System.out.println("\u274c" + BOLD + " invalid" + RESET);
            }
        }
    }

    private static void printWelcome() {
        final String RESET = "\033[0m";
        final String BOLD = "\033[1m";
        final String GREEN = "\033[32m";
        final String BLUE = "\033[34m";
        System.out.println(GREEN + "---------------------------------------------" + RESET);
        System.out.println(BOLD + GREEN + "Welcome to the Interactive Shell!" + RESET);
        System.out.println(BLUE + "Type in any expression and check if matches against the provided ebnfValidator.Grammar Description" + RESET);
        System.out.println(BLUE + "Type '.exit' to quit at any time." + RESET);
        System.out.println();
    }
}

