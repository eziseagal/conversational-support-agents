package com.DominikKubacka.supportai.agents;

import com.DominikKubacka.supportai.core.LLMClient;
import com.DominikKubacka.supportai.core.Message;
import com.DominikKubacka.supportai.tools.BillingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillingAgentTest {

    @Mock private LLMClient mockLlmClient;
    @Mock private BillingService mockBillingService;

    @Test
    void testToolCallingCheckPlan() {
        ObjectMapper mapper = new ObjectMapper();

        // 1. SIMULATE STEP 1: Gemini API asks to use the "checkPlan" function
        ObjectNode funcCallResponse = mapper.createObjectNode();
        ObjectNode functionCall = funcCallResponse.putObject("functionCall");
        functionCall.put("name", "checkPlan");
        functionCall.putObject("args").put("customerId", "CUST-123");

        // 2. SIMULATE STEP 2: Gemini API returns the final text after receiving the function result
        ObjectNode finalResponse = mapper.createObjectNode();
        finalResponse.put("text", "Your current plan is Premium.");

        // Teach the LLMClient to return the function call first, then the final response
        when(mockLlmClient.generateContent(anyList(), anyString(), anyString(), anyDouble(), any()))
                .thenReturn(funcCallResponse, finalResponse);

        // Teach the BillingService how to respond when Java calls this tool
        when(mockBillingService.checkPlan("CUST-123")).thenReturn("Premium Plan Data");

        // 3. Launch the test agent
        BillingAgent agent = new BillingAgent(mockLlmClient, mockBillingService);
        List<Message> dummyHistory = List.of(new Message("user", "What is my plan?"));

        String response = agent.handle(dummyHistory);

        // 4. Verify that the agent returned the final text...
        assertEquals("Your current plan is Premium.", response);
        
        // ...and whether it actually used BillingService during its while loop!
        verify(mockBillingService, times(1)).checkPlan("CUST-123");
    }
}