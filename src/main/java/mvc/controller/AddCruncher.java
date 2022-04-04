package mvc.controller;

import components.Pools;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Text;
import mvc.model.Cruncher;
import mvc.view.CruncherView;
import mvc.view.MainView;
import java.util.ArrayList;
import java.util.Optional;

public class AddCruncher implements EventHandler<ActionEvent> {

    public MainView mainView;

    public AddCruncher(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        ArrayList<Cruncher> availableCrunchers = mainView.getAvailableCrunchers();
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Add cruncher");
        dialog.setHeaderText("Enter cruncher arity");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(res -> {
            try {
                int arity = Integer.parseInt(res);
                for (Cruncher cruncher : availableCrunchers) {
                    if (cruncher.getArity() == arity) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error");
                        alert.setHeaderText("Cruncher with this arity already exists.");
                        alert.setContentText(null);
                        alert.showAndWait();
                        return;
                    }
                }
                Cruncher cruncher = new Cruncher(arity);
                CruncherView cruncherView = new CruncherView(mainView, cruncher);
                Text text = new Text("Idle cruncher");
                cruncherView.setStatus(text);
//                System.out.println("ovde");
                Pools.getInstance().addCruncherComp(arity, text);

                mainView.getVBoxCruncher().getChildren().add(cruncherView.getCruncherView());
                availableCrunchers.add(cruncher);
                mainView.updateCrunchers(availableCrunchers);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Wrong input");
                alert.setHeaderText("Arity must be a number");
                alert.showAndWait();
            }
        });
    }
}
