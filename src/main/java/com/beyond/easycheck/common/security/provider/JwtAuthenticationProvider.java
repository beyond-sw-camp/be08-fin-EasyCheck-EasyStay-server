package com.beyond.easycheck.common.security.provider;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.common.security.exception.JwtMessageType;
import com.beyond.easycheck.common.security.infrastructure.persistence.repository.ExpiredAccessTokenJpaRepository;
import com.beyond.easycheck.common.security.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtil jwtUtil;

    private final ExpiredAccessTokenJpaRepository expiredAccessTokenJpaRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String accessToken = (String) authentication.getPrincipal();

        if (jwtUtil.isExpired(accessToken) || bannedToken(accessToken)) {
            throw new EasyCheckException(JwtMessageType.TOKEN_EXPIRED);
        }

        return new UsernamePasswordAuthenticationToken(
                jwtUtil.parseUserId(accessToken),
                null,
                jwtUtil.parseAuthorities(accessToken)
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private boolean bannedToken(String accessToken) {
        return expiredAccessTokenJpaRepository
                .findById(accessToken)
                .isPresent();
    }
}
