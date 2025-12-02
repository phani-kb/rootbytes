/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.common;

public final class ValidationConstants {

    public static final int SIZE_XS = 10;
    public static final int SIZE_S = 50;
    public static final int SIZE_M = 255;
    public static final int SIZE_L = 1000;
    public static final int SIZE_XL = 65_535;

    public static final int MAX_SUBSCRIBED_EVENTS = 10;
    public static final int MAX_INGREDIENTS = 20;
    public static final int MAX_INSTRUCTIONS = 30;

    private ValidationConstants() {}

    public static final class Messages {

        private static final String CHARS = " characters";

        public static final String REQUIRED = " is required";
        public static final String TITLE_REQUIRED = "Title" + REQUIRED;
        public static final String NAME_REQUIRED = "Name" + REQUIRED;
        public static final String DESCRIPTION_REQUIRED = "Description" + REQUIRED;
        public static final String MESSAGE_REQUIRED = "Message" + REQUIRED;
        public static final String REASON_REQUIRED = "Reason" + REQUIRED;
        public static final String COMMENTS_REQUIRED = "Comments are required";
        public static final String STATUS_REQUIRED = "Status" + REQUIRED;
        public static final String TYPE_REQUIRED = "Type" + REQUIRED;
        public static final String ACTION_REQUIRED = "Action" + REQUIRED;
        public static final String USER_REQUIRED = "User" + REQUIRED;

        public static final String TITLE_TOO_LONG = "Title must not exceed " + SIZE_M + CHARS;
        public static final String NAME_TOO_LONG = "Name must not exceed " + SIZE_M + CHARS;
        public static final String CATEGORY_TOO_LONG = "Category must not exceed " + SIZE_S + CHARS;
        public static final String DESCRIPTION_TOO_LONG = "Description must not exceed " + SIZE_L + CHARS;
        public static final String REASON_TOO_LONG = "Reason must not exceed " + SIZE_L + CHARS;
        public static final String MESSAGE_TOO_LONG = "Message must not exceed " + SIZE_L + CHARS;
        public static final String NOTES_TOO_LONG = "Notes must not exceed " + SIZE_L + CHARS;
        public static final String COMMENTS_TOO_LONG = "Comments must not exceed " + SIZE_XL + CHARS;
        public static final String URL_TOO_LONG = "URL must not exceed " + SIZE_M + CHARS;
        public static final String TEXT_TOO_LONG = "Content must not exceed " + SIZE_XL + CHARS;
        public static final String DATA_TOO_LONG = "Data must not exceed " + SIZE_XL + CHARS;
        public static final String CONFIG_VALUE_TOO_LONG = "Value must not exceed " + SIZE_M + CHARS;
        public static final String ABBREVIATION_TOO_LONG = "Abbreviation must not exceed " + SIZE_XS + CHARS;

        public static final String MUST_BE_POSITIVE = " must be positive";
        public static final String MUST_BE_POSITIVE_OR_ZERO = " must be zero or positive";

        private Messages() {}
    }
}
