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
package ch.xxx.mcpserver.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import ch.xxx.mcpserver.client.external.OpenLibraryClient;

@Component
public class OpenLibraryRestClient implements OpenLibraryClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenLibraryRestClient.class);
	private final String baseUrl = "https://openlibrary.org/search.json";
	private final RestClient restClient;

	@Value("${openlibrary.result-size:5}")
	private int resultLimit;
	
	public OpenLibraryRestClient(RestClient restClient) {
		this.restClient = restClient;
	}
	    
	@Override
	public OpenLibraryClient.Response loadBooks(OpenLibraryClient.Request request) {
		var authorOpt = this.createParamOpt(request.author(), "author");
		var titleOpt = this.createParamOpt(request.title(), "title");
		var subjectOpt = this.createParamOpt(request.subject(), "subject");
		var paramsStr = List.of(authorOpt, titleOpt, subjectOpt).stream().flatMap(Optional::stream)
				.collect(Collectors.joining("&"));
		var urlStr = String.format("%s?%s&fields=*&limit=%d", this.baseUrl, paramsStr, this.resultLimit);
		LOGGER.info(urlStr);
		var response = this.restClient.get().uri(urlStr).retrieve().body(Response.class);
		return response;
	}

	private Optional<String> createParamOpt(String valueStr, String keyStr) {
		return Optional.ofNullable(valueStr).stream().filter(Predicate.not(String::isBlank))
				.map(myAuthor -> String.format("%s=%s", keyStr, URLEncoder.encode(myAuthor, StandardCharsets.UTF_8)))
				.findFirst();
	}
}
