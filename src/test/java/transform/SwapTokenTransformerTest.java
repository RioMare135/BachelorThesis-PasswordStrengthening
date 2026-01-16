package transform;

import Transform.SwapTokenTransformer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SwapTokenTransformerTest {

    @Test
    void swap_withLessThanTwoTokens_returnsUnchanged() {
        SwapTokenTransformer t = new SwapTokenTransformer();

        assertEquals(List.of(), t.transform(List.of()));
        assertEquals(List.of("only"), t.transform(List.of("only")));
    }

    @Test
    void swap_withTwoTokens_preservesElementsAndLength() {
        SwapTokenTransformer t = new SwapTokenTransformer();

        List<String> in = List.of("a", "b");
        List<String> out = t.transform(in);

        assertEquals(2, out.size());
        assertTrue(out.contains("a"));
        assertTrue(out.contains("b"));
    }
}
