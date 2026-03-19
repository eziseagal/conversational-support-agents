package com.DominikKubacka.supportai.rag;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextChunkerTest {

    @Test
    void testChunkingByDoubleNewline() {
        String markdownText = 
            "Header 1\n" +
            "This is the first paragraph.\n" +
            "\n" + // Double enter
            "This is the second paragraph.\n" +
            "\n\n\n" + // Multiple enters and spaces
            "This is the third paragraph.";

        List<String> chunks = TextChunker.chunkText(markdownText);

        assertEquals(3, chunks.size());
        assertEquals("Header 1\nThis is the first paragraph.", chunks.get(0));
        assertEquals("This is the second paragraph.", chunks.get(1));
        assertEquals("This is the third paragraph.", chunks.get(2));
    }
}