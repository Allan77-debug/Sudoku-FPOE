package com.example.sudokufpoe.Controller;

import com.example.sudokufpoe.Model.SudokuGrid;
import com.example.sudokufpoe.Util.InputValidator;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Controller class for managing the Sudoku game.
 */
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

                // Ensure each field accepts only numbers from 1 to 6
                cells[row][col].setOnKeyReleased(e -> {
                    String text = cells[finalRow][finalCol].getText();
                    if (text.length() > 1 || !text.matches("[1-6]?")) {
                        cells[finalRow][finalCol].setText(""); // Clear if input is invalid
                    }
                });

                cells[row][col].setOnMouseClicked(e -> onCellClick(finalRow, finalCol));
            }
        }
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
     * Updates the view of a cell.
     *
     * @param row    the row of the cell
     * @param col    the column of the cell
     * @param number the number to set in the cell
     */
    private void updateCellView(int row, int col, int number) {
        TextField cell = cells[row][col];
        if (number == 0) {
            cell.clear(); // Visually clear the cell if the number is 0
        } else {
            cell.setText(String.valueOf(number)); // Display the entered number
        }
    }

    /**
     * Handles cell click and editing.
     *
     * @param row the row of the clicked cell
     * @param col the column of the clicked cell
     */
    public void onCellClick(int row, int col) {
        TextField selectedCell = cells[row][col];
        selectedCell.requestFocus(); // Select the cell
        selectedCell.setOnKeyTyped(event -> handleKeyPress(event.getCharacter(), row, col));
    }

    /**
     * Handles key press events.
     *
     * @param key the key pressed
     * @param row the row of the cell
     * @param col the column of the cell
     */
    private void handleKeyPress(String key, int row, int col) {
        key = cleanInput(key);
        if (InputValidator.isValidNumber(key)) {
            int number = Integer.parseInt(key);
            updateModelAndView(row, col, number);
        } else {
            InputValidator.handleInvalidInput(key);
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
        updateCellView(row, col, number);
    }

    /**
     * Handles invalid input.
     *
     * @param key the invalid input string
     */
    private void handleInvalidInput(String key) {
        if (key.isEmpty() || !key.matches("[1-6]")) {
            System.out.println("Invalid input. Only numbers from 1 to 6 are allowed.");
        } else {
            System.out.println("Number out of range. Enter a number between 1 and 6.");
        }
    }

    /**
     * Clears the content of a cell.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    public void clearCell(int row, int col) {
        model.clearNumber(row, col);
        cells[row][col].setText("");
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
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                int number = model.getNumber(row, col);
                updateCellView(row, col, number);
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