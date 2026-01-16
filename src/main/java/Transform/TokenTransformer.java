package Transform;


import java.security.SecureRandom;
import java.util.List;

public interface TokenTransformer {
    List<String> transform(List<String> tokens);
    String name();
    static SecureRandom rng() { return new SecureRandom(); }
}
