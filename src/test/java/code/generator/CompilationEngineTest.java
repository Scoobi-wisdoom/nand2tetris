package code.generator;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static code.generator.SymbolKind.*;
import static code.generator.SymbolTableLevel.KLASS;
import static java.nio.charset.StandardCharsets.UTF_8;

class CompilationEngineTest {
    @Test
    public void compileClassVarDec() {
        // given
        String jackCode = """
                class Fraction {
                   field int numerator, denominator;
                   static boolean test;
                   field List next;
                }
                """;
        CompilationEngine compilationEngine = new CompilationEngine(new ByteArrayInputStream(jackCode.getBytes(UTF_8)), new ByteArrayOutputStream());
        SymbolTable klassSymbolTable =  compilationEngine.getSymbolTable(KLASS);

        // when
        compilationEngine.compileClass();

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(3, klassSymbolTable.varCount(FIELD)),
                () -> Assertions.assertEquals(FIELD, klassSymbolTable.kindOf("numerator")),
                () -> Assertions.assertEquals("int", klassSymbolTable.typeOf("numerator")),
                () -> Assertions.assertEquals(0, klassSymbolTable.indexOf("numerator")),
                () -> Assertions.assertEquals(FIELD, klassSymbolTable.kindOf("denominator")),
                () -> Assertions.assertEquals("int", klassSymbolTable.typeOf("denominator")),
                () -> Assertions.assertEquals(1, klassSymbolTable.indexOf("denominator")),
                () -> Assertions.assertEquals(FIELD, klassSymbolTable.kindOf("next")),
                () -> Assertions.assertEquals("List", klassSymbolTable.typeOf("next")),
                () -> Assertions.assertEquals(2, klassSymbolTable.indexOf("next")),

                () -> Assertions.assertEquals(1, klassSymbolTable.varCount(STATIC)),
                () -> Assertions.assertEquals(STATIC, klassSymbolTable.kindOf("test")),
                () -> Assertions.assertEquals("boolean", klassSymbolTable.typeOf("test")),
                () -> Assertions.assertEquals(0, klassSymbolTable.indexOf("test"))
        );
    }
}
