package com.postutmetrainer.dto;

public record WeakTopicResponse(
        Long topicId, String topicName, Long subjectId, String subjectName, double accuracy) {
}
