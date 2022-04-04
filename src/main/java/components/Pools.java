package components;

import components.cruncher.CounterCruncherComp;
import components.input.FileInputComp;
import components.output.CacheOutputComp;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import mvc.model.Directory;
import mvc.model.FileInput;
import mvc.model.FileOutput;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;

public class Pools {

    public static Pools instance;

    private final ExecutorService inputThreadPool;
    private final HashMap<String, FileInputComp> inputComponents;

    private final ForkJoinPool cruncherThreadPool;
    private final HashMap<Integer, CounterCruncherComp> cruncherComponents;

    private final ExecutorService outputThreadPool;
    private CacheOutputComp output;

    private Pools() {
        inputThreadPool = Executors.newCachedThreadPool();
        inputComponents = new HashMap<>();

        cruncherThreadPool = ForkJoinPool.commonPool();
        cruncherComponents = new HashMap<>();

        outputThreadPool = Executors.newCachedThreadPool();
    }

    public static Pools getInstance() {
        if (instance == null) {
            synchronized (Pools.class) {
                if (instance == null)
                    instance = new Pools();
            }
        }

        return instance;
    }

    public void addInputComp(FileInput fileInput, Text text) {
        FileInputComp fileInputComp = new FileInputComp(inputThreadPool, fileInput.getDisk().toString(), text);
        inputComponents.put(fileInput.getName(), fileInputComp);
    }


    public void addCruncherComp(int arity, Text text) {
        CounterCruncherComp counterCruncherComp = new CounterCruncherComp(cruncherThreadPool, arity, text);
        counterCruncherComp.addCache(output);
        cruncherComponents.put(arity, counterCruncherComp);
    }

    public void removeInputComp(String name) {
        inputComponents.get(name).quit();
        inputComponents.remove(name);
    }

    public void startInputFile(String name) {
        inputComponents.get(name).start();
    }

    public void pauseInputFile(String name) {
        inputComponents.get(name).pause();
    }

    public void linkCruncher(String name, int arity) {
        CounterCruncherComp counterCruncherComp = cruncherComponents.get(arity);
        FileInputComp fileInputComp = inputComponents.get(name);

        fileInputComp.addCruncher(counterCruncherComp);
    }

    public void addDirectory(String name, Directory directory) {
        inputComponents.get(name).addDirectory(directory.getDirectory());
    }

    public void addObservable(ObservableList<FileOutput> observableList) {
        output = new CacheOutputComp(outputThreadPool, observableList);
    }

    public CacheOutputComp getOutput() {
        return output;
    }

    public ExecutorService getOutputThreadPool() {
        return outputThreadPool;
    }

    public void shutDownPools() {
        inputComponents.values().iterator().forEachRemaining(FileInputComp::quit);

        inputThreadPool.shutdown();
        cruncherThreadPool.shutdown();
        outputThreadPool.shutdown();

        while(true) {
            if (((ThreadPoolExecutor)inputThreadPool).getActiveCount() == 0 &&
                    cruncherThreadPool.getActiveThreadCount() == 0 &&
                    ((ThreadPoolExecutor)inputThreadPool).getActiveCount() == 0)
                break;
        }

    }

    public void removeCruncher(int arity) {
        CounterCruncherComp counterCruncherComp = cruncherComponents.get(arity);
        for(FileInputComp fileInputComp : inputComponents.values()) {
            fileInputComp.removeCruncher(counterCruncherComp);
        }
    }

    public void removeDirectory(String name, Directory directory) {
        inputComponents.get(name).removeDirectory(directory.getDirectory());
    }
}
