/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.Unit;
import com.github.phanikb.rootbytes.enums.UnitType;

@Repository
public interface UnitRepository extends JpaRepository<Unit, UUID> {

    Optional<Unit> findByName(String name);

    Optional<Unit> findByAbbreviation(String abbreviation);

    List<Unit> findByUnitType(UnitType type);

    List<Unit> findByIsActiveTrue();

    List<Unit> findByUnitTypeAndIsActiveTrue(UnitType type);

    boolean existsByName(String name);

    boolean existsByAbbreviation(String abbreviation);
}
