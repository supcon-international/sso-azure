package com.sib.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenStorageService {

    private final ConcurrentHashMap<String, String> tokenStore = new ConcurrentHashMap<>();

    public void saveToken(String userId, String accessToken) {
        tokenStore.put(userId, accessToken);
    }

    public String getToken(String userId) {
        return tokenStore.get(userId);
    }

    public void removeToken(String userId) {
        tokenStore.remove(userId);
    }
}
