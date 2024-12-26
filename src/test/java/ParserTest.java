import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

class ParserTest {
    @Test
    public void parseSymbols() {
        List<String> actual = Parser.getTokens("let x = x - 4;");
        Assertions.assertAll(
                () -> Assertions.assertEquals(7, actual.size()),
                () -> Assertions.assertEquals("let", actual.get(0)),
                () -> Assertions.assertEquals("x", actual.get(1)),
                () -> Assertions.assertEquals("=", actual.get(2)),
                () -> Assertions.assertEquals("x", actual.get(3)),
                () -> Assertions.assertEquals("-", actual.get(4)),
                () -> Assertions.assertEquals("4", actual.get(5)),
                () -> Assertions.assertEquals(";", actual.get(6))
        );
    }

    @Test
    public void parseQuotations() {
        List<String> actual = Parser.getTokens("do Output.printString(\"Score: 0\");");
        Assertions.assertAll(
                () -> Assertions.assertEquals(8, actual.size()),
                () -> Assertions.assertEquals("do", actual.get(0)),
                () -> Assertions.assertEquals("Output", actual.get(1)),
                () -> Assertions.assertEquals(".", actual.get(2)),
                () -> Assertions.assertEquals("printString", actual.get(3)),
                () -> Assertions.assertEquals("(", actual.get(4)),
                () -> Assertions.assertEquals("\"Score: 0\"", actual.get(5)),
                () -> Assertions.assertEquals(")", actual.get(6)),
                () -> Assertions.assertEquals(";", actual.get(7))
        );
    }

    @Test
    public void removeAsteriskComments() throws IOException {
        byte[] multiLineComment = """
                /**
                 * Implements the Square Dance game.
                 */
                 class SquareGame
                """.getBytes(UTF_8);
        byte[] multiLineCommentOnly = """
                /**
                 * Implements the Square Dance game.
                 */
                """.getBytes(UTF_8);
        byte[] singleLineComment = """
                /** Constructs a new Square Game. */
                constructor SquareGame new()
                """
                .getBytes(UTF_8);
        byte[] singleLineCommentOnly = "/** Constructs a new Square Game. */"
                .getBytes(UTF_8);
        byte[] commentInMiddleInput = "do Sys.wait(5); /** delays the next movement */"
                .getBytes(UTF_8);

        String multiLineCommentActual = Parser.removeComments(new ByteArrayInputStream(multiLineComment));
        String multiLineCommentOnlyActual = Parser.removeComments(new ByteArrayInputStream(multiLineCommentOnly));
        String singleLineCommentActual = Parser.removeComments(new ByteArrayInputStream(singleLineComment));
        String singleLineCommentOnlyActual = Parser.removeComments(new ByteArrayInputStream(singleLineCommentOnly));
        String commentInMiddleActual = Parser.removeComments(new ByteArrayInputStream(commentInMiddleInput));

        Assertions.assertAll(
                () -> Assertions.assertEquals("\nclass SquareGame\n", multiLineCommentActual),
                () -> Assertions.assertTrue(multiLineCommentOnlyActual.isBlank()),
                () -> Assertions.assertEquals("\nconstructor SquareGame new()\n", singleLineCommentActual),
                () -> Assertions.assertTrue(singleLineCommentOnlyActual.isBlank()),
                () -> Assertions.assertEquals("do Sys.wait(5); \n", commentInMiddleActual)
        );
    }

    @Test
    public void removeSlashComments() {
        byte[] commentInAboveInput = """
                // waits for the key to be released
                         while (~(key = 0)) {
                """.getBytes(UTF_8);
        byte[] commentInMiddleInput = "field Square square; // the square of this game"
                .getBytes(UTF_8);
        byte[] commentOnlyInput = "// 0=none, 1=up, 2=down, 3=left, 4=right"
                .getBytes(UTF_8);

        String commentInAboveActual = Parser.removeComments(new ByteArrayInputStream(commentInAboveInput));
        String commentInMiddleActual = Parser.removeComments(new ByteArrayInputStream(commentInMiddleInput));
        String commentOnlyActual = Parser.removeComments(new ByteArrayInputStream(commentOnlyInput));

        Assertions.assertAll(
                () -> Assertions.assertEquals("\nwhile (~(key = 0)) {\n", commentInAboveActual),
                () -> Assertions.assertEquals("field Square square; \n", commentInMiddleActual),
                () -> Assertions.assertTrue(commentOnlyActual.isBlank())
        );
    }

    @Test
    public void removeComments() {
        byte[] input = """
                /**
                 * Implements the Square Dance game.
                 */
                
                class SquareGame {
                    field Square square; // the square of this game
                    field int direction; // the square's current direction:
                    // 0=none, 1=up, 2=down, 3=left, 4=right
                
                    /** Constructs a new Square Game. */
                    constructor SquareGame new() {}
                """
                .getBytes(UTF_8);

        String actual = Parser.removeComments(new ByteArrayInputStream(input));
        String expected = """
               
               class SquareGame {
               field Square square;\s
               field int direction;\s
               
               
               constructor SquareGame new() {}
               """;
        Assertions.assertEquals(expected, actual);
    }
}
