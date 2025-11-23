/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.common;

public final class Constants {
    public static final String AUTHENTICATED = "isAuthenticated()";
    public static final String ADMIN_ROLE = "hasRole('ADMIN')";
    public static final String ADMIN_MODERATOR_ROLE = "hasAnyRole('ADMIN', 'MODERATOR')";
    public static final String APPLICATION_NAME = "RootBytes";
    public static final String APPLICATION_VERSION = "0.0.1";
    public static final String BANNER_TEMPLATE = """
            \u001B[92m::
            :: %s :: %s ::
            ::\u001B[0m""";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final long DEFAULT_INVITATION_CODE_EXPIRY_DAYS = 7;
    public static final int MAX_INVITATION_CODES_PER_USER = 1;
    public static final int INVITATION_CODE_LENGTH = 8;
    public static final int MAX_INVITATION_CODE_LENGTH = 32;
    public static final String DEFAULT_USER_ROLE = "USER";

    public static final class Page {
        public static final int DEFAULT_PAGE_NUMBER = 0;
        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int MAX_PAGE_SIZE = 50;

        private Page() {}
    }

    private Constants() {}
}
