package metrics;

import com.nulabinc.zxcvbn.Strength;

public record StrengthSnapshot(
        String password,
        int score,                 // 0..4
        double guesses,
        String crackTimeOnlineThrottling100perHour,
        String crackTimeOnlineNoThrottling10perSecond,
        String crackTimeOfflineSlowHashing1e4perSecond,
        String crackTimeOfflineFastHashing1e10perSecond
) {
    public static StrengthSnapshot from(String password, Strength s) {
        return new StrengthSnapshot(
                password,
                s.getScore(),
                s.getGuesses(),
                s.getCrackTimesDisplay().getOnlineThrottling100perHour(),
                s.getCrackTimesDisplay().getOnlineNoThrottling10perSecond(),
                s.getCrackTimesDisplay().getOfflineSlowHashing1e4perSecond(),
                s.getCrackTimesDisplay().getOfflineFastHashing1e10PerSecond()
        );
    }
}
