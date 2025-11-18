/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.security;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.security.test.context.support.WithSecurityContext;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
@WithSecurityContext(factory = WithMockRbUserSecurityContextFactory.class)
public @interface WithMockRbUser {

    String id() default "00000000-0000-0000-0000-000000000001";

    String email() default "test.user@rootbytes.dev";

    String password() default "password";

    String role() default "USER";

    String status() default "ACTIVE";

    boolean emailVerified() default true;
}
