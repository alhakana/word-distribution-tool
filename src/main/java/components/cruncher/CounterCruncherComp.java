package components.cruncher;

import components.Input;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class CounterCruncherComp {

    private ExecutorService threadPool;
    private int arity;
    private Text text;

    private BlockingQueue<Input> inputs;


    public CounterCruncherComp(ExecutorService threadPool, Integer arity, Text text) {
        this.threadPool = threadPool;
        this.arity = arity;
        this.text = text;

        inputs = new LinkedBlockingQueue<>();
    }

    public void addInput(Input input) {
        inputs.add(input);
    }




}
