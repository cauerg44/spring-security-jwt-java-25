package com.security.test.api.utils;

import com.security.test.api.entity.User;

public interface TokenProviderUtil {
    String generateToken(User user);
    String validateToken(String token);
}