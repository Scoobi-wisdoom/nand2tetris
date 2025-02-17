package code.generator;

import static code.generator.SymbolKind.NONE;
import static code.generator.SymbolTableLevel.KLASS;
import static code.generator.SymbolTableLevel.SUBROUTINE;

public class SymbolTablePair {
    private final SymbolTable klassSymbolTable;
    private final SymbolTable subroutineSymbolTable;

    public SymbolTablePair() {
        this.klassSymbolTable = new SymbolTable(KLASS);
        this.subroutineSymbolTable = new SymbolTable(SUBROUTINE);
    }

    public SymbolTable getKlassSymbolTable() {
        return klassSymbolTable;
    }

    public SymbolTable getSubroutineSymbolTable() {
        return subroutineSymbolTable;
    }

    public MemorySegment getMemorySegment(String name) {
        MemorySegment memorySegment;
        if (klassSymbolTable.kindOf(name) == NONE) {
            memorySegment = subroutineSymbolTable.kindOf(name).getMemorySegment();
        } else {
            assert klassSymbolTable.kindOf(name) != NONE;
            memorySegment = klassSymbolTable.kindOf(name).getMemorySegment();
        }
        return memorySegment;
    }

    public int getIndex(String name) {
        int index;
        if (klassSymbolTable.kindOf(name) == NONE) {
            index = subroutineSymbolTable.indexOf(name);
        } else {
            assert klassSymbolTable.kindOf(name) != NONE;
            index = klassSymbolTable.indexOf(name);
        }
        return index;
    }

    public String getType(String name) {
        String type;
        if (klassSymbolTable.kindOf(name) == NONE) {
            type = subroutineSymbolTable.typeOf(name);
        } else {
            assert klassSymbolTable.kindOf(name) != NONE;
            type = klassSymbolTable.typeOf(name);
        }
        return type;
    }

    public boolean contains(String name) {
        return klassSymbolTable.kindOf(name) != NONE ||
                subroutineSymbolTable.kindOf(name) != NONE;
    }
}
