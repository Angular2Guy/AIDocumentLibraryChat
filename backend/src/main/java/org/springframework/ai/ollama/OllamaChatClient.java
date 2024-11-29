package org.springframework.ai.ollama;

import java.util.Base64;
import java.util.List;

import org.springframework.ai.ollama.metadata.OllamaChatResponseMetadata;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApi.Message.Role;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class OllamaChatClient implements ChatClient, StreamingChatClient {

	/**
	 * Low-level Ollama API library.
	 */
	private final OllamaApi chatApi;

	/**
	 * Default options to be used for all chat requests.
	 */
	private OllamaOptions defaultOptions;

	public OllamaChatClient(OllamaApi chatApi) {
		this(chatApi, OllamaOptions.create().withModel(OllamaOptions.DEFAULT_MODEL));
	}

	public OllamaChatClient(OllamaApi chatApi, OllamaOptions defaultOptions) {
		Assert.notNull(chatApi, "OllamaApi must not be null");
		Assert.notNull(defaultOptions, "DefaultOptions must not be null");
		this.chatApi = chatApi;
		this.defaultOptions = defaultOptions;
	}

	/**
	 * @deprecated Use {@link OllamaOptions#setModel} instead.
	 */
	@Deprecated
	public OllamaChatClient withModel(String model) {
		this.defaultOptions.setModel(model);
		return this;
	}

	/**
	 * @deprecated Use {@link OllamaOptions} constructor instead.
	 */
	public OllamaChatClient withDefaultOptions(OllamaOptions options) {
		this.defaultOptions = options;
		return this;
	}

	@Override
	public ChatResponse call(Prompt prompt) {

		OllamaApi.ChatResponse response = this.chatApi.chat(ollamaChatRequest(prompt, false));

		var generator = new Generation(response.message().content());
		if (response.promptEvalCount() != null && response.evalCount() != null) {
			generator = generator.withGenerationMetadata(ChatGenerationMetadata.from("unknown", null));
		}
		return new ChatResponse(List.of(generator), OllamaChatResponseMetadata.from(response));
	}

	@Override
	public Flux<ChatResponse> stream(Prompt prompt) {

		Flux<OllamaApi.ChatResponse> response = this.chatApi.streamingChat(ollamaChatRequest(prompt, true));

		return response.map(chunk -> {
			Generation generation = (chunk.message() != null) ? new Generation(chunk.message().content())
					: new Generation("");
			if (Boolean.TRUE.equals(chunk.done())) {
				generation = generation.withGenerationMetadata(ChatGenerationMetadata.from("unknown", null));
			}
			return new ChatResponse(List.of(generation), OllamaChatResponseMetadata.from(chunk));
		});
	}

	/**
	 * Package access for testing.
	 */
	OllamaApi.ChatRequest ollamaChatRequest(Prompt prompt, boolean stream) {

		List<OllamaApi.Message> ollamaMessages = prompt.getInstructions()
			.stream()
			.filter(message -> message.getMessageType() == MessageType.USER
					|| message.getMessageType() == MessageType.ASSISTANT
					|| message.getMessageType() == MessageType.SYSTEM)
			.map(m -> {
				var messageBuilder = OllamaApi.Message.builder(OllamaChatClient.toRole(m)).withContent(m.getContent());

				if (!CollectionUtils.isEmpty(m.getMedia())) {
					messageBuilder
						.withImages(m.getMedia().stream().map(media -> OllamaChatClient.fromMediaData(media.getData())).toList());
				}
				return messageBuilder.build();
			})
			.toList();

		// runtime options
		OllamaOptions runtimeOptions = null;
		if (prompt.getOptions() != null) {
			if (prompt.getOptions() instanceof ChatOptions runtimeChatOptions) {
				runtimeOptions = ModelOptionsUtils.copyToTarget(runtimeChatOptions, ChatOptions.class,
						OllamaOptions.class);
			}
			else {
				throw new IllegalArgumentException("Prompt options are not of type ChatOptions: "
						+ prompt.getOptions().getClass().getSimpleName());
			}
		}

		OllamaOptions mergedOptions = ModelOptionsUtils.merge(runtimeOptions, this.defaultOptions, OllamaOptions.class);

		// Override the model.
		if (!StringUtils.hasText(mergedOptions.getModel())) {
			throw new IllegalArgumentException("Model is not set!");
		}

		String model = mergedOptions.getModel();
		OllamaApi.ChatRequest.Builder requestBuilder = OllamaApi.ChatRequest.builder(model)
			.withStream(stream)
			.withMessages(ollamaMessages)
			.withOptions(mergedOptions);

		if (mergedOptions.getFormat() != null) {
			requestBuilder.withFormat(mergedOptions.getFormat());
		}

		if (mergedOptions.getKeepAlive() != null) {
			requestBuilder.withKeepAlive(mergedOptions.getKeepAlive());
		}

		return requestBuilder.build();
	}

	private static String fromMediaData(Object mediaData) {
		if (mediaData instanceof byte[] bytes) {
			return Base64.getEncoder().encodeToString(bytes);
		}
		else if (mediaData instanceof String text) {
			return text;
		}
		else {
			throw new IllegalArgumentException("Unsupported media data type: " + mediaData.getClass().getSimpleName());
		}

	}

	private static OllamaApi.Message.Role toRole(Message message) {

		switch (message.getMessageType()) {
			case USER:
				return Role.USER;
			case ASSISTANT:
				return Role.ASSISTANT;
			case SYSTEM:
				return Role.SYSTEM;
			default:
				throw new IllegalArgumentException("Unsupported message type: " + message.getMessageType());
		}
	}

}
