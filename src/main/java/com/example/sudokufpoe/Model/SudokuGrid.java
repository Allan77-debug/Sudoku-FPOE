package com.example.sudokufpoe.Model;

import com.example.sudokufpoe.Util.InputValidator;
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

    /**
     * Constructs a new SudokuGrid.
     */
    public SudokuGrid() {
        grid = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            grid.add(0);
        }
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
    public boolean isValid(int row, int col, int number) {
        int index = row * 6 + col;
        return isValidNumber(number) && !isNumberInRow(row, number) && !isNumberInColumn(col, number) && !isNumberInBlock(index, number);
    }

    /**
     * Checks if a number is already present in the specified row.
     *
     * @param row the row to check
     * @param number the number to check
     * @return true if the number is present, false otherwise
     */
    private boolean isNumberInRow(int row, int number) {
        for (int col = 0; col < 6; col++) {
            int index = row * 6 + col;
            if (grid.get(index) == number) return true;
        }
        return false;
    }

    /**
     * Checks if a number is already present in the specified column.
     *
     * @param col the column to check
     * @param number the number to check
     * @return true if the number is present, false otherwise
     */
    private boolean isNumberInColumn(int col, int number) {
        for (int row = 0; row < 6; row++) {
            int index = row * 6 + col;
            if (grid.get(index) == number) return true;
        }
        return false;
    }

    /**
     * Checks if a number is already present in the 2x3 block containing the specified cell.
     *
     * @param index the index of the cell
     * @param number the number to check
     * @return true if the number is present, false otherwise
     */
    private boolean isNumberInBlock(int index, int number) {
        int row = index / 6;
        int col = index % 6;
        int startRow = (row / 2) * 2;
        int startCol = (col / 3) * 3;
        for (int i = startRow; i < startRow + 2; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                int blockIndex = i * 6 + j;
                if (grid.get(blockIndex) == number) return true;
            }
        }
        return false;
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
        if (isValidNumber(number) && isValid(row, col, number)) {
            savePreviousAction(index);
            resetRedoStack();
            updateGrid(index, number);
            logAction("Ingresado " + number + " en [" + row + "," + col + "]");
        } else {
            InputValidator.handleInvalidInput(String.valueOf(number));
        }
    }

    /**
     * Checks if a number is within the valid range (1-6).
     *
     * @param number the number to check
     * @return true if the number is within range, false otherwise
     */
    private boolean isValidNumber(int number) {
        return number >= 1 && number <= 6;
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
        if (isCellNotEmpty(index)) {
            savePreviousAction(index);
            resetRedoStack();
            clearGridCell(index);
            logClearAction(row, col);
        }
    }

    /**
     * Checks if a cell is not empty.
     *
     * @param index the index of the cell
     * @return true if the cell is not empty, false otherwise
     */
    private boolean isCellNotEmpty(int index) {
        return grid.get(index) != 0;
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
