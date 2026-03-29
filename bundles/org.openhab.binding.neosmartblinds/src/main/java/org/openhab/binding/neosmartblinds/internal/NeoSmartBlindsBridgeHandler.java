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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NeoSmartBlindsBridgeHandler} is responsible for handling commands for the NeoSmart Blinds hub.
 *
 * @author Jonathan Gilbert - Initial contribution
 */
@NonNullByDefault
public class NeoSmartBlindsBridgeHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(NeoSmartBlindsBridgeHandler.class);

    private @Nullable NeoSmartBlindsHttpClient httpClient;
    private final Queue<CommandRequest> commandQueue = new ConcurrentLinkedQueue<>();
    private @Nullable ScheduledFuture<?> processingTask;
    private int commandDelay = 600;

    private static class CommandRequest {
        final String blindCode;
        final @Nullable String motorCode;
        final String action;

        CommandRequest(String blindCode, @Nullable String motorCode, String action) {
            this.blindCode = blindCode;
            this.motorCode = motorCode;
            this.action = action;
        }
    }

    public NeoSmartBlindsBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void initialize() {
        NeoSmartBlindsBridgeConfiguration config = getConfigAs(NeoSmartBlindsBridgeConfiguration.class);

        if (config.host.isEmpty() || config.hubId.isEmpty()) {
            updateStatus(ThingStatus.OFFLINE, org.openhab.core.thing.ThingStatusDetail.CONFIGURATION_ERROR,
                    "Host and Hub ID must be configured.");
            return;
        }

        commandDelay = config.commandDelay;
        httpClient = new NeoSmartBlindsHttpClient(config);

        // Background initialization to check if the hub is reachable
        scheduler.execute(() -> {
            try {
                if (httpClient != null && httpClient.ping()) {
                    updateStatus(ThingStatus.ONLINE);
                } else {
                    updateStatus(ThingStatus.OFFLINE, org.openhab.core.thing.ThingStatusDetail.COMMUNICATION_ERROR,
                            "Hub not reachable or incorrect configuration.");
                }
            } catch (Exception e) {
                updateStatus(ThingStatus.OFFLINE, org.openhab.core.thing.ThingStatusDetail.COMMUNICATION_ERROR,
                        "Error connecting to hub: " + e.getMessage());
            }
        });
    }

    @Override
    public void dispose() {
        if (processingTask != null) {
            processingTask.cancel(true);
        }
        super.dispose();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // Bridge itself doesn't have channels yet
    }

    /**
     * Queues a command to be sent to the hub.
     *
     * @param blindCode The code of the blind.
     * @param motorCode Optional motor code.
     * @param action The action (up, down, stop).
     * @return true if the command was queued.
     */
    public boolean sendCommand(String blindCode, @Nullable String motorCode, String action) {
        commandQueue.add(new CommandRequest(blindCode, motorCode, action));
        startProcessing();
        return true;
    }

    private synchronized void startProcessing() {
        if (processingTask == null || processingTask.isDone()) {
            processingTask = scheduler.schedule(this::processNextCommand, 0, TimeUnit.MILLISECONDS);
        }
    }

    private void processNextCommand() {
        CommandRequest request = commandQueue.poll();
        if (request != null && httpClient != null) {
            logger.debug("Processing queued command: {} for {} (motor: {})", request.action, request.blindCode,
                    request.motorCode);
            httpClient.sendCommand(request.blindCode, request.motorCode, request.action);

            if (!commandQueue.isEmpty()) {
                // If there are more commands, schedule the next one after the delay
                processingTask = scheduler.schedule(this::processNextCommand, commandDelay, TimeUnit.MILLISECONDS);
            }
        }
    }
}
