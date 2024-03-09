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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.xxx.aidoclibchat.domain.client.OpenLibraryClient;


@Service
public class FunctionService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionService.class);	
	private final ObjectMapper objectMapper;
	@Value("${spring.profiles.active:}")
	private String activeProfile;
	
	public FunctionService(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	public String functionCall(String question) {
		if(!this.activeProfile.contains("ollama")) {
			return "false";
		}
		var request = new OpenLibraryClient.Request(question);
		String jsonStr = "";
		try {
			jsonStr =  this.objectMapper.writeValueAsString(request);
		} catch (JsonProcessingException e) {
			LOGGER.error("Json Mapping failed.", e);
		}		
		return jsonStr;
	}
}
