/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.phanikb.rootbytes.dto.v1.request.UnitRequest;
import com.github.phanikb.rootbytes.dto.v1.response.UnitResponse;
import com.github.phanikb.rootbytes.entity.Unit;
import com.github.phanikb.rootbytes.enums.UnitType;
import com.github.phanikb.rootbytes.mapper.UnitMapper;
import com.github.phanikb.rootbytes.service.UnitService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitControllerTest {

    @Mock
    private UnitService service;

    @Mock
    private UnitMapper mapper;

    private UnitController controller;

    private UUID unitId;
    private Unit unit;
    private UnitResponse response;

    @BeforeEach
    void setUp() {
        controller = new UnitController(service, mapper);
        unitId = UUID.randomUUID();

        unit = Unit.builder()
                .id(unitId)
                .name("Cup")
                .abbreviation("cup")
                .unitType(UnitType.VOLUME)
                .isActive(true)
                .build();

        response = UnitResponse.builder()
                .id(unitId)
                .name("Cup")
                .abbreviation("cup")
                .unitType(UnitType.VOLUME)
                .isActive(true)
                .build();
    }

    @Test
    void shouldGetAllActiveUnits() {
        when(service.getAllActiveUnits()).thenReturn(Collections.singletonList(unit));
        when(mapper.toResponse(any(Unit.class))).thenReturn(response);

        var result = controller.getAllActiveUnits();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(1, result.getBody().getData().size());
    }

    @Test
    void shouldGetUnitById() {
        when(service.getUnitById(unitId)).thenReturn(unit);
        when(mapper.toResponse(unit)).thenReturn(response);

        var result = controller.getUnitById(unitId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(response, result.getBody().getData());
    }

    @Test
    void shouldGetUnitsByType() {
        when(service.getUnitsByType(UnitType.VOLUME)).thenReturn(Collections.singletonList(unit));
        when(mapper.toResponse(any(Unit.class))).thenReturn(response);

        var result = controller.getUnitsByType(UnitType.VOLUME);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(1, result.getBody().getData().size());
    }

    @Test
    void shouldCreateUnit() {
        var request = UnitRequest.builder()
                .name("Tablespoon")
                .abbreviation("tbsp")
                .unitType(UnitType.VOLUME)
                .build();

        when(service.createUnit(any(UnitRequest.class))).thenReturn(unit);
        when(mapper.toResponse(unit)).thenReturn(response);

        var result = controller.createUnit(request);

        assertEquals(201, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(response, result.getBody().getData());
    }

    @Test
    void shouldUpdateUnit() {
        var request = UnitRequest.builder()
                .name("Cup")
                .abbreviation("c")
                .unitType(UnitType.VOLUME)
                .build();

        when(service.updateUnitById(any(UUID.class), any(UnitRequest.class))).thenReturn(unit);
        when(mapper.toResponse(unit)).thenReturn(response);

        var result = controller.updateUnit(unitId, request);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(response, result.getBody().getData());
    }

    @Test
    void shouldActivateUnit() {
        var result = controller.activateUnit(unitId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
    }

    @Test
    void shouldDeactivateUnit() {
        var result = controller.deactivateUnit(unitId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
    }
}
