public enum Keyword {
    CLASS,
    METHOD,
    FUNCTION,
    CONSTRUCTOR,
    INT,
    BOOLEAN,
    CHAR,
    VOID,
    VAR,
    STATIC,
    FIELD,
    LET,
    DO,
    IF,
    ELSE,
    WHILE,
    RETURN,
    TRUE,
    FALSE,
    NULL,
    THIS,
    ;

    public static final String[] keywords = new String[]{
            "class",
            "method",
            "function",
            "constructor",
            "int",
            "boolean",
            "char",
            "void",
            "var",
            "static",
            "field",
            "let",
            "do",
            "if",
            "else",
            "while",
            "return",
            "true",
            "false",
            "null",
            "this",
    };

    public static Keyword getKeyword(String token) {
        return switch (token) {
            case "class" -> Keyword.CLASS;
            case "method" -> Keyword.METHOD;
            case "function" -> Keyword.FUNCTION;
            case "constructor" -> Keyword.CONSTRUCTOR;
            case "int" -> Keyword.INT;
            case "boolean" -> Keyword.BOOLEAN;
            case "char" -> Keyword.CHAR;
            case "void" -> Keyword.VOID;
            case "var" -> Keyword.VAR;
            case "static" -> Keyword.STATIC;
            case "field" -> Keyword.FIELD;
            case "let" -> Keyword.LET;
            case "do" -> Keyword.DO;
            case "if" -> Keyword.IF;
            case "else" -> Keyword.ELSE;
            case "while" -> Keyword.WHILE;
            case "return" -> Keyword.RETURN;
            case "true" -> Keyword.TRUE;
            case "false" -> Keyword.FALSE;
            case "null" -> Keyword.NULL;
            case "this" -> Keyword.THIS;
            default -> throw new RuntimeException(token + " is not a keyword.");
        };
    }
}
