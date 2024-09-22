package ebnfValidator;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class EBNF {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: java ebnf <grammar>");
            System.exit(1);
        }
        // try loading grammar
        Grammar grammar = null;
        try {
            grammar = new Grammar(args[0]);
        } catch (FileNotFoundException e) {
            Logger.printError("File " + args[0] + " not found");
            System.exit(1);
        } catch (parseError e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        interactive(grammar);
    }

    private static void interactive(Grammar grammar) {

        final String RESET = "\033[0m";
        final String BOLD = "\033[1m";
        final String CHECK = "\u2714";
        final String CROSS = "\u274c";

        printWelcome();

        GrammarValidator validator = new GrammarValidator(grammar);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if (line == null || line.equals(".exit")) {
                break;
            } else if (validator.isValid(line)) {
                System.out.println(CHECK + BOLD + " valid" + RESET);
            } else {
                System.out.println(CROSS + BOLD + " invalid" + RESET);
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

