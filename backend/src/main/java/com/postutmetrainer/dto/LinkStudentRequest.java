package com.postutmetrainer.dto;

import jakarta.validation.constraints.NotBlank;

public record LinkStudentRequest(@NotBlank String inviteCode) {
}
