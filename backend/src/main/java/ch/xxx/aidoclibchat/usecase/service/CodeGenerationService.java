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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.model.dto.GithubClient;
import ch.xxx.aidoclibchat.domain.model.dto.GithubSource;

@Service
public class CodeGenerationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CodeGenerationService.class);
	private final GithubClient githubClient;
	private final ChatClient chatClient;
	private final String ollamaPrompt = """
			You are an assistant to generate spring tests for the class under test. 
			Analyse the classes provided and generate tests for all methods. Base your tests on the test example.
			Generate and implement the test methods. 	
					 
			Generate tests for this class:
			{classToTest}

			Use these classes as context for the tests:
			{contextClasses}

			{testExample}
			""";

	public CodeGenerationService(GithubClient githubClient, ChatClient chatClient) {
		this.githubClient = githubClient;
		this.chatClient = chatClient;
	}

	public String generateTest(String url, Optional<String> testUrlOpt) {
		var githubSource = this.createTestSources(url, true);
		var githubTestSource = testUrlOpt.map(testUrl -> this.createTestSources(testUrl, false))
				.orElse(new GithubSource(null, null, List.of(), List.of()));
		String contextClasses = githubSource.dependencies().stream()
				.map(myGithubSource -> myGithubSource.sourceName() + ":" + System.getProperty("line.separator")
						+ myGithubSource.lines().stream()
								.collect(Collectors.joining(System.getProperty("line.separator"))))
				.collect(Collectors.joining(System.getProperty("line.separator")));
		String testExample = Optional
				.ofNullable(
						githubTestSource.sourceName())
				.map(x -> "Use this class as test example:" + System.getProperty("line.separator") + githubTestSource
						.lines().stream().collect(Collectors.joining(System.getProperty("line.separator"))))
				.orElse("");
		String classToTest = githubSource.lines().stream()
				.collect(Collectors.joining(System.getProperty("line.separator")));
		LOGGER.debug(new PromptTemplate(this.ollamaPrompt,
				Map.of("classToTest", classToTest, "contextClasses", contextClasses, "testExample", testExample)).createMessage().getContent());
		var response = chatClient.call(new PromptTemplate(this.ollamaPrompt,
				Map.of("classToTest", classToTest, "contextClasses", contextClasses, "testExample", testExample)).create());
		return response.getResult().getOutput().getContent();
	}

	public GithubSource createTestSources(String url, final boolean referencedSources) {
		final var myUrl = url.replace("https://github.com", GithubClient.GITHUB_BASE_URL).replace("/blob", "");
		var result = this.githubClient.readSourceFile(myUrl);
		final var isComment = new AtomicBoolean(false);
		final var sourceLines = result.lines().stream().map(myLine -> myLine.replaceAll("[\t]", "").trim())
				.filter(myLine -> !myLine.isBlank()).filter(myLine -> filterComments(isComment, myLine)).toList();
		final var basePackage = List.of(result.sourcePackage().split("\\.")).stream().limit(2)
				.collect(Collectors.joining("."));
		final var dependencies = this.createDependencies(referencedSources, myUrl, sourceLines, basePackage);
		return new GithubSource(result.sourceName(), result.sourcePackage(), sourceLines, dependencies);
	}

	private List<GithubSource> createDependencies(final boolean referencedSources, final String myUrl,
			final List<String> sourceLines, final String basePackage) {
		return sourceLines.stream().filter(x -> referencedSources).filter(myLine -> myLine.contains("import"))
				.filter(myLine -> myLine.contains(basePackage))
				.map(myLine -> String.format("%s%s%s", myUrl.split(basePackage.replace(".", "/"))[0].trim(),
						myLine.split("import")[1].split(";")[0].replaceAll("\\.", "/").trim(), ".java"))
				.map(myLine -> this.createTestSources(myLine, false)).toList();
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
