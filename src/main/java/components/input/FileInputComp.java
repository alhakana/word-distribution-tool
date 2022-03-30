package components.input;

import components.Input;
import components.Utils;
import components.cruncher.CounterCruncherComp;
import javafx.scene.text.Text;
import mvc.app.Config;
import mvc.model.Cruncher;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class FileInputComp implements Runnable {

    private ExecutorService threadPool;
    private final String disc;
    private Text text;

    private List<CounterCruncherComp> crunchers;
    private List<File> directories;
    private Map<File, Long> lastModify;

    private volatile boolean started = false;
    private volatile boolean running = false;
    private volatile boolean quit = false;

    public FileInputComp(ExecutorService threadPool, String disc, Text text) {
        this.threadPool = threadPool;
        this.disc = disc;
        this.text = text;

        directories = new CopyOnWriteArrayList<>();
        lastModify = new ConcurrentHashMap<>();
        crunchers = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        while(!quit) {
            if (running) {
//                System.out.println("running " + disc);
                searchDirectories();
                sleep(Integer.parseInt(Config.getProperty("file_input_sleep_time")));
            } else
                sleep(0);
        }
    }

    public void searchDirectories() {
        directories.iterator().forEachRemaining(this::searchFiles);
    }

    private void searchFiles(File directory) {
        List<File> files = Arrays.asList(directory.listFiles());

        if (!files.isEmpty()) {
            for (File f : files) {
                if (f.isDirectory())
                    searchFiles(f);
                else if (getFileExtension(f).equals(".txt")) {
                    isReadable(f);
                }
            }
        }
    }

    private void isReadable(File file) {
//        System.out.println("is readable");
        Long lastModified = lastModify.get(file);
        if (lastModified == null || lastModified < file.lastModified()) {
            lastModify.put(file, file.lastModified());
            Utils.notifyPlatform(text, "File input started reading on " + disc);
            Future<Input> input = threadPool.submit(new FileReader(file, disc, crunchers));
            try {
                sendInput(input.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileExtension(File directory) {
        if (directory == null || directory.getName() == null)
            return "";
        String name = directory.getName().toLowerCase();
        if (directory.getName().contains("."))
            return name.substring(name.lastIndexOf("."));

        return "";
    }

    private synchronized void sleep(int time) {
        try {
            if (time == 0)
                wait();
            else
                wait(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addCruncher(CounterCruncherComp cruncher) {
        crunchers.add(cruncher);
    }

    void removeCruncher(CounterCruncherComp cruncher) {
        crunchers.remove(cruncher);
    }

    public void addDirectory(File directory) {
        directories.add(directory);
    }

    void removeDirectory(File directory) {
        List<File> files = Collections.synchronizedList(Arrays.asList(directory.listFiles()));

        if (!files.isEmpty()) {
            for (File f : files) {
                if (f.isDirectory())
                    removeDirectory(f);
                else if (getFileExtension(f).equals(".txt")) {
                    lastModify.remove(f);
                }
            }
        }
    }

    public synchronized void start() {
        if (!started) {
            threadPool.execute(this);
            started = true;
        }

        running = true;
        notify();
    }

    public synchronized void pause() {
        running = false;
        notify();
    }

    public void quit() {
        quit = true;
        Input input = new Input();
        sendInput(input);
    }

    private void sendInput(Input input) {
        crunchers.iterator().forEachRemaining(counterCruncherComp -> counterCruncherComp.addInput(input));
    }

}
