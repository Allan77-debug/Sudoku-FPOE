package com.example.sudokufpoe.Model;

import java.util.LinkedList;
import java.util.Queue;

public class ActionQueue {
    private final Queue<String> actionQueue;

    public ActionQueue() {
        actionQueue = new LinkedList<>();
    }

    public void addAction(String action) {
        actionQueue.offer(action); // Agregar acción al final de la cola
    }

    public String getNextAction() {
        return actionQueue.poll(); // Obtener la acción más antigua
    }

    public Queue<String> getActionHistory() {
        return new LinkedList<>(actionQueue);
    }
}
