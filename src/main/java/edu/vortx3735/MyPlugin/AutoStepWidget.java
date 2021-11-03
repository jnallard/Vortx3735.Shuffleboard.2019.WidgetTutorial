package edu.vortx3735.MyPlugin;

import edu.vortx3735.MyPlugin.components.IStepChangeListener;
import edu.vortx3735.MyPlugin.components.StepListCell;
import edu.vortx3735.MyPlugin.components.StepListCellModel;
import edu.wpi.first.shuffleboard.api.data.types.NoneType;
import edu.wpi.first.shuffleboard.api.data.types.StringArrayType;
import edu.wpi.first.shuffleboard.api.widget.Description;
import edu.wpi.first.shuffleboard.api.widget.ParametrizedController;
import edu.wpi.first.shuffleboard.api.widget.SimpleAnnotatedWidget;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
	private TextField selectedAuto;
	@FXML
	private Button saveButton;
	@FXML
	private ComboBox selector;
	@FXML
	private ListView steps;

	private ObservableList<StepListCellModel> stepList = FXCollections.observableArrayList(new StepListCellModel("No steps set"));

	@FXML
	private void initialize() {
		selectedAuto.setText("not-selected.txt");
		saveButton.setText("Save File");
		try {
			final var theWidget = this;
			loadFiles();
			steps.setItems(stepList);
			steps.setEditable(true);
			steps.setCellFactory(param -> new StepListCell((ListView<StepListCellModel>) param, new IStepChangeListener(){
				@Override
				public void onStepChanged() {
					var changedStepModels = theWidget.stepList;
					var newSteps = new ArrayList<String>();
					for(var stepModel: changedStepModels) {
						newSteps.add(stepModel.step);
					}
					dataProperty().setValue(newSteps.toArray(String[]::new));
				}
			}));
			dataProperty().addListener(new ChangeListener<String[]>() {
	
				@Override
				public void changed(ObservableValue<? extends String[]> arg0, String[] arg1, String[] arg2) {
					theWidget.setSteps(arg0.getValue());
				}
				
			});
		} catch(Exception ex) {
			selectedAuto.setText("Failed to set up Widget: " + ex.getMessage());
		}
	}

	private void loadFiles() throws IOException {
		var autoFolderPath = Paths.get(System.getProperty("user.home"), "/autos");
		if(!Files.exists(autoFolderPath)) {
			Files.createDirectory(autoFolderPath);
		}
		File folder = new File(autoFolderPath.toString());
		File[] listOfFiles = folder.listFiles();
		var fileCollection = FXCollections.observableArrayList(listOfFiles);
		selector.setItems(fileCollection);
	}

	@Override
	public Pane getView() {
		return _thePane;
	}

	@FXML
	protected void onSelect(ActionEvent e)
	{
		File selectedFile = (File) selector.getValue();
		if(selectedFile == null) {
			return;
		}
		selectedAuto.setText(selectedFile.getName());
		var stepLines = readFile(selectedFile.getPath());
		var stepLinesArray = stepLines.toArray(String[]::new);
		setSteps(stepLinesArray); 
	}

	@FXML
	protected void onSave(ActionEvent e) throws IOException
	{
		System.out.println("on save");
		var file = writeFile(selectedAuto.getText(), stepList);
		selector.setValue(file);
		loadFiles();
	}

	private void setSteps(String[] stepArray) {
		stepList.clear();
		for(var step: stepArray) {
			stepList.add(new StepListCellModel(step));
		}
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

	private Path getAutoFolderPath() {
		return Paths.get(System.getProperty("user.home"), "/autos");
	}

	private File writeFile(String fileName, ObservableList<StepListCellModel> stepModels) {
		
		var path = Paths.get(getAutoFolderPath().toString(), fileName);
		try {
			FileWriter writer = new FileWriter(path.toString());
			writer.write("");
			for(var stepModel: stepModels) {
				writer.append(stepModel.step + "\r\n");
			}
			writer.close();
			System.out.println("Successfully wrote to the file.");
		  } catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		  }
		return path.toFile();
	}
	
}
