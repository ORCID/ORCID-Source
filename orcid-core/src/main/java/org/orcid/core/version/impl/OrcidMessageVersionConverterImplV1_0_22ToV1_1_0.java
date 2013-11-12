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

import java.util.Iterator;

import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationAddress;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class OrcidMessageVersionConverterImplV1_0_22ToV1_1_0 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.0.22";
    private static final String TO_VERSION = "1.1.0";

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
                downgradeAffiliations(orcidActivities.getAffiliations());
            }
        }

        return orcidMessage;
    }

    private void downgradeAffiliations(Affiliations affiliations) {
        if (affiliations != null) {
            for (Iterator<Affiliation> affiliationIterator = affiliations.getAffiliation().iterator(); affiliationIterator.hasNext();) {
                Affiliation affiliation = affiliationIterator.next();
                AffiliationAddress address = affiliation.getAffiliationAddress();
                if (address != null) {
                    Iso3166Country country = address.getAffiliationCountry().getValue();
                    if (Iso3166Country.XK.equals(country)) {
                        // The country code is not valid in the earlier version
                        // of the schema, so unfortunately we have to
                        // omit the affiliation
                        affiliationIterator.remove();
                    }
                }
            }
        }
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
