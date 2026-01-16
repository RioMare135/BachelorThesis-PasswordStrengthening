package data;

import java.util.List;
import java.util.Map;

public final class VocabCategories {

    private VocabCategories() {}

    public static Map<String, List<String>> categories() {
        return Map.of(
                "color", List.of("blue","green","orange","purple","silver","gold","black","white","red","yellow"),
                "nature", List.of("river","mountain","forest","ocean","cloud","storm","wind","fire","stone","tree"),
                "tech", List.of("secure","strong","random","system","network","data","crypto","hash","token"),
                "adjective", List.of("safe","fast","better","solid","robust","smart","brave","sharp"),
                "greek", List.of("alpha","beta","delta","gamma","kappa","lambda","omega","sigma")
        );
    }
}