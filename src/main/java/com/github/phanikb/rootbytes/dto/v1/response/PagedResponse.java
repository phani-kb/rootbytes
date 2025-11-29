/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResponse<T> {

    private List<T> content;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    private boolean last;

    private boolean first;

    private boolean empty;

    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements, int totalPages) {
        return PagedResponse.<T>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .empty(content == null || content.isEmpty())
                .build();
    }
}
