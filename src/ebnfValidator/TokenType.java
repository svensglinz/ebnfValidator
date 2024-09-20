package ebnfValidator;

/**
 * Multiple: {}
 * Option: []
 * Select: |
 * Group: ()
 * Rule: <>
 */
public enum TokenType {
    QUOTE, GROUP_OPEN, GROUP_CLOSE, MULTIPLE_OPEN,
    MULTIPLE_CLOSE, SELECT_OPEN, SELECT_CLOSE, OPTION_OPEN, OPTION_CLOSE, RULE, LITERAL,
    RULE_OPEN, RULE_CLOSE, EMPTY, SELECT
}
