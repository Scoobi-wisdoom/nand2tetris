package code.generator;


import java.util.HashMap;
import java.util.Map;

import static code.generator.SymbolTableLevel.KLASS;
import static code.generator.SymbolTableLevel.SUBROUTINE;

public class SymbolTable {
    private final SymbolTableLevel level;
    private final Map<String, Symbol> nameToSymbol;
    private int staticTypeVarCount;
    private int fieldTypeVarCount;
    private int argTypeVarCount;
    private int varTypeVarCount;

    public SymbolTable(SymbolTableLevel level) {
        this.nameToSymbol = new HashMap<>();
        this.level = level;
        this.staticTypeVarCount = 0;
        this.fieldTypeVarCount = 0;
        this.argTypeVarCount = 0;
        this.varTypeVarCount = 0;
    }

    public void reset() {
        assert this.level != KLASS;
        nameToSymbol.clear();
        staticTypeVarCount = 0;
        fieldTypeVarCount = 0;
        argTypeVarCount = 0;
        varTypeVarCount = 0;
    }

    public void define(String name, String type, SymbolKind kind) {
        if (nameToSymbol.get(name) != null) throw new RuntimeException("Already existing Symbol with name: " + name);

        int existingCount = switch (kind) {
            case STATIC:
                assert this.level == KLASS;
                yield staticTypeVarCount++;
            case FIELD:
                assert this.level == KLASS;
                yield fieldTypeVarCount++;
            case ARG:
                assert this.level == SUBROUTINE;
                yield argTypeVarCount++;
            case VAR:
                assert this.level == SUBROUTINE;
                yield varTypeVarCount++;
            case NONE:
                throw new RuntimeException("Invalid SymbolKind.");
        };

        nameToSymbol.put(name, new Symbol(type, kind, existingCount));
    }

    public int varCount(SymbolKind kind) {
        return switch (kind) {
            case STATIC:
                assert this.level == KLASS;
                yield staticTypeVarCount;
            case FIELD:
                assert this.level == KLASS;
                yield fieldTypeVarCount;
            case ARG:
                assert this.level == SUBROUTINE;
                yield argTypeVarCount;
            case VAR:
                assert this.level == SUBROUTINE;
                yield varTypeVarCount;
            case NONE:
                throw new RuntimeException("Invalid SymbolKind.");
        };
    }

    public SymbolKind kindOf(String name) {
        Symbol symbol = nameToSymbol.get(name);
        if (symbol == null) {
            return SymbolKind.NONE;
        } else {
            return symbol.getKind();
        }
    }

    public String typeOf(String name) {
        Symbol symbol = nameToSymbol.get(name);
        if (symbol == null) {
            return "";
        } else {
            return symbol.getType();
        }
    }

    public int indexOf(String name) {
        Symbol symbol = nameToSymbol.get(name);
        if (symbol == null) {
            return -1;
        } else {
            return symbol.getCount();
        }
    }
}
