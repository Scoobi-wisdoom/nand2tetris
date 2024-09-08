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
                    popFromAddressOf("@ARG", index);
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push argument " + index);
                    pushToAddressOf("@ARG", index);
                }
                break;
            }
            case "local": {
                if (command == Parser.C_POP) {
                    printWriter.println("// pop local " + index);
                    popFromAddressOf("@LCL", index);
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push local " + index);
                    pushToAddressOf("@LCL", index);
                }
                break;
            }
            case "this": {
                if (command == Parser.C_POP) {
                    printWriter.println("// pop this " + index);
                    popFromAddressOf("@THIS", index);
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push this " + index);
                    pushToAddressOf("@THIS", index);
                }
                break;
            }
            case "that": {
                if (command == Parser.C_POP) {
                    printWriter.println("// pop that " + index);
                    popFromAddressOf("@THAT", index);
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push that " + index);
                    pushToAddressOf("@THAT", index);
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
                    popFromAddressOf(thisOrThat);
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push pointer " + index);
                    pushToAddressOf(thisOrThat);
                }
                break;
            }
            case "temp": {
                int tempAddressIndex = index + 5;
                if (index < 5 || index > 12)
                    throw new RuntimeException("temp segment should be between @R5 and @R12, inclusively.");
                if (command == Parser.C_POP) {
                    printWriter.println("// pop temp " + index);
                    popFromAddressOf("@R" + tempAddressIndex);
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push temp " + index);
                    pushToAddressOf("@R" + tempAddressIndex);
                }
                break;
            }
            case "static": {
                if (fileName == null) throw new RuntimeException("fileName should not be null.");
                if (command == Parser.C_POP) {
                    printWriter.println("// pop static " + index);
                    popFromAddressOf("@" + fileName + "." + index);
                }
                if (command == Parser.C_PUSH) {
                    printWriter.println("// push static " + index);
                    pushToAddressOf("@" + fileName + "." + index);
                }
                break;
            }
        }
    }

    private void popFromAddressOf(String segmentBaseAddress, int index) {
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
            popFromAddressOf(segmentBaseAddress);
        }
    }

    private void popFromAddressOf(String address) {
        printWriter.println("@SP");
        printWriter.println("AM=M-1");
        printWriter.println("D=M");
        printWriter.println(address);
        printWriter.println("A=M");
        printWriter.println("M=D");
    }

    private void pushToAddressOf(String segmentBaseAddress, int index) {
        if (index != 0) {
            printWriter.println("@" + index);
            printWriter.println("D=A");
            printWriter.println(segmentBaseAddress);
            printWriter.println("A=D+A");
            printWriter.println("D=M");
            printWriter.println("@SP");
            printWriter.println("A=M");
            printWriter.println("M=D");
            printWriter.println("@SP");
            printWriter.println("M=M+1");
        } else {
            pushToAddressOf(segmentBaseAddress);
        }
    }

    private void pushToAddressOf(String address) {
        printWriter.println(address);
        printWriter.println("D=M");
        printWriter.println("@SP");
        printWriter.println("A=M");
        printWriter.println("M=D");
        printWriter.println("@SP");
        printWriter.println("M=M+1");
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
