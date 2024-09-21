package ebnfValidator;

public class Main {
    public static void main(String[] args) {
        Grammar g = EBNF.loadGrammar("./example/fs21.txt");
        System.out.println(g.isValid("true"));
        System.out.println("done");
    }
}
