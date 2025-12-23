/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.github.phanikb.rootbytes.dto.v1.response.NotificationCountResponse;
import com.github.phanikb.rootbytes.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}/counts")
    public ResponseEntity<NotificationCountResponse> getNotificationCounts(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getNotificationCounts(userId));
    }

    @PostMapping("/{userId}/read")
    public ResponseEntity<NotificationCountResponse> markAllAsRead(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.markAllAsRead(userId));
    }
}
