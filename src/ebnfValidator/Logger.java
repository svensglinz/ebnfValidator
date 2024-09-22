package ebnfValidator;

// tires to return as many informational errors as possible
class Logger {
    static private final String red = "\u001B[31m";
    static private final String green = "\u001B[32m";
    static private final String yellow = "\u001B[33m";
    static private final String reset = "\u001B[0m";

    public static void error(String msg) {
        throw new parseError(red + msg + reset);
    }

    public static void printError(String msg) {
        System.out.println(red + msg + reset);
    }

    public static void warning(String msg) {
        System.out.println(yellow + msg + reset);
    }

    public static void info(String msg) {
        System.out.println(green + msg + reset);
    }
}

// only error class for the moment
class parseError extends RuntimeException {
    public parseError(String msg) {
        super(msg);
    }
}