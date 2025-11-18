/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.github.phanikb.rootbytes.config.TestJpaConfig;
import com.github.phanikb.rootbytes.entity.Unit;
import com.github.phanikb.rootbytes.enums.UnitType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
class UnitRepositoryTest {

    @Autowired
    private UnitRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Unit activeVolumeUnit;

    @BeforeEach
    void setUp() {
        activeVolumeUnit = repository.findByName("Liter").orElseThrow();

        Unit gallon = repository.findByName("Gallon").orElseThrow();
        gallon.setActive(false);
        repository.saveAndFlush(gallon);

        entityManager.clear();
    }

    @Test
    void shouldFindUnitByName() {
        var result = repository.findByName("Liter");
        assertTrue(result.isPresent());
        assertEquals(activeVolumeUnit.getId(), result.get().getId());
    }

    @Test
    void shouldFindUnitByAbbreviation() {
        var result = repository.findByAbbreviation("L");
        assertTrue(result.isPresent());
        assertEquals(activeVolumeUnit.getId(), result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenUnitNameNotFound() {
        var result = repository.findByName("NonExistent");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindUnitsByType() {
        List<Unit> volumeUnits = repository.findByUnitType(UnitType.VOLUME);

        assertTrue(volumeUnits.size() >= 2, "Should find at least 2 volume units from migrations");
        assertTrue(volumeUnits.stream().allMatch(u -> u.getUnitType() == UnitType.VOLUME));
    }

    @Test
    void shouldFindOnlyActiveUnits() {
        List<Unit> activeUnits = repository.findByIsActiveTrue();

        assertTrue(activeUnits.size() >= 2, "Should find at least 2 active units");
        assertTrue(activeUnits.stream().allMatch(Unit::isActive));
        assertTrue(activeUnits.stream().anyMatch(u -> u.getName().equals("Liter")));
        assertFalse(activeUnits.stream().anyMatch(u -> u.getName().equals("Gallon")), "Gallon should be inactive");
    }

    @Test
    void shouldFindActiveUnitsByType() {
        List<Unit> activeVolumeUnits = repository.findByUnitTypeAndIsActiveTrue(UnitType.VOLUME);

        assertFalse(activeVolumeUnits.isEmpty(), "Should find at least 1 active volume unit");
        assertTrue(activeVolumeUnits.stream().anyMatch(u -> u.getName().equals("Liter")));
        assertFalse(
                activeVolumeUnits.stream().anyMatch(u -> u.getName().equals("Gallon")),
                "Gallon should not be in active list");
    }

    @Test
    void shouldCheckExistenceByName() {
        assertTrue(repository.existsByName("Liter"));
        assertFalse(repository.existsByName("NonExistent"));
    }

    @Test
    void shouldCheckExistenceByAbbreviation() {
        assertTrue(repository.existsByAbbreviation("L"));
        assertFalse(repository.existsByAbbreviation("xyz"));
    }

    @Test
    void shouldEnforceUniqueNameConstraint() {
        Unit duplicate = Unit.builder()
                .name("Liter")
                .abbreviation("l")
                .unitType(UnitType.VOLUME)
                .isActive(true)
                .build();
        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(duplicate));
    }

    @Test
    void shouldEnforceUniqueAbbreviationConstraint() {
        Unit duplicate = Unit.builder()
                .name("Litre")
                .abbreviation("L")
                .unitType(UnitType.VOLUME)
                .isActive(true)
                .build();
        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(duplicate));
    }
}
