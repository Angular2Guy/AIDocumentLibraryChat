# AI Coding Agent Instructions for AIDocumentLibraryChat

## Architecture Overview
This is a Spring Boot 3.5+ application with an embedded Angular frontend, demonstrating Spring AI 1.1+ capabilities for document/image chat, SQL generation, and function calling. It uses PostgreSQL with pgvector for vector storage and embeddings.

- **Backend**: REST controllers (Document, Image, Table/SQL, Function) → Services → Repositories (JPA + VectorStore) → ChatClient for AI interactions.
- **Frontend**: Angular SPA for uploads, searches, and result display.
- **Data Flow**: Upload content → Chunk/embed/store in vector DB → Query embed → Vector search → AI generates response with source links.
- **Why this structure**: Enables RAG on personal documents/images/DBs, reducing hallucinations by grounding responses in stored content.

Key files: `structurizr/workspace.dsl` (C4 diagrams), `backend/src/main/resources/application.properties` (configs).

## Critical Workflows
- **Build**: `./gradlew clean build -PwithAngular=true` (includes npm build). Add `-PuseOllama=true` for Ollama dependencies.
- **Run**: `java -jar backend/build/libs/aidocumentlibrarychat.jar --spring.profiles.active=ollama` (or default for OpenAI).
- **Services**: `./runPostgresql.sh` (DB), `./runOllama.sh` (local AI), `./runStructurizr.sh` (diagrams).
- **Debug**: Check AI prompts/responses in logs. Use profiles to switch models. Test with `curl` to REST endpoints.

## Project-Specific Patterns
- **AI Integration**: Use `ChatClient` with prompts for tasks (e.g., `@SystemMessage` for RAG). Embeddings via OpenAI API or ONNX transformers.
- **Model Selection**: Different Ollama models per feature (e.g., `qwen2.5:32b` for docs, `llama3.2-vision` for images). Configured in `application-ollama.properties`.
- **Token Management**: Set `document-token-limit`, `embedding-token-limit` per profile to control context.
- **DB Migrations**: Liquibase changelogs in `backend/src/main/resources/dbchangelog/`. Separate for Ollama (includes vector setup).
- **MCP Tools**: Client connects to external servers via SSE for book/movie data. Enabled with `spring.ai.mcp.client.enabled=true`.

Examples:
- Document RAG: `DocumentService` chunks text, embeds, stores; queries use cosine similarity.
- Image Search: `ImageService` generates descriptions via LLava, embeds them.
- SQL Gen: `TableService` uses metadata embeddings to build queries.

## Conventions
- Profiles: `default` (OpenAI), `ollama` (local models), `prod` (production settings).
- Properties: Environment vars for API keys (e.g., `OPENAI-API-KEY`, `OLLAMA-BASE-URL`).
- Testing: Unit tests with JUnit; integration via Spring Boot Test. ArchUnit for architecture checks.
- Dependencies: Managed via Spring BOM; vector store via `spring-ai-starter-vector-store-pgvector`.

Reference: `backend/build.gradle` (dependencies), `application-ollama.properties` (model configs).