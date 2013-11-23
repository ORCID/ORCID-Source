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
import org.orcid.core.tree.TreeCleaner;
import org.orcid.core.tree.TreeCleaningStrategy;
import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationAddress;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Orcid;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidId;
import org.orcid.jaxb.model.message.OrcidIdBase;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.WorkSource;
import org.orcid.utils.OrcidStringUtils;

/**
 * 
 * @author rcpeters
 * 
 */
public class OrcidMessageVersionConverterImplV1_0_23ToV1_1_0 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.0.23";
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
            downgradeOrcidIds(orcidProfile);
            OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
            if (orcidActivities != null) {
                downgradeAffiliations(orcidActivities.getAffiliations());
            }
        }

        return orcidMessage;
    }

    private void downgradeOrcidIds(OrcidProfile orcidProfile) {
        final String orcid = orcidProfile.retrieveOrcidPath();
        OrcidId orcidId = orcidProfile.getOrcidId();
        if (orcidId != null && orcidId.getPath() != null) {
            orcidProfile.setOrcid(orcidId.getPath());
        }
        TreeCleaner treeCleaner = new TreeCleaner();
        // For backwards compatibility
        treeCleaner.setRemoveEmptyObjects(false);
        treeCleaner.clean(orcidProfile, new TreeCleaningStrategy() {
            @Override
            public boolean needsStripping(Object obj) {
                if (obj instanceof OrcidId) {
                    // The main ID for the record
                    OrcidId orcidId = (OrcidId) obj;
                    String currentValue = orcidId.getUri();
                    orcidId.setValue(currentValue);
                    orcidId.setUri(null);
                    orcidId.setPath(null);
                    orcidId.setHost(null);
                } else if (obj instanceof OrcidIdBase) {
                    // Work sources etc.
                    OrcidIdBase orcidId = (OrcidIdBase) obj;
                    String currentValue = orcidId.getPath();
                    orcidId.setValue(currentValue);
                    orcidId.setUri(null);
                    orcidId.setPath(null);
                    orcidId.setHost(null);
                    if (obj instanceof ExternalIdentifier) {
                        ExternalIdentifier externalIdentifier = (ExternalIdentifier) obj;
                        externalIdentifier.setOrcid(new Orcid(orcid));
                    }
                }
                // Always return false because we do not want to remove the obj
                // itself
                return false;
            }
        });
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

        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        if (orcidProfile != null) {
            OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
            if (orcidActivities != null) {
                upgradeOrcidWorks(orcidActivities.getOrcidWorks());
            }
            upgradeOrcidIds(orcidProfile);
        }

        return orcidMessage;
    }

    private void upgradeOrcidIds(OrcidProfile orcidProfile) {
        Orcid orcid = orcidProfile.getOrcid();
        if (orcid != null) {
            String orcidValue = orcid.getValue();
            if (StringUtils.isNotBlank(orcidValue)) {
                OrcidId existingOrcidId = orcidProfile.getOrcidId();
                OrcidId orcidId = existingOrcidId != null ? existingOrcidId : new OrcidId();
                orcidId.setPath(orcidValue);
                orcidProfile.setOrcidId(orcidId);
            }
        }
        TreeCleaner treeCleaner = new TreeCleaner();
        treeCleaner.clean(orcidProfile, new TreeCleaningStrategy() {
            @Override
            public boolean needsStripping(Object obj) {
                if (obj instanceof OrcidId) {
                    // The main ID for the record
                    OrcidId orcidId = (OrcidId) obj;
                    String currentValue = orcidId.getValue();
                    orcidId.setUri(currentValue);
                    if (currentValue != null) {
                        orcidId.setPath(OrcidStringUtils.getOrcidNumber(currentValue));
                    }
                } else if (obj instanceof OrcidIdBase) {
                    // Work sources etc.
                    OrcidIdBase orcidId = (OrcidIdBase) obj;
                    String currentValue = orcidId.getValue();
                    orcidId.setPath(currentValue);
                    if (obj instanceof ExternalIdentifier) {
                        ExternalIdentifier externalIdentifier = (ExternalIdentifier) obj;
                        externalIdentifier.setOrcid(null);
                    }
                }
                // Always return false because we do not want to remove the obj
                // itself
                return false;
            }
        });
    }

    private void upgradeOrcidWorks(OrcidWorks orcidWorks) {
        if (orcidWorks != null) {
            for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
                WorkSource workSource = orcidWork.getWorkSource();
                if (workSource != null) {
                    if (WorkSource.NULL_SOURCE_PROFILE.equals(workSource.getValue())) {
                        orcidWork.setWorkSource(null);
                    }
                }
            }
        }
    }

}
