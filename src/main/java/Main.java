import com.nulabinc.zxcvbn.Strength;
import data.Vocab;
import evaluator.PasswordEvaluator;
import metrics.StrengthDiff;
import metrics.StrengthSnapshot;
import tokenizer.Tokenizer;
import Transform.*;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class Main {
    private static final int MIN_ACCEPTED_SCORE = 3;
    private static final int MAX_ENHANCEMENT_ROUNDS = 10; // safety guard

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Console console = System.console();

        PasswordEvaluator evaluator = new PasswordEvaluator();
        Tokenizer tokenizer = new Tokenizer();

        List<String> userInputs = new ArrayList<>();
        System.out.println("Optional: enter related words to penalize (name/email/etc), comma-separated, or blank:");
        String related = sc.nextLine().trim();
        if (!related.isEmpty()) {
            for (String r : related.split(",")) userInputs.add(r.trim());
        }

        List<TokenTransformer> transformers = List.of(
                new AddTokenTransformer(Vocab.defaultVocabulary()),
                new ReplaceTokenTransformer(Vocab.defaultVocabulary()),
                new SwapTokenTransformer()
        );

        while (true) {
            System.out.println("\nEnter password to evaluate:");
            String pw = (console != null) ? new String(console.readPassword()) : sc.nextLine();

            Strength base = evaluator.evaluate(pw, userInputs);
            StrengthSnapshot before = StrengthSnapshot.from(pw, base);
            printStrength("Initial", before);

            if (before.score() >= MIN_ACCEPTED_SCORE) {
                System.out.println("Password accepted (meets threshold).");
                return;
            }

            String currentPw = pw;
            StrengthSnapshot currentSnap = before;

            int round = 0;
            while (currentSnap.score() < MIN_ACCEPTED_SCORE && round < MAX_ENHANCEMENT_ROUNDS) {
                round++;

                System.out.println("\nPassword is too weak. Choose approach:");
                System.out.println("1) Apply ONE method (add/replace/swap)");
                System.out.println("2) Apply ALL THREE methods (pipeline)");
                System.out.println("3) Quit");
                int approach = readInt(sc, 1, 3);
                if (approach == 3) return;

                List<String> tokens = tokenizer.tokenize(currentPw);
                List<String> transformedTokens;

                if (approach == 1) {
                    System.out.println("\nChoose method:");
                    System.out.println("1) add token");
                    System.out.println("2) replace token");
                    System.out.println("3) swap token");
                    int method = readInt(sc, 1, 3);
                    TokenTransformer t = transformers.get(method - 1);
                    transformedTokens = t.transform(tokens);
                    System.out.println("Applied: " + t.name());
                } else {
                    transformedTokens = tokens;
                    for (TokenTransformer t : transformers) {
                        transformedTokens = t.transform(transformedTokens);
                    }
                    System.out.println("Applied: add token -> replace token -> swap token");
                }

                String enhancedPw = tokenizer.detokenize(transformedTokens);

                Strength enhanced = evaluator.evaluate(enhancedPw, userInputs);
                StrengthSnapshot after = StrengthSnapshot.from(enhancedPw, enhanced);

                printStrength("Enhanced (round " + round + ")", after);
                System.out.println(new StrengthDiff(currentSnap, after).summary());

                currentPw = enhancedPw;
                currentSnap = after;

                if (currentSnap.score() >= MIN_ACCEPTED_SCORE) {
                    System.out.println("Enhanced password meets the threshold.");
                    return;
                }

                System.out.println("\nStill below threshold. What next?");
                System.out.println("1) Strengthen again (continue from current password)");
                System.out.println("2) Start over with a new password");
                System.out.println("3) Quit");
                int next = readInt(sc, 1, 3);
                if (next == 1) continue;
                if (next == 2) break; // breaks inner loop; outer loop asks for new password
                return;
            }

            if (round >= MAX_ENHANCEMENT_ROUNDS) {
                System.out.println("\nReached max enhancement rounds (" + MAX_ENHANCEMENT_ROUNDS + ").");
                System.out.println("Start over with a new password.");
            }
        }
    }

    private static int readInt(Scanner sc, int min, int max) {
        while (true) {
            String s = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) {}
            System.out.printf("Enter a number between %d and %d:%n", min, max);
        }
    }

    private static void printStrength(String label, StrengthSnapshot snap) {
        System.out.printf("%n--- %s evaluation ---%n", label);
        System.out.println("Password: " + snap.password());
        System.out.println("Score   : " + snap.score() + " (0=weak, 4=strong)");
        System.out.printf("Guesses : %.3g%n", snap.guesses());
        System.out.println("Crack time (online throttled 100/h): " + snap.crackTimeOnlineThrottling100perHour());
        System.out.println("Crack time (online 10/s)           : " + snap.crackTimeOnlineNoThrottling10perSecond());
        System.out.println("Crack time (offline slow 1e4/s)    : " + snap.crackTimeOfflineSlowHashing1e4perSecond());
        System.out.println("Crack time (offline fast 1e10/s)   : " + snap.crackTimeOfflineFastHashing1e10perSecond());
    }
}
