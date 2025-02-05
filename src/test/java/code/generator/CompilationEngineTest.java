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
    @Test
    public void countExpression() {

    }

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

    @Nested
    class Operation {
        @Test
        public void multiply() {
            // given
            String jackCode = """
                    class Main {
                        function int double(int a) {
                        	return a * 2;
                        }
                    }
                    """;
            String expected = """
                    function Main.double 0
                    push argument 0
                    push constant 2
                    call Math.multiply 2
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void divide() {
            // given
            String jackCode = """
                    class Main {
                        function int half(int a) {
                        	return a / 2;
                        }
                    }
                    """;
            String expected = """
                    function Main.half 0
                    push argument 0
                    push constant 2
                    call Math.divide 2
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void add() {
            // given
            String jackCode = """
                    class Main {
                        function int plus(int a) {
                        	return a + 2;
                        }
                    }
                    """;
            String expected = """
                    function Main.plus 0
                    push argument 0
                    push constant 2
                    add
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void sub() {
            // given
            String jackCode = """
                    class Main {
                        function int minus(int a) {
                        	return a - 2;
                        }
                    }
                    """;
            String expected = """
                    function Main.minus 0
                    push argument 0
                    push constant 2
                    sub
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void eq() {
            // given
            String jackCode = """
                    class Main {
                        function boolean equals(int a, int b) {
                            return a = b;
                        }
                    }
                    """;
            String expected = """
                    function Main.equals 0
                    push argument 0
                    push argument 1
                    eq
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void gt() {
            // given
            String jackCode = """
                    class Main {
                        function boolean greaterThan(int a) {
                        	return a > 2;
                        }
                    }
                    """;
            String expected = """
                    function Main.greaterThan 0
                    push argument 0
                    push constant 2
                    gt
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void lt() {
            // given
            String jackCode = """
                    class Main {
                        function boolean lessThan(int a) {
                        	return a < 2;
                        }
                    }
                    """;
            String expected = """
                    function Main.lessThan 0
                    push argument 0
                    push constant 2
                    lt
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void and() {
            // given
            String jackCode = """
                    class Main {
                        function int bitwiseAnd(int a, int b) {
                            return a & b;
                        }
                    }
                    """;
            String expected = """
                    function Main.bitwiseAnd 0
                    push argument 0
                    push argument 1
                    and
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void or() {
            // given
            String jackCode = """
                    class Main {
                        function int bitwiseOr(int a, int b) {
                            return a | b;
                        }
                    }
                    """;
            String expected = """
                    function Main.bitwiseOr 0
                    push argument 0
                    push argument 1
                    or
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class Assignee {
        @Test
        public void field() {
            // given
            String jackCode = """
                    class Bat {
                       field int direction;
                    
                       method void setDirection(int Adirection) {
                           let direction = Adirection;
                           return;
                       }
                    }
                    """;
            String expected = """
                    function Bat.setDirection 0
                    push argument 0
                    pop pointer 0
                    push argument 1
                    pop this 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void staticKind() {
            // given
            String jackCode = """
                    class Bat {
                       static int direction;
                    
                       method void setDirection(int Adirection) {
                           let direction = Adirection;
                           return;
                       }
                    }
                    """;
            String expected = """
                    function Bat.setDirection 0
                    push argument 0
                    pop pointer 0
                    push argument 1
                    pop static 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void var() {
            // given
            String jackCode = """
                    class Main {
                        method void run() {
                           var int exit;
                           let exit = 1;
                           return;
                        }
                    }
                    """;
            String expected = """
                    function Main.run 1
                    push argument 0
                    pop pointer 0
                    push constant 1
                    pop local 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class AssignedValue {
        @Test
        public void trueValue() {
            // given
            String jackCode = """
                    class Main {
                        method void run() {
                           var boolean exit;
                           let exit = true;
                           return;
                        }
                    }
                    """;
            String expected = """
                    function Main.run 1
                    push argument 0
                    pop pointer 0
                    push constant 0
                    not
                    pop local 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void falseValue() {
            // given
            String jackCode = """
                    class Main {
                        method void run() {
                           var boolean exit;
                           let exit = false;
                           return;
                        }
                    }
                    """;
            String expected = """
                    function Main.run 1
                    push argument 0
                    pop pointer 0
                    push constant 0
                    pop local 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void nullValue() {
            // given
            String jackCode = """
                    class Main {
                        function void more() {
                           var String s;
                           let s = null;
                           return;
                        }
                    }
                    """;
            String expected = """
                    function Main.more 1
                    push constant 0
                    pop local 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void thisValue() {
            // given
            String jackCode = """
                    class Main {
                        method void print() {
                            var List current;
                            let current = this;
                            return;
                        }
                    }
                    """;
            String expected = """
                    function Main.print 1
                    push argument 0
                    pop pointer 0
                    push pointer 0
                    pop local 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void string() {
            // given
            String jackCode = """
                    class Main {
                        function void more() {
                           var String s;
                           let s = "abcd";
                           return;
                        }
                    }
                    """;
            String expected = """
                    function Main.more 1
                    push constant 4
                    call String.new 1
                    push constant 97
                    call String.appendChar 2
                    push constant 98
                    call String.appendChar 2
                    push constant 99
                    call String.appendChar 2
                    push constant 100
                    call String.appendChar 2
                    pop local 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void negative() {
            // given
            String jackCode = """
                    class Main {
                        function void more() {
                           var int s;
                           let s = -123;
                           return;
                        }
                    }
                    """;
            String expected = """
                    function Main.more 1
                    push constant 123
                    neg
                    pop local 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class Call {
        @Test
        public void OsApi() {
            // given
            String jackCode = """
                    class Main {
                        function void main() {
                            do Output.printString();
                            return;
                        }
                    }
                    """;
            String expected = """
                    function Main.main 0
                    call Output.printString 0
                    pop temp 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void OsApiExpression() {
            // given
            String jackCode = """
                    class Main {
                       field int x, y;
                    
                       method void moveLeft() {
                          do Screen.some((x + 2) - 3, y);
                          return;
                       }
                    }
                    """;
            String expected = """
                    function Main.moveLeft 0
                    push argument 0
                    pop pointer 0
                    push this 0
                    push constant 2
                    add
                    push constant 3
                    sub
                    push this 1
                    call Screen.some 2
                    pop temp 0
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
                            do Main.checkRange(3, 4);
                            return;
                        }
                    
                        function void checkRange(int a, int a_len) {
                            return;
                        }
                    }
                    """;
            String expected = """
                    function Main.main 0
                    push constant 3
                    push constant 4
                    call Main.checkRange 2
                    pop temp 0
                    push constant 0
                    return
                    function Main.checkRange 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void method() {
            // given
            String jackCode = """
                    class Square {
                       method void erase() {
                          return;
                       }
                    
                       method void eraseSecond() {
                          do erase();
                          return;
                       }
                    }
                    """;
            String expected = """
                    function Square.erase 0
                    push argument 0
                    pop pointer 0
                    push constant 0
                    return
                    function Square.eraseSecond 0
                    push argument 0
                    pop pointer 0
                    call Square.erase 1
                    pop temp 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void methodOfObject() {
            // given
            String jackCode = """
                    class SquareGame {
                       field Square square;
                    
                       method void dispose() {
                          do square.dispose();
                          return;
                       }
                    }
                    """;
            String expected = """
                    function SquareGame.dispose 0
                    push argument 0
                    pop pointer 0
                    push this 0
                    call Square.dispose 1
                    pop temp 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void methodAsArgument() {
            // given
            String jackCode = """
                    class Square {
                       method int getData() { return 3; }
                    
                       method void print() {
                          var List current;
                          let current = this;
                          do Output.printInt(current.getData());
                          return;
                       }
                    }
                    """;
            String expected = """
                    function Square.getData 0
                    push argument 0
                    pop pointer 0
                    push constant 3
                    return
                    function Square.print 1
                    push argument 0
                    pop pointer 0
                    push pointer 0
                    pop local 0
                    push local 0
                    call List.getData 1
                    call Output.printInt 1
                    pop temp 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void functionAsArgument() {
            // given
            String jackCode = """
                    class Square {
                       function int getData() { return 3; }
                    
                       method void print() {
                          do Output.printInt(Square.getData());
                          return;
                       }
                    }
                    """;
            String expected = """
                    function Square.getData 0
                    push constant 3
                    return
                    function Square.print 0
                    push argument 0
                    pop pointer 0
                    call Square.getData 0
                    call Output.printInt 1
                    pop temp 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class JackObject {
        @Test
        public void constructorNoField() {
            // given
            String jackCode = """
                    class Point {
                        constructor Point new(int x) {
                            do Output.printInt(x);
                            return this;
                        }
                    }
                    """;
            String expected = """
                    function Point.new 0
                    push constant 0
                    call Memory.alloc 1
                    pop pointer 0
                    push argument 0
                    call Output.printInt 1
                    pop temp 0
                    push pointer 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void constructor() {
            // given
            String jackCode = """
                    class Point {
                        field int x, y;
                        static int pointCount;
                    
                        constructor Point new(int ax, int ay) {
                            let x = ax;
                            let y = ay;
                            let pointCount = pointCount + 1;
                            return this;
                        }
                    }
                    """;
            String expected = """
                    function Point.new 0
                    push constant 2
                    call Memory.alloc 1
                    pop pointer 0
                    push argument 0
                    pop this 0
                    push argument 1
                    pop this 1
                    push static 0
                    push constant 1
                    add
                    pop static 0
                    push pointer 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void returnConstructedObject() {
            // given
            String jackCode = """
                    class Fraction {
                       field int numerator, denominator;
                    
                       constructor Fraction new(int x, int y) {
                          let numerator = x;
                          let denominator = y;
                          return this;
                       }
                    
                       method Fraction plus(Fraction other) {
                          return Fraction.new(100, 200);
                       }
                    }
                    """;
            String expected = """
                    function Fraction.new 0
                    push constant 2
                    call Memory.alloc 1
                    pop pointer 0
                    push argument 0
                    pop this 0
                    push argument 1
                    pop this 1
                    push pointer 0
                    return
                    function Fraction.plus 0
                    push argument 0
                    pop pointer 0
                    push constant 100
                    push constant 200
                    call Fraction.new 2
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void returnStaticObject() {
            // given
            String jackCode = """
                    class PongGame {
                        static PongGame instance;
                    
                        function PongGame getInstance() {
                            return instance;
                        }
                    }
                    """;
            String expected = """
                    function PongGame.getInstance 0
                    push static 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class OsArray {
        @Test
        public void initialize() {
            // given
            String jackCode = """
                    class Main {
                        function void main() {
                           var Array a;
                           let a = Array.new(10);
                           return;
                        }
                    }
                    """;
            String expected = """
                    function Main.main 1
                    push constant 10
                    call Array.new 1
                    pop local 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void assign() {
            // given
            String jackCode = """
                    class Main {
                        static Array a;
                        function void main() {
                           let a[2] = 222;
                           return;
                        }
                    }
                    """;
            String expected = """
                    function Main.main 0
                    push constant 2
                    push static 0
                    add
                    push constant 222
                    pop temp 0
                    pop pointer 1
                    push temp 0
                    pop that 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void returnArray() {
            // given
            String jackCode = """
                    class Output {
                        static Array charMaps;
                    
                        function Array getMap(char c) {
                            return charMaps[c];
                        }
                    }
                    """;
            String expected = """
                    function Output.getMap 0
                    push argument 0
                    push static 0
                    add
                    pop pointer 1
                    push that 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void complex() {
            // given
            String jackCode = """
                    class Array {
                        static Array a, b;
                    
                        function void complex(int i, int j) {
                           let a[b[i] + a[j + b[a[3]]]] = b[b[j] + 2];
                           return;
                        }
                    }
                    """;
            String expected = """
                    function Array.complex 0
                    push argument 0
                    push static 1
                    add
                    pop pointer 1
                    push that 0
                    push argument 1
                    push constant 3
                    push static 0
                    add
                    pop pointer 1
                    push that 0
                    push static 1
                    add
                    pop pointer 1
                    push that 0
                    add
                    push static 0
                    add
                    pop pointer 1
                    push that 0
                    add
                    push static 0
                    add
                    push argument 1
                    push static 1
                    add
                    pop pointer 1
                    push that 0
                    push constant 2
                    add
                    push static 1
                    add
                    pop pointer 1
                    push that 0
                    pop temp 0
                    pop pointer 1
                    push temp 0
                    pop that 0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class Condition {
        @Test
        public void ifOnly() {
            // given
            String jackCode = """
                    class Main {
                         static boolean b;
                         function void more() {
                             if (b) {
                             }
                             return;
                         }
                    }
                    """;
            String expected = """
                    function Main.more 0
                    push static 0
                    if-goto IF_TRUE0
                    goto IF_FALSE0
                    label IF_TRUE0
                    label IF_FALSE0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void ifElse() {
            // given
            String jackCode = """
                    class Main {
                         static boolean b;
                         function void more() {
                             if (b) {
                             }
                             else {
                             }
                             return;
                         }
                    }
                    """;
            String expected = """
                    function Main.more 0
                    push static 0
                    if-goto IF_TRUE0
                    goto IF_FALSE0
                    label IF_TRUE0
                    goto IF_END0
                    label IF_FALSE0
                    label IF_END0
                    push constant 0
                    return
                    """;

            // when
            String actual = getCompileOutput(jackCode);

            // then
            Assertions.assertEquals(expected, actual);
        }


        @Test
        public void whileCondition() {
            // given
            String jackCode = """
                    class SquareGame {
                       static char key;
                       function void run() {
                          while (key = 0) {
                          }
                          return;
                       }
                    }
                    """;
            String expected = """
                    function SquareGame.run 0
                    label WHILE_EXP0
                    push static 0
                    push constant 0
                    eq
                    not
                    if-goto END_WHILE0
                    goto WHILE_EXP0
                    label END_WHILE0
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
