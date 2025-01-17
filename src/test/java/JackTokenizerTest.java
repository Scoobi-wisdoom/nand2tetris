import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
