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
import org.springframework.core.env.Environment;

import com.github.phanikb.rootbytes.entity.SystemConfig;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.repository.SystemConfigRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemConfigServiceTest {

    @Mock
    private SystemConfigRepository systemConfigRepository;

    @Mock
    private Environment environment; // TODO: check config fallback

    @Mock
    private SystemConfigCacheDelegate cacheDelegate;

    @InjectMocks
    private SystemConfigService systemConfigService;

    private SystemConfig testConfig;
    private static final String TEST_KEY = "favorites.max-per-user";
    private static final String TEST_VALUE = "50";
    private static final String TEST_DESCRIPTION = "Maximum favorites allowed per user";

    @BeforeEach
    void setUp() {
        testConfig = SystemConfig.builder()
                .keyName(TEST_KEY)
                .keyValue(TEST_VALUE)
                .description(TEST_DESCRIPTION)
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void shouldGetValueFromDatabase() {
        when(cacheDelegate.getValue(TEST_KEY)).thenReturn(Optional.of(TEST_VALUE));

        Optional<String> result = systemConfigService.getValue(TEST_KEY);

        assertTrue(result.isPresent());
        assertEquals(TEST_VALUE, result.get());
        verify(cacheDelegate).getValue(TEST_KEY);
    }

    @Test
    void shouldFallbackToApplicationYml() {
        String ymlValue = "100";
        when(cacheDelegate.getValue(TEST_KEY)).thenReturn(Optional.of(ymlValue));

        Optional<String> result = systemConfigService.getValue(TEST_KEY);

        assertTrue(result.isPresent());
        assertEquals(ymlValue, result.get());
        verify(cacheDelegate).getValue(TEST_KEY);
    }

    @Test
    void shouldReturnEmptyWhenKeyNotFound() {
        when(cacheDelegate.getValue(TEST_KEY)).thenReturn(Optional.empty());

        Optional<String> result = systemConfigService.getValue(TEST_KEY);

        assertTrue(result.isEmpty());
        verify(cacheDelegate).getValue(TEST_KEY);
    }

    @Test
    void shouldGetValueWithDefault() {
        when(cacheDelegate.getValue(TEST_KEY)).thenReturn(Optional.of(TEST_VALUE));

        String result = systemConfigService.getValue(TEST_KEY, "default");

        assertEquals(TEST_VALUE, result);
    }

    @Test
    void shouldReturnDefaultWhenKeyNotFound() {
        String defaultValue = "default-value";
        when(cacheDelegate.getValue(TEST_KEY)).thenReturn(Optional.empty());

        String result = systemConfigService.getValue(TEST_KEY, defaultValue);

        assertEquals(defaultValue, result);
    }

    @Test
    void shouldGetIntValueSuccessfully() {
        when(cacheDelegate.getValue("page.size")).thenReturn(Optional.of("20"));

        Integer result = systemConfigService.getIntValue("page.size", 10);

        assertEquals(20, result);
    }

    @Test
    void shouldReturnDefaultWhenIntParsingFails() {
        when(cacheDelegate.getValue("page.size")).thenReturn(Optional.of("invalid"));

        Integer result = systemConfigService.getIntValue("page.size", 10);

        assertEquals(10, result);
    }

    @Test
    void shouldGetLongValueSuccessfully() {
        when(cacheDelegate.getValue("max.file.size")).thenReturn(Optional.of("5242880"));

        Long result = systemConfigService.getLongValue("max.file.size", 1000000L);

        assertEquals(5242880L, result);
    }

    @Test
    void shouldGetBooleanValueSuccessfully() {
        when(cacheDelegate.getValue("feature.enabled")).thenReturn(Optional.of("true"));

        Boolean result = systemConfigService.getBooleanValue("feature.enabled", false);

        assertEquals(true, result);
    }

    @Test
    void shouldCreateNewConfig() {
        when(cacheDelegate.setValue(eq(TEST_KEY), eq(TEST_VALUE), eq(TEST_DESCRIPTION), any(UserEntity.class)))
                .thenReturn(testConfig);
        UserEntity mockUser =
                UserEntity.builder().id(UUID.randomUUID()).email("admin").build();

        SystemConfig result = systemConfigService.setValue(TEST_KEY, TEST_VALUE, TEST_DESCRIPTION, mockUser);

        assertNotNull(result);
        assertEquals(TEST_KEY, result.getKeyName());
        assertEquals(TEST_VALUE, result.getKeyValue());
        assertEquals(TEST_DESCRIPTION, result.getDescription());
        verify(cacheDelegate).setValue(TEST_KEY, TEST_VALUE, TEST_DESCRIPTION, mockUser);
    }

    @Test
    void shouldUpdateExistingConfig() {
        String newValue = "75";
        SystemConfig updatedConfig = SystemConfig.builder()
                .keyName(TEST_KEY)
                .keyValue(newValue)
                .description(TEST_DESCRIPTION)
                .build();
        UserEntity mockUser =
                UserEntity.builder().id(UUID.randomUUID()).email("admin").build();
        when(cacheDelegate.setValue(TEST_KEY, newValue, TEST_DESCRIPTION, mockUser))
                .thenReturn(updatedConfig);

        SystemConfig result = systemConfigService.setValue(TEST_KEY, newValue, TEST_DESCRIPTION, mockUser);

        assertNotNull(result);
        assertEquals(TEST_KEY, result.getKeyName());
        assertEquals(newValue, result.getKeyValue());
        assertEquals(TEST_DESCRIPTION, result.getDescription());
        verify(cacheDelegate).setValue(TEST_KEY, newValue, TEST_DESCRIPTION, mockUser);
    }

    @Test
    void shouldSetValueWithoutDescription() {
        SystemConfig emptyDescConfig = SystemConfig.builder()
                .keyName(TEST_KEY)
                .keyValue(TEST_VALUE)
                .description("")
                .updatedAt(Instant.now())
                .build();
        when(cacheDelegate.setValue(eq(TEST_KEY), eq(TEST_VALUE), eq(""), any(UserEntity.class)))
                .thenReturn(emptyDescConfig);
        UserEntity mockUser =
                UserEntity.builder().id(UUID.randomUUID()).email("admin").build();

        SystemConfig result = systemConfigService.setValue(TEST_KEY, TEST_VALUE, "", mockUser);

        assertNotNull(result);
        assertEquals(TEST_KEY, result.getKeyName());
        assertEquals(TEST_VALUE, result.getKeyValue());
        assertEquals("", result.getDescription());
        verify(cacheDelegate).setValue(TEST_KEY, TEST_VALUE, "", mockUser);
    }

    @Test
    void shouldDeleteConfig() {
        systemConfigService.deleteValue(TEST_KEY);

        verify(cacheDelegate).deleteValue(TEST_KEY);
    }

    @Test
    void shouldGetAllConfigs() {
        SystemConfig config1 =
                SystemConfig.builder().keyName("key1").keyValue("value1").build();
        SystemConfig config2 =
                SystemConfig.builder().keyName("key2").keyValue("value2").build();
        List<SystemConfig> configs = List.of(config1, config2);

        when(systemConfigRepository.findAll()).thenReturn(configs);

        List<SystemConfig> result = systemConfigService.getAllConfigs();

        assertEquals(2, result.size());
        assertEquals(List.of(config1, config2), result);
        verify(systemConfigRepository).findAll();
    }

    @Test
    void shouldGetConfigEntity() {
        when(systemConfigRepository.findByKeyName(TEST_KEY)).thenReturn(Optional.of(testConfig));

        Optional<SystemConfig> result = systemConfigService.getConfig(TEST_KEY);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(testConfig, result.get());
        verify(systemConfigRepository).findByKeyName(TEST_KEY);
    }

    @Test
    void shouldCheckIfConfigExists() {
        when(systemConfigRepository.existsByKeyName(TEST_KEY)).thenReturn(true);

        boolean result = systemConfigService.exists(TEST_KEY);

        assertTrue(result);
        verify(systemConfigRepository).existsByKeyName(TEST_KEY);
    }

    @Test
    void shouldReturnFalseWhenConfigNotExists() {
        when(systemConfigRepository.existsByKeyName(TEST_KEY)).thenReturn(false);

        boolean result = systemConfigService.exists(TEST_KEY);

        assertFalse(result);
        verify(systemConfigRepository).existsByKeyName(TEST_KEY);
    }
}
