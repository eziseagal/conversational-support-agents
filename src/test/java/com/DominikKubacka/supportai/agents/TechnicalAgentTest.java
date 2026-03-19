package com.DominikKubacka.supportai.agents;

import com.DominikKubacka.supportai.core.LLMClient;
import com.DominikKubacka.supportai.core.Message;
import com.DominikKubacka.supportai.rag.DocumentStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TechnicalAgentTest {

    @Mock private LLMClient mockLlmClient;
    @Mock private DocumentStore mockDocumentStore;

    @Test
    void testRagWithFoundDocuments() {
        // 1. Setup: User asks about API limits
        String userQuery = "What are the API limits?";
        List<Message> history = List.of(new Message("user", userQuery));
        
        // 2. Simulation of the Database (DocumentStore): Return two synthetic document fragments
        List<String> mockChunks = List.of(
            "HubSpot API limit is 100 requests per second.", 
            "Use exponential backoff for retries."
        );
        when(mockDocumentStore.search(userQuery, 3)).thenReturn(mockChunks);

        // 3. Simulation of the LLM: Response from the model
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode fakeResponse = mapper.createObjectNode();
        fakeResponse.put("text", "The limit is 100 requests per second.");
        
        when(mockLlmClient.generateContent(anyList(), anyString(), anyString(), anyDouble(), isNull()))
                .thenReturn(fakeResponse);

        // 4. Execution: Launch the agent
        TechnicalAgent agent = new TechnicalAgent(mockLlmClient, mockDocumentStore);
        String response = agent.handle(history);

        // 5. Verification: Is the final text correct?
        assertEquals("The limit is 100 requests per second.", response);

        // 6. Mockito Magic: Capture the "System Prompt" to check if the agent correctly concatenated the documents
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLlmClient).generateContent(eq(history), promptCaptor.capture(), eq("gemini-2.5-flash"), eq(0.2), isNull());
        
        String capturedSystemPrompt = promptCaptor.getValue();
        
        // Ensure that the synthetic documents were correctly appended to the Prompt
        assertTrue(capturedSystemPrompt.contains("CONTEXT:\n"));
        assertTrue(capturedSystemPrompt.contains("HubSpot API limit is 100 requests per second."));
        assertTrue(capturedSystemPrompt.contains("Use exponential backoff for retries."));
    }

    @Test
    void testRagWithNoDocumentsFound() {
        // 1. Setup: User asks about something that is not in the documentation
        String userQuery = "How to hack the mainframe?";
        List<Message> history = List.of(new Message("user", userQuery));
        
        // 2. Simulation of the Database: Nothing found (empty list)
        when(mockDocumentStore.search(userQuery, 3)).thenReturn(Collections.emptyList());

        // 3. Simulation of the LLM: The model politely declines because it didn't receive any context
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode fakeResponse = mapper.createObjectNode();
        fakeResponse.put("text", "I'm sorry, but I don't have that information in my documentation.");
        
        when(mockLlmClient.generateContent(anyList(), anyString(), anyString(), anyDouble(), isNull()))
                .thenReturn(fakeResponse);

        // 4. Execution
        TechnicalAgent agent = new TechnicalAgent(mockLlmClient, mockDocumentStore);
        String response = agent.handle(history);

        // 5. Verification
        assertEquals("I'm sorry, but I don't have that information in my documentation.", response);
        
        // Capture the System Prompt and check if the CONTEXT section is empty
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLlmClient).generateContent(anyList(), promptCaptor.capture(), anyString(), anyDouble(), isNull());
        
        String capturedSystemPrompt = promptCaptor.getValue();
        // Since the "relevantChunks" list was empty, String.join didn't append anything,
        // so the entire prompt should end exactly with "CONTEXT:\n"
        assertTrue(capturedSystemPrompt.endsWith("CONTEXT:\n"));
    }
}