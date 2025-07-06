/**
 *    Copyright 2023 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.mcpserver.client.external;

import java.util.List;

import org.springframework.ai.tool.annotation.ToolParam;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public interface OpenLibraryClient {
	Response loadBooks(Request request);

	@JsonIgnoreProperties(ignoreUnknown = true)
	record Book(@JsonProperty(value = "author_name", required = false) List<String> authorName,
			@JsonProperty(value = "language", required = false) List<String> languages,
			@JsonProperty(value = "publish_date", required = false) List<String> publishDates,
			@JsonProperty(value = "publisher", required = false) List<String> publishers, String title, String type,
			@JsonProperty(value = "subject", required = false) List<String> subjects,
			@JsonProperty(value = "place", required = false) List<String> places,
			@JsonProperty(value = "time", required = false) List<String> times,
			@JsonProperty(value = "person", required = false) List<String> persons,
			@JsonProperty(value = "ratings_average", required = false) Double ratingsAverage) {
	}

	@JsonInclude(Include.NON_NULL)
	@JsonClassDescription("OpenLibrary API request")
	record Request(
			@JsonProperty(required = false, value = "author") @ToolParam(description = "The book authors name") 
			@JsonPropertyDescription("The book authors name") String author,
			@JsonProperty(required = false, value = "title") @ToolParam(description = "The book title") 
			@JsonPropertyDescription("The book title") String title,
			@JsonProperty(required = false, value = "subject") @ToolParam(description = "The book subject") 
			@JsonPropertyDescription("The book subject") String subject) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record Response(Long numFound, Long start, Boolean numFoundExact, List<Book> docs) {
	}
}
