package com.example.sudokufpoe.Util;

public class InputValidator {
    /**
     * Handles invalid input by printing an appropriate message to the console.
     *
     * @param key the input string to handle
     */
    public static void handleInvalidInput(String key) {
        if (key.isEmpty() || !key.matches("[1-6]")) {
            System.out.println("Invalid input. Only numbers from 1 to 6 are allowed.");
        } else {
            System.out.println("Number out of range. Enter a number between 1 and 6.");
        }
    }
}