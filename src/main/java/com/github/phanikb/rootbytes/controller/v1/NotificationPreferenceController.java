/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.github.phanikb.rootbytes.common.Constants;
import com.github.phanikb.rootbytes.dto.v1.request.NotificationPreferenceRequest;
import com.github.phanikb.rootbytes.dto.v1.response.NotificationPreferenceResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RbApiResponse;
import com.github.phanikb.rootbytes.service.NotificationPreferenceService;

@RestController
@RequestMapping(Constants.API_V1 + "/notifications/preferences")
@PreAuthorize(Constants.AUTHENTICATED)
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceService notificationPreferenceService;

    @GetMapping("/{userId}")
    public ResponseEntity<RbApiResponse<NotificationPreferenceResponse>> getPreferences(@PathVariable UUID userId) {
        return ResponseEntity.ok(RbApiResponse.success(notificationPreferenceService.getPreferences(userId)));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<RbApiResponse<NotificationPreferenceResponse>> updatePreferences(
            @PathVariable UUID userId, @Valid @RequestBody NotificationPreferenceRequest request) {
        NotificationPreferenceResponse response = notificationPreferenceService.updatePreferences(userId, request);
        return ResponseEntity.ok(RbApiResponse.success(response));
    }
}
