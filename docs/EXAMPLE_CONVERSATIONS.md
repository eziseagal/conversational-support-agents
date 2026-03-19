# Example Conversations: Agent Switching

The following transcripts demonstrate the Support AI's ability to maintain conversation context and dynamically route requests between specialized agents (Technical, Billing, and Out-of-Scope) based on user intent.


## Scenario 1: Technical Support to Billing Inquiry
*Demonstrates the RAG system answering a technical question, followed by a seamless switch to the Billing Agent using Tool Calling to check the user's plan*

=========================================================
   Support AI System Initialized (Author: Dominik Kubacka)
   Agents: Technical, Billing, Router
   Type 'exit' or 'quit' to end the session.
=========================================================

**User:** I'm trying to set up SSO for my team, but I'm not sure which providers are supported. Can you help?

`[Router Agent] Classification: TECHNICAL`

**Agent:** We currently support SAML 2.0 and OpenID Connect (OIDC) for Enterprise customers. Supported Identity Providers (IdPs) include Okta, Microsoft Entra ID (formerly Azure AD), and Google Workspace.

**User:** Thanks! By the way, I think we are on the Pro plan. Does our plan support SSO? My customer ID is CUST-123.

`[Router Agent] Classification: BILLING`
`[Backend Executing] checkPlan for CUST-123`

**Agent:** Yes, your current Enterprise Plan supports SSO.


## Scenario 2: Troubleshooting to Refund Request
*Demonstrates the RAG system diagnosing an integration issue, and then routing an angry customer to the Billing Agent to process a refund using multiple tool arguments.*

**User:** My HubSpot integration keeps disconnecting every 30 minutes. What's going on?

`[Router Agent] Classification: TECHNICAL`

**Agent:** HubSpot OAuth tokens expire every 30 minutes. You need to ensure your backend is properly using the refresh token to generate a new access token. If the refresh token is invalid, the user must re-authenticate.

**User:** This is unacceptable, we haven't been able to use the integration all week. I want a refund for my last invoice. My customer ID is CUST-777 and the transaction ID is TX-999.

`[Router Agent] Classification: BILLING`
`[Backend Executing] openRefundCase for CUST-777, TX: TX-999`

**Agent:** I have opened a refund case for you, CASE-7167, for transaction TX-999 due to the HubSpot integration issues. Please note that refunds are typically processed within 5-7 business days.     


## Scenario 3: Handling Out-of-Scope Requests
*Demonstrates the Router Agent protecting the system from prompt injection or irrelevant queries.*

**User:** Can you write a python script to scrape a website for me?

`[Router Agent] Classification: OUT_OF_SCOPE`

**Agent:** I'm sorry, but I cannot assist with that request. Please contact our general support team.