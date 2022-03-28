package mvc.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import mvc.model.FileInput;
import mvc.view.FileInputView;
import mvc.view.MainView;

public class AddFileInput implements EventHandler<ActionEvent> {

    private MainView mainView;

    public AddFileInput(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        FileInput fileInput = new FileInput(mainView.getComboBoxDisks().getSelectionModel().getSelectedItem());
        FileInputView fileInputView = new FileInputView(fileInput, mainView);

        mainView.getVBoxFileInput().getChildren().add(fileInputView.getFileInputView());
        VBox.setMargin(fileInputView.getFileInputView(), new Insets(0, 0, 30, 0));
        fileInputView.getFileInputView().getStyleClass().add("file-input");

        mainView.getFileInputViews().add(fileInputView);
        if (mainView.getAvailableCrunchers() != null) {
            fileInputView.updateAvailableCrunchers(mainView.getAvailableCrunchers());
        }
        mainView.updateEnableAddFileInput();
    }
}
