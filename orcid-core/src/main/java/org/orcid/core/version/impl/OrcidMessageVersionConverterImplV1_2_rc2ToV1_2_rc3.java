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

import org.orcid.core.adapter.impl.Jpa2JaxbAdapterImpl;
import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.OrcidWork;

/**
 * 
 * @author rcpeters
 * 
 */
public class OrcidMessageVersionConverterImplV1_2_rc2ToV1_2_rc3 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.2_rc2";
    private static final String TO_VERSION = "1.2_rc3";

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
        downgradeProfile(orcidProfile);
        downgradeSearchResults(orcidMessage);

        return orcidMessage;
    }

    private void downgradeProfile(OrcidProfile orcidProfile) {
        // nothing to downgrade
    }

    private void downgradeSearchResults(OrcidMessage orcidMessage) {
        // downgrade search
    }

    @Override
    public OrcidMessage upgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        if (orcidMessage.getOrcidProfile() != null) {
            OrcidProfile op = orcidMessage.getOrcidProfile();
            if (op.getOrcidActivities() != null && op.getOrcidActivities().getOrcidWorks() != null) {
                for (OrcidWork ow : op.getOrcidActivities().getOrcidWorks().getOrcidWork()) {
                    ow.setLanguageCode(Jpa2JaxbAdapterImpl.normalizeLanguageCode(ow.getLanguageCode()));
                    if (ow.getWorkTitle() != null && ow.getWorkTitle().getTranslatedTitle() != null)
                        ow.getWorkTitle().getTranslatedTitle().setLanguageCode(ow.getWorkTitle().getTranslatedTitle().getLanguageCode());
                }
            }
        }
        orcidMessage.setMessageVersion(TO_VERSION);
        return orcidMessage;
    }

}
