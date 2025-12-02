/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_dietary_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDietaryInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false, unique = true)
    private Recipe recipe;

    @Column(name = "is_vegetarian")
    @Builder.Default
    private Boolean isVegetarian = false;

    @Column(name = "is_vegan")
    @Builder.Default
    private Boolean isVegan = false;

    @Column(name = "is_dairy_free")
    @Builder.Default
    private Boolean isDairyFree = false;

    @Column(name = "is_gluten_free")
    @Builder.Default
    private Boolean isGlutenFree = false;

    @Column(name = "has_nuts")
    @Builder.Default
    private Boolean hasNuts = false;

    @Column(name = "has_onion")
    @Builder.Default
    private Boolean hasOnion = false;

    @Column(name = "has_garlic")
    @Builder.Default
    private Boolean hasGarlic = false;

    @Column(name = "has_eggs")
    @Builder.Default
    private Boolean hasEggs = false;

    @Column(name = "has_soy")
    @Builder.Default
    private Boolean hasSoy = false;

    @Column(name = "has_shellfish")
    @Builder.Default
    private Boolean hasShellfish = false;
}
