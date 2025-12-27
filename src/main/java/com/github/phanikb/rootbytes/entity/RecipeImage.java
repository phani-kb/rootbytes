/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.entity;

import java.time.Instant;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.github.phanikb.rootbytes.enums.recipe.RecipeImageApprovalStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "recipe_images")
@EntityListeners(AuditingEntityListener.class)
public class RecipeImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false)
    private String fileName;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false)
    private String storedName;

    @Size(max = 1000)
    @NotNull
    @Column(nullable = false, length = 1000)
    private String filePath;

    @Size(max = 500)
    @Column(length = 500)
    private String thumbnailPath;

    @NotNull
    @Column(nullable = false)
    private Long fileSize;

    @Size(max = 100)
    @Column(length = 100)
    private String mimeType;

    private Integer width;

    private Integer height;

    @Size(max = 255)
    private String caption;

    @Enumerated(EnumType.STRING)
    @Size(max = 20)
    @NotNull
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RecipeImageApprovalStatus approvalStatus = RecipeImageApprovalStatus.PENDING;

    private Instant approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private UserEntity approvedBy;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String rejectedReason;

    private Boolean isPrimary;

    @NotNull
    @Column(nullable = false)
    private Integer orderIndex;

    @NotNull
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    @NotNull
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private UserEntity uploadedBy;

    @NotNull
    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    @Size(max = 45)
    @Column(length = 45)
    private String uploadIp;

    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeImageModeration> moderations;

    public boolean isApproved() {
        return this.approvalStatus == RecipeImageApprovalStatus.APPROVED;
    }

    public boolean isRejected() {
        return this.approvalStatus == RecipeImageApprovalStatus.REJECTED;
    }

    public boolean isPending() {
        return this.approvalStatus == RecipeImageApprovalStatus.PENDING;
    }
}
