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
        public void parenthesis() {
            // given/when
            String actual = getCompileOutput("(x + size) - 1", CompilationEngine::compileExpression);
            String expected = """
                    <expression>
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
                    <symbol> - </symbol>
                    <term>
                    <integerConstant> 1 </integerConstant>
                    </term>
                    </expression>
                    """;

            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void noParenthesis() {
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

        @Test
        public void constructor() {
            // given/when
            String actual = getCompileOutput("SquareGame.new()", CompilationEngine::compileExpression);
            String expected = """
                    <expression>
                    <term>
                    <identifier> SquareGame </identifier>
                    <symbol> . </symbol>
                    <identifier> new </identifier>
                    <symbol> ( </symbol>
                    <expressionList>
                    </expressionList>
                    <symbol> ) </symbol>
                    </term>
                    </expression>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class CompileExpressionList {
        @Test
        public void simple() {
            String actual = getCompileOutput("(x, y, x, y)"
                            .substring(1),
                    CompilationEngine::compileExpressionList
            );
            String expected = """
                    <expressionList>
                    <expression>
                    <term>
                    <identifier> x </identifier>
                    </term>
                    </expression>
                    <symbol> , </symbol>
                    <expression>
                    <term>
                    <identifier> y </identifier>
                    </term>
                    </expression>
                    <symbol> , </symbol>
                    <expression>
                    <term>
                    <identifier> x </identifier>
                    </term>
                    </expression>
                    <symbol> , </symbol>
                    <expression>
                    <term>
                    <identifier> y </identifier>
                    </term>
                    </expression>
                    </expressionList>
                    """;

            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void operation() {
            // given/when
            String actual = getCompileOutput(
                    "((x + size) - 1, y, x + size, y + size)"
                            .substring(1),
                    CompilationEngine::compileExpressionList
            );
            String expected = """
                    <expressionList>
                    <expression>
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
                    <symbol> - </symbol>
                    <term>
                    <integerConstant> 1 </integerConstant>
                    </term>
                    </expression>
                    <symbol> , </symbol>
                    <expression>
                    <term>
                    <identifier> y </identifier>
                    </term>
                    </expression>
                    <symbol> , </symbol>
                    <expression>
                    <term>
                    <identifier> x </identifier>
                    </term>
                    <symbol> + </symbol>
                    <term>
                    <identifier> size </identifier>
                    </term>
                    </expression>
                    <symbol> , </symbol>
                    <expression>
                    <term>
                    <identifier> y </identifier>
                    </term>
                    <symbol> + </symbol>
                    <term>
                    <identifier> size </identifier>
                    </term>
                    </expression>
                    </expressionList>
                    """;

            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class CompileLet {
        @Test
        public void plainIdentifier() {
            // given/when
            String stringConstantActual = getCompileOutput("let s = \"string constant\";", CompilationEngine::compileLet);
            String stringConstantExpected = """
                    <letStatement>
                    <keyword> let </keyword>
                    <identifier> s </identifier>
                    <symbol> = </symbol>
                    <expression>
                    <term>
                    <stringConstant> string constant </stringConstant>
                    </term>
                    </expression>
                    <symbol> ; </symbol>
                    </letStatement>
                    """;

            String integerConstantActual = getCompileOutput("let i = 0;", CompilationEngine::compileLet);
            String integerConstantExpected = """
                    <letStatement>
                    <keyword> let </keyword>
                    <identifier> i </identifier>
                    <symbol> = </symbol>
                    <expression>
                    <term>
                    <integerConstant> 0 </integerConstant>
                    </term>
                    </expression>
                    <symbol> ; </symbol>
                    </letStatement>
                    """;

            String nullActual = getCompileOutput("let s = null;", CompilationEngine::compileLet);
            String nullExpected = """
                    <letStatement>
                    <keyword> let </keyword>
                    <identifier> s </identifier>
                    <symbol> = </symbol>
                    <expression>
                    <term>
                    <keyword> null </keyword>
                    </term>
                    </expression>
                    <symbol> ; </symbol>
                    </letStatement>
                    """;

            Assertions.assertAll(
                    () -> Assertions.assertEquals(stringConstantExpected, stringConstantActual),
                    () -> Assertions.assertEquals(integerConstantExpected, integerConstantActual),
                    () -> Assertions.assertEquals(nullExpected, nullActual)
            );
        }

        @Test
        public void arrayIdentifier() {
            // given/when
            String actual = getCompileOutput("let a[1] = a[2];", CompilationEngine::compileLet);
            String expected = """
                    <letStatement>
                    <keyword> let </keyword>
                    <identifier> a </identifier>
                    <symbol> [ </symbol>
                    <expression>
                    <term>
                    <integerConstant> 1 </integerConstant>
                    </term>
                    </expression>
                    <symbol> ] </symbol>
                    <symbol> = </symbol>
                    <expression>
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
                    </expression>
                    <symbol> ; </symbol>
                    </letStatement>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class CompileReturn {
        @Test
        public void nothing() {
            // given/when
            String actual = getCompileOutput("return;", CompilationEngine::compileReturn);
            String expected = """
                    <returnStatement>
                    <keyword> return </keyword>
                    <symbol> ; </symbol>
                    </returnStatement>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void single() {
            // given/when
            String identifierActual = getCompileOutput("return x;", CompilationEngine::compileReturn);
            String identifierExpected = """
                    <returnStatement>
                    <keyword> return </keyword>
                    <expression>
                    <term>
                    <identifier> x </identifier>
                    </term>
                    </expression>
                    <symbol> ; </symbol>
                    </returnStatement>
                    """;

            String keywordActual = getCompileOutput("return this;", CompilationEngine::compileReturn);
            String keywordExpected = """
                    <returnStatement>
                    <keyword> return </keyword>
                    <expression>
                    <term>
                    <keyword> this </keyword>
                    </term>
                    </expression>
                    <symbol> ; </symbol>
                    </returnStatement>
                    """;

            // then
            Assertions.assertAll(
                    () -> Assertions.assertEquals(identifierExpected, identifierActual),
                    () -> Assertions.assertEquals(keywordExpected, keywordActual)
            );
        }

        @Test
        public void combination() {
            // given/when
            String operationActual = getCompileOutput("return x + 5;", CompilationEngine::compileReturn);
            String operationExpected = """
                    <returnStatement>
                    <keyword> return </keyword>
                    <expression>
                    <term>
                    <identifier> x </identifier>
                    </term>
                    <symbol> + </symbol>
                    <term>
                    <integerConstant> 5 </integerConstant>
                    </term>
                    </expression>
                    <symbol> ; </symbol>
                    </returnStatement>
                    """;

            String arrayActual = getCompileOutput("return charMaps[c];", CompilationEngine::compileReturn);
            String arrayExpected = """
                    <returnStatement>
                    <keyword> return </keyword>
                    <expression>
                    <term>
                    <identifier> charMaps </identifier>
                    <symbol> [ </symbol>
                    <expression>
                    <term>
                    <identifier> c </identifier>
                    </term>
                    </expression>
                    <symbol> ] </symbol>
                    </term>
                    </expression>
                    <symbol> ; </symbol>
                    </returnStatement>
                    """;

            // then
            Assertions.assertAll(
                    () -> Assertions.assertEquals(operationExpected, operationActual),
                    () -> Assertions.assertEquals(arrayExpected, arrayActual)
            );
        }
    }
}
