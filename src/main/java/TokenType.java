import java.util.Arrays;

public enum TokenType {
    KEYWORD,
    SYMBOL,
    IDENTIFIER,
    INT_CONST,
    STRING_CONST,
    ;
    private static final char[] symbols = new char[]{
            '{',
            '}',
            '(',
            ')',
            '[',
            ']',
            '.',
            ',',
            ';',
            '+',
            '-',
            '*',
            '/',
            '&',
            '|',
            '<',
            '>',
            '=',
            '~',
    };

    public static TokenType getTokenType(String token) {
        if (token == null) throw new RuntimeException("Given token is null.");

        if (Arrays.asList(Keyword.keywords).contains(token)) {
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
}
