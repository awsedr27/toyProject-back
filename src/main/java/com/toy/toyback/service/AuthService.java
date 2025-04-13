package com.toy.toyback.service;

import com.toy.toyback.entity.AuthorizationEntity;
import com.toy.toyback.entity.UserEntity;
import com.toy.toyback.repository.AuthorizationRepository;
import com.toy.toyback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthorizationRepository authorizationRepository;

    /**
     * 인가 코드 발급
     *
     * @param clientId     요청한 앱 ID
     * @param redirectUri  등록된 redirect URI
     * @return 인가 코드
     */
    public String generateAuthorizationCode(String clientId, String redirectUri) {

        UserEntity user = userRepository.findByClientId(clientId).orElseThrow(() -> new IllegalArgumentException("Invalid Client Id"));

        if (!user.getRedirectUri().equals(redirectUri)) {
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
}
