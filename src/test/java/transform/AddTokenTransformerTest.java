package transform;

import Transform.AddTokenTransformer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddTokenTransformerTest {

    @Test
    void addToken_increasesLengthByOne_evenIfInputEmpty() {
        AddTokenTransformer t = new AddTokenTransformer(List.of("X"));

        List<String> out1 = t.transform(List.of());
        assertEquals(1, out1.size());
        assertEquals("X", out1.get(0));

        List<String> out2 = t.transform(List.of("a", "b"));
        assertEquals(3, out2.size());
        assertTrue(out2.contains("a"));
        assertTrue(out2.contains("b"));
        assertTrue(out2.contains("X"));
    }
}
