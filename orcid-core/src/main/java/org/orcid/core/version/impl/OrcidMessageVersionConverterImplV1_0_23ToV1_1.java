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
import org.orcid.core.tree.TreeCleaningDecision;
import org.orcid.core.tree.TreeCleaningStrategy;
import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Orcid;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidIdBase;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.PrimaryRecord;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.WorkSource;

/**
 * 
 * @author rcpeters
 * 
 */
public class OrcidMessageVersionConverterImplV1_0_23ToV1_1 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.0.23";
    private static final String TO_VERSION = "1.1";

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
            downgradeOrcidIds(orcidProfile);
            OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
            if (orcidActivities != null) {
                downgradeAffiliations(orcidActivities.getAffiliations());
            }
        }
    }

    private void downgradeSearchResults(OrcidMessage orcidMessage) {
        OrcidSearchResults orcidSearchResults = orcidMessage.getOrcidSearchResults();
        if (orcidSearchResults != null) {
            for (OrcidSearchResult orcidSearchResult : orcidSearchResults.getOrcidSearchResult()) {
                downgradeProfile(orcidSearchResult.getOrcidProfile());
            }
        }
    }

    private void downgradeOrcidIds(OrcidProfile orcidProfile) {
        final String orcid = orcidProfile.retrieveOrcidPath();
        OrcidIdentifier orcidIdentifier = orcidProfile.getOrcidIdentifier();
        if (orcidIdentifier != null) {
            if (orcidIdentifier.getPath() != null) {
                orcidProfile.setOrcid(orcidIdentifier.getPath());
            }
            if (orcidIdentifier.getUri() != null) {
                orcidProfile.setOrcidId(orcidIdentifier.getUri());
            }
        }
        orcidProfile.setOrcidIdentifier((OrcidIdentifier) null);
        TreeCleaner treeCleaner = new TreeCleaner();
        // For backwards compatibility
        treeCleaner.setRemoveEmptyObjects(false);
        treeCleaner.clean(orcidProfile, new TreeCleaningStrategy() {
            @Override
            public TreeCleaningDecision needsStripping(Object obj) {
                if (obj instanceof OrcidIdBase) {
                    // Work sources etc.
                    OrcidIdBase orcidId = (OrcidIdBase) obj;
                    String currentValue = orcidId.getPath();
                    orcidId.setValueAsString(currentValue);
                    if (currentValue != null) {
                        orcidId.setUri(null);
                        orcidId.setPath(null);
                        orcidId.setHost(null);
                    }
                    if (obj instanceof ExternalIdentifier) {
                        ExternalIdentifier externalIdentifier = (ExternalIdentifier) obj;
                        externalIdentifier.setOrcid(new Orcid(orcid));
                    }
                } else if (obj instanceof PrimaryRecord) {
                    PrimaryRecord primaryRecord = (PrimaryRecord) obj;
                    OrcidIdentifier orcidIdentifier = primaryRecord.getOrcidIdentifier();
                    if (orcidIdentifier != null) {
                        if (orcidIdentifier.getPath() != null) {
                            primaryRecord.setOrcid(new Orcid(orcidIdentifier.getPath()));
                        }
                        if (orcidIdentifier.getUri() != null) {
                            primaryRecord.setOrcidId(new Url(orcidIdentifier.getUri()));
                        }
                    }
                    primaryRecord.setOrcidIdentifier((OrcidIdentifier) null);
                }
                // Always return default because we do not want to remove the
                // obj
                // itself
                return TreeCleaningDecision.DEFAULT;
            }
        });
    }

    private void downgradeAffiliations(Affiliations affiliations) {
        if (affiliations != null) {
            for (Iterator<Affiliation> affiliationIterator = affiliations.getAffiliation().iterator(); affiliationIterator.hasNext();) {
                Affiliation affiliation = affiliationIterator.next();
                Organization organization = affiliation.getOrganization();
                if (organization != null) {
                    OrganizationAddress address = organization.getAddress();
                    if (address != null) {
                        Iso3166Country country = address.getCountry();
                        if (Iso3166Country.XK.equals(country)) {
                            // The country code is not valid in the earlier
                            // version
                            // of the schema, so unfortunately we have to
                            // omit the affiliation
                            affiliationIterator.remove();
                        }
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
        upgradeProfile(orcidProfile);
        upgradeSearchResults(orcidMessage);

        return orcidMessage;
    }

    private void upgradeProfile(OrcidProfile orcidProfile) {
        if (orcidProfile != null) {
            OrcidActivities orcidActivities = orcidProfile.getOrcidActivities();
            if (orcidActivities != null) {
                upgradeOrcidWorks(orcidActivities.getOrcidWorks());
            }
            upgradeOrcidIds(orcidProfile);
        }
    }

    private void upgradeSearchResults(OrcidMessage orcidMessage) {
        OrcidSearchResults orcidSearchResults = orcidMessage.getOrcidSearchResults();
        if (orcidSearchResults != null) {
            for (OrcidSearchResult orcidSearchResult : orcidSearchResults.getOrcidSearchResult()) {
                upgradeProfile(orcidSearchResult.getOrcidProfile());
            }
        }
    }

    private void upgradeOrcidIds(OrcidProfile orcidProfile) {
        OrcidIdentifier newOrcidIdentifier = new OrcidIdentifier();
        orcidProfile.setOrcidIdentifier(newOrcidIdentifier);
        Orcid orcid = orcidProfile.getOrcid();
        if (orcid != null) {
            String orcidValue = orcid.getValue();
            if (StringUtils.isNotBlank(orcidValue)) {
                newOrcidIdentifier.setPath(orcidValue);
            }
        }
        String orcidId = orcidProfile.getOrcidId();
        if (StringUtils.isNotBlank(orcidId)) {
            newOrcidIdentifier.setUri(orcidId);
        }
        TreeCleaner treeCleaner = new TreeCleaner();
        treeCleaner.clean(orcidProfile, new TreeCleaningStrategy() {
            @Override
            public TreeCleaningDecision needsStripping(Object obj) {
                if (obj instanceof OrcidIdBase) {
                    // Work sources etc.
                    OrcidIdBase orcidId = (OrcidIdBase) obj;
                    String currentValue = orcidId.getValueAsString();
                    if (StringUtils.isNotBlank(currentValue)) {
                        orcidId.setPath(currentValue);
                    }
                    if (obj instanceof ExternalIdentifier) {
                        ExternalIdentifier externalIdentifier = (ExternalIdentifier) obj;
                        externalIdentifier.setOrcid(null);
                    }
                } else if (obj instanceof PrimaryRecord) {
                    PrimaryRecord primaryRecord = (PrimaryRecord) obj;
                    OrcidIdentifier newOrcidIdentifier = new OrcidIdentifier();
                    primaryRecord.setOrcidIdentifier(newOrcidIdentifier);
                    Orcid orcid = primaryRecord.getOrcid();
                    if (orcid != null) {
                        String orcidValue = orcid.getValue();
                        if (StringUtils.isNotBlank(orcidValue)) {
                            newOrcidIdentifier.setPath(orcidValue);
                        }
                    }
                    Url orcidId = primaryRecord.getOrcidId();
                    if (orcidId != null) {
                        newOrcidIdentifier.setUri(orcidId.getValue());
                    }
                }
                // Always return default because we do not want to remove the
                // obj itself
                return TreeCleaningDecision.DEFAULT;
            }
        });
    }

    private void upgradeOrcidWorks(OrcidWorks orcidWorks) {
        if (orcidWorks != null) {
            for (OrcidWork orcidWork : orcidWorks.getOrcidWork()) {
                WorkSource workSource = orcidWork.getWorkSource();
                if (workSource != null) {
                    if (WorkSource.NULL_SOURCE_PROFILE.equals(workSource.getValueAsString())) {
                        orcidWork.setWorkSource(null);
                    }
                }
            }
        }
    }

}
