/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.entity.SystemConfig;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.repository.SystemConfigRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SystemConfigService {

    private final SystemConfigCacheDelegate cacheDelegate;
    private final SystemConfigRepository systemConfigRepository;

    public Optional<String> getValue(String key) {
        return cacheDelegate.getValue(key);
    }

    public String getValue(String key, String defaultValue) {
        return getValue(key).orElse(defaultValue);
    }

    public Integer getIntValue(String key, Integer defaultValue) {
        Integer result = defaultValue;
        try {
            result = getValue(key).map(Integer::parseInt).orElse(defaultValue);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse integer value for key: {}, returning default", key);
        }
        return result;
    }

    public Double getDoubleValue(String key, Double defaultValue) {
        Double result = defaultValue;
        try {
            result = getValue(key).map(Double::parseDouble).orElse(defaultValue);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse double value for key: {}, returning default", key);
        }
        return result;
    }

    public Long getLongValue(String key, Long defaultValue) {
        Long result = defaultValue;
        try {
            result = getValue(key).map(Long::parseLong).orElse(defaultValue);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse long value for key: {}, returning default", key);
        }
        return result;
    }

    public Boolean getBooleanValue(String key, Boolean defaultValue) {
        return getValue(key).map(Boolean::parseBoolean).orElse(defaultValue);
    }

    @Transactional
    public SystemConfig setValue(String key, String value, String description, UserEntity updatedBy) {
        return cacheDelegate.setValue(key, value, description, updatedBy);
    }

    @Transactional
    public void deleteValue(String key) {
        cacheDelegate.deleteValue(key);
    }

    public List<SystemConfig> getAllConfigs() {
        log.debug("Getting all configuration entries");
        return systemConfigRepository.findAll();
    }

    public Optional<SystemConfig> getConfig(String key) {
        return systemConfigRepository.findByKeyName(key);
    }

    public boolean exists(String key) {
        return systemConfigRepository.existsByKeyName(key);
    }
}
