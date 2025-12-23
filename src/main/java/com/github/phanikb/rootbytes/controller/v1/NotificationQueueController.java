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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.github.phanikb.rootbytes.dto.v1.request.NotificationQueueRequest;
import com.github.phanikb.rootbytes.dto.v1.response.NotificationQueueResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RbApiResponse;
import com.github.phanikb.rootbytes.enums.notification.NotificationChannel;
import com.github.phanikb.rootbytes.enums.notification.QueueStatus;
import com.github.phanikb.rootbytes.service.NotificationQueueService;

@RestController
@RequestMapping("/notification-queue")
@RequiredArgsConstructor
public class NotificationQueueController {

    private final NotificationQueueService queueService;

    @PostMapping
    public ResponseEntity<RbApiResponse<NotificationQueueResponse>> enqueue(
            @Valid @RequestBody NotificationQueueRequest request) {
        NotificationQueueResponse response = queueService.enqueue(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(RbApiResponse.success("Notification queued", response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<RbApiResponse<List<NotificationQueueResponse>>> getQueueForUser(
            @PathVariable UUID userId, @RequestParam(value = "status", required = false) QueueStatus status) {
        List<NotificationQueueResponse> queue = queueService.getQueueForUser(userId, status);
        return ResponseEntity.ok(RbApiResponse.success(queue));
    }

    @GetMapping("/due")
    public ResponseEntity<RbApiResponse<List<NotificationQueueResponse>>> getDueNotifications(
            @RequestParam(value = "channel", required = false) NotificationChannel channel) {
        List<NotificationQueueResponse> due = queueService.getDueNotifications(channel);
        return ResponseEntity.ok(RbApiResponse.success(due));
    }
}
