package components.input;

import components.cruncher.CounterCruncherComp;
import java.io.File;
import java.util.List;

public class FileReader implements Runnable {

    private File file;
    private String disc;
    private List<CounterCruncherComp> crunchers;

    public FileReader(File file, String disc, List<CounterCruncherComp> crunchers) {
        this.file = file;
        this.disc = disc;
        this.crunchers = crunchers;
    }

    @Override
    public void run() {
        synchronized (disc) {

        }
    }

}
