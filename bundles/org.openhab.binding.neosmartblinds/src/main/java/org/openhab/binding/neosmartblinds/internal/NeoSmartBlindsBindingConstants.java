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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link NeoSmartBlindsBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Jonathan Gilbert - Initial contribution
 */
@NonNullByDefault
public class NeoSmartBlindsBindingConstants {

    public static final String BINDING_ID = "neosmartblinds";

    // List of all Thing Type UIDs
    public static final ThingTypeUID BRIDGE_TYPE_HUB = new ThingTypeUID(BINDING_ID, "hub");
    public static final ThingTypeUID THING_TYPE_BLIND = new ThingTypeUID(BINDING_ID, "blind");

    // List of all Channel ids
    public static final String CHANNEL_ROLLERSHUTTER = "rollershutter";
    public static final String CHANNEL_POSITION = "position";
    public static final String CHANNEL_FAVOURITE_1 = "favourite1";
    public static final String CHANNEL_FAVOURITE_2 = "favourite2";
}
