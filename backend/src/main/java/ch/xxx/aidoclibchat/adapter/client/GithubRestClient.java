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

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import ch.xxx.aidoclibchat.domain.model.dto.GithubClient;
import ch.xxx.aidoclibchat.domain.model.dto.GithubSource;

@Component
public class GithubRestClient implements GithubClient {
	private final RestClient restClient;

	public GithubRestClient(RestClient restClient) {
		this.restClient = restClient;
	}

	public GithubSource readSourceFile(String url) {
		var result = this.restClient.get().uri(url).retrieve().body(String.class);
		var sourceName = Arrays.asList(url.split("/")).reversed().get(0).split("\\.")[0].trim();
		var resultLines = result.lines().toList();
		var sourcePackage = resultLines.stream().filter(myLine -> myLine.contains("package")).findFirst().orElseThrow()
				.trim().split(" ")[1].split(";")[0].trim();
		return new GithubSource(sourceName, sourcePackage, resultLines, List.of());
	}
}
