package components.output;

import java.util.Map;

public interface CacheOutputComp {

    Map<String, Integer> poll();
    Map<String, Integer> take();

    // agregacija
}
