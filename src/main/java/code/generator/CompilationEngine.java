package code.generator;

import syntax.analyzer.JackTokenizer;
import syntax.analyzer.Keyword;
import syntax.analyzer.TokenType;

import java.io.InputStream;
import java.io.OutputStream;

import static code.generator.ArithmeticCommand.ADD;
import static code.generator.ArithmeticCommand.NEG;
import static code.generator.ArithmeticCommand.NOT;
import static code.generator.MemorySegment.ARGUMENT;
import static code.generator.MemorySegment.CONSTANT;
import static code.generator.MemorySegment.POINTER;
import static code.generator.MemorySegment.TEMP;
import static code.generator.MemorySegment.THAT;
import static code.generator.SubroutineType.METHOD;
import static code.generator.SymbolKind.ARG;
import static code.generator.SymbolKind.FIELD;
import static code.generator.SymbolKind.NONE;
import static code.generator.SymbolKind.VAR;

public class CompilationEngine {
    private final JackTokenizer jackTokenizer;
    private final VMWriter vmWriter;
    private final SymbolTablePair symbolTablePair;
    private String currentKlassName = "";
    private String currentFunctionName = "";
    private SubroutineType declaredSubroutineType;
    private long labelIfCount = 0;
    private long labelWhileCount = 0;

    public CompilationEngine(InputStream inputStream, OutputStream outputStream) {
        this.jackTokenizer = new JackTokenizer(inputStream);
        this.vmWriter = new VMWriter(outputStream);
        this.symbolTablePair = new SymbolTablePair();
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
            symbolTablePair.getKlassSymbolTable().define(jackTokenizer.identifier(), symbolType, symbolKind);
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

        symbolTablePair.getSubroutineSymbolTable().reset();
        declaredSubroutineType = SubroutineType.from(jackTokenizer.keyword());
        if (declaredSubroutineType == METHOD) {
            symbolTablePair.getSubroutineSymbolTable().define("this", currentKlassName, ARG);
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
            symbolTablePair.getSubroutineSymbolTable().define(jackTokenizer.identifier(), symbolType, ARG);
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
                vmWriter.writeFunction(currentKlassName + "." + currentFunctionName, symbolTablePair.getSubroutineSymbolTable().varCount(VAR));
                switch (declaredSubroutineType) {
                    case METHOD:
                        vmWriter.writePush(ARGUMENT, symbolTablePair.getSubroutineSymbolTable().indexOf("this"));
                        vmWriter.writePop(POINTER, 0);
                        break;
                    case CONSTRUCTOR:
                        vmWriter.writePush(CONSTANT, symbolTablePair.getKlassSymbolTable().varCount(FIELD));
                        vmWriter.writeCall("Memory.alloc", 1);
                        vmWriter.writePop(POINTER, 0);
                        break;
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
            symbolTablePair.getSubroutineSymbolTable().define(jackTokenizer.identifier(), symbolType, VAR);
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
    }

    /**
     * let statement := `let`, `varName`, =, `expression`, and `;`
     */
    private void compileLet() {
        assert jackTokenizer.keyword() == Keyword.LET;
        jackTokenizer.advance();

        String assignee = jackTokenizer.identifier();

        jackTokenizer.advance();
        boolean isAssigneeArray = false;
        if (jackTokenizer.tokenType() == TokenType.SYMBOL && jackTokenizer.symbol() == '[') {
            isAssigneeArray = true;
            jackTokenizer.advance();
            compileExpression();
            jackTokenizer.advance();
            assert jackTokenizer.symbol() == ']';
            vmWriter.writePush(
                    symbolTablePair.getMemorySegment(assignee),
                    symbolTablePair.getIndex(assignee)
            );
            vmWriter.writeArithmetic(ADD);
            jackTokenizer.advance();
        }

        assert jackTokenizer.symbol() == '=';
        jackTokenizer.advance();
        compileExpression();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ';';

        if (isAssigneeArray) {
            vmWriter.writePop(TEMP, 0);
            vmWriter.writePop(POINTER, 1);
            vmWriter.writePush(TEMP, 0);
            vmWriter.writePop(THAT, 0);
        } else {
            vmWriter.writePop(
                    symbolTablePair.getMemorySegment(assignee),
                    symbolTablePair.getIndex(assignee)
            );
        }
    }


    /**
     * if statement := `if`, `(expression)`, `{statements}`
     */
    private void compileIf() {
        assert jackTokenizer.keyword() == Keyword.IF;
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == '(';
        jackTokenizer.advance();
        compileExpression();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ')';

        vmWriter.writeIf("IF_TRUE" + labelIfCount);
        vmWriter.writeGoTo("IF_FALSE" + labelIfCount);
        vmWriter.writeLabel("IF_TRUE" + labelIfCount);

        jackTokenizer.advance();
        assert jackTokenizer.symbol() == '{';
        jackTokenizer.advance();
        compileStatements();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == '}';


        if (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == TokenType.KEYWORD && jackTokenizer.keyword() == Keyword.ELSE) {
                vmWriter.writeGoTo("IF_END" + labelIfCount);
                vmWriter.writeLabel("IF_FALSE" + labelIfCount);

                jackTokenizer.advance();
                assert jackTokenizer.symbol() == '{';
                jackTokenizer.advance();
                compileStatements();
                jackTokenizer.advance();
                assert jackTokenizer.symbol() == '}';

                vmWriter.writeLabel("IF_END" + labelIfCount);
            } else {
                vmWriter.writeLabel("IF_FALSE" + labelIfCount);
                jackTokenizer.retreat();
            }
        }

        labelIfCount++;
    }

    /**
     * while statement := `while`, `(expression)`, `{statements}`
     */
    private void compileWhile() {
        assert jackTokenizer.keyword() == Keyword.WHILE;

        vmWriter.writeLabel("WHILE_EXP" + labelWhileCount);

        jackTokenizer.advance();
        assert jackTokenizer.symbol() == '(';
        jackTokenizer.advance();
        compileExpression();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ')';
        jackTokenizer.advance();

        vmWriter.writeArithmetic(NOT);
        vmWriter.writeIf("END_WHILE" + labelWhileCount);

        assert jackTokenizer.symbol() == '{';
        jackTokenizer.advance();
        compileStatements();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == '}';

        vmWriter.writeGoTo("WHILE_EXP" + labelWhileCount);
        vmWriter.writeLabel("END_WHILE" + labelWhileCount);

        labelWhileCount++;
    }

    private void compileDo() {
        assert jackTokenizer.keyword() == Keyword.DO;
        jackTokenizer.advance();
        compileCall();
        vmWriter.writePop(TEMP, 0);
        assert jackTokenizer.symbol() == ';';
    }

    private void compileCall() {
        StringBuilder calleeName = new StringBuilder(jackTokenizer.identifier());
        whileLoop:
        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == TokenType.SYMBOL) {
                switch (jackTokenizer.symbol()) {
                    case '(':
                        break whileLoop;
                    case '.':
                        calleeName.append(jackTokenizer.symbol());
                        break;
                    default:
                        throw new RuntimeException("Unexpected symbol: " + jackTokenizer.symbol());
                }
            } else {
                calleeName.append(jackTokenizer.identifier());
            }
        }
        assert jackTokenizer.symbol() == '(';
        jackTokenizer.advance();
        int expressionCount = compileExpressionList();
        jackTokenizer.advance();
        assert jackTokenizer.symbol() == ')';
        jackTokenizer.advance();

        int dotIndex = calleeName.indexOf(".");
        if (dotIndex == -1) {
            vmWriter.writeCall(currentKlassName + "." + calleeName, expressionCount + 1);
        } else {
            String receiver = calleeName.substring(0, dotIndex);
            if (symbolTablePair.getSubroutineSymbolTable().kindOf(receiver) != NONE) {
                vmWriter.writePush(
                        symbolTablePair.getMemorySegment(receiver),
                        symbolTablePair.getIndex(receiver)
                );
                vmWriter.writeCall(
                        symbolTablePair.getSubroutineSymbolTable().typeOf(receiver) + calleeName.substring(dotIndex),
                        expressionCount + 1
                );
            } else {
                vmWriter.writeCall(calleeName.toString(), expressionCount);
            }
        }
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
        compileTerm();
        while (jackTokenizer.hasMoreTokens()) {
            jackTokenizer.advance();
            if (
                    jackTokenizer.tokenType() == TokenType.SYMBOL &&
                            TokenType.isOperationSymbol(jackTokenizer.symbol())
            ) {
                char operationSymbol = jackTokenizer.symbol();
                jackTokenizer.advance();
                compileTerm();

                switch (operationSymbol) {
                    case '+',
                         '-',
                         '&',
                         '|',
                         '<',
                         '>',
                         '=':
                        ArithmeticCommand command = ArithmeticCommand.getCommand(operationSymbol);
                        vmWriter.writeArithmetic(command);
                        break;
                    case '*':
                        vmWriter.writeCall("Math.multiply", 2);
                        break;
                    case '/':
                        vmWriter.writeCall("Math.divide", 2);
                        break;
                    default:
                        throw new IllegalStateException("Not operation symbol: " + operationSymbol);
                }
            } else {
                jackTokenizer.retreat();
                break;
            }
        }
    }

    /**
     * By definition, term is either varName or constant.
     * varName: a string not beginning with a digit
     * constant: a non-negative integer
     */
    private void compileTerm() {
        switch (jackTokenizer.tokenType()) {
            case IDENTIFIER:
                String identifier = jackTokenizer.identifier();
                jackTokenizer.advance();
                if (jackTokenizer.tokenType() == TokenType.SYMBOL) {
                    switch (jackTokenizer.symbol()) {
                        case '[':
                            jackTokenizer.advance();
                            compileExpression();
                            jackTokenizer.advance();
                            assert jackTokenizer.symbol() == ']';
                            vmWriter.writePush(
                                    symbolTablePair.getMemorySegment(identifier),
                                    symbolTablePair.getIndex(identifier)
                            );
                            vmWriter.writeArithmetic(ADD);
                            vmWriter.writePop(POINTER, 1);
                            vmWriter.writePush(THAT, 0);
                            break;
                        case '.':
                            jackTokenizer.retreat();
                            compileCall();
                            jackTokenizer.retreat();
                            break;
                        default:
                            vmWriter.writePush(
                                    symbolTablePair.getMemorySegment(identifier),
                                    symbolTablePair.getIndex(identifier)
                            );
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
                        jackTokenizer.advance();
                        compileExpression();
                        jackTokenizer.advance();
                        assert jackTokenizer.symbol() == ')';
                        break;
                    case '-':
                        jackTokenizer.advance();
                        compileTerm();
                        vmWriter.writeArithmetic(NEG);
                        break;
                    case '~':
                        jackTokenizer.advance();
                        compileTerm();
                        vmWriter.writeArithmetic(NOT);
                        break;
                    default:
                        throw new RuntimeException("Not an eligible symbol in a term: " + jackTokenizer.symbol());
                }
                break;
            case KEYWORD:
                switch (jackTokenizer.keyword()) {
                    case TRUE:
                        vmWriter.writePush(CONSTANT, 0);
                        vmWriter.writeArithmetic(NOT);
                        break;
                    case FALSE,
                         NULL:
                        vmWriter.writePush(CONSTANT, 0);
                        break;
                    case THIS:
                        vmWriter.writePush(POINTER, 0);
                        break;
                    default:
                        throw new RuntimeException("Not an eligible keyword in a term: " + jackTokenizer.keyword());
                }
                break;
            case INT_CONST:
                vmWriter.writePush(CONSTANT, jackTokenizer.intVal());
                break;
            case STRING_CONST:
                vmWriter.writePush(CONSTANT, jackTokenizer.stringVal().length());
                vmWriter.writeCall("String.new", 1);
                for (int i = 0; i < jackTokenizer.stringVal().length(); i++) {
                    vmWriter.writePush(CONSTANT, jackTokenizer.stringVal().charAt(i));
                    vmWriter.writeCall("String.appendChar", 2);
                }
                break;
            default:
                throw new RuntimeException("Not a term");
        }
    }

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
                        if (expressionCount == 0) {
                            expressionCount++;
                        }
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
                yield symbolTablePair.getKlassSymbolTable();
            case SUBROUTINE:
                yield symbolTablePair.getSubroutineSymbolTable();
        };
    }
}
