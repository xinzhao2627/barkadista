package com.example.barkadista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
//App.java runs the MainFrame where the main menu of the application is operational
public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //admin_room_selection
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("MainFrame-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 620);
        stage.setTitle("Barkadista");
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
