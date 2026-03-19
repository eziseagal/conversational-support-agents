package com.DominikKubacka.supportai.tools;

/**
 * Mock backend service for billing operations.
 * 
 * @author Dominik Kubacka
 */
public class BillingService {

    public String checkPlan(String customerId) {
        System.out.println("[Backend Executing] checkPlan for " + customerId);
        return "Customer " + customerId + " is currently on the Enterprise Plan ($499/month). Next billing date is the 1st of next month.";
    }

    public String getBillingHistory(String customerId) {
        System.out.println("[Backend Executing] getBillingHistory for " + customerId);
        return "Recent invoices for " + customerId + ":\n" +
               "- INV-001: $499 (Paid)\n" +
               "- INV-002: $499 (Paid)\n" +
               "- INV-003: $499 (Pending - Suspected duplicate)";
    }

    public String openRefundCase(String customerId, String transactionId, String reason) {
        System.out.println("[Backend Executing] openRefundCase for " + customerId + ", TX: " + transactionId);
        String caseId = "CASE-" + (int)(Math.random() * 10000);
        return "Refund case " + caseId + " successfully opened for transaction " + transactionId + 
               ". Reason: " + reason + ". Our policy states refunds are processed within 5-7 business days.";
    }
}