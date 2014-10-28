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
package org.orcid.core.version.impl;

import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.Activity;
import org.orcid.jaxb.model.message.ExternalIdSource;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SourceOrcid;
import org.orcid.jaxb.model.message.WorkSource;

/**
 * 
 * @author rcpeters
 * 
 */
public class OrcidMessageVersionConverterImplV1_2_rc5ToV1_2_rc6 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.2_rc5";
    private static final String TO_VERSION = "1.2_rc6";

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
            if (orcidProfile.getOrcidBio() != null)
                if (orcidProfile.getOrcidBio().getExternalIdentifiers() != null)
                    for (ExternalIdentifier externalIdentifier : orcidProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier()) {
                        Source source = externalIdentifier.getSource();
                        if (source != null) {
                            SourceOrcid sourceOrcid = source.getSourceOrcid();
                            if (sourceOrcid != null) {
                                externalIdentifier.setSource(null);
                                externalIdentifier.setExternalIdSource(new ExternalIdSource(sourceOrcid));
                            }
                        }
                    }
            if (orcidProfile.getOrcidActivities() != null) {
                if (orcidProfile.getOrcidActivities().getOrcidWorks() != null) {
                    for (OrcidWork act : orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork())
                        downGradeActivity(act);
                }
            }
        }
    }

    private void downGradeActivity(OrcidWork orcidWork) {
        Source source = orcidWork.getSource();
        if (source != null) {
            SourceOrcid sourceOrcid = source.getSourceOrcid();
            if (sourceOrcid != null) {
                orcidWork.setSource(null);
                orcidWork.setWorkSource(new WorkSource(sourceOrcid));
            }
        }
    }

    private void downgradeSearchResults(OrcidMessage orcidMessage) {
        OrcidSearchResults searchResults = orcidMessage.getOrcidSearchResults();
        if (searchResults != null) {
            for (OrcidSearchResult searchResult : searchResults.getOrcidSearchResult()) {
                downgradeProfile(searchResult.getOrcidProfile());
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
            if (orcidProfile.getOrcidBio() != null) {
                if (orcidProfile.getOrcidBio().getExternalIdentifiers() != null)
                    for (ExternalIdentifier externalIdentifier : orcidProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier()) {
                        ExternalIdSource eis = externalIdentifier.getExternalIdSource();
                        if (eis != null) {
                            externalIdentifier.setSource(new Source(eis.getPath()));
                            externalIdentifier.setExternalIdSource(null);
                        }
                    }
            }
        }
        if (orcidProfile.getOrcidActivities() != null) {
            if (orcidProfile.getOrcidActivities().getOrcidWorks() != null) {
                for (OrcidWork act : orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork())
                    upgradeActivity(act);
            }
        }
    }

    private void upgradeActivity(OrcidWork orcidWork) {
        WorkSource workSource = orcidWork.getWorkSource();
        if (workSource != null) {
            Source source = new Source();
            source.setSourceOrcid(new SourceOrcid(workSource));
        }
    }

    private void upgradeSearchResults(OrcidMessage orcidMessage) {
        OrcidSearchResults searchResults = orcidMessage.getOrcidSearchResults();
        if (searchResults != null) {
            for (OrcidSearchResult searchResult : searchResults.getOrcidSearchResult()) {
                upgradeProfile(searchResult.getOrcidProfile());
            }
        }
    }

}
