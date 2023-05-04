package com.myproj.dendytanks;

import com.myproj.dendytanks.core.main.TanksMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class MenuController {

    @FXML
    private Label choiceLabel;

    @FXML
    private Text titleLabel;

    @FXML
    private Button quitButton;

    @FXML
    private Button playButton;

    @FXML
    private ChoiceBox<String> choiceLevelBox;
    @FXML
    private CheckBox redactPermission;

////////////////////////////////////////////////////////////////Привязка к Main классу игры
    int numOfLevel = 1; //Выбранный уровень
    boolean isRedactable = false;
    TanksMain tanks = new TanksMain();
    Stage stage;

////////////////////////////////////////////////////////////////
    @FXML
    void play() {

        tanks.start(numOfLevel, isRedactable);
    }

    @FXML
    public void quit() {
        tanks.stop();
        stage.close();

    }
    public void initialize(){

        ObservableList<String> levelsList = FXCollections.observableArrayList("Level 1", "Level 2", "Level 3");
        choiceLevelBox.setItems(levelsList);
        choiceLevelBox.setValue(levelsList.get(0));

        choiceLevelBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String levelString = choiceLevelBox.getSelectionModel().getSelectedItem();
                choiceLabel.setText(levelString);

                numOfLevel = Integer.parseInt(levelString.substring(levelString.length() - 1));
            }
        });

        redactPermission.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                isRedactable = redactPermission.isSelected();
            }
        });

    }

    public void shareStage(Stage stage){
        this.stage = stage;
    }




}
