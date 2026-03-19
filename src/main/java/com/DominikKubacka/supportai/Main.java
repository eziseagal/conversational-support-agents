package com.DominikKubacka.supportai;

import com.DominikKubacka.supportai.core.ConversationManager;

/**
 * Application Entry Point.
 * 
 * @author Dominik Kubacka
 */
public class Main {
    public static void main(String[] args) {
        try {
            ConversationManager manager = new ConversationManager();
            manager.start();
        } catch (Exception e) {
            System.err.println("Fatal Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}