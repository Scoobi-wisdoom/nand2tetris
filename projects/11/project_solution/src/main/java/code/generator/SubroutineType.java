package code.generator;

import syntax.analyzer.Keyword;

public enum SubroutineType {
    CONSTRUCTOR,
    METHOD,
    FUNCTION,
    ;
    public static SubroutineType from(Keyword keyword) {
        return switch (keyword) {
            case CONSTRUCTOR:
                yield CONSTRUCTOR;
            case METHOD:
                yield METHOD;
            case FUNCTION:
                yield FUNCTION;
            default:
                throw new RuntimeException("SubroutineType conversion failed with: " + keyword);
        };
    }
}
