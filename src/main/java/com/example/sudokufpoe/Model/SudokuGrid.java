package com.example.sudokufpoe.Model;

import com.example.sudokufpoe.Util.InputValidator;
import javafx.scene.control.TextField;

import java.util.*;

/**
 * Represents a Sudoku grid with undo and redo functionality.
 */
public class SudokuGrid {
    private final ArrayList<ArrayList<Integer>> grid;
    private final ArrayList<ArrayList<Integer>> solution;
    private final Deque<CellAction> undoStack;
    private final Deque<CellAction> redoStack;
    private final ActionQueue actionQueue;
    private final SudokuNumberValidation numberValidator;

    /**
     * Constructs a new SudokuGrid.
     */
    public SudokuGrid() {
        grid = new ArrayList<>();
        solution = new ArrayList<>();
        SudokuNumberGenerator numberGenerator = new SudokuNumberGenerator();

        initMatrix(grid);
        initMatrix(solution);


        numberGenerator.generateSudokuSolution(solution);
        numberGenerator.fillEmptyMatrixWithTwoNumbersPerBlock(solution, grid);

        printGrid(grid);
        printGrid(solution);

        numberValidator =  new SudokuNumberValidation();

        undoStack = new LinkedList<>();
        redoStack = new LinkedList<>();
        actionQueue = new ActionQueue();
    }

    private ArrayList<ArrayList<Integer>> initMatrix(ArrayList<ArrayList<Integer>> matrix) {
        for (int i = 0; i < 6; i++) {
            ArrayList<Integer> fila = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                fila.add(0);
            }
            matrix.add(fila);
        }

        return matrix;
    }

    /**
     * Represents an action performed on a cell.
     */
    private static class CellAction {
        int index, previousNumber;

        /**
         * Constructs a new CellAction.
         *
         * @param index the index of the cell
         * @param previousNumber the previous number in the cell
         */
        CellAction(int index, int previousNumber) {
            this.index = index;
            this.previousNumber = previousNumber;
        }
    }

    /**
     * Checks if a number is valid for a given cell.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @param number the number to check
     * @return true if the number is valid, false otherwise
     */
    public boolean isValid(int row, int col, int number) {
        return (numberValidator.isValidNumber(number) && !numberValidator.isNumberInRow(row, col, number, grid) && !numberValidator.isNumberInColumn(row, col, number, grid) && !numberValidator.isNumberInBlock(row, col, number, grid));
    }

    /**
     * Checks if a number is valid for a given cell.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @param number the number to check
     * @return result print dictionary with the valid ´position in the grid
     */
    public Map<String, Boolean> isValidDictionary(int row, int col, int number) {
        Map<String, Boolean> result = new HashMap<>();

        result.put("isValidNumber", numberValidator.isValidNumber(number));
        result.put("isNumberInRow", numberValidator.isNumberInRow(row, col, number, grid));
        result.put("isNumberInColumn", numberValidator.isNumberInColumn(row, col, number, grid));
        result.put("isNumberInBlock", numberValidator.isNumberInBlock(row,col, number, grid));

        return result;
    }

    /**
     * Sets a number in the specified cell if it is valid.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @param number the number to set
     */
    public void setNumber(int row, int col, int number) {
        int index = row * 6 + col;
        if (numberValidator.isValidNumber(number) && isValid(row, col, number)) {
            savePreviousAction(index);
            resetRedoStack();
            updateGrid(row, col, number);
            logAction("Ingresado " + number + " en [" + row + "," + col + "]");
        } else {
            InputValidator.handleInvalidInput(String.valueOf(number));
        }
    }

    /**
     * Saves the previous action for undo functionality.
     *
     * @param index the index of the cell
     */
    private void savePreviousAction(int index) {
        //undoStack.push(new CellAction(index, grid.get(index)));
    }

    /**
     * Updates the grid with the specified number.
     *
     * @param row the row of the cell
     * @param col the row of the cell
     * @param number the number to set
     */
    private void updateGrid(int row, int col, int number) {
        grid.get(row).set(col, number);
    }

    /**
     * Logs an action to the action queue.
     *
     * @param action the action to log
     */
    private void logAction(String action) {
        actionQueue.addAction(action);
    }

    /**
     * Undoes the last action.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            CellAction lastAction = undoStack.pop();
            saveRedoAction(lastAction);
            restorePreviousState(lastAction);
            logUndoAction(lastAction);
        }
    }

    /**
     * Redoes the last undone action.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            CellAction redoAction = redoStack.pop();
            saveUndoAction(redoAction);
            restoreRedoState(redoAction);
            logRedoAction(redoAction);
        }
    }


    /**
     * Saves the current state for redo functionality.
     *
     * @param action the action to save
     */
    private void saveRedoAction(CellAction action) {
        //redoStack.push(new CellAction(action.index, grid.get(action.index)));
    }

    /**
     * Restores the previous state of the grid.
     *
     * @param action the action to restore
     */
    private void restorePreviousState(CellAction action) {
        //grid.set(action.index, action.previousNumber);
    }

    /**
     * Logs an undo action to the action queue.
     *
     * @param action the action to log
     */
    private void logUndoAction(CellAction action) {
        int row = action.index / 6;
        int col = action.index % 6;
        actionQueue.addAction("Deshacer en [" + row + "," + col + "]");
    }

    /**
     * Saves the current state for undo functionality.
     *
     * @param action the action to save
     */
    private void saveUndoAction(CellAction action) {
        //undoStack.push(new CellAction(action.index, grid.get(action.index)));
    }

    /**
     * Restores the redo state of the grid.
     *
     * @param action the action to restore
     */
    private void restoreRedoState(CellAction action) {
        // grid.set(action.index, action.previousNumber);
    }

    /**
     * Prints the current state of the Sudoku grid to the console in a formatted way.
     */
    public void printGrid(ArrayList<ArrayList<Integer>> matrix) {

        for (ArrayList<Integer> fila : matrix) {
            System.out.print("| ");
            for (Integer elemento : fila) {
                System.out.print(elemento + " ");
            }
            System.out.println("|"); // Cerrar la fila con una barra vertical
        }

    }

    /**
     * Logs a redo action to the action queue.
     *
     * @param action the action to log
     */
    private void logRedoAction(CellAction action) {
        int row = action.index / 6;
        int col = action.index % 6;
        actionQueue.addAction("Rehacer en [" + row + "," + col + "]");
    }

    /**
     * Gets the number in the specified cell.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @return the number in the cell
     */
    public int getNumber(int row, int col) {
        return grid.get(row).get(col);
    }

    /**
     * Clears the number in the specified cell.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    public void clearNumber(int row, int col) {
        int index = row * 6 + col;
        if (numberValidator.isCellNotEmpty(row, col, grid)) {
            savePreviousAction(index);
            resetRedoStack();
            clearGridCell(row, col);
            logClearAction(row, col);
        }
    }


    /**
     * Clears the specified cell in the grid.
     *
     * @param row the row fo the cell to set
     * @param col the col fo the cell to set
     */
    private void clearGridCell(int row, int col) {
        grid.get(row).set(col, 0);
    }

    /**
     * Logs a clear action to the action queue.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    private void logClearAction(int row, int col) {
        actionQueue.addAction("Eliminado de [" + row + "," + col + "]");
    }

    /**
     * Gets the action history.
     *
     * @return the action history
     */
    public Queue<String> getActionHistory() {
        return actionQueue.getActionHistory();
    }

    /**
     * Resets the redo stack.
     */
    private void resetRedoStack() {
        redoStack.clear();
    }
}
