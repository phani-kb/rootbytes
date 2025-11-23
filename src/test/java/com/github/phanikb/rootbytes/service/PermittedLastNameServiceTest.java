/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.github.phanikb.rootbytes.dto.request.PermittedLastNameRequest;
import com.github.phanikb.rootbytes.entity.PermittedLastName;
import com.github.phanikb.rootbytes.exception.DuplicateResourceException;
import com.github.phanikb.rootbytes.exception.InvalidLastNameException;
import com.github.phanikb.rootbytes.exception.ResourceNotFoundException;
import com.github.phanikb.rootbytes.repository.PermittedLastNameRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermittedLastNameServiceTest {

    @Mock
    private PermittedLastNameRepository validLastNameRepository;

    @Mock
    private SystemConfigService configService;

    @InjectMocks
    private PermittedLastNameService permittedLastNameService;

    private PermittedLastName lastName1;
    private PermittedLastName lastName2;
    private PermittedLastName lastName3;
    private PermittedLastNameRequest request;

    @BeforeEach
    void setUp() {
        lastName1 = PermittedLastName.builder()
                .id(UUID.randomUUID())
                .lastName("Smith")
                .category("Common")
                .isActive(true)
                .build();

        lastName2 = PermittedLastName.builder()
                .id(UUID.randomUUID())
                .lastName("Johnson")
                .category("Common")
                .isActive(true)
                .build();

        lastName3 = PermittedLastName.builder()
                .id(UUID.randomUUID())
                .lastName("Anderson")
                .category("Scandinavian")
                .isActive(true)
                .build();

        request = new PermittedLastNameRequest();
        request.setLastName("Williams");
        request.setCategory("Common");
    }

    @Test
    void shouldValidateLastNameWhenValidationEnabled() {
        when(configService.getBooleanValue("validation.lastname.enabled", false))
                .thenReturn(true);
        when(configService.getBooleanValue("validation.lastname.case-sensitive", false))
                .thenReturn(false);
        when(validLastNameRepository.existsActiveByLastNameIgnoreCase("Smith")).thenReturn(true);

        assertDoesNotThrow(() -> permittedLastNameService.validateLastName("Smith"));

        verify(configService).getBooleanValue("validation.lastname.enabled", false);
        verify(validLastNameRepository).existsActiveByLastNameIgnoreCase("Smith");
    }

    @Test
    void shouldNotValidateWhenValidationDisabled() {
        when(configService.getBooleanValue("validation.lastname.enabled", false))
                .thenReturn(false);

        assertDoesNotThrow(() -> permittedLastNameService.validateLastName("AnyLastName"));

        verify(configService).getBooleanValue("validation.lastname.enabled", false);
    }

    @Test
    void shouldThrowExceptionWhenLastNameNotValid() {
        when(configService.getBooleanValue("validation.lastname.enabled", false))
                .thenReturn(true);
        when(configService.getBooleanValue("validation.lastname.case-sensitive", false))
                .thenReturn(false);
        when(validLastNameRepository.existsActiveByLastNameIgnoreCase("InvalidName"))
                .thenReturn(false);

        assertThrows(InvalidLastNameException.class, () -> permittedLastNameService.validateLastName("InvalidName"));

        verify(validLastNameRepository).existsActiveByLastNameIgnoreCase("InvalidName");
    }

    @Test
    void shouldValidateLastNameCaseSensitive() {
        when(configService.getBooleanValue("validation.lastname.enabled", false))
                .thenReturn(true);
        when(configService.getBooleanValue("validation.lastname.case-sensitive", false))
                .thenReturn(true);
        when(validLastNameRepository.existsByLastName("Smith")).thenReturn(true);

        assertDoesNotThrow(() -> permittedLastNameService.validateLastName("Smith"));

        verify(validLastNameRepository).existsByLastName("Smith");
    }

    @Test
    void shouldGetAllActiveLastNames() {
        when(validLastNameRepository.findByIsActiveTrue()).thenReturn(List.of(lastName1, lastName2, lastName3));

        List<PermittedLastName> result = permittedLastNameService.getAllActiveLastNames();

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(validLastNameRepository).findByIsActiveTrue();
    }

    @Test
    void shouldGetAllActiveLastNamesWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PermittedLastName> page = new PageImpl<>(List.of(lastName1, lastName2, lastName3));

        when(validLastNameRepository.findByIsActiveTrue(pageable)).thenReturn(page);

        Page<PermittedLastName> result = permittedLastNameService.getAllActiveLastNames(pageable);

        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        verify(validLastNameRepository).findByIsActiveTrue(pageable);
    }

    @Test
    void shouldGetById() {
        UUID id = lastName1.getId();
        when(validLastNameRepository.findById(id)).thenReturn(Optional.of(lastName1));

        PermittedLastName result = permittedLastNameService.getById(id);

        assertNotNull(result);
        assertEquals("Smith", result.getLastName());
        verify(validLastNameRepository).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenIdNotFound() {
        UUID id = UUID.randomUUID();
        when(validLastNameRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> permittedLastNameService.getById(id));

        verify(validLastNameRepository).findById(id);
    }

    @Test
    void shouldSearchLastNames() {
        String searchTerm = "son";
        when(validLastNameRepository.searchActiveByLastName(searchTerm)).thenReturn(List.of(lastName2, lastName3));

        List<PermittedLastName> result = permittedLastNameService.searchLastNames(searchTerm);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(validLastNameRepository).searchActiveByLastName(searchTerm);
    }

    @Test
    void shouldGetByCategory() {
        String category = "Common";
        when(validLastNameRepository.findByCategoryAndIsActiveTrue(category)).thenReturn(List.of(lastName1, lastName2));

        List<PermittedLastName> result = permittedLastNameService.getByCategory(category);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(validLastNameRepository).findByCategoryAndIsActiveTrue(category);
    }

    @Test
    void shouldGetAllCategories() {
        List<String> categories = List.of("Common", "Scandinavian", "Irish");
        when(validLastNameRepository.findDistinctCategories()).thenReturn(categories);

        List<String> result = permittedLastNameService.getAllCategories();

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(validLastNameRepository).findDistinctCategories();
    }

    @Test
    void shouldCreateValidLastName() {
        when(validLastNameRepository.existsByLastNameIgnoreCase(request.getLastName()))
                .thenReturn(false);
        when(validLastNameRepository.save(any(PermittedLastName.class))).thenReturn(lastName1);

        PermittedLastName result = permittedLastNameService.createValidLastName(request);

        assertNotNull(result);
        verify(validLastNameRepository).existsByLastNameIgnoreCase(request.getLastName());
        verify(validLastNameRepository).save(any(PermittedLastName.class));
    }

    @Test
    void shouldThrowExceptionWhenLastNameAlreadyExists() {
        when(validLastNameRepository.existsByLastNameIgnoreCase(request.getLastName()))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> permittedLastNameService.createValidLastName(request));

        verify(validLastNameRepository).existsByLastNameIgnoreCase(request.getLastName());
    }

    @Test
    void shouldCreateValidLastNameWithAliases() {
        request.setAliases(List.of("Will", "Willie"));
        when(validLastNameRepository.existsByLastNameIgnoreCase(request.getLastName()))
                .thenReturn(false);
        when(validLastNameRepository.save(any(PermittedLastName.class))).thenAnswer(invocation -> {
            PermittedLastName saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        PermittedLastName result = permittedLastNameService.createValidLastName(request);

        assertNotNull(result);
        assertEquals(2, result.getAliases().size());
        verify(validLastNameRepository).save(any(PermittedLastName.class));
    }

    @Test
    void shouldCreateValidLastNameWithEmptyAliases() {
        request.setAliases(List.of());
        when(validLastNameRepository.existsByLastNameIgnoreCase(request.getLastName()))
                .thenReturn(false);
        when(validLastNameRepository.save(any(PermittedLastName.class))).thenReturn(lastName1);

        PermittedLastName result = permittedLastNameService.createValidLastName(request);

        assertNotNull(result);
        verify(validLastNameRepository).save(any(PermittedLastName.class));
    }

    @Test
    void shouldUpdateValidLastNameWithAliases() {
        UUID id = lastName1.getId();
        request.setAliases(List.of("Smithy", "Smitty"));
        when(validLastNameRepository.findById(id)).thenReturn(Optional.of(lastName1));
        when(validLastNameRepository.save(any(PermittedLastName.class)))
                .thenAnswer(invocation -> invocation.<PermittedLastName>getArgument(0));

        PermittedLastName result = permittedLastNameService.updateValidLastName(id, request);

        assertNotNull(result);
        assertEquals(2, result.getAliases().size());
        verify(validLastNameRepository).save(any(PermittedLastName.class));
    }

    @Test
    void shouldSearchLastNamesByAlias() {
        String searchTerm = "Jon";
        when(validLastNameRepository.searchActiveByLastName(searchTerm)).thenReturn(List.of(lastName2));

        List<PermittedLastName> result = permittedLastNameService.searchLastNames(searchTerm);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(validLastNameRepository).searchActiveByLastName(searchTerm);
    }
}
