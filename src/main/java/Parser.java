public class Parser {
    public String parse(String command) {
        String trimmedCommand = command.trim();

        if (trimmedCommand.startsWith("//") ||
                trimmedCommand.isBlank()
        ) {
            return "";
        }

        return trimmedCommand;
    }
}
