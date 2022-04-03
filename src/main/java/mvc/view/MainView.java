package mvc.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import components.Pools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import mvc.app.Config;
import mvc.controller.AddCruncher;
import mvc.controller.AddFileInput;
import mvc.controller.GetSingleResult;
import mvc.controller.SumResults;
import mvc.model.Cruncher;
import mvc.model.Disk;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mvc.model.FileOutput;

public class MainView {

	private Stage stage;
	private ComboBox<Disk> disks;
	private HBox left;
	private VBox vBoxFileInput, vBoxCruncher;
	private Pane right;
	private ListView<FileOutput> results;
	private ObservableList<FileOutput> resultList;
	private Button addFileInput, singleResult, sumResult;
	private ArrayList<FileInputView> fileInputViews;
	private LineChart<Number, Number> lineChart;
	private ArrayList<Cruncher> availableCrunchers;
	private ProgressBar progressBar;

	public void initMainView(BorderPane borderPane, Stage stage) {
		this.stage = stage;

		fileInputViews = new ArrayList<>();
		availableCrunchers = new ArrayList<>();

		left = new HBox();
		borderPane.setLeft(left);

		initFileInput();
		initCruncher();

		initCenter(borderPane);
		initRight(borderPane);
	}

	private void initFileInput() {
		vBoxFileInput = new VBox();

		vBoxFileInput.getChildren().add(new Text("File inputs:"));
		VBox.setMargin(vBoxFileInput.getChildren().get(0), new Insets(0, 0, 10, 0));

		disks = new ComboBox<>();
		disks.getSelectionModel().selectedItemProperty().addListener(e -> updateEnableAddFileInput());
		disks.setMinWidth(120);
		disks.setMaxWidth(120);
		vBoxFileInput.getChildren().add(disks);

		addFileInput = new Button("Add FileInput");
		addFileInput.setOnAction(new AddFileInput(this));
		VBox.setMargin(addFileInput, new Insets(5, 0, 10, 0));
		addFileInput.setMinWidth(120);
		addFileInput.setMaxWidth(120);
		vBoxFileInput.getChildren().add(addFileInput);

		int width = 210;

		VBox divider = new VBox();
		divider.getStyleClass().add("divider");
		divider.setMinWidth(width);
		divider.setMaxWidth(width);
		vBoxFileInput.getChildren().add(divider);
		VBox.setMargin(divider, new Insets(0, 0, 15, 0));

		Insets insets = new Insets(10);
		ScrollPane scrollPane = new ScrollPane(vBoxFileInput);
		scrollPane.setMinWidth(width + 35);
		vBoxFileInput.setPadding(insets);
		vBoxFileInput.getChildren().add(scrollPane);

		left.getChildren().add(scrollPane);

		
		try {
			String[] disksArray = Config.getProperty("disks").split(";");
			for (String disk : disksArray) {
				File file = new File(disk);
				if(!file.exists() || !file.isDirectory()) {
					throw new Exception("Bad directory path");
				}
				disks.getItems().add(new Disk(file));
			}
			if (disksArray.length > 0) {
				disks.getSelectionModel().select(0);
			}
		} catch (Exception e) {
			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Closing");
				alert.setHeaderText("Bad config disks");
				alert.setContentText(null);

				alert.showAndWait();
				System.exit(0);
			});
		}

		updateEnableAddFileInput();
	}

	private void initCruncher() {
		vBoxCruncher = new VBox();

		Text text = new Text("Crunchers");
		vBoxCruncher.getChildren().add(text);
		VBox.setMargin(text, new Insets(0, 0, 5, 0));

		Button addCruncher = new Button("Add cruncher");
		addCruncher.setOnAction(new AddCruncher(this));
		vBoxCruncher.getChildren().add(addCruncher);
		VBox.setMargin(addCruncher, new Insets(0, 0, 15, 0));

		int width = 110;

		Insets insets = new Insets(10);
		ScrollPane scrollPane = new ScrollPane(vBoxCruncher);
		scrollPane.setMinWidth(width + 35);
		vBoxCruncher.setPadding(insets);
		left.getChildren().add(scrollPane);
	}

	private void initCenter(BorderPane borderPane) {
		Pane center = new HBox();

		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Bag of words");
		yAxis.setLabel("Frequency");
		lineChart = new LineChart<>(xAxis, yAxis);
		lineChart.setMinWidth(700);
		lineChart.setMinHeight(600);
		center.getChildren().add(lineChart);

		borderPane.setCenter(center);
	}

	private void initRight(BorderPane borderPane) {
		right = new VBox();
		right.setPadding(new Insets(10));
		right.setMaxWidth(200);

		results = new ListView<>();
		resultList = FXCollections.observableArrayList();
		results.setItems(resultList);
		Pools.getInstance().addObservable(resultList);
		right.getChildren().add(results);
		VBox.setMargin(results, new Insets(0, 0, 10, 0));
		results.getSelectionModel().selectedItemProperty().addListener(e -> updateResultButtons());
		results.getSelectionModel().selectedIndexProperty().addListener(e -> updateResultButtons());
		results.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		singleResult = new Button("Single result");
		singleResult.setOnAction(new GetSingleResult(this));
		singleResult.setDisable(true);
		right.getChildren().add(singleResult);
		VBox.setMargin(singleResult, new Insets(0, 0, 5, 0));

		sumResult = new Button("Sum results");
		sumResult.setDisable(true);
		sumResult.setOnAction(new SumResults());
		right.getChildren().add(sumResult);
		VBox.setMargin(sumResult, new Insets(0, 0, 10, 0));

		progressBar = new ProgressBar();

		borderPane.setRight(right);
	}

	public void updateEnableAddFileInput() {
		Disk disk = disks.getSelectionModel().getSelectedItem();
		if (disk != null) {
			for (FileInputView fileInputView : fileInputViews) {
				if (fileInputView.getFileInput().getDisk() == disk) {
					addFileInput.setDisable(true);
					return;
				}
			}
			addFileInput.setDisable(false);
		} else {
			addFileInput.setDisable(true);
		}
	}

	public void updateResultButtons() {
		if (results.getSelectionModel().getSelectedItems() == null
				|| results.getSelectionModel().getSelectedItems().size() == 0) {
			singleResult.setDisable(true);
			sumResult.setDisable(true);
		} else if (results.getSelectionModel().getSelectedItems().size() == 1) {
			singleResult.setDisable(false);
			sumResult.setDisable(true);
		} else {
			singleResult.setDisable(true);
			sumResult.setDisable(false);
		}
	}



	public void removeFileInputView(FileInputView fileInputView) {
		vBoxFileInput.getChildren().remove(fileInputView.getFileInputView());
		fileInputViews.remove(fileInputView);
		updateEnableAddFileInput();
	}

	public void updateCrunchers(ArrayList<Cruncher> crunchers) {
		for (FileInputView fileInputView : fileInputViews) {
			fileInputView.updateAvailableCrunchers(crunchers);
		}
		this.availableCrunchers = crunchers;
	}

	public Stage getStage() {
		return stage;
	}



	public void stopCrunchers() {
		
	}

	public void stopFileInputs() {
		
	}

	public void removeCruncher(CruncherView cruncherView) {
		for (FileInputView fileInputView : fileInputViews) {
			fileInputView.removeLinkedCruncher(cruncherView.getCruncher());
		}
		availableCrunchers.remove(cruncherView.getCruncher());
		updateCrunchers(availableCrunchers);
		vBoxCruncher.getChildren().remove(cruncherView.getCruncherView());
	}

	public Pane getRight() {
		return right;
	}

	public ComboBox<Disk> getComboBoxDisks() {
		return disks;
	}

	public VBox getVBoxFileInput() {
		return vBoxFileInput;
	}

	public VBox getVBoxCruncher() {
		return vBoxCruncher;
	}
	public ArrayList<FileInputView> getFileInputViews() {
		return fileInputViews;
	}

	public ArrayList<Cruncher> getAvailableCrunchers() {
		return availableCrunchers;
	}

	public ListView<FileOutput> getResults() {
		return results;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void updateChart(Map<Number, Number> resultUpdate, String fileName) {
		lineChart.getData().clear();
		XYChart.Series<Number, Number> xy = new XYChart.Series<>();
		xy.setName(fileName);
		resultUpdate.forEach((x, y) -> {
			xy.getData().add(new XYChart.Data<>(x, y));
		});

		lineChart.getData().add(xy);
	}

	public void removeAndResetProgressBar() {
		right.getChildren().remove(progressBar);
		progressBar = new ProgressBar();
	}

	/*
	public void addFileInput(FileInput fileInput) {
		FileInputView fileInputView = new FileInputView(fileInput, this);
		this.vBoxFileInput.getChildren().add(fileInputView.getFileInputView());
		VBox.setMargin(fileInputView.getFileInputView(), new Insets(0, 0, 30, 0));
		fileInputView.getFileInputView().getStyleClass().add("file-input");
		fileInputViews.add(fileInputView);
		if (availableCrunchers != null) {
			fileInputView.updateAvailableCrunchers(availableCrunchers);
		}
		updateEnableAddFileInput();
	}

	private void addCruncher() {
		TextInputDialog dialog = new TextInputDialog("1");
		dialog.setTitle("Add cruncher");
		dialog.setHeaderText("Enter cruncher arity");

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(res -> {
			try {
				int arity = Integer.parseInt(res);
				for (Cruncher cruncher : availableCrunchers) {
					if (cruncher.getArity() == arity) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Error");
						alert.setHeaderText("Cruncher with this arity already exists.");
						alert.setContentText(null);
						alert.showAndWait();
						return;
					}
				}
				Cruncher cruncher = new Cruncher(arity);
				CruncherView cruncherView = new CruncherView(this, cruncher);
				this.vBoxCruncher.getChildren().add(cruncherView.getCruncherView());
				availableCrunchers.add(cruncher);
				updateCrunchers(availableCrunchers);
			} catch (NumberFormatException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Wrong input");
				alert.setHeaderText("Arity must be a number");
				alert.showAndWait();
			}
		});
	}

		private void getSingleResult() {

	}

	private void sumResults() {

	}
	*/
}
