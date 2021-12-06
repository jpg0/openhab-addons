/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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

package org.openhab.automation.jsscripting.internal.fs.watch;

import java.io.File;

import org.openhab.core.OpenHAB;
import org.openhab.core.automation.module.script.rulesupport.loader.DependencyTracker;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * Tracks JS module dependencies
 *
 * @author Jonathan Gilbert - Initial contribution
 */
@Component(immediate = true, service = JSDependencyTracker.class)
public class JSDependencyTracker extends DependencyTracker {

    public static final String LIB_PATH = String.join(File.separator, OpenHAB.getConfigFolder(), "automation", "js",
            "node_modules");

    public JSDependencyTracker() {
        super(LIB_PATH);
    }

    @Activate
    public void activate() {
        super.activate();
    }

    @Deactivate
    public void deactivate() {
        super.deactivate();
    }
}
