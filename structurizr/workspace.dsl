workspace "AIDocumentLibraryChat" "A project to show howto use SpringAI with OpenAI to chat with the documents in a library. Documents are stored in a normal/vector database. The AI is used to create embeddings from documents that are stored in the vector database. The vector database is used to query for the nearest document. That document is used by the AI to generate the answer. " {
    model {
        user = person "User"
        aiDocumentLibraryChatSystem = softwareSystem "AIDocumentLibraryChat System" "AIDocumentLibraryChat System" {
        	aiModel = container "OpenAI/Ollama AI Model" "The AI Model provides the Embeddings(OpenAI) and Answers to the questions based on the documents. The OpenAI is an external model Ollama is a local model."
        	aiDocumentLibraryChat = container "AIDocumentLibraryChat" "Angular Frontend and Spring Boot Backend integrated." {
        		angularFrontend = component "Angular Frontend" "The SPA imports/searches the documents and results." tag "Browser"
        		backendDocumentController = component "Document Controller" "Provides the rest interfaces for document import/search."
        	    backendDocumentVsRepository = component "Vectorstore repository for embeddings" "Repository to create/read/store embeddings. OpenAI embeddings are created by a service and Ollama embeddings are create with a library."
        	    backendDocumentRepository = component "Jpa repository for the documents" "Repository stores the originals and the metadata of the documents."
        	    backendChatClientServices = component "ChatClient for AI Answers" "The ChatClient generates answers with the OpenAI/Ollama models."
        	    backendDocumentService = component "Document Service" "Document Service provides the logic for import/search of documents."
        	}
        	database = container "Postgresql Db" "Postgresql stores the relational and vector data of the system." tag "Database"
        }
		aiModelSystem = softwareSystem "OpenAI/Ollama AI Model" "The OpenAI/Ollama interface for Embeddings/Answers"
		databaseSystem = softwareSystem "Postgresql Db" "Postgresql relational and vector data database" 
		
		# relationships people / software systems
        user -> aiDocumentLibraryChatSystem "ask questions about documents"
        aiDocumentLibraryChatSystem -> aiModelSystem "generate embeddings/answers"
        aiDocumentLibraryChatSystem -> databaseSystem "store relational/vector data"
        
        # relationships containers
        user -> aiDocumentLibraryChat "manages the document imports/searches"
        aiDocumentLibraryChat -> aiModel "create embeddings/answers"
        aiDocumentLibraryChat -> database "read/store document data"
        
        # relationships components
        angularFrontend -> backendDocumentController "rest requests"
        backendDocumentController -> backendDocumentService
        backendDocumentService -> backendDocumentVsRepository
        backendDocumentService -> backendDocumentRepository
        backendDocumentService -> backendChatClientServices
    }

    views {
        systemContext aiDocumentLibraryChatSystem "SystemContext" {
            include *
            autoLayout
        }
        
        container aiDocumentLibraryChatSystem "Containers" {
        	include *
            autoLayout lr
        }
        
        component aiDocumentLibraryChat "Components" {
        	include *
            autoLayout
        }    
        
        styles {
        	element "Person" {            
            	shape Person
        	}
        	element "Database" {
                shape Cylinder                
            }
            element "Browser" {
                shape WebBrowser
            }
            element "Consumer" {
            	shape Pipe
            }
        }
    }
}