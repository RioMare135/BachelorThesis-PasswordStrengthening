package Transform;

import java.util.ArrayList;
import java.util.List;

public final class SwapTokenTransformer implements TokenTransformer {
    @Override
    public List<String> transform(List<String> tokens) {
        if (tokens.size() < 2) return tokens;

        List<String> out = new ArrayList<>(tokens);
        int i = TokenTransformer.rng().nextInt(out.size());
        int j = TokenTransformer.rng().nextInt(out.size());
        while (j == i) j = TokenTransformer.rng().nextInt(out.size());

        String tmp = out.get(i);
        out.set(i, out.get(j));
        out.set(j, tmp);
        return out;
    }

    @Override
    public String name() {
        return "swap token";
    }
}
