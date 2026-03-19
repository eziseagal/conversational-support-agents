package com.eziseagal.supportai.rag;

import com.eziseagal.supportai.core.LLMClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An in-memory vector database that stores document chunks and performs 
 * manual cosine similarity search.
 * 
 * @author eziseagal
 */
public class DocumentStore {
    private final LLMClient llmClient;
    private final List<DocumentChunk> chunksDatabase;

    public DocumentStore(LLMClient llmClient) {
        this.llmClient = llmClient;
        this.chunksDatabase = new ArrayList<>();
    }

    /**
     * Reads a file, chunks it, generates embeddings, and stores them.
     */
    public void ingestDocument(Path filePath) {
        try {
            String content = Files.readString(filePath);
            List<String> textChunks = TextChunker.chunkText(content);
            
            for (String text : textChunks) {
                double[] embedding = llmClient.createEmbedding(text);
                chunksDatabase.add(new DocumentChunk(text, embedding));
            }
            System.out.println("[System] Ingested document: " + filePath.getFileName() + " (" + textChunks.size() + " chunks)");
        } catch (IOException e) {
            System.err.println("Failed to read document: " + filePath);
        }
    }

    /**
     * Searches the store for the most relevant chunks using Cosine Similarity.
     */
    public List<String> search(String query, int topK) {
        if (chunksDatabase.isEmpty()) return new ArrayList<>();

        // 1. Embed the user's query
        double[] queryEmbedding = llmClient.createEmbedding(query);

        // 2. Calculate similarity for all chunks
        List<SearchResult> results = new ArrayList<>();
        for (DocumentChunk chunk : chunksDatabase) {
            double similarity = calculateCosineSimilarity(queryEmbedding, chunk.embedding);
            results.add(new SearchResult(chunk.text, similarity));
        }

        // 3. Sort by highest similarity descending and take top K
        return results.stream()
                .sorted(Comparator.comparingDouble(SearchResult::getSimilarity).reversed())
                .limit(topK)
                .map(SearchResult::getText)
                .collect(Collectors.toList());
    }

    /**
     * Manual implementation of Cosine Similarity.
     * Formula: (A · B) / (||A|| * ||B||)
     */
    private double calculateCosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // --- Inner Data Classes ---

    private static class DocumentChunk {
        String text;
        double[] embedding;

        DocumentChunk(String text, double[] embedding) {
            this.text = text;
            this.embedding = embedding;
        }
    }

    private static class SearchResult {
        String text;
        double similarity;

        SearchResult(String text, double similarity) {
            this.text = text;
            this.similarity = similarity;
        }
        public double getSimilarity() { return similarity; }
        public String getText() { return text; }
    }
}