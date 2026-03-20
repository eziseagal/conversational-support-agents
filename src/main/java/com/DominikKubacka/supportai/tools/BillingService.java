package com.DominikKubacka.supportai.tools;

/**
 * Mock backend service for billing operations.
 * 
 * @author Dominik Kubacka
 */
public class BillingService {

    // In a real app, this would come from a SecurityContext or Session
    private static final String LOGGED_IN_CUSTOMER_ID = "CUST-123";

    private void validateCustomer(String customerId) {
        if (!LOGGED_IN_CUSTOMER_ID.equals(customerId)) {
            throw new SecurityException("Unauthorized access: You can only access data for your own account (" + LOGGED_IN_CUSTOMER_ID + ").");
        }
    }

    public String checkPlan(String customerId) {
        validateCustomer(customerId);
        System.out.println("[Backend Executing] checkPlan for " + customerId);
        return "Customer " + customerId + " is currently on the Enterprise Plan ($499/month). Next billing date is the 1st of next month.";
    }

    public String getBillingHistory(String customerId) {
        validateCustomer(customerId);
        System.out.println("[Backend Executing] getBillingHistory for " + customerId);
        return "Recent invoices for " + customerId + ":\n" +
               "- INV-001: $499 (Paid)\n" +
               "- INV-002: $499 (Paid)\n" +
               "- INV-003: $499 (Pending - Suspected duplicate)";
    }

    public String openRefundCase(String customerId, String transactionId, String reason) {
        validateCustomer(customerId);
        System.out.println("[Backend Executing] openRefundCase for " + customerId + ", TX: " + transactionId);
        String caseId = "CASE-" + (int)(Math.random() * 10000);
        return "Refund case " + caseId + " successfully opened for transaction " + transactionId + 
               ". Reason: " + reason + ". Our policy states refunds are processed within 5-7 business days.";
    }
}