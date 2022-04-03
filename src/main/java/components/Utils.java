package components;

import javafx.application.Platform;
import javafx.scene.text.Text;

public class Utils {

    public static void notifyPlatformAppend(Text text, String string) {
        synchronized (text) {
            Platform.runLater(() -> text.setText(text.getText() + "\n" + string));
        }
    }

    public static void notifyPlatformReset(Text text, String string) {
        synchronized (text) {
            Platform.runLater(() -> text.setText(string));
        }
    }

    public static void notifyPlatformReplace(Text text, String string, int arity) {
        synchronized (text) {
            Platform.runLater(() -> text.setText(text.getText().replace(string, string+"-arity"+arity)));
        }
    }
}
