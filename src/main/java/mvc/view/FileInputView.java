package mvc.view;

import java.io.File;
import java.util.ArrayList;

import components.Pools;
import mvc.controller.LinkCruncher;
import mvc.controller.StartFileInput;
import mvc.model.Cruncher;
import mvc.model.Directory;
import mvc.model.FileInput;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;

/** @noinspection IntegerDivisionInFloatingPointContext*/
public class FileInputView {
	MainView mainView;
	Pane main;
	FileInput fileInput;
	ListView<Cruncher> linkedCrunchers;
	ListView<Directory> directories;
	ComboBox<Cruncher> availableCrunchers;
	Button linkCruncher;
	Button unlinkCruncher;
	Button addDirectory;
	Button removeDirectory;
	Button start;
	Button removeDiskInput;
	Text status;

	public FileInputView(FileInput fileInput, MainView mainView) {
		this.mainView = mainView;
		this.fileInput = fileInput;

		main = new VBox();
		main.getChildren().add(new Text(fileInput.toString() + ": " + fileInput.getDisk().toString()));
		VBox.setMargin(main.getChildren().get(0), new Insets(0, 0, 10, 0));
		main.getChildren().add(new Text("Crunchers:"));

		int width = 210;

		linkedCrunchers = new ListView<>();
		linkedCrunchers.setMinWidth(width);
		linkedCrunchers.setMaxWidth(width);
		linkedCrunchers.setMinHeight(150);
		linkedCrunchers.setMaxHeight(150);
		linkedCrunchers.getSelectionModel().selectedItemProperty().addListener(e -> updateUnlinkCruncherButtonEnabled());
		main.getChildren().add(linkedCrunchers);

		availableCrunchers = new ComboBox<>();
		availableCrunchers.setMinWidth(width / 2 - 10);
		availableCrunchers.setMaxWidth(width / 2 - 10);
		availableCrunchers.getSelectionModel().selectedItemProperty().addListener(e -> updateLinkCruncherButtonEnabled());

		linkCruncher = new Button("Link cruncher");
		linkCruncher.setOnAction(new LinkCruncher(this));
		linkCruncher.setMinWidth(width / 2 - 10);
		linkCruncher.setMaxWidth(width / 2 - 10);
		linkCruncher.setDisable(true);

		HBox hBox = new HBox();
		hBox.getChildren().addAll(availableCrunchers, linkCruncher);
		HBox.setMargin(availableCrunchers, new Insets(0, 20, 0, 0));
		VBox.setMargin(hBox, new Insets(5, 0, 0, 0));
		main.getChildren().add(hBox);

		unlinkCruncher = new Button("Unlink cruncher");
		unlinkCruncher.setOnAction(e -> unlinkCruncher(linkedCrunchers.getSelectionModel().getSelectedItem()));
		unlinkCruncher.setMinWidth(width);
		unlinkCruncher.setMaxWidth(width);
		unlinkCruncher.setDisable(true);
		VBox.setMargin(unlinkCruncher, new Insets(5, 0, 0, 0));
		main.getChildren().add(unlinkCruncher);

		Text dirTitle = new Text("Dirs:");
		main.getChildren().add(dirTitle);
		VBox.setMargin(dirTitle, new Insets(10, 0, 0, 0));

		directories = new ListView<>();
		directories.setMinWidth(width);
		directories.setMaxWidth(width);
		directories.setMinHeight(150);
		directories.setMaxHeight(150);
		directories.getSelectionModel().selectedItemProperty().addListener(e -> updateRemoveDirectoryButtonEnabled());
		main.getChildren().add(directories);

		addDirectory = new Button("Add dir");
		addDirectory.setOnAction(e -> addDirectory());
		addDirectory.setMinWidth(width / 2 - 10);
		addDirectory.setMaxWidth(width / 2 - 10);

		removeDirectory = new Button("Remove dir");
		removeDirectory.setOnAction(e -> removeDirectory(directories.getSelectionModel().getSelectedItem()));
		removeDirectory.setMinWidth(width / 2 - 10);
		removeDirectory.setMaxWidth(width / 2 - 10);
		removeDirectory.setDisable(true);

		hBox = new HBox();
		hBox.getChildren().addAll(addDirectory, removeDirectory);
		HBox.setMargin(addDirectory, new Insets(0, 20, 0, 0));
		VBox.setMargin(hBox, new Insets(5, 0, 0, 0));
		main.getChildren().add(hBox);

		start = new Button("Start");
		start.setOnAction(new StartFileInput(fileInput.getName()));
		start.setMinWidth(width);
		start.setMaxWidth(width);
		VBox.setMargin(start, new Insets(15, 0, 0, 0));
		main.getChildren().add(start);

		removeDiskInput = new Button("Remove disk input");
		removeDiskInput.setOnAction(e -> removeDiskInput());
		removeDiskInput.setMinWidth(width);
		removeDiskInput.setMaxWidth(width);
		VBox.setMargin(removeDiskInput, new Insets(5, 0, 0, 0));
		main.getChildren().add(removeDiskInput);

		status = new Text("Idle");
		VBox.setMargin(status, new Insets(5, 0, 0, 0));
		main.getChildren().add(status);
	}

	private void updateRemoveDirectoryButtonEnabled() {
		removeDirectory.setDisable(directories.getSelectionModel().getSelectedItem() == null);
	}

	public Pane getFileInputView() {
		return main;
	}
	
	public void updateLinkCruncherButtonEnabled() {
		Cruncher cruncher =  availableCrunchers.getSelectionModel().getSelectedItem();
		if(cruncher != null) {
			for(Cruncher linkedCruncher: linkedCrunchers.getItems()) {
				if(cruncher == linkedCruncher) {
					linkCruncher.setDisable(true);
					return;
				}
			}
			linkCruncher.setDisable(false);
		} else {
			linkCruncher.setDisable(true);
		}
	}
	
	private void updateUnlinkCruncherButtonEnabled() {
		unlinkCruncher.setDisable(linkedCrunchers.getSelectionModel().getSelectedItem() == null);
	}
	

	public void updateAvailableCrunchers(ArrayList<Cruncher> crunchers) {
		availableCrunchers.getItems().clear();
		if (crunchers == null || crunchers.size() == 0) {
			return;
		}
		availableCrunchers.getItems().addAll(crunchers);
		availableCrunchers.getSelectionModel().select(0);
	}


	
	public void removeLinkedCruncher(Cruncher cruncher) {
		linkedCrunchers.getItems().remove(cruncher);
		updateLinkCruncherButtonEnabled();
	}

	private void unlinkCruncher(Cruncher cruncher) {
		linkedCrunchers.getItems().remove(cruncher);
		updateLinkCruncherButtonEnabled();
	}

	private void addDirectory() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(fileInput.getDisk().getDirectory());
		File fileDirectory = directoryChooser.showDialog(mainView.getStage());
		if (fileDirectory != null && fileDirectory.exists() && fileDirectory.isDirectory()) {
			for(Directory directory: directories.getItems()) {
				if(directory.toString().equals(fileDirectory.getPath())) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Error");
					alert.setHeaderText("Directory: " + fileDirectory.getPath() + " is already added.");
					alert.setContentText(null);
					alert.showAndWait();
					return;
				}
			}
			Directory directory = new Directory(fileDirectory);
			directories.getItems().add(directory);
			Pools.getInstance().addDirectory(fileInput.getName(), directory);
		}
	}

	private void removeDirectory(Directory directory) {
		directories.getItems().remove(directory);
	}
	/*
	private void start() {
		
	}


	private void linkCruncher(Cruncher cruncher) {
		linkedCrunchers.getItems().add(cruncher);
		updateLinkCruncherButtonEnabled();
	}

	 */

	private void removeDiskInput() {
		mainView.removeFileInputView(this);
	}
	
	public void setStatus(Text text) {
		main.getChildren().remove(status);
		main.getChildren().add(text);
	}
	
	public FileInput getFileInput() {
		return fileInput;
	}

	public ListView<Cruncher> getLinkedCrunchers() {
		return linkedCrunchers;
	}

	public ComboBox<Cruncher> getAvailableCrunchers() {
		return availableCrunchers;
	}

}
