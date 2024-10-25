package com.example.sudokufpoe.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SudokuNumberValidation {



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
     * @param grid the 6x6 grid to check
     * @return true if the number is present in the row, false otherwise
     */
    public boolean isNumberInRow(int row, int col,  int number, ArrayList<ArrayList<Integer>> grid) {
        for (int indexCol = 0; indexCol < 6; indexCol++) {
            if (grid.get(row).get(indexCol).equals(number) && indexCol != col) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a number is already present in the specified column.
     *
     * @param col the column to check
     * @param number the number to check
     * @param grid the 6x6 grid to check
     * @return true if the number is present in the column, false otherwise
     */
    public boolean isNumberInColumn(int row, int col, int number, ArrayList<ArrayList<Integer>> grid) {
        for (int indexRow = 0; indexRow < 6; indexRow++) {
            if (grid.get(indexRow).get(col).equals(number) && indexRow != row) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a number is already present in the specified 3x2 block.
     *
     * @param row the starting row of the block
     * @param col the starting column of the block
     * @param number the number to check
     * @param grid the 6x6 grid to check
     * @return true if the number is present in the 3x2 block, false otherwise
     */
    public boolean isNumberInBlock(int row, int col, int number, ArrayList<ArrayList<Integer>> grid) {
        // Adjust to find the top-left corner of the 3x2 block
        int blockRowStart = (row / 2) * 2;
        int blockColStart = (col / 3) * 3;

        for (int indexRow = blockRowStart; indexRow < blockRowStart + 2; indexRow++) {
            for (int indexCol = blockColStart; indexCol < blockColStart + 3; indexCol++) {
                if (grid.get(indexRow).get(indexCol).equals(number) && (indexCol != col || indexRow != row)) {
                    System.out.println("col: " + indexCol + " row: " + indexRow);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Checks if a cell is not empty.
     *
     * @param row of the cell
     * @param col of the cell
     * @return true if the cell is not empty, false otherwise
     */
    public boolean isCellNotEmpty(int row, int col, ArrayList<ArrayList<Integer>> grid) {
        return grid.get(row).get(col) != 0;
    }
}
