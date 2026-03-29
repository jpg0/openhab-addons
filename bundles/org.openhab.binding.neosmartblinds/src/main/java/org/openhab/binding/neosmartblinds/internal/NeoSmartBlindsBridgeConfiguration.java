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

/**
 * The {@link NeoSmartBlindsBridgeConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Jonathan Gilbert - Initial contribution
 */
@NonNullByDefault
public class NeoSmartBlindsBridgeConfiguration {

    public String host = "";
    public String hubId = "";
    public String protocol = "http";
    public int port = 8838;
    public int commandDelay = 600;
}
