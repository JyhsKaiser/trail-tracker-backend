package jyhs.trail.domain.model;

import java.time.Instant;

public record RefreshToken(
        Long id,
        String token,
        Instant expiryDate,
        User user
) {
    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}