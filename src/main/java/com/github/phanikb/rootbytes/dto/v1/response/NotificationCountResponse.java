/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NotificationCountResponse {
    long internalUnread;
    long externalPending;
    long total;
}
