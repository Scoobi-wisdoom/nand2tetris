package syntax.analyzer;

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
    public void parseOperations() {
        List<String> actual = Parser.getTokens("i + 0 - 1");

        Assertions.assertAll(
                () -> Assertions.assertEquals(5, actual.size()),
                () -> Assertions.assertEquals("i", actual.get(0)),
                () -> Assertions.assertEquals("+", actual.get(1)),
                () -> Assertions.assertEquals("0", actual.get(2)),
                () -> Assertions.assertEquals("-", actual.get(3)),
                () -> Assertions.assertEquals("1", actual.get(4))
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
        String multiLineCommentActual = Parser.removeComments(new ByteArrayInputStream("""
                /**
                 * Implements the Square Dance game.
                 */
                 class SquareGame
                """.getBytes(UTF_8)));
        String multiLineCommentExpected = """
                
                
                
                class SquareGame
                """;

        String multiLineCommentOnlyActual = Parser.removeComments(new ByteArrayInputStream("""
                /**
                 * Implements the Square Dance game.
                 */
                """.getBytes(UTF_8)));
        String multiLineCommentOnlyExpected = """
                
                
                
                """;

        String singleLineCommentActual = Parser.removeComments(new ByteArrayInputStream("""
                /** Constructs a new Square Game. */
                constructor SquareGame new()
                """
                .getBytes(UTF_8)));
        String singleLineCommentExpected = """
                
                constructor SquareGame new()
                """;

        String singleLineCommentOnlyActual = Parser.removeComments(new ByteArrayInputStream("/** Constructs a new Square Game. */"
                .getBytes(UTF_8)));
        String singleLineCommentOnlyExpected = """
                
                """;

        String commentInMiddleActual = Parser.removeComments(new ByteArrayInputStream("do Sys.wait(5); /** delays the next movement */"
                .getBytes(UTF_8)));
        String commentInMiddleExpected = """
                do Sys.wait(5);\s
                """;

        Assertions.assertAll(
                () -> Assertions.assertEquals(multiLineCommentExpected, multiLineCommentActual),
                () -> Assertions.assertEquals(multiLineCommentOnlyExpected, multiLineCommentOnlyActual),
                () -> Assertions.assertEquals(singleLineCommentExpected, singleLineCommentActual),
                () -> Assertions.assertEquals(singleLineCommentOnlyExpected, singleLineCommentOnlyActual),
                () -> Assertions.assertEquals(commentInMiddleExpected, commentInMiddleActual)
        );
    }

    @Test
    public void removeSlashComments() {
        String commentInAboveActual = Parser.removeComments(new ByteArrayInputStream("""
                // waits for the key to be released
                         while (~(key = 0)) {
                """.getBytes(UTF_8)));
        String commentInAboveExpected = """
                
                while (~(key = 0)) {
                """;

        String commentInMiddleActual = Parser.removeComments(new ByteArrayInputStream("field Square square; // the square of this game"
                .getBytes(UTF_8)));
        String commentInMiddleExpected = """
                field Square square;\s
                """;

        String commentOnlyActual = Parser.removeComments(new ByteArrayInputStream("// 0=none, 1=up, 2=down, 3=left, 4=right"
                .getBytes(UTF_8)));
        String commentOnlyExpected = """
                
                """;

        Assertions.assertAll(
                () -> Assertions.assertEquals(commentInAboveExpected, commentInAboveActual),
                () -> Assertions.assertEquals(commentInMiddleExpected, commentInMiddleActual),
                () -> Assertions.assertEquals(commentOnlyExpected, commentOnlyActual)
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

    @Test
    public void removeNothing() {
        byte[] input = "1"
                .getBytes(UTF_8);

        String actual = Parser.removeComments(new ByteArrayInputStream(input));
        String expected = """
                1
                """;
        Assertions.assertEquals(expected, actual);
    }
}
