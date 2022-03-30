package mvc.controller;

import components.Pools;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import mvc.model.Cruncher;
import mvc.view.FileInputView;

public class LinkCruncher implements EventHandler<ActionEvent> {

    public FileInputView fileInputView;

    public LinkCruncher(FileInputView fileInputView) {
        this.fileInputView = fileInputView;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        Cruncher cruncher = fileInputView.getAvailableCrunchers().getSelectionModel().getSelectedItem();
        fileInputView.getLinkedCrunchers().getItems().add(cruncher);
        fileInputView.updateLinkCruncherButtonEnabled();
        Pools.getInstance().linkCruncher(fileInputView.getFileInput().getName(), cruncher.getArity());
    }
}
