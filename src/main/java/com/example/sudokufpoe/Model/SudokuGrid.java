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
    private int helpAttemps = 6;

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

        numberValidator =  new SudokuNumberValidation();

        undoStack = new LinkedList<>();
        redoStack = new LinkedList<>();
        actionQueue = new ActionQueue();
    }

    /**
     * Initializes a 6x6 matrix with all elements set to 0.
     *
     * @param matrix the matrix to initialize
     * @return the initialized matrix
     */
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
            savePreviousAction(row, col);
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
     * @param row the row of the cell
     * @param col the column of the cell
     */
    private void savePreviousAction(int row, int col) {
        int previousNumber = grid.get(row).get(col);
        undoStack.push(new CellAction(row, col, previousNumber));
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
     * Provides assistance to the player by filling an empty cell with its correct value from the solution grid.
     * This method finds an empty cell (value 0) within the current game grid, retrieves the correct value
     * from the solution grid, places it in the game grid, and then returns the position and value of the cell.
     * The player has a limited number of attempts to use this feature, which decrements each time the method is called.
     *
     * @return a map with the following key-value pairs:
     *         - "row": the row index of the cell where help was provided
     *         - "col": the column index of the cell where help was provided
     *         - "value": the correct solution value placed in the cell
     *         Returns null if there are no attempts left or if the grid has no empty cells.
     */
    public Map<String, Integer> help() {
        if (helpAttemps <= 0) return null;

        helpAttemps--;

        List<int[]> emptyCells = new ArrayList<>();

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                if (grid.get(row).get(col) == 0) {
                    emptyCells.add(new int[]{row, col});
                }
            }
        }

        if (emptyCells.isEmpty()) return null;

        Random random = new Random();
        int[] chosenCell = emptyCells.get(random.nextInt(emptyCells.size()));

        int row = chosenCell[0];
        int col = chosenCell[1];
        int solutionValue = solution.get(row).get(col);

        grid.get(row).set(col, solutionValue);

        Map<String, Integer> result = new HashMap<>();
        result.put("row", row);
        result.put("col", col);
        result.put("value", solutionValue);

        return result;
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
        grid.get(action.row).set(action.col, action.previousNumber);
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
        undoStack.push(new CellAction(action.row,action.col, grid.get(action.row).get(action.col)));
    }

    /**
     * Restores the redo state of the grid.
     *
     * @param action the action to restore
     */
    private void restoreRedoState(CellAction action) {
         grid.get(action.row).set(action.col, action.previousNumber);
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
        return grid.get(row).get(col);
    }

    /**
     * Clears the number in the specified cell.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    public void clearNumber(int row, int col) {
        if (numberValidator.isCellNotEmpty(row, col, grid)) {
            savePreviousAction(row, col);
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
