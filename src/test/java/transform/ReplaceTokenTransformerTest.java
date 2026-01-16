package transform;

import Transform.ReplaceTokenTransformer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReplaceTokenTransformerTest {

    @Test
    void replaceToken_withEmptyInput_returnsEmpty() {
        ReplaceTokenTransformer t = new ReplaceTokenTransformer(List.of("R"));
        assertEquals(List.of(), t.transform(List.of()));
    }

    @Test
    void replaceToken_preservesLength() {
        ReplaceTokenTransformer t = new ReplaceTokenTransformer(List.of("R"));

        List<String> in = List.of("alpha", "123", "!");
        List<String> out = t.transform(in);

        assertEquals(in.size(), out.size());
        // Mindestens ein Token muss "R" werden können
        assertTrue(out.contains("R"));
    }
}
