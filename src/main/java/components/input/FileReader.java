package components.input;

import components.Input;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileReader implements Runnable {

    private final File file;
    private final FileInputComp fileInputComp;

    public FileReader(File file, FileInputComp fileInputComp) {
        this.file = file;
        this.fileInputComp = fileInputComp;
    }

    /** @noinspection ResultOfMethodCallIgnored*/
    @Override
    public void run() {
        synchronized (fileInputComp.getDisc()) {
            FileInputStream fileInputStream;

            try {
                fileInputStream = new FileInputStream(file);
                byte[] textByte = new byte[(int) file.length()];
                fileInputStream.read(textByte);

                String text = new String(textByte, StandardCharsets.US_ASCII);
                Input input = new Input(file.getName(), text);
                fileInputStream.close();

                fileInputComp.sendInput(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
