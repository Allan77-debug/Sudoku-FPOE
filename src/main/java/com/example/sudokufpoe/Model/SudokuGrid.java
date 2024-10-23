package com.example.sudokufpoe.Model;

import com.example.sudokufpoe.Util.InputValidator;
import javafx.scene.control.TextField;

import java.util.Queue;
import java.util.Deque;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * Represents a Sudoku grid with undo and redo functionality.
 */
public class SudokuGrid {
    private final ArrayList<Integer> grid; // Reemplazamos la matriz con ArrayList
    private final Deque<CellAction> undoStack;
    private final Deque<CellAction> redoStack;
    private final ActionQueue actionQueue;
    private final SudokuNumberValidation numberValidator;

    /**
     * Constructs a new SudokuGrid.
     */
    public SudokuGrid() {
        grid = new ArrayList<>();
        SudokuNumberGenerator numberGenerator = new SudokuNumberGenerator();
        numberValidator =  new SudokuNumberValidation();
        for (int i = 0; i < 36; i++) {
            grid.add(0);
        }
        numberGenerator.generateValidNumbers(grid);

        undoStack = new LinkedList<>();
        redoStack = new LinkedList<>();
        actionQueue = new ActionQueue();
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
    public boolean eisValid(int row, int col, int number) {
        int index = row * 6 + col;
        return (numberValidator.isValidNumber(number) && !numberValidator.isNumberInRow(row, number, grid) && !numberValidator.isNumberInColumn(col, number, grid) && !numberValidator.isNumberInBlock(index, number, grid));
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
        if (numberValidator.isValidNumber(number) && numberValidator.isValid(row, col, number, grid)) {
            savePreviousAction(index);
            resetRedoStack();
            updateGrid(index, number);
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
        undoStack.push(new CellAction(index, grid.get(index)));
    }

    /**
     * Updates the grid with the specified number.
     *
     * @param index the index of the cell
     * @param number the number to set
     */
    private void updateGrid(int index, int number) {
        grid.set(index, number);
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
        redoStack.push(new CellAction(action.index, grid.get(action.index)));
    }

    /**
     * Restores the previous state of the grid.
     *
     * @param action the action to restore
     */
    private void restorePreviousState(CellAction action) {
        grid.set(action.index, action.previousNumber);
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
        undoStack.push(new CellAction(action.index, grid.get(action.index)));
    }

    /**
     * Restores the redo state of the grid.
     *
     * @param action the action to restore
     */
    private void restoreRedoState(CellAction action) {
        grid.set(action.index, action.previousNumber);
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
        int index = row * 6 + col;
        return grid.get(index);
    }

    /**
     * Clears the number in the specified cell.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    public void clearNumber(int row, int col) {
        int index = row * 6 + col;
        if (numberValidator.isCellNotEmpty(index, grid)) {
            savePreviousAction(index);
            resetRedoStack();
            clearGridCell(index);
            logClearAction(row, col);
        }
    }


    /**
     * Clears the specified cell in the grid.
     *
     * @param index the index of the cell
     */
    private void clearGridCell(int index) {
        grid.set(index, 0);
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
