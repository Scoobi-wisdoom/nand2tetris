import java.io.FileInputStream;

public class Parser {
    private FileInputStream fileInputStream;

    public Parser(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    public boolean hasMoreLines() {
        return false;
    }

    public void advance() {

    }

    public String commandType() {
        // C_ARITHMETIC
        // C_PUSH
        // C_POP
        // C_LABEL
        // C_GOTO
        // C_IF
        // C_FUNCTION
        // C_RETURN
        // C_CALL
        return "";
    }

    public String arg1() {
        return "";
    }

    public String arg2() {
        return "";
    }
}
