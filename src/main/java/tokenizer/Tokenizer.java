package tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Tokenizer {

    // First-level split: letters | digits | symbols
    private static final Pattern RUNS =
            Pattern.compile("[A-Za-z]+|\\d+|[^A-Za-z\\d]+");

    // Split camelCase / PascalCase boundaries
    // Example: "iLoveYou" -> "i", "Love", "You"
    private static final Pattern CAMEL_CASE =
            Pattern.compile("(?<=[a-z])(?=[A-Z])");

    // Split common word separators inside letter runs
    private static final Pattern WORD_SEPARATORS =
            Pattern.compile("[_\\-]+");

    public List<String> tokenize(String password) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = RUNS.matcher(password);

        while (matcher.find()) {
            String run = matcher.group();

            // If it's purely alphabetic, apply semantic splitting
            if (run.matches("[A-Za-z]+")) {
                splitAlphabeticRun(run, tokens);
            } else {
                // Digits or symbols stay unchanged
                tokens.add(run);
            }
        }
        return tokens;
    }

    private void splitAlphabeticRun(String run, List<String> tokens) {
        // Step 1: split by separators like "_" or "-"
        String[] parts = WORD_SEPARATORS.split(run);

        for (String part : parts) {
            if (part.isEmpty()) continue;

            // Step 2: split camelCase / PascalCase
            String[] camelParts = CAMEL_CASE.split(part);
            for (String cp : camelParts) {
                if (!cp.isEmpty()) {
                    tokens.add(cp);
                }
            }
        }
    }

    public String detokenize(List<String> tokens) {
        // Keep original behavior: exact reconstruction
        return String.join("", tokens);
    }
}