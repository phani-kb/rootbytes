/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.github.phanikb.rootbytes.dto.v1.request.PermittedLastNameRequest;
import com.github.phanikb.rootbytes.entity.PermittedLastName;
import com.github.phanikb.rootbytes.exception.DuplicateResourceException;
import com.github.phanikb.rootbytes.exception.InvalidLastNameException;
import com.github.phanikb.rootbytes.exception.ResourceNotFoundException;
import com.github.phanikb.rootbytes.repository.PermittedLastNameRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermittedLastNameServiceTest {

    @Mock
    private PermittedLastNameRepository validLastNameRepository;

    @Mock
    private SystemConfigService configService;

    @InjectMocks
    private PermittedLastNameService service;

    private PermittedLastName smith;
    private PermittedLastName kumar;
    private PermittedLastNameRequest request;

    @BeforeEach
    void setUp() {
        smith = PermittedLastName.builder()
                .id(UUID.randomUUID())
                .lastName("Smith")
                .category("Common")
                .isActive(true)
                .aliases(new ArrayList<>())
                .build();

        kumar = PermittedLastName.builder()
                .id(UUID.randomUUID())
                .lastName("Kumar")
                .category("Indian")
                .isActive(true)
                .aliases(new ArrayList<>())
                .build();

        request = new PermittedLastNameRequest();
        request.setLastName("Williams");
        request.setCategory("Common");
        request.setDescription("Common English surname");
    }

    @Nested
    class ValidationTests {

        @Test
        void shouldValidateLastNameWhenEnabled() {
            when(configService.getBooleanValue("validation.lastname.enabled", false))
                    .thenReturn(true);
            when(configService.getBooleanValue("validation.lastname.case-sensitive", false))
                    .thenReturn(false);
            when(validLastNameRepository.existsActiveByLastNameIgnoreCase("Kumar"))
                    .thenReturn(true);

            assertDoesNotThrow(() -> service.validateLastName("Kumar"));

            verify(validLastNameRepository).existsActiveByLastNameIgnoreCase("Kumar");
        }

        @Test
        void shouldSkipValidationWhenDisabled() {
            when(configService.getBooleanValue("validation.lastname.enabled", false))
                    .thenReturn(false);

            assertDoesNotThrow(() -> service.validateLastName("AnyName"));

            verify(validLastNameRepository, never()).existsActiveByLastNameIgnoreCase(any());
            verify(validLastNameRepository, never()).existsByLastName(any());
        }

        @Test
        void shouldThrowWhenLastNameInvalid() {
            when(configService.getBooleanValue("validation.lastname.enabled", false))
                    .thenReturn(true);
            when(configService.getBooleanValue("validation.lastname.case-sensitive", false))
                    .thenReturn(false);
            when(validLastNameRepository.existsActiveByLastNameIgnoreCase("InvalidName"))
                    .thenReturn(false);

            assertThrows(InvalidLastNameException.class, () -> service.validateLastName("InvalidName"));
        }

        @Test
        void shouldValidateCaseSensitively() {
            when(configService.getBooleanValue("validation.lastname.enabled", false))
                    .thenReturn(true);
            when(configService.getBooleanValue("validation.lastname.case-sensitive", false))
                    .thenReturn(true);
            when(validLastNameRepository.existsByLastName("Kumar")).thenReturn(true);

            assertDoesNotThrow(() -> service.validateLastName("Kumar"));

            verify(validLastNameRepository).existsByLastName("Kumar");
            verify(validLastNameRepository, never()).existsActiveByLastNameIgnoreCase(any());
        }

        @Test
        void shouldReturnValidationEnabledStatus() {
            when(configService.getBooleanValue("validation.lastname.enabled", false))
                    .thenReturn(true);
            assertTrue(service.isValidationEnabled());

            when(configService.getBooleanValue("validation.lastname.enabled", false))
                    .thenReturn(false);
            assertFalse(service.isValidationEnabled());
        }
    }

    @Nested
    class QueryTests {

        @Test
        void shouldGetAllActiveLastNames() {
            when(validLastNameRepository.findByIsActiveTrue()).thenReturn(List.of(smith, kumar));

            List<PermittedLastName> result = service.getAllActiveLastNames();

            assertEquals(2, result.size());
            verify(validLastNameRepository).findByIsActiveTrue();
        }

        @Test
        void shouldGetAllActiveLastNamesWithPagination() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<PermittedLastName> page = new PageImpl<>(List.of(smith, kumar), pageable, 2);
            when(validLastNameRepository.findByIsActiveTrue(pageable)).thenReturn(page);

            Page<PermittedLastName> result = service.getAllActiveLastNames(pageable);

            assertEquals(2, result.getTotalElements());
            verify(validLastNameRepository).findByIsActiveTrue(pageable);
        }

        @Test
        void shouldGetById() {
            UUID id = kumar.getId();
            when(validLastNameRepository.findById(id)).thenReturn(Optional.of(kumar));

            PermittedLastName result = service.getById(id);

            assertEquals("Kumar", result.getLastName());
            assertEquals("Indian", result.getCategory());
        }

        @Test
        void shouldThrowWhenIdNotFound() {
            UUID id = UUID.randomUUID();
            when(validLastNameRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.getById(id));
        }

        @Test
        void shouldSearchLastNames() {
            when(validLastNameRepository.searchActiveByLastName("mar")).thenReturn(List.of(kumar));

            List<PermittedLastName> result = service.searchLastNames("mar");

            assertEquals(1, result.size());
            assertEquals("Kumar", result.getFirst().getLastName());
        }

        @Test
        void shouldGetByCategory() {
            when(validLastNameRepository.findByCategoryAndIsActiveTrue("Indian"))
                    .thenReturn(List.of(kumar));

            List<PermittedLastName> result = service.getByCategory("Indian");

            assertEquals(1, result.size());
            assertEquals("Kumar", result.getFirst().getLastName());
        }

        @Test
        void shouldGetAllCategories() {
            when(validLastNameRepository.findDistinctCategories()).thenReturn(List.of("Common", "Indian"));

            List<String> result = service.getAllCategories();

            assertEquals(2, result.size());
            assertTrue(result.contains("Indian"));
        }
    }

    @Nested
    class CreateTests {

        @Test
        void shouldCreateLastName() {
            when(validLastNameRepository.existsByLastNameIgnoreCase("Williams")).thenReturn(false);
            when(validLastNameRepository.save(any(PermittedLastName.class))).thenAnswer(inv -> {
                PermittedLastName saved = inv.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });

            PermittedLastName result = service.createValidLastName(request);

            assertNotNull(result);
            verify(validLastNameRepository).save(any(PermittedLastName.class));
        }

        @Test
        void shouldThrowWhenDuplicateLastName() {
            when(validLastNameRepository.existsByLastNameIgnoreCase("Williams")).thenReturn(true);

            assertThrows(DuplicateResourceException.class, () -> service.createValidLastName(request));

            verify(validLastNameRepository, never()).save(any());
        }

        @Test
        void shouldCreateWithAliases() {
            request.setAliases(List.of("Kumari", "Kumaran"));
            when(validLastNameRepository.existsByLastNameIgnoreCase("Williams")).thenReturn(false);
            when(validLastNameRepository.save(any(PermittedLastName.class))).thenAnswer(inv -> {
                PermittedLastName saved = inv.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });

            PermittedLastName result = service.createValidLastName(request);

            assertEquals(2, result.getAliases().size());
        }

        @Test
        void shouldCreateWithEmptyAliases() {
            request.setAliases(List.of());
            when(validLastNameRepository.existsByLastNameIgnoreCase("Williams")).thenReturn(false);
            when(validLastNameRepository.save(any(PermittedLastName.class))).thenReturn(smith);

            PermittedLastName result = service.createValidLastName(request);

            assertNotNull(result);
        }

        @Test
        void shouldCreateWithNullAliases() {
            request.setAliases(null);
            when(validLastNameRepository.existsByLastNameIgnoreCase("Williams")).thenReturn(false);
            when(validLastNameRepository.save(any(PermittedLastName.class))).thenReturn(smith);

            PermittedLastName result = service.createValidLastName(request);

            assertNotNull(result);
        }

        @Test
        void shouldFilterBlankAliases() {
            request.setAliases(List.of("Kumari", "", "  ", "Kumaran"));
            when(validLastNameRepository.existsByLastNameIgnoreCase("Williams")).thenReturn(false);
            when(validLastNameRepository.save(any(PermittedLastName.class))).thenAnswer(inv -> {
                PermittedLastName saved = inv.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });

            PermittedLastName result = service.createValidLastName(request);

            assertEquals(2, result.getAliases().size());
        }

        @Test
        void shouldDeduplicateAliases() {
            request.setAliases(List.of("Kumari", "Kumari", "Kumaran"));
            when(validLastNameRepository.existsByLastNameIgnoreCase("Williams")).thenReturn(false);
            when(validLastNameRepository.save(any(PermittedLastName.class))).thenAnswer(inv -> {
                PermittedLastName saved = inv.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });

            PermittedLastName result = service.createValidLastName(request);

            assertEquals(2, result.getAliases().size());
        }
    }

    @Nested
    class UpdateTests {

        @Test
        void shouldUpdateLastName() {
            UUID id = kumar.getId();
            request.setLastName("Kumar");
            when(validLastNameRepository.findById(id)).thenReturn(Optional.of(kumar));
            when(validLastNameRepository.save(any(PermittedLastName.class))).thenAnswer(inv -> inv.getArgument(0));

            PermittedLastName result = service.updateValidLastName(id, request);

            assertNotNull(result);
            verify(validLastNameRepository).save(any(PermittedLastName.class));
        }

        @Test
        void shouldThrowWhenUpdatingToDuplicateName() {
            UUID id = kumar.getId();
            request.setLastName("Smith");
            when(validLastNameRepository.findById(id)).thenReturn(Optional.of(kumar));
            when(validLastNameRepository.existsByLastNameIgnoreCase("Smith")).thenReturn(true);

            assertThrows(DuplicateResourceException.class, () -> service.updateValidLastName(id, request));

            verify(validLastNameRepository, never()).save(any());
        }

        @Test
        void shouldUpdateWithAliases() {
            UUID id = kumar.getId();
            request.setLastName("Kumar");
            request.setAliases(List.of("Kumari", "Kumaran"));
            when(validLastNameRepository.findById(id)).thenReturn(Optional.of(kumar));
            when(validLastNameRepository.save(any(PermittedLastName.class))).thenAnswer(inv -> inv.getArgument(0));

            PermittedLastName result = service.updateValidLastName(id, request);

            assertEquals(2, result.getAliases().size());
        }

        @Test
        void shouldAllowUpdateToSameNameCaseInsensitive() {
            UUID id = kumar.getId();
            request.setLastName("KUMAR");
            when(validLastNameRepository.findById(id)).thenReturn(Optional.of(kumar));
            when(validLastNameRepository.save(any(PermittedLastName.class))).thenAnswer(inv -> inv.getArgument(0));

            PermittedLastName result = service.updateValidLastName(id, request);

            assertNotNull(result);
            verify(validLastNameRepository, never()).existsByLastNameIgnoreCase(any());
        }
    }

    @Nested
    class ActivationTests {

        @Test
        void shouldDeactivateLastName() {
            UUID id = kumar.getId();
            when(validLastNameRepository.findById(id)).thenReturn(Optional.of(kumar));
            when(validLastNameRepository.save(any(PermittedLastName.class))).thenAnswer(inv -> inv.getArgument(0));

            service.deactivateLastName(id);

            assertFalse(kumar.getIsActive());
            verify(validLastNameRepository).save(kumar);
        }

        @Test
        void shouldActivateLastName() {
            UUID id = kumar.getId();
            kumar.setIsActive(false);
            when(validLastNameRepository.findById(id)).thenReturn(Optional.of(kumar));
            when(validLastNameRepository.save(any(PermittedLastName.class))).thenAnswer(inv -> inv.getArgument(0));

            service.activateLastName(id);

            assertTrue(kumar.getIsActive());
            verify(validLastNameRepository).save(kumar);
        }

        @Test
        void shouldDeleteLastName() {
            UUID id = kumar.getId();
            when(validLastNameRepository.findById(id)).thenReturn(Optional.of(kumar));

            service.deleteLastName(id);

            verify(validLastNameRepository).delete(kumar);
        }
    }
}
