package components.input;

import components.Input;
import components.Utils;
import components.cruncher.CounterCruncherComp;
import javafx.scene.text.Text;
import mvc.app.Config;
import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class FileInputComp implements Runnable {

    private final ExecutorService threadPool;
    private final String disc;
    private final Text text;

    private final List<CounterCruncherComp> crunchers;
    private final List<File> directories;
    private final Map<File, Long> lastModify;

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
//        System.out.println("running " + disc);

        while(!quit) {
            System.out.println("while petlja input");
            if (running) {
                System.out.println("running!!!");
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
//        System.out.println("searching directories");
        File[] filesArray = directory.listFiles();
        if (filesArray == null)
            return;

        List<File> files = Arrays.asList(filesArray);

        if (!files.isEmpty()) {
//            System.out.println("searching files");
            for (File f : files) {
                if (f.isDirectory()) {
//                    System.out.println("searching directory " + f.getName());
                    searchFiles(f);
                }
                else if (getFileExtension(f).equals(".txt")) {
//                    System.out.println("searching file " + f.getName());
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
            Utils.notifyPlatform(text, text.getText() + "\n" + file.getName());
            Future<Input> input = threadPool.submit(new FileReader(file, disc, crunchers));
            try {
                sendInput(input.get());
//                System.out.println("poslat input");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileExtension(File directory) {
        if (directory == null)
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
        File[] filesArray = directory.listFiles();
        if (filesArray == null)
            return;
        List<File> files = Collections.synchronizedList(Arrays.asList(filesArray));

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
            started = true;
            running = true;
            threadPool.execute(this);
        }
        running = true;
        Utils.notifyPlatform(text, "File input started");
        notify();
    }

    public synchronized void pause() {
        running = false;
        notify();
        Utils.notifyPlatform(text, "File input paused");
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
