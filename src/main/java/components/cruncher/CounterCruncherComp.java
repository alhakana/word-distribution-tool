package components.cruncher;

import components.Input;
import components.Output;
import components.Utils;
import components.output.CacheOutputComp;
import javafx.scene.text.Text;
import mvc.app.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class CounterCruncherComp implements Runnable{

    private final ForkJoinPool threadPool;
    private final int arity;
    private final Text text;

    private final BlockingQueue<Input> inputs;
    private final List<CacheOutputComp> caches;

    public static int L = Integer.parseInt(Config.getProperty("counter_data_limit"));

    public CounterCruncherComp(ForkJoinPool threadPool, Integer arity, Text text) {
        this.threadPool = threadPool;
        this.arity = arity;
        this.text = text;

        inputs = new LinkedBlockingQueue<>();
        caches = new ArrayList<>();

        threadPool.execute(this);
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
//                System.out.println("pokrenut cruncher");
                Input input = inputs.take();
                if (input.getName().equals("")) {
                    sendOutput(new Output("", null));
                    break;
                }

                System.out.println("stigao inputs");
                Utils.notifyPlatformAppend(text, input.getName());
                String inputText = input.getText();
                Future<Map<String, Integer>> futureResult = threadPool.submit(new FileCruncher(inputText, arity, 0, inputText.length()));

                Output output = new Output(input.getName()+"-arity"+arity, futureResult);
                sendOutput(output);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void addCache(CacheOutputComp cacheOutputComp) {
        caches.add(cacheOutputComp);
    }

//    public void removeCache(CacheOutputComp cacheOutputComp) {
//        caches.remove(cacheOutputComp);
//    }

    public void sendOutput(Output output) {
        caches.iterator().forEachRemaining(cache -> cache.addOutput(output));
    }

}
