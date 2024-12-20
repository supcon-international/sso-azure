package com.sib.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AuthService {

    private final RestTemplate restTemplate;

    @Value("${auth.graphApiBase}")
    private String graphApiBase;

    @Value("${auth.tenantId}")
    private String tenantId;

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Value("${auth.redirectUri}")
    private String redirectUri;

    @Autowired
    public AuthService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String exchangeCodeForToken(String authCode) {
        String tokenEndpoint = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";
        String codeVerifier = "aBcD1234567890_-~.abcdefGHIJKLMNOPQRSTUVWXYZ"; // For debug purpose

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", authCode);
        params.add("redirect_uri", redirectUri);
        params.add("code_verifier", codeVerifier);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenEndpoint, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().get("access_token").toString();
        } else {
            throw new RuntimeException("Failed to exchange token");
        }
    }

    public String getUserId(String accessToken) {
        String url = graphApiBase + "/me";
    
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
    
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
    
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().get("id").toString();
        } else {
            throw new RuntimeException("Failed to fetch user ID");
        }
    }

    public List<Map<String, Object>> getUserTeams(String authToken) {
        String url = graphApiBase + "/me/joinedTeams";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (List<Map<String, Object>>) response.getBody().get("value");
        } else {
            throw new RuntimeException("Failed to fetch teams");
        }
    }

    public List<Map<String, Object>> getTeamChannels(String authToken, String teamId) {
        String url = String.format("%s/teams/%s/channels", graphApiBase, teamId);
    
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
    
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
    
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (List<Map<String, Object>>) response.getBody().get("value");
        } else {
            throw new RuntimeException("Failed to fetch channels for team " + teamId);
        }
    }

    public void sendMessageToChannel(String authToken, String teamId, String channelId, String message) {
        String url = String.format("%s/teams/%s/channels/%s/messages", graphApiBase, teamId, channelId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("body", Collections.singletonMap("content", message));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to send message");
        }
    }
}
