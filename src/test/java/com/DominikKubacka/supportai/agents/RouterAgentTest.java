package com.DominikKubacka.supportai.agents;

import com.DominikKubacka.supportai.core.LLMClient;
import com.DominikKubacka.supportai.core.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RouterAgentTest {

    @Mock
    private LLMClient mockLlmClient;

    @Test
    void testRoutesToTechnical() {
        // 1. We prepare an artificial response from the Gemini API (e.g. the model decided it was TECHNICAL)
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode fakeGeminiResponse = mapper.createObjectNode();
        fakeGeminiResponse.put("text", "TECHNICAL\n"); // We simulate a newline to check trim()

        // 2. We teach mocking: When someone calls generateContent with any arguments, return our JSON
        when(mockLlmClient.generateContent(anyList(), anyString(), anyString(), anyDouble(), isNull()))
                .thenReturn(fakeGeminiResponse);

        // 3. We launch our agent with a "fake" client inside
        RouterAgent agent = new RouterAgent(mockLlmClient);
        List<Message> dummyHistory = List.of(new Message("user", "My router is blinking red"));
        
        String intent = agent.determineIntent(dummyHistory);

        // 4. We verify if the agent correctly interpreted the intent
        assertEquals("TECHNICAL", intent);
    }
    
    @Test
    void testRoutesToBilling() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode fakeGeminiResponse = mapper.createObjectNode();
        fakeGeminiResponse.put("text", " BILLING "); 

        when(mockLlmClient.generateContent(anyList(), anyString(), anyString(), anyDouble(), isNull()))
                .thenReturn(fakeGeminiResponse);

        RouterAgent agent = new RouterAgent(mockLlmClient);
        List<Message> dummyHistory = List.of(new Message("user", "I want a refund"));
        
        String intent = agent.determineIntent(dummyHistory);

        assertEquals("BILLING", intent);
    }
}