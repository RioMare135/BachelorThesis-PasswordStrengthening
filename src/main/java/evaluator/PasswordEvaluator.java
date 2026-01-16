package evaluator;

import com.nulabinc.zxcvbn.Zxcvbn;
import com.nulabinc.zxcvbn.Strength;

import java.util.List;

public final class PasswordEvaluator {
    private final Zxcvbn zxcvbn = new Zxcvbn();

    public Strength evaluate(String password, List<String> userInputs) {
        // userInputs can contain name/email/etc. to penalize easy matches
        return zxcvbn.measure(password, userInputs);
    }
}
