package com.sib.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.sib.service.*;
import com.sib.dto.*;

import java.util.Collections;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final TokenStorageService tokenStorageService;

    @Autowired
    public AuthController(AuthService authService, TokenStorageService tokenStorageService) {
        this.authService = authService;
        this.tokenStorageService = tokenStorageService;
    }

    @GetMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestParam String code) {
        try {
            String accessToken = authService.exchangeCodeForToken(code);
            String userId = authService.getUserId(accessToken); 
            tokenStorageService.saveToken(userId, accessToken);
    
            // 返回包含 userId 的 HTML 页面
            String successHtml = """
                <html>
                <script src="https://statics.teams.cdn.office.net/sdk/v1.13.0/js/MicrosoftTeams.min.js"></script>
                <script>
                    microsoftTeams.initialize();
                    microsoftTeams.authentication.notifySuccess(JSON.stringify({ userId: '%s', message: 'Authentication Successful' }));
                </script>
                <body>Authentication Successful! You can close this window.</body>
                </html>
                """.formatted(userId); // 将 userId 注入到返回的 HTML 中
            return ResponseEntity.ok(successHtml);
        } catch (Exception e) {
            String errorHtml = """
                <html>
                <script src="https://statics.teams.cdn.office.net/sdk/v1.13.0/js/MicrosoftTeams.min.js"></script>
                <script>
                    microsoftTeams.initialize();
                    microsoftTeams.authentication.notifyFailure("Authentication Failed: %s");
                </script>
                <body>Authentication Failed: %s</body>
                </html>
                """.formatted(e.getMessage(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorHtml);
        }
    }

    @GetMapping("/teams")
    public ResponseEntity<?> getTeams(@RequestHeader("X-User-ID") String userId) {
        try {
            String accessToken = tokenStorageService.getToken(userId);
            return ResponseEntity.ok(authService.getUserTeams(accessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @GetMapping("/teams/{teamId}/channels")
    public ResponseEntity<?> getChannels(@RequestHeader("X-User-ID") String userId, @PathVariable String teamId) {
        try {
            String accessToken = tokenStorageService.getToken(userId);
            return ResponseEntity.ok(authService.getTeamChannels(accessToken, teamId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PostMapping("/teams/{teamId}/channels/{channelId}/messages")
    public ResponseEntity<?> sendMessage(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable String teamId,
            @PathVariable String channelId,
            @RequestBody MessageRequest messageRequest) {
        try {
            String accessToken = tokenStorageService.getToken(userId);
            authService.sendMessageToChannel(accessToken, teamId, channelId, messageRequest.getMessage());
            return ResponseEntity.ok(Collections.singletonMap("message", "Message sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("message", e.getMessage()));
        }
    }
}
