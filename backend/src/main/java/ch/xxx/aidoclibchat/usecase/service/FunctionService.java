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
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.xxx.aidoclibchat.domain.client.OpenLibraryClient;
import ch.xxx.aidoclibchat.domain.client.OpenLibraryClient.FunctionTool.Type;
import ch.xxx.aidoclibchat.domain.client.OpenLibraryClient.Response;

@Service
public class FunctionService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionService.class);
	private final ObjectMapper objectMapper;
	private final ChatClient chatClient;
	private final OpenLibraryClient openLibraryClient;
	private final List<String> nullCodes = List.of("none", "string");
	private final String promptStr = """
			You have access to the following tools:
			%s

			You must follow these instructions:
			Always select one or more of the above tools based on the user query
			If a tool is found, you must respond in the JSON format matching the following schema:
			{"tools": [{
					"tool": "<name of the selected tool>",
					"tool_input": "<parameters for the selected tool, matching the tool's JSON schema>"
				}]}
			Make sure to include all tool parameters in the JSON at tool_input.
			If there is no tool that match the user request, you will respond with empty json.
			Do not add any additional Notes or Explanations. Respond only with the JSON.

			User Query:
			%s
			""";

	private record Tool(@JsonProperty("tool") String tool, @JsonProperty("tool_input") Map<String, Object> toolInput) {
		@ConstructorBinding
		public Tool(String tool, String jsonSchema) {
			this(tool, OpenLibraryClient.parseJson(jsonSchema));
		}
	}

	private record Tools(@JsonProperty("tools") List<Tool> tools) {
	}

	@Value("${spring.profiles.active:}")
	private String activeProfile;

	public FunctionService(ObjectMapper objectMapper, ChatClient chatClient, OpenLibraryClient openLibraryClient) {
		this.objectMapper = objectMapper;
		this.chatClient = chatClient;
		this.openLibraryClient = openLibraryClient;
	}

	public Response functionCall(String question, Long resultsAmount) {
		if (!this.activeProfile.contains("ollama")) {
			return new Response(0L, 0L, false, List.of());
		}
		var description = "Search for books by author, title or subject.";
		var name = "booksearch";
		var aiFunction = new OpenLibraryClient.FunctionTool(Type.FUNCTION, new OpenLibraryClient.FunctionTool.Function(
				description, name, Map.of("author", "string", "title", "string", "subject", "string")));
		String jsonStr = "";
		try {
			jsonStr = this.objectMapper.writeValueAsString(aiFunction);
		} catch (JsonProcessingException e) {
			LOGGER.error("Json Mapping failed.", e);
		}
		var query = String.format(this.promptStr, jsonStr, question);
		int aiCallCounter = 0;
		var responseRef = new AtomicReference<Response>(new Response(0L, 0L, false, List.of()));
		List<Tool> myToolsList = List.of();
		while (aiCallCounter < 3 && myToolsList.isEmpty()) {
			aiCallCounter += 1;
			var response = this.chatClient.call(query);
			try {
				response = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
				final var atomicResponse = new AtomicReference<String>(response);
				this.nullCodes.forEach(myCode -> {
					var myResponse = atomicResponse.get();
					atomicResponse.set(myResponse.replaceAll(myCode, ""));
				});
				var myTools = this.objectMapper.readValue(atomicResponse.get(), Tools.class);
				//		LOGGER.info(myTools.toString());
				myToolsList = myTools.tools().stream()
						.filter(myTool1 -> myTool1.toolInput().values().stream()
								.filter(myValue -> (myValue instanceof String) && !((String) myValue).isBlank())
								.findFirst().isPresent())
						.toList();
				if (myToolsList.isEmpty()) {
					throw new RuntimeException("No parameters found.");
				}
			} catch (Exception e) {
				LOGGER.error("Chatresult Json Mapping failed.", e);
				LOGGER.error("ChatResponse: {}", response);
			}
		}

		myToolsList.forEach(myTool -> {
			var myRequest = new OpenLibraryClient.Request((String) myTool.toolInput().get("author"),
					(String) myTool.toolInput().get("title"), (String) myTool.toolInput().get("subject"));
			var myResponse = this.openLibraryClient.apply(myRequest);
			// LOGGER.info(myResponse.toString());
			responseRef.set(myResponse);
		});
		return responseRef.get();
	}
}
