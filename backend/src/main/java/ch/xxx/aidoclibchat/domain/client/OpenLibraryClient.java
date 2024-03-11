package ch.xxx.aidoclibchat.domain.client;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.boot.context.properties.bind.ConstructorBinding;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;



public interface OpenLibraryClient extends Function<OpenLibraryClient.Request, OpenLibraryClient.Response> {
	@JsonIgnoreProperties(ignoreUnknown = true)
	record Book(@JsonProperty(value= "author_name", required = false) List<String> authorName,
			@JsonProperty(value= "language", required = false) List<String> languages,
			@JsonProperty(value= "publish_date", required = false) List<String> publishDates,
			@JsonProperty(value= "publisher", required = false) List<String> publishers, 
			String title, String type,
			@JsonProperty(value= "subject", required = false) List<String> subjects,
			@JsonProperty(value= "place", required = false) List<String> places,
			@JsonProperty(value= "time", required = false) List<String> times,
			@JsonProperty(value= "person", required = false) List<String> persons,
			@JsonProperty(value= "ratings_average", required = false) Double ratingsAverage) {}
	@JsonInclude(Include.NON_NULL)
	@JsonClassDescription("OpenLibrary API request")
	record Request(@JsonProperty(required=false, value="author") @JsonPropertyDescription("The book author") String author,
			@JsonProperty(required=false, value="title") @JsonPropertyDescription("The book title") String title,
			@JsonProperty(required=false, value="subject") @JsonPropertyDescription("The book subject") String subject) {}
	@JsonIgnoreProperties(ignoreUnknown = true)
	record Response(Long numFound, Long start, Boolean numFoundExact, List<Book> docs) {}
	
	@JsonInclude(Include.NON_NULL)
	record FunctionTool(
			@JsonProperty("type") Type type,
			@JsonProperty("function") Function function) {

		/**
		 * Create a tool of type 'function' and the given function definition.
		 * @param function function definition.
		 */
		@ConstructorBinding
		public FunctionTool(Function function) {
			this(Type.FUNCTION, function);
		}

		/**
		 * Create a tool of type 'function' and the given function definition.
		 */
		public enum Type {
			/**
			 * Function tool type.
			 */
			@JsonProperty("function") FUNCTION
		}

		/**
		 * Function definition.
		 *
		 * @param description A description of what the function does, used by the model to choose when and how to call
		 * the function.
		 * @param name The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and dashes,
		 * with a maximum length of 64.
		 * @param parameters The parameters the functions accepts, described as a JSON Schema object. To describe a
		 * function that accepts no parameters, provide the value {"type": "object", "properties": {}}.
		 */
		public record Function(
				@JsonProperty("description") String description,
				@JsonProperty("name") String name,
				@JsonProperty("parameters") Map<String, Object> parameters) {

			/**
			 * Create tool function definition.
			 *
			 * @param description tool function description.
			 * @param name tool function name.
			 * @param jsonSchema tool function schema as json.
			 */
			@ConstructorBinding
			public Function(String description, String name, String jsonSchema) {
				this(description, name, parseJson(jsonSchema));
			}
		}
	}
	
	 static Map<String, Object> parseJson(String jsonSchema) {
		try {
			return new ObjectMapper().readValue(jsonSchema,
					new TypeReference<Map<String, Object>>() {
					});
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to parse schema: " + jsonSchema, e);
		}
	}
}
