package edu.vortx3735.MyPlugin;

import edu.wpi.first.shuffleboard.api.data.types.NoneType;
import edu.wpi.first.shuffleboard.api.data.types.StringArrayType;
import edu.wpi.first.shuffleboard.api.widget.Description;
import edu.wpi.first.shuffleboard.api.widget.ParametrizedController;
import edu.wpi.first.shuffleboard.api.widget.SimpleAnnotatedWidget;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;

// The tutorial video shows the dataTypes as "Boolean.class" but it should be
// NoneType.class for widgets that don't support data binding.
@Description(dataTypes = { StringArrayType.class }, name = "Auto Steps")
@ParametrizedController(value = "AutoStepWidget.fxml")
@SuppressWarnings("all")
public class AutoStepWidget extends SimpleAnnotatedWidget<String[]> {

	@FXML
	private Pane _thePane;
	@FXML
	private Label selectedAuto;
	@FXML
	private ComboBox selector;
	@FXML
	private ListView steps;

	private ObservableList<String> stepList = FXCollections.observableArrayList("no auto selected");

	@FXML
	private void initialize() {
		selectedAuto.setText("No File Selected");
		try {
			var autoFolderPath = Paths.get(System.getProperty("user.home"), "/autos");
			if(!Files.exists(autoFolderPath)) {
				Files.createDirectory(autoFolderPath);
			}
			File folder = new File(autoFolderPath.toString());
			File[] listOfFiles = folder.listFiles();
			var fileCollection = FXCollections.observableArrayList(listOfFiles);
			selector.setItems(fileCollection);
			steps.setItems(stepList);
			steps.setEditable(true);
			steps.setCellFactory(TextFieldListCell.forListView());
			final var theWidget = this;
			dataProperty().addListener(new ChangeListener<String[]>() {
	
				@Override
				public void changed(ObservableValue<? extends String[]> arg0, String[] arg1, String[] arg2) {
					theWidget.setSteps(arg0.getValue());
				}
				
			});;
		} catch(Exception ex) {
			selectedAuto.setText("Failed to set up Widget: " + ex.getMessage());
		}
	}

	@Override
	public Pane getView() {
		return _thePane;
	}

	@FXML
	protected void onSelect(ActionEvent e)
	{
		File selectedFile = (File) selector.getValue();
		selectedAuto.setText(selectedFile.getName());
		var stepLines = readFile(selectedFile.getPath());
		var stepLinesArray = stepLines.toArray(String[]::new);
		setSteps(stepLinesArray); 
	}

	private void setSteps(String[] stepArray) {
		stepList.clear();
		stepList.addAll(stepArray);
		dataProperty().setValue(stepArray);
	}

	private ArrayList<String> readFile(String file) {
		ArrayList<String> lines = new ArrayList<>();

		try (BufferedReader input = new BufferedReader(new FileReader(file))) {
			for (String line = input.readLine(); line != null; line = input.readLine()) {
				lines.add(line);
			}
			input.close();
		} catch (Exception e) {

		}
		return lines;
	}
	
}
