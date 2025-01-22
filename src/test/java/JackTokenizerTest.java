import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.ListIterator;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JackTokenizerTest {
    @Test
    public void advance() {
        String jackCode = "i + 0 - 1";
        JackTokenizer jackTokenizer = new JackTokenizer(new ByteArrayInputStream(jackCode.getBytes(UTF_8)));
        jackTokenizer.advance();
        assertEquals("i", jackTokenizer.identifier());
        jackTokenizer.advance();
        assertEquals('+', jackTokenizer.symbol());
        jackTokenizer.advance();
        assertEquals(0, jackTokenizer.intVal());
        jackTokenizer.advance();
        assertEquals('-', jackTokenizer.symbol());
        jackTokenizer.advance();
        assertEquals(1, jackTokenizer.intVal());
    }

    @Test
    public void advanceAndRetreat() {
        String jackCode = "1 + 0";
        JackTokenizer jackTokenizer = new JackTokenizer(new ByteArrayInputStream(jackCode.getBytes(UTF_8)));
        jackTokenizer.advance();
        jackTokenizer.advance();
        jackTokenizer.advance();

        jackTokenizer.retreat();
        assertEquals('+', jackTokenizer.symbol());
        jackTokenizer.retreat();
        assertEquals(1, jackTokenizer.intVal());

        assertThrows(RuntimeException.class, jackTokenizer::retreat);
    }

    @Test
    public void retreatAndAdvance() {
        String jackCode = "1 + 0";
        JackTokenizer jackTokenizer = new JackTokenizer(new ByteArrayInputStream(jackCode.getBytes(UTF_8)));
        jackTokenizer.advance();

        jackTokenizer.advance();
        jackTokenizer.advance();
        jackTokenizer.retreat();
        jackTokenizer.retreat();

        jackTokenizer.advance();
        assertEquals('+', jackTokenizer.symbol());
        jackTokenizer.advance();
        assertEquals(0, jackTokenizer.intVal());

        assertThrows(RuntimeException.class, jackTokenizer::advance);
    }
}
