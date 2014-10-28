/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.orcid.core.manager.AppIdGenerationManager;

/**
 * 
 * @author Will Simpson
 * 
 */
public class AppIdGenerationManagerImpl implements AppIdGenerationManager {

    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public String createNewAppId() {
        return "APP-" + RandomStringUtils.random(16, CHARS);
    }

}
