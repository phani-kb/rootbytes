/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.response;

import java.time.Instant;
import java.util.function.Function;

import jakarta.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RbApiResponse<T> {

    private Boolean success;

    @Nullable
    private String message;

    @Nullable
    private T data;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private Instant timestamp;

    public static RbApiResponse<Void> success(String message) {
        return RbApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static RbApiResponse<Void> failure(String message) {
        return RbApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> RbApiResponse<T> error(String message) {
        return RbApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> RbApiResponse<T> success(T data) {
        return RbApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public <U> RbApiResponse<U> map(Function<T, U> mapper) {
        return RbApiResponse.<U>builder()
                .success(this.success)
                .data(this.data != null ? mapper.apply(this.data) : null)
                .message(this.message)
                .build();
    }

    public static <T> RbApiResponse<T> success(@Nullable String message, @Nullable T data) {
        return RbApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }
}
