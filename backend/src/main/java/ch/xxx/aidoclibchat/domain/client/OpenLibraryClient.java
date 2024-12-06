package ch.xxx.aidoclibchat.domain.client;

import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;



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
}
