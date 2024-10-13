package com.example.sudokufpoe.Model;
import java.util.Queue;
import java.util.Stack;
import java.util.Deque;
import java.util.LinkedList;


public class SudokuGrid {
    private int[][] grid;
    private Deque<CellAction> undoStack;
    private Deque<CellAction> redoStack;
    private ActionQueue actionQueue;

    public SudokuGrid() {
        grid = new int[6][6];
        undoStack = new LinkedList<>();
        redoStack = new LinkedList<>();
        actionQueue = new ActionQueue();
    }

    private static class CellAction {
        int row, col, previousNumber;

        CellAction(int row, int col, int previousNumber) {
            this.row = row;
            this.col = col;
            this.previousNumber = previousNumber;
        }
    }

    // Método para verificar si un número es válido en una posición específica
    public boolean isValid(int row, int col, int number) {
        // Verificar que el número esté entre 1 y 6
        if (number < 1 || number > 6) return false;

        // Verificar que el número no se repita en la fila
        for (int i = 0; i < 6; i++) {
            if (grid[row][i] == number) return false;
        }

        // Verificar que el número no se repita en la columna
        for (int i = 0; i < 6; i++) {
            if (grid[i][col] == number) return false;
        }

        // Verificar que el número no se repita en el bloque 2x3
        int startRow = (row / 2) * 2;
        int startCol = (col / 3) * 3;
        for (int i = startRow; i < startRow + 2; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (grid[i][j] == number) return false;
            }
        }

        return true;
    }

    public void setNumber(int row, int col, int number) {
        if (isValid(row, col, number)) {
            undoStack.push(new CellAction(row, col, grid[row][col])); // Guardar acción previa
            resetRedoStack(); // Limpiar la pila de rehacer cuando hay una nueva acción
            grid[row][col] = number;
            actionQueue.addAction("Ingresado " + number + " en [" + row + "," + col + "]"); // Registro de acción

        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            CellAction lastAction = undoStack.pop();
            redoStack.push(new CellAction(lastAction.row, lastAction.col, grid[lastAction.row][lastAction.col]));
            grid[lastAction.row][lastAction.col] = lastAction.previousNumber;
            actionQueue.addAction("Deshacer en [" + lastAction.row + "," + lastAction.col + "]"); // Registro de acción

        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            CellAction redoAction = redoStack.pop();
            undoStack.push(new CellAction(redoAction.row, redoAction.col, grid[redoAction.row][redoAction.col]));
            grid[redoAction.row][redoAction.col] = redoAction.previousNumber;
            actionQueue.addAction("Rehacer en [" + redoAction.row + "," + redoAction.col + "]"); // Registro de acción

        }
    }

    public int getNumber(int row, int col) {
        return grid[row][col];
    }

    public void clearNumber(int row, int col) {
        if (grid[row][col] != 0) {
            undoStack.push(new CellAction(row, col, grid[row][col]));
            resetRedoStack(); // Limpiar la pila de rehacer
            grid[row][col] = 0; // Eliminar el número
            actionQueue.addAction("Eliminado " + grid[row][col] + " de [" + row + "," + col + "]"); // Registro de acción

        }
    }

    public Queue<String> getActionHistory() {
        return actionQueue.getActionHistory();
    }

    private void resetRedoStack() {
        redoStack.clear();
    }
}
