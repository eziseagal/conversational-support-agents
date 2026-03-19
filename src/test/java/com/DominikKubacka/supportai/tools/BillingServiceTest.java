package com.DominikKubacka.supportai.tools;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BillingServiceTest {

    @Test
    void testCheckPlan() {
        BillingService service = new BillingService();
        String response = service.checkPlan("CUST-999");
        assertTrue(response.contains("CUST-999"));
        assertTrue(response.contains("Enterprise Plan"));
    }

    @Test
    void testOpenRefundCase() {
        BillingService service = new BillingService();
        String response = service.openRefundCase("CUST-123", "TX-456", "Accidental charge");
        assertTrue(response.contains("TX-456"));
        assertTrue(response.contains("Accidental charge"));
        assertTrue(response.contains("CASE-"));
    }
}