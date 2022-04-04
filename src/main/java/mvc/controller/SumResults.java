package mvc.controller;

import components.Pools;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import mvc.model.FileOutput;
import mvc.view.MainView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class SumResults implements EventHandler<ActionEvent> {

    private final MainView mainView;

    public SumResults(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        TextInputDialog sumNameDialog = new TextInputDialog("sum");
        sumNameDialog.setHeaderText("Enter name");

        sumNameDialog.showAndWait().ifPresent(name -> {
            if (mainView.getResults().getItems().contains(new FileOutput(name))) {
                new Alert(Alert.AlertType.ERROR, "Name of the sum already exists").show();
                return;
            }

            List<FileOutput> files = new ArrayList<>(mainView.getResults().getSelectionModel().getSelectedItems());
            double progressUpdate = 1.0/files.size();

            Platform.runLater(() -> mainView.getRight().getChildren().add(mainView.getProgressBar()));

            Future<Map<String,Integer>> newMap = Pools.getInstance().getOutputThreadPool().submit(() -> {
                HashMap<String, Integer> aggregate = new HashMap<>();

                for (FileOutput fileOutput : files) {
                    Map<String, Integer> fileBagOfWords = Pools.getInstance().getOutput().take(fileOutput.getName());

                    fileBagOfWords.keySet().iterator().forEachRemaining(key -> {
                        if(aggregate.containsKey(key))
                            aggregate.put(key, aggregate.get(key)+fileBagOfWords.get(key));
                        else aggregate.put(key, fileBagOfWords.get(key));

                    });

                    Platform.runLater(() -> mainView.getProgressBar().
                            setProgress(mainView.getProgressBar().getProgress() + progressUpdate));
                }

                Platform.runLater(mainView::removeAndResetProgressBar);
                return aggregate;
            });

            Pools.getInstance().getOutput().addResult(name, newMap);

        });


    }
}
