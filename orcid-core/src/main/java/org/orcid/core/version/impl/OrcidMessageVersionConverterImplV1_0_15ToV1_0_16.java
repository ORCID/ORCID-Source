/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.version.impl;

import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.Orcid;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidMessageVersionConverterImplV1_0_15ToV1_0_16 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.0.15";
    private static final String TARGET_VERSION = "1.0.16";

    @Override
    public String getFromVersion() {
        return FROM_VERSION;
    }

    @Override
    public String getToVersion() {
        return TARGET_VERSION;
    }

    @Override
    public OrcidMessage downgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(FROM_VERSION);
        return orcidMessage;
    }

    @Override
    public OrcidMessage upgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(TARGET_VERSION);
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        if (orcidProfile != null) {
            orcidProfile.setOrcid((Orcid) null);
        }
        return orcidMessage;
    }

}
