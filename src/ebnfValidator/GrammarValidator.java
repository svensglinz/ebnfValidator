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
    public final int MAX_STACK_DEPTH = 30;
    Grammar grammar;
    Map<String, GrammarElement> rules;
    List<Token> tokens;

    public GrammarValidator(Grammar grammar) {
        this.grammar = grammar;
        rules = grammar.parsedRules;
    }

    // evaluate a ebnfValidator.Grammar Element
    private Set<Integer> evaluate(GrammarElement elem, Set<Integer> indexSet) {
        // any production may at most make MAX_STACK_DEPTH calls to evaluate
        depth++;
        try {
            // no need to traverse tree
            if (indexSet.isEmpty()) {
                return indexSet;
            }

            // trying to prevent infinite recursion
            if (depth > MAX_STACK_DEPTH) {
                return Set.of();
            }

            if (elem instanceof Expression) {
                return evaluateExpression((Expression) elem, indexSet);
            } else if (elem instanceof Node) {
                return evaluateNode((Node) elem, indexSet);
            }
            return Set.of();
        } finally {
            depth--;
        }
    }

    // checks if any is true
    private Set<Integer> evaluateSelect(GrammarElement expression, Set<Integer> indexSet) {

        Set<Integer> reachedIndices = new HashSet<>();
        for (GrammarElement expr : expression.elements) {
            Set<Integer> reached = evaluate(expr, indexSet);
            if (!reached.isEmpty()) {
                reachedIndices.addAll(reached);
            }
        }
        return reachedIndices;

    }

    // checks if all are true
    private Set<Integer> evaluateGroup(Expression expr, Set<Integer> indexSet) {

        Set<Integer> reachedIndices = null;
        for (GrammarElement elem : expr.elements) {
            if (reachedIndices == null)
                reachedIndices = evaluate(elem, indexSet);
            else {
                reachedIndices = evaluate(elem, reachedIndices);
            }

            // early exit
            if (reachedIndices.isEmpty()) {
                break;
            }
        }
        return reachedIndices == null ? Set.of() : reachedIndices;

    }

    private Set<Integer> evaluateMultiple(Expression expr, Set<Integer> indexSet) {

        // assume Multiple can also be executed 0 times. if > 1, remove indexSet from initializer
        Set<Integer> reachedIndices = new HashSet<>(indexSet);
        Set<Integer> curIndices = indexSet;
        do {
            // multiple can contain exactly one group as a child
            curIndices = evaluate(expr.elements.getFirst(), curIndices);
            reachedIndices.addAll(curIndices);
        } while (!curIndices.isEmpty());
        return reachedIndices;

    }

    private Set<Integer> evaluateOption(Expression expr, Set<Integer> indexSet) {
        // reached by not evaluating
        Set<Integer> reachedIndices = new HashSet<>(indexSet);
        // option must contain exactly one group as a child
        reachedIndices.addAll(evaluate(expr.elements.getFirst(), indexSet));
        return reachedIndices;
    }

    private Set<Integer> evaluateNode(Node node, Set<Integer> indexSet) {
        if (node.isTerminal()) {
            return evaluateTerminal(node, indexSet);
        } else {
            return evaluateNonTerminal(node, indexSet);
        }
    }

    private Set<Integer> evaluateNonTerminal(Node node, Set<Integer> indexSet) {
        return evaluate(getRule(node.token.value), indexSet);
    }

    private GrammarElement getRule(String ruleName) {
        return rules.get(ruleName);
    }

    private Set<Integer> evaluateTerminal(Node node, Set<Integer> indexSet) {

        Set<Integer> matched = new HashSet<>();
        for (Integer index : indexSet) {
            if (matchToken(node.token.value, index)) {
                matched.add(index + 1);
            }
        }
        return matched;
    }

    // check if token matches the next token!
    private boolean matchToken(String token, int index) {
        if (index >= tokens.size()) {
            return false;
        }
        return tokens.get(index).value.equals(token);
    }

    // try to match this expression against the input
    private Set<Integer> evaluateExpression(Expression expression, Set<Integer> indexSet) {

        return switch (expression.type) {
            case "group" -> evaluateGroup(expression, indexSet);
            case "multiple" -> evaluateMultiple(expression, indexSet);
            case "select" -> evaluateSelect(expression, indexSet);
            case "option" -> evaluateOption(expression, indexSet);
            default -> Set.of();
        };


    }

    // assumes that expression rule is entry point into grammar
    public boolean isValid(String expression) {
        depth = 0;
        StatementLexer lexer = new StatementLexer(expression, grammar.literals);

        // if lexer throws an error, illegal sequence has been supplied which cannot be matched
        try {
            tokens = lexer.tokenize();
        } catch (IllegalArgumentException e) {
            return false;
        }
        return evaluate(getRule("expression"), Set.of(0)).contains(tokens.size());
    }
}
