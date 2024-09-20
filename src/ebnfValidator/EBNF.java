package ebnfValidator;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class EBNF {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: java ebnf <grammar>");
            System.exit(1);
        }

        // start interactive shell
        Grammar grammar = loadGrammar(args[0]);
        interactive(grammar);
    }

    public static Grammar loadGrammar(String path) {
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

