package components.output;

import components.Output;
import components.Utils;
import javafx.collections.ObservableList;
import mvc.model.FileOutput;

import java.util.Map;
import java.util.concurrent.*;

public class CacheOutputComp implements Runnable{

    private final BlockingQueue<Output> outputs;
    private final ExecutorService threadPool;
    private final Map<String, Future<Map<String, Integer>>> result;
    private final ObservableList<FileOutput> observableList;

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
                Output output = outputs.take();
                if (output.getName().equals(""))
                    break;

                threadPool.execute(new BagShower(output, this));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                Utils.closeApp();
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

    public Map<String, Future<Map<String, Integer>>> getResult() {
        return result;
    }

    public ObservableList<FileOutput> getObservableList() {
        return observableList;
    }

    public void addResult(String name, Future<Map<String, Integer>> newMap) {
        FileOutput fileOutput = new FileOutput(name);
        fileOutput.setDone(true);
        result.put(name, newMap);
        Utils.updateList(observableList, fileOutput);
    }
}
