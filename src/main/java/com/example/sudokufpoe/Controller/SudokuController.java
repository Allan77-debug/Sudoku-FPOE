package com.example.sudokufpoe.Controller;

import com.example.sudokufpoe.Model.SudokuGrid;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Queue;

public class SudokuController {
    @FXML
    private TextField[][] cells;
    private final SudokuGrid model;
    private ArrayList<TextField> cellList;
    @FXML
    private TextField cell_0_0, cell_0_1, cell_0_2, cell_0_3, cell_0_4, cell_0_5;
    @FXML
    private TextField cell_1_0, cell_1_1, cell_1_2, cell_1_3, cell_1_4, cell_1_5;
    @FXML
    private TextField cell_2_0, cell_2_1, cell_2_2, cell_2_3, cell_2_4, cell_2_5;
    @FXML
    private TextField cell_3_0, cell_3_1, cell_3_2, cell_3_3, cell_3_4, cell_3_5;
    @FXML
    private TextField cell_4_0, cell_4_1, cell_4_2, cell_4_3, cell_4_4, cell_4_5;
    @FXML
    private TextField cell_5_0, cell_5_1, cell_5_2, cell_5_3, cell_5_4, cell_5_5;

    public SudokuController() {
        model = new SudokuGrid();
        cellList = new ArrayList<>();
    }

    @FXML
    public void initialize() {
        cells = new TextField[][] {
                {cell_0_0, cell_0_1, cell_0_2, cell_0_3, cell_0_4, cell_0_5},
                {cell_1_0, cell_1_1, cell_1_2, cell_1_3, cell_1_4, cell_1_5},
                {cell_2_0, cell_2_1, cell_2_2, cell_2_3, cell_2_4, cell_2_5},
                {cell_3_0, cell_3_1, cell_3_2, cell_3_3, cell_3_4, cell_3_5},
                {cell_4_0, cell_4_1, cell_4_2, cell_4_3, cell_4_4, cell_4_5},
                {cell_5_0, cell_5_1, cell_5_2, cell_5_3, cell_5_4, cell_5_5}
        };

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                TextField cell = new TextField(); // O agrega el TextField del FXML
                int finalRow = row;
                int finalCol = col;
                cell.setOnMouseClicked(e -> onCellClick(finalRow, finalCol));
                cell.setOnKeyTyped(e -> handleKeyPress(e.getCharacter(), finalRow, finalCol));
                cells[row][col] = cell;
                cellList.add(cell);
            }
        }
    }

    private void updateCellView(int row, int col, int number) {
        TextField cell = cells[row][col];
        if (number == 0) {
            cell.clear(); // Visualmente vacía la celda si el número es 0
        } else {
            cell.setText(String.valueOf(number)); // Muestra el número ingresado
        }
    }

    // Método para manejar la selección de celdas y edición
    public void onCellClick(int row, int col) {
        TextField selectedCell = cells[row][col];
        selectedCell.requestFocus(); // Seleccionar la celda
        selectedCell.setOnKeyTyped(event -> handleKeyPress(event.getCharacter(), row, col));
    }

    private void handleKeyPress(String key, int row, int col) {
        try {
            int number = Integer.parseInt(key);
            if (number >= 1 && number <= 6) {
                model.setNumber(row, col, number);
                cells[row][col].setText(String.valueOf(number));
            }
        } catch (NumberFormatException e) {
            // Si el carácter no es un número válido
            System.out.println("Número inválido ingresado.");
        }
    }

    public void clearCell(int row, int col) {
        model.clearNumber(row, col);
        cells[row][col].setText("");
    }

    public void undo() {
        model.undo();
        refreshView();
    }

    public void redo() {
        model.redo();
        refreshView();
    }

    private void refreshView() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                int number = model.getNumber(row, col);
                updateCellView(row, col, number);
            }
        }
    }

    public void showActionHistory() {
        Queue<String> actions = model.getActionHistory();
        while (!actions.isEmpty()) {
            String action = actions.poll();
            System.out.println(action); // Imprime la acción en la consola o muestra en la interfaz
        }
    }
}
