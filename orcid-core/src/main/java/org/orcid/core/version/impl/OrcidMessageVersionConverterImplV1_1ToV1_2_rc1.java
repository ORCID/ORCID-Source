package org.orcid.core.version.impl;

import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.OrcidBio;
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
public class OrcidMessageVersionConverterImplV1_1ToV1_2_rc1 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.1";
    private static final String TO_VERSION = "1.2_rc1";

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
            orcidProfile.setAffiliations(null);
            OrcidBio orcidBio = orcidProfile.getOrcidBio();
            if (orcidBio != null) {
                ContactDetails contactDetails = orcidBio.getContactDetails();
                if (contactDetails != null) {
                    for (Email email : contactDetails.getEmail()) {
                        email.setSourceClientId(null);
                    }
                }
                ExternalIdentifiers extIds = orcidBio.getExternalIdentifiers();
                if (extIds != null) {
                    for (ExternalIdentifier extId : extIds.getExternalIdentifier()) {
                        extId.setSource(null);
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

    private void downGradeActivity(OrcidWork work) {
        work.setSource(null);
    }

    private void downgradeSearchResults(OrcidMessage orcidMessage) {
        OrcidSearchResults orcidSearchResults = orcidMessage.getOrcidSearchResults();
        if (orcidSearchResults != null) {
            for (OrcidSearchResult orcidSearchResult : orcidSearchResults.getOrcidSearchResult()) {
                downgradeProfile(orcidSearchResult.getOrcidProfile());
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
