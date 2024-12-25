import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static List<String> getTokens(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder cumulativeString = new StringBuilder();
        boolean isCursorInQuotation = false;

        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);

            if (isCursorInQuotation) {
                cumulativeString.append(currentChar);
                if (currentChar == '"') {
                    tokens.add(cumulativeString.toString());
                    cumulativeString.setLength(0);
                    isCursorInQuotation = false;
                }
            } else {
                if (currentChar == '"') {
                    if (!cumulativeString.isEmpty()) {
                        tokens.add(cumulativeString.toString());
                        cumulativeString.setLength(0);
                    }

                    cumulativeString.append(currentChar);
                    isCursorInQuotation = true;
                } else if (TokenType.isSymbol(currentChar)) {
                    if (!cumulativeString.isEmpty()) {
                        tokens.add(cumulativeString.toString());
                        cumulativeString.setLength(0);
                    }
                    tokens.add(String.valueOf(currentChar));
                } else if (Character.isWhitespace(currentChar)) {
                    if (!cumulativeString.isEmpty()) {
                        tokens.add(cumulativeString.toString());
                        cumulativeString.setLength(0);
                    }
                } else {
                    cumulativeString.append(currentChar);
                }
            }
        }
        return tokens;
    }
}
