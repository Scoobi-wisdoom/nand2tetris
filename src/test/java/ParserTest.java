import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {
    private Parser parser = new Parser();

    @Test
    @DisplayName("comments and empty strings are ignored.")
    public void parse() {
        String input = "// This file is part of www.nand2tetris.org\n" +
                "// and the book \"The Elements of Computing Systems\"\n" +
                "// by Nisan and Schocken, MIT Press.\n" +
                "// File name: projects/07/StackArithmetic/SimpleAdd/SimpleAdd.vm\n" +
                "\n" +
                "// Pushes and adds two constants.\n" +
                "push constant 7\n" +
                "push constant 8\n" +
                "add";


        String expected = "push constant 7\n" +
                "push constant 8\n" +
                "add";

        List<String> commands = Arrays.asList(input.split("\n"));
        assertEquals(expected, parser.parse(commands));
    }
}
