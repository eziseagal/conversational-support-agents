package com.eziseagal.supportai.agents;

import com.eziseagal.supportai.core.LLMClient;
import com.eziseagal.supportai.core.Message;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * Evaluates the conversation context and routes the user's request.
 * 
 * @author eziseagal
 */
public class RouterAgent {
    private final LLMClient llmClient;
    private final String model = "gemini-2.5-flash";

    private static final String ROUTER_SYSTEM_PROMPT = 
        "You are a routing supervisor. Classify the user's latest message into EXACTLY ONE category:\n" +
        "1. TECHNICAL: Troubleshooting, API, setup, integration.\n" +
        "2. BILLING: Refunds, pricing, plans, charges, history.\n" +
        "3. OUT_OF_SCOPE: Anything else.\n" +
        "Respond with ONLY the category name (TECHNICAL, BILLING, or OUT_OF_SCOPE).";

    public RouterAgent(LLMClient llmClient) {
        this.llmClient = llmClient;
    }

    public String determineIntent(List<Message> history) {
        JsonNode response = llmClient.generateContent(history, ROUTER_SYSTEM_PROMPT, model, 0.0, null);
        return response.path("text").asText().trim().toUpperCase();
    }
}