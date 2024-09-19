package ebnfValidator;

class Token {
    String value;
    TokenType type;

    public Token(String value, TokenType type) {
        this.value = value;
        this.type = type;
    }

    public Token(TokenType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type + " " + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token) {
            return ((Token) obj).value.equals(value);
        }
        return false;
    }
}
