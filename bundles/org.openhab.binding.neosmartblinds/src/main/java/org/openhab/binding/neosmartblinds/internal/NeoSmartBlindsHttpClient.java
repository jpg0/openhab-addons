/*
 * Copyright (c) 2010-2026 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.neosmartblinds.internal;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NeoSmartBlindsHttpClient} class handles the HTTP communication with the NeoSmart Blinds hub.
 *
 * @author Jonathan Gilbert - Initial contribution
 */
@NonNullByDefault
public class NeoSmartBlindsHttpClient {

    private final Logger logger = LoggerFactory.getLogger(NeoSmartBlindsHttpClient.class);

    private final HttpClient httpClient;
    private final NeoSmartBlindsBridgeConfiguration config;

    public NeoSmartBlindsHttpClient(NeoSmartBlindsBridgeConfiguration config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    /**
     * Pings the NeoSmart Blinds hub to check for connectivity.
     *
     * @return true if the hub is reachable
     */
    public boolean ping() {
        String baseUrl = config.protocol + "://" + config.host + ":" + config.port;
        String url = baseUrl + "/neo/v1/ping";

        logger.debug("Pinging NeoSmart Blinds hub: {}", url);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(5)).GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // Some hubs return 200 OK with no body for a ping.
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            logger.debug("Error pinging NeoSmart Blinds hub: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Sends a command to the NeoSmart Blinds hub.
     *
     * @param blindCode The code of the blind (e.g., ID1.ID2-CHANNEL)
     * @param motorCode Optional motor code (can be empty string or null)
     * @param action The action to perform (e.g., up, down, stop)
     * @return true if the command was sent successfully
     */
    public boolean sendCommand(String blindCode, @Nullable String motorCode, String action) {
        String baseUrl = config.protocol + "://" + config.host + ":" + config.port;

        StringBuilder commandStringBuilder = new StringBuilder(blindCode);
        if (motorCode != null && !motorCode.isEmpty()) {
            commandStringBuilder.append("-").append(motorCode);
        }
        commandStringBuilder.append("-").append(action);

        String encodedCommand = URLEncoder.encode(commandStringBuilder.toString(), StandardCharsets.UTF_8);
        String encodedHubId = URLEncoder.encode(config.hubId, StandardCharsets.UTF_8);

        String url = baseUrl + "/neo/v1/transmit?command=" + encodedCommand + "&id=" + encodedHubId;

        logger.debug("Sending command to NeoSmart Blinds hub: {}", url);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(10)).GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                logger.debug("Successfully sent command: {}", response.body());
                return true;
            } else {
                logger.warn("Failed to send command to NeoSmart Blinds hub. Status code: {}, Body: {}, URL: {}",
                        response.statusCode(), response.body(), url);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error communicating with NeoSmart Blinds hub: {}, URL: {}", e.getMessage(), url);
            return false;
        }
    }
}
