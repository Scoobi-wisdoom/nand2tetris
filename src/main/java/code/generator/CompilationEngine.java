package code.generator;

import syntax.analyzer.JackTokenizer;
import syntax.analyzer.Keyword;
import syntax.analyzer.TokenType;

import java.io.InputStream;
import java.io.OutputStream;

import static code.generator.MemorySegment.ARGUMENT;
import static code.generator.MemorySegment.CONSTANT;
import static code.generator.MemorySegment.POINTER;
import static code.generator.SymbolKind.ARG;
import static code.generator.SymbolKind.VAR;
import static code.generator.SymbolTableLevel.KLASS;
import static code.generator.SymbolTableLevel.SUBROUTINE;

public class CompilationEngine {
    private final JackTokenizer jackTokenizer;
    private final VMWriter vmWriter;
    private final SymbolTable klassSymbolTable;
    private final SymbolTable subroutineSymbolTable;
    private String currentKlassName = "";
    private String currentFunctionName = "";
    private boolean isMethod = false;

    public CompilationEngine(InputStream inputStream, OutputStream outputStream) {
        this.jackTokenizer = new JackTokenizer(inputStream);
        this.vmWriter = new VMWriter(outputStream);
        this.klassSymbolTable = new SymbolTable(KLASS);
        this.subroutineSymbolTable = new SymbolTable(SUBROUTINE);
        jackTokenizer.advance();
    }

    public void compileClass() {
        assert jackTokenizer.keyword() == Keyword.CLASS;
        jackTokenizer.advance();
        currentKlassName = jackTokenizer.identifier();
        jackTokenizer.advance();

        assert jackTokenizer.symbol() == '{';

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
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");

//        printWriter.println("</class>");

        vmWriter.close();
    }

    private void compileClassVarDec() {
        SymbolKind symbolKind = switch (jackTokenizer.keyword()) {
            case STATIC:
                yield SymbolKind.STATIC;
            case FIELD:
                yield SymbolKind.FIELD;
            default:
                throw new RuntimeException("Class SymbolTable does no allow keyword: " + jackTokenizer.keyword());
        };
        jackTokenizer.advance();

        String symbolType = switch (jackTokenizer.tokenType()) {
            case KEYWORD:
                yield jackTokenizer.keyword().toString();
            case IDENTIFIER:
                yield jackTokenizer.identifier();
            default:
                throw new RuntimeException("Either keyword or identifier tokenType expected but found: " + jackTokenizer.tokenType());
        };

        whileLoop:
        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            klassSymbolTable.define(jackTokenizer.identifier(), symbolType, symbolKind);
            jackTokenizer.advance();
            switch (jackTokenizer.symbol()) {
                case ',':
                    break;
                case ';':
                    break whileLoop;
                default:
                    throw new RuntimeException("Either ',' or ';' expected but found: " + jackTokenizer.symbol());
            }
        }
    }

    private void compileSubroutine() {
        assert jackTokenizer.keyword() == Keyword.CONSTRUCTOR ||
                jackTokenizer.keyword() == Keyword.METHOD ||
                jackTokenizer.keyword() == Keyword.FUNCTION;

        subroutineSymbolTable.reset();
        if (jackTokenizer.keyword() == Keyword.METHOD) {
            subroutineSymbolTable.define("this", currentKlassName, ARG);
            isMethod = true;
        } else {
            isMethod = false;
        }

        jackTokenizer.advance();
        switch (jackTokenizer.tokenType()) {
            case KEYWORD,
                 IDENTIFIER:
                break;
            default:
                throw new RuntimeException("Either keyword or identifier tokenType expected but found: " + jackTokenizer.tokenType());
        }
        jackTokenizer.advance();
        currentFunctionName = jackTokenizer.identifier();
        jackTokenizer.advance();

        assert jackTokenizer.symbol() == '(';
        jackTokenizer.advance();
        compileParameterList();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ')';
        jackTokenizer.advance();
        compileSubroutineBody();
    }

    private void compileParameterList() {
        while (jackTokenizer.hasMoreTokens()) {
            if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.symbol() == ')') {
                jackTokenizer.retreat();
                break;
            }

            String symbolType = switch (jackTokenizer.tokenType()) {
                case KEYWORD:
                    yield jackTokenizer.keyword().toString();
                case IDENTIFIER:
                    yield jackTokenizer.identifier();
                default:
                    throw new RuntimeException("ParameterList types should not be " + jackTokenizer.tokenType());
            };
            jackTokenizer.advance();
            subroutineSymbolTable.define(jackTokenizer.identifier(), symbolType, ARG);
            jackTokenizer.advance();
            if (jackTokenizer.symbol() == ',') {
                jackTokenizer.advance();
            }
        }
    }

    private void compileSubroutineBody() {
        assert jackTokenizer.symbol() == '{';

        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == TokenType.KEYWORD && jackTokenizer.keyword() == Keyword.VAR) {
                compileVarDec();
            } else {
                vmWriter.writeFunction(currentKlassName + "." + currentFunctionName, subroutineSymbolTable.varCount(VAR));
                if (isMethod) {
                    vmWriter.writePush(ARGUMENT, subroutineSymbolTable.indexOf("this"));
                    vmWriter.writePop(POINTER, 0);
                }
                compileStatements();
                jackTokenizer.advance();
                break;
            }
        }
        assert jackTokenizer.symbol() == '}';
    }

    private void compileVarDec() {
        assert jackTokenizer.keyword() == Keyword.VAR;
        jackTokenizer.advance();

        String symbolType = switch (jackTokenizer.tokenType()) {
            case KEYWORD:
                yield jackTokenizer.keyword().toString();
            case IDENTIFIER:
                yield jackTokenizer.identifier();
            default:
                throw new RuntimeException("ParameterList types should not be " + jackTokenizer.tokenType());
        };

        whileLoop:
        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            subroutineSymbolTable.define(jackTokenizer.identifier(), symbolType, VAR);
            jackTokenizer.advance();
            switch (jackTokenizer.symbol()) {
                case ',':
                    break;
                case ';':
                    break whileLoop;
                default:
                    throw new RuntimeException("Either ',' or ';' expected but found: " + jackTokenizer.symbol());
            }
        }
    }

    /**
     * Called only if the previous token is '{'.
     */
    private void compileStatements() {
//        printWriter.println("<statements>");
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
//        printWriter.println("</statements>");
    }

    /**
     * let statement := `let`, `varName`, =, `expression`, and `;`
     */
    private void compileLet() {
//        printWriter.println("<letStatement>");
        assert jackTokenizer.keyword() == Keyword.LET;
//        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
        jackTokenizer.advance();
//        printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
        jackTokenizer.advance();
        if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.symbol() == '[') {
//            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
            jackTokenizer.advance();
            compileExpression();
            jackTokenizer.advance();
            assert jackTokenizer.symbol() == ']';
//            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
            jackTokenizer.advance();
        }

        assert jackTokenizer.symbol() == '=';
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileExpression();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ';';
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
//        printWriter.println("</letStatement>");
    }


    /**
     * if statement := `if`, `(expression)`, `{statements}`
     */
    private void compileIf() {
//        printWriter.println("<ifStatement>");

        assert jackTokenizer.keyword() == Keyword.IF;
//        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == '(';
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileExpression();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ')';
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == '{';
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileStatements();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == '}';
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");

        if (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == TokenType.KEYWORD && jackTokenizer.keyword() == Keyword.ELSE) {
//                printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
                jackTokenizer.advance();
                assert jackTokenizer.symbol() == '{';
//                printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                jackTokenizer.advance();
                compileStatements();
                jackTokenizer.advance();
                assert jackTokenizer.symbol() == '}';
//                printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
            } else {
                jackTokenizer.retreat();
            }
        }

//        printWriter.println("</ifStatement>");
    }

    /**
     * while statement := `while`, `(expression)`, `{statements}`
     */
    private void compileWhile() {
//        printWriter.println("<whileStatement>");

        assert jackTokenizer.keyword() == Keyword.WHILE;
//        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
        jackTokenizer.advance();

        assert jackTokenizer.symbol() == '(';
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileExpression();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ')';
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();

        assert jackTokenizer.symbol() == '{';
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileStatements();
        jackTokenizer.advance();

        assert jackTokenizer.symbol() == '}';
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");

//        printWriter.println("</whileStatement>");
    }

    private void compileDo() {
//        printWriter.println("<doStatement>");
        assert jackTokenizer.keyword() == Keyword.DO;
//        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
        whileLoop:
        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == TokenType.SYMBOL) {
                switch (jackTokenizer.symbol()) {
                    case '(':
                        break whileLoop;
                    case '.':
//                        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                        break;
                    default:
                        throw new RuntimeException("Unexpected symbol: " + jackTokenizer.symbol());
                }
            } else {
//                printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            }
        }
        assert jackTokenizer.symbol() == '(';
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        compileExpressionList();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ')';
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ';';
//        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
//        printWriter.println("</doStatement>");
    }

    private void compileReturn() {
        assert jackTokenizer.keyword() == Keyword.RETURN;
        jackTokenizer.advance();
        if (jackTokenizer.tokenType() != TokenType.SYMBOL || jackTokenizer.symbol() != ';') {
            compileExpression();
            jackTokenizer.advance();
        } else {
            vmWriter.writePush(CONSTANT, 0);
        }
        assert jackTokenizer.symbol() == ';';

        vmWriter.writeReturn();
    }

    /**
     * By definition, expression is either a term or (term op term).
     * op: operation such as +, -, *, /, =, >, <, &, |
     * Inferred: `<term>` only exists between `<expression>` and `</expression>`.
     */
    private void compileExpression() {
//        printWriter.println("<expression>");
        compileTerm();
        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            if (
                    jackTokenizer.tokenType() == TokenType.SYMBOL &&
                            TokenType.isOperationSymbol(jackTokenizer.symbol())
            ) {
                switch (jackTokenizer.symbol()) {
                    case '>':
//                        printWriter.println("<symbol> &gt; </symbol>");
                        break;
                    case '<':
//                        printWriter.println("<symbol> &lt; </symbol>");
                        break;
                    case '&':
//                        printWriter.println("<symbol> &amp; </symbol>");
                        break;
                    default:
//                        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                        break;
                }
                jackTokenizer.advance();
                compileTerm();
            } else {
                jackTokenizer.retreat();
                break;
            }
        }
//        printWriter.println("</expression>");
    }

    /**
     * By definition, term is either varName or constant.
     * varName: a string not beginning with a digit
     * constant: a non-negative integer
     */
    private void compileTerm() {
//        printWriter.println("<term>");
        switch (jackTokenizer.tokenType()) {
            case IDENTIFIER:
//                printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
                jackTokenizer.advance();

                if (jackTokenizer.tokenType() == TokenType.SYMBOL) {
                    switch (jackTokenizer.symbol()) {
                        case '[':
//                            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                            jackTokenizer.advance();
                            compileExpression();
                            jackTokenizer.advance();
                            assert jackTokenizer.symbol() == ']';
//                            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                            break;
                        case '.':
//                            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                            jackTokenizer.advance();
//                            printWriter.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
                            jackTokenizer.advance();
                            assert jackTokenizer.symbol() == '(';
//                            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                            jackTokenizer.advance();
                            compileExpressionList();
                            jackTokenizer.advance();
                            assert jackTokenizer.symbol() == ')';
//                            printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
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
//                        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                        jackTokenizer.advance();
                        compileExpression();
                        jackTokenizer.advance();
                        assert jackTokenizer.symbol() == ')';
//                        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
                        break;
                    case '-',
                         '~':
//                        printWriter.println("<symbol> " + jackTokenizer.symbol() + " </symbol>");
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
//                        printWriter.println("<keyword> " + jackTokenizer.keyword() + " </keyword>");
                        break;
                    default:
                        throw new RuntimeException("Not an eligible keyword in a term: " + jackTokenizer.keyword());
                }
                break;
            case INT_CONST:
//                printWriter.println("<integerConstant> " + jackTokenizer.intVal() + " </integerConstant>");
                break;
            case STRING_CONST:
//                printWriter.println("<stringConstant> " + jackTokenizer.stringVal() + " </stringConstant>");
                break;
            default:
                throw new RuntimeException("Not a term");
        }
//        printWriter.println("</term>");
    }

    /**
     * Implemented to return int value according to the textbook's requirements even though it is never used.
     */
    private int compileExpressionList() {
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
                        expressionCount++;
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
        return expressionCount;
    }

    /**
     * For testing purposes
     */
    SymbolTable getSymbolTable(SymbolTableLevel level) {
        return switch (level) {
            case KLASS:
                yield klassSymbolTable;
            case SUBROUTINE:
                yield subroutineSymbolTable;
        };
    }
}
