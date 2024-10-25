package com.example.sudokufpoe.View;
import com.example.sudokufpoe.SudokuApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class SudokuStage extends Stage{
    public SudokuStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(SudokuApplication.class.getResource("hello-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        setScene(scene);
        setTitle("Sudoku");
        Image icon = new Image(String.valueOf(getClass().getResource("/com/example/images/favico.png")));
        this.getIcons().add(icon);
        setResizable(false);
        show();
    }

    private static class SudokuStageHolder{
        private static SudokuStage INSTANCE;
    }

    public static SudokuStage getInstance() throws IOException{
        SudokuStage.SudokuStageHolder.INSTANCE =
                SudokuStage.SudokuStageHolder.INSTANCE != null ?
                        SudokuStage.SudokuStageHolder.INSTANCE :
                        new SudokuStage();

        return SudokuStage.SudokuStageHolder.INSTANCE;
    }

    public static void deleteInstance(){
        SudokuStage.SudokuStageHolder.INSTANCE.close();
        SudokuStage.SudokuStageHolder.INSTANCE = null;
    }
}
