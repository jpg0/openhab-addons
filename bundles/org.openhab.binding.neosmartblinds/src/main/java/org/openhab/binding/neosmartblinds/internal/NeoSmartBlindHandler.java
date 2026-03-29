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

import static org.openhab.binding.neosmartblinds.internal.NeoSmartBlindsBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.StopMoveType;
import org.openhab.core.library.types.UpDownType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link NeoSmartBlindHandler} is responsible for handling commands for an individual blind.
 *
 * @author Jonathan Gilbert - Initial contribution
 */
@NonNullByDefault
public class NeoSmartBlindHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(NeoSmartBlindHandler.class);

    private @Nullable NeoSmartBlindsBlindConfiguration config;

    public NeoSmartBlindHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        config = getConfigAs(NeoSmartBlindsBlindConfiguration.class);

        if (config.blindCode.isEmpty()) {
            updateStatus(ThingStatus.OFFLINE, org.openhab.core.thing.ThingStatusDetail.CONFIGURATION_ERROR,
                    "Blind Code must be configured.");
            return;
        }

        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        NeoSmartBlindsBridgeHandler bridgeHandler = getBridgeHandler();
        if (bridgeHandler == null || config == null) {
            return;
        }

        String action = null;
        String channelId = channelUID.getId();

        if (CHANNEL_ROLLERSHUTTER.equals(channelId)) {
            if (command instanceof UpDownType) {
                action = (command == UpDownType.UP) ? "up" : "down";
            } else if (command instanceof StopMoveType) {
                action = "sp";
            } else if (command instanceof PercentType) {
                action = ((PercentType) command).toString() + "-gp";
            }
        } else if (CHANNEL_POSITION.equals(channelId)) {
            if (command instanceof PercentType) {
                action = ((PercentType) command).toString() + "-gp";
            } else if (command instanceof DecimalType) {
                action = Integer.toString(((DecimalType) command).intValue()) + "-gp";
            }
        } else if (CHANNEL_FAVOURITE_1.equals(channelId)) {
            if (command == OnOffType.ON) {
                action = "fav_1"; // Or whatever the code is for favorite 1
            }
        } else if (CHANNEL_FAVOURITE_2.equals(channelId)) {
            if (command == OnOffType.ON) {
                action = "fav_2";
            }
        }

        if (action != null) {
            bridgeHandler.sendCommand(config.blindCode, config.motorCode, action);
        }
    }

    private @Nullable NeoSmartBlindsBridgeHandler getBridgeHandler() {
        org.openhab.core.thing.Bridge bridge = getBridge();
        if (bridge == null) {
            return null;
        }
        org.openhab.core.thing.binding.ThingHandler handler = bridge.getHandler();
        if (handler instanceof NeoSmartBlindsBridgeHandler) {
            return (NeoSmartBlindsBridgeHandler) handler;
        }
        return null;
    }
}
