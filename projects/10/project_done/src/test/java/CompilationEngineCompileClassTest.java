import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

class CompilationEngineCompileClassTest {
    private static String getCompileOutput(String jackCode) {
        InputStream inputStream = new ByteArrayInputStream(jackCode.getBytes(UTF_8));
        OutputStream outputStream = new ByteArrayOutputStream();
        CompilationEngine compilationEngine = new CompilationEngine(inputStream, outputStream);
        compilationEngine.compileClass();
        compilationEngine.close();
        return outputStream.toString();
    }

    @Test
    public void noVariable() {
        // given
        String jackCode = """
                class Main {
                    function void main() {
                        var Array a;
                        var int length;
                        var int i, sum;
                
                	let length = Keyboard.readInt("HOW MANY NUMBERS? ");
                	let a = Array.new(length);
                	let i = 0;
                
                	while (i < length) {
                	    let a[i] = Keyboard.readInt("ENTER THE NEXT NUMBER: ");
                	    let i = i + 1;
                	}
                
                	let i = 0;
                	let sum = 0;
                
                	while (i < length) {
                	    let sum = sum + a[i];
                	    let i = i + 1;
                	}
                
                	do Output.printString("THE AVERAGE IS: ");
                	do Output.printInt(sum / length);
                	do Output.println();
                
                	return;
                    }
                }
                """;
        String expected = """
                <class>
                <keyword> class </keyword>
                <identifier> Main </identifier>
                <symbol> { </symbol>
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
                <identifier> Array </identifier>
                <identifier> a </identifier>
                <symbol> ; </symbol>
                </varDec>
                <varDec>
                <keyword> var </keyword>
                <keyword> int </keyword>
                <identifier> length </identifier>
                <symbol> ; </symbol>
                </varDec>
                <varDec>
                <keyword> var </keyword>
                <keyword> int </keyword>
                <identifier> i </identifier>
                <symbol> , </symbol>
                <identifier> sum </identifier>
                <symbol> ; </symbol>
                </varDec>
                <statements>
                <letStatement>
                <keyword> let </keyword>
                <identifier> length </identifier>
                <symbol> = </symbol>
                <expression>
                <term>
                <identifier> Keyboard </identifier>
                <symbol> . </symbol>
                <identifier> readInt </identifier>
                <symbol> ( </symbol>
                <expressionList>
                <expression>
                <term>
                <stringConstant> HOW MANY NUMBERS?  </stringConstant>
                </term>
                </expression>
                </expressionList>
                <symbol> ) </symbol>
                </term>
                </expression>
                <symbol> ; </symbol>
                </letStatement>
                <letStatement>
                <keyword> let </keyword>
                <identifier> a </identifier>
                <symbol> = </symbol>
                <expression>
                <term>
                <identifier> Array </identifier>
                <symbol> . </symbol>
                <identifier> new </identifier>
                <symbol> ( </symbol>
                <expressionList>
                <expression>
                <term>
                <identifier> length </identifier>
                </term>
                </expression>
                </expressionList>
                <symbol> ) </symbol>
                </term>
                </expression>
                <symbol> ; </symbol>
                </letStatement>
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
                <whileStatement>
                <keyword> while </keyword>
                <symbol> ( </symbol>
                <expression>
                <term>
                <identifier> i </identifier>
                </term>
                <symbol> &lt; </symbol>
                <term>
                <identifier> length </identifier>
                </term>
                </expression>
                <symbol> ) </symbol>
                <symbol> { </symbol>
                <statements>
                <letStatement>
                <keyword> let </keyword>
                <identifier> a </identifier>
                <symbol> [ </symbol>
                <expression>
                <term>
                <identifier> i </identifier>
                </term>
                </expression>
                <symbol> ] </symbol>
                <symbol> = </symbol>
                <expression>
                <term>
                <identifier> Keyboard </identifier>
                <symbol> . </symbol>
                <identifier> readInt </identifier>
                <symbol> ( </symbol>
                <expressionList>
                <expression>
                <term>
                <stringConstant> ENTER THE NEXT NUMBER:  </stringConstant>
                </term>
                </expression>
                </expressionList>
                <symbol> ) </symbol>
                </term>
                </expression>
                <symbol> ; </symbol>
                </letStatement>
                <letStatement>
                <keyword> let </keyword>
                <identifier> i </identifier>
                <symbol> = </symbol>
                <expression>
                <term>
                <identifier> i </identifier>
                </term>
                <symbol> + </symbol>
                <term>
                <integerConstant> 1 </integerConstant>
                </term>
                </expression>
                <symbol> ; </symbol>
                </letStatement>
                </statements>
                <symbol> } </symbol>
                </whileStatement>
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
                <letStatement>
                <keyword> let </keyword>
                <identifier> sum </identifier>
                <symbol> = </symbol>
                <expression>
                <term>
                <integerConstant> 0 </integerConstant>
                </term>
                </expression>
                <symbol> ; </symbol>
                </letStatement>
                <whileStatement>
                <keyword> while </keyword>
                <symbol> ( </symbol>
                <expression>
                <term>
                <identifier> i </identifier>
                </term>
                <symbol> &lt; </symbol>
                <term>
                <identifier> length </identifier>
                </term>
                </expression>
                <symbol> ) </symbol>
                <symbol> { </symbol>
                <statements>
                <letStatement>
                <keyword> let </keyword>
                <identifier> sum </identifier>
                <symbol> = </symbol>
                <expression>
                <term>
                <identifier> sum </identifier>
                </term>
                <symbol> + </symbol>
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
                </expression>
                <symbol> ; </symbol>
                </letStatement>
                <letStatement>
                <keyword> let </keyword>
                <identifier> i </identifier>
                <symbol> = </symbol>
                <expression>
                <term>
                <identifier> i </identifier>
                </term>
                <symbol> + </symbol>
                <term>
                <integerConstant> 1 </integerConstant>
                </term>
                </expression>
                <symbol> ; </symbol>
                </letStatement>
                </statements>
                <symbol> } </symbol>
                </whileStatement>
                <doStatement>
                <keyword> do </keyword>
                <identifier> Output </identifier>
                <symbol> . </symbol>
                <identifier> printString </identifier>
                <symbol> ( </symbol>
                <expressionList>
                <expression>
                <term>
                <stringConstant> THE AVERAGE IS:  </stringConstant>
                </term>
                </expression>
                </expressionList>
                <symbol> ) </symbol>
                <symbol> ; </symbol>
                </doStatement>
                <doStatement>
                <keyword> do </keyword>
                <identifier> Output </identifier>
                <symbol> . </symbol>
                <identifier> printInt </identifier>
                <symbol> ( </symbol>
                <expressionList>
                <expression>
                <term>
                <identifier> sum </identifier>
                </term>
                <symbol> / </symbol>
                <term>
                <identifier> length </identifier>
                </term>
                </expression>
                </expressionList>
                <symbol> ) </symbol>
                <symbol> ; </symbol>
                </doStatement>
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
                <returnStatement>
                <keyword> return </keyword>
                <symbol> ; </symbol>
                </returnStatement>
                </statements>
                <symbol> } </symbol>
                </subroutineBody>
                </subroutineDec>
                <symbol> } </symbol>
                </class>
                """;

        // when
        String actual = getCompileOutput(jackCode);

        // then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void variable() {
        // given
        String jackCode = """
                class Main {
                    static boolean test;
                
                    function void main() {
                        var SquareGame game;
                        let game = game;
                        do game.run();
                        do game.dispose();
                        return;
                    }
                
                    function void more() {
                        var boolean b;
                        if (b) {
                        }
                        else {
                        }
                        return;
                    }
                }
                """;
        String expected = """
                <class>
                <keyword> class </keyword>
                <identifier> Main </identifier>
                <symbol> { </symbol>
                <classVarDec>
                <keyword> static </keyword>
                <keyword> boolean </keyword>
                <identifier> test </identifier>
                <symbol> ; </symbol>
                </classVarDec>
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
                <subroutineDec>
                <keyword> function </keyword>
                <keyword> void </keyword>
                <identifier> more </identifier>
                <symbol> ( </symbol>
                <parameterList>
                </parameterList>
                <symbol> ) </symbol>
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
                </subroutineDec>
                <symbol> } </symbol>
                </class>
                """;

        // when
        String actual = getCompileOutput(jackCode);

        // then
        Assertions.assertEquals(expected, actual);
    }
}