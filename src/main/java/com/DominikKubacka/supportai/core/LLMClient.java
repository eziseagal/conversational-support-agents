package com.DominikKubacka.supportai.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * A lightweight HTTP client for interacting with the Google Gemini API.
 * 
 * @author Dominik Kubacka
 */
public class LLMClient {
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public LLMClient() {
        this.apiKey = System.getenv("GEMINI_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new IllegalStateException("GEMINI_API_KEY environment variable is missing.");
        }
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        this.mapper = new ObjectMapper();
    }

    /**
     * Generates a vector embedding using text-embedding-001.
     */
    public double[] createEmbedding(String text) {
        try {
            ObjectNode payload = mapper.createObjectNode();
            payload.put("model", "models/gemini-embedding-001");
            payload.putObject("content").putArray("parts").addObject().put("text", text);

            String url = BASE_URL + "gemini-embedding-001:embedContent?key=" + apiKey;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) throw new RuntimeException("Embedding Error: " + response.body());

            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode values = rootNode.path("embedding").path("values");
            
            if (values.isMissingNode() || !values.isArray()) {
                throw new RuntimeException("Invalid embedding response: " + response.body());
            }

            double[] embedding = new double[values.size()];
            for (int i = 0; i < values.size(); i++) embedding[i] = values.get(i).asDouble();
            return embedding;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embedding", e);
        }
    }

    /**
     * Sends a chat completion request to Gemini (gemini-2.5-flash or gemini-2.5-pro).
     */
    public JsonNode generateContent(List<Message> messages, String systemInstruction, String model, double temperature, ArrayNode tools) {
        try {
            ObjectNode payload = mapper.createObjectNode();
            
            ObjectNode config = payload.putObject("generationConfig");
            config.put("temperature", temperature);

            if (systemInstruction != null && !systemInstruction.isEmpty()) {
                payload.putObject("systemInstruction")
                       .putArray("parts")
                       .addObject()
                       .put("text", systemInstruction);
            }

            if (tools != null && !tools.isEmpty()) {
                payload.putArray("tools").addObject().set("functionDeclarations", tools);
            }

            ArrayNode contents = payload.putArray("contents");
            for (Message msg : messages) {
                ObjectNode contentNode = contents.addObject();
                contentNode.put("role", msg.getRole());
                ArrayNode parts = contentNode.putArray("parts");

                // Logika mapowania Message -> Gemini Parts (musi być obsłużona w klasie Message)
                msg.fillParts(parts);
            }

            String url = BASE_URL + model + ":generateContent?key=" + apiKey;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) throw new RuntimeException("Gemini API Error: " + response.body());

            JsonNode rootNode = mapper.readTree(response.body());
            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isMissingNode() || !candidates.isArray() || candidates.isEmpty()) {
                throw new RuntimeException("No candidates returned by Gemini API: " + response.body());
            }
            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (parts.isMissingNode() || !parts.isArray() || parts.isEmpty()) {
                throw new RuntimeException("No parts returned by Gemini API: " + response.body());
            }
            return parts.get(0);

        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with Gemini", e);
        }
    }
}