/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.phanikb.rootbytes.common.Constants;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer.usePathSegment(1).setDefaultVersion(Constants.API_VERSION);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Set a common path prefix for all controllers
        configurer.addPathPrefix(
                Constants.API_BASE,
                c -> c.getPackage() != null
                        && c.getPackage().getName().startsWith("com.github.phanikb.rootbytes.controller"));
    }
}
