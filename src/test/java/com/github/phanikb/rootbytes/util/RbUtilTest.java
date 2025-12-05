/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import com.github.phanikb.rootbytes.common.Constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RbUtilTest {

    @Test
    void testGetAppNameVersion() {
        String appName = "RootBytes";
        String appVersion = "1.0.0";

        String expectedBanner = String.format(Constants.BANNER_TEMPLATE, appName, appVersion);
        assertEquals(expectedBanner, RbUtil.getAppNameVersion(appName, appVersion));
    }

    @Test
    void testGetPath() {
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");
        var webRequest = new ServletWebRequest(request);
        assertEquals("/api/v1/test", RbUtil.getPath(webRequest));
    }
}
