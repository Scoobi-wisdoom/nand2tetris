import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Parser {
    public static final int C_ARITHMETIC = 0;
    public static final int C_PUSH = 1;
    public static final int C_POP = 2;
    public static final int C_LABEL = 3;
    public static final int C_GOTO = 4;
    public static final int C_IF = 5;
    public static final int C_FUNCTION = 6;
    public static final int C_RETURN = 7;
    public static final int C_CALL = 8;
    private static final ArrayList<String> arithmeticCommands = new ArrayList<>(9);

    static {
        arithmeticCommands.add("add");
        arithmeticCommands.add("sub");
        arithmeticCommands.add("neg");
        arithmeticCommands.add("eq");
        arithmeticCommands.add("gt");
        arithmeticCommands.add("lt");
        arithmeticCommands.add("and");
        arithmeticCommands.add("or");
        arithmeticCommands.add("not");
    }

    private String currentCommand;
    private final Scanner commands;

    public Parser(InputStream inputStream) {
        this.commands = new Scanner(inputStream);
    }

    public boolean hasMoreLines() {
        return commands.hasNextLine();
    }

    public void advance() {
        if (hasMoreLines()) {
            currentCommand = commands.nextLine();
            int commentIndex = currentCommand.indexOf("//");
            if (commentIndex != -1) {
                currentCommand = currentCommand.substring(0, commentIndex);
            }
        }

        currentCommand = currentCommand.trim();
        if (currentCommand.isEmpty()) {
            advance();
        }
    }

    public int commandType() {
        String[] currentCommandSplits = currentCommand.split(" ");
        if (arithmeticCommands.contains(currentCommandSplits[0])) {
            return C_ARITHMETIC;
        } else if (Objects.equals(currentCommandSplits[0], "push")) {
            return C_PUSH;
        } else if (Objects.equals(currentCommandSplits[0], "pop")) {
            return C_POP;
        }
        else if (Objects.equals(currentCommandSplits[0], "C_LABEL")) {
            return C_LABEL;
        }
        else if (Objects.equals(currentCommandSplits[0], "C_GOTO")) {
            return C_GOTO;
        }
        else if (Objects.equals(currentCommandSplits[0], "C_IF")) {
            return C_IF;
        }
        else if (Objects.equals(currentCommandSplits[0], "C_FUNCTION")) {
            return C_FUNCTION;
        }
        else if (Objects.equals(currentCommandSplits[0], "C_RETURN")) {
            return C_RETURN;
        }
        else if (Objects.equals(currentCommandSplits[0], "C_CALL")) {
            return C_CALL;
        } else {
            throw new RuntimeException("Wrong currentCommand: " + currentCommand);
        }
    }

    public String arg1() {
        if (commandType() == C_RETURN) {
            throw new RuntimeException("CommandType" + commandType() + "should never call arg1().");
        } else if (commandType() == C_ARITHMETIC) {
            return currentCommand;
        } else {
            return currentCommand.split(" ")[1];
        }
    }

    public int arg2() {
        if (commandType() == C_PUSH ||
                commandType() == C_POP ||
                commandType() == C_FUNCTION ||
                commandType() == C_CALL
        ) {
            return Integer.parseInt(currentCommand.split(" ")[2]);
        } else {
            throw new RuntimeException("CommandType" + commandType() + "should never call arg2().");
        }
    }
}
