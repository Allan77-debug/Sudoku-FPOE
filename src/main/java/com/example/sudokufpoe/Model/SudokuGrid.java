package com.example.sudokufpoe.Model;

import com.example.sudokufpoe.Util.InputValidator;

import java.util.Queue;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Represents a Sudoku grid with undo and redo functionality.
 */
public class SudokuGrid {
    private final int[][] grid;
    private final Deque<CellAction> undoStack;
    private final Deque<CellAction> redoStack;
    private final ActionQueue actionQueue;

    /**
     * Constructs a new SudokuGrid.
     */
    public SudokuGrid() {
        grid = new int[6][6];
        undoStack = new LinkedList<>();
        redoStack = new LinkedList<>();
        actionQueue = new ActionQueue();
    }

    /**
     * Represents an action performed on a cell.
     */
    private static class CellAction {
        int row, col, previousNumber;

        /**
         * Constructs a new CellAction.
         *
         * @param row the row of the cell
         * @param col the column of the cell
         * @param previousNumber the previous number in the cell
         */
        CellAction(int row, int col, int previousNumber) {
            this.row = row;
            this.col = col;
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
        return isValidNumber(number) && !isNumberInRow(row, number) && !isNumberInColumn(col, number) && !isNumberInBlock(row, col, number);
    }

    /**
     * Checks if a number is already present in the specified row.
     *
     * @param row the row to check
     * @param number the number to check
     * @return true if the number is present, false otherwise
     */
    private boolean isNumberInRow(int row, int number) {
        for (int i = 0; i < 6; i++) {
            if (grid[row][i] == number) return true;
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
        for (int i = 0; i < 6; i++) {
            if (grid[i][col] == number) return true;
        }
        return false;
    }

    /**
     * Checks if a number is already present in the 2x3 block containing the specified cell.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @param number the number to check
     * @return true if the number is present, false otherwise
     */
    private boolean isNumberInBlock(int row, int col, int number) {
        int startRow = (row / 2) * 2;
        int startCol = (col / 3) * 3;
        for (int i = startRow; i < startRow + 2; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (grid[i][j] == number) return true;
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
        if (isValidNumber(number) && isValid(row, col, number)) {
            savePreviousAction(row, col);
            resetRedoStack();
            updateGrid(row, col, number);
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
     * @param row the row of the cell
     * @param col the column of the cell
     */
    private void savePreviousAction(int row, int col) {
        undoStack.push(new CellAction(row, col, grid[row][col]));
    }

    /**
     * Updates the grid with the specified number.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @param number the number to set
     */
    private void updateGrid(int row, int col, int number) {
        grid[row][col] = number;
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
        redoStack.push(new CellAction(action.row, action.col, grid[action.row][action.col]));
    }

    /**
     * Restores the previous state of the grid.
     *
     * @param action the action to restore
     */
    private void restorePreviousState(CellAction action) {
        grid[action.row][action.col] = action.previousNumber;
    }

    /**
     * Logs an undo action to the action queue.
     *
     * @param action the action to log
     */
    private void logUndoAction(CellAction action) {
        actionQueue.addAction("Deshacer en [" + action.row + "," + action.col + "]");
    }

    /**
     * Saves the current state for undo functionality.
     *
     * @param action the action to save
     */
    private void saveUndoAction(CellAction action) {
        undoStack.push(new CellAction(action.row, action.col, grid[action.row][action.col]));
    }

    /**
     * Restores the redo state of the grid.
     *
     * @param action the action to restore
     */
    private void restoreRedoState(CellAction action) {
        grid[action.row][action.col] = action.previousNumber;
    }

    /**
     * Logs a redo action to the action queue.
     *
     * @param action the action to log
     */
    private void logRedoAction(CellAction action) {
        actionQueue.addAction("Rehacer en [" + action.row + "," + action.col + "]");
    }

    /**
     * Gets the number in the specified cell.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @return the number in the cell
     */
    public int getNumber(int row, int col) {
        return grid[row][col];
    }

    /**
     * Clears the number in the specified cell.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    public void clearNumber(int row, int col) {
        if (isCellNotEmpty(row, col)) {
            savePreviousAction(row, col);
            resetRedoStack();
            clearGridCell(row, col);
            logClearAction(row, col);
        }
    }

    /**
     * Checks if a cell is not empty.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @return true if the cell is not empty, false otherwise
     */
    private boolean isCellNotEmpty(int row, int col) {
        return grid[row][col] != 0;
    }

    /**
     * Clears the specified cell in the grid.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    private void clearGridCell(int row, int col) {
        grid[row][col] = 0;
    }

    /**
     * Logs a clear action to the action queue.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    private void logClearAction(int row, int col) {
        actionQueue.addAction("Eliminado " + grid[row][col] + " de [" + row + "," + col + "]");
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