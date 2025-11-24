/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.exception.ResourceNotFoundException;
import com.github.phanikb.rootbytes.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    @Transactional
    public UserEntity updatePublicName(String email, String publicName) {
        log.info("Updating public name for user: {}", email);

        UserEntity user =
                userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // allow clearing the public name by setting to null
        if (publicName == null || publicName.trim().isEmpty()) {
            user.setPublicName(null);
            log.info("Cleared public name for user: {}", email);
        } else {
            user.setPublicName(publicName.trim());
            log.info("Updated public name for user {} to: {}", email, publicName);
        }

        return userRepository.save(user);
    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserEntity getUserByUniqueName(String uniqueName) {
        return userRepository
                .findByUniqueName(uniqueName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
