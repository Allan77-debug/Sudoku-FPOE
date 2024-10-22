package com.example.sudokufpoe.Model;

import java.util.ArrayList;

public class SudokuNumberValidation {

    /**
     * Checks if a number is valid for a given cell.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @param number the number to check
     * @return true if the number is valid, false otherwise
     */
    public boolean isValid(int row, int col, int number,ArrayList<Integer> grid) {
        int index = row * 6 + col;
        return isValidNumber(number) && isNumberInRow(row, number, grid) && isNumberInColumn(col, number, grid) && isNumberInBlock(index, number, grid);
    }

    /**
     * Checks if a number is within the valid range (1-6).
     *
     * @param number the number to check
     * @return true if the number is within range, false otherwise
     */
    public boolean isValidNumber(int number) {
        return number >= 1 && number <= 6;
    }

    /**
     * Checks if a number is already present in the specified row.
     *
     * @param row the row to check
     * @param number the number to check
     * @return true if the number is present, false otherwise
     */
    public boolean isNumberInRow(int row, int number,ArrayList<Integer> grid) {
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
    public boolean isNumberInColumn(int col, int number,ArrayList<Integer> grid) {
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
    public boolean isNumberInBlock(int index, int number, ArrayList<Integer> grid) {
        int row = index / 6;
        int col = index % 6;
        int startRow = (row / 2) * 2;
        int startCol = (col / 3) * 3;
        for (int i = startRow; i < startRow + 2; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                int blockIndex = i * 6 + j;
                if (grid.get(blockIndex) == number) return false;
            }
        }
        return true;
    }


    /**
     * Checks if a cell is not empty.
     *
     * @param index the index of the cell
     * @return true if the cell is not empty, false otherwise
     */
    public boolean isCellNotEmpty(int index, ArrayList<Integer> grid) {
        return grid.get(index) != 0;
    }
}
