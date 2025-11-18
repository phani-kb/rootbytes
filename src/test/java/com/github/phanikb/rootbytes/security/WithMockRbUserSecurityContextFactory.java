/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.security;

import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.github.phanikb.rootbytes.enums.UserRole;
import com.github.phanikb.rootbytes.enums.UserStatus;

public class WithMockRbUserSecurityContextFactory implements WithSecurityContextFactory<WithMockRbUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockRbUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        RbUserDetails principal = new RbUserDetails(
                UUID.fromString(annotation.id()),
                annotation.email(),
                annotation.password(),
                UserRole.valueOf(annotation.role()),
                UserStatus.valueOf(annotation.status()),
                annotation.emailVerified());

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                principal, principal.getPassword(), principal.getAuthorities());
        context.setAuthentication(authentication);
        return context;
    }
}
