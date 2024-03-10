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

@Service
public class FunctionService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionService.class);
	private final ObjectMapper objectMapper;
	private final ChatClient chatClient;	
	private final List<String> nullCodes = List.of("none", "string");
	private final String promptStr = """
			You have access to the following tools:
			%s

			You must follow these instructions:
			Always select one or more of the above tools based on the user query
			If a tool is found, you must respond in the JSON format matching the following schema:
			{
				"tools": [{
					"tool": "<name of the selected tool>",
					"tool_input": "<parameters for the selected tool, matching the tool's JSON schema>"
				}]
			}			
			Make sure to include all tool parameters in the JSON at tool_input. 
			If there is no tool that match the user request, you will respond with empty json.
			Do not add any additional Notes or Explanations. Respond only with the JSON. 

			User Query: 
			%s
			""";
	private record Tool(@JsonProperty("tool")String tool, @JsonProperty("tool_input") Map<String, Object> toolInput) {
		@ConstructorBinding
		public Tool(String tool, String jsonSchema) {
			this(tool, OpenLibraryClient.parseJson(jsonSchema));
		}
	}
	private record Tools(@JsonProperty("tools") List<Tool> tools) { }
	@Value("${spring.profiles.active:}")
	private String activeProfile;

	public FunctionService(ObjectMapper objectMapper,ChatClient chatClient) {
		this.objectMapper = objectMapper;
		this.chatClient = chatClient;
	}

	public String functionCall(String question) {
		if (!this.activeProfile.contains("ollama")) {
			return "false";
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
		var response = this.chatClient.call(query);
		try {
		response = response.substring(response.indexOf("{"), response.lastIndexOf("}"));
		final var atomicResponse = new AtomicReference<String>(response);
		this.nullCodes.forEach(myCode -> {
			var myResponse = atomicResponse.get();
			atomicResponse.set(myResponse.replaceAll(myCode, ""));
			});
		
		} catch(Exception e) {
			LOGGER.error("Json Mapping failed.",e);
		}
		return response;
	}
}
