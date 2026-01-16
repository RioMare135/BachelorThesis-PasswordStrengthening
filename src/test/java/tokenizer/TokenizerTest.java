package tokenizer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    private final Tokenizer tokenizer = new Tokenizer();

    @Test
    void tokenize_emptyString_returnsEmptyList_andDetokenizeIsEmpty() {
        List<String> tokens = tokenizer.tokenize("");
        assertNotNull(tokens);
        assertTrue(tokens.isEmpty());
        assertEquals("", tokenizer.detokenize(tokens));
    }

    @Test
    void tokenize_onlySymbols_keepsAsSingleToken() {
        List<String> tokens = tokenizer.tokenize("###");
        assertEquals(List.of("###"), tokens);
        assertEquals("###", tokenizer.detokenize(tokens));
    }

    @Test
    void tokenize_lettersDigitsSymbols_runsSplitCorrectly() {
        List<String> tokens = tokenizer.tokenize("ilove#dog24");
        assertEquals(List.of("ilove", "#", "dog", "24"), tokens);
        assertEquals("ilove#dog24", tokenizer.detokenize(tokens));
    }

    @Test
    void tokenize_camelCase_splitsIntoSemanticParts() {
        List<String> tokens = tokenizer.tokenize("iLoveYou123");
        // Erwartetes Verhalten aus der verbesserten Tokenizer-Version
        assertEquals(List.of("i", "Love", "You", "123"), tokens);
        assertEquals("iLoveYou123", tokenizer.detokenize(tokens));
    }

    @Test
    void tokenize_mixedSeparators_splitsWordsButPreservesNonLetters() {
        List<String> tokens = tokenizer.tokenize("my_password-Name99!");
        // my_password-Name99! -> ["my", "password", "-", "Name", "99", "!"]
        assertEquals(List.of("my", "_", "password", "-", "Name", "99", "!"), tokens);
        assertEquals("my_password-Name99!", tokenizer.detokenize(tokens));
    }

    @Test
    void detokenize_roundTripProperty_holdsForVariousInputs() {
        String[] inputs = {
                "",
                "12345",
                "!!!!",
                "AbcDEF",
                "aB1#cD2",
                "SecurePass-2024!"
        };
        for (String in : inputs) {
            List<String> tokens = tokenizer.tokenize(in);
            String out = tokenizer.detokenize(tokens);
            assertEquals(in, out, "Round-trip failed for: " + in);
        }
    }
}
