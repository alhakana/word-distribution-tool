package components;

import components.cruncher.CounterCruncherComp;
import components.input.FileInputComp;
import javafx.scene.text.Text;
import mvc.model.FileInput;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Pools {

    public static Pools instance;

    private ExecutorService inputThreadPool;
    private HashMap<String, FileInputComp> inputComponents;

    private ExecutorService cruncherThreadPool;
    private HashMap<Integer, CounterCruncherComp> cruncherComponents;

    private Pools() {
        inputThreadPool = Executors.newCachedThreadPool();
        inputComponents = new HashMap<>();

//        cruncherThreadPool = 
        cruncherComponents = new HashMap<>();
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
    }

    public void removeInputComp() {

    }

    public void startInputFile(String name) {
        inputComponents.get(name).start();
    }

    public void pauseInputFile(String name) {
        FileInputComp fileInputComp = inputComponents.get(name);


    }



}
