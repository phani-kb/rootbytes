/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.github.phanikb.rootbytes.enums.RecipeDifficulty;
import com.github.phanikb.rootbytes.enums.RecipeStatus;

@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = {"ingredients", "instructions", "author"})
public class Recipe {
    private static final String RECIPE = "recipe";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String story;

    @Column(nullable = false)
    @Builder.Default
    private Integer version = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private RecipeStatus status = RecipeStatus.DRAFT;

    @Column(name = "is_current_version")
    @Builder.Default
    private Boolean isCurrentVersion = false;

    @Column(name = "is_private")
    @Builder.Default
    private Boolean isPrivate = false;

    @Column(name = "strike_count")
    @Builder.Default
    private Integer strikeCount = 0;

    @OneToOne(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RecipeDietaryInfo dietaryInfo;

    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;

    @Column(name = "cook_time_minutes")
    private Integer cookTimeMinutes;

    private Integer servings;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RecipeDifficulty difficulty;

    @Column(length = 50)
    private String cuisine;

    @Column(length = 50)
    private String category;

    @OneToMany(mappedBy = RECIPE, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Ingredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = RECIPE, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepNumber")
    @Builder.Default
    private List<Instruction> instructions = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }

    public void addInstruction(Instruction instruction) {
        if (instruction.getDescription() == null || instruction.getDescription().isBlank()) {
            return;
        }
        instructions.add(instruction);
        instruction.setRecipe(this);
    }

    public List<Ingredient> getIngredients() {
        return Collections.unmodifiableList(ingredients == null ? new ArrayList<>() : new ArrayList<>(ingredients));
    }

    public void setIngredients(List<Ingredient> ingredients) {
        List<Ingredient> result = new ArrayList<>();
        for (Ingredient i : ingredients) {
            if (i.getName() != null && !i.getName().isBlank()) {
                i.setRecipe(this);
                i.setOrderIndex(result.size() + 1);
                result.add(i);
            }
        }
        this.ingredients = result;
    }

    public List<Instruction> getInstructions() {
        return Collections.unmodifiableList(instructions == null ? new ArrayList<>() : new ArrayList<>(instructions));
    }

    public void setInstructions(List<Instruction> instructions) {
        List<Instruction> result = new ArrayList<>();
        int step = 1;
        for (Instruction i : instructions) {
            if (i.getDescription() != null && !i.getDescription().isBlank()) {
                i.setRecipe(this);
                i.setStepNumber(step);
                step++;
                result.add(i);
            }
        }
        this.instructions = result;
    }
}
