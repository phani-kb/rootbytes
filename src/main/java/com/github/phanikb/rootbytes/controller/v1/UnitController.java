/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.github.phanikb.rootbytes.common.Constants;
import com.github.phanikb.rootbytes.dto.v1.request.UnitRequest;
import com.github.phanikb.rootbytes.dto.v1.response.RbApiResponse;
import com.github.phanikb.rootbytes.dto.v1.response.UnitResponse;
import com.github.phanikb.rootbytes.entity.Unit;
import com.github.phanikb.rootbytes.enums.UnitType;
import com.github.phanikb.rootbytes.mapper.UnitMapper;
import com.github.phanikb.rootbytes.service.UnitService;

@RestController
@RequestMapping("/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService service;
    private final UnitMapper mapper;

    @GetMapping
    public ResponseEntity<RbApiResponse<List<UnitResponse>>> getAllActiveUnits() {
        List<UnitResponse> units =
                service.getAllActiveUnits().stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(RbApiResponse.success(units));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RbApiResponse<UnitResponse>> getUnitById(@PathVariable UUID id) {
        Unit unit = service.getUnitById(id);
        return ResponseEntity.ok(RbApiResponse.success(mapper.toResponse(unit)));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<RbApiResponse<List<UnitResponse>>> getUnitsByType(@PathVariable UnitType type) {
        List<UnitResponse> units =
                service.getUnitsByType(type).stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(RbApiResponse.success(units));
    }

    @PostMapping
    @PreAuthorize(Constants.ADMIN_MODERATOR_ROLE)
    public ResponseEntity<RbApiResponse<UnitResponse>> createUnit(@Valid @RequestBody UnitRequest unitRequest) {
        Unit unit = service.createUnit(unitRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RbApiResponse.success("Unit created successfully", mapper.toResponse(unit)));
    }

    @PutMapping("/{id}")
    @PreAuthorize(Constants.ADMIN_MODERATOR_ROLE)
    public ResponseEntity<RbApiResponse<UnitResponse>> updateUnit(
            @PathVariable UUID id, @Valid @RequestBody UnitRequest request) {
        Unit unit = service.updateUnitById(id, request);
        return ResponseEntity.ok(RbApiResponse.success("Unit updated successfully", mapper.toResponse(unit)));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize(Constants.ADMIN_MODERATOR_ROLE)
    public ResponseEntity<RbApiResponse<Void>> deactivateUnit(@PathVariable UUID id) {
        service.deactivateUnit(id);
        return ResponseEntity.ok(RbApiResponse.success("Unit deactivated successfully"));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize(Constants.ADMIN_MODERATOR_ROLE)
    public ResponseEntity<RbApiResponse<Void>> activateUnit(@PathVariable UUID id) {
        service.activateUnit(id);
        return ResponseEntity.ok(RbApiResponse.success("Unit activated successfully"));
    }
}
