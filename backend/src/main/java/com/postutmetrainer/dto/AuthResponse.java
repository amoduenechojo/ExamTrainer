package com.postutmetrainer.dto;

// inviteCode is populated for students only; null for parents.
public record AuthResponse(String token, String role, Long profileId, String fullName, String inviteCode) {
}
