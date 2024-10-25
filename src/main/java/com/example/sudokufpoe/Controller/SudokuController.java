package com.example.sudokufpoe.Controller;

import com.example.sudokufpoe.Model.SudokuNumberGenerator;
import com.example.sudokufpoe.Model.SudokuGrid;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;

/**
 * Controller class for managing the Sudoku game.
 */
public class SudokuController {

    @FXML
    private GridPane gridPane; // Referencia al GridPane del FXML
    @FXML
    private final SudokuGrid model;
    private final ArrayList<ArrayList<TextField>> cellGrid;


    /**
     * Constructor for SudokuController.
     */
    public SudokuController() {
        model = new SudokuGrid();
        cellGrid = new ArrayList<>();
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        createDynamicGrid();
        fillWithRandomNumbers();
    }

    /**
     * Crea la cuadrícula de TextFields de manera dinámica.
     */
    private void createDynamicGrid() {
        for (int row = 0; row < 6; row++) {
            ArrayList<TextField> rowList = new ArrayList<>();
            for (int col = 0; col < 6; col++) {
                TextField cell = new TextField();
                cell.setPrefSize(62, 61);
                final int colFinal = col;
                final int rowFinal = row;

                cell.setOnKeyReleased(e -> {
                    String text = cell.getText();
                    if (text.length() > 1 || !text.matches("[1-6]?")) {
                        cell.setText("");
                    }
                });

                //cell.setOnMouseClicked(e -> onCellClick(rowFinal, colFinal));

                addTextChangeListener(cell, rowFinal, colFinal);

                gridPane.add(cell, col, row); // Añade el TextField al GridPane
                rowList.add(cell); // Añade el TextField a la lista de la fila actual
            }
            cellGrid.add(rowList); // Añade la fila a la lista de celdas
        }
    }

    /**
     * Maneja el evento de clic en una celda.
     *
     * @param row the row of the clicked cell
     * @param col the col of the clicked cell
     */
    public void onCellClick(int row, int col) {
        TextField selectedCell = cellGrid.get(row).get(col);
        selectedCell.requestFocus();

        selectedCell.setOnKeyTyped(event -> handleKeyPress(event.getCharacter(), row, col));
    }

    /**
     * Maneja las pulsaciones de teclas.
     *
     * @param key el valor de la tecla presionada
     * @param row la fila de la celda
     * @param col la columna de la celda
     */
    private void handleKeyPress(String key, int row, int col) {
        key = cleanInput(key);
        if (key.matches("[1-6]")) {
            int number = Integer.parseInt(key);
            updateModelAndView(row, col, number);
        }
    }

    /**
     * Llena la cuadrícula con números aleatorios del modelo.
     */
    private void fillWithRandomNumbers() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                int number = model.getNumber(row, col);
                if (number != 0) {
                    cellGrid.get(row).get(col).setText(String.valueOf(number));
                }
            }
        }
    }

    /**
     * Adds a listener to a {@code TextField} to monitor changes in text input.
     * The listener ensures that only valid letters are entered and converts all input to lowercase.
     *
     * @param txt      the {@code TextField} to which the listener is added
     */
    private void addTextChangeListener(TextField txt, int row, int col) {
        txt.textProperty().addListener((observable, oldValue, newValue) -> {
            // Allow only valid letters and revert to oldValue if the input is invalid
            highlightRow(row, col, "white", true);
            highlightCol(row, col,"white", true);
            highlightBlock(row, col, "white", true);
            if(!newValue.matches("[1-6]")) return;
            int number = Integer.parseInt(newValue);
            updateModelAndView(row, col,number);
        });
    }

    /**
     * Limpia la entrada de texto.
     *
     * @param key el texto a limpiar
     * @return el texto limpio
     */
    private String cleanInput(String key) {
        return key.trim();
    }

    /**
     * Actualiza el modelo y la vista con el número dado.
     *
     * @param row la fila de la celda
     * @param col la columna de la celda
     * @param number el número a establecer
     */
    private void updateModelAndView(int row, int col, int number) {
        model.setNumber(row, col, number);
        boolean isValid = model.isValid(row, col, number);
        Map<String, Boolean> isValidMap = model.isValidDictionary(row, col, number);

        TextField cell = cellGrid.get(row).get(col);

        if(!isValid) highlightPathBetweenCells(row, col, isValidMap.get("isNumberInRow"), isValidMap.get("isNumberInColumn"), isValidMap.get("isNumberInBlock"));

        cell.setText(String.valueOf(number));
    }

    /**
     * Resalta todas las celdas entre la celda seleccionada y la celda en conflicto.
     *
     * @param selectedRow la fila de la celda seleccionada
     * @param selectedCol la columna de la celda seleccionada
     */
    private void highlightPathBetweenCells(int selectedRow, int selectedCol, boolean isNumberInRow, boolean isNumberInCol, boolean isNumberInBlock) {
        if(isNumberInRow) highlightRow(selectedRow, selectedCol,"#fbe5e5", !isNumberInRow);

        if(isNumberInCol) highlightCol(selectedRow, selectedCol,"#fbe5e5", !isNumberInCol);

       if (isNumberInBlock) highlightBlock(selectedRow, selectedCol, "#fbe5e5", !isNumberInBlock);

    }

    private void highlightRow(int row, int col,  String color, boolean isValid){

        for (int i = 0; i < 6; i++) {
            highlightField(cellGrid.get(row).get(i),color, "black");
        }
        if(!isValid) highlightField(cellGrid.get(row).get(col),color, "red");
    }

    private void highlightCol(int row, int col, String color, boolean isValid){
        for (int i = 0; i < 6; i++) {
            highlightField(cellGrid.get(i).get(col),color, "black");
        }
        if(!isValid) highlightField(cellGrid.get(row).get(col),color, "red");
    }

    /**
     * Resalta todas las celdas en el bloque 3x2 que contiene la celda seleccionada.
     *
     * @param row la fila de la celda seleccionada
     * @param col la columna de la celda seleccionada
     * @param color el color de fondo para resaltar
     * @param isValid indica si la celda es válida o no (para aplicar el color de texto correspondiente)
     */
    private void highlightBlock(int row, int col, String color, boolean isValid) {
        // Calculamos la fila y columna iniciales del bloque 2x3 que contiene la celda seleccionada
        int blockStartRow = (row / 2) * 2; // Multiplicamos por 2 para obtener la fila superior del bloque
        int blockStartCol = (col / 3) * 3; // Multiplicamos por 3 para obtener la columna izquierda del bloque

        // Recorremos las celdas del bloque 2x3
        for (int i = blockStartRow; i < blockStartRow + 2; i++) {
            for (int j = blockStartCol; j < blockStartCol + 3; j++) {
                highlightField(cellGrid.get(i).get(j), color, "black");
            }
        }

        // Si no es válido, resaltar la celda seleccionada en rojo
        if (!isValid) highlightField(cellGrid.get(row).get(col), color, "red");

    }

    private void highlightField(TextField txt, String color, String textColor) {
        txt.setStyle("-fx-background-color: " + color + "; " + "-fx-text-fill: " + textColor + ";");
    }

    /**
     * Maneja la acción de deshacer.
     */
    @FXML
    private void handleUndo() {
        undo();
    }

    /**
     * Maneja la acción de rehacer.
     */
    @FXML
    private void handleRedo() {
        redo();
    }

    public void undo() {
        model.undo();
        refreshView();
    }

    public void redo() {
        model.redo();
        refreshView();
    }

    private void refreshView() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                int number = model.getNumber(row, col);
                TextField cell = cellGrid.get(row).get(col);
                if (number == 0) {
                    cell.clear();
                } else {
                    cell.setText(String.valueOf(number));
                }
            }
        }
    }
}