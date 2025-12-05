/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.github.phanikb.rootbytes.entity.SystemConfig;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.repository.SystemConfigRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemConfigCacheDelegateTest {

    @Mock
    private SystemConfigRepository systemConfigRepository;

    @Mock
    private Environment environment;

    @InjectMocks
    private SystemConfigCacheDelegate cacheDelegate;

    private SystemConfig testConfig;
    private UserEntity testUser;
    private static final String TEST_KEY = "test.config.key";
    private static final String TEST_VALUE = "test-value";
    private static final String TEST_DESCRIPTION = "Test configuration";

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("admin@test.com")
                .lastName("Admin")
                .build();

        testConfig = SystemConfig.builder()
                .keyName(TEST_KEY)
                .keyValue(TEST_VALUE)
                .description(TEST_DESCRIPTION)
                .updatedBy(testUser)
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void shouldGetValueFromDatabase() {
        when(systemConfigRepository.findByKeyName(TEST_KEY)).thenReturn(Optional.of(testConfig));

        Optional<String> result = cacheDelegate.getValue(TEST_KEY);

        assertTrue(result.isPresent());
        assertEquals(TEST_VALUE, result.get());
        verify(systemConfigRepository).findByKeyName(TEST_KEY);
    }

    @Test
    void shouldFallbackToEnvironment() {
        when(systemConfigRepository.findByKeyName(TEST_KEY)).thenReturn(Optional.empty());
        when(environment.getProperty(TEST_KEY)).thenReturn("env-value");

        Optional<String> result = cacheDelegate.getValue(TEST_KEY);

        assertTrue(result.isPresent());
        assertEquals("env-value", result.get());
        verify(systemConfigRepository).findByKeyName(TEST_KEY);
        verify(environment).getProperty(TEST_KEY);
    }

    @Test
    void shouldReturnEmptyWhenKeyNotFound() {
        when(systemConfigRepository.findByKeyName(TEST_KEY)).thenReturn(Optional.empty());
        when(environment.getProperty(TEST_KEY)).thenReturn(null);

        Optional<String> result = cacheDelegate.getValue(TEST_KEY);

        assertFalse(result.isPresent());
        verify(systemConfigRepository).findByKeyName(TEST_KEY);
        verify(environment).getProperty(TEST_KEY);
    }

    @Test
    void shouldCreateNewConfig() {
        when(systemConfigRepository.findByKeyName(TEST_KEY)).thenReturn(Optional.empty());
        when(systemConfigRepository.save(any(SystemConfig.class)))
                .thenAnswer(invocation -> invocation.<SystemConfig>getArgument(0));

        SystemConfig result = cacheDelegate.setValue(TEST_KEY, TEST_VALUE, TEST_DESCRIPTION, testUser);

        assertNotNull(result);
        assertEquals(TEST_KEY, result.getKeyName());
        assertEquals(TEST_VALUE, result.getKeyValue());
        assertEquals(TEST_DESCRIPTION, result.getDescription());
        assertEquals(testUser, result.getUpdatedBy());
        assertNotNull(result.getUpdatedAt());
        verify(systemConfigRepository).findByKeyName(TEST_KEY);
        verify(systemConfigRepository).save(any(SystemConfig.class));
    }

    @Test
    void shouldUpdateExistingConfig() {
        SystemConfig existingConfig = SystemConfig.builder()
                .keyName(TEST_KEY)
                .keyValue("old-value")
                .description("Old description")
                .updatedAt(Instant.now().minusSeconds(3600))
                .build();

        when(systemConfigRepository.findByKeyName(TEST_KEY)).thenReturn(Optional.of(existingConfig));
        when(systemConfigRepository.save(any(SystemConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SystemConfig result = cacheDelegate.setValue(TEST_KEY, "new-value", "New description", testUser);

        assertNotNull(result);
        assertEquals(TEST_KEY, result.getKeyName());
        assertEquals("new-value", result.getKeyValue());
        assertEquals("New description", result.getDescription());
        assertEquals(testUser, result.getUpdatedBy());
        assertNotNull(result.getUpdatedAt());
        verify(systemConfigRepository).findByKeyName(TEST_KEY);
        verify(systemConfigRepository).save(any(SystemConfig.class));
    }

    @Test
    void shouldSetValueWithoutUpdatingDescription() {
        SystemConfig existingConfig = SystemConfig.builder()
                .keyName(TEST_KEY)
                .keyValue("old-value")
                .description("Original description")
                .updatedAt(Instant.now().minusSeconds(3600))
                .build();

        when(systemConfigRepository.findByKeyName(TEST_KEY)).thenReturn(Optional.of(existingConfig));
        when(systemConfigRepository.save(any(SystemConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SystemConfig result = cacheDelegate.setValue(TEST_KEY, "new-value", null, testUser);

        assertNotNull(result);
        assertEquals(TEST_KEY, result.getKeyName());
        assertEquals("new-value", result.getKeyValue());
        assertEquals("Original description", result.getDescription());
        assertEquals(testUser, result.getUpdatedBy());
        verify(systemConfigRepository).save(any(SystemConfig.class));
    }

    @Test
    void shouldDeleteValue() {
        cacheDelegate.deleteValue(TEST_KEY);

        verify(systemConfigRepository).deleteById(TEST_KEY);
    }

    @Test
    void shouldHandleMultipleGetValueCalls() {
        when(systemConfigRepository.findByKeyName(TEST_KEY)).thenReturn(Optional.of(testConfig));

        Optional<String> result1 = cacheDelegate.getValue(TEST_KEY);
        Optional<String> result2 = cacheDelegate.getValue(TEST_KEY);

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(TEST_VALUE, result1.get());
        assertEquals(TEST_VALUE, result2.get());
    }

    @Test
    void shouldHandleEmptyStringValues() {
        when(systemConfigRepository.findByKeyName(TEST_KEY)).thenReturn(Optional.empty());
        when(systemConfigRepository.save(any(SystemConfig.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SystemConfig result = cacheDelegate.setValue(TEST_KEY, "", "Empty value config", testUser);

        assertNotNull(result);
        assertEquals("", result.getKeyValue());
        verify(systemConfigRepository).save(any(SystemConfig.class));
    }
}
