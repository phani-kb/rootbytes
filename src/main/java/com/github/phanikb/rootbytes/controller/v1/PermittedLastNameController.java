/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.github.phanikb.rootbytes.common.Constants;
import com.github.phanikb.rootbytes.dto.v1.request.PermittedLastNameRequest;
import com.github.phanikb.rootbytes.dto.v1.response.PagedResponse;
import com.github.phanikb.rootbytes.dto.v1.response.PermittedLastNameResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RbApiResponse;
import com.github.phanikb.rootbytes.entity.PermittedLastName;
import com.github.phanikb.rootbytes.mapper.PermittedLastNameMapper;
import com.github.phanikb.rootbytes.service.PermittedLastNameService;

@RestController
@RequestMapping("/permitted-lastnames")
@RequiredArgsConstructor
public class PermittedLastNameController {

    private final PermittedLastNameService validLastNameService;
    private final PermittedLastNameMapper mapper;

    @GetMapping
    public ResponseEntity<RbApiResponse<List<PermittedLastNameResponse>>> getAllActiveLastNames() {
        List<PermittedLastNameResponse> lastNames = validLastNameService.getAllActiveLastNames().stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(RbApiResponse.success(lastNames));
    }

    @GetMapping("/paginated")
    public ResponseEntity<RbApiResponse<PagedResponse<PermittedLastNameResponse>>> getAllActiveLastNamesPaginated(
            @RequestParam(defaultValue = "#{${rb.pagination.default-page-number:0}}") int page,
            @RequestParam(defaultValue = "#{${rb.pagination.default-page-size:20}}") int size,
            @RequestParam(defaultValue = "lastName") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<PermittedLastName> lastNamesPage = validLastNameService.getAllActiveLastNames(pageable);

        PagedResponse<PermittedLastNameResponse> response = PagedResponse.<PermittedLastNameResponse>builder()
                .content(lastNamesPage.getContent().stream()
                        .map(mapper::toResponse)
                        .toList())
                .page(lastNamesPage.getNumber())
                .size(lastNamesPage.getSize())
                .totalElements(lastNamesPage.getTotalElements())
                .totalPages(lastNamesPage.getTotalPages())
                .last(lastNamesPage.isLast())
                .first(lastNamesPage.isFirst())
                .build();

        return ResponseEntity.ok(RbApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<RbApiResponse<List<PermittedLastNameResponse>>> searchLastNames(@RequestParam String query) {

        List<PermittedLastNameResponse> results = validLastNameService.searchLastNames(query).stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(RbApiResponse.success(results));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<RbApiResponse<List<PermittedLastNameResponse>>> getByCategory(@PathVariable String category) {

        List<PermittedLastNameResponse> lastNames = validLastNameService.getByCategory(category).stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(RbApiResponse.success(lastNames));
    }

    @GetMapping("/categories")
    public ResponseEntity<RbApiResponse<List<String>>> getAllCategories() {
        List<String> categories = validLastNameService.getAllCategories();
        return ResponseEntity.ok(RbApiResponse.success(categories));
    }

    @GetMapping("/validation-status")
    public ResponseEntity<RbApiResponse<Map<String, Boolean>>> getValidationStatus() {
        Map<String, Boolean> status = new HashMap<>();
        status.put("enabled", validLastNameService.isValidationEnabled());
        return ResponseEntity.ok(RbApiResponse.success(status));
    }

    @GetMapping("/{id}")
    @PreAuthorize(Constants.ADMIN_MODERATOR_ROLE)
    public ResponseEntity<RbApiResponse<PermittedLastNameResponse>> getById(@PathVariable UUID id) {
        PermittedLastName lastName = validLastNameService.getById(id);
        return ResponseEntity.ok(RbApiResponse.success(mapper.toResponse(lastName)));
    }

    @PostMapping
    @PreAuthorize(Constants.ADMIN_ROLE)
    public ResponseEntity<RbApiResponse<PermittedLastNameResponse>> create(
            @Valid @RequestBody PermittedLastNameRequest request) {

        PermittedLastName created = validLastNameService.createValidLastName(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RbApiResponse.success("Valid last name created successfully", mapper.toResponse(created)));
    }

    @PutMapping("/{id}")
    @PreAuthorize(Constants.ADMIN_ROLE)
    public ResponseEntity<RbApiResponse<PermittedLastNameResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody PermittedLastNameRequest request) {

        PermittedLastName updated = validLastNameService.updateValidLastName(id, request);
        return ResponseEntity.ok(
                RbApiResponse.success("Valid last name updated successfully", mapper.toResponse(updated)));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize(Constants.ADMIN_ROLE)
    public ResponseEntity<RbApiResponse<Void>> deactivate(@PathVariable UUID id) {
        validLastNameService.deactivateLastName(id);
        return ResponseEntity.ok(RbApiResponse.success("Last name deactivated successfully"));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize(Constants.ADMIN_ROLE)
    public ResponseEntity<RbApiResponse<Void>> activate(@PathVariable UUID id) {
        validLastNameService.activateLastName(id);
        return ResponseEntity.ok(RbApiResponse.success("Last name activated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(Constants.ADMIN_ROLE)
    public ResponseEntity<RbApiResponse<Void>> delete(@PathVariable UUID id) {
        validLastNameService.deleteLastName(id);
        return ResponseEntity.ok(RbApiResponse.success("Last name deleted successfully"));
    }
}
