# AIDocumentLibraryChat
This is a project to show howto use SpringAI to chat with the documents in a library. Document can be uploaded are then stored in a normal and vector database. The AI is used to create embeddings from the chunks of the documents that are stored in the vector database. In the chat questions can be asked that are turned in embeddings and are used for requests to the vector database. The document vector with the lowest distance is used to load the document from the database and to request an answer from the AI based on document content tokens. That enables the AI to generate the answer based on the content of the document and limits hallucinations. A link to the source document is provided for further research.

The project uses Spring Boot with Spring AI to access OpenAI and the vector database. The Postgresql DB is used with the vector, hstore and the uuid-ossp extensions. Liquibase is used to manage the database migrations. Jpa is used for database access in the services by the rest controllers. For the frontend Angular, Angular Material and Typescript is used to create the table of documents, the upload dialog and the document chat. Angular Cli is used for the frontend build and Gradle for the project build.

Author: Sven Loesekann

Technologies: Angular, Angular-Cli, Angular-Material, Typescript, Spring Boot, Spring AI, OpenAI, Postgresql(vector,hstore), Liquibase, Jpa, Gradle, Java

