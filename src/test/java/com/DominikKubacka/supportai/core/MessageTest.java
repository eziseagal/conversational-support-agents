package com.DominikKubacka.supportai.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    void testStandardTextPayload() {
        Message msg = new Message("user", "Hello Gemini");
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode parts = mapper.createArrayNode();

        msg.fillParts(parts);

        // We check if it correctly generated the JSON structure ("parts": [{"text": "Hello Gemini"}])
        assertEquals(1, parts.size());
        assertTrue(parts.get(0).has("text"));
        assertEquals("Hello Gemini", parts.get(0).get("text").asText());
    }
}