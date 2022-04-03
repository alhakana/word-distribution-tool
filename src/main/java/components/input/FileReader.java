package components.input;

import components.Input;
import components.Pools;
import components.cruncher.CounterCruncherComp;
import mvc.model.FileInput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Callable;

public class FileReader implements Runnable {

    private File file;
    private FileInputComp fileInputComp;

    public FileReader(File file, FileInputComp fileInputComp) {
        this.file = file;
        this.fileInputComp = fileInputComp;
    }

    @Override
    public void run() {
        synchronized (fileInputComp.getDisc()) {
            FileInputStream fileInputStream = null;

            try {
                fileInputStream = new FileInputStream(file);
                byte[] textByte = new byte[(int) file.length()];
                fileInputStream.read(textByte);

                String text = new String(textByte, StandardCharsets.US_ASCII);
                Input input = new Input(file.getName(), text);
                fileInputStream.close();

                fileInputComp.sendInput(input);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
