package com.beyond.easycheck.user.application.mock;



import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithEasyCheckMockUserSecurityContextFactory.class)
public @interface WithEasyCheckMockUser {
    long id() default 1000L;
    String role() default "USER";
}
