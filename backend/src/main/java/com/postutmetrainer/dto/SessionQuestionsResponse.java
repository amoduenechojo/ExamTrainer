package com.postutmetrainer.dto;

import java.util.List;

public record SessionQuestionsResponse(Long sessionId, String mode, List<QuestionResponse> questions) {
}
