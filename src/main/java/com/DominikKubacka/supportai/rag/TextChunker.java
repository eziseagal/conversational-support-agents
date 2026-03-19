package com.DominikKubacka.supportai.rag;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to split large texts into smaller semantic chunks.
 * 
 * @author Dominik Kubacka
 */
public class TextChunker {
    
    /**
     * Splits text by double newlines to separate paragraphs/markdown sections.
     */
    public static List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();
        // Split by one or more blank lines
        String[] paragraphs = text.split("\\n\\s*\\n");
        
        for (String p : paragraphs) {
            String cleanChunk = p.trim();
            if (!cleanChunk.isEmpty()) {
                chunks.add(cleanChunk);
            }
        }
        return chunks;
    }
}