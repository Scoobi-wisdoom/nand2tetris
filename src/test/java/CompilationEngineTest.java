import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;

class CompilationEngineTest {
    private static String getCompileOutput(String jackCode, Consumer<CompilationEngine> compilationFunction) {
        InputStream inputStream = new ByteArrayInputStream(jackCode.getBytes(UTF_8));
        OutputStream outputStream = new ByteArrayOutputStream();
        CompilationEngine compilationEngine = new CompilationEngine(inputStream, outputStream);
        compilationFunction.accept(compilationEngine);
        compilationEngine.close();
        return outputStream.toString();
    }

    @Nested
    class CompileTerm {
        @Test
        public void integerConstant() {
            String jackCode = "1";
            String actual = CompilationEngineTest.getCompileOutput(jackCode, CompilationEngine::compileTerm);
            String expected = """
                    <term>
                    <integerConstant> 1 </integerConstant>
                    </term>
                    """;

            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void stringConstant() {
            String jackCode = "\"1\"";
            String actual = CompilationEngineTest.getCompileOutput(jackCode, CompilationEngine::compileTerm);
            String expected = """
                    <term>
                    <stringConstant> 1 </stringConstant>
                    </term>
                    """;

            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void keyword() {
            // given/when
            String trueActual = CompilationEngineTest.getCompileOutput("true", CompilationEngine::compileTerm);
            String expectedTrue = """
                    <term>
                    <keyword> true </keyword>
                    </term>
                    """;
            String falseActual = CompilationEngineTest.getCompileOutput("false", CompilationEngine::compileTerm);
            String expectedFalse = """
                    <term>
                    <keyword> false </keyword>
                    </term>
                    """;

            String nullActual = CompilationEngineTest.getCompileOutput("null", CompilationEngine::compileTerm);
            String nullExpected = """
                    <term>
                    <keyword> null </keyword>
                    </term>
                    """;

            String thisActual = CompilationEngineTest.getCompileOutput("this", CompilationEngine::compileTerm);
            String thisExpected = """
                    <term>
                    <keyword> this </keyword>
                    </term>
                    """;

            // then
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expectedTrue, trueActual),
                    () -> Assertions.assertEquals(expectedFalse, falseActual),
                    () -> Assertions.assertEquals(nullExpected, nullActual),
                    () -> Assertions.assertEquals(thisExpected, thisActual)
            );
        }

        @Test
        public void negateSymbols() {
            // given/when
            String negativeActual = CompilationEngineTest.getCompileOutput("-1", CompilationEngine::compileTerm);
            String negativeExpected = """
                    <term>
                    <symbol> - </symbol>
                    <term>
                    <integerConstant> 1 </integerConstant>
                    </term>
                    </term>
                    """;
            String notActual = CompilationEngineTest.getCompileOutput("~true", CompilationEngine::compileTerm);
            String notExpected = """
                    <term>
                    <symbol> ~ </symbol>
                    <term>
                    <keyword> true </keyword>
                    </term>
                    </term>
                    """;

            Assertions.assertAll(
                    () -> Assertions.assertEquals(negativeExpected, negativeActual),
                    () -> Assertions.assertEquals(notExpected, notActual)
            );
        }

        @Test
        public void parenthesisSymbol() {
            // given/when
            String actual = getCompileOutput("(x + size)", CompilationEngine::compileTerm);
            String expected = """
                    <term>
                    <symbol> ( </symbol>
                    <expression>
                    <term>
                    <identifier> x </identifier>
                    </term>
                    <symbol> + </symbol>
                    <term>
                    <identifier> size </identifier>
                    </term>
                    </expression>
                    <symbol> ) </symbol>
                    </term>
                    """;

            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void arrayIdentifier() {
            // given/when
            String integerActual = CompilationEngineTest.getCompileOutput("a[2]", CompilationEngine::compileTerm);
            String integerExpected = """
                    <term>
                    <identifier> a </identifier>
                    <symbol> [ </symbol>
                    <expression>
                    <term>
                    <integerConstant> 2 </integerConstant>
                    </term>
                    </expression>
                    <symbol> ] </symbol>
                    </term>
                    """;

            String identifierActual = CompilationEngineTest.getCompileOutput("a[i]", CompilationEngine::compileTerm);
            String identifierExpected = """
                    <term>
                    <identifier> a </identifier>
                    <symbol> [ </symbol>
                    <expression>
                    <term>
                    <identifier> i </identifier>
                    </term>
                    </expression>
                    <symbol> ] </symbol>
                    </term>
                    """;

            // then
            Assertions.assertAll(
                    () -> Assertions.assertEquals(integerExpected, integerActual),
                    () -> Assertions.assertEquals(identifierExpected, identifierActual)
            );
        }

        @Test
        public void methodIdentifier() {
            // given/when
            String actual = CompilationEngineTest.getCompileOutput("SquareGame.new()", CompilationEngine::compileTerm);
            String expected = """
                    <term>
                    <identifier> SquareGame </identifier>
                    <symbol> . </symbol>
                    <identifier> new </identifier>
                    <symbol> ( </symbol>
                    <expressionList>
                    </expressionList>
                    <symbol> ) </symbol>
                    </term>
                    """;

            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class CompileExpression {
        @Test
        public void operations() {
            // given/when
            String actual = getCompileOutput("i + 0 - 1", CompilationEngine::compileExpression);
            String expected = """
                    <expression>
                    <term>
                    <identifier> i </identifier>
                    </term>
                    <symbol> + </symbol>
                    <term>
                    <integerConstant> 0 </integerConstant>
                    </term>
                    <symbol> - </symbol>
                    <term>
                    <integerConstant> 1 </integerConstant>
                    </term>
                    </expression>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }
    }
}
