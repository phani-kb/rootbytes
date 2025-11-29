/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.scheduler;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.config.NotificationQueueProperties;
import com.github.phanikb.rootbytes.dto.v1.response.NotificationQueueResponse;
import com.github.phanikb.rootbytes.service.NotificationQueueService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationQueueScheduler {

    private final NotificationQueueService notificationQueueService;
    private final NotificationQueueProperties notificationQueueProperties;

    @Scheduled(
            fixedDelayString = "${rootbytes.notification.queue.processing-interval-minutes:15}",
            initialDelayString = "${rootbytes.notification.queue.processing-interval-minutes:15}",
            timeUnit = TimeUnit.MINUTES)
    public void notificationProcessing() {
        if (!notificationQueueProperties.isEnabled()) {
            log.trace("Notification queue disabled; skipping scheduled processing");
            return;
        }

        notificationQueueService.retryFailedNotifications();
        List<NotificationQueueResponse> processed = notificationQueueService.processDueNotifications();
        if (processed.isEmpty()) {
            log.debug("Notification queue scheduler executed; no pending notifications ready to send");
            return;
        }

        processed.forEach(response -> log.info(
                "Notification delivery id={}, user={}, channel={}, scheduledFor={}",
                response.getId(),
                response.getUserId(),
                response.getChannel(),
                response.getScheduledFor()));
    }
}
