/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.NAME_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_M;

@Entity
@Table(name = "lastname_aliases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class LastNameAlias {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Permitted last name" + REQUIRED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permitted_lastname_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PermittedLastName permittedLastName;

    @NotBlank(message = "Alias" + REQUIRED)
    @Size(max = SIZE_M, message = NAME_TOO_LONG)
    @Column(name = "alias", nullable = false, length = SIZE_M)
    private String alias;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private java.time.Instant updatedAt;
}
