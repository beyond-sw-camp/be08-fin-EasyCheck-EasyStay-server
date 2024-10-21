package com.beyond.easycheck.common.security.config;

import com.beyond.easycheck.common.security.filter.JwtAuthenticationFilter;
import com.beyond.easycheck.common.security.provider.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class EasyCheckSecurityConfig {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Bean
    public SecurityFilterChain defaultConfig(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // jwt authentication filter
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // api endpoint
        http
                .authorizeHttpRequests(registry -> {

                    registry.requestMatchers(
                                    "/api/v1/users",
                                    "/api/v1/users/login",
                                    "/api/v1/users/change-password",
                                    "/api/v1/verification-code",
                                    "/api/v1/verify-code",
                                    "/api/v1/sms/**",

                                    // Swagger UI v3 (OpenAPI)
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**"
                            )
                            .permitAll();

                    registry.requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll();

                    registry.requestMatchers("/api/v1/users/{id}/status")
                            .hasRole("ADMIN");

                    // 권한 생성은 SUPER_ADMIN만 가능
                    registry.requestMatchers("/api/v1/permissions/**")
                            .hasRole("SUPER_ADMIN");

                    registry.anyRequest().authenticated();
                });


        return http.build();
    }

    // 비밀번호 암호화용 인코더 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtAuthenticationProvider);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "PATCH"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}