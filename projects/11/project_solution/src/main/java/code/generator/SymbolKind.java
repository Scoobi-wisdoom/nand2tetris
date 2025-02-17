package code.generator;

public enum SymbolKind {
    STATIC,
    FIELD,
    ARG,
    VAR,
    NONE,
    ;

    public MemorySegment getMemorySegment() {
        return switch (this) {
            case STATIC:
                yield MemorySegment.STATIC;
            case FIELD:
                yield MemorySegment.THIS;
            case ARG:
                yield MemorySegment.ARGUMENT;
            case VAR:
                yield MemorySegment.LOCAL;
            case NONE:
                throw new RuntimeException("No corresponding MemorySegment for SymbolKind: " + this);
        };
    }
}
