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
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class OrcidMessageVersionConverterImplV1_0_21ToV1_0_22 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.0.21";
    private static final String TO_VERSION = "1.0.22";

    @Override
    public String getFromVersion() {
        return FROM_VERSION;
    }

    @Override
    public String getToVersion() {
        return TO_VERSION;
    }

    @Override
    public OrcidMessage downgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(FROM_VERSION);

        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        if (orcidProfile != null) {
            OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
            if (orcidActivities != null) {
                OrcidWorks orcidWorks = orcidActivities.getOrcidWorks();
                if (orcidWorks != null) {
                    for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
                        // Remove the translated title
                        if (orcidWork.getWorkTitle() != null) {
                            orcidWork.getWorkTitle().setTranslatedTitle(null);
                        }

                        // Remove the language code
                        orcidWork.setLanguageCode(null);

                        // Remove the location
                        orcidWork.setCountry(null);
                    }
                }
            }
        }

        return orcidMessage;
    }

    @Override
    public OrcidMessage upgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(TO_VERSION);
        return orcidMessage;
    }

}
