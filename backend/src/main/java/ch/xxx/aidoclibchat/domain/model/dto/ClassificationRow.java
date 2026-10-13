package ch.xxx.aidoclibchat.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"Text", "Label"})
public record ClassificationRow(@JsonProperty("Text") String text, @JsonProperty("Label") String label) {
}
