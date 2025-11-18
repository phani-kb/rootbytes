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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.UserRole;
import com.github.phanikb.rootbytes.enums.UserStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RbUserDetails implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String email;
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
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.BANNED;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE && emailVerified;
    }

    public UserEntity toUserEntity() {
        UserEntity user = new UserEntity();
        user.setId(this.id);
        user.setEmail(this.email);
        user.setPasswordHash(this.password);
        user.setRole(this.role);
        user.setStatus(this.status);
        user.setEmailVerified(this.emailVerified);
        return user;
    }
}
