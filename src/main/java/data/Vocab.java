package data;

import java.util.List;

public final class Vocab {
    private Vocab() {}

    public static List<String> defaultVocabulary() {
        return List.of(
                "about","above","after","again","among","around","because","before","between",
                "blue","green","orange","purple","silver",
                "river","mountain","forest","ocean","cloud",
                "alpha","delta","gamma","kappa","omega",
                "Secure","Strong","Better","Random",
                "!", "#", "$", "@", "_"
        );
    }
}