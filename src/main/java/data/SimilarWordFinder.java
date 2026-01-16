package data;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SimilarWordFinder {

    private SimilarWordFinder() {}

    private static final SecureRandom RNG = new SecureRandom();

    public static Optional<String> findSimilar(String token) {
        if (token == null || token.isBlank()) return Optional.empty();

        String lower = token.toLowerCase();

        for (List<String> words : VocabCategories.categories().values()) {
            if (words.contains(lower)) {

                // Build candidate list excluding the original token
                List<String> candidates = new ArrayList<>(words.size());
                for (String w : words) {
                    if (!w.equals(lower)) candidates.add(w);
                }

                if (candidates.isEmpty()) return Optional.empty();

                String chosen = candidates.get(RNG.nextInt(candidates.size()));

                // Preserve capitalization if original starts with uppercase
                if (Character.isUpperCase(token.charAt(0))) {
                    chosen = capitalize(chosen);
                }

                return Optional.of(chosen);
            }
        }

        return Optional.empty();
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}

