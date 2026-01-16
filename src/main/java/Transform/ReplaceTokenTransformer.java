package Transform;


import data.SimilarWordFinder;
import data.Vocab;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ReplaceTokenTransformer implements TokenTransformer {

    private final List<String> vocabulary;

    public ReplaceTokenTransformer() {
        this(Vocab.defaultVocabulary());
    }

    public ReplaceTokenTransformer(List<String> vocabulary) {
        if (vocabulary == null || vocabulary.isEmpty()) {
            throw new IllegalArgumentException("Vocabulary must not be null/empty");
        }
        this.vocabulary = vocabulary;
    }

    @Override
    public List<String> transform(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return tokens == null ? List.of() : tokens;
        }

        List<String> out = new ArrayList<>(tokens);

        // Choose a token index to replace; prefer alphabetic tokens
        int idx = pickReplaceIndex(out);
        if (idx < 0) {
            // No suitable token found, fallback: replace a random token
            idx = TokenTransformer.rng().nextInt(out.size());
        }

        String original = out.get(idx);

        // If token is alphabetic, try to find a similar word by category
        if (original != null && original.matches("[A-Za-z]+")) {
            Optional<String> similar = SimilarWordFinder.findSimilar(original);
            if (similar.isPresent() && !similar.get().equals(original)) {
                out.set(idx, similar.get());
                return out;
            }
        }

        // Fallback: replace with a random token from the global vocabulary
        String replacement = pickReplacementPreservingCase(original);
        out.set(idx, replacement);

        return out;
    }

    @Override
    public String name() {
        return "replace token";
    }

    /**
     * Picks an index of a token that is likely to be meaningful to replace.
     * Prefers pure alphabetic tokens, otherwise returns -1.
     */
    private int pickReplaceIndex(List<String> tokens) {
        // Try a few random attempts to find an alphabetic token
        for (int attempts = 0; attempts < 10; attempts++) {
            int i = TokenTransformer.rng().nextInt(tokens.size());
            String t = tokens.get(i);
            if (t != null && t.matches("[A-Za-z]+")) {
                return i;
            }
        }

        // If random attempts fail, do a linear scan for any alphabetic token
        for (int i = 0; i < tokens.size(); i++) {
            String t = tokens.get(i);
            if (t != null && t.matches("[A-Za-z]+")) {
                return i;
            }
        }

        return -1;
    }

    private String pickReplacementPreservingCase(String original) {
        String candidate = vocabulary.get(TokenTransformer.rng().nextInt(vocabulary.size()));

        // If original started with uppercase, capitalize the replacement if it is alphabetic
        if (original != null && !original.isEmpty()
                && Character.isUpperCase(original.charAt(0))
                && candidate.matches("[A-Za-z]+")) {
            return capitalize(candidate);
        }

        return candidate;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
