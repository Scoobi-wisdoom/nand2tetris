import java.io.FileOutputStream;
import java.io.IOException;

public class CodeWriter {
    private FileOutputStream fileOutputStream;

    public CodeWriter(FileOutputStream fileOutputStream) {
        this.fileOutputStream = fileOutputStream;
    }

    public void writeArithmetic(String command) {

    }

    private void writeToOutputStream(String data) {
        try {
            fileOutputStream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePushPop(String command, String segment, int index) {

    }

    public void close() {
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}