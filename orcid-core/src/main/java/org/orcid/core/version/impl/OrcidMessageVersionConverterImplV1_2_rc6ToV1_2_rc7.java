package org.orcid.core.version.impl;

import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.OTHER_ID;
import static org.orcid.jaxb.model.message.WorkExternalIdentifierType.WOSUID;

import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;

/**
 * 
 * @author rcpeters
 * 
 */
public class OrcidMessageVersionConverterImplV1_2_rc6ToV1_2_rc7 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.2_rc6";
    private static final String TO_VERSION = "1.2_rc7";

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
            if (orcidProfile.getOrcidActivities() != null) {
                if (orcidProfile.getOrcidActivities().getOrcidWorks() != null) {
                    for (OrcidWork act : orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork())
                        downGradeActivity(act);
                }
            }
        }
    }

    private void downGradeActivity(OrcidWork orcidWork) {
        WorkExternalIdentifiers externalIdentifiers = orcidWork.getWorkExternalIdentifiers();
        if (externalIdentifiers != null) {
            for (WorkExternalIdentifier wei : externalIdentifiers.getWorkExternalIdentifier()) {
                WorkExternalIdentifierType type = wei.getWorkExternalIdentifierType();
                if (WOSUID.equals(type)) {
                    wei.setWorkExternalIdentifierType(OTHER_ID);
                }
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
        return orcidMessage;
    }

}
