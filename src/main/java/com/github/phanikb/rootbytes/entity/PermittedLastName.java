/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.CATEGORY_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.DESCRIPTION_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.NAME_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_M;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_S;

@Entity
@Table(name = "permitted_lastnames")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PermittedLastName extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Last name" + REQUIRED)
    @Size(max = SIZE_M, message = NAME_TOO_LONG)
    @Column(name = "last_name", nullable = false, unique = true, length = SIZE_M)
    private String lastName;

    @Size(max = SIZE_M, message = DESCRIPTION_TOO_LONG)
    @Column(name = "description", length = SIZE_M)
    private String description;

    @Size(max = SIZE_S, message = CATEGORY_TOO_LONG)
    @Column(name = "category", length = SIZE_S)
    private String category;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "permittedLastName", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LastNameAlias> aliases = new ArrayList<>();
}
