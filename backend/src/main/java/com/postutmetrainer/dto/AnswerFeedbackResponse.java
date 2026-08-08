package com.postutmetrainer.dto;

public record AnswerFeedbackResponse(
        boolean isCorrect, Long correctOptionId, String explanation, String shortcut) {
}
