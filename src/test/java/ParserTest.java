import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {
    private Parser parser = new Parser();

    @Test
    public void parse() {
        String comment = "// This file is part of www.nand2tetris.org";
        String blank = "";
        String command = "push constant 7";

        assertAll(
                "Group Assertions of commands",
                () -> assertEquals("", parser.parse(comment), "Comments are ignored."),
                () -> assertEquals("", parser.parse(blank), "Empty strings are ignored."),
                () -> assertEquals(command, parser.parse(command), "Empty strings are ignored.")
        );

    }
}
