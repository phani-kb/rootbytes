/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.converter.UpperCaseConverter;
import com.github.phanikb.rootbytes.enums.UserRole;
import com.github.phanikb.rootbytes.enums.UserStatus;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true, length = 20)
    private String phone;

    @Column(name = "last_name", nullable = false, length = 100)
    @Convert(converter = UpperCaseConverter.class)
    private String lastName;

    @Column(name = "unique_name", nullable = false, unique = true, length = 6, updatable = false)
    private String uniqueName;

    @Column(name = "public_name", length = 100)
    @Nullable
    private String publicName;

    @Column(name = "password_hash")
    private String passwordHash;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.PENDING;

    @Builder.Default
    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Builder.Default
    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Builder.Default
    @Column(name = "ban_count")
    private Integer banCount = 0;

    @Nullable
    @Column(name = "banned_until")
    private Instant bannedUntil;

    /**
     * Returns the display name for the author. If publicName is set, returns
     * "PublicName (@uniqueName)" If publicName
     * is null or empty, returns "@uniqueName"
     *
     * @return the formatted display name for the author
     */
    public String getAuthorDisplay() {
        if (publicName != null && !publicName.trim().isEmpty()) {
            return publicName + " (@" + uniqueName + ")";
        }
        return "@" + uniqueName;
    }
}
