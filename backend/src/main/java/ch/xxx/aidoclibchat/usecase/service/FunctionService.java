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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FunctionService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionService.class);
	private final ChatClient chatClient;
	private final String promptStr = """
			Make sure to have a parameter when calling a function. 
			If no parameter is provided ask the user for the parameter.
			Create a summary for each book based on the function response subject.			

			User Query:
			%s
			""";

	@Value("${spring.profiles.active:}")
	private String activeProfile;

	public FunctionService(Builder builder) {
		this.chatClient = builder.build();
	}

	public String functionCall(String question) {
		if (!this.activeProfile.contains("ollama")) {
			return "";
		}

		var result = this.chatClient.prompt().user(this.promptStr + question).functions("openLibraryClient").call().content();
		return result;
	}

}
