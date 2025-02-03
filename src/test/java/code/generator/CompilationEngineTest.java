package code.generator;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static code.generator.SymbolKind.ARG;
import static code.generator.SymbolKind.FIELD;
import static code.generator.SymbolKind.STATIC;
import static code.generator.SymbolKind.VAR;
import static code.generator.SymbolTableLevel.KLASS;
import static code.generator.SymbolTableLevel.SUBROUTINE;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Nand2tetris tool JackCompiler.sh was used to generate vm code in test code.
 */
class CompilationEngineTest {
    @Nested
    class SymbolTable {
        @Test
        public void klass() {
            // given
            String jackCode = """
                    class Fraction {
                       field int numerator, denominator;
                       static boolean test;
                       field List next;
                    }
                    """;
            CompilationEngine compilationEngine = new CompilationEngine(new ByteArrayInputStream(jackCode.getBytes(UTF_8)), new ByteArrayOutputStream());
            code.generator.SymbolTable klassSymbolTable = compilationEngine.getSymbolTable(KLASS);

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

        @Test
        public void function() {
            // given
            String jackCode = """
                    class Fraction {
                        function int gcd(int a, int b) {}
                    }
                    """;
            CompilationEngine compilationEngine = new CompilationEngine(new ByteArrayInputStream(jackCode.getBytes(UTF_8)), new ByteArrayOutputStream());
            code.generator.SymbolTable subroutineSymbolTable = compilationEngine.getSymbolTable(SUBROUTINE);

            // when
            compilationEngine.compileClass();

            // then
            Assertions.assertAll(
                    () -> Assertions.assertEquals(2, subroutineSymbolTable.varCount(ARG)),
                    () -> Assertions.assertEquals(ARG, subroutineSymbolTable.kindOf("a")),
                    () -> Assertions.assertEquals("int", subroutineSymbolTable.typeOf("a")),
                    () -> Assertions.assertEquals(0, subroutineSymbolTable.indexOf("a")),
                    () -> Assertions.assertEquals(ARG, subroutineSymbolTable.kindOf("b")),
                    () -> Assertions.assertEquals("int", subroutineSymbolTable.typeOf("b")),
                    () -> Assertions.assertEquals(1, subroutineSymbolTable.indexOf("b"))
            );
        }

        @Test
        public void method() {
            // given
            String jackCode = """
                    class Fraction {
                        method Fraction plus(Fraction other) {}
                    }
                    """;
            CompilationEngine compilationEngine = new CompilationEngine(new ByteArrayInputStream(jackCode.getBytes(UTF_8)), new ByteArrayOutputStream());
            code.generator.SymbolTable subroutineSymbolTable = compilationEngine.getSymbolTable(SUBROUTINE);

            // when
            compilationEngine.compileClass();

            // then
            Assertions.assertAll(
                    () -> Assertions.assertEquals(2, subroutineSymbolTable.varCount(ARG)),
                    () -> Assertions.assertEquals(ARG, subroutineSymbolTable.kindOf("this")),
                    () -> Assertions.assertEquals("Fraction", subroutineSymbolTable.typeOf("this")),
                    () -> Assertions.assertEquals(0, subroutineSymbolTable.indexOf("this")),
                    () -> Assertions.assertEquals(ARG, subroutineSymbolTable.kindOf("other")),
                    () -> Assertions.assertEquals("Fraction", subroutineSymbolTable.typeOf("other")),
                    () -> Assertions.assertEquals(1, subroutineSymbolTable.indexOf("other"))
            );
        }

        @Test
        public void subroutines() {
            // given
            String jackCode = """
                    class List {
                        constructor List new(int car, List cdr) {}
                        function int gcd(int a, int b) {}
                    }
                    """;
            CompilationEngine compilationEngine = new CompilationEngine(new ByteArrayInputStream(jackCode.getBytes(UTF_8)), new ByteArrayOutputStream());
            code.generator.SymbolTable subroutineSymbolTable = compilationEngine.getSymbolTable(SUBROUTINE);

            // when
            compilationEngine.compileClass();

            // then
            Assertions.assertAll(
                    () -> Assertions.assertEquals(2, subroutineSymbolTable.varCount(ARG)),
                    () -> Assertions.assertEquals(ARG, subroutineSymbolTable.kindOf("a")),
                    () -> Assertions.assertEquals("int", subroutineSymbolTable.typeOf("a")),
                    () -> Assertions.assertEquals(0, subroutineSymbolTable.indexOf("a")),
                    () -> Assertions.assertEquals(ARG, subroutineSymbolTable.kindOf("b")),
                    () -> Assertions.assertEquals("int", subroutineSymbolTable.typeOf("b")),
                    () -> Assertions.assertEquals(1, subroutineSymbolTable.indexOf("b"))
            );
        }

        @Test
        public void var() {
            // given
            String jackCode = """
                    class Main {
                        function void more() {
                            var boolean b;
                        }
                    }
                    """;
            CompilationEngine compilationEngine = new CompilationEngine(new ByteArrayInputStream(jackCode.getBytes(UTF_8)), new ByteArrayOutputStream());
            code.generator.SymbolTable subroutineSymbolTable = compilationEngine.getSymbolTable(SUBROUTINE);

            // when
            compilationEngine.compileClass();

            // then
            Assertions.assertAll(
                    () -> Assertions.assertEquals(1, subroutineSymbolTable.varCount(VAR)),
                    () -> Assertions.assertEquals(VAR, subroutineSymbolTable.kindOf("b")),
                    () -> Assertions.assertEquals("boolean", subroutineSymbolTable.typeOf("b")),
                    () -> Assertions.assertEquals(0, subroutineSymbolTable.indexOf("b"))
            );
        }
    }

    private static String getCompileOutput(String jackCode) {
        InputStream inputStream = new ByteArrayInputStream(jackCode.getBytes(UTF_8));
        OutputStream outputStream = new ByteArrayOutputStream();
        CompilationEngine compilationEngine = new CompilationEngine(inputStream, outputStream);
        compilationEngine.compileClass();
        return outputStream.toString();
    }

    @Nested
    class ReturnVoid {
        @Test
        public void method() {
            // given
            String jackCode = """
                    class Bat {
                       method void setDirection() {
                           return;
                       }
                    }
                    """;
            String expected = """
                    function Bat.setDirection 0
                    push argument 0
                    pop pointer 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void function() {
            // given
            String jackCode = """
                    class Main {
                       function void main() {
                          return;
                       }
                    }
                    """;
            String expected = """
                    function Main.main 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }
    }
}
