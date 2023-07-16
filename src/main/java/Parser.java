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
        return "";
    }

    public String arg1() {
        return "";
    }

    public String arg2() {
        return "";
    }
}
