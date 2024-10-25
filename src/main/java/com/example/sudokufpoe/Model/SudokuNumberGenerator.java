package com.example.sudokufpoe.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates and fills a 6x6 Sudoku grid with solutions and initial hints.
 * Provides methods to generate a valid Sudoku solution, fill an empty grid with
 * a few hints, and verify if numbers placed in cells are valid.
 */
public class SudokuNumberGenerator {

    /**
     * Generates a complete 6x6 Sudoku solution grid.
     *
     * @param matrix the 6x6 matrix to be filled with a valid Sudoku solution
     */
    public void generateSudokuSolution(ArrayList<ArrayList<Integer>> matrix) {
        solveSudoku(matrix, 0, 0);
    }

    /**
     * Fills an empty 6x6 Sudoku grid with two random numbers per 2x3 subgrid from the solution.
     * This method adds two numbers to each 2x3 block in the empty grid, taken from the corresponding
     * values in the provided solution matrix, to create the initial playable puzzle.
     *
     * @param solvedMatrix the 6x6 solved Sudoku solution matrix
     * @param emptyMatrix  the 6x6 empty grid to be filled with hints from the solution
     */
    public void fillEmptyMatrixWithTwoNumbersPerBlock(ArrayList<ArrayList<Integer>> solvedMatrix, ArrayList<ArrayList<Integer>> emptyMatrix) {
        for (int row = 0; row < 6; row += 2) {
            for (int col = 0; col < 6; col += 3) {
                List<int[]> availableCells = getAvailableCellsInBlock(row, col);
                placeTwoRandomNumbersInBlock(solvedMatrix, emptyMatrix, row, col, availableCells);
            }
        }
    }

    /**
     * Recursively solves a 6x6 Sudoku grid by filling each cell with a valid number.
     *
     * @param matrix the 6x6 Sudoku matrix to be solved
     * @param row    the current row being processed
     * @param col    the current column being processed
     * @return true if the Sudoku solution is valid, false if no solution can be found
     */
    private boolean solveSudoku(ArrayList<ArrayList<Integer>> matrix, int row, int col) {
        if (row == 6) {
            return true;
        }
        if (col == 6) {
            return solveSudoku(matrix, row + 1, 0);
        }
        if (matrix.get(row).get(col) != 0) {
            return solveSudoku(matrix, row, col + 1);
        }

        List<Integer> numbers = generateShuffledNumbers();

        for (int num : numbers) {
            if (isSafe(matrix, row, col, num)) {
                matrix.get(row).set(col, num);
                if (solveSudoku(matrix, row, col + 1)) {
                    return true;
                }
                matrix.get(row).set(col, 0); // Reset if solution not found
            }
        }
        return false;
    }

    /**
     * Generates a list of numbers from 1 to 6 in random order.
     *
     * @return a shuffled list of numbers from 1 to 6
     */
    private List<Integer> generateShuffledNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        return numbers;
    }

    /**
     * Checks if a given number can be safely placed in a specific cell based on Sudoku rules.
     *
     * @param matrix the 6x6 Sudoku grid
     * @param row    the row index of the cell
     * @param col    the column index of the cell
     * @param num    the number to check for placement
     * @return true if the number can be placed in the cell without conflicts
     */
    private boolean isSafe(ArrayList<ArrayList<Integer>> matrix, int row, int col, int num) {
        return isRowSafe(matrix, row, num) && isColumnSafe(matrix, col, num) && isSubgridSafe(matrix, row, col, num);
    }

    /**
     * Checks if a number is unique within its row.
     *
     * @param matrix the 6x6 Sudoku grid
     * @param row    the row index of the cell
     * @param num    the number to check for uniqueness in the row
     * @return true if the number is not present in the row
     */
    private boolean isRowSafe(ArrayList<ArrayList<Integer>> matrix, int row, int num) {
        for (int x = 0; x < 6; x++) {
            if (matrix.get(row).get(x) == num) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a number is unique within its column.
     *
     * @param matrix the 6x6 Sudoku grid
     * @param col    the column index of the cell
     * @param num    the number to check for uniqueness in the column
     * @return true if the number is not present in the column
     */
    private boolean isColumnSafe(ArrayList<ArrayList<Integer>> matrix, int col, int num) {
        for (int x = 0; x < 6; x++) {
            if (matrix.get(x).get(col) == num) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a number is unique within its 2x3 subgrid.
     *
     * @param matrix the 6x6 Sudoku grid
     * @param row    the row index of the cell
     * @param col    the column index of the cell
     * @param num    the number to check for uniqueness in the 2x3 subgrid
     * @return true if the number is not present in the 2x3 subgrid
     */
    private boolean isSubgridSafe(ArrayList<ArrayList<Integer>> matrix, int row, int col, int num) {
        int startRow = row - row % 2;
        int startCol = col - col % 3;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                if (matrix.get(i + startRow).get(j + startCol) == num) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Retrieves the list of available cells in a specific 2x3 subgrid.
     *
     * @param startRow the starting row of the 2x3 subgrid
     * @param startCol the starting column of the 2x3 subgrid
     * @return a list of cell positions represented as arrays of row and column indices
     */
    private List<int[]> getAvailableCellsInBlock(int startRow, int startCol) {
        List<int[]> cells = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                cells.add(new int[]{startRow + i, startCol + j});
            }
        }
        return cells;
    }

    /**
     * Places two random numbers from the solution into two random cells in a 2x3 subgrid in the empty grid.
     *
     * @param solvedMatrix   the 6x6 solved Sudoku solution matrix
     * @param emptyMatrix    the 6x6 empty grid to place the numbers in
     * @param startRow       the starting row of the 2x3 subgrid
     * @param startCol       the starting column of the 2x3 subgrid
     * @param availableCells a list of available cell positions in the subgrid
     */
    private void placeTwoRandomNumbersInBlock(ArrayList<ArrayList<Integer>> solvedMatrix, ArrayList<ArrayList<Integer>> emptyMatrix, int startRow, int startCol, List<int[]> availableCells) {
        Random rand = new Random();
        Collections.shuffle(availableCells);

        for (int i = 0; i < 2; i++) {
            int[] cell = availableCells.get(i);
            int row = cell[0];
            int col = cell[1];
            int numberFromSolution = solvedMatrix.get(row).get(col);
            emptyMatrix.get(row).set(col, numberFromSolution);
        }
    }
}
