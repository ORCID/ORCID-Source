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
public class OrcidMessageVersionConverterImplV1_2_rc3ToV1_2_rc4 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.2_rc3";
    private static final String TO_VERSION = "1.2_rc4";

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
        if (orcidProfile != null) {
            if (orcidProfile.getOrcidHistory() != null) {
                // earlier versions of the XSD don;t have GroupOrcidIdentifier 
                if (orcidProfile.getOrcidHistory().getReferredBy() != null) {
                    orcidProfile.getOrcidHistory().setReferredBy(null);
                }
                if (orcidProfile.getOrcidHistory().getVerifiedEmail() != null) {
                   orcidProfile.getOrcidHistory().setVerifiedEmail(null);
                }
                if (orcidProfile.getOrcidHistory().getVerifiedPrimaryEmail() != null) {
                    orcidProfile.getOrcidHistory().setVerifiedPrimaryEmail(null);
                 }
            }
        }
    }

    private void downgradeSearchResults(OrcidMessage orcidMessage) {
        // downgrade search
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
