/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.github.phanikb.rootbytes.common.Constants;
import com.github.phanikb.rootbytes.dto.v1.request.SystemConfigRequest;
import com.github.phanikb.rootbytes.dto.v1.response.RbApiResponse;
import com.github.phanikb.rootbytes.dto.v1.response.SystemConfigResponse;
import com.github.phanikb.rootbytes.entity.SystemConfig;
import com.github.phanikb.rootbytes.security.RbCurrentUser;
import com.github.phanikb.rootbytes.security.RbUserDetails;
import com.github.phanikb.rootbytes.service.SystemConfigService;

@RestController
@RequestMapping(Constants.API_V1 + "/rbconfig")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;
    private final com.github.phanikb.rootbytes.mapper.SystemConfigMapper systemConfigMapper;
    public static final String KEY_CONFIG_NOT_FOUND = "Configuration not found for key: ";

    @GetMapping
    @PreAuthorize(Constants.ADMIN_ROLE)
    public ResponseEntity<RbApiResponse<List<SystemConfigResponse>>> getAllConfigs() {
        List<SystemConfig> configs = systemConfigService.getAllConfigs();
        return ResponseEntity.ok(RbApiResponse.success(
                configs.stream().map(systemConfigMapper::toResponse).toList()));
    }

    @GetMapping("/{key}")
    @PreAuthorize(Constants.ADMIN_ROLE)
    public ResponseEntity<RbApiResponse<SystemConfigResponse>> getConfig(@PathVariable String key) {
        return systemConfigService
                .getConfig(key)
                .map(config -> ResponseEntity.ok(RbApiResponse.success(systemConfigMapper.toResponse(config))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(RbApiResponse.error(KEY_CONFIG_NOT_FOUND + key)));
    }

    @GetMapping("/{key}/value")
    @PreAuthorize(Constants.ADMIN_ROLE)
    public ResponseEntity<RbApiResponse<String>> getConfigValue(@PathVariable String key) {
        Optional<String> value = systemConfigService.getValue(key);
        return value.map(s -> ResponseEntity.ok(RbApiResponse.success(null, s)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(RbApiResponse.error(KEY_CONFIG_NOT_FOUND + key)));
    }

    @PutMapping("/{key}")
    @PreAuthorize(Constants.ADMIN_ROLE)
    public ResponseEntity<RbApiResponse<SystemConfigResponse>> updateConfig(
            @PathVariable String key,
            @Valid @RequestBody SystemConfigRequest request,
            @RbCurrentUser RbUserDetails currentUser) {
        SystemConfig config = systemConfigService.setValue(
                key, request.getKeyValue(), request.getDescription(), currentUser.toUserEntity());
        return ResponseEntity.ok(RbApiResponse.success(systemConfigMapper.toResponse(config)));
    }

    @PostMapping
    @PreAuthorize(Constants.ADMIN_ROLE)
    public ResponseEntity<RbApiResponse<SystemConfigResponse>> createConfig(
            @Valid @RequestBody SystemConfigRequest request, @RbCurrentUser RbUserDetails currentUser) {

        if (request.getKeyName() == null || request.getKeyName().isBlank()) {
            return ResponseEntity.badRequest().body(RbApiResponse.error("Key name is required"));
        }

        if (systemConfigService.exists(request.getKeyName())) {
            return ResponseEntity.badRequest()
                    .body(RbApiResponse.error("Configuration already exists for key: " + request.getKeyName()));
        }

        SystemConfig config = systemConfigService.setValue(
                request.getKeyName(), request.getKeyValue(), request.getDescription(), currentUser.toUserEntity());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RbApiResponse.success(systemConfigMapper.toResponse(config)));
    }

    @DeleteMapping("/{key}")
    @PreAuthorize(Constants.ADMIN_ROLE)
    public ResponseEntity<RbApiResponse<Void>> deleteConfig(@PathVariable String key) {
        if (!systemConfigService.exists(key)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(RbApiResponse.error(KEY_CONFIG_NOT_FOUND + key));
        }

        systemConfigService.deleteValue(key);
        return ResponseEntity.ok(RbApiResponse.success("Configuration deleted successfully"));
    }
}
