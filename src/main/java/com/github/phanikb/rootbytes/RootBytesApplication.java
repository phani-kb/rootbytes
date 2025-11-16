/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.github.phanikb.rootbytes.common.Constants;
import com.github.phanikb.rootbytes.util.RbUtil;

@SuppressWarnings("PMD.UseUtilityClass")
@EnableConfigurationProperties
@SpringBootApplication
public class RootBytesApplication {
    public static void main(String[] args) {
        SpringApplication springApp = new SpringApplication(RootBytesApplication.class);
        springApp.setBanner((environment, sourceClass, out) -> {
            String appName = environment.getProperty("spring.application.name", Constants.APPLICATION_NAME);
            String appVersion = environment.getProperty("spring.application.version", Constants.APPLICATION_VERSION);
            out.println(RbUtil.getAppNameVersion(appName, appVersion));
        });
        springApp.run(args);
    }
}
