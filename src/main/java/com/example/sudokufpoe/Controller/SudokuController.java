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
                final int finalRow = row;
                final int finalCol = col;

                // Asegurar que cada campo acepte solo números del 1 al 6
                cells[row][col].setOnKeyReleased(e -> {
                    String text = cells[finalRow][finalCol].getText();
                    if (text.length() > 1 || !text.matches("[1-6]?")) {
                        cells[finalRow][finalCol].setText(""); // Vaciar si la entrada es inválida
                    }
                });

                cells[row][col].setOnMouseClicked(e -> onCellClick(finalRow, finalCol));
            }
        }
    }

    @FXML
    private void handleUndo() {
        undo();
    }

    @FXML
    private void handleRedo() {
        redo();
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
        // Limpiar el carácter para manejar solo el primer carácter ingresado
        key = key.trim();

        // Verificar si el carácter ingresado es un número válido del 1 al 6
        try {
            int number = Integer.parseInt(key);
            if (number >= 1 && number <= 6) {
                model.setNumber(row, col, number);
                updateCellView(row, col, number); // Actualizar la vista para reflejar el cambio
            } else {
                System.out.println("Número fuera de rango. Ingresa un número entre 1 y 6.");
            }
        } catch (NumberFormatException e) {
            // Ignorar cualquier entrada que no sea un número válido
            System.out.println("Entrada inválida. Solo se permiten números del 1 al 6.");
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
