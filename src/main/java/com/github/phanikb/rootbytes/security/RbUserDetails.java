/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.security;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import jakarta.annotation.Nullable;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.UserRole;
import com.github.phanikb.rootbytes.enums.UserStatus;

@Getter
@AllArgsConstructor
public class RbUserDetails implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;

    private String email;

    @Nullable
    private String password;

    private UserRole role;

    private UserStatus status;

    private boolean emailVerified;

    public static RbUserDetails fromUser(UserEntity user) {
        return new RbUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.getStatus(),
                user.getEmailVerified());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password != null ? password : "";
    }

    @Override
    public String getUsername() {
        return email != null ? email : "";
    }

    @Override
    public boolean isAccountNonLocked() {
        return status == null || status != UserStatus.BANNED;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE && emailVerified;
    }

    public UserEntity toUserEntity() {
        UserEntity user = new UserEntity();
        user.setId(this.id);
        user.setEmail(this.email);
        if (this.password != null) {
            user.setPasswordHash(this.password);
        }
        user.setRole(this.role);
        user.setStatus(this.status);
        user.setEmailVerified(this.emailVerified);
        return user;
    }
}
