package com.DominikKubacka.supportai.agents;

import com.DominikKubacka.supportai.core.LLMClient;
import com.DominikKubacka.supportai.core.Message;
import com.DominikKubacka.supportai.tools.BillingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

/**
 * The Billing Specialist Agent (Tool Calling with Gemini).
 * 
 * @author Dominik Kubacka
 */
public class BillingAgent {
    private final LLMClient llmClient;
    private final BillingService billingService;
    private final ObjectMapper mapper;
    private final String model = "gemini-2.5-flash";

    public BillingAgent(LLMClient llmClient, BillingService billingService) {
        this.llmClient = llmClient;
        this.billingService = billingService;
        this.mapper = new ObjectMapper();
    }

    public String handle(List<Message> history) {
        ArrayNode tools = buildGeminiTools();
        String systemInstruction = 
            "You are a Billing Support Specialist. Your job is to help customers with payments, plans, and refunds.\n" +
            "You have access to backend tools. You MUST use these tools to answer questions.\n" +
            "Rules:\n" +
            "1. Assume the logged-in customerId is 'CUST-123' unless stated otherwise.\n" +
            "2. If a tool requires specific parameters (like transactionId or a reason for a refund) and the user hasn't provided them, DO NOT guess. Politely ask the user to provide the missing information.\n" +
            "3. When confirming a refund, briefly explain that refunds typically process within 5-7 business days.";
        
        List<Message> agentMessages = new ArrayList<>(history);
        int maxLoops = 5;
        int loopCount = 0;

        while (loopCount < maxLoops) {
            loopCount++;
            JsonNode responsePart = llmClient.generateContent(agentMessages, systemInstruction, model, 0.2, tools);
            
            if (responsePart.has("functionCall")) {
                JsonNode functionCall = responsePart.get("functionCall");
                String functionName = functionCall.path("name").asText();
                JsonNode args = functionCall.path("args");
                
                // 1. Append model's request to history
                agentMessages.add(Message.createFunctionCall(functionName, args));

                // 2. Execute local Java method
                String resultStr = executeTool(functionName, args);
                
                // 3. Append our response to history
                ObjectNode resultNode = mapper.createObjectNode().put("result", resultStr);
                agentMessages.add(Message.createFunctionResponse(functionName, resultNode));
            } else {
                // Final text response
                return responsePart.get("text").asText();
            }
        }
        return "I encountered an error processing your billing request.";
    }

    private String executeTool(String functionName, JsonNode args) {
        try {
            switch (functionName) {
                case "checkPlan": return billingService.checkPlan(args.path("customerId").asText());
                case "getBillingHistory": return billingService.getBillingHistory(args.path("customerId").asText());
                case "openRefundCase": return billingService.openRefundCase(
                        args.path("customerId").asText(), args.path("transactionId").asText(), args.path("reason").asText());
                default: return "Unknown function";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private ArrayNode buildGeminiTools() {
        ArrayNode declarations = mapper.createArrayNode();

        // Tool 1: checkPlan
        ObjectNode checkPlan = declarations.addObject();
        checkPlan.put("name", "checkPlan");
        checkPlan.put("description", "Check the current subscription plan for a customer.");
        ObjectNode cpParams = checkPlan.putObject("parameters");
        cpParams.put("type", "OBJECT");
        cpParams.putObject("properties").putObject("customerId").put("type", "STRING");
        cpParams.putArray("required").add("customerId");

        // Tool 2: getBillingHistory
        ObjectNode getHistory = declarations.addObject();
        getHistory.put("name", "getBillingHistory");
        getHistory.put("description", "Retrieve the recent invoices and billing history.");
        ObjectNode ghParams = getHistory.putObject("parameters");
        ghParams.put("type", "OBJECT");
        ghParams.putObject("properties").putObject("customerId").put("type", "STRING");
        ghParams.putArray("required").add("customerId");

        // Tool 3: openRefundCase
        ObjectNode openRefund = declarations.addObject();
        openRefund.put("name", "openRefundCase");
        openRefund.put("description", "Open a refund request case.");
        ObjectNode orParams = openRefund.putObject("parameters");
        orParams.put("type", "OBJECT");
        ObjectNode orProps = orParams.putObject("properties");
        orProps.putObject("customerId").put("type", "STRING");
        orProps.putObject("transactionId").put("type", "STRING");
        orProps.putObject("reason").put("type", "STRING");
        orParams.putArray("required").add("customerId").add("transactionId").add("reason");

        return declarations;
    }
}