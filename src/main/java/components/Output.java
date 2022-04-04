package components;

import java.util.Map;
import java.util.concurrent.Future;

public class Output {

    private final String name;
    private final Future<Map<String, Integer>> bagOfWords;
    public Output(String name, Future<Map<String, Integer>> bagOfWords) {
        this.name = name;
        this.bagOfWords = bagOfWords;
    }

    public Future<Map<String, Integer>> getBagOfWords() {
        return bagOfWords;
    }

    public String getName() {
        return name;
    }
}
