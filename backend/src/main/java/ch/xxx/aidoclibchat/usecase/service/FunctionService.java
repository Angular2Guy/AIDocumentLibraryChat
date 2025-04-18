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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import ch.xxx.aidoclibchat.adapter.config.FunctionConfig;
import ch.xxx.aidoclibchat.domain.model.dto.FunctionResult;
import ch.xxx.aidoclibchat.domain.model.dto.FunctionSearch.ResultFormat;

@Service
public class FunctionService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionService.class);
	private final ChatClient chatClient;

	@JsonPropertyOrder({ "title", "summary" })
	public record JsonBook(String title, String summary) {
	}

	@JsonPropertyOrder({ "author", "books" })
	public record JsonResult(String author, List<JsonBook> books) {
	}

	private final String promptStr = """
			Make sure to have a parameter when calling a function.
			If no parameter is provided ask the user for the parameter.
			Create a summary of two full sentences based on the subject property for each book and put it in the function response subject.

			User Query:
			%s
			""";

	@Value("${spring.profiles.active:}")
	private String activeProfile;

	public FunctionService(Builder builder) {
		this.chatClient = builder.build();
	}

	public FunctionResult functionCall(String question, ResultFormat resultFormat) {
		if (!this.activeProfile.contains("ollama")) {
			return new FunctionResult(" ", null);
		}
		var result = new FunctionResult(" ", null);

		int i = 0;
		while (i < 3 && (result.jsonResult() == null && " ".equals(result.result()))) {
			try {
				result = switch (resultFormat) {
				case ResultFormat.Text -> this.functionCallText(question);
				case ResultFormat.Json -> this.functionCallJson(question);
				};
			} catch (Exception e) {
				LOGGER.warn("AI Call failed.", e);
			}
			i++;
		}
		return result;
	}

	private FunctionResult functionCallText(String question) {
		var result = this.chatClient.prompt().user(this.promptStr + question).tools(FunctionConfig.OPEN_LIBRARY_CLIENT)
				.call().content();
		return new FunctionResult(result, null);
	}

	private FunctionResult functionCallJson(String question) {
		var result = this.chatClient.prompt().user(this.promptStr + question).tools(FunctionConfig.OPEN_LIBRARY_CLIENT)
				.call().entity(new ParameterizedTypeReference<List<JsonResult>>() {
				});
		return new FunctionResult(null, result);
	}
}
