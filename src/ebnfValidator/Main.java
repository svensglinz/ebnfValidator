package ebnfValidator;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Grammar g = new Grammar("./text.txt");
        boolean a = g.isValid("@a=1");
    }
}
