package syntax.analyzer;

public enum TokenType {
    KEYWORD,
    SYMBOL,
    IDENTIFIER,
    INT_CONST,
    STRING_CONST,
    ;
    private static final char[] operationSymbols = new char[]{
            '+',
            '-',
            '*',
            '/',
            '&',
            '|',
            '<',
            '>',
            '=',
    };

    private static final char[] symbols;

    static {
        char[] nonOperationSymbols = new char[]{
                '{',
                '}',
                '(',
                ')',
                '[',
                ']',
                '.',
                ',',
                ';',
                '~',
        };

        symbols = new char[nonOperationSymbols.length + operationSymbols.length];

        for (int i = 0; i < nonOperationSymbols.length; i++) {
            symbols[i] = nonOperationSymbols[i];
        }
        for (int i = 0; i < operationSymbols.length; i++) {
            symbols[i + nonOperationSymbols.length] = operationSymbols[i];
        }
    }

    public static TokenType getTokenType(String token) {
        if (token == null) throw new RuntimeException("Given token is null.");

        if (Keyword.keywords.contains(token)) {
            return TokenType.KEYWORD;
        } else if (token.startsWith("\"")) {
            return TokenType.STRING_CONST;
        } else if (isSymbol(token.charAt(0))) {
            return TokenType.SYMBOL;
        } else if (Character.isDigit(token.charAt(0))) {
            return TokenType.INT_CONST;
        } else {
            return TokenType.IDENTIFIER;
        }
    }

    public static boolean isSymbol(char c) {
        for (char symbol : symbols) {
            if (symbol == c) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOperationSymbol(char c) {
        for (char symbol : operationSymbols) {
            if (symbol == c) {
                return true;
            }
        }
        return false;
    }
}
