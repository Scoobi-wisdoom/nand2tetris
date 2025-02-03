package code.generator;

public enum ArithmeticCommand {
    ADD,
    SUB,
    NEG,
    EQ,
    GT,
    LT,
    AND,
    OR,
    NOT,
    ;

    public static ArithmeticCommand getCommand(char symbol) {
        return switch (symbol) {
            case '+':
                yield ADD;
            case '-':
                yield SUB;
            case '&':
                yield AND;
            case '|':
                yield OR;
            case '<':
                yield LT;
            case '>':
                yield GT;
            case '=':
                yield EQ;
            default:
                throw new RuntimeException("Unexpected value: " + symbol);
        };
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
