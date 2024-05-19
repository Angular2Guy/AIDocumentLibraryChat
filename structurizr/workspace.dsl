workspace "AIDocumentLibraryChat" "A project to show howto use SpringAI with OpenAI to chat with the documents in a library. Documents are stored in a normal/vector database. The AI is used to create embeddings from documents that are stored in the vector database. The vector database is used to query for the nearest document. That document is used by the AI to generate the answer. " {
    model {    	
        documentUser = person "Document User"
        imageUser = person "Image User"
        functionUser = person "Function User"
        dbUser = person "Database User"
        aiDocumentLibraryChatSystem = softwareSystem "AIDocumentLibraryChat System" "AIDocumentLibraryChat System" {
        	aiModel = container "OpenAI/Ollama AI Model" "The AI Model provides the Embeddings(OpenAI) and Answers to the questions based on the documents. The OpenAI is an external model Ollama is a local model."
        	aiDocumentLibraryChat = container "AIDocumentLibraryChat" "Angular Frontend and Spring Boot Backend integrated." {
        		angularFrontend = component "Angular Frontend" "The SPA imports/searches the documents and results." tag "Browser"
        		backendImageController = component "Image Controller" "Provides the rest interfaces for image import/search."
        		backendFunctionController = component "Function Controller" "Provides the rest interfaces for function calls."
        		backendTableController = component "Table Controller" "Provides the rest interfaces for Sql database search."
        		backendDocumentController = component "Document Controller" "Provides the rest interfaces for document import/search."
        	    backendDocumentVsRepository = component "Vectorstore repository for embeddings" "Repository to create/read/store embeddings. OpenAI embeddings are created by a service and Ollama embeddings are create with a library."
        	    backendDocumentRepository = component "Jpa repository for the documents" "Repository stores the originals and the metadata of the documents."
        	    backendImageRepository = component "Jpa repository for the images" "Repository stores the resized images and the metadata of the images."
        	    backendSqlTemplate = component "Sql Template" "Sql template executes sql queries on a relational database."
        	    backendChatClientServices = component "ChatClient for AI Answers" "The ChatClient generates answers for documents/images/dbs/function calls with the OpenAI/Ollama models."
        	    backendDocumentService = component "Document Service" "Document Service provides the logic for import/search of documents."
        	    backendImageService = component "Image Service" "Image Service provides the logic for import/search of images."
        	    backendTableService = component "Table Service" "Table Service provides the logic for the generation and execution of Sql queries."
        	    backendFunctionService = component "Function Service" "Function Service provides the logic for the generation of the function calls with its parameters."
        	}
        	database = container "Postgresql Db" "Postgresql stores the relational and vector data of the system." tag "Database"
        }
		aiModelSystem = softwareSystem "OpenAI/Ollama Model" "The OpenAI/Ollama interface for Embeddings/Answers"
		#databaseSystem = softwareSystem "Postgresql Db" "Postgresql relational and vector data database" 
		
		# relationships people / software systems
        #user -> aiDocumentLibraryChatSystem "ask questions about documents"
        
        aiDocumentLibraryChatSystem -> aiModelSystem "generate embeddings/answers"
        #aiDocumentLibraryChatSystem -> databaseSystem "store relational/vector data"
        
        # relationships containers
        documentUser -> aiDocumentLibraryChat "manages the document imports/searches"
        imageUser ->  aiDocumentLibraryChat "manages the image imports/searches"
        functionUser -> aiDocumentLibraryChat "manages the function call and the parameters"
        dbUser -> aiDocumentLibraryChat "manages the sql query creation and execution"
        aiDocumentLibraryChat -> aiModel "create embeddings/answers or sql queries or funtion calls"
        aiDocumentLibraryChat -> database "read/store document/image data and embeddings"
        
        # relationships components
        angularFrontend -> backendDocumentController "rest requests"
        angularFrontend -> backendImageController "rest requests"
        angularFrontend -> backendTableController "rest requests"
        angularFrontend -> backendFunctionController "rest requests"
        backendDocumentController -> backendDocumentService   
        backendImageController -> backendImageService
        backendTableController -> backendTableService
        backendFunctionController -> backendFunctionService 
        backendDocumentService -> backendDocumentVsRepository
        backendDocumentService -> backendDocumentRepository
        backendDocumentService -> backendChatClientServices
        backendImageService -> backendChatClientServices
        backendImageService -> backendDocumentVsRepository
        backendImageService -> backendImageRepository
        backendTableService -> backendSqlTemplate
        backendTableService -> backendChatClientServices
        backendTableService -> backendDocumentVsRepository
        backendFunctionService -> backendChatClientServices
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