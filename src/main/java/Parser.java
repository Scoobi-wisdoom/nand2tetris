import java.io.FileInputStream;
import java.util.Objects;

public class Parser {
    private static final int C_ARITHMETIC = 0;
    private static final int C_PUSH = 1;
    private static final int C_POP = 2;
    private static final int C_LABEL = 3;
    private static final int C_GOTO = 4;
    private static final int C_IF = 5;
    private static final int C_FUNCTION = 6;
    private static final int C_RETURN = 7;
    private static final int C_CALL = 8;
    private String currentCommand;
    private String arg1;
    private String arg2;
    private FileInputStream fileInputStream;

    public Parser(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    public boolean hasMoreLines() {
        return false;
    }

    public void advance() {
        if (hasMoreLines()) {
            currentCommand = "";
        } else {
            throw new RuntimeException("there is no next line.");
        }
    }

    public int commandType() {
        if (Objects.equals(currentCommand, "C_ARITHMETIC")) {
            return C_ARITHMETIC;
        } else if (Objects.equals(currentCommand, "C_PUSH")) {
            return C_PUSH;
        } else if (Objects.equals(currentCommand, "C_POP")) {
            return C_POP;
        } else if (Objects.equals(currentCommand, "C_LABEL")) {
            return C_LABEL;
        } else if (Objects.equals(currentCommand, "C_GOTO")) {
            return C_GOTO;
        } else if (Objects.equals(currentCommand, "C_IF")) {
            return C_IF;
        } else if (Objects.equals(currentCommand, "C_FUNCTION")) {
            return C_FUNCTION;
        } else if (Objects.equals(currentCommand, "C_RETURN")) {
            return C_RETURN;
        } else if (Objects.equals(currentCommand, "C_CALL")) {
            return C_CALL;
        } else {
            throw new RuntimeException("Wrong currentCommand: " + currentCommand);
        }
    }

    public String arg1() {
        if (commandType() != C_RETURN) {
            return arg1;
        } else {
            throw new RuntimeException("arg1() should never be called");
        }
    }

    public String arg2() {
        if (commandType() == C_PUSH ||
                commandType() == C_POP ||
                commandType() == C_FUNCTION ||
                commandType() == C_CALL
        ) {
            return arg2;
        } else {
            throw new RuntimeException("arg2() should never be called");
        }
    }
}
