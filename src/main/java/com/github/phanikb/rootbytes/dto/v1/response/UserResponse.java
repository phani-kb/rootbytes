/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.response;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserResponse {
    private UUID id;
    private String email;
    private String phone;
    private String lastName;
    private String uniqueName;
    private String publicName;
    private String role;
    private String status;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Instant createdAt;
    private Instant updatedAt;
}
