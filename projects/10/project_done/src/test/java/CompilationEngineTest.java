import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

    @Nested
    class CompileDo {
        @Test
        public void function() {
            // given/when
            String actual = getCompileOutput("do Output.println();", CompilationEngine::compileDo);
            String expected = """
                    <doStatement>
                    <keyword> do </keyword>
                    <identifier> Output </identifier>
                    <symbol> . </symbol>
                    <identifier> println </identifier>
                    <symbol> ( </symbol>
                    <expressionList>
                    </expressionList>
                    <symbol> ) </symbol>
                    <symbol> ; </symbol>
                    </doStatement>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void method() {
            // given/when
            String actual = getCompileOutput("do draw();", CompilationEngine::compileDo);
            String expected = """
                    <doStatement>
                    <keyword> do </keyword>
                    <identifier> draw </identifier>
                    <symbol> ( </symbol>
                    <expressionList>
                    </expressionList>
                    <symbol> ) </symbol>
                    <symbol> ; </symbol>
                    </doStatement>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void otherMethod() {
            // given/when
            String actual = getCompileOutput("do game.run();", CompilationEngine::compileDo);
            String expected = """
                    <doStatement>
                    <keyword> do </keyword>
                    <identifier> game </identifier>
                    <symbol> . </symbol>
                    <identifier> run </identifier>
                    <symbol> ( </symbol>
                    <expressionList>
                    </expressionList>
                    <symbol> ) </symbol>
                    <symbol> ; </symbol>
                    </doStatement>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void arguments() {
            // given/when
            String actual = getCompileOutput("do Screen.setColor(x);", CompilationEngine::compileDo);
            String expected = """
                    <doStatement>
                    <keyword> do </keyword>
                    <identifier> Screen </identifier>
                    <symbol> . </symbol>
                    <identifier> setColor </identifier>
                    <symbol> ( </symbol>
                    <expressionList>
                    <expression>
                    <term>
                    <identifier> x </identifier>
                    </term>
                    </expression>
                    </expressionList>
                    <symbol> ) </symbol>
                    <symbol> ; </symbol>
                    </doStatement>
                    """;

            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class CompileIf {
        @Test
        public void compileIf() {
            // given/when
            String actual = getCompileOutput("if (key) { let exit = exit; }", CompilationEngine::compileIf);
            String expected = """
                    <ifStatement>
                    <keyword> if </keyword>
                    <symbol> ( </symbol>
                    <expression>
                    <term>
                    <identifier> key </identifier>
                    </term>
                    </expression>
                    <symbol> ) </symbol>
                    <symbol> { </symbol>
                    <statements>
                    <letStatement>
                    <keyword> let </keyword>
                    <identifier> exit </identifier>
                    <symbol> = </symbol>
                    <expression>
                    <term>
                    <identifier> exit </identifier>
                    </term>
                    </expression>
                    <symbol> ; </symbol>
                    </letStatement>
                    </statements>
                    <symbol> } </symbol>
                    </ifStatement>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void compileIfElse() {
            // given
            String jackCode = """
                    if (b) {
                    } else {
                    }
                    """;
            String expected = """
                    <ifStatement>
                    <keyword> if </keyword>
                    <symbol> ( </symbol>
                    <expression>
                    <term>
                    <identifier> b </identifier>
                    </term>
                    </expression>
                    <symbol> ) </symbol>
                    <symbol> { </symbol>
                    <statements>
                    </statements>
                    <symbol> } </symbol>
                    <keyword> else </keyword>
                    <symbol> { </symbol>
                    <statements>
                    </statements>
                    <symbol> } </symbol>
                    </ifStatement>
                    """;

            // when
            String actual = getCompileOutput(jackCode, CompilationEngine::compileIf);

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class CompileWhile {
        @Test
        public void compileWhile() {
            // given/when
            String actual = getCompileOutput("while (~(key = 0)) {}", CompilationEngine::compileWhile);
            String expected = """
                    <whileStatement>
                    <keyword> while </keyword>
                    <symbol> ( </symbol>
                    <expression>
                    <term>
                    <symbol> ~ </symbol>
                    <term>
                    <symbol> ( </symbol>
                    <expression>
                    <term>
                    <identifier> key </identifier>
                    </term>
                    <symbol> = </symbol>
                    <term>
                    <integerConstant> 0 </integerConstant>
                    </term>
                    </expression>
                    <symbol> ) </symbol>
                    </term>
                    </term>
                    </expression>
                    <symbol> ) </symbol>
                    <symbol> { </symbol>
                    <statements>
                    </statements>
                    <symbol> } </symbol>
                    </whileStatement>
                    """;

            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class CompileStatements {
        @Test
        @DisplayName("statements should end before '}'.")
        public void statements() {
            // given
            String jackCode = """
                    {
                        let game = game;
                        do game.run();
                        do game.dispose();
                        return;
                    }
                    """.substring(1);
            String expected = """
                    <statements>
                    <letStatement>
                    <keyword> let </keyword>
                    <identifier> game </identifier>
                    <symbol> = </symbol>
                    <expression>
                    <term>
                    <identifier> game </identifier>
                    </term>
                    </expression>
                    <symbol> ; </symbol>
                    </letStatement>
                    <doStatement>
                    <keyword> do </keyword>
                    <identifier> game </identifier>
                    <symbol> . </symbol>
                    <identifier> run </identifier>
                    <symbol> ( </symbol>
                    <expressionList>
                    </expressionList>
                    <symbol> ) </symbol>
                    <symbol> ; </symbol>
                    </doStatement>
                    <doStatement>
                    <keyword> do </keyword>
                    <identifier> game </identifier>
                    <symbol> . </symbol>
                    <identifier> dispose </identifier>
                    <symbol> ( </symbol>
                    <expressionList>
                    </expressionList>
                    <symbol> ) </symbol>
                    <symbol> ; </symbol>
                    </doStatement>
                    <returnStatement>
                    <keyword> return </keyword>
                    <symbol> ; </symbol>
                    </returnStatement>
                    </statements>
                    """;

            // when
            String actual = getCompileOutput(jackCode, CompilationEngine::compileStatements);

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class CompileParameterList {
        @Test
        public void parameterList() {
            // given/when
            String actual = getCompileOutput(
                    "(int Ax, int Ay, int Asize)".substring(1),
                    CompilationEngine::compileParameterList
            );
            String expected = """
                    <parameterList>
                    <keyword> int </keyword>
                    <identifier> Ax </identifier>
                    <symbol> , </symbol>
                    <keyword> int </keyword>
                    <identifier> Ay </identifier>
                    <symbol> , </symbol>
                    <keyword> int </keyword>
                    <identifier> Asize </identifier>
                    </parameterList>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void list() {
            // given/when
            String actual = getCompileOutput(
                    "(List cdr)".substring(1),
                    CompilationEngine::compileParameterList
            );
            String expected = """
                    <parameterList>
                    <identifier> List </identifier>
                    <identifier> cdr </identifier>
                    </parameterList>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class CompileVarDec {
        @Test
        public void primitive() {
            // given/when
            String actual = getCompileOutput("var int length;", CompilationEngine::compileVarDec);
            String expected = """
                    <varDec>
                    <keyword> var </keyword>
                    <keyword> int </keyword>
                    <identifier> length </identifier>
                    <symbol> ; </symbol>
                    </varDec>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void primitives() {
            // given/when
            String actual = getCompileOutput("var int i, sum;", CompilationEngine::compileVarDec);
            String expected = """
                    <varDec>
                    <keyword> var </keyword>
                    <keyword> int </keyword>
                    <identifier> i </identifier>
                    <symbol> , </symbol>
                    <identifier> sum </identifier>
                    <symbol> ; </symbol>
                    </varDec>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void array() {
            // given/when
            String actual = getCompileOutput("var Array a;", CompilationEngine::compileVarDec);
            String expected = """
                    <varDec>
                    <keyword> var </keyword>
                    <identifier> Array </identifier>
                    <identifier> a </identifier>
                    <symbol> ; </symbol>
                    </varDec>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class CompileSubroutineBody {
        @Test
        public void compileSubroutineBody() {
            // given
            String jackCode = """
                    {
                        var boolean b;
                        if (b) {
                        }
                        else {
                        }
                        return;
                    }
                    """;
            String expected = """
                    <subroutineBody>
                    <symbol> { </symbol>
                    <varDec>
                    <keyword> var </keyword>
                    <keyword> boolean </keyword>
                    <identifier> b </identifier>
                    <symbol> ; </symbol>
                    </varDec>
                    <statements>
                    <ifStatement>
                    <keyword> if </keyword>
                    <symbol> ( </symbol>
                    <expression>
                    <term>
                    <identifier> b </identifier>
                    </term>
                    </expression>
                    <symbol> ) </symbol>
                    <symbol> { </symbol>
                    <statements>
                    </statements>
                    <symbol> } </symbol>
                    <keyword> else </keyword>
                    <symbol> { </symbol>
                    <statements>
                    </statements>
                    <symbol> } </symbol>
                    </ifStatement>
                    <returnStatement>
                    <keyword> return </keyword>
                    <symbol> ; </symbol>
                    </returnStatement>
                    </statements>
                    <symbol> } </symbol>
                    </subroutineBody>
                    """;

            // when
            String actual = getCompileOutput(jackCode, CompilationEngine::compileSubroutineBody);

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class CompileSubroutine {
        @Test
        public void constructor() {
            // given
            String jackCode = """
                    constructor SquareGame new() {
                         let square = square;
                         let direction = direction;
                         return square;
                    }
                    """;
            String expected = """
                    <subroutineDec>
                    <keyword> constructor </keyword>
                    <identifier> SquareGame </identifier>
                    <identifier> new </identifier>
                    <symbol> ( </symbol>
                    <parameterList>
                    </parameterList>
                    <symbol> ) </symbol>
                    <subroutineBody>
                    <symbol> { </symbol>
                    <statements>
                    <letStatement>
                    <keyword> let </keyword>
                    <identifier> square </identifier>
                    <symbol> = </symbol>
                    <expression>
                    <term>
                    <identifier> square </identifier>
                    </term>
                    </expression>
                    <symbol> ; </symbol>
                    </letStatement>
                    <letStatement>
                    <keyword> let </keyword>
                    <identifier> direction </identifier>
                    <symbol> = </symbol>
                    <expression>
                    <term>
                    <identifier> direction </identifier>
                    </term>
                    </expression>
                    <symbol> ; </symbol>
                    </letStatement>
                    <returnStatement>
                    <keyword> return </keyword>
                    <expression>
                    <term>
                    <identifier> square </identifier>
                    </term>
                    </expression>
                    <symbol> ; </symbol>
                    </returnStatement>
                    </statements>
                    <symbol> } </symbol>
                    </subroutineBody>
                    </subroutineDec>
                    """;

            // when
            String actual = getCompileOutput(jackCode, CompilationEngine::compileSubroutine);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void method() {
            // given
            String jackCode = """
                    method void dispose() {
                        do Memory.deAlloc(this);
                        return;
                    }
                    """;
            String expected = """
                    <subroutineDec>
                    <keyword> method </keyword>
                    <keyword> void </keyword>
                    <identifier> dispose </identifier>
                    <symbol> ( </symbol>
                    <parameterList>
                    </parameterList>
                    <symbol> ) </symbol>
                    <subroutineBody>
                    <symbol> { </symbol>
                    <statements>
                    <doStatement>
                    <keyword> do </keyword>
                    <identifier> Memory </identifier>
                    <symbol> . </symbol>
                    <identifier> deAlloc </identifier>
                    <symbol> ( </symbol>
                    <expressionList>
                    <expression>
                    <term>
                    <keyword> this </keyword>
                    </term>
                    </expression>
                    </expressionList>
                    <symbol> ) </symbol>
                    <symbol> ; </symbol>
                    </doStatement>
                    <returnStatement>
                    <keyword> return </keyword>
                    <symbol> ; </symbol>
                    </returnStatement>
                    </statements>
                    <symbol> } </symbol>
                    </subroutineBody>
                    </subroutineDec>
                    """;

            // when
            String actual = getCompileOutput(jackCode, CompilationEngine::compileSubroutine);

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void function() {
            // given
            String jackCode = """
                    function void main() {
                        var SquareGame game;
                        let game = game;
                        do game.run();
                        do game.dispose();
                        return;
                    }
                    """;
            String expected = """
                    <subroutineDec>
                    <keyword> function </keyword>
                    <keyword> void </keyword>
                    <identifier> main </identifier>
                    <symbol> ( </symbol>
                    <parameterList>
                    </parameterList>
                    <symbol> ) </symbol>
                    <subroutineBody>
                    <symbol> { </symbol>
                    <varDec>
                    <keyword> var </keyword>
                    <identifier> SquareGame </identifier>
                    <identifier> game </identifier>
                    <symbol> ; </symbol>
                    </varDec>
                    <statements>
                    <letStatement>
                    <keyword> let </keyword>
                    <identifier> game </identifier>
                    <symbol> = </symbol>
                    <expression>
                    <term>
                    <identifier> game </identifier>
                    </term>
                    </expression>
                    <symbol> ; </symbol>
                    </letStatement>
                    <doStatement>
                    <keyword> do </keyword>
                    <identifier> game </identifier>
                    <symbol> . </symbol>
                    <identifier> run </identifier>
                    <symbol> ( </symbol>
                    <expressionList>
                    </expressionList>
                    <symbol> ) </symbol>
                    <symbol> ; </symbol>
                    </doStatement>
                    <doStatement>
                    <keyword> do </keyword>
                    <identifier> game </identifier>
                    <symbol> . </symbol>
                    <identifier> dispose </identifier>
                    <symbol> ( </symbol>
                    <expressionList>
                    </expressionList>
                    <symbol> ) </symbol>
                    <symbol> ; </symbol>
                    </doStatement>
                    <returnStatement>
                    <keyword> return </keyword>
                    <symbol> ; </symbol>
                    </returnStatement>
                    </statements>
                    <symbol> } </symbol>
                    </subroutineBody>
                    </subroutineDec>
                    """;

            // when
            String actual = getCompileOutput(jackCode, CompilationEngine::compileSubroutine);

            // then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class CompileClassVarDec {
        @Test
        public void primitive() {
            // given/when
            String staticActual = getCompileOutput("static boolean test;", CompilationEngine::compileClassVarDec);
            String staticExpected = """
                    <classVarDec>
                    <keyword> static </keyword>
                    <keyword> boolean </keyword>
                    <identifier> test </identifier>
                    <symbol> ; </symbol>
                    </classVarDec>
                    """;

            String fieldActual = getCompileOutput("field int size;", CompilationEngine::compileClassVarDec);
            String fieldExpected = """
                    <classVarDec>
                    <keyword> field </keyword>
                    <keyword> int </keyword>
                    <identifier> size </identifier>
                    <symbol> ; </symbol>
                    </classVarDec>
                    """;

            // then
            Assertions.assertAll(
                    () -> Assertions.assertEquals(staticExpected, staticActual),
                    () -> Assertions.assertEquals(fieldExpected, fieldActual)
            );
        }

        @Test
        public void primitives() {
            // given/when
            String actual = getCompileOutput("field int x, y;", CompilationEngine::compileClassVarDec);
            String expected = """
                    <classVarDec>
                    <keyword> field </keyword>
                    <keyword> int </keyword>
                    <identifier> x </identifier>
                    <symbol> , </symbol>
                    <identifier> y </identifier>
                    <symbol> ; </symbol>
                    </classVarDec>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        public void otherClass() {
            // given/when
            String actual = getCompileOutput("field Square square;", CompilationEngine::compileClassVarDec);
            String expected = """
                    <classVarDec>
                    <keyword> field </keyword>
                    <identifier> Square </identifier>
                    <identifier> square </identifier>
                    <symbol> ; </symbol>
                    </classVarDec>
                    """;

            // then
            Assertions.assertEquals(expected, actual);
        }
    }
}
