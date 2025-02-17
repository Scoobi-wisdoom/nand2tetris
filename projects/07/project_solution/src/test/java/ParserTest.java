import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    @Test
    public void commandType() {
        String pushInput = """
                // a comment
                push constant 1
                """;
        Parser pushParser = new Parser(new ByteArrayInputStream(pushInput.getBytes()));

        String arithmeticInput = """
                // an example only to get commandTypes.
                eq
                lt
                gt
                add
                sub
                neg
                and
                or
                not
                """;
        Parser arithmeticParser = new Parser(new ByteArrayInputStream(arithmeticInput.getBytes()));

        pushParser.advance();
        arithmeticParser.advance();
        assertAll(
                () -> assertEquals(Parser.C_PUSH, pushParser.commandType()),
                () -> {
                    for (int i = 0; i < 10; i++) {
                        arithmeticParser.advance();
                        int actual = arithmeticParser.commandType();
                        assertEquals(Parser.C_ARITHMETIC, actual);
                    }
                }
        );
    }

    @Test
    public void args1() {
        String pushInput = """
                push constant 1
                """;
        Parser pushParser = new Parser(new ByteArrayInputStream(pushInput.getBytes()));
        String arithmeticInput = """
                eq
                """;
        Parser arithmeticParser = new Parser(new ByteArrayInputStream(arithmeticInput.getBytes()));

        pushParser.advance();
        arithmeticParser.advance();
        assertAll(
                () -> assertEquals("constant", pushParser.arg1()),
                () -> assertEquals("eq", arithmeticParser.arg1())
        );
    }

    @Test
    public void args2() {
        String pushInput = """
                push constant 1
                """;
        Parser pushParser = new Parser(new ByteArrayInputStream(pushInput.getBytes()));
        String arithmeticInput = """
                eq
                """;
        Parser arithmeticParser = new Parser(new ByteArrayInputStream(arithmeticInput.getBytes()));

        pushParser.advance();
        arithmeticParser.advance();
        assertAll(
                () -> assertEquals(1, pushParser.arg2()),
                () -> assertThrows(RuntimeException.class, arithmeticParser::arg2)
        );
    }
}
