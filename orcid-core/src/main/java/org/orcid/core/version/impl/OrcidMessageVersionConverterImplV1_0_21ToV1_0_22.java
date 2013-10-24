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

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationAddress;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.WorkTitle;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class OrcidMessageVersionConverterImplV1_0_21ToV1_0_22 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.0.21";
    private static final String TO_VERSION = "1.0.22";

    private static final String EMPTY_TITLE = "NOT_DEFINED";

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
                downgradeAffiliations(orcidActivities);
                downgradeWorks(orcidActivities);
            }
        }

        return orcidMessage;
    }

    private void downgradeAffiliations(OrcidActivities orcidActivities) {
        Affiliations affiliations = orcidActivities.getAffiliations();
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

    private void downgradeWorks(OrcidActivities orcidActivities) {
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

                // If title is NOT_DEFINED, change it to an empty string
                if (OrcidMessageVersionConverterImplV1_0_21ToV1_0_22.EMPTY_TITLE.equals(orcidWork.getWorkTitle().getTitle()))
                    orcidWork.getWorkTitle().getTitle().setContent("");
            }
        }
    }

    @Override
    public OrcidMessage upgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(TO_VERSION);

        // Title cannot be empty, so, for backwards compatibility, set the
        // string
        // NOT_DEFINED to empty titles
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        if (orcidProfile != null) {
            OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
            if (orcidActivities != null) {
                OrcidWorks orcidWorks = orcidActivities.getOrcidWorks();
                if (orcidWorks != null) {
                    for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
                        WorkTitle workTitle = orcidWork.getWorkTitle();
                        if (workTitle == null) {
                            workTitle = new WorkTitle();
                            Title title = new Title(OrcidMessageVersionConverterImplV1_0_21ToV1_0_22.EMPTY_TITLE);
                            workTitle.setTitle(title);
                            orcidWork.setWorkTitle(workTitle);
                        } else if (workTitle.getTitle() == null) {
                            Title title = new Title(OrcidMessageVersionConverterImplV1_0_21ToV1_0_22.EMPTY_TITLE);
                            workTitle.setTitle(title);
                        } else if (StringUtils.isEmpty(workTitle.getTitle().getContent())) {
                            workTitle.getTitle().setContent(OrcidMessageVersionConverterImplV1_0_21ToV1_0_22.EMPTY_TITLE);
                        }
                    }
                }
            }
        }
        return orcidMessage;
    }

}
