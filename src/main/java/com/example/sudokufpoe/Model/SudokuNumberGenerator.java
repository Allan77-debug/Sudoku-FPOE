package com.example.sudokufpoe.Model;

import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SudokuNumberGenerator {
    private final Random random = new Random();
    private final int totalNumbersToGenerate = 19;
    private final SudokuNumberValidation numberValidator;

    public SudokuNumberGenerator(){
        numberValidator = new SudokuNumberValidation();
    }

    public void generateValidNumbers(ArrayList<Integer> cellList) {
        int[][] subGridLimits = new int[][] {
                {0, 1}, {2, 3}, {4, 5}
        };

        int totalGeneratedNumbers = 0;

        for (int rowGroup = 0; rowGroup < 3; rowGroup++) {
            for (int colGroup = 0; colGroup < 2; colGroup++) {
                int numbersInSubGrid = calculateNumbersToGenerate(totalGeneratedNumbers);
                totalGeneratedNumbers += numbersInSubGrid;

                fillSubGridWithNumbers(cellList, subGridLimits[rowGroup], colGroup * 3, numbersInSubGrid);

                if (totalGeneratedNumbers >= totalNumbersToGenerate) {
                    return;
                }
            }
        }
        System.out.println("total" + totalGeneratedNumbers);
    }

    private int calculateNumbersToGenerate(int currentTotalGenerated) {
        int numbersInSubGrid = random.nextInt(3) + 2;

        if (currentTotalGenerated + numbersInSubGrid > totalNumbersToGenerate) {
            numbersInSubGrid = totalNumbersToGenerate - currentTotalGenerated;
        }

        return numbersInSubGrid;
    }

    private void fillSubGridWithNumbers(ArrayList<Integer> cellList, int[] rows, int startCol, int numbersToGenerate) {
        Set<Integer> filledCells = new HashSet<>();
        while (filledCells.size() < numbersToGenerate) {
            int row = random.nextInt(2);
            int col = random.nextInt(3);
            int cellIndex = rows[row] * 6 + (startCol + col);

            if (!filledCells.contains(cellIndex) && cellList.get(cellIndex) == 0) {
                setValidCellNumber(cellList, rows[row], startCol + col);
                filledCells.add(cellIndex);
            }
        }
    }

    private void setValidCellNumber(ArrayList<Integer> cellList, int row, int col) {
        int number;
        do {
            number = generateRandomNumber();
        } while (!isValidNumberForCell(cellList, row, col, number));

        cellList.set((row * 6) + col, number);
    }

    private int generateRandomNumber() {
        return random.nextInt(6) + 1;
    }

    private boolean isValidNumberForCell(ArrayList<Integer> cellList, int row, int col, int number) {

        return !numberValidator.isNumberInRow(row, number,cellList) && !numberValidator.isNumberInColumn(col, number,cellList);
    }

}
