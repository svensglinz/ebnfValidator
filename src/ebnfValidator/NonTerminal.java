package ebnfValidator;

import java.util.LinkedList;
import java.util.List;

import static ebnfValidator.TokenType.*;

// rule can consist of expressions, rules and rules, each can be select, repeat and optional or optionalRepeat

class GrammarElement {
    List<GrammarElement> elements;

    public GrammarElement(List<GrammarElement> elements) {
        this.elements = elements;
    }

    public GrammarElement() {
        this.elements = new LinkedList<>();
    }

    public void addElement(GrammarElement element) {
        elements.add(element);
    }
}

// expression that groups the grammar
class Expression extends GrammarElement {
    String type;
    public Expression(String type) {
        this.type = type;
    }

    public Expression(List<GrammarElement> elements, String type) {
        this.elements = elements;
        this.type = type;
    }
}

// node that can be evaluated (either terminal or non-terminal)
class Node extends GrammarElement {
    Token token;

    public Node(Token token) {
        this.token = token;
    }

    public Node() {}

    public boolean isTerminal() {
        return token.type == TokenType.LITERAL;
    }
}
