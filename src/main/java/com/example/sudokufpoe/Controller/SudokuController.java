package com.example.sudokufpoe.Controller;

import com.example.sudokufpoe.Model.SudokuNumberGenerator;
import com.example.sudokufpoe.Model.SudokuGrid;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Controller class for managing the Sudoku game.
 */
public class SudokuController {
    @FXML
    private final SudokuGrid model;
    private final ArrayList<TextField> cellList;
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


    /**
     * Constructor for SudokuController.
     */
    public SudokuController() {
        model = new SudokuGrid();
        cellList = new ArrayList<>();
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {

        cellList.add(cell_0_0); cellList.add(cell_0_1); cellList.add(cell_0_2); cellList.add(cell_0_3); cellList.add(cell_0_4); cellList.add(cell_0_5);
        cellList.add(cell_1_0); cellList.add(cell_1_1); cellList.add(cell_1_2); cellList.add(cell_1_3); cellList.add(cell_1_4); cellList.add(cell_1_5);
        cellList.add(cell_2_0); cellList.add(cell_2_1); cellList.add(cell_2_2); cellList.add(cell_2_3); cellList.add(cell_2_4); cellList.add(cell_2_5);
        cellList.add(cell_3_0); cellList.add(cell_3_1); cellList.add(cell_3_2); cellList.add(cell_3_3); cellList.add(cell_3_4); cellList.add(cell_3_5);
        cellList.add(cell_4_0); cellList.add(cell_4_1); cellList.add(cell_4_2); cellList.add(cell_4_3); cellList.add(cell_4_4); cellList.add(cell_4_5);
        cellList.add(cell_5_0); cellList.add(cell_5_1); cellList.add(cell_5_2); cellList.add(cell_5_3); cellList.add(cell_5_4); cellList.add(cell_5_5);

        fillWithRandomNumbers();

        for (int i = 0; i < cellList.size(); i++) {
            final int index = i;
            cellList.get(i).setOnKeyReleased(e -> {
                String text = cellList.get(index).getText();
                if (text.length() > 1 || !text.matches("[1-6]?")) {
                    cellList.get(index).setText("");
                }
            });

            cellList.get(i).setOnMouseClicked(e -> onCellClick(index));
        }
    }

    /**
     * Handles cell click and editing.
     *
     * @param index the index of the clicked cell
     */
    public void onCellClick(int index) {
        TextField selectedCell = cellList.get(index);
        selectedCell.requestFocus();
        selectedCell.setOnKeyTyped(event -> handleKeyPress(event.getCharacter(), index));
    }

    /**
     * Handles the undo action.
     */
    @FXML
    private void handleUndo() {
        undo();
    }

    /**
     * Handles the redo action.
     */
    @FXML
    private void handleRedo() {
        redo();
    }

    /**
     * Handles key press events.
     *
     * @param key the key pressed
     * @param index the index of the cell
     */
    private void handleKeyPress(String key, int index) {
        key = cleanInput(key);
        System.out.println(key + " " + key.matches("[1-6]"));
        if (key.matches("[1-6]")) {
            int number = Integer.parseInt(key);
            int row = index / 6;
            int col = index % 6;
            updateModelAndView(row, col, number);
        }
    }

    private void fillWithRandomNumbers(){
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                int index = row * 6 + col; // Calcula el índice de la celda
                if (model.getNumber(row, col) != 0) {
                    cellList.get(index).setText(String.valueOf(model.getNumber(row, col)));
                }
            }
        }
    }

    /**
     * Cleans the input string.
     *
     * @param key the input string
     * @return the cleaned input string
     */
    private String cleanInput(String key) {
        return key.trim();
    }

    /**
     * Updates the model and view with the given number.
     *
     * @param row    the row of the cell
     * @param col    the column of the cell
     * @param number the number to set in the cell
     */
    private void updateModelAndView(int row, int col, int number) {
        model.setNumber(row, col, number);
        boolean isValid = model.isValid(row,col,number);
        int index = row * 6 + col;
        TextField cell = cellList.get(index);
        highlightField(cell, "white");
        if(!isValid) {
            int conflictCellIndex = model.getCellConflictIndex(row,col,number);
            System.out.println(conflictCellIndex);
            highlightPathBetweenCells(index, conflictCellIndex);
        }

        cell.setText(String.valueOf(number));
    }

    /**
     * Highlights all cells from the selected cell to the conflict cell.
     *
     * @param selectedIndex the index of the selected cell
     * @param conflictIndex the index of the conflict cell
     */
    private void highlightPathBetweenCells(int selectedIndex, int conflictIndex) {
        int selectedRow = selectedIndex / 6;
        int selectedCol = selectedIndex % 6;
        int conflictRow = conflictIndex / 6;
        int conflictCol = conflictIndex % 6;

        // Highlight cells in the same row
        if (selectedRow == conflictRow) {
            for (int col = Math.min(selectedCol, conflictCol); col <= Math.max(selectedCol, conflictCol); col++) {
                int index = selectedRow * 6 + col;
                highlightField(cellList.get(index), "lightcoral");
            }
        }
        // Highlight cells in the same column
        else if (selectedCol == conflictCol) {
            for (int row = Math.min(selectedRow, conflictRow); row <= Math.max(selectedRow, conflictRow); row++) {
                int index = row * 6 + selectedCol;
                highlightField(cellList.get(index), "lightcoral");
            }
        }
    }


    private void highlightField(TextField txt, String color) {
        txt.setStyle("-fx-background-color: " + color + ";");
    }

    /**
     * Clears the content of a cell.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    public void clearCell(int row, int col) {
        model.clearNumber(row, col);
        int index = row * 6 + col;
        cellList.get(index).setText("");
    }

    /**
     * Undoes the last action.
     */
    public void undo() {
        model.undo();
        refreshView();
    }

    /**
     * Redoes the last undone action.
     */
    public void redo() {
        model.redo();
        refreshView();
    }

    /**
     * Refreshes the view to reflect the current state of the model.
     */
    private void refreshView() {
        for (int i = 0; i < 36; i++) {
            int row = i / 6;
            int col = i % 6;
            int number = model.getNumber(row, col);
            TextField cell = cellList.get(i);
            if (number == 0) {
                cell.clear();
            } else {
                cell.setText(String.valueOf(number));
            }
        }
    }

    /**
     * Displays the action history.
     */
    public void showActionHistory() {
        Queue<String> actions = model.getActionHistory();
        while (!actions.isEmpty()) {
            String action = actions.poll();
            System.out.println(action); // Print the action to the console or display in the interface
        }
    }
}