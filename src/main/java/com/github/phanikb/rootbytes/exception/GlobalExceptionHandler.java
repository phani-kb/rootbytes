/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.dto.v1.response.ErrorResponse;
import com.github.phanikb.rootbytes.util.LogSanitizer;
import com.github.phanikb.rootbytes.util.RbUtil;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unhandled exception: {}", LogSanitizer.sanitize(ex));

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred. Please try again later.")
                .path(RbUtil.getPath(request))
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RbException.class)
    public ResponseEntity<ErrorResponse> handleRbException(RbException ex, WebRequest request) {
        log.error("RbException: {}", LogSanitizer.sanitize(ex));

        String message = ex.getMessage() != null ? ex.getMessage() : "An error occurred";
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(RbUtil.getPath(request))
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation error: {}", LogSanitizer.sanitize(ex));

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(RbUtil.getPath(request))
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource not found error: {}", LogSanitizer.sanitize(ex));

        String message = ex.getMessage() != null ? ex.getMessage() : "Resource not found";
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(message)
                .path(RbUtil.getPath(request))
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {
        log.error("Duplicate resource error: {}", LogSanitizer.sanitize(ex));

        String message = ex.getMessage() != null ? ex.getMessage() : "Duplicate resource error";
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(RbUtil.getPath(request))
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRecipeNotFoundException(RecipeNotFoundException ex, WebRequest request) {
        log.warn("Recipe not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(RbUtil.getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidInvitationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInvitationException(
            InvalidInvitationException ex, WebRequest request) {
        log.error("Invalid invitation error: {}", LogSanitizer.sanitize(ex));

        String message = ex.getMessage() != null ? ex.getMessage() : "Invalid invitation";
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(RbUtil.getPath(request))
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidLastNameException.class)
    public ResponseEntity<ErrorResponse> handleInvalidLastNameException(
            InvalidLastNameException ex, WebRequest request) {
        log.warn("Invalid last name: {}", LogSanitizer.sanitize(ex));

        String message = ex.getMessage() != null ? ex.getMessage() : "Invalid last name";
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(RbUtil.getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotificationQueueDisabledException.class)
    public ResponseEntity<ErrorResponse> handleNotificationQueueDisabledException(
            NotificationQueueDisabledException ex, WebRequest request) {
        log.error("Notification queue disabled error: {}", LogSanitizer.sanitize(ex));

        String message = ex.getMessage() != null ? ex.getMessage() : "Notification queue is disabled";
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(message)
                .path(RbUtil.getPath(request))
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotificationQueueLimitException.class)
    public ResponseEntity<ErrorResponse> handleNotificationQueueLimitException(
            NotificationQueueLimitException ex, WebRequest request) {
        log.error("Notification queue limit exceeded: {}", LogSanitizer.sanitize(ex));

        String message = ex.getMessage() != null ? ex.getMessage() : "Notification queue limit exceeded";
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(message)
                .path(RbUtil.getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccessException(
            UnauthorizedAccessException ex, WebRequest request) {
        log.error("Unauthorized access error: {}", LogSanitizer.sanitize(ex));

        String message = ex.getMessage() != null ? ex.getMessage() : "Unauthorized access";
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(message)
                .path(RbUtil.getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ApprovalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleApprovalNotFoundException(
            ApprovalNotFoundException ex, WebRequest request) {
        log.error("Approval not found error: {}", LogSanitizer.sanitize(ex));

        String message = ex.getMessage() != null ? ex.getMessage() : "Approval not found";
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(message)
                .path(RbUtil.getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReviewNotFoundException(ReviewNotFoundException ex, WebRequest request) {
        log.error("Review not found error: {}", LogSanitizer.sanitize(ex));

        String message = ex.getMessage() != null ? ex.getMessage() : "Review not found";
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(message)
                .path(RbUtil.getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<ErrorResponse> handleResourceInUseException(ResourceInUseException ex, WebRequest request) {
        log.error("Resource in use error: {}", LogSanitizer.sanitize(ex));

        String message = ex.getMessage() != null ? ex.getMessage() : "Resource is currently in use";
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(message)
                .path(RbUtil.getPath(request))
                .build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}
