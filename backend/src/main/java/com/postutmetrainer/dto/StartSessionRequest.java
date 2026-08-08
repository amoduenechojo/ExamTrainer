package com.postutmetrainer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// mode: "topic" | "subject" | "full-mock". topicId required only when mode == "topic".
public record StartSessionRequest(@NotNull Long subjectId, Long topicId, @NotBlank String mode) {
}
