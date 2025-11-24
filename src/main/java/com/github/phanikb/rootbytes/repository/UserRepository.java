/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUniqueName(String uniqueName);

    Optional<UserEntity> findByPublicName(String publicName);
}
