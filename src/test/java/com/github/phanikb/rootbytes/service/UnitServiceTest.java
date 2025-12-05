/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.phanikb.rootbytes.dto.v1.request.UnitRequest;
import com.github.phanikb.rootbytes.entity.Unit;
import com.github.phanikb.rootbytes.enums.UnitType;
import com.github.phanikb.rootbytes.exception.DuplicateResourceException;
import com.github.phanikb.rootbytes.exception.ResourceInUseException;
import com.github.phanikb.rootbytes.exception.ResourceNotFoundException;
import com.github.phanikb.rootbytes.repository.UnitRepository;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnitServiceTest {

    @Mock
    private UnitRepository repository;

    @InjectMocks
    private UnitService service;

    private Unit tablespoon;
    private Unit teaspoon;
    private Unit gram;

    @BeforeEach
    public void setUp() {
        tablespoon = Unit.builder()
                .id(UUID.randomUUID())
                .name("Tablespoon")
                .abbreviation("tbsp")
                .unitType(UnitType.VOLUME)
                .description("Medium volume measurement")
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        teaspoon = Unit.builder()
                .id(UUID.randomUUID())
                .name("Teaspoon")
                .abbreviation("tsp")
                .unitType(UnitType.VOLUME)
                .description("Small volume measurement")
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        gram = Unit.builder()
                .id(UUID.randomUUID())
                .name("Gram")
                .abbreviation("g")
                .unitType(UnitType.WEIGHT)
                .description("Metric weight measurement")
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void shouldGetAllActiveUnits() {
        when(repository.findByIsActiveTrue()).thenReturn(List.of(tablespoon, teaspoon, gram));

        List<Unit> result = service.getAllActiveUnits();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertArrayEquals(
                new String[] {"Tablespoon", "Teaspoon", "Gram"},
                result.stream().map(Unit::getName).toArray());
        verify(repository).findByIsActiveTrue();
    }

    @Test
    void shouldGetUnitsByType() {
        when(repository.findByUnitTypeAndIsActiveTrue(UnitType.VOLUME)).thenReturn(List.of(tablespoon, teaspoon));

        List<Unit> result = service.getUnitsByType(UnitType.VOLUME);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertArrayEquals(
                new String[] {"Tablespoon", "Teaspoon"},
                result.stream().map(Unit::getName).toArray());
        verify(repository).findByUnitTypeAndIsActiveTrue(UnitType.VOLUME);
    }

    @Test
    void shouldGetUnitById() {
        UUID id = tablespoon.getId();
        when(repository.findById(id)).thenReturn(Optional.of(tablespoon));

        Unit result = service.getUnitById(id);

        assertNotNull(result);
        assertEquals("Tablespoon", result.getName());
        assertEquals("tbsp", result.getAbbreviation());
        verify(repository).findById(id);
    }

    @Test
    void shouldGetUnitByNameAndAbbreviation() {
        when(repository.findByName("Tablespoon")).thenReturn(Optional.of(tablespoon));
        when(repository.findByAbbreviation("tbsp")).thenReturn(Optional.of(tablespoon));

        Unit resultByName = service.getUnitByName("Tablespoon");
        Unit resultByAbbr = service.getUnitByAbbreviation("tbsp");

        assertNotNull(resultByName);
        assertEquals("Tablespoon", resultByName.getName());
        assertNotNull(resultByAbbr);
        assertEquals("tbsp", resultByAbbr.getAbbreviation());
    }

    @Test
    void shouldThrowExceptionWhenUnitNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        when(repository.findByName("Invalid")).thenReturn(Optional.empty());
        when(repository.findByAbbreviation("inv")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getUnitById(id));
        assertThrows(ResourceNotFoundException.class, () -> service.getUnitByName("Invalid"));
        assertThrows(ResourceNotFoundException.class, () -> service.getUnitByAbbreviation("inv"));
    }

    @Test
    void shouldCreateUnit_WithValidData() {
        UnitRequest newUnit = UnitRequest.builder()
                .name("Liter")
                .abbreviation("L")
                .unitType(UnitType.VOLUME)
                .description("Metric large volume measurement")
                .build();

        when(repository.existsByName("Liter")).thenReturn(false);
        when(repository.existsByAbbreviation("L")).thenReturn(false);
        when(repository.save(any(Unit.class))).thenAnswer(invocation -> {
            Unit unit = invocation.getArgument(0);
            unit.setId(UUID.randomUUID());
            unit.setCreatedAt(Instant.now());
            unit.setUpdatedAt(Instant.now());
            return unit;
        });

        Unit createdUnit = service.createUnit(newUnit);

        assertNotNull(createdUnit);
        assertNotNull(createdUnit.getId());
        assertEquals("Liter", createdUnit.getName());
        assertEquals("L", createdUnit.getAbbreviation());
        assertEquals(UnitType.VOLUME, createdUnit.getUnitType());
        assertEquals("Metric large volume measurement", createdUnit.getDescription());
        assertTrue(createdUnit.isActive());
        assertNotNull(createdUnit.getCreatedAt());
        assertNotNull(createdUnit.getUpdatedAt());

        verify(repository).existsByName("Liter");
        verify(repository).existsByAbbreviation("L");
        verify(repository).save(any(Unit.class));
    }

    @Test
    void shouldThrowException_WhenCreatingUnitWithDuplicateName() {
        UnitRequest newUnit = UnitRequest.builder()
                .name("Tablespoon")
                .abbreviation("L")
                .unitType(UnitType.VOLUME)
                .description("Metric large volume measurement")
                .build();

        when(repository.existsByName("Tablespoon")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.createUnit(newUnit));
        verify(repository).existsByName("Tablespoon");
        verify(repository, never()).save(any(Unit.class));
    }

    @Test
    void shouldThrowException_WhenCreatingUnitWithDuplicateAbbreviation() {
        UnitRequest newUnit = UnitRequest.builder()
                .name("Teaspoon")
                .abbreviation("tbsp")
                .unitType(UnitType.VOLUME)
                .description("Metric small volume measurement")
                .build();

        when(repository.existsByAbbreviation("tbsp")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.createUnit(newUnit));
        verify(repository).existsByAbbreviation("tbsp");
        verify(repository, never()).save(any(Unit.class));
    }

    @Test
    void shouldUpdateUnit_WithValidData() {
        UUID id = tablespoon.getId();
        UnitRequest request = UnitRequest.builder()
                .name("Tablespoon")
                .abbreviation("utbsp")
                .unitType(UnitType.VOLUME)
                .description("Updated large volume measurement")
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(tablespoon));
        when(repository.existsByAbbreviation("utbsp")).thenReturn(false);
        when(repository.save(any(Unit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Unit updatedUnit = service.updateUnitById(id, request);

        assertNotNull(updatedUnit);
        assertEquals("Tablespoon", updatedUnit.getName());
        assertEquals("utbsp", updatedUnit.getAbbreviation());
        assertEquals(UnitType.VOLUME, updatedUnit.getUnitType());
        assertEquals("Updated large volume measurement", updatedUnit.getDescription());

        verify(repository).findById(id);
        verify(repository).existsByAbbreviation("utbsp");
        verify(repository).save(any(Unit.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToDuplicateNameOrAbbreviation() {
        UUID id = tablespoon.getId();

        UnitRequest nameRequest = UnitRequest.builder()
                .name("Teaspoon")
                .abbreviation("tsp")
                .unitType(UnitType.VOLUME)
                .build();
        when(repository.findById(id)).thenReturn(Optional.of(tablespoon));
        when(repository.existsByName("Teaspoon")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.updateUnitById(id, nameRequest));

        UnitRequest abbrRequest = UnitRequest.builder()
                .name("Tablespoon")
                .abbreviation("utsp")
                .unitType(UnitType.VOLUME)
                .build();
        when(repository.findById(id)).thenReturn(Optional.of(tablespoon));
        when(repository.existsByAbbreviation("utsp")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> service.updateUnitById(id, abbrRequest));

        verify(repository, never()).save(any(Unit.class));
    }

    @Test
    void shouldUpdateWithoutDuplicateCheckWhenFieldsUnchanged() {
        UUID id = tablespoon.getId();
        UnitRequest request = UnitRequest.builder()
                .name(tablespoon.getName()) // same name
                .abbreviation(tablespoon.getAbbreviation()) // same abbreviation
                .unitType(UnitType.VOLUME)
                .description("Updated description")
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(tablespoon));
        when(repository.save(any(Unit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Unit updated = service.updateUnitById(id, request);

        assertEquals(tablespoon.getName(), updated.getName());
        verify(repository, never()).existsByName(anyString());
        verify(repository, never()).existsByAbbreviation(anyString());
        verify(repository).save(any(Unit.class));
    }

    @Test
    void shouldUpdateUnitWithNewUniqueName() {
        UUID id = tablespoon.getId();
        UnitRequest request = UnitRequest.builder()
                .name("NewTablespoon")
                .abbreviation(tablespoon.getAbbreviation())
                .unitType(UnitType.VOLUME)
                .description("Updated with new name")
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(tablespoon));
        when(repository.existsByName("NewTablespoon")).thenReturn(false);
        when(repository.save(any(Unit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Unit updated = service.updateUnitById(id, request);

        assertEquals("NewTablespoon", updated.getName());
        verify(repository).existsByName("NewTablespoon");
        verify(repository).save(any(Unit.class));
    }

    @Test
    void shouldActivateUnit() {
        UUID id = teaspoon.getId();
        teaspoon.setActive(false);

        when(repository.findById(id)).thenReturn(Optional.of(teaspoon));
        when(repository.save(any(Unit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.activateUnit(id);

        assertTrue(teaspoon.isActive());
        verify(repository).findById(id);
        verify(repository).save(any(Unit.class));
    }

    @Test
    void shouldDeactivateUnit() {
        UUID id = gram.getId();
        gram.setActive(false);

        when(repository.findById(id)).thenReturn(Optional.of(gram));
        when(repository.save(any(Unit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.deactivateUnit(id);

        assertFalse(gram.isActive());
        verify(repository).findById(id);
        verify(repository).save(any(Unit.class));
    }

    @Test
    void shouldThrowExceptionWhenDeactivatingUsedUnit() {
        UUID id = tablespoon.getId();

        when(repository.findById(id)).thenReturn(Optional.of(tablespoon));
        when(repository.isUsedInIngredients(id)).thenReturn(true);

        assertThrows(ResourceInUseException.class, () -> service.deactivateUnit(id));

        verify(repository).findById(id);
        verify(repository).isUsedInIngredients(id);
        verify(repository, never()).save(any(Unit.class));
    }
}
