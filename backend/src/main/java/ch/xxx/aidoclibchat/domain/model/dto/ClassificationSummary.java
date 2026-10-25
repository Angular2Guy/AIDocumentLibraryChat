package ch.xxx.aidoclibchat.domain.model.dto;

import java.util.List;

public record ClassificationSummary(List<ClassificationResult> summaries, List<ClassificationResult> results) {
}
