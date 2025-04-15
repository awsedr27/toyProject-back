package com.toy.toyback.service;

import com.toy.toyback.entity.AuthorizationEntity;
import com.toy.toyback.entity.AppEntity;
import com.toy.toyback.entity.AppRole;
import com.toy.toyback.jwt.JwtProvider;
import com.toy.toyback.repository.AuthorizationRepository;
import com.toy.toyback.repository.AppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppRepository appRepository;
    private final AuthorizationRepository authorizationRepository;
    private final JwtProvider jwtProvider;

    /**
     * 인가 코드 발급
     *
     * @param clientId     요청한 앱 ID
     * @param redirectUri  등록된 redirect URI
     * @return 인가 코드
     */
    public String generateAuthorizationCode(String clientId, String redirectUri) {

        AppEntity app = appRepository.findByClientId(clientId).orElseThrow(() -> new IllegalArgumentException("Invalid Client Id"));

        if (!app.getRedirectUri().equals(redirectUri)) {
            throw new IllegalArgumentException("Invalid Redirect URI");
        }

        String code = UUID.randomUUID().toString();
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        authorizationEntity.setCode(code);
        authorizationEntity.setClientId(clientId);
        authorizationEntity.setRedirectUri(redirectUri);
        authorizationEntity.setCreatedAt(LocalDateTime.now());
        authorizationEntity.setUsed(false);

        authorizationRepository.save(authorizationEntity);
        return code;
    }

    public Map<String, String> exchangeCodeForToken(String code, String clientId, String clientPassword) {
        AuthorizationEntity authCode = authorizationRepository.findById(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid code"));

        if (authCode.isUsed() || authCode.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalArgumentException("Code expired or used");
        }

        AppEntity app = appRepository.findByClientId(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid client"));

        if (!app.getClientPassword().equals(clientPassword)) {
            throw new IllegalArgumentException("Invalid secret");
        }

        authCode.setUsed(true);
        authorizationRepository.save(authCode);

        String accessToken = jwtProvider.generateAccessToken(clientId, AppRole.APP);
        String refreshToken = jwtProvider.generateRefreshToken(clientId);

        Map<String, String> result = new HashMap<>();
        result.put("access_token", accessToken);
        result.put("refresh_token", refreshToken);
        result.put("token_type", "Bearer");
        result.put("expires_in", String.valueOf(30 * 60)); // 30분

        return result;
    }
}
