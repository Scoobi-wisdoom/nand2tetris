import java.io.FileOutputStream;
import java.io.PrintWriter;

public class CodeWriter {
    private PrintWriter printWriter;
    private int labelCount = 0;

    public CodeWriter(FileOutputStream fileOutputStream) {
        this.printWriter = new PrintWriter(fileOutputStream);
    }

    private void useTwoStacksWithCInstruction(String cInstruction) {
        printWriter.println("@SP");
        printWriter.println("AM=M-1");
        printWriter.println("D=M");
        printWriter.println("A=A-1");
        printWriter.println("M=D-M");
        printWriter.println("@TRUE" + labelCount);
        printWriter.println(cInstruction);
        printWriter.println("@SP");
        printWriter.println("A=M-1");
        // https://www.quora.com/A-memory-byte-is-never-empty-but-its-initial-content-may-be-meaningless-to-your-program-What-does-this-mean
        // https://softwareengineering.stackexchange.com/questions/53272/the-default-state-of-unused-memory
        printWriter.println("M=-1");
        printWriter.println("@END" + labelCount);
        printWriter.println("0;JMP");
        printWriter.println("(TRUE" + labelCount + ")");
        printWriter.println("@SP");
        printWriter.println("A=M-1");
        printWriter.println("M=1");
        printWriter.println("(END" + labelCount + ")");
    }

    public void writeArithmetic(String command) {
        // C-Instruction 이 특별하다는 것을 인지해야 풀 수 있는 문제다. 다음과 같은 항목이 특이하다.
        // 1. label 이 사용된다.
        // 2. label 의 이름은 high level 변수 짓듯 할 수 있지 않고, index 를 부여해 구분해야 한다.
        if (command.equals("eq")) {
            printWriter.println("// eq");
            useTwoStacksWithCInstruction("D;JEQ");
            labelCount++;
        } else if (command.equals("gt")) {
            printWriter.println("// gt");
            useTwoStacksWithCInstruction("D;JGT");
            labelCount++;
        } else if (command.equals("lt")) {
            printWriter.println("// lt");
            useTwoStacksWithCInstruction("D;JLT");
            labelCount++;
        } else if (command.equals("add")) {
            printWriter.println("// add");
            printWriter.println("@SP");
            printWriter.println("AM=M-1");
            printWriter.println("D=M");
            printWriter.println("A=A-1");
            printWriter.println("M=D+M");
        } else if (command.equals("sub")) {
            printWriter.println("// sub");
            printWriter.println("@SP");
            printWriter.println("AM=M-1");
            printWriter.println("D=M");
            printWriter.println("A=A-1");
            printWriter.println("M=M-D");
        } else if (command.equals("and")) {
            printWriter.println("// and");
            printWriter.println("@SP");
            printWriter.println("AM=M-1");
            printWriter.println("D=M");
            printWriter.println("A=A-1");
            printWriter.println("M=D&M");
        } else if (command.equals("or")) {
            printWriter.println("// or");
            printWriter.println("@SP");
            printWriter.println("AM=M-1");
            printWriter.println("D=M");
            printWriter.println("A=A-1");
            printWriter.println("M=D|M");
        } else if (command.equals("neg")) {
            printWriter.println("// neg");
            printWriter.println("@SP");
            printWriter.println("A=M-1");
            printWriter.println("M=-M");
        } else if (command.equals("not")) {
            printWriter.println("// not");
            printWriter.println("@SP");
            printWriter.println("A=M-1");
            printWriter.println("M=!M");
        }
    }

    public void writePushPop(int command, String segment, int index) {
        // TODO("Not yet implemented.")
        if (command == Parser.C_PUSH) {

        }
        // TODO("Not yet implemented.")
        else if (command == Parser.C_POP) {

        } else {
            throw new RuntimeException("Command" + command + "is not push or pop.");
        }
    }

    public void close() {
        if (printWriter != null) {
            printWriter.close();
        }
    }
}
