/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.dto.v1.request.UnitRequest;
import com.github.phanikb.rootbytes.entity.Unit;
import com.github.phanikb.rootbytes.enums.UnitType;
import com.github.phanikb.rootbytes.exception.DuplicateResourceException;
import com.github.phanikb.rootbytes.exception.ResourceNotFoundException;
import com.github.phanikb.rootbytes.repository.UnitRepository;

@Slf4j
@Service
public class UnitService {

    private static final String UNIT_ENTITY = "Unit";
    private static final String UNIT_NAME = "name";

    private final UnitRepository repository;

    public UnitService(UnitRepository unitRepository) {
        this.repository = unitRepository;
    }

    @Transactional(readOnly = true)
    public List<Unit> getAllActiveUnits() {
        return repository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Unit> getUnitsByType(UnitType type) {
        return repository.findByUnitTypeAndIsActiveTrue(type);
    }

    @Transactional(readOnly = true)
    public Unit getUnitById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(UNIT_ENTITY, "id", id));
    }

    @Transactional(readOnly = true)
    public Unit getUnitByName(String name) {
        return repository
                .findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(UNIT_ENTITY, UNIT_NAME, name));
    }

    @Transactional(readOnly = true)
    public Unit getUnitByAbbreviation(String abbreviation) {
        return repository
                .findByAbbreviation(abbreviation)
                .orElseThrow(() -> new ResourceNotFoundException(UNIT_ENTITY, "abbreviation", abbreviation));
    }

    @Transactional
    public Unit saveUnit(Unit unit) {
        return repository.save(unit);
    }

    @Transactional
    public Unit createUnit(UnitRequest request) {
        if (repository.existsByName(request.getName())) {
            throw new DuplicateResourceException(UNIT_ENTITY, UNIT_NAME, request.getName());
        }

        if (repository.existsByAbbreviation(request.getAbbreviation())) {
            throw new DuplicateResourceException(UNIT_ENTITY, "abbreviation", request.getAbbreviation());
        }

        Unit unit = Unit.builder()
                .name(request.getName())
                .abbreviation(request.getAbbreviation())
                .unitType(request.getUnitType())
                .description(request.getDescription())
                .isActive(true)
                .build();
        Unit savedUnit = saveUnit(unit);
        log.info("Created new unit: {} ({})", savedUnit.getName(), savedUnit.getAbbreviation());
        return savedUnit;
    }

    @Transactional
    public Unit updateUnitById(UUID id, UnitRequest request) {
        Unit unit = getUnitById(id);

        if (!unit.getName().equals(request.getName()) && repository.existsByName(request.getName())) {
            throw new DuplicateResourceException(UNIT_ENTITY, UNIT_NAME, request.getName());
        }

        if (!unit.getAbbreviation().equals(request.getAbbreviation())
                && repository.existsByAbbreviation(request.getAbbreviation())) {
            throw new DuplicateResourceException(UNIT_ENTITY, "abbreviation", request.getAbbreviation());
        }

        unit.setName(request.getName());
        unit.setAbbreviation(request.getAbbreviation());
        unit.setUnitType(request.getUnitType());
        unit.setDescription(request.getDescription());

        Unit updatedUnit = saveUnit(unit);
        log.info("Updated unit: {} ({})", updatedUnit.getName(), updatedUnit.getAbbreviation());
        return updatedUnit;
    }

    @Transactional
    public void activateUnit(UUID id) {
        Unit unit = getUnitById(id);
        unit.setActive(true);
        repository.save(unit);
        log.info("Activated unit: {} ({})", unit.getName(), unit.getAbbreviation());
    }
}
