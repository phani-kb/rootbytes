/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.common.Constants;
import com.github.phanikb.rootbytes.entity.InvitationCode;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.exception.InvalidInvitationException;
import com.github.phanikb.rootbytes.repository.InvitationCodeRepository;
import com.github.phanikb.rootbytes.util.RbStringUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvitationCodeService {

    private final InvitationCodeRepository invitationCodeRepository;

    public InvitationCode validateInvitationCode(String code, UserEntity invitee) {
        log.debug("Validating invitation code: {}", code);

        InvitationCode invitation = invitationCodeRepository
                .findByCode(code)
                .orElseThrow(() -> new InvalidInvitationException("Invalid invitation code"));

        if (!Boolean.TRUE.equals(invitation.getIsActive())) {
            throw new InvalidInvitationException("Invitation code is no longer active");
        }

        if (invitation.getUsedAt() != null) {
            throw new InvalidInvitationException("Invitation code has already been used");
        }

        if (!invitation.getInviteeEmail().equals(invitee.getEmail())) {
            throw new InvalidInvitationException("Invitation code is not valid for this user");
        }

        return invitation;
    }

    @Transactional
    public InvitationCode useInvitationCode(String code, UserEntity invitee) {
        log.info("Using invitation code: {}", code);

        InvitationCode invitation = validateInvitationCode(code, invitee);
        invitation.setInvitee(invitee);
        invitation.setUsedAt(Instant.now());
        invitation.setIsActive(false);

        return invitationCodeRepository.save(invitation);
    }

    @Transactional
    public InvitationCode generateInvitationCode(UserEntity inviter, String inviteeEmail) {
        log.info("Generating invitation code for user: {} with invitee email: {}", inviter.getEmail(), inviteeEmail);

        String code = generateUniqueCode();

        InvitationCode invitation = InvitationCode.builder()
                .code(code)
                .inviter(inviter)
                .inviteeEmail(inviteeEmail)
                .isActive(true)
                .expiresAt(Instant.now().plus(Constants.DEFAULT_INVITATION_CODE_EXPIRY_DAYS, ChronoUnit.DAYS))
                .build();

        return invitationCodeRepository.save(invitation);
    }

    private String generateUniqueCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, Math.min(Constants.INVITATION_CODE_LENGTH, Constants.MAX_INVITATION_CODE_LENGTH))
                .toUpperCase(RbStringUtil.ROOT_LOCALE);
    }
}
