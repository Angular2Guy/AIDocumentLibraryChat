package ch.xxx.aidoclibchat.domain.client;

import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonInclude.Include;



public interface OpenLibraryClient extends Function<OpenLibraryClient.Request, OpenLibraryClient.Response> {
	@JsonInclude(Include.NON_NULL)
	@JsonClassDescription("OpenLibrary API request")
	record Request(@JsonProperty(required=false, value="author") @JsonPropertyDescription("The book author") String author ) {}
	record Response(String title) {}
}
