package com.postutmetrainer.dto;

import java.util.List;

public record QuestionResponse(Long id, String stem, List<QuestionOptionResponse> options) {
}
