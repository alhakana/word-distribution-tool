package components.output;

import components.Output;
import javafx.collections.ObservableList;
import mvc.model.FileOutput;

import java.util.Map;
import java.util.concurrent.*;

public class CacheOutputComp implements Runnable{

    private BlockingQueue<Output> outputs;
    private ExecutorService threadPool;
    private Map<String, Future<Map<String, Integer>>> result;
    private ObservableList<FileOutput> observableList;

    public CacheOutputComp(ExecutorService threadPool, ObservableList<FileOutput> observableList) {
        this.threadPool = threadPool;
        outputs = new LinkedBlockingQueue<>();
        result = new ConcurrentHashMap<>();
        this.observableList = observableList;

        threadPool.execute(this);
    }

    public void addOutput(Output output) {
        try {
            outputs.put(output);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Output output = output = outputs.take();
                if (output.getName().equals(""))
                    break;

                threadPool.execute(new BagShower(output, this));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Integer> take(String fileName) {
        try {
            return result.get(fileName).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Map<String, Integer> poll(String fileName) {
        if (result.get(fileName) != null && result.get(fileName).isDone())
            return take(fileName);
        return null;
    }

//     agregacija


    public Map<String, Future<Map<String, Integer>>> getResult() {
        return result;
    }

    public ObservableList<FileOutput> getObservableList() {
        return observableList;
    }
}
