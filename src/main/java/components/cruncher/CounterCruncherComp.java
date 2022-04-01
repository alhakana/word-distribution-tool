package components.cruncher;

import components.Input;
import components.Output;
import components.output.CacheOutputComp;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class CounterCruncherComp implements Runnable{

    private ExecutorService threadPool;
    private int arity;
    private Text text;

    private BlockingQueue<Input> inputs;
    private List<CacheOutputComp> caches;


    public CounterCruncherComp(ExecutorService threadPool, Integer arity, Text text) {
        this.threadPool = threadPool;
        this.arity = arity;
        this.text = text;

        inputs = new LinkedBlockingQueue<>();
        caches = new ArrayList<>();
    }

    public void addInput(Input input) {
        try {
            inputs.put(input);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while(true) {
            try {
                Input input = inputs.take();
                if (input.getName().equals("")) {
                    sendOutput(new Output("", null));
                    break;
                }







            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void addCache(CacheOutputComp cacheOutputComp) {
        caches.add(cacheOutputComp);
    }

    public void removeCache(CacheOutputComp cacheOutputComp) {
        caches.remove(cacheOutputComp);
    }

    public void sendOutput(Output output) {
        caches.iterator().forEachRemaining(cache -> cache.addOutput(output));
    }

}
