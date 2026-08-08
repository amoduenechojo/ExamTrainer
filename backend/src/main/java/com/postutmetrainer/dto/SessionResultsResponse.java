package com.postutmetrainer.dto;

import java.util.List;

public record SessionResultsResponse(
        int score, int totalQuestions, double percentage, List<WeakTopicResponse> topicsToRevisit) {
}
