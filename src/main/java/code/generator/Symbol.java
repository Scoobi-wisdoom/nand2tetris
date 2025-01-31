package code.generator;

public class Symbol {
    private final String type;
    private final SymbolKind kind;
    private final int count;

    public Symbol(
            String type,
            SymbolKind kind,
            int count
    ) {
        assert kind != SymbolKind.NONE;
        this.type = type;
        this.kind = kind;
        this.count = count;
    }

    public SymbolKind getKind() {
        return kind;
    }

    public String getType() {
        return type;
    }

    public int getCount() {
        return count;
    }
}
