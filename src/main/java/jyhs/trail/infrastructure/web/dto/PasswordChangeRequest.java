package jyhs.trail.infrastructure.web.dto;
public record PasswordChangeRequest(
        String oldPassword,
        String newPassword
) {}