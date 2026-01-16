package data;

import java.util.List;

public final class Vocab {

    private Vocab() {}

    public static List<String> defaultVocabulary() {
        return List.of(
                // Common words
                "about","above","after","again","among","around","because","before","between",
                "inside","outside","without","within","towards","through",

                // Colors
                "blue","green","orange","purple","silver","gold","black","white","red","yellow",

                // Nature
                "river","mountain","forest","ocean","cloud","storm","wind","fire","stone","tree",

                // Time & seasons
                "spring","summer","autumn","winter","morning","evening","night","day","year",

                // Greek / symbolic
                "alpha","beta","delta","gamma","kappa","lambda","omega","sigma",

                // Tech-related
                "secure","strong","random","system","network","data","crypto","hash","token",
                "binary","digital","signal","vector","matrix",

                // Action verbs
                "run","jump","build","create","change","protect","verify","encode","decode",

                // Positive adjectives
                "safe","fast","better","solid","robust","smart","brave","sharp",

                // Common numbers
                "one","two","three","four","five","seven","eight","nine","ten",

                // Symbols
                "!","#","$","@","_","%","&","*","+","?"
        );
    }
}