package org.springframework.ai.ollama.api;

public enum OllamaModel {

	/**
	 * Llama 2 is a collection of language models ranging from 7B to 70B parameters.
	 */
	LLAMA2("llama2"),

	/**
	 * Llama 3 is a collection of language models ranging from 8B and 70B parameters.
	 */
	LLAMA3("llama3"),

	/**
	 * The 7B parameters model
	 */
	MISTRAL("mistral"),

	/**
	 * The 2.7B uncensored Dolphin model
	 */
	DOLPHIN_PHI("dolphin-phi"),

	/**
	 * The Phi-2 2.7B language model
	 */
	PHI("phi"),

	/**
	 * The Phi-3 3.8B language model
	 */
	PHI3("phi3"),

	/**
	 * A fine-tuned Mistral model
	 */
	NEURAL_CHAT("neural-chat"),

	/**
	 * Starling-7B model
	 */
	STARLING_LM("starling-lm"),

	/**
	 * Code Llama is based on Llama 2 model
	 */
	CODELLAMA("codellama"),

	/**
	 * Orca Mini is based on Llama and Llama 2 ranging from 3 billion parameters to 70
	 * billion
	 */
	ORCA_MINI("orca-mini"),

	/**
	 * Llava is a Large Language and Vision Assistant model
	 */
	LLAVA("llava"),

	/**
	 * Gemma is a lightweight model with 2 billion and 7 billion
	 */
	GEMMA("gemma"),

	/**
	 * Uncensored Llama 2 model
	 */
	LLAMA2_UNCENSORED("llama2-uncensored");

	private final String id;

	OllamaModel(String id) {
		this.id = id;
	}

	public String id() {
		return this.id;
	}

}
