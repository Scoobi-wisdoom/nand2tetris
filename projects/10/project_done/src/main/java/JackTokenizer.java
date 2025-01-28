import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class JackTokenizer {
    private final List<String> tokens;
    private int currentIndex;
    private String currentToken;

    public JackTokenizer(InputStream inputStream) {
        String commentRemovedInput = Parser.removeComments(inputStream);
        tokens = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(commentRemovedInput))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) tokens.addAll(Parser.getTokens(line.trim()));
            }
            currentIndex = -1;
            currentToken = null;
        } catch (IOException e) {
            throw new RuntimeException("Error while reading the file", e);
        }
    }

    public boolean hasMoreTokens() {
        return !tokens.isEmpty() && currentIndex + 1 < tokens.size();
    }

    public void advance() {
        if (!hasMoreTokens()) throw new RuntimeException("No token left.");
        currentIndex += 1;
        currentToken = tokens.get(currentIndex);
    }

    public void retreat() {
        if (currentIndex <= 0) throw new RuntimeException("No previous token exists.");
        currentIndex -= 1;
        currentToken = tokens.get(currentIndex);
    }

    public TokenType tokenType() {
        return TokenType.getTokenType(currentToken);
    }

    public Keyword keyword() {
        if (tokenType() != TokenType.KEYWORD)
            throw new RuntimeException("KEYWORD tokenType expected but token " + currentToken + " of type " + tokenType() + " found.");

        return Keyword.getKeyword(currentToken);
    }

    public char symbol() {
        if (tokenType() != TokenType.SYMBOL)
            throw new RuntimeException("SYMBOL tokenType expected but token " + currentToken + " of type " + tokenType() + " found.");
        return currentToken.charAt(0);
    }

    public String identifier() {
        if (tokenType() != TokenType.IDENTIFIER)
            throw new RuntimeException("IDENTIFIER tokenType expected but token " + currentToken + " of type " + tokenType() + " found.");
        return currentToken;
    }

    public int intVal() {
        if (tokenType() != TokenType.INT_CONST)
            throw new RuntimeException("INT_CONST tokenType expected but token " + currentToken + " of type " + tokenType() + " found.");
        return Integer.parseInt(currentToken);
    }

    public String stringVal() {
        if (tokenType() != TokenType.STRING_CONST)
            throw new RuntimeException("STRING_CONST tokenType expected but token " + currentToken + " of type " + tokenType() + " found.");
        return currentToken.replace("\"", "");
    }
}
