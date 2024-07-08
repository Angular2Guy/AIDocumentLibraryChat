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
package ch.xxx.aidoclibchat.usecase.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.model.dto.GithubClient;
import ch.xxx.aidoclibchat.domain.model.dto.GithubSource;

@Service
public class CodeGenerationService {
	private final GithubClient githubClient;

	public CodeGenerationService(GithubClient githubClient) {
		this.githubClient = githubClient;
	}

	public GithubSource generateTests(String url) {
		var myUrl = url.replace("https://github.com", GithubClient.GITHUB_BASE_URL).replace("/blob", "");
		var result = this.githubClient.readSourceFile(myUrl);
		var isComment = new AtomicBoolean(false);
		var sourceLines = result.lines().stream().map(myLine -> myLine.replaceAll("[\t]", "").trim()).filter(myLine -> !myLine.isBlank())
				.filter(myLine -> filterComments(isComment, myLine)).toList();
		return new GithubSource(result.sourceName(), result.sourcePackage(), sourceLines);
	}

	private boolean filterComments(AtomicBoolean isComment, String myLine) {
		var result1 = true;
		if (myLine.contains("/*") || isComment.get()) {
			isComment.set(true);
			result1 = false;
		}
		if (myLine.contains("*/")) {
			isComment.set(false);
			result1 = false;
		}
		result1 = result1 && !myLine.trim().startsWith("//");
		return result1;
	}
}
