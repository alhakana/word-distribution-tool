package components;

import javafx.application.Platform;
import javafx.scene.text.Text;

public class Utils {

    public static void notifyPlatform(Text text, String string) {
        Platform.runLater(() -> text.setText(string));
    }
}
