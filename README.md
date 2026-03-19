# Conversational AI Support Agents (Java Implementation)

**Author:** eziseagal  
**Stack:** Java 17, Maven, Jackson (JSON), Google Gemini API.

## Overview
This is a multi-agent conversational support system built from scratch in Java without the use of agentic frameworks like LangChain. It features a **Router-Worker** architecture to handle technical and billing inquiries dynamically.

## Key Features
- **Manual Orchestration:** Custom routing logic using LLM intent classification.
- **Technical Specialist (RAG):**
    - Local documentation processing (Markdown).
    - Manual Text Chunking.
    - Vector embeddings via `text-embedding-004` (Gemini).
    - **Manual Cosine Similarity** implementation for semantic search.
- **Billing Specialist (Tool Calling):**
    - Manual implementation of the Gemini tool-calling loop.
    - Mock backend integration for plan checks, billing history, and refund cases.
- **Context Preservation:** Full conversation history is maintained across agent switches.

## Project Structure
- `src/main/java/com/eziseagal/supportai/core`: Core LLM client and conversation management.
- `src/main/java/com/eziseagal/supportai/agents`: Specialist agents (Technical, Billing, Router).
- `src/main/java/com/eziseagal/supportai/rag`: Manual RAG components.
- `src/main/java/com/eziseagal/supportai/tools`: Backend service mocks.
- `docs/`: Technical documentation used by the Technical Agent.

## Setup and Running

1. **Prerequisites:**
   - JDK 17 (e.g., OpenJDK 17.0.18)
   - Maven (e.g., 3.9.14)
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
