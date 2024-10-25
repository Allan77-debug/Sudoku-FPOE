package com.example.sudokufpoe.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SudokuNumberGenerator {

    public void generateSudokuSolution(ArrayList<ArrayList<Integer>> matrix) {
        solveSudoku(matrix, 0, 0);
    }

    // Método para rellenar una matriz con dos números aleatorios de cada bloque 2x3 de una matriz solucionada
    public void fillEmptyMatrixWithTwoNumbersPerBlock(ArrayList<ArrayList<Integer>> solvedMatrix, ArrayList<ArrayList<Integer>> emptyMatrix) {
        for (int row = 0; row < 6; row += 2) { // Recorre las subcuadrículas de 2x3
            for (int col = 0; col < 6; col += 3) {
                List<int[]> availableCells = getAvailableCellsInBlock(row, col);
                placeTwoRandomNumbersInBlock(solvedMatrix, emptyMatrix, row, col, availableCells);
            }
        }
    }

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

                        matrix.get(row).set(col, 0);
            }
        }

        return false;
    }

    private List<Integer> generateShuffledNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        return numbers;
    }

    private boolean isSafe(ArrayList<ArrayList<Integer>> matrix, int row, int col, int num) {
        return isRowSafe(matrix, row, num) && isColumnSafe(matrix, col, num) && isSubgridSafe(matrix, row, col, num);
    }

    private boolean isRowSafe(ArrayList<ArrayList<Integer>> matrix, int row, int num) {
        for (int x = 0; x < 6; x++) {
            if (matrix.get(row).get(x) == num) {
                return false;
            }
        }
        return true;
    }

    private boolean isColumnSafe(ArrayList<ArrayList<Integer>> matrix, int col, int num) {
        for (int x = 0; x < 6; x++) {
            if (matrix.get(x).get(col) == num) {
                return false;
            }
        }
        return true;
    }

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

    private List<int[]> getAvailableCellsInBlock(int startRow, int startCol) {
        List<int[]> cells = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                cells.add(new int[]{startRow + i, startCol + j});
            }
        }
        return cells;
    }

    private void placeTwoRandomNumbersInBlock(ArrayList<ArrayList<Integer>> solvedMatrix, ArrayList<ArrayList<Integer>> emptyMatrix, int startRow, int startCol, List<int[]> availableCells) {
        Random rand = new Random();
        Collections.shuffle(availableCells); // Mezclamos las celdas del bloque para obtener dos al azar

        for (int i = 0; i < 2; i++) { // Elegimos dos celdas aleatorias
            int[] cell = availableCells.get(i);
            int row = cell[0];
            int col = cell[1];
            int numberFromSolution = solvedMatrix.get(row).get(col);
            emptyMatrix.get(row).set(col, numberFromSolution); // Colocamos el número en la matriz vacía
        }
    }
}
