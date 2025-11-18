/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.InvitationCode;
import com.github.phanikb.rootbytes.entity.UserEntity;

@Repository
public interface InvitationCodeRepository extends JpaRepository<InvitationCode, UUID> {

    Optional<InvitationCode> findByCode(String code);

    List<InvitationCode> findByInvitee(UserEntity invitee);

    List<InvitationCode> findByInviter(UserEntity inviter);

    Optional<InvitationCode> findByInviteeEmail(String inviteeEmail);
}
