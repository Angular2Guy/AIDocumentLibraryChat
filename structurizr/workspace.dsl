workspace "AIDocumentLibraryChat" "A project to show howto use SpringAI with OpenAI to chat with the documents in a library. Documents are stored in a normal/vector database. The AI is used to create embeddings from documents that are stored in the vector database. The vector database is used to query for the nearest document. That document is used by the AI to generate the answer. " {
    model {
        user = person "User"
        aiDocumentLibraryChatSystem = softwareSystem "AIDocumentLibraryChat System" "AIDocumentLibraryChat System" {
        	aiModel = container "OpenAI/Ollama AI Model" "The AI Model provides the Embeddings(OpenAI) and Answers to the questions based on the documents. The OpenAI is an external model Ollama is a local model."
        	aiDocumentLibraryChat = container "AIDocumentLibraryChat" "Angular Frontend and Spring Boot Backend integrated." {
        		angularFrontend = component "Angular Frontend" "The SPA imports/searches the documents and results." tag "Browser"
        		#backendMovieClient = component "Rest Client" "The rest client to import the movie data."
        		#backendJwtTokenFilters = component "Jwt Token Filters" "Provide the security based on Jwt Tokens."
        		#backendMovieActorControllers = component "Movie/Actor Controllers" "Provides the rest interfaces for Movies / Actors."
        		#backendAuthController = component "Auth Controller" "Provides the rest interfaces for Login / Signin / Logout."
        		#backendImportController = component "Import Controller" "Provides the rest interfaces to start movie related imports."
        		#backendKafkaConsumer = component "Kafka Consumer" "Consume the Kafka events." tag "Consumer"
        	    #backendKafkaProducer = component "Kafka Producer" "Produce the Kafka events." tag "Consumer"
        	    #backendMovieRelatedRepositories = component "Movie related Repositories" "Repositories forA Cast / Actor / Genere / Movie."
        	    #backendUserTokenRepository = component "User/Token Repositories" "User / Token Repositories"        	    
        	    #backendMovieRelatedServices = component "Movie related Services" "Services for Cast / Actor / Genere / Movie."
        	    #backendUserService = component "User Service" "User Service provides the logic for login / signin / logout."
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
        #angularFrontend -> backendMovieActorControllers "rest requests"
        #angularFrontend -> backendAuthController "rest requests"
        #angularFrontend -> backendImportController "rest requests"
        #backendMovieActorControllers -> backendJwtTokenFilters
        #backendAuthController -> backendJwtTokenFilters
        #backendImportController -> backendJwtTokenFilters
        #backendMovieActorControllers -> backendMovieRelatedServices
        #backendAuthController -> backendUserService
        #backendImportController -> backendMovieRelatedServices        
        #backendMovieRelatedServices -> backendMovieClient "import movie related data"
        #backendKafkaConsumer -> backendUserService "process kafka events"
        #backendUserService -> backendKafkaProducer "send kafka events"
        #backendMovieRelatedServices -> backendMovieRelatedRepositories
        #backendUserService -> backendUserTokenRepository
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