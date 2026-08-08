package com.postutmetrainer.dto;

import java.util.List;

public record StudentProgressResponse(
        double overallAccuracy, long sessionsCompleted, List<WeakTopicResponse> weakTopics) {
}
