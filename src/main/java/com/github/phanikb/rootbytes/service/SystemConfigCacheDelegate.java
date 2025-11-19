/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.entity.SystemConfig;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.repository.SystemConfigRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class SystemConfigCacheDelegate {

    private final SystemConfigRepository systemConfigRepository;
    private final Environment environment;

    @Cacheable(value = "systemConfig", key = "#key")
    @Transactional(readOnly = true)
    public Optional<String> getValue(String key) {
        log.debug("Getting configuration value for key: {}", key);

        Optional<String> result = Optional.empty();
        Optional<SystemConfig> config = systemConfigRepository.findByKeyName(key);

        if (config.isPresent()) {
            result = Optional.of(config.get().getKeyValue());
        } else {
            String propertyValue = environment.getProperty(key);
            if (propertyValue != null) {
                result = Optional.of(propertyValue);
            }
        }

        return result;
    }

    @Transactional
    @CacheEvict(value = "systemConfig", key = "#key")
    public SystemConfig setValue(String key, String value, String description, UserEntity updatedBy) {
        log.info("Setting configuration value and evicting cache for key: {}", key);

        SystemConfig config = systemConfigRepository
                .findByKeyName(key)
                .orElse(SystemConfig.builder().keyName(key).build());

        config.setKeyValue(value);
        if (description != null) {
            config.setDescription(description);
        }
        config.setUpdatedBy(updatedBy);
        config.setUpdatedAt(Instant.now());

        return systemConfigRepository.save(config);
    }

    @Transactional
    @CacheEvict(value = "systemConfig", key = "#key")
    public void deleteValue(String key) {
        log.info("Deleting configuration value and evicting cache for key: {}", key);
        systemConfigRepository.deleteById(key);
    }
}
