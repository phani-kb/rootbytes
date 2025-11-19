/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.config;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class H2Config {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(value = "rootbytes.h2.tcp-enabled", havingValue = "true")
    public Server h2TcpServer(@Value("${rootbytes.h2.tcp-port:9092}") int tcpPort) throws SQLException {
        log.info("Starting H2 TCP Server on port {}", tcpPort);
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", String.valueOf(tcpPort));
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(value = "spring.h2.console.enabled", havingValue = "true")
    public Server h2WebServer(
            @Value("${rootbytes.h2.web-port:8082}") int webPort,
            @Value("${spring.h2.console.settings.web-allow-others:false}") boolean webAllowOthers)
            throws SQLException {

        log.info("Starting H2 Web Console on port {} (allow others: {})", webPort, webAllowOthers);

        return webAllowOthers
                ? Server.createWebServer("-web", "-webAllowOthers", "-webPort", String.valueOf(webPort))
                : Server.createWebServer("-web", "-webPort", String.valueOf(webPort));
    }
}
