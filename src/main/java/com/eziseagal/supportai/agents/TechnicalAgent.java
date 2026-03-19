package com.eziseagal.supportai.agents;

import com.eziseagal.supportai.core.LLMClient;
import com.eziseagal.supportai.core.Message;
import com.eziseagal.supportai.rag.DocumentStore;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * The Technical Specialist Agent (RAG).
 * 
 * @author eziseagal
 */
public class TechnicalAgent {
    private final LLMClient llmClient;
    private final DocumentStore documentStore;
    private final String model = "gemini-2.5-flash";

    public TechnicalAgent(LLMClient llmClient, DocumentStore documentStore) {
        this.llmClient = llmClient;
        this.documentStore = documentStore;
    }

    public String handle(List<Message> history) {
        String userQuery = history.get(history.size() - 1).getText();
        List<String> relevantChunks = documentStore.search(userQuery, 3);
        String contextString = String.join("\n\n---\n\n", relevantChunks);

        String systemPrompt = 
            "You are a Technical Support Specialist. Answer using ONLY the CONTEXT below. " +
            "If the answer is not in the CONTEXT, reply exactly with: " +
            "\"I'm sorry, but I don't have that information in my documentation.\"\n\n" +
            "CONTEXT:\n" + contextString;

        JsonNode response = llmClient.generateContent(history, systemPrompt, model, 0.2, null);
        return response.path("text").asText();
    }
}