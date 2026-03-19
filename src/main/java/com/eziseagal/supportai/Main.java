package com.eziseagal.supportai;

import com.eziseagal.supportai.core.ConversationManager;

/**
 * Application Entry Point.
 * 
 * @author eziseagal
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