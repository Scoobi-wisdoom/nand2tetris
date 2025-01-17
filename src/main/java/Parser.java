import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        if (!cumulativeString.isEmpty()) {
            tokens.add(cumulativeString.toString());
        }

        return tokens;
    }

    public static String removeComments(InputStream inputStream) {
        StringBuilder sourceWithoutComment = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {
            boolean isInsideAsteriskComment = false;

            String line;
            while ((line = reader.readLine()) != null) {
                String lineWithoutComment = "";
                line = line.trim();
                for (int i = 0; i < line.length(); i++) {
                    if (i + 1 >= line.length()) break;
                    char currentChar = line.charAt(i);
                    char nextChar = line.charAt(i + 1);

                    if (isInsideAsteriskComment) {
                        if (currentChar == '*' && nextChar == '/') {
                            isInsideAsteriskComment = false;
                        }
                    } else {
                        if (currentChar == '/' && nextChar == '*') {
                            isInsideAsteriskComment = true;
                            lineWithoutComment = line.substring(0, i);
                        } else if (currentChar == '/' && nextChar == '/') {
                            lineWithoutComment = line.substring(0, i);
                            break;
                        } else {
                            lineWithoutComment = line;
                        }
                    }
                }
                sourceWithoutComment.append(lineWithoutComment).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading a file", e);
        }
        return sourceWithoutComment.toString();
    }
}
