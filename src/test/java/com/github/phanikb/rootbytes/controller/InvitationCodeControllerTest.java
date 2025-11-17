/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.phanikb.rootbytes.dto.request.InvitationCodeRequest;
import com.github.phanikb.rootbytes.dto.response.InvitationCodeResponse;
import com.github.phanikb.rootbytes.entity.InvitationCode;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.mapper.InvitationCodeMapper;
import com.github.phanikb.rootbytes.service.InvitationCodeService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class InvitationCodeControllerTest {

    @Mock
    private InvitationCodeService invitationCodeService;

    @Mock
    private InvitationCodeMapper invitationCodeMapper;

    private InvitationCodeController controller;

    @BeforeEach
    void setUp() {
        controller = new InvitationCodeController(invitationCodeService, invitationCodeMapper);
    }

    @Test
    void shouldGenerateInvitationCode_whenValidRequest() {
        var request = new InvitationCodeRequest("invitee@example.com");
        var admin = createMockAdmin();
        var invitation = createMockInvitation(admin, request.getInviteeEmail());
        var expectedResponse = createMockResponse();

        given(invitationCodeService.generateInvitationCode(any(UserEntity.class), anyString()))
                .willReturn(invitation);
        given(invitationCodeMapper.toResponse(invitation)).willReturn(expectedResponse);

        var response = controller.generateInvitationCode(request, admin);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().getCode()).isEqualTo("ABC12345");
    }

    private UserEntity createMockAdmin() {
        return UserEntity.builder()
                .id(UUID.randomUUID())
                .email("admin@example.com")
                .build();
    }

    private InvitationCode createMockInvitation(UserEntity admin, String inviteeEmail) {
        return InvitationCode.builder()
                .code("ABC12345")
                .inviter(admin)
                .inviteeEmail(inviteeEmail)
                .isActive(true)
                .expiresAt(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now())
                .build();
    }

    private InvitationCodeResponse createMockResponse() {
        return InvitationCodeResponse.builder().code("ABC12345").build();
    }
}
