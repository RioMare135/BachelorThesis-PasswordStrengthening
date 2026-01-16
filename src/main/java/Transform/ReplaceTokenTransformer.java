package Transform;

import java.util.ArrayList;
import java.util.List;

public final class ReplaceTokenTransformer implements TokenTransformer {
    private final List<String> vocabulary;

    public ReplaceTokenTransformer(List<String> vocabulary) {
        this.vocabulary = vocabulary;
    }

    @Override
    public List<String> transform(List<String> tokens) {
        if (tokens.isEmpty()) return tokens;

        List<String> out = new ArrayList<>(tokens);
        int idx = TokenTransformer.rng().nextInt(out.size());

        // Replace only “word-like” tokens by preference; fallback to any index
        int attempts = 0;
        while (attempts++ < 10 && !out.get(idx).matches("[A-Za-z]+")) {
            idx = TokenTransformer.rng().nextInt(out.size());
        }

        String replacement = vocabulary.get(TokenTransformer.rng().nextInt(vocabulary.size()));
        out.set(idx, replacement);
        return out;
    }

    @Override
    public String name() {
        return "replace token";
    }
}
