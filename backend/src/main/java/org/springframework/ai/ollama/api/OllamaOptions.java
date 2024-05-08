package org.springframework.ai.ollama.api;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.embedding.EmbeddingOptions;

/**
 * Helper class for creating strongly-typed Ollama options.
 *
 * @author Christian Tzolov
 * @since 0.8.0
 * @see <a href=
 * "https://github.com/ollama/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values">Ollama
 * Valid Parameters and Values</a>
 * @see <a href="https://github.com/ollama/ollama/blob/main/api/types.go">Ollama Types</a>
 */
@JsonInclude(Include.NON_NULL)
public class OllamaOptions implements ChatOptions, EmbeddingOptions {

	public static final String DEFAULT_MODEL = OllamaModel.MISTRAL.id();

	private static final List<String> NON_SUPPORTED_FIELDS = List.of("model", "format", "keep_alive");

	// Following fields are ptions which must be set when the model is loaded into memory.

	// @formatter:off
	/**
	 * useNUMA Whether to use NUMA.
	 */
	@JsonProperty("numa") private Boolean useNUMA;

	/**
	 * Sets the size of the context window used to generate the next token. (Default: 2048)
	 */
	@JsonProperty("num_ctx") private Integer numCtx;

	/**
	 * ???
	 */
	@JsonProperty("num_batch") private Integer numBatch;

	/**
	 * The number of GQA groups in the transformer layer. Required for some models,
	 * for example it is 8 for llama2:70b.
	 */
	@JsonProperty("num_gqa") private Integer numGQA;

	/**
	 * The number of layers to send to the GPU(s). On macOS it defaults to 1
	 * to enable metal support, 0 to disable.
		*/
	@JsonProperty("num_gpu") private Integer numGPU;

	/**
	 * ???
	 */
	@JsonProperty("main_gpu")private Integer mainGPU;

	/**
	 * ???
	 */
	@JsonProperty("low_vram") private Boolean lowVRAM;

	/**
	 * ???
	 */
	@JsonProperty("f16_kv") private Boolean f16KV;

	/**
	 * ???
	 */
	@JsonProperty("logits_all") private Boolean logitsAll;

	/**
	 * ???
	 */
	@JsonProperty("vocab_only") private Boolean vocabOnly;

	/**
	 * ???
	 */
	@JsonProperty("use_mmap") private Boolean useMMap;

	/**
	 * ???
	 */
	@JsonProperty("use_mlock") private Boolean useMLock;

	/**
	 * Sets the number of threads to use during computation. By default,
	 * Ollama will detect this for optimal performance. It is recommended to set this
	 * value to the number of physical CPU cores your system has (as opposed to the
	 * logical number of cores).
	 */
	@JsonProperty("num_thread") private Integer numThread;

	// Following fields are predict options used at runtime.

	/**
	 * ???
	 */
	@JsonProperty("num_keep") private Integer numKeep;

	/**
	 * Sets the random number seed to use for generation. Setting this to a
	 * specific number will make the model generate the same text for the same prompt.
	 * (Default: 0)
	 */
	@JsonProperty("seed") private Integer seed;

	/**
	 * Maximum number of tokens to predict when generating text.
	 * (Default: 128, -1 = infinite generation, -2 = fill context)
	 */
	@JsonProperty("num_predict") private Integer numPredict;

	/**
	 * Reduces the probability of generating nonsense. A higher value (e.g.
	 * 100) will give more diverse answers, while a lower value (e.g. 10) will be more
	 * conservative. (Default: 40)
	 */
	@JsonProperty("top_k") private Integer topK;

	/**
	 * Works together with top-k. A higher value (e.g., 0.95) will lead to
	 * more diverse text, while a lower value (e.g., 0.5) will generate more focused and
	 * conservative text. (Default: 0.9)
	 */
	@JsonProperty("top_p") private Float topP;

	/**
	 * Tail free sampling is used to reduce the impact of less probable tokens
	 * from the output. A higher value (e.g., 2.0) will reduce the impact more, while a
	 * value of 1.0 disables this setting. (default: 1)
	 */
	@JsonProperty("tfs_z") private Float tfsZ;

	/**
	 * ???
	 */
	@JsonProperty("typical_p") private Float typicalP;

	/**
	 * Sets how far back for the model to look back to prevent
	 * repetition. (Default: 64, 0 = disabled, -1 = num_ctx)
	 */
	@JsonProperty("repeat_last_n") private Integer repeatLastN;

	/**
	 * The temperature of the model. Increasing the temperature will
	 * make the model answer more creatively. (Default: 0.8)
	 */
	@JsonProperty("temperature") private Float temperature;

	/**
	 * Sets how strongly to penalize repetitions. A higher value
	 * (e.g., 1.5) will penalize repetitions more strongly, while a lower value (e.g.,
	 * 0.9) will be more lenient. (Default: 1.1)
	 */
	@JsonProperty("repeat_penalty") private Float repeatPenalty;

	/**
	 * ???
	 */
	@JsonProperty("presence_penalty") private Float presencePenalty;

	/**
	 * ???
	 */
	@JsonProperty("frequency_penalty") private Float frequencyPenalty;

	/**
	 * Enable Mirostat sampling for controlling perplexity. (default: 0, 0
	 * = disabled, 1 = Mirostat, 2 = Mirostat 2.0)
	 */
	@JsonProperty("mirostat") private Integer mirostat;

	/**
	 * Controls the balance between coherence and diversity of the output.
	 * A lower value will result in more focused and coherent text. (Default: 5.0)
	 */
	@JsonProperty("mirostat_tau") private Float mirostatTau;

	/**
	 * Influences how quickly the algorithm responds to feedback from the generated text.
	 * A lower learning rate will result in slower adjustments, while a higher learning rate
	 * will make the algorithm more responsive. (Default: 0.1)
	 */
	@JsonProperty("mirostat_eta") private Float mirostatEta;

	/**
	 * ???
	 */
	@JsonProperty("penalize_newline") private Boolean penalizeNewline;

	/**
	 * Sets the stop sequences to use. When this pattern is encountered the
	 * LLM will stop generating text and return. Multiple stop patterns may be set by
	 * specifying multiple separate stop parameters in a modelfile.
	 */
	@JsonProperty("stop") private List<String> stop;


	// Following fields are not part of the Ollama Options API but part of the Request.

	/**
	 * NOTE: Synthetic field not part of the official Ollama API.
	 * Used to allow overriding the model name with prompt options.
	 * Part of Chat completion <a href="https://github.com/ollama/ollama/blob/main/docs/api.md#parameters-1">parameters</a>.
	 */
	@JsonProperty("model") private String model;

	/**
	 * Sets the desired format of output from the LLM. The only valid values are null or "json".
	 * Part of Chat completion <a href="https://github.com/ollama/ollama/blob/main/docs/api.md#parameters-1">advanced parameters</a>.
	 */
	@JsonProperty("format") private String format;

	/**
	 * Sets the length of time for Ollama to keep the model loaded. Valid values for this
	 * setting are parsed by <a href="https://pkg.go.dev/time#ParseDuration">ParseDuration in Go</a>.
	 * Part of Chat completion <a href="https://github.com/ollama/ollama/blob/main/docs/api.md#parameters-1">advanced parameters</a>.
	 */
	@JsonProperty("keep_alive") private String keepAlive;

	/**
	 * @param model The ollama model names to use. See the {@link OllamaModel} for the common models.
	 */
	public OllamaOptions withModel(String model) {
		this.model = model;
		return this;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public OllamaOptions withFormat(String format) {
		this.format = format;
		return this;
	}

	public OllamaOptions withKeepAlive(String keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}

	public OllamaOptions withUseNUMA(Boolean useNUMA) {
		this.useNUMA = useNUMA;
		return this;
	}

	public OllamaOptions withNumCtx(Integer numCtx) {
		this.numCtx = numCtx;
		return this;
	}

	public OllamaOptions withNumBatch(Integer numBatch) {
		this.numBatch = numBatch;
		return this;
	}

	public OllamaOptions withNumGQA(Integer numGQA) {
		this.numGQA = numGQA;
		return this;
	}

	public OllamaOptions withNumGPU(Integer numGPU) {
		this.numGPU = numGPU;
		return this;
	}

	public OllamaOptions withMainGPU(Integer mainGPU) {
		this.mainGPU = mainGPU;
		return this;
	}

	public OllamaOptions withLowVRAM(Boolean lowVRAM) {
		this.lowVRAM = lowVRAM;
		return this;
	}

	public OllamaOptions withF16KV(Boolean f16KV) {
		this.f16KV = f16KV;
		return this;
	}

	public OllamaOptions withLogitsAll(Boolean logitsAll) {
		this.logitsAll = logitsAll;
		return this;
	}

	public OllamaOptions withVocabOnly(Boolean vocabOnly) {
		this.vocabOnly = vocabOnly;
		return this;
	}

	public OllamaOptions withUseMMap(Boolean useMMap) {
		this.useMMap = useMMap;
		return this;
	}

	public OllamaOptions withUseMLock(Boolean useMLock) {
		this.useMLock = useMLock;
		return this;
	}

	public OllamaOptions withNumThread(Integer numThread) {
		this.numThread = numThread;
		return this;
	}

	public OllamaOptions withNumKeep(Integer numKeep) {
		this.numKeep = numKeep;
		return this;
	}

	public OllamaOptions withSeed(Integer seed) {
		this.seed = seed;
		return this;
	}

	public OllamaOptions withNumPredict(Integer numPredict) {
		this.numPredict = numPredict;
		return this;
	}

	public OllamaOptions withTopK(Integer topK) {
		this.topK = topK;
		return this;
	}

	public OllamaOptions withTopP(Float topP) {
		this.topP = topP;
		return this;
	}

	public OllamaOptions withTfsZ(Float tfsZ) {
		this.tfsZ = tfsZ;
		return this;
	}

	public OllamaOptions withTypicalP(Float typicalP) {
		this.typicalP = typicalP;
		return this;
	}

	public OllamaOptions withRepeatLastN(Integer repeatLastN) {
		this.repeatLastN = repeatLastN;
		return this;
	}

	public OllamaOptions withTemperature(Float temperature) {
		this.temperature = temperature;
		return this;
	}

	public OllamaOptions withRepeatPenalty(Float repeatPenalty) {
		this.repeatPenalty = repeatPenalty;
		return this;
	}

	public OllamaOptions withPresencePenalty(Float presencePenalty) {
		this.presencePenalty = presencePenalty;
		return this;
	}

	public OllamaOptions withFrequencyPenalty(Float frequencyPenalty) {
		this.frequencyPenalty = frequencyPenalty;
		return this;
	}

	public OllamaOptions withMirostat(Integer mirostat) {
		this.mirostat = mirostat;
		return this;
	}

	public OllamaOptions withMirostatTau(Float mirostatTau) {
		this.mirostatTau = mirostatTau;
		return this;
	}

	public OllamaOptions withMirostatEta(Float mirostatEta) {
		this.mirostatEta = mirostatEta;
		return this;
	}

	public OllamaOptions withPenalizeNewline(Boolean penalizeNewline) {
		this.penalizeNewline = penalizeNewline;
		return this;
	}

	public OllamaOptions withStop(List<String> stop) {
		this.stop = stop;
		return this;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getKeepAlive() {
		return this.keepAlive;
	}

	public void setKeepAlive(String keepAlive) {
		this.keepAlive = keepAlive;
	}

	public Boolean getUseNUMA() {
		return this.useNUMA;
	}

	public void setUseNUMA(Boolean useNUMA) {
		this.useNUMA = useNUMA;
	}

	public Integer getNumCtx() {
		return this.numCtx;
	}

	public void setNumCtx(Integer numCtx) {
		this.numCtx = numCtx;
	}

	public Integer getNumBatch() {
		return this.numBatch;
	}

	public void setNumBatch(Integer numBatch) {
		this.numBatch = numBatch;
	}

	public Integer getNumGQA() {
		return this.numGQA;
	}

	public void setNumGQA(Integer numGQA) {
		this.numGQA = numGQA;
	}

	public Integer getNumGPU() {
		return this.numGPU;
	}

	public void setNumGPU(Integer numGPU) {
		this.numGPU = numGPU;
	}

	public Integer getMainGPU() {
		return this.mainGPU;
	}

	public void setMainGPU(Integer mainGPU) {
		this.mainGPU = mainGPU;
	}

	public Boolean getLowVRAM() {
		return this.lowVRAM;
	}

	public void setLowVRAM(Boolean lowVRAM) {
		this.lowVRAM = lowVRAM;
	}

	public Boolean getF16KV() {
		return this.f16KV;
	}

	public void setF16KV(Boolean f16kv) {
		this.f16KV = f16kv;
	}

	public Boolean getLogitsAll() {
		return this.logitsAll;
	}

	public void setLogitsAll(Boolean logitsAll) {
		this.logitsAll = logitsAll;
	}

	public Boolean getVocabOnly() {
		return this.vocabOnly;
	}

	public void setVocabOnly(Boolean vocabOnly) {
		this.vocabOnly = vocabOnly;
	}

	public Boolean getUseMMap() {
		return this.useMMap;
	}

	public void setUseMMap(Boolean useMMap) {
		this.useMMap = useMMap;
	}

	public Boolean getUseMLock() {
		return this.useMLock;
	}

	public void setUseMLock(Boolean useMLock) {
		this.useMLock = useMLock;
	}

	public Integer getNumThread() {
		return this.numThread;
	}

	public void setNumThread(Integer numThread) {
		this.numThread = numThread;
	}

	public Integer getNumKeep() {
		return this.numKeep;
	}

	public void setNumKeep(Integer numKeep) {
		this.numKeep = numKeep;
	}

	public Integer getSeed() {
		return this.seed;
	}

	public void setSeed(Integer seed) {
		this.seed = seed;
	}

	public Integer getNumPredict() {
		return this.numPredict;
	}

	public void setNumPredict(Integer numPredict) {
		this.numPredict = numPredict;
	}

	public Integer getTopK() {
		return this.topK;
	}

	public void setTopK(Integer topK) {
		this.topK = topK;
	}

	public Float getTopP() {
		return this.topP;
	}

	public void setTopP(Float topP) {
		this.topP = topP;
	}

	public Float getTfsZ() {
		return this.tfsZ;
	}

	public void setTfsZ(Float tfsZ) {
		this.tfsZ = tfsZ;
	}

	public Float getTypicalP() {
		return this.typicalP;
	}

	public void setTypicalP(Float typicalP) {
		this.typicalP = typicalP;
	}

	public Integer getRepeatLastN() {
		return this.repeatLastN;
	}

	public void setRepeatLastN(Integer repeatLastN) {
		this.repeatLastN = repeatLastN;
	}

	public Float getTemperature() {
		return this.temperature;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	public Float getRepeatPenalty() {
		return this.repeatPenalty;
	}

	public void setRepeatPenalty(Float repeatPenalty) {
		this.repeatPenalty = repeatPenalty;
	}

	public Float getPresencePenalty() {
		return this.presencePenalty;
	}

	public void setPresencePenalty(Float presencePenalty) {
		this.presencePenalty = presencePenalty;
	}

	public Float getFrequencyPenalty() {
		return this.frequencyPenalty;
	}

	public void setFrequencyPenalty(Float frequencyPenalty) {
		this.frequencyPenalty = frequencyPenalty;
	}

	public Integer getMirostat() {
		return this.mirostat;
	}

	public void setMirostat(Integer mirostat) {
		this.mirostat = mirostat;
	}

	public Float getMirostatTau() {
		return this.mirostatTau;
	}

	public void setMirostatTau(Float mirostatTau) {
		this.mirostatTau = mirostatTau;
	}

	public Float getMirostatEta() {
		return this.mirostatEta;
	}

	public void setMirostatEta(Float mirostatEta) {
		this.mirostatEta = mirostatEta;
	}

	public Boolean getPenalizeNewline() {
		return this.penalizeNewline;
	}

	public void setPenalizeNewline(Boolean penalizeNewline) {
		this.penalizeNewline = penalizeNewline;
	}

	public List<String> getStop() {
		return this.stop;
	}

	public void setStop(List<String> stop) {
		this.stop = stop;
	}

	/**
	 * Convert the {@link OllamaOptions} object to a {@link Map} of key/value pairs.
	 * @return The {@link Map} of key/value pairs.
	 */
	public Map<String, Object> toMap() {
		try {
			var json = new ObjectMapper().writeValueAsString(this);
			return new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>() {
			});
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Helper factory method to create a new {@link OllamaOptions} instance.
	 * @return A new {@link OllamaOptions} instance.
	 */
	public static OllamaOptions create() {
		return new OllamaOptions();
	}

	/**
	 * Filter out the non-supported fields from the options.
	 * @param options The options to filter.
	 * @return The filtered options.
	 */
	public static Map<String, Object> filterNonSupportedFields(Map<String, Object> options) {
		return options.entrySet().stream()
			.filter(e -> !NON_SUPPORTED_FIELDS.contains(e.getKey()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}


	// @formatter:on

}

