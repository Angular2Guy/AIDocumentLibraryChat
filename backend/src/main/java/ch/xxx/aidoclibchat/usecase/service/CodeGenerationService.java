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

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.model.dto.GithubClient;
import ch.xxx.aidoclibchat.domain.model.dto.GithubSource;

@Service
public class CodeGenerationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CodeGenerationService.class);
	private final GithubClient githubClient;
	private final org.springframework.ai.chat.client.ChatClient chatClient;
	private final String ollamaPrompt = """
			You are an assistant to generate spring tests for the class under test.
			Analyse the classes provided and generate tests for all methods. Base your tests on the example.
			Generate and implement the test methods. Generate and implement complete tests methods.
			Generate the complete source of the test class.

			              Your additional guidelines:
			                     1.Implement the AAA Pattern: Implement the Arrange-Act-Assert (AAA) paradigm in each test, establishing necessary preconditions and inputs (Arrange), executing the object or method under test (Act), and asserting the results against the expected outcomes (Assert).
			                     2.Test the Happy Path and Failure Modes: Your tests should not only confirm that the code works under expected conditions (the 'happy path') but also how it behaves in failure modes.
			                     3.Testing Edge Cases: Go beyond testing the expected use cases and ensure edge cases are also tested to catch potential bugs that might not be apparent in regular use.
			                     4.Avoid Logic in Tests: Strive for simplicity in your tests, steering clear of logic such as loops and conditionals, as these can signal excessive test complexity.
			                     5.Leverage TypeScript's Type System: Leverage static typing to catch potential bugs before they occur, potentially reducing the number of tests needed.
			                     6.Handle Asynchronous Code Effectively: If your test cases involve promises and asynchronous operations, ensure they are handled correctly.
			                     7.Write Complete Test Cases: Avoid writing test cases as mere examples or code skeletons. You have to write a complete set of tests. They should effectively validate the functionality under test.

			Generate tests for this class:
			{classToTest}

			Use these classes as context for the tests:
			{contextClasses}

			{testExample}
			""";
	private final String ollamaPrompt1 = """
			You are an assistant to generate a spring test class for the source class.
			1. Analyse the source class
			2. Analyse the context classes for the classes used by the source class
			3. Analyse the class in test example to base the code of the generated test class on it.
			4. Generate a test class for the source class and use the context classes as sources for creating the test class.
			5. Use the code of the test class as test example.
			6. Generate tests for each of the public methods of the source class.

			         Your additional guidelines:
			         1.Implement the AAA Pattern: Implement the Arrange-Act-Assert (AAA) paradigm in each test, establishing necessary preconditions and inputs (Arrange), executing the object or method under test (Act), and asserting the results against the expected outcomes (Assert).
			         2.Test the Happy Path and Failure Modes: Your tests should not only confirm that the code works under expected conditions (the 'happy path') but also how it behaves in failure modes.
			         3.Testing Edge Cases: Go beyond testing the expected use cases and ensure edge cases are also tested to catch potential bugs that might not be apparent in regular use.
			         4.Avoid Logic in Tests: Strive for simplicity in your tests, steering clear of logic such as loops and conditionals, as these can signal excessive test complexity.
			         5.Leverage Java's Type System: Leverage static typing to catch potential bugs before they occur, potentially reducing the number of tests needed.
			         6.Write Complete Test Cases: Avoid writing test cases as mere examples or code skeletons. You have to write a complete set of tests. They should effectively validate the functionality under test.

			Generate the complete source code of the test class implementing the tests.

			{testExample}

			Use these context classes as extension for the source class:
			{contextClasses}

			Generate the complete source code of the test class implementing the tests.
			Generate tests for this source class:
			{classToTest}
			""";
	@Value("${spring.ai.ollama.chat.options.num-ctx:0}")
	private Long contextWindowSize;

	public CodeGenerationService(GithubClient githubClient, Builder builder) {
		this.githubClient = githubClient;
		this.chatClient = builder.build();
	}

	public String generateTest(String url, Optional<String> testUrlOpt) {
		var start = Instant.now();
		var githubSource = this.createTestSources(url, true);
		var githubTestSource = testUrlOpt.map(testUrl -> this.createTestSources(testUrl, false))
				.orElse(new GithubSource(null, null, List.of(), List.of()));
		String contextClasses = githubSource.dependencies().stream().filter(x -> this.contextWindowSize >= 16 * 1024)
				.map(myGithubSource -> myGithubSource.sourceName() + ":" + System.getProperty("line.separator")
						+ myGithubSource.lines().stream()
								.collect(Collectors.joining(System.getProperty("line.separator"))))
				.collect(Collectors.joining(System.getProperty("line.separator")));
		String testExample = Optional
				.ofNullable(
						githubTestSource.sourceName())
				.map(x -> "Use this as test example class:" + System.getProperty("line.separator") + githubTestSource
						.lines().stream().collect(Collectors.joining(System.getProperty("line.separator"))))
				.orElse("");
		String classToTest = githubSource.lines().stream()
				.collect(Collectors.joining(System.getProperty("line.separator")));
		LOGGER.debug(PromptTemplate.builder().template(this.contextWindowSize >= 16 * 1024 ? this.ollamaPrompt1 : this.ollamaPrompt)
		  .variables(Map.of("classToTest", classToTest, "contextClasses", contextClasses, "testExample", testExample)).build().getTemplate());		
		LOGGER.info("Generation started with context window: {}", this.contextWindowSize);
		var response = chatClient.prompt()
				.user(u -> u.text(this.contextWindowSize >= 16 * 1024 ? this.ollamaPrompt1 : this.ollamaPrompt)
						.params(Map.of("classToTest", classToTest, "contextClasses", contextClasses, "testExample",
								testExample)))
				.call().chatResponse();
		if ((Instant.now().getEpochSecond() - start.getEpochSecond()) >= 300) {
			LOGGER.info(response.getResult().getOutput().getText());
		}
		LOGGER.info("Prompt tokens: " + response.getMetadata().getUsage().getPromptTokens());
		LOGGER.info("Generation tokens: " + response.getMetadata().getUsage().getCompletionTokens());
		LOGGER.info("Total tokens: " + response.getMetadata().getUsage().getTotalTokens());
		LOGGER.info("Time in seconds: {}", (Instant.now().toEpochMilli() - start.toEpochMilli()) / 1000.0);
		return response.getResult().getOutput().getText();
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
						myLine.split("import")[1].split(";")[0].replaceAll("\\.", "/").trim(),
						myUrl.substring(myUrl.lastIndexOf('.'))))
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
