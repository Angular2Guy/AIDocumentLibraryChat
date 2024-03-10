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
package ch.xxx.aidoclibchat.adapter.client;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.xxx.aidoclibchat.domain.client.OpenLibraryClient;

@Component
public class OpenLibraryRestClient implements OpenLibraryClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenLibraryRestClient.class);
	private final String baseUrl = "https://openlibrary.org/search.json";

	@Override
	public Response apply(Request request) {
		var authorOpt = Optional.ofNullable(request.author()).stream().filter(myAuthor -> !myAuthor.isBlank())
				.map(myAuthor -> "author=" + myAuthor.replace(" ", "+")).findFirst();
		var titleOpt = Optional.ofNullable(request.title()).stream().filter(myAuthor -> !myAuthor.isBlank())
				.map(myAuthor -> "title=" + myAuthor.replace(" ", "+")).findFirst();
		var subjectOpt = Optional.ofNullable(request.subject()).stream().filter(myAuthor -> !myAuthor.isBlank())
				.map(myAuthor -> "subject=" + myAuthor.replace(" ", "+")).findFirst();
		var paramStr = "?" + List.of(authorOpt, titleOpt, subjectOpt).stream().filter(Optional::isPresent)
				.map(myOpt -> myOpt.get()).collect(Collectors.joining("&"));
		LOGGER.info(this.baseUrl + paramStr);
		return new Response("Kevin Rudd");
	}
}
