package metrics;

public final class StrengthDiff {
    public final StrengthSnapshot before;
    public final StrengthSnapshot after;

    public StrengthDiff(StrengthSnapshot before, StrengthSnapshot after) {
        this.before = before;
        this.after = after;
    }

    public int scoreDelta() {
        return after.score() - before.score();
    }

    public double guessesMultiplier() {
        if (before.guesses() <= 0) return Double.POSITIVE_INFINITY;
        return after.guesses() / before.guesses();
    }

    public String summary() {
        return """
                --- Strength improvement ---
                Before: score=%d, guesses=%.3g
                After : score=%d, guesses=%.3g
                Score delta          : %+d
                Guess multiplier     : x%.3g

                Crack time (online throttled 100/h): %s  ->  %s
                Crack time (online 10/s)           : %s  ->  %s
                Crack time (offline slow 1e4/s)    : %s  ->  %s
                Crack time (offline fast 1e10/s)   : %s  ->  %s
                """.formatted(
                before.score(), before.guesses(),
                after.score(), after.guesses(),
                scoreDelta(),
                guessesMultiplier(),
                before.crackTimeOnlineThrottling100perHour(), after.crackTimeOnlineThrottling100perHour(),
                before.crackTimeOnlineNoThrottling10perSecond(), after.crackTimeOnlineNoThrottling10perSecond(),
                before.crackTimeOfflineSlowHashing1e4perSecond(), after.crackTimeOfflineSlowHashing1e4perSecond(),
                before.crackTimeOfflineFastHashing1e10perSecond(), after.crackTimeOfflineFastHashing1e10perSecond()
        );
    }
}
