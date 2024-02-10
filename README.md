# AIDocumentLibraryChat
This is a project to show howto use SpringAI to chat with the documents in a library. Document can be uploaded are then stored in a normal and vector database. The AI is used to create embeddings from the chunks of the documents that are stored in the vector database. In the chat questions can be asked that are turned in embeddings and are used for requests to the vector database. The document vector with the lowest distance is used to load the document from the database and to request an answer from the AI based on document content tokens. That enables the AI to generate the answer based on the content of the document and limits hallucinations. A link to the source document is provided for further research.

The project uses Spring Boot with Spring AI to access OpenAI and the vector database. The Postgresql DB is used with the vector, hstore and the uuid-ossp extensions. Liquibase is used to manage the database migrations. Jpa is used for database access in the services by the rest controllers. For the frontend Angular, Angular Material and Typescript is used to create the table of documents, the upload dialog and the document chat. Angular Cli is used for the frontend build and Gradle for the project build.

Author: Sven Loesekann

Technologies: Angular, Angular-Cli, Angular-Material, Typescript, Spring Boot, Spring AI, OpenAI, Ollama, Postgresql(vector,hstore), Liquibase, Jpa, Gradle, Java

[![CodeQL](https://github.com/Angular2Guy/AIDocumentLibraryChat/actions/workflows/codeql.yml/badge.svg)](https://github.com/Angular2Guy/AIDocumentLibraryChat/actions/workflows/codeql.yml)

## Articles
* [Making Spring AI and OpenAI GPT useful with RAG on your own Documents](https://angular2guy.wordpress.com/2023/11/19/making-spring-ai-and-openai-gpt-useful-with-rag-on-your-own-documents/)
* [Implementing RAG With Spring AI and Ollama Using Local AI/LLM Models](https://angular2guy.wordpress.com/2023/12/17/using-spring-ai-with-ollama-for-a-local-ai-model/)

## Features
1. It shows the list of the documents.
2. It uploads new documents and creates the embeddings.
3. It provides a chat box and shows the AI answers based on the nearest document with a link.

## Mission Statement
The project shows howto use Spring AI to generate answers based on a provided set of documents with a link to the source. The Angular frontend provides the use interface for the backend and shows the responses. Spring AI makes using OpenAI / Ollama services simple and useful and this project demonstrates that. 

## C4 Architecture Diagrams
The project has a [System Context Diagram](structurizr/diagrams/structurizr-1-SystemContext.svg), a [Container Diagram](structurizr/diagrams/structurizr-1-Containers.svg) and a [Component Diagram](structurizr/diagrams/structurizr-1-Components.svg). The Diagrams have been created with Structurizr. The file runStructurizr.sh contains the commands to use Structurizr and the directory structurizr contains the dsl file.

## Kubernetes setup
In the helm directory is a kubernetes setup to run the AIDocumentLibraryChat project with minikube. The Helm chart deploys the postgres database and the AIDocumentLibraryChat with the needed parameters(SpringProfile is in values.yaml) to run. It uses the resource limit support of Jdk 16 to limit memory. Kubernetes limits the cpu use and uses the startupprobes and livenessprobes that Spring Actuator provides.


## Postgresql setup
In the [runPostgresql.sh](https://github.com/Angular2Guy/AIDocumentLibraryChat/blob/master/runPostgresql.sh) file are the commands to pull and run the Postgresql Docker image with vector extension locally. 

## OpenAI Api key
To get the OpenAI api key you need to create a account at [https://platform.openai.com](https://platform.openai.com) and create a key in the user settings. That key has to be added in the [application.properties](https://github.com/Angular2Guy/AIDocumentLibraryChat/blob/master/backend/src/main/resources/application.properties) at the key: 'spring.ai.openai.api-key'.
As alternative it can be provided as the environment variable 'OPEN-API-KEY'.

## Ollama setup
[Ollama](https://ollama.ai/) can run the AI model locally. The file [runOllama.sh](https://github.com/Angular2Guy/AIDocumentLibraryChat/blob/master/runOllama.sh) has the commands to run it as Docker container. The application needs to be build with the 'useOllama=true' Gradle build property to include the dependencies. The application needs to be started with the 'ollama' profile to switch on the configs/features to use Ollama based models. Ollama has support for GPU acceleration.

## Setup
Postgresql with Vector Extension 0.5.1 or newer

Java 21 or newer

Gradle 8.3 or newer

NodeJs 18.13.x or newer

Npm 8.19.x or newer

Angular Cli 17 or newer