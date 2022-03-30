package components.input;

import components.Input;
import components.cruncher.CounterCruncherComp;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Callable;

public class FileReader implements Callable<Input> {

    private File file;
    private String disc;
    private List<CounterCruncherComp> crunchers;

    public FileReader(File file, String disc, List<CounterCruncherComp> crunchers) {
        this.file = file;
        this.disc = disc;
        this.crunchers = crunchers;
    }


    @Override
    public Input call() throws Exception {
        synchronized (disc) {
            System.out.println("ovvdeee");
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] text = new byte[(int) file.length()];
            fileInputStream.read(text);
            fileInputStream.close();

            System.out.println("ovde2");
            System.out.println(new String(text));
            System.out.println(new String(text, StandardCharsets.US_ASCII));

            return new Input(file.getName(), new String(text));
        }
    }
}
