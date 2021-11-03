package edu.vortx3735.MyPlugin.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class StepListCell extends ListCell<StepListCellModel> {

    private ListView<StepListCellModel> _listView;
    private StepListCellModel _model;
    private IStepChangeListener _changeListener;

    @FXML
    private TextField textField;

    @FXML
    private Button addButton;

    @FXML
    private Button upButton;

    @FXML
    private Button downButton;

    @FXML
    private Button deleteButton;

    @FXML
    private GridPane gridPane;

    private FXMLLoader mLLoader;

    public StepListCell(ListView<StepListCellModel> listView, IStepChangeListener changeListener) {
        _listView = listView;
        _changeListener = changeListener;
    }

    @Override
    protected void updateItem(StepListCellModel model, boolean empty) {
        super.updateItem(model, empty);
        _model = model;

        if(empty || model == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("StepListCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            textField.setText(model.step);
            addButton.setText("+ Row");
            upButton.setText("^");
            downButton.setText("v");
            deleteButton.setText("Delete");

            setText(null);
            setGraphic(gridPane);
        }

    }

	@FXML
	protected void onTextChanged(ActionEvent e)
	{
        System.out.println("onTextChanged");
        var list = _listView.getItems();
        var index = list.indexOf(_model);
        list.set(index, new StepListCellModel(textField.getText()));
        _changeListener.onStepChanged();
	}

	@FXML
	protected void onAddRow(ActionEvent e)
	{
        System.out.println("onAddRow");
        var list = _listView.getItems();
        var index = list.indexOf(_model);
        list.add(index + 1, new StepListCellModel(""));
        _changeListener.onStepChanged();
	}

	@FXML
	protected void onDeleteRow(ActionEvent e)
	{
        System.out.println("onDeleteRow");
        var list = _listView.getItems();
        var index = list.indexOf(_model);
        list.remove(index);
        _changeListener.onStepChanged();
	}

	@FXML
	protected void onMoveRowUp(ActionEvent e)
	{
        System.out.println("onMoveRowUp");
        var list = _listView.getItems();
        var index = list.indexOf(_model);
        if(index == 0) {
            return;
        }
        var rowAbove = list.get(index - 1);
        list.set(index - 1, _model);
        list.set(index, rowAbove);
        _changeListener.onStepChanged();
	}

	@FXML
	protected void onMoveRowDown(ActionEvent e)
	{
        System.out.println("onMoveRowDown");
        var list = _listView.getItems();
        var index = list.indexOf(_model);
        if(index >= list.size() - 1) {
            return;
        }
        var rowBelow = list.get(index + 1);
        list.set(index + 1, _model);
        list.set(index, rowBelow);
        _changeListener.onStepChanged();
	}
}