import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class CompilationEngine {
    private final JackTokenizer jackTokenizer;
    private final PrintWriter printWriter;


    public CompilationEngine(InputStream inputStream, OutputStream outputStream) {
        this.jackTokenizer = new JackTokenizer(inputStream);
        jackTokenizer.advance();
        this.printWriter = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
    }

    public void compileClass() {
    }

    public void compileClassVarDec() {
    }

    public void compileSubroutine() {
    }

    public void compileParameterList() {
    }

    public void compileSubroutineBody() {
    }

    public void compileVarDec() {
    }

    public void compileStatements() {
    }

    /**
     * let statement := `let`, `varName`, =, `expression`, and `;`
     */
    public void compileLet() {
        printWriter.println("<letStatement>");
        assert jackTokenizer.keyword() == Keyword.LET;
        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
        jackTokenizer.advance();
        printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
        jackTokenizer.advance();
        if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.symbol() == '[') {
            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
            jackTokenizer.advance();
            compileExpression();
            jackTokenizer.advance();
            assert jackTokenizer.symbol() == ']';
            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
            jackTokenizer.advance();
        }

        assert jackTokenizer.symbol() == '=';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileExpression();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ';';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        printWriter.println("</letStatement>");
    }


    /**
     * if statement := `if`, `(expression)`, `{statements}`
     */
    public void compileIf() {
    }

    /**
     * while statement := `if`, `(expression)`, `{statements}`
     */
    public void compileWhile() {
    }

    public void compileDo() {
        printWriter.println("<doStatement>");
        assert jackTokenizer.keyword() == Keyword.DO;
        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
        whileLoop:
        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == TokenType.SYMBOL) {
                switch (jackTokenizer.symbol()) {
                    case '(':
                        break whileLoop;
                    case '.':
                        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                        break;
                    default:
                        throw new RuntimeException("Unexpected symbol: " + jackTokenizer.symbol());
                }
            } else {
                printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            }
        }
        assert jackTokenizer.symbol() == '(';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileExpressionList();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ')';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ';';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        printWriter.println("</doStatement>");
    }

    public void compileReturn() {
        printWriter.println("<returnStatement>");
        assert jackTokenizer.keyword() == Keyword.RETURN;
        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
        jackTokenizer.advance();
        if (jackTokenizer.tokenType() != TokenType.SYMBOL || jackTokenizer.symbol() != ';') {
            compileExpression();
            jackTokenizer.advance();
        }
        assert jackTokenizer.symbol() == ';';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        printWriter.println("</returnStatement>");
    }

    /**
     * By definition, expression is either a term or (term op term).
     * op: operation such as +, -, *, /, =, >, <, &, |
     * Inferred: `<term>` only exists between `<expression>` and `</expression>`.
     */
    public void compileExpression() {
        printWriter.println("<expression>");
        compileTerm();
        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            if (
                    jackTokenizer.tokenType() == TokenType.SYMBOL &&
                            TokenType.isOperationSymbol(jackTokenizer.symbol())
            ) {
                switch (jackTokenizer.symbol()) {
                    case '>':
                        printWriter.println("<symbol> &gt; </symbol>");
                        break;
                    case '<':
                        printWriter.println("<symbol> &lt; </symbol>");
                        break;
                    case '&':
                        printWriter.println("<symbol> &amp; </symbol>");
                        break;
                    default:
                        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                        break;
                }
                jackTokenizer.advance();
                compileTerm();
            } else {
                jackTokenizer.retreat();
                break;
            }
        }
        printWriter.println("</expression>");
    }

    /**
     * By definition, term is either varName or constant.
     * varName: a string not beginning with a digit
     * constant: a non-negative integer
     */
    public void compileTerm() {
        printWriter.println("<term>");
        switch (jackTokenizer.tokenType()) {
            case IDENTIFIER:
                printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
                jackTokenizer.advance();

                if (jackTokenizer.tokenType() == TokenType.SYMBOL) {
                    switch (jackTokenizer.symbol()) {
                        case '[':
                            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                            jackTokenizer.advance();
                            compileExpression();
                            jackTokenizer.advance();
                            assert jackTokenizer.symbol() == ']';
                            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                            break;
                        case '.':
                            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                            jackTokenizer.advance();
                            printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
                            jackTokenizer.advance();
                            assert jackTokenizer.symbol() == '(';
                            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                            jackTokenizer.advance();
                            compileExpressionList();
                            jackTokenizer.advance();
                            assert jackTokenizer.symbol() == ')';
                            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                            break;
                        default:
                            jackTokenizer.retreat();
                            break;
                    }
                } else {
                    jackTokenizer.retreat();
                }
                break;
            case SYMBOL:
                switch (jackTokenizer.symbol()) {
                    case '(':
                        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                        jackTokenizer.advance();
                        compileExpression();
                        jackTokenizer.advance();
                        assert jackTokenizer.symbol() == ')';
                        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                        break;
                    case '-',
                         '~':
                        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                        jackTokenizer.advance();
                        compileTerm();
                        break;
                    default:
                        throw new RuntimeException("Not an eligible symbol in a term: " + jackTokenizer.symbol());
                }
                break;
            case KEYWORD:
                switch (jackTokenizer.keyword()) {
                    case TRUE,
                         FALSE,
                         NULL,
                         THIS:
                        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
                        break;
                    default:
                        throw new RuntimeException("Not an eligible keyword in a term: " + jackTokenizer.keyword());
                }
                break;
            case INT_CONST:
                printWriter.println("<integerConstant> " + jackTokenizer.intVal() + " </integerConstant>");
                break;
            case STRING_CONST:
                printWriter.println("<stringConstant> " + jackTokenizer.stringVal() + " </stringConstant>");
                break;
            default:
                throw new RuntimeException("Not a term");
        }
        printWriter.println("</term>");
    }

    /**
     * Implemented to return int value according to the textbook's requirements even though it is never used.
     */
    public int compileExpressionList() {
        printWriter.println("<expressionList>");
        int argumentCount = 0;
        whileLoop:
        while (jackTokenizer.hasMoreTokens()) {
            if (jackTokenizer.tokenType() == TokenType.SYMBOL) {
                switch (jackTokenizer.symbol()) {
                    case ')':
                        break whileLoop;
                    case '(':
                        compileExpression();
                        break;
                    case ',':
                        argumentCount++;
                        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                        jackTokenizer.advance();
                        compileExpression();
                        break;
                }
            } else {
                compileExpression();
            }
            jackTokenizer.advance();
        }

        assert jackTokenizer.symbol() == ')';
        jackTokenizer.retreat();
        printWriter.println("</expressionList>");
        return argumentCount;
    }

    // Custom method for testing.
    public void close() {
        printWriter.close();
    }
}
