package components;

import java.util.Map;
import java.util.concurrent.Future;

public class Output {

    private String name;
    private Future<Map<String, Integer>> bagOfWords;
    public Output(String name, Future<Map<String, Integer>> bagOfWords) {
        this.name = name;
        this.bagOfWords = bagOfWords;
    }
}
