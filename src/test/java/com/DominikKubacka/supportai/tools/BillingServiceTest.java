package com.DominikKubacka.supportai.tools;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BillingServiceTest {

    @Test
    void testCheckPlanWithAuthorizedCustomer() {
        BillingService service = new BillingService();
        String response = service.checkPlan("CUST-123");
        assertTrue(response.contains("CUST-123"));
        assertTrue(response.contains("Enterprise Plan"));
    }

    @Test
    void testCheckPlanWithUnauthorizedCustomer() {
        BillingService service = new BillingService();
        // We test whether the system actually throws an error for a foreign ID, which is a critical security check
        assertThrows(SecurityException.class, () -> {
            service.checkPlan("CUST-999");
        });
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