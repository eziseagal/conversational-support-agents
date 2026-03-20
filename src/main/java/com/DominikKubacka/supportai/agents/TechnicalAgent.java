package com.DominikKubacka.supportai.agents;

import com.DominikKubacka.supportai.core.LLMClient;
import com.DominikKubacka.supportai.core.Message;
import com.DominikKubacka.supportai.rag.DocumentStore;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * The Technical Specialist Agent (RAG).
 * 
 * @author Dominik Kubacka
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
            "You are a Technical Support Specialist. Your goal is to help users with technical issues.\n" +
            "Answer the user's question using ONLY the information provided in the CONTEXT below.\n" +
            "IMPORTANT: You must not guess or hallucinate. If the answer is not present in the CONTEXT, " +
            "you must clearly state that the information is not available in your documentation and politely ask the user to clarify their request.\n\n" +
            "CONTEXT:\n" + contextString;

        JsonNode response = llmClient.generateContent(history, systemPrompt, model, 0.2, null);
        return response.path("text").asText();
    }
}