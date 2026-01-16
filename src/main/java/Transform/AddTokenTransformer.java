package Transform;

import java.util.ArrayList;
import java.util.List;

public final class AddTokenTransformer implements TokenTransformer {
    private final List<String> vocabulary;

    public AddTokenTransformer(List<String> vocabulary) {
        this.vocabulary = vocabulary;
    }

    @Override
    public List<String> transform(List<String> tokens) {
        List<String> out = new ArrayList<>(tokens);
        //TokenTransformer.rng() could cause problems
        String tokenToAdd = vocabulary.get(TokenTransformer.rng().nextInt(vocabulary.size()));
        int pos = TokenTransformer.rng().nextInt(out.size() + 1); // insert anywhere
        out.add(pos, tokenToAdd);
        return out;
    }

    @Override
    public String name() {
        return "add token";
    }
}
