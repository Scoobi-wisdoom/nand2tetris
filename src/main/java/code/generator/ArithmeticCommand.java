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

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
