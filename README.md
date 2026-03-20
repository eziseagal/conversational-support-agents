# Conversational AI Support Agents (Java Implementation)

**Author:** Dominik Kubacka  
**Stack:** Java 17, Maven, Jackson (JSON), Google Gemini API, JUnit 5, Mockito.

## Overview
This is a multi-agent conversational support system built from scratch in Java without the use of agentic frameworks like LangChain. It features a **Router-Worker** architecture to handle technical and billing inquiries dynamically.

## Key Features
- **Manual Orchestration:** Custom routing logic using LLM intent classification.
- **Technical Specialist (RAG):**
    - Local documentation processing (Markdown).
    - Manual Text Chunking.
    - Vector embeddings via `text-embedding-001` (Gemini).
    - **Manual Cosine Similarity** implementation for semantic search.
- **Billing Specialist (Tool Calling):**
    - Manual implementation of the Gemini tool-calling loop.
    - Mock backend integration for plan checks, billing history, and refund cases.
- **BillingServiceTest:** Verifies mock backend logic and enforces security constraints, ensuring that unauthorized customer ID access attempts trigger `SecurityExceptions`.
- **Context Preservation:** Full conversation history is maintained across agent switches.

## Security & Guardrails
The system implements a **Defense-in-Depth** strategy:
1. **Semantic Routing:** The Router Agent acts as a firewall, filtering out-of-scope requests.
2. **LLM Instructions:** System prompts define strict boundaries for each agent.
3. **Backend Validation:** The `BillingService` performs hard-coded authorization checks on every tool call, ensuring that even a compromised LLM cannot access unauthorized `customerId` data.

## Project Structure
- `src/main/java/com/DominikKubacka/supportai/core`: Core LLM client and conversation management.
- `src/main/java/com/DominikKubacka/supportai/agents`: Specialist agents (Technical, Billing, Router).
- `src/main/java/com/DominikKubacka/supportai/rag`: Manual RAG components (DocumentStore, TextChunker).
- `src/main/java/com/DominikKubacka/supportai/tools`: Backend service mocks.
- `docs/`: Technical documentation used by the Technical Agent.

## Example Scenarios
The system's behavior is documented in detail in `EXAMPLE_CONVERSATIONS.md`. Key scenarios include:

- `Contextual Switching:` Seamlessly moving from technical troubleshooting to billing inquiries.
- `RAG Precision:` Refusing to answer technical questions not covered in the local `docs/`.
- `Security Guardrails:` Gracefully rejecting out-of-scope requests (e.g., "Write a Python script" or "What's the weather?").
- `Authorization Enforcement:` Blocking attempts to access billing data of other customers (e.g., CUST-999).

## Model Configuration
- `Default Model:` gemini-2.5-flash (chosen for its speed and high accuracy in Tool Calling and Routing tasks).
- `Embedding Model:` text-embedding-001 (used for generating semantic vectors for documentation).

## Setup and Running

1. **Prerequisites:**
   - JDK 17 (e.g., OpenJDK 17.0+)
   - Maven (e.g., 3.8+)
   - Google Gemini API Key

2. **Set API Key:**
   Since you are using Windows PowerShell, set the environment variable like this:
   ```powershell
   $env:GEMINI_API_KEY="your_api_key_gemini"
   ```
   *(For Linux/macOS use: `export GEMINI_API_KEY="your_api_key_gemini"`)*

3. **Build and Run:**
   The project is configured with the `exec-maven-plugin`, so you can compile and run it directly with Maven:
   ```powershell
   mvn clean compile
   mvn exec:java
   ```

## Testing Strategy
The system includes a comprehensive suite of **16 unit tests** designed to verify core business logic, agent orchestration, and mathematical operations **without making live HTTP calls** to the Gemini API. 

This is achieved using **Dependency Injection** and **Mockito** to mock the `LLMClient` and HTTP responses, ensuring tests are fast, deterministic, and cost-free.

* **`BillingAgentTest`**: Validates the **Tool Calling loop** (ReAct pattern), ensuring the agent correctly parses function requests, executes local Java methods, and returns the result to the LLM.
* **`RouterAgentTest`**: Verifies deterministic intent classification and dynamic routing logic.
* **`TechnicalAgentTest`**: Tests the **RAG workflow** using Mockito's `ArgumentCaptor` to ensure retrieved documentation chunks are properly injected into the System Prompt.
* **`MessageTest`**: Validates the mapping between Java objects and the Gemini API JSON structure.
* **`DocumentStoreTest`**: Verifies the accuracy of the **manual Cosine Similarity** math ($\frac{A \cdot B}{||A|| ||B||}$) used for vector search.
* **`TextChunkerTest`**: Validates the regex-based semantic splitting of Markdown files into clean, processable text chunks.
* **`BillingServiceTest`**: Verifies mock backend logic and enforces security constraints, ensuring that unauthorized customer ID access attempts trigger SecurityExceptions.

**To run the test suite:**
```powershell
mvn clean test
```