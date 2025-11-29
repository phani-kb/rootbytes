/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.phanikb.rootbytes.dto.v1.request.InvitationCodeRequest;
import com.github.phanikb.rootbytes.dto.v1.response.InvitationCodeResponse;
import com.github.phanikb.rootbytes.entity.InvitationCode;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.mapper.InvitationCodeMapper;
import com.github.phanikb.rootbytes.service.InvitationCodeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals("ABC12345", response.getBody().getData().getCode());
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
