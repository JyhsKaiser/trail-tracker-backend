package jyhs.trail.domain.service;

import jyhs.trail.domain.model.User;

public interface TokenService {
    String generateToken(User user);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
}