package com.toy.toyback.controller;

import com.toy.toyback.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AppController {

    private final AuthService authService;

    /**
     * 인가 코드 발급 → Redirect
     */
    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(@RequestParam("client_id") String clientId, @RequestParam("redirect_uri") String redirectUri, @RequestParam("response_type") String responseType) {
        if (!"code".equalsIgnoreCase(responseType)) {
            return ResponseEntity.badRequest().body("Unsupported response_type");
        }

        String code = authService.generateAuthorizationCode(clientId, redirectUri);
        URI redirect = URI.create(redirectUri + "?code=" + code);
        return ResponseEntity.status(HttpStatus.FOUND).location(redirect).build();
    }
}
