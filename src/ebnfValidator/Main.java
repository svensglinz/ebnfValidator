package ebnfValidator;

public class Main {
    public static void main(String[] args) {
        Grammar g = EBNF.loadGrammar("grammar.txt");
        System.out.println(g.isValid("a0 = 0009"));
        System.out.println("done");
    }
}
