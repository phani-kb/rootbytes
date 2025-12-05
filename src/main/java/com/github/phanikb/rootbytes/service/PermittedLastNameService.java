/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.dto.v1.request.PermittedLastNameRequest;
import com.github.phanikb.rootbytes.entity.LastNameAlias;
import com.github.phanikb.rootbytes.entity.PermittedLastName;
import com.github.phanikb.rootbytes.exception.DuplicateResourceException;
import com.github.phanikb.rootbytes.exception.InvalidLastNameException;
import com.github.phanikb.rootbytes.exception.ResourceNotFoundException;
import com.github.phanikb.rootbytes.repository.PermittedLastNameRepository;
import com.github.phanikb.rootbytes.util.RbStringUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermittedLastNameService {

    private static final String PERMITTEDLASTNAME_ENITY = "PermittedLastName";

    private final PermittedLastNameRepository validLastNameRepository;
    private final SystemConfigService configService;

    /**
     * Validates if a last name is in the valid list
     *
     * @param lastName the last name to validate
     * @throws InvalidLastNameException if validation is enabled and last name is not valid
     */
    @Transactional(readOnly = true)
    public void validateLastName(String lastName) {
        boolean validationEnabled = configService.getBooleanValue("validation.lastname.enabled", false);

        if (!validationEnabled) {
            log.debug("Last name validation is disabled");
            return;
        }

        boolean caseSensitive = configService.getBooleanValue("validation.lastname.case-sensitive", false);

        boolean isValid;
        if (caseSensitive) {
            isValid = validLastNameRepository.existsByLastName(lastName);
        } else {
            isValid = validLastNameRepository.existsActiveByLastNameIgnoreCase(lastName);
        }

        if (!isValid) {
            log.warn("Invalid last name attempted: {}", lastName);
            throw new InvalidLastNameException(String.format(
                    "Last name '%s' is not in the valid list. Please contact support if this is your actual last name.",
                    lastName));
        }

        log.debug("Last name '{}' validated successfully", lastName);
    }

    @Transactional(readOnly = true)
    public List<PermittedLastName> getAllActiveLastNames() {
        return validLastNameRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public Page<PermittedLastName> getAllActiveLastNames(Pageable pageable) {
        return validLastNameRepository.findByIsActiveTrue(pageable);
    }

    @Transactional(readOnly = true)
    public PermittedLastName getById(UUID id) {
        return findByIdOrThrow(id);
    }

    private PermittedLastName findByIdOrThrow(UUID id) {
        return validLastNameRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PERMITTEDLASTNAME_ENITY, "id", id));
    }

    @Transactional(readOnly = true)
    public List<PermittedLastName> searchLastNames(String searchTerm) {
        return validLastNameRepository.searchActiveByLastName(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<PermittedLastName> getByCategory(String category) {
        return validLastNameRepository.findByCategoryAndIsActiveTrue(category);
    }

    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return validLastNameRepository.findDistinctCategories();
    }

    @Transactional
    public PermittedLastName createValidLastName(PermittedLastNameRequest request) {
        log.info("Creating new valid last name: {}", request.getLastName());

        if (validLastNameRepository.existsByLastNameIgnoreCase(request.getLastName())) {
            throw new DuplicateResourceException(PERMITTEDLASTNAME_ENITY, "lastName", request.getLastName());
        }

        PermittedLastName validLastName = PermittedLastName.builder()
                .lastName(request.getLastName())
                .description(request.getDescription())
                .category(request.getCategory())
                .isActive(true)
                .aliases(new ArrayList<>())
                .build();

        updateAliases(validLastName, request.getAliases());

        PermittedLastName saved = validLastNameRepository.save(validLastName);
        log.info(
                "Created valid last name with id: {} and {} aliases",
                saved.getId(),
                saved.getAliases().size());
        return saved;
    }

    @Transactional
    public PermittedLastName updateValidLastName(UUID id, PermittedLastNameRequest request) {
        log.info("Updating valid last name: {}", id);

        PermittedLastName existing = findByIdOrThrow(id);

        if (!RbStringUtil.equalsIgnoreCase(existing.getLastName(), request.getLastName())
                && validLastNameRepository.existsByLastNameIgnoreCase(request.getLastName())) {
            throw new DuplicateResourceException(PERMITTEDLASTNAME_ENITY, "lastName", request.getLastName());
        }

        existing.setLastName(request.getLastName());
        existing.setDescription(request.getDescription());
        existing.setCategory(request.getCategory());

        existing.getAliases().clear();
        updateAliases(existing, request.getAliases());

        PermittedLastName updated = validLastNameRepository.save(existing);
        log.info(
                "Updated valid last name: {} with {} aliases",
                id,
                updated.getAliases().size());
        return updated;
    }

    private void updateAliases(PermittedLastName lastName, List<String> aliases) {
        if (aliases != null && !aliases.isEmpty()) {
            List<LastNameAlias> aliasEntities = aliases.stream()
                    .filter(alias -> alias != null && !alias.trim().isEmpty())
                    .distinct()
                    .map(alias -> LastNameAlias.builder()
                            .alias(alias.trim())
                            .permittedLastName(lastName)
                            .build())
                    .toList();
            lastName.getAliases().addAll(aliasEntities);
        }
    }

    @Transactional
    public void deactivateLastName(UUID id) {
        log.info("Deactivating last name: {}", id);
        PermittedLastName validLastName = findByIdOrThrow(id);
        validLastName.setIsActive(false);
        validLastNameRepository.save(validLastName);
    }

    @Transactional
    public void activateLastName(UUID id) {
        log.info("Activating last name: {}", id);
        PermittedLastName validLastName = findByIdOrThrow(id);
        validLastName.setIsActive(true);
        validLastNameRepository.save(validLastName);
    }

    @Transactional
    public void deleteLastName(UUID id) {
        log.info("Deleting last name: {}", id);
        PermittedLastName validLastName = findByIdOrThrow(id);
        validLastNameRepository.delete(validLastName);
    }

    @Transactional(readOnly = true)
    public boolean isValidationEnabled() {
        return configService.getBooleanValue("validation.lastname.enabled", false);
    }
}
