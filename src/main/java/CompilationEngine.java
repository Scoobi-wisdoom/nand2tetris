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
    }

    public void compileReturn() {
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
                if (
                        jackTokenizer.symbol() == '-' ||
                                jackTokenizer.symbol() == '~'
                ) {
                    printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                    jackTokenizer.advance();
                    compileTerm();
                } else {
                    throw new RuntimeException("Not an eligible symbol in a term: " + jackTokenizer.symbol());
                }
                break;
            case KEYWORD:
                if (
                        jackTokenizer.keyword() == Keyword.TRUE ||
                                jackTokenizer.keyword() == Keyword.FALSE ||
                                jackTokenizer.keyword() == Keyword.NULL ||
                                jackTokenizer.keyword() == Keyword.THIS
                ) {
                    printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
                } else {
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

    public void compileExpressionList() {
        printWriter.println("<expressionList>");
        if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.symbol() == ')') {
            jackTokenizer.retreat();
        }
        printWriter.println("</expressionList>");
    }

    // Custom method for testing.
    public void close() {
        printWriter.close();
    }
}
