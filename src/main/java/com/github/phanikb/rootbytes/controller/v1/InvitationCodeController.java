/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.util.Objects;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.common.Constants;
import com.github.phanikb.rootbytes.dto.v1.request.InvitationCodeRequest;
import com.github.phanikb.rootbytes.dto.v1.response.InvitationCodeResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RbApiResponse;
import com.github.phanikb.rootbytes.entity.InvitationCode;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.mapper.InvitationCodeMapper;
import com.github.phanikb.rootbytes.security.RbCurrentUser;
import com.github.phanikb.rootbytes.service.InvitationCodeService;

@Slf4j
@RestController
@RequestMapping(Constants.API_V1 + "/invitations")
@RequiredArgsConstructor
public class InvitationCodeController {

    private final InvitationCodeService invitationCodeService;
    private final InvitationCodeMapper invitationCodeMapper;

    @PostMapping("/generate")
    @PreAuthorize(Constants.ADMIN_ROLE)
    public ResponseEntity<RbApiResponse<InvitationCodeResponse>> generateInvitationCode(
            @Valid @RequestBody InvitationCodeRequest request, @RbCurrentUser UserEntity admin) {
        String inviteeEmail = Objects.requireNonNull(request.getInviteeEmail(), "Invitee email is required");
        log.info("Admin {} is generating invitation code for email: {}", admin.getEmail(), request.getInviteeEmail());

        InvitationCode invitation = invitationCodeService.generateInvitationCode(admin, inviteeEmail);
        InvitationCodeResponse response = Objects.requireNonNull(
                invitationCodeMapper.toResponse(invitation), "Failed to map invitation response");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RbApiResponse.success("Invitation code generated successfully", response));
    }
}
