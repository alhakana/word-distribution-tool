package mvc.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import mvc.view.FileInputView;

import java.io.File;

public class LinkCruncher implements EventHandler<ActionEvent> {

    public FileInputView fileInputView;

    public LinkCruncher(FileInputView fileInputView) {
        this.fileInputView = fileInputView;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        fileInputView.getLinkedCrunchers().getItems().add(fileInputView.getAvailableCrunchers().getSelectionModel().getSelectedItem());
        fileInputView.updateLinkCruncherButtonEnabled();
    }
}
