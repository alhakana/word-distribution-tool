package components.output;

import components.Output;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CacheOutputComp {

    private BlockingQueue<Output> outputs;

    public CacheOutputComp() {
        outputs = new LinkedBlockingQueue<>();
    }

    public void addOutput(Output output) {
        try {
            outputs.put(output);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    public Map<String, Integer> take() {
//
//    }
//
//    public Map<String, Integer> poll() {
//
//    }

//     agregacija
}
