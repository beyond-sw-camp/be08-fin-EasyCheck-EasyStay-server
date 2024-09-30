package com.beyond.easycheck.user.application.domain;

import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.permission.PermissionEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.role.UserPermissionEntity;
import com.beyond.easycheck.user.infrastructure.persistence.mariadb.entity.user.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@ToString
@RequiredArgsConstructor
public class EasyCheckUserDetails implements UserDetails {

    private final String email;
    private final String password;
    private final String status;

    // 유저 역할 필드
    private String role;
    // 유저 권한 필드
    private Set<String> permissions;

    public EasyCheckUserDetails(UserEntity user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.status = user.getStatus();
        this.role = user.getRole().getName();

        this.permissions = user.getUserPermission()
                .stream()
                .map(UserPermissionEntity::getPermission)
                .map(PermissionEntity::getName)
                .collect(Collectors.toSet());

    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 유저 역할과 권한을 리스트 형태로 모두 합친다.
        return Stream.concat(
                        Stream.of(new SimpleGrantedAuthority("ROLE_" + role)),
                        permissions.stream().map(SimpleGrantedAuthority::new)
                )
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return "EXPIRED".equals(this.status);
    }

    @Override
    public boolean isAccountNonLocked() {
        return "LOCKED".equals(this.status);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return "ENABLED".equals(this.status);
    }
}