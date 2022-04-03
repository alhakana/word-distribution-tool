package mvc.controller;

import components.Pools;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import mvc.app.Config;
import mvc.view.MainView;

import java.util.*;

public class GetSingleResult implements EventHandler<ActionEvent> {

    private MainView mainView;
    private int K = Integer.parseInt(Config.getProperty("sort_progress_limit"));
    private int i;

    public GetSingleResult(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        String fileName = mainView.getResults().getSelectionModel().getSelectedItem().getName();
        Map<String, Integer> result = Pools.getInstance().getOutput().poll(fileName);

        if (result == null) {
            new Alert(Alert.AlertType.ERROR, "Result isn't ready yet.").show();
            return;
        }

        double progressBarUpdateValue = 1/((double)result.size()*Math.log(result.size())/K);
        Pools.getInstance().getOutputThreadPool().execute(() -> {
            Platform.runLater(() -> mainView.getRight().getChildren().add(mainView.getProgressBar()));

            i = 0;
            Map<String, Integer> sortedMap = new HashMap<>(result);
            List<Map.Entry<String, Integer>> list = new ArrayList<>(result.entrySet());


            list.sort(new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> item1, Map.Entry<String, Integer> item2) {
                    i++;
                    if(i % K == 0) {
                        Platform.runLater(() -> mainView.getProgressBar().
                                setProgress(mainView.getProgressBar().getProgress() + progressBarUpdateValue));
                    }
                    return -Integer.compare(item1.getValue(), item2.getValue());
                }
            });

            i = 0;

            Map<Number, Number> resultUpdate = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : list) {
                resultUpdate.put(i, entry.getValue());
                i++;
                if (i == 100) {
                    break;
                }
            }

            Platform.runLater(() -> {
                mainView.removeAndResetProgressBar();
                mainView.updateChart(resultUpdate, fileName);
            });
        });

    }
}
