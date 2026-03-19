package com.DominikKubacka.supportai.core;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.DominikKubacka.supportai.agents.BillingAgent;
import com.DominikKubacka.supportai.agents.RouterAgent;
import com.DominikKubacka.supportai.agents.TechnicalAgent;
import com.DominikKubacka.supportai.rag.DocumentStore;
import com.DominikKubacka.supportai.tools.BillingService;

/**
 * The central orchestrator of the Support AI system.
 * It manages the conversation state, initializes all agents,
 * and routes user messages based on intent.
 * 
 * @author Dominik Kubacka
 */
public class ConversationManager {
    private final List<Message> history;
    private final RouterAgent routerAgent;
    private final TechnicalAgent technicalAgent;
    private final BillingAgent billingAgent;

    public ConversationManager() {
        // 1. Core initialization
        this.history = new ArrayList<>();
        LLMClient llmClient = new LLMClient();
        
        // 2. Initialize Routing Agent
        this.routerAgent = new RouterAgent(llmClient);
        
        // 3. Initialize Technical Agent and RAG (Retrieval-Augmented Generation)
        DocumentStore documentStore = new DocumentStore(llmClient);
        ingestDocumentation(documentStore);
        this.technicalAgent = new TechnicalAgent(llmClient, documentStore);
        
        // 4. Initialize Billing Agent and Mock Backend Tools
        BillingService billingService = new BillingService();
        this.billingAgent = new BillingAgent(llmClient, billingService);
    }

    /**
     * Loads the local documentation files into the in-memory vector store.
     */
    private void ingestDocumentation(DocumentStore store) {
        try {
            System.out.println("[System] Initializing knowledge base...");
            store.ingestDocument(Paths.get("docs/troubleshooting_hubspot.md"));
            store.ingestDocument(Paths.get("docs/api_rate_limits.md"));
            store.ingestDocument(Paths.get("docs/sso_setup_guide.md"));
        } catch (Exception e) {
            System.err.println("[Warning] Failed to ingest some documents. RAG might be incomplete: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
        }
    }

    /**
     * Starts the interactive command-line interface.
     */
    public void start() {
        System.out.println("\n=========================================================");
        System.out.println("   Support AI System Initialized (Author: Dominik Kubacka)      ");
        System.out.println("   Agents: Technical, Billing, Router                    ");
        System.out.println("   Type 'exit' or 'quit' to end the session.             ");
        System.out.println("=========================================================");
        
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nUser: ");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("exit") || userInput.equalsIgnoreCase("quit")) {
                System.out.println("System: Ending conversation. Goodbye!");
                break;
            }

            if (userInput.trim().isEmpty()) continue;

            // 1. Add user message to the shared conversation history
            history.add(new Message("user", userInput));

            // 2. Determine which agent should handle the message
            String intent = routerAgent.determineIntent(history);
            System.out.println("\n[Router Agent] Classification: " + intent);

            // 3. Delegate execution to the specialized agent
            String agentResponse;
            try {
                if ("TECHNICAL".equals(intent)) {
                    agentResponse = technicalAgent.handle(history);
                } else if ("BILLING".equals(intent)) {
                    agentResponse = billingAgent.handle(history);
                } else {
                    // OUT_OF_SCOPE or fallback
                    agentResponse = "I'm sorry, but I cannot assist with that request. Please contact our general support team.";
                }
            } catch (Exception e) {
                agentResponse = "I apologize, but I encountered an internal error processing that request.";
                System.err.println("[Error] Agent execution failed: " + e.getMessage());
            }

            // 4. Record and display the response
            history.add(new Message("model", agentResponse));
            System.out.println("\nAgent: " + agentResponse);
        }
        scanner.close();
    }
}