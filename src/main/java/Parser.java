import java.util.List;

public class Parser {
    public String parse(List<String> commands) {
        String joinedCommand =  commands.stream()
                .map(Parser::parse)
                .reduce((s1, s2) -> {
                    if (s1.isBlank() && s2.isBlank()) {
                        return "";
                    } else {
                        return s1 + "\n" + s2;
                    }
                })
                .orElse("");

        return joinedCommand.trim();
    }

    private static String parse(String command) {
        String trimmedCommand = command.trim();

        if (trimmedCommand.startsWith("//") ||
                trimmedCommand.isBlank()
        ) {
            return "";
        }

        return trimmedCommand;
    }
}
