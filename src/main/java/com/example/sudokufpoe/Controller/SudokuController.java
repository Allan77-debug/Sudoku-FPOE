package com.example.sudokufpoe.Controller;

import com.example.sudokufpoe.Model.SudokuGrid;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Queue;

public class SudokuController {
    @FXML
    private TextField[][] cells;
    private SudokuGrid model;
    private ArrayList<TextField> cellList;

    public SudokuController() {
        model = new SudokuGrid();
        cellList = new ArrayList<>();
    }

    @FXML
    public void initialize() {
        cells = new TextField[6][6];
        // Inicializar y agregar TextFields al GridPane basado en FXML

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                TextField cell = new TextField(); // O agrega el TextField del FXML
                cell.setOnMouseClicked(e -> onCellClick(row, col));
                cell.setOnKeyTyped(e -> handleKeyPress(e.getCharacter(), row, col));
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
