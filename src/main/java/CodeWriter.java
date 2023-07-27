import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeWriter {
    private FileOutputStream fileOutputStream;
    private int methodCallCounter = 1;

    private final Map<String, String> commandToJump = new HashMap<>() {{
        put(EQ, "JEQ");
        put(GT, "JGT");
        put(LT, "JLT");
    }};

    public CodeWriter(FileOutputStream fileOutputStream) {
        this.fileOutputStream = fileOutputStream;
    }

    public void writeArithmetic(String command) {
        List<String> assemblyCommands = new ArrayList<>();
        assemblyCommands.add("");

        if (command.equals("add")) {
            doAdd(assemblyCommands);
        } else if (command.equals("sub")) {
            doSub(assemblyCommands);
        } else if (command.equals("neg")) {
            doNeg(assemblyCommands);
        } else if (command.equals("and")) {
            doAnd(assemblyCommands);
        } else if (command.equals("or")) {
            doOr(assemblyCommands);
        } else if (command.equals("not")) {
            doNot(assemblyCommands);
        } else if (command.equals(EQ) ||
                command.equals(GT) ||
                command.equals(LT)
        ) {
            if (command.equals(EQ)) {
                assemblyCommands.add("// eq");
            } else if (command.equals(GT)) {
                assemblyCommands.add("// gt");
            } else if (command.equals(LT)) {
                assemblyCommands.add("// lt");
            }
            getPopToDRegister(assemblyCommands);
            getPeekToARegister(assemblyCommands);
            assemblyCommands.add("D=M-D");
            assemblyCommands.add("@IF" + methodCallCounter);
            assemblyCommands.add("D;" + commandToJump.get(command));

            assemblyCommands.add("@END" + methodCallCounter);
            assemblyCommands.add("M=0");
            assemblyCommands.add("0;JMP");

            assemblyCommands.add("(IF" + methodCallCounter + ")");
            assemblyCommands.add("M=1");

            assemblyCommands.add("(END" + methodCallCounter + ")");
        }

        methodCallCounter++;

        String joinedCommand = String.join(System.lineSeparator(), assemblyCommands);
        writeToOutputStream(joinedCommand);
    }

    private void doAdd(List<String> assemblyCommands) {
        assemblyCommands.add("// add");
        getPopToDRegister(assemblyCommands);
        getPeekToARegister(assemblyCommands);
        assemblyCommands.add("M=D+M");
    }

    private void getPopToDRegister(List<String> assemblyCommands) {
        assemblyCommands.add(STACK_POINTER);
        assemblyCommands.add("AM=M-1");
        assemblyCommands.add("D=M");
    }

    private void getPeekToARegister(List<String> assemblyCommands) {
        assemblyCommands.add(STACK_POINTER);
        assemblyCommands.add("A=M-1");
    }

    private void doSub(List<String> assemblyCommands) {
        assemblyCommands.add("// sub");
        getPopToDRegister(assemblyCommands);
        getPeekToARegister(assemblyCommands);
        assemblyCommands.add("M=M-D");
    }

    private void doNeg(List<String> assemblyCommands) {
        assemblyCommands.add("// neg");
        getPeekToARegister(assemblyCommands);
        assemblyCommands.add("M=-M");
    }

    private void doAnd(List<String> assemblyCommands) {
        assemblyCommands.add("// and");
        getPopToDRegister(assemblyCommands);
        getPeekToARegister(assemblyCommands);
        assemblyCommands.add("M=D&M");
    }

    private void doOr(List<String> assemblyCommands) {
        assemblyCommands.add("// or");
        getPopToDRegister(assemblyCommands);
        getPeekToARegister(assemblyCommands);
        assemblyCommands.add("M=D|M");
    }

    private void doNot(List<String> assemblyCommands) {
        assemblyCommands.add("// not");
        getPeekToARegister(assemblyCommands);
        assemblyCommands.add("M=!M");
    }

    private final String STACK_POINTER = "@SP";

    private final String EQ = "eq";
    private final String LT = "lt";
    private final String GT = "gt";

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