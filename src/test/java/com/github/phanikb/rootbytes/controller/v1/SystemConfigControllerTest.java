/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.phanikb.rootbytes.dto.v1.request.SystemConfigRequest;
import com.github.phanikb.rootbytes.dto.v1.response.SystemConfigResponse;
import com.github.phanikb.rootbytes.entity.SystemConfig;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.UserRole;
import com.github.phanikb.rootbytes.enums.UserStatus;
import com.github.phanikb.rootbytes.mapper.SystemConfigMapper;
import com.github.phanikb.rootbytes.security.RbUserDetails;
import com.github.phanikb.rootbytes.service.SystemConfigService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemConfigControllerTest {

    @Mock
    private SystemConfigService systemConfigService;

    @Mock
    private SystemConfigMapper systemConfigMapper;

    private SystemConfigController controller;

    private SystemConfig config;
    private SystemConfigResponse response;

    @BeforeEach
    void setUp() {
        controller = new SystemConfigController(systemConfigService, systemConfigMapper);

        UserEntity testUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("admin@test.com")
                .lastName("Admin")
                .build();

        config = SystemConfig.builder()
                .keyName("test.key")
                .keyValue("test-value")
                .description("Test configuration")
                .updatedBy(testUser)
                .build();

        response = SystemConfigResponse.builder()
                .keyName("test.key")
                .keyValue("test-value")
                .description("Test configuration")
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void shouldGetAllConfigs() {
        List<SystemConfig> configs = Collections.singletonList(config);
        when(systemConfigService.getAllConfigs()).thenReturn(configs);
        when(systemConfigMapper.toResponse(config)).thenReturn(response);

        var result = controller.getAllConfigs();

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(true, result.getBody().getSuccess());
        assertEquals(1, result.getBody().getData().size());
        assertEquals("test.key", result.getBody().getData().getFirst().getKeyName());

        verify(systemConfigService).getAllConfigs();
    }

    @Test
    void shouldGetConfigByKey() {
        when(systemConfigService.getConfig("test.key")).thenReturn(Optional.of(config));
        when(systemConfigMapper.toResponse(config)).thenReturn(response);

        var result = controller.getConfig("test.key");

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(true, result.getBody().getSuccess());
        assertEquals("test.key", result.getBody().getData().getKeyName());

        verify(systemConfigService).getConfig("test.key");
    }

    @Test
    void shouldReturnNotFoundWhenConfigDoesNotExist() {
        when(systemConfigService.getConfig("nonexistent.key")).thenReturn(Optional.empty());

        var result = controller.getConfig("nonexistent.key");

        assertEquals(404, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(false, result.getBody().getSuccess());
        assertEquals(
                SystemConfigController.KEY_CONFIG_NOT_FOUND + "nonexistent.key",
                result.getBody().getMessage());

        verify(systemConfigService).getConfig("nonexistent.key");
    }

    @Test
    void shouldGetConfigValue() {
        when(systemConfigService.getValue("test.key")).thenReturn(Optional.of("test-value"));

        var result = controller.getConfigValue("test.key");

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(true, result.getBody().getSuccess());
        assertEquals("test-value", result.getBody().getData());

        verify(systemConfigService).getValue("test.key");
    }

    @Test
    void shouldReturnNotFoundWhenConfigValueDoesNotExist() {
        when(systemConfigService.getValue("nonexistent.key")).thenReturn(Optional.empty());

        var result = controller.getConfigValue("nonexistent.key");

        assertEquals(404, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(false, result.getBody().getSuccess());
        assertEquals(
                SystemConfigController.KEY_CONFIG_NOT_FOUND + "nonexistent.key",
                result.getBody().getMessage());

        verify(systemConfigService).getValue("nonexistent.key");
    }

    @Test
    void shouldUpdateConfig() {
        SystemConfigRequest request = SystemConfigRequest.builder()
                .keyValue("updated-value")
                .description("Updated description")
                .build();

        when(systemConfigService.setValue(
                        eq("test.key"), eq("updated-value"), eq("Updated description"), any(UserEntity.class)))
                .thenReturn(config);
        when(systemConfigMapper.toResponse(config)).thenReturn(response);

        var admin = createMockAdmin();
        var result = controller.updateConfig("test.key", request, admin);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(true, result.getBody().getSuccess());
        assertEquals("test.key", result.getBody().getData().getKeyName());

        verify(systemConfigService)
                .setValue(eq("test.key"), eq("updated-value"), eq("Updated description"), any(UserEntity.class));
    }

    @Test
    void shouldCreateConfig() {
        SystemConfigRequest request = SystemConfigRequest.builder()
                .keyName("new.key")
                .keyValue("new-value")
                .description("New configuration")
                .build();

        when(systemConfigService.exists("new.key")).thenReturn(false);
        when(systemConfigService.setValue(
                        eq("new.key"), eq("new-value"), eq("New configuration"), any(UserEntity.class)))
                .thenReturn(config);
        when(systemConfigMapper.toResponse(config)).thenReturn(response);

        var admin = createMockAdmin();
        var result = controller.createConfig(request, admin);

        assertEquals(201, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(true, result.getBody().getSuccess());

        verify(systemConfigService).exists("new.key");
        verify(systemConfigService)
                .setValue(eq("new.key"), eq("new-value"), eq("New configuration"), any(UserEntity.class));
    }

    @Test
    void shouldReturnBadRequestWhenKeyNameIsNull() {
        SystemConfigRequest request = SystemConfigRequest.builder()
                .keyValue("value")
                .description("Description")
                .build();

        var admin = createMockAdmin();
        var result = controller.createConfig(request, admin);

        assertEquals(400, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(false, result.getBody().getSuccess());
        assertEquals("Key name is required", result.getBody().getMessage());
    }

    @Test
    void shouldReturnBadRequestWhenKeyNameIsBlank() {
        SystemConfigRequest request = SystemConfigRequest.builder()
                .keyName("   ")
                .keyValue("value")
                .description("Description")
                .build();

        var admin = createMockAdmin();
        var result = controller.createConfig(request, admin);

        assertEquals(400, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(false, result.getBody().getSuccess());
        assertEquals("Key name is required", result.getBody().getMessage());
    }

    @Test
    void shouldReturnBadRequestWhenConfigAlreadyExists() {
        SystemConfigRequest request = SystemConfigRequest.builder()
                .keyName("existing.key")
                .keyValue("value")
                .description("Description")
                .build();

        when(systemConfigService.exists("existing.key")).thenReturn(true);

        var admin = createMockAdmin();
        var result = controller.createConfig(request, admin);

        assertEquals(400, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(false, result.getBody().getSuccess());
        assertEquals(
                "Configuration already exists for key: existing.key",
                result.getBody().getMessage());

        verify(systemConfigService).exists("existing.key");
    }

    @Test
    void shouldDeleteConfig() {
        when(systemConfigService.exists("test.key")).thenReturn(true);

        var result = controller.deleteConfig("test.key");

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(true, result.getBody().getSuccess());
        assertEquals("Configuration deleted successfully", result.getBody().getMessage());

        verify(systemConfigService).exists("test.key");
        verify(systemConfigService).deleteValue("test.key");
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentConfig() {
        when(systemConfigService.exists("nonexistent.key")).thenReturn(false);

        var result = controller.deleteConfig("nonexistent.key");

        assertEquals(404, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(false, result.getBody().getSuccess());
        assertEquals(
                SystemConfigController.KEY_CONFIG_NOT_FOUND + "nonexistent.key",
                result.getBody().getMessage());

        verify(systemConfigService).exists("nonexistent.key");
    }

    private RbUserDetails createMockAdmin() {
        return new RbUserDetails(
                UUID.randomUUID(), "admin@test.com", "password", UserRole.ADMIN, UserStatus.ACTIVE, true);
    }
}
