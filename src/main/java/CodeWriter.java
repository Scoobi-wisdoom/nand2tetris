import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CodeWriter {
    private final PrintWriter printWriter;
    private final String fileName;
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

    public CodeWriter(File file) {
        try {
            this.printWriter = new PrintWriter(new FileWriter(file));
            this.fileName = file.getName();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void useTwoStacksWithCInstruction(String cInstruction) {
        printWriter.println("@SP");
        printWriter.println("AM=M-1");
        printWriter.println("D=M");
        printWriter.println("@SP");
        printWriter.println("A=M-1");
        printWriter.println("D=M-D");
        printWriter.println("@TRUE" + labelCount);
        printWriter.println(cInstruction);
        // https://www.quora.com/A-memory-byte-is-never-empty-but-its-initial-content-may-be-meaningless-to-your-program-What-does-this-mean
        // https://softwareengineering.stackexchange.com/questions/53272/the-default-state-of-unused-memory
        printWriter.println("@SP");
        printWriter.println("A=M-1");
        printWriter.println("M=0");
        printWriter.println("@END" + labelCount);
        printWriter.println("0;JMP");
        printWriter.println("(TRUE" + labelCount + ")");
        printWriter.println("@SP");
        printWriter.println("A=M-1");
        // true 가 1 이 아니라 -1 이라는 것을 알아야 풀 수 있는 문제.
        printWriter.println("M=-1");
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

        switch (segment) {
            case "constant": {
                if (command == Parser.C_POP)
                    throw new RuntimeException("constant segment should not be with pop command.");
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push constant " + index);
                    printWriter.println("@" + index);
                    printWriter.println("D=A");
                    printWriter.println("@SP");
                    printWriter.println("A=M");
                    printWriter.println("M=D");
                    printWriter.println("@SP");
                    printWriter.println("M=M+1");
                }
                break;
            }
            case "argument": {
                if (command == Parser.C_POP) {
                    printWriter.println("// pop argument " + index);
                    popToAddressOf("@ARG", index);
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push argument " + index);
                    pushFromAddressOf("@ARG", index);
                }
                break;
            }
            case "local": {
                if (command == Parser.C_POP) {
                    printWriter.println("// pop local " + index);
                    popToAddressOf("@LCL", index);
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push local " + index);
                    pushFromAddressOf("@LCL", index);
                }
                break;
            }
            case "this": {
                if (command == Parser.C_POP) {
                    printWriter.println("// pop this " + index);
                    popToAddressOf("@THIS", index);
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push this " + index);
                    pushFromAddressOf("@THIS", index);
                }
                break;
            }
            case "that": {
                if (command == Parser.C_POP) {
                    printWriter.println("// pop that " + index);
                    popToAddressOf("@THAT", index);
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push that " + index);
                    pushFromAddressOf("@THAT", index);
                }
                break;
            }
            case "pointer": {
                final String thisOrThat;
                if (index == 0) {
                    thisOrThat = "@THIS";
                } else if (index == 1) {
                    thisOrThat = "@THAT";
                } else {
                    throw new RuntimeException("pointer segment is only compatible with 0 or 1 index.");
                }

                if (command == Parser.C_POP) {
                    printWriter.println("// pop pointer " + index);
                    printWriter.println("@SP");
                    printWriter.println("AM=M-1");
                    printWriter.println("D=M");
                    printWriter.println(thisOrThat);
                    printWriter.println("M=D");
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push pointer " + index);
                    printWriter.println(thisOrThat);
                    printWriter.println("D=M");
                    printWriter.println("@SP");
                    printWriter.println("A=M");
                    printWriter.println("M=D");
                    printWriter.println("@SP");
                    printWriter.println("M=M+1");
                }
                break;
            }
            case "temp": {
                int tempAddressIndex = index + 5;
                if (index < 5 || index > 12)
                    throw new RuntimeException("temp segment should be between @R5 and @R12, inclusively.");
                if (command == Parser.C_POP) {
                    printWriter.println("// pop temp " + index);
                    printWriter.println("@SP");
                    printWriter.println("AM=M-1");
                    printWriter.println("D=M");
                    printWriter.println("@R" + tempAddressIndex);
                    printWriter.println("M=D");
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push temp " + index);
                    printWriter.println("@R" + tempAddressIndex);
                    printWriter.println("D=M");
                    printWriter.println("@SP");
                    printWriter.println("A=M");
                    printWriter.println("M=D");
                    printWriter.println("@SP");
                    printWriter.println("M=M+1");
                }
                break;
            }
            // static 의 base 가 16 임을 캐치해야 풀 수 있는 문제.
            case "static": {
                if (fileName == null) throw new RuntimeException("fileName should not be null.");
                if (command == Parser.C_POP) {
                    printWriter.println("// pop static " + index);
                    printWriter.println("@" + (16 + index));
                    printWriter.println("D=A");
                    printWriter.println("@R13");
                    printWriter.println("M=D");
                    printWriter.println("@SP");
                    printWriter.println("AM=M-1");
                    printWriter.println("D=M");
                    printWriter.println("@R13");
                    printWriter.println("A=M");
                    printWriter.println("M=D");
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push static " + index);
                    printWriter.println("@" + (16 + index));
                    printWriter.println("D=M");
                    printWriter.println("@SP");
                    printWriter.println("A=M");
                    printWriter.println("M=D");
                    printWriter.println("@SP");
                    printWriter.println("M=M+1");
                }
                break;
            }
        }
    }

    private void popToAddressOf(String segmentBaseAddress, int index) {
        if (index != 0) {
            printWriter.println("@" + index);
            printWriter.println("D=A");
            printWriter.println(segmentBaseAddress);
            printWriter.println("D=D+M");
            printWriter.println("@R13");
            printWriter.println("M=D");
            printWriter.println("@SP");
            printWriter.println("AM=M-1");
            printWriter.println("D=M");
            printWriter.println("@R13");
            printWriter.println("A=M");
            printWriter.println("M=D");
        } else {
            printWriter.println("@SP");
            printWriter.println("AM=M-1");
            printWriter.println("D=M");
            printWriter.println(segmentBaseAddress);
            printWriter.println("A=M");
            printWriter.println("M=D");
        }
    }

    private void pushFromAddressOf(String segmentBaseAddress, int index) {
        if (index != 0) {
            printWriter.println("@" + index);
            printWriter.println("D=A");
            printWriter.println(segmentBaseAddress);
            printWriter.println("A=D+M");
            printWriter.println("D=M");
            printWriter.println("@SP");
            printWriter.println("A=M");
            printWriter.println("M=D");
            printWriter.println("@SP");
            printWriter.println("M=M+1");
        } else {
            printWriter.println(segmentBaseAddress);
            printWriter.println("A=M");
            printWriter.println("D=M");
            printWriter.println("@SP");
            printWriter.println("A=M");
            printWriter.println("M=D");
            printWriter.println("@SP");
            printWriter.println("M=M+1");
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
