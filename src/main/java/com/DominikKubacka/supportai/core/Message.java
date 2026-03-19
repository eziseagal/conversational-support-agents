package com.DominikKubacka.supportai.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents a single message in the Gemini conversation history.
 * Roles: "user", "model", or "function".
 * 
 * @author Dominik Kubacka
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {
    private String role;
    private String text;
    private String functionName;
    private JsonNode functionArgs;
    private JsonNode functionResponse;

    public Message() {}

    // Standard text message
    public Message(String role, String text) {
        this.role = role;
        this.text = text;
    }

    // Constructor for Model calling a function
    public Message(String role, String functionName, JsonNode functionArgs) {
        this.role = role;
        this.functionName = functionName;
        this.functionArgs = functionArgs;
    }

    // Constructor for Function response
    public static Message createFunctionResponse(String functionName, JsonNode response) {
        Message m = new Message();
        m.role = "user"; // Gemini API expects 'user' role for function responses
        m.functionName = functionName;
        m.functionResponse = response;
        return m;
    }

    // Constructor for Function call
    public static Message createFunctionCall(String functionName, JsonNode args) {
        Message m = new Message();
        m.role = "model";
        m.functionName = functionName;
        m.functionArgs = args;
        return m;
    }

    public String getRole() { return role; }
    public String getText() { return text; }
    public String getFunctionName() { return functionName; }
    public JsonNode getFunctionArgs() { return functionArgs; }
    public JsonNode getFunctionResponse() { return functionResponse; }

    /**
     * Maps this message object into Gemini's "parts" JSON structure.
     */
    public void fillParts(ArrayNode parts) {
        ObjectNode part = parts.addObject();
        if (text != null) {
            part.put("text", text);
        } else if (functionName != null && "model".equals(role)) {
            ObjectNode funcCall = part.putObject("functionCall");
            funcCall.put("name", functionName);
            funcCall.set("args", functionArgs);
        } else if (functionName != null && functionResponse != null) {
            ObjectNode funcResp = part.putObject("functionResponse");
            funcResp.put("name", functionName);
            funcResp.putObject("response").set("content", functionResponse);
        }
    }
}