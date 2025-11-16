/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.phanikb.rootbytes.entity.InvitationCode;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.exception.InvalidInvitationException;
import com.github.phanikb.rootbytes.repository.InvitationCodeRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvitationCodeServiceTest {

    @Mock
    private InvitationCodeRepository invitationCodeRepository;

    @InjectMocks
    private InvitationCodeService invitationCodeService;

    private UserEntity inviter;
    private UserEntity invitee;
    private InvitationCode validCode;
    private InvitationCode usedCode;
    private InvitationCode inactiveCode;
    private String code;

    @BeforeEach
    void setUp() {
        code = "TEST1234";

        inviter = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("inviter@test.com")
                .lastName("Inviter")
                .build();

        invitee = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("invitee@test.com")
                .lastName("Invitee")
                .build();

        validCode = InvitationCode.builder()
                .id(UUID.randomUUID())
                .code(code)
                .inviter(inviter)
                .isActive(true)
                .build();

        usedCode = InvitationCode.builder()
                .id(UUID.randomUUID())
                .code("USED1234")
                .inviter(inviter)
                .invitee(invitee)
                .isActive(true)
                .usedAt(Instant.now())
                .build();

        inactiveCode = InvitationCode.builder()
                .id(UUID.randomUUID())
                .code("INACTIVE")
                .inviter(inviter)
                .isActive(false)
                .build();
    }

    @Test
    void shouldValidateInvitationCode() {
        when(invitationCodeRepository.findByCode(code)).thenReturn(Optional.of(validCode));

        InvitationCode result = invitationCodeService.validateInvitationCode(code);

        assertNotNull(result);
        assertEquals(code, result.getCode());
        assertTrue(result.getIsActive());
        verify(invitationCodeRepository).findByCode(code);
    }

    @Test
    void shouldThrowExceptionWhenCodeNotFound() {
        when(invitationCodeRepository.findByCode(code)).thenReturn(Optional.empty());

        assertThrows(InvalidInvitationException.class, () -> invitationCodeService.validateInvitationCode(code));

        verify(invitationCodeRepository).findByCode(code);
    }

    @Test
    void shouldThrowExceptionWhenCodeIsInactive() {
        when(invitationCodeRepository.findByCode("INACTIVE")).thenReturn(Optional.of(inactiveCode));

        assertThrows(InvalidInvitationException.class, () -> invitationCodeService.validateInvitationCode("INACTIVE"));

        verify(invitationCodeRepository).findByCode("INACTIVE");
    }

    @Test
    void shouldThrowExceptionWhenCodeAlreadyUsed() {
        when(invitationCodeRepository.findByCode("USED1234")).thenReturn(Optional.of(usedCode));

        assertThrows(InvalidInvitationException.class, () -> invitationCodeService.validateInvitationCode("USED1234"));

        verify(invitationCodeRepository).findByCode("USED1234");
    }

    @Test
    void shouldUseInvitationCode() {
        when(invitationCodeRepository.findByCode(code)).thenReturn(Optional.of(validCode));
        when(invitationCodeRepository.save(any(InvitationCode.class))).thenReturn(validCode);

        InvitationCode result = invitationCodeService.useInvitationCode(code, invitee);

        assertNotNull(result);
        assertEquals(invitee, result.getInvitee());
        assertNotNull(result.getUsedAt());
        assertFalse(result.getIsActive());
        verify(invitationCodeRepository).findByCode(code);
        verify(invitationCodeRepository).save(validCode);
    }

    @Test
    void shouldGenerateInvitationCode() {
        when(invitationCodeRepository.save(any(InvitationCode.class))).thenReturn(validCode);

        InvitationCode result = invitationCodeService.generateInvitationCode(inviter);

        assertNotNull(result);
        assertEquals(inviter, result.getInviter());
        assertTrue(result.getIsActive());
        assertNotNull(result.getCode());
        verify(invitationCodeRepository).save(any(InvitationCode.class));
    }

    @Test
    void shouldGenerateUniqueCodeFormat() {
        when(invitationCodeRepository.save(any(InvitationCode.class))).thenAnswer(invocation -> {
            InvitationCode code = invocation.getArgument(0);
            assertEquals(8, code.getCode().length());
            assertEquals(code.getCode(), code.getCode().toUpperCase());
            return code;
        });

        invitationCodeService.generateInvitationCode(inviter);

        verify(invitationCodeRepository).save(any(InvitationCode.class));
    }
}
