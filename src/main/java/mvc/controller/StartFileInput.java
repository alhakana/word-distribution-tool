package mvc.controller;

import components.Pools;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class StartFileInput implements EventHandler<ActionEvent> {

    private String fileInputName;

    public  StartFileInput(String fileInputName) {
        this.fileInputName = fileInputName;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        Button btnStartOrPause = (Button) actionEvent.getSource();
        if (btnStartOrPause.getText().equals("Start")) {
            Pools.getInstance().startInputFile(fileInputName);
            btnStartOrPause.setText("Pause");
        } else {
            Pools.getInstance().pauseInputFile(fileInputName);
            btnStartOrPause.setText("Start");
        }
    }
}
