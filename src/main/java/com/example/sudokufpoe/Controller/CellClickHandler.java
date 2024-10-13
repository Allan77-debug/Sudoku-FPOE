package com.example.sudokufpoe.Controller;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class CellClickHandler implements EventHandler<MouseEvent> {
    private final SudokuController controller;
    private final int row;
    private final int col;

    public CellClickHandler(SudokuController controller, int row, int col) {
        this.controller = controller;
        this.row = row;
        this.col = col;
    }

    @Override
    public void handle(MouseEvent event) {
        controller.onCellClick(row, col);
    }
}
