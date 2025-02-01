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
        printWriter.println("<class>");

        assert jackTokenizer.keyword() == Keyword.CLASS;
        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
        jackTokenizer.advance();
        printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
        jackTokenizer.advance();

        assert jackTokenizer.symbol() == '{';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");

        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.symbol() == '}') break;

            switch (jackTokenizer.keyword()) {
                case STATIC,
                     FIELD:
                    compileClassVarDec();
                    break;
                case CONSTRUCTOR,
                     METHOD,
                     FUNCTION:
                    compileSubroutine();
                    break;
                default:
                    throw new RuntimeException("compileClass failed with: " + jackTokenizer.keyword());
            }
        }

        assert jackTokenizer.symbol() == '}';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");

        printWriter.println("</class>");
    }

    public void compileClassVarDec() {
        printWriter.println("<classVarDec>");

        assert jackTokenizer.keyword() == Keyword.STATIC ||
                jackTokenizer.keyword() == Keyword.FIELD;
        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
        jackTokenizer.advance();
        switch (jackTokenizer.tokenType()) {
            case KEYWORD:
                printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
                break;
            case IDENTIFIER:
                printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
                break;
            default:
                throw new RuntimeException("Either keyword or identifier tokenType expected but found: " + jackTokenizer.tokenType());
        }

        whileLoop:
        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            jackTokenizer.advance();
            switch (jackTokenizer.symbol()) {
                case ',':
                    printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                    break;
                case ';':
                    printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                    break whileLoop;
                default:
                    throw new RuntimeException("Either ',' or ';' expected but found: " + jackTokenizer.symbol());
            }
        }

        printWriter.println("</classVarDec>");
    }

    public void compileSubroutine() {
        printWriter.println("<subroutineDec>");

        assert jackTokenizer.keyword() == Keyword.CONSTRUCTOR ||
                jackTokenizer.keyword() == Keyword.METHOD ||
                jackTokenizer.keyword() == Keyword.FUNCTION;
        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
        jackTokenizer.advance();
        switch (jackTokenizer.tokenType()) {
            case KEYWORD:
                printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
                break;
            case IDENTIFIER:
                printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
                break;
            default:
                throw new RuntimeException("Either keyword or identifier tokenType expected but found: " + jackTokenizer.tokenType());
        }
        jackTokenizer.advance();
        printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
        jackTokenizer.advance();

        assert jackTokenizer.symbol() == '(';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileParameterList();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ')';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileSubroutineBody();

        printWriter.println("</subroutineDec>");
    }

    public void compileParameterList() {
        printWriter.println("<parameterList>");
        while (jackTokenizer.hasMoreTokens()) {
            if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.symbol() == ')') {
                jackTokenizer.retreat();
                break;
            }

            assert jackTokenizer.tokenType() == TokenType.KEYWORD ||
                    jackTokenizer.tokenType() == TokenType.IDENTIFIER;
            switch (jackTokenizer.tokenType()) {
                case KEYWORD:
                    printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
                    break;
                case IDENTIFIER:
                    printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
                    break;
            }
            jackTokenizer.advance();
            printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            jackTokenizer.advance();
            if (jackTokenizer.symbol() == ',') {
                printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                jackTokenizer.advance();
            }
        }
        printWriter.println("</parameterList>");
    }

    public void compileSubroutineBody() {
        printWriter.println("<subroutineBody>");
        assert jackTokenizer.symbol() == '{';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");

        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == TokenType.KEYWORD && jackTokenizer.keyword() == Keyword.VAR) {
                compileVarDec();
            } else {
                compileStatements();
                jackTokenizer.advance();
                break;
            }
        }
        assert jackTokenizer.symbol() == '}';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");

        printWriter.println("</subroutineBody>");
    }

    public void compileVarDec() {
        printWriter.println("<varDec>");
        assert jackTokenizer.keyword() == Keyword.VAR;
        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
        jackTokenizer.advance();

        switch (jackTokenizer.tokenType()) {
            case KEYWORD:
                printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
                break;
            case IDENTIFIER:
                printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
                break;
            default:
                throw new RuntimeException("Either keyword or identifier tokenType expected but found: " + jackTokenizer.tokenType());
        }

        whileLoop:
        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            jackTokenizer.advance();
            switch (jackTokenizer.symbol()) {
                case ',':
                    printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                    break;
                case ';':
                    printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                    break whileLoop;
                default:
                    throw new RuntimeException("Either ',' or ';' expected but found: " + jackTokenizer.symbol());
            }
        }

        printWriter.println("</varDec>");
    }

    /**
     * Called only if the previous token is '{'.
     */
    public void compileStatements() {
        printWriter.println("<statements>");
        while (jackTokenizer.hasMoreTokens()) {
            if (jackTokenizer.tokenType() == TokenType.KEYWORD) {
                switch (jackTokenizer.keyword()) {
                    case LET:
                        compileLet();
                        break;
                    case RETURN:
                        compileReturn();
                        break;
                    case DO:
                        compileDo();
                        break;
                    case IF:
                        compileIf();
                        break;
                    case WHILE:
                        compileWhile();
                        break;
                    default:
                        throw new RuntimeException("Statement kind keyword expected but found: " + jackTokenizer.keyword());
                }
                jackTokenizer.advance();
            } else {
                break;
            }
        }
        assert jackTokenizer.symbol() == '}';
        jackTokenizer.retreat();
        printWriter.println("</statements>");
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
        printWriter.println("<ifStatement>");

        assert jackTokenizer.keyword() == Keyword.IF;
        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == '(';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileExpression();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ')';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == '{';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileStatements();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == '}';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");

        if (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == TokenType.KEYWORD && jackTokenizer.keyword() == Keyword.ELSE) {
                printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
                jackTokenizer.advance();
                assert jackTokenizer.symbol() == '{';
                printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                jackTokenizer.advance();
                compileStatements();
                jackTokenizer.advance();
                assert jackTokenizer.symbol() == '}';
                printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
            } else {
                jackTokenizer.retreat();
            }
        }

        printWriter.println("</ifStatement>");
    }

    /**
     * while statement := `while`, `(expression)`, `{statements}`
     */
    public void compileWhile() {
        printWriter.println("<whileStatement>");

        assert jackTokenizer.keyword() == Keyword.WHILE;
        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
        jackTokenizer.advance();

        assert jackTokenizer.symbol() == '(';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileExpression();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ')';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();

        assert jackTokenizer.symbol() == '{';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileStatements();
        jackTokenizer.advance();

        assert jackTokenizer.symbol() == '}';
        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");

        printWriter.println("</whileStatement>");
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
        int expressionCount = 0;
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
                        if (expressionCount == 0) {
                            expressionCount++;
                        }
                        expressionCount++;
                        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                        jackTokenizer.advance();
                        compileExpression();
                        break;
                }
            } else {
                expressionCount++;
                compileExpression();
            }
            jackTokenizer.advance();
        }

        assert jackTokenizer.symbol() == ')';
        jackTokenizer.retreat();
        printWriter.println("</expressionList>");
        return expressionCount;
    }

    public void close() {
        printWriter.close();
    }
}
