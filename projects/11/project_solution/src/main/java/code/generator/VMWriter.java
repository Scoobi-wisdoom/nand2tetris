package code.generator;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class VMWriter {
    private final PrintWriter printWriter;

    public VMWriter(OutputStream outputStream) {
        this.printWriter = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
    }

    public void writePush(MemorySegment segment, int index) {
        printWriter.println("push " + segment + " " + index);
    }

    public void writePop(MemorySegment segment, int index) {
        printWriter.println("pop " + segment + " " + index);
    }

    public void writeArithmetic(ArithmeticCommand command) {
        printWriter.println(command);
    }

    public void writeLabel(String label) {
        printWriter.println("label " + label);
    }

    public void writeGoTo(String label) {
        printWriter.println("goto " + label);
    }

    public void writeIf(String label) {
        printWriter.println("if-goto " + label);
    }

    public void writeCall(String name, int nArgs) {
        printWriter.println("call " + name + " " + nArgs);
    }

    public void writeFunction(String name, int nVars) {
        printWriter.println("function " + name + " " + nVars);
    }

    public void writeReturn() {
        printWriter.println("return");
    }

    public void close() {
        printWriter.close();
    }
}
