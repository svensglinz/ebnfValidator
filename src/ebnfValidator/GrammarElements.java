package ebnfValidator;

import java.util.ArrayList;
import java.util.List;

class Expression {
    List<Term> terms = new ArrayList<>();

    public void add(Term term) {
        terms.add(term);
    }
}

class Term {
    Expression expression;

    public Term() {
    }

    public Term(Expression expression) {
        this.expression = expression;
    }
}

class Terminal extends Term {
    String value;

    public Terminal(String value) {
        this.value = value;
    }
}

class Rule extends Term {
    String value;

    public Rule(String value) {
        this.value = value;
    }
}

class Select extends Term {
    public Select(Expression expression) {
        super(expression);
    }
}

class Group extends Term {
    public Group(Expression expression) {
        super(expression);
    }
}

class Multiple extends Term {
    public Multiple(Expression expression) {
        super(expression);
    }
}

class Option extends Term {
    public Option(Expression expression) {
        super(expression);
    }
}
