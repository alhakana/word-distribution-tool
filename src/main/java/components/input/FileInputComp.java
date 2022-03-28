package components.input;

import components.cruncher.CounterCruncherComp;
import mvc.app.Config;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

public class FileInputComp implements Runnable {

    private ExecutorService threadPool;
    private final String disc;

    private List<CounterCruncherComp> crunchers;
    private List<File> directories;
    private Map<File, Long> lastModify;

    private volatile boolean started = false;
    private volatile boolean running = false;
    private volatile boolean quit = false;

    public FileInputComp(ExecutorService threadPool, String disc) {
        this.disc = disc;
        directories = new CopyOnWriteArrayList<>();
        this.threadPool = threadPool;
        lastModify = new ConcurrentHashMap<>();
        crunchers = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        while(!quit) {
            if (started && running) {
                searchDirectories();
                sleep(Integer.parseInt(Config.getProperty("file_input_sleep_time")));
            } else
                sleep(0);
        }
    }

    public void searchDirectories() {
        directories.iterator().forEachRemaining(directory -> searchFiles(directory));
    }

    private void searchFiles(File directory) {
        List<File> files = new ArrayList<>();
        files = Arrays.asList(directory.listFiles());

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
        Long lastModified = lastModify.get(file);
        if (lastModified == null || !lastModified.equals(file.lastModified())) {
            lastModify.put(file, file.lastModified());
            threadPool.execute(new FileReader(file, disc, crunchers));
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


    private void sleep(int time) {
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

    }

}
