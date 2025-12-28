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

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.model.dto.SiteSummaryDto;

@Service
public class SiteSummaryService {
    private final ChatClient chatClient;

	private final String promptStr = """
			Tell me a joke about the following topic and put in in the summary property.

			Topic:

			""";

    public SiteSummaryService(Builder builder) {
        this.chatClient = builder.build();
    }

    public SiteSummaryDto getJokeAboutTopic(String topic) {
        var result = this.chatClient.prompt(this.promptStr + topic).call().entity(SiteSummaryDto.class);
        return result;
    }
}
