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
import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.chat.client.advisor.ChatModelCallAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.stereotype.Service;

import ch.xxx.aidoclibchat.domain.model.dto.McpRequestDto;
import ch.xxx.aidoclibchat.domain.model.dto.McpResponseDto;
import io.modelcontextprotocol.client.McpSyncClient;

@Service
public class LocalMcpClient {
    private final List<McpSyncClient> mcpSyncClients;   
    private final ChatClient chatClient; 

    public LocalMcpClient(List<McpSyncClient> mcpSyncClients, Builder builder, ChatModel chatModel) {
        this.mcpSyncClients = mcpSyncClients;        
        var advisor = ChatModelCallAdvisor.builder().chatModel(chatModel).build();        
        this.chatClient = builder.defaultAdvisors(List.of(advisor)).build();
    }

    public McpResponseDto createResponse(McpRequestDto requestDto) {
        var result = this.chatClient.prompt(requestDto.question()).toolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients)).call();
        var resultText = Optional.ofNullable(result.chatResponse()).stream().map(value -> value.getResult().getOutput().getText()).findFirst().orElse("");
        var toolCalls = Optional.ofNullable(result.chatResponse()).stream().map(value -> value.getResult().getOutput().getToolCalls()).findFirst().orElse(List.of());
        var responseDto = new McpResponseDto(resultText, toolCalls);
        return responseDto;
    }
}
