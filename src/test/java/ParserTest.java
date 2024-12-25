import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

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
}
