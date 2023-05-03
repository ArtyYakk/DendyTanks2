package com.myproj.dendytanks;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


import java.io.IOException;

public class Application extends javafx.application.Application {
    MenuController menuController;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);

        stage.setResizable(false);
        stage.show();

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primaryScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primaryScreenBounds.getHeight() - stage.getHeight()) / 2);

       menuController = fxmlLoader.getController();
       menuController.shareStage(stage);

       stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
           @Override
           public void handle(WindowEvent windowEvent) {
               menuController.quit();
           }
       });


    }

    public MenuController getMenuController() {
        return menuController;
    }

    public static void main(String[] args) {
        launch();
    }


}