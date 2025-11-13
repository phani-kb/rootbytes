/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */
package com.github.phanikb.rootbytes.util;

import org.springframework.web.context.request.WebRequest;

import com.github.phanikb.rootbytes.common.Constants;

public final class RbUtil {

    private RbUtil() {}

    public static String getAppNameVersion(String appName, String appVersion) {
        return String.format(Constants.BANNER_TEMPLATE, appName, appVersion);
    }

    public static String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
