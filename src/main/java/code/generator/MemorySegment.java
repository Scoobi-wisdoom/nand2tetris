package code.generator;

public enum MemorySegment {
    ARGUMENT,
    LOCAL,
    STATIC,
    THIS,
    THAT,
    POINTER,
    TEMP,
    ;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
