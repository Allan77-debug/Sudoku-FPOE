package com.example.sudokufpoe;

import com.example.sudokufpoe.View.SudokuStage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SudokuApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        SudokuStage.getInstance();
    }

    public static void main(String[] args) {
        launch();
    }
}