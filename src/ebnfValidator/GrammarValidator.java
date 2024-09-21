package ebnfValidator;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * A brute force parser that checks every production in the entire tree. Becomes impractical for very large rules and productions and could quickly lead
 * to incorrect results for extremely nested productions.
 */

public class GrammarValidator {
    private int depth = 0;
    public final int MAX_STACK_DEPTH = 25;
    Grammar grammar;
    Map<String, Expression> rules;
    List<Token> tokens;

    public GrammarValidator(Grammar grammar) {
        this.grammar = grammar;
        rules = grammar.parsedRules;
    }

    // try to match this expression against the input
    private Set<Integer> evaluateExpression(Expression expression, Set<Integer> indexSet) {

        depth++;

        try {

            if (depth > MAX_STACK_DEPTH) {
                return Set.of();
            }

            Set<Integer> reachedIdxSet = null;

            // expression consists of different options --> only one must be fulfilled
            if (isSelect(expression)) {
                reachedIdxSet = new HashSet<>();
                for (Term term : expression.terms) {
                    reachedIdxSet.addAll(evaluateTerm(term, indexSet));
                }
                return reachedIdxSet;
            }

            // expression consists of an expression where all terms must be fulfilled
            for (Term term : expression.terms) {
                if (reachedIdxSet == null)
                    reachedIdxSet = evaluateTerm(term, indexSet);
                else
                    reachedIdxSet = evaluateTerm(term, reachedIdxSet);
            }
            return reachedIdxSet;

        } finally {
            depth--;
        }

    }

    private boolean isSelect(Expression expression) {
        if (expression.terms == null)
            return false;
        for (Term term : expression.terms) {
            if (!(term instanceof Select))
                return false;
        }
        return true;
    }

    private Set<Integer> evaluateTerm(Term term, Set<Integer> indexSet) {
        return switch (term) {
            case Option o -> evaluateOption(o, indexSet);
            case Multiple m -> evaluateMultiple(m, indexSet);
            case Group g -> evaluateGroup(g, indexSet);
            case Terminal t -> evaluateTerminal(t, indexSet);
            case Rule r -> evaluateRule(r, indexSet);
            case Select s -> evaluateSelect(s, indexSet);
            default -> throw new IllegalStateException("Unexpected value: " + term);
        };
    }

    private Set<Integer> evaluateMultiple(Multiple m, Set<Integer> indexSet) {
        Set<Integer> reachedIdxSet = new HashSet<>(indexSet);
        Set<Integer> curIndices = indexSet;

        do {
            curIndices = evaluateExpression(m.expression, curIndices);
            reachedIdxSet.addAll(curIndices);
        } while (!curIndices.isEmpty());
        return reachedIdxSet;
    }

    private Set<Integer> evaluateGroup(Group g, Set<Integer> indexSet) {
        return evaluateExpression(g.expression, indexSet);
    }

    private Set<Integer> evaluateSelect(Select s, Set<Integer> indexSet) {
        return evaluateExpression(s.expression, indexSet);
    }

    private Set<Integer> evaluateOption(Option o, Set<Integer> indexSet) {
        Set<Integer> reachedIdxSet = new HashSet<>(indexSet);
        reachedIdxSet.addAll(evaluateExpression(o.expression, indexSet));
        return reachedIdxSet;
    }

    private Set<Integer> evaluateTerminal(Terminal t, Set<Integer> indexSet) {

        Set<Integer> matched = new HashSet<>();
        for (Integer index : indexSet) {
            if (matchTerminal(t, index)) {
                matched.add(index + 1);
            }
        }
        return matched;
    }

    private Set<Integer> evaluateRule (Rule r, Set<Integer> indexSet) {
        Expression e = rules.get(r.value);
        return evaluateExpression(e, indexSet);
    }

    // check if token matches the next token!
    private boolean matchTerminal(Terminal t, int index) {
        if (index >= tokens.size()) {
            return false;
        }
        return tokens.get(index).value.equals(t.value);
    }


    // assumes that EXPRESSION rule is entry point into grammar
    public boolean isValid(String expression) {
        depth = 0;
        StatementLexer lexer = new StatementLexer(expression, grammar.literals);

        // if lexer throws an error, illegal sequence has been supplied which cannot be matched
        try {
            tokens = lexer.tokenize();
        } catch (IllegalArgumentException e) {
            return false;
        }
        return evaluateExpression(rules.get("expression"), Set.of(0)).contains(tokens.size());
    }
}