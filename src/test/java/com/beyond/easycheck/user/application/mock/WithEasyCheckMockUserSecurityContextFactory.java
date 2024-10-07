package com.beyond.easycheck.user.application.mock;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithEasyCheckMockUserSecurityContextFactory implements WithSecurityContextFactory<WithEasyCheckMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithEasyCheckMockUser annotation) {

        Authentication auth = new UsernamePasswordAuthenticationToken(
                annotation.id(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + annotation.role()))
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);

        return context;
    }
}