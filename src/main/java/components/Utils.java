package components;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import mvc.model.FileOutput;

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

    public static void updateList(ObservableList<FileOutput> observableList, FileOutput fileOutput) {
        Platform.runLater(() -> {
            if (!observableList.contains(fileOutput))
                observableList.add(fileOutput);
        });
    }

    public static void removeAndUpdateList(ObservableList<FileOutput> observableList, FileOutput fileOutput) {
        Platform.runLater(() -> {
            observableList.remove(fileOutput);
            fileOutput.setDone(true);
            observableList.add(fileOutput);
        });
    }

    public static void closeApp() {
        new Thread(() -> Pools.getInstance().shutDownPools()).start();
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Memory error :(").showAndWait());
    }
}
