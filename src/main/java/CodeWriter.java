import java.io.FileOutputStream;
import java.io.PrintWriter;

public class CodeWriter {
    private final PrintWriter printWriter;
    private int labelCount = 0;
    private static final String[] segments = new String[]{
            "constant",
            "argument",
            "local",
            "this",
            "that",
            "temp",
            "pointer",
            "static",
    };

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
        labelCount++;
    }

    public void writeArithmetic(String command) {
        // C-Instruction 이 특별하다는 것을 인지해야 풀 수 있는 문제다. 다음과 같은 항목이 특이하다.
        // 1. label 이 사용된다.
        // 2. label 의 이름은 high level 변수 짓듯 할 수 있지 않고, index 를 부여해 구분해야 한다.
        if (command.equals("eq")) {
            printWriter.println("// eq");
            useTwoStacksWithCInstruction("D;JEQ");
        } else if (command.equals("gt")) {
            printWriter.println("// gt");
            useTwoStacksWithCInstruction("D;JGT");
        } else if (command.equals("lt")) {
            printWriter.println("// lt");
            useTwoStacksWithCInstruction("D;JLT");
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
        validate(command, segment);

        // https://github.com/Scoobi-wisdoom/nand2tetris?tab=readme-ov-file#week-1-1
        if (segment.equals("constant")) {

        } else if (segment.equals("argument")) {
            if (command == Parser.C_PUSH) {
                printWriter.println("// push " + segment + " " + index);
                printWriter.println("@" + index);
                printWriter.println("D=A");
                printWriter.println("@ARG");
                printWriter.println("A=D+A");
                printWriter.println("D=M");
                printWriter.println("@SP");
                printWriter.println("A=M");
                printWriter.println("M=D");
                printWriter.println("@SP");
                printWriter.println("M=M+1");
            } else {
                printWriter.println("// pop " + segment + " " + index);
                printWriter.println("@" + index);
                printWriter.println("D=A");
                printWriter.println("@ARG");
                printWriter.println("D=D+A");
                printWriter.println("@R13");
                printWriter.println("M=D");
                printWriter.println("@SP");
                printWriter.println("AM=M-1");
                printWriter.println("D=M");
                printWriter.println("@R13");
                printWriter.println("A=M");
                printWriter.println("M=D");
            }
        } else if (segment.equals("local")) {
            if (command == Parser.C_PUSH) {
            } else {

            }
        } else if (segment.equals("this")) {
            if (command == Parser.C_PUSH) {
            } else {

            }
        } else if (segment.equals("that")) {
            if (command == Parser.C_PUSH) {
            } else {

            }
        } else if (segment.equals("temp")) {
            if (command == Parser.C_PUSH) {
            } else {

            }
        } else if (segment.equals("pointer")) {
            if (command == Parser.C_PUSH) {
            } else {

            }
        } else if (segment.equals("static")) {
            if (command == Parser.C_PUSH) {
            } else {

            }
        }
    }

    private void validate(int command, String segment) {
        if (command != Parser.C_PUSH && command != Parser.C_POP) {
            throw new RuntimeException("Command" + command + "is not push or pop.");
        }

        boolean isValidSegment = false;
        for (String s : segments) {
            if (s.equals(segment)) {
                isValidSegment = true;
                break;
            }
        }
        if (!isValidSegment) {
            throw new RuntimeException("Segment " + segment + " is not valid.");
        }
    }

    public void close() {
        printWriter.close();
    }
}
