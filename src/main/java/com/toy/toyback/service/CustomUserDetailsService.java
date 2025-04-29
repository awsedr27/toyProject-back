package com.toy.toyback.service;

import com.toy.toyback.entity.AppRole;
import com.toy.toyback.entity.UserEntity;
import com.toy.toyback.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService  implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DB에서 유저를 조회 (username 기준)
        UserEntity userEntity = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // UserDetails 객체 반환 , custom detail로 변화시킬 예정
        return new org.springframework.security.core.userdetails.User(
                userEntity.getUserId(),
                userEntity.getPassword(),
                List.of(new SimpleGrantedAuthority(AppRole.ROLE_USER.name())) // 권한 부여 (예: ROLE_USER)
        );
    }

}

