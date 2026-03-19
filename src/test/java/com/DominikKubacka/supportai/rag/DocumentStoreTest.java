package com.DominikKubacka.supportai.rag;

import com.DominikKubacka.supportai.core.LLMClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DocumentStoreTest {

    @Mock
    private LLMClient mockLlmClient;

    // JUnit will automatically create a temporary folder and delete it after the test!
    @TempDir
    Path tempDir;

    @Test
    void testIngestAndSearchCosineSimilarity() throws Exception {
        // 1. We create a temporary file with two paragraphs (chunks)
        Path tempFile = tempDir.resolve("test_doc.md");
        Files.writeString(tempFile, "Vector X Content\n\nVector Y Content");

        // 2. We train the mock to embed the texts
        when(mockLlmClient.createEmbedding("Vector X Content")).thenReturn(new double[]{1.0, 0.0});
        when(mockLlmClient.createEmbedding("Vector Y Content")).thenReturn(new double[]{0.0, 1.0});
        
        // 3. We simulate a user query that is perfectly aligned with "Vector X"
        when(mockLlmClient.createEmbedding("Give me X")).thenReturn(new double[]{1.0, 0.0});

        // 4. We initialize the database and load the file
        DocumentStore store = new DocumentStore(mockLlmClient);
        store.ingestDocument(tempFile);

        // 5. We search for the query
        List<String> results = store.search("Give me X", 1); // We want only the best result (Top K = 1)

        // 5. We verify that the mathematics worked correctly
        assertEquals(1, results.size());
        assertEquals("Vector X Content", results.get(0));
    }

    @Test
    void testSearchOnEmptyDatabase() {
        DocumentStore store = new DocumentStore(mockLlmClient);
        List<String> results = store.search("Any query", 3);
        assertTrue(results.isEmpty());
    }
}