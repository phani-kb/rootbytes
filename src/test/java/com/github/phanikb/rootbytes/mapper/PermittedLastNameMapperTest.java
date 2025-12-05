/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.phanikb.rootbytes.dto.v1.response.PermittedLastNameResponse;
import com.github.phanikb.rootbytes.entity.LastNameAlias;
import com.github.phanikb.rootbytes.entity.PermittedLastName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PermittedLastNameMapperTest {

    private PermittedLastNameMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PermittedLastNameMapperImpl();
    }

    @Test
    void shouldMapEntityToResponse() {
        UUID id = UUID.randomUUID();
        PermittedLastName entity = PermittedLastName.builder()
                .id(id)
                .lastName("Kumar")
                .description("Common Indian surname")
                .category("Indian")
                .isActive(true)
                .aliases(new ArrayList<>())
                .build();

        PermittedLastNameResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Kumar", response.getLastName());
        assertEquals("Common Indian surname", response.getDescription());
        assertEquals("Indian", response.getCategory());
        assertEquals(true, response.getIsActive());
        assertEquals("", response.getAliases());
    }

    @Test
    void shouldMapAliasesToCommaSeparatedString() {
        PermittedLastName entity = PermittedLastName.builder()
                .id(UUID.randomUUID())
                .lastName("Kumar")
                .category("Indian")
                .isActive(true)
                .aliases(new ArrayList<>())
                .build();

        LastNameAlias alias1 = LastNameAlias.builder()
                .id(UUID.randomUUID())
                .alias("Kumari")
                .permittedLastName(entity)
                .build();
        LastNameAlias alias2 = LastNameAlias.builder()
                .id(UUID.randomUUID())
                .alias("Kumaran")
                .permittedLastName(entity)
                .build();
        entity.getAliases().add(alias1);
        entity.getAliases().add(alias2);

        PermittedLastNameResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals("Kumari, Kumaran", response.getAliases());
    }

    @Test
    void shouldHandleNullAliases() {
        PermittedLastName entity = PermittedLastName.builder()
                .id(UUID.randomUUID())
                .lastName("Smith")
                .category("Common")
                .isActive(true)
                .aliases(null)
                .build();

        PermittedLastNameResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals("", response.getAliases());
    }

    @Test
    void shouldHandleEmptyAliases() {
        PermittedLastName entity = PermittedLastName.builder()
                .id(UUID.randomUUID())
                .lastName("Williams")
                .category("Common")
                .isActive(true)
                .aliases(List.of())
                .build();

        PermittedLastNameResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals("", response.getAliases());
    }

    @Test
    void shouldMapSingleAlias() {
        PermittedLastName entity = PermittedLastName.builder()
                .id(UUID.randomUUID())
                .lastName("Singh")
                .category("Indian")
                .isActive(true)
                .aliases(new ArrayList<>())
                .build();

        LastNameAlias alias = LastNameAlias.builder()
                .id(UUID.randomUUID())
                .alias("Singha")
                .permittedLastName(entity)
                .build();
        entity.getAliases().add(alias);

        PermittedLastNameResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals("Singha", response.getAliases());
    }

    @Test
    void shouldMapAliasesToStringMethod() {
        assertEquals("", mapper.aliasesToString(null));
        assertEquals("", mapper.aliasesToString(List.of()));

        PermittedLastName entity = PermittedLastName.builder().build();
        LastNameAlias alias1 =
                LastNameAlias.builder().alias("Test1").permittedLastName(entity).build();
        LastNameAlias alias2 =
                LastNameAlias.builder().alias("Test2").permittedLastName(entity).build();

        assertEquals("Test1, Test2", mapper.aliasesToString(List.of(alias1, alias2)));
    }
}
