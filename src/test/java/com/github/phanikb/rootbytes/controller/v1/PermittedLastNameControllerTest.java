/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.github.phanikb.rootbytes.dto.v1.request.PermittedLastNameRequest;
import com.github.phanikb.rootbytes.dto.v1.response.PermittedLastNameResponse;
import com.github.phanikb.rootbytes.entity.LastNameAlias;
import com.github.phanikb.rootbytes.entity.PermittedLastName;
import com.github.phanikb.rootbytes.mapper.PermittedLastNameMapper;
import com.github.phanikb.rootbytes.service.PermittedLastNameService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermittedLastNameControllerTest {

    @Mock
    private PermittedLastNameService permittedLastNameService;

    @Mock
    private PermittedLastNameMapper mapper;

    private PermittedLastNameController controller;

    private PermittedLastName lastName;
    private PermittedLastNameResponse response;
    private UUID testId;

    @BeforeEach
    void setUp() {
        controller = new PermittedLastNameController(permittedLastNameService, mapper);

        testId = UUID.randomUUID();

        lastName = PermittedLastName.builder()
                .id(testId)
                .lastName("Kumar")
                .category("Common")
                .description("Common English surname")
                .isActive(true)
                .build();

        response = PermittedLastNameResponse.builder()
                .id(testId)
                .lastName("Kumar")
                .category("Common")
                .description("Common English surname")
                .isActive(true)
                .build();
    }

    @Test
    void shouldGetAllLastNames() {
        when(permittedLastNameService.getAllActiveLastNames()).thenReturn(Collections.singletonList(lastName));
        when(mapper.toResponse(lastName)).thenReturn(response);

        var result = controller.getAllActiveLastNames();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(1, result.getBody().getData().size());
        assertEquals("Kumar", result.getBody().getData().getFirst().getLastName());

        verify(permittedLastNameService).getAllActiveLastNames();
    }

    @Test
    void shouldSearchLastNames() {
        when(permittedLastNameService.searchLastNames("Kum")).thenReturn(Collections.singletonList(lastName));
        when(mapper.toResponse(lastName)).thenReturn(response);

        var result = controller.searchLastNames("Kum");

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(1, result.getBody().getData().size());
        assertEquals("Kumar", result.getBody().getData().getFirst().getLastName());

        verify(permittedLastNameService).searchLastNames("Kum");
    }

    @Test
    void shouldGetByCategory() {
        when(permittedLastNameService.getByCategory("Common")).thenReturn(Collections.singletonList(lastName));
        when(mapper.toResponse(lastName)).thenReturn(response);

        var result = controller.getByCategory("Common");

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(1, result.getBody().getData().size());
        assertEquals("Common", result.getBody().getData().getFirst().getCategory());

        verify(permittedLastNameService).getByCategory("Common");
    }

    @Test
    void shouldGetCategories() {
        List<String> categories = Arrays.asList("Common", "Irish", "Scottish");
        when(permittedLastNameService.getAllCategories()).thenReturn(categories);

        var result = controller.getAllCategories();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(3, result.getBody().getData().size());

        verify(permittedLastNameService).getAllCategories();
    }

    @Test
    void shouldReturnLastNameWithAliases() {
        PermittedLastName lastNameWithAliases = PermittedLastName.builder()
                .id(lastName.getId())
                .lastName("Kumar")
                .category("Common")
                .description("Common English surname")
                .isActive(true)
                .aliases(new ArrayList<>())
                .build();

        LastNameAlias alias1 = LastNameAlias.builder()
                .id(UUID.randomUUID())
                .alias("Kumari")
                .permittedLastName(lastNameWithAliases)
                .build();

        LastNameAlias alias2 = LastNameAlias.builder()
                .id(UUID.randomUUID())
                .alias("Kumare")
                .permittedLastName(lastNameWithAliases)
                .build();

        lastNameWithAliases.getAliases().add(alias1);
        lastNameWithAliases.getAliases().add(alias2);

        PermittedLastNameResponse responseWithAliases = PermittedLastNameResponse.builder()
                .id(lastName.getId())
                .lastName("Kumar")
                .category("Common")
                .description("Common English surname")
                .isActive(true)
                .aliases("Kumari, Kumare")
                .build();

        when(permittedLastNameService.getAllActiveLastNames())
                .thenReturn(Collections.singletonList(lastNameWithAliases));
        when(mapper.toResponse(lastNameWithAliases)).thenReturn(responseWithAliases);

        var result = controller.getAllActiveLastNames();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Kumar", result.getBody().getData().getFirst().getLastName());
        assertEquals("Kumari, Kumare", result.getBody().getData().getFirst().getAliases());

        verify(permittedLastNameService).getAllActiveLastNames();
    }

    @Test
    void shouldSearchLastNamesByAlias() {
        PermittedLastName lastNameWithAliases = PermittedLastName.builder()
                .id(lastName.getId())
                .lastName("Johnson")
                .category("Common")
                .isActive(true)
                .aliases(new ArrayList<>())
                .build();

        LastNameAlias alias = LastNameAlias.builder()
                .id(UUID.randomUUID())
                .alias("Jon")
                .permittedLastName(lastNameWithAliases)
                .build();

        lastNameWithAliases.getAliases().add(alias);

        PermittedLastNameResponse responseWithAlias = PermittedLastNameResponse.builder()
                .id(lastName.getId())
                .lastName("Johnson")
                .category("Common")
                .isActive(true)
                .aliases("Jon")
                .build();

        when(permittedLastNameService.searchLastNames("Jon"))
                .thenReturn(Collections.singletonList(lastNameWithAliases));
        when(mapper.toResponse(lastNameWithAliases)).thenReturn(responseWithAlias);

        var result = controller.searchLastNames("Jon");

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Johnson", result.getBody().getData().getFirst().getLastName());
        assertEquals("Jon", result.getBody().getData().getFirst().getAliases());

        verify(permittedLastNameService).searchLastNames("Jon");
    }

    @Test
    void shouldGetPaginatedLastNames() {
        Page<PermittedLastName> page = new PageImpl<>(Collections.singletonList(lastName));
        when(permittedLastNameService.getAllActiveLastNames(any())).thenReturn(page);
        when(mapper.toResponse(lastName)).thenReturn(response);

        var result = controller.getAllActiveLastNamesPaginated(0, 20, "lastName");

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Kumar", result.getBody().getData().getContent().getFirst().getLastName());
        assertEquals(1, result.getBody().getData().getTotalElements());

        verify(permittedLastNameService).getAllActiveLastNames(any(PageRequest.class));
    }

    @Test
    void shouldGetValidationStatus() {
        when(permittedLastNameService.isValidationEnabled()).thenReturn(true);

        var result = controller.getValidationStatus();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertTrue(result.getBody().getData().get("enabled"));

        verify(permittedLastNameService).isValidationEnabled();
    }

    @Test
    void shouldGetById() {
        when(permittedLastNameService.getById(testId)).thenReturn(lastName);
        when(mapper.toResponse(lastName)).thenReturn(response);

        var result = controller.getById(testId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Kumar", result.getBody().getData().getLastName());

        verify(permittedLastNameService).getById(testId);
    }

    @Test
    void shouldCreateLastName() {
        PermittedLastNameRequest request = PermittedLastNameRequest.builder()
                .lastName("Williams")
                .category("Common")
                .description("Common surname")
                .aliases(Arrays.asList("Will", "Bill"))
                .build();

        when(permittedLastNameService.createValidLastName(any(PermittedLastNameRequest.class)))
                .thenReturn(lastName);
        when(mapper.toResponse(lastName)).thenReturn(response);

        var result = controller.create(request);

        assertEquals(201, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Valid last name created successfully", result.getBody().getMessage());

        verify(permittedLastNameService).createValidLastName(any(PermittedLastNameRequest.class));
    }

    @Test
    void shouldUpdateLastName() {
        PermittedLastNameRequest request = PermittedLastNameRequest.builder()
                .lastName("Kumar")
                .category("Updated")
                .description("Updated description")
                .aliases(List.of("Kumari"))
                .build();

        when(permittedLastNameService.updateValidLastName(eq(testId), any(PermittedLastNameRequest.class)))
                .thenReturn(lastName);
        when(mapper.toResponse(lastName)).thenReturn(response);

        var result = controller.update(testId, request);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Valid last name updated successfully", result.getBody().getMessage());

        verify(permittedLastNameService).updateValidLastName(eq(testId), any(PermittedLastNameRequest.class));
    }

    @Test
    void shouldDeactivateLastName() {
        doNothing().when(permittedLastNameService).deactivateLastName(testId);

        var result = controller.deactivate(testId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Last name deactivated successfully", result.getBody().getMessage());

        verify(permittedLastNameService).deactivateLastName(testId);
    }

    @Test
    void shouldActivateLastName() {
        doNothing().when(permittedLastNameService).activateLastName(testId);

        var result = controller.activate(testId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Last name activated successfully", result.getBody().getMessage());

        verify(permittedLastNameService).activateLastName(testId);
    }

    @Test
    void shouldDeleteLastName() {
        doNothing().when(permittedLastNameService).deleteLastName(testId);

        var result = controller.delete(testId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals("Last name deleted successfully", result.getBody().getMessage());

        verify(permittedLastNameService).deleteLastName(testId);
    }
}
