package org.orcid.core.version.impl;

import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.Activity;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.SalesforceId;

/**
 * 
 * @author rcpeters
 * 
 */
public class OrcidMessageVersionConverterImplV1_2_rc4ToV1_2_rc5 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.2_rc4";
    private static final String TO_VERSION = "1.2_rc5";

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
                        externalIdentifier.setExternalIdOrcid(externalIdentifier.getExternalIdSource());
                        externalIdentifier.setExternalIdSource(null);
                    }
            if (orcidProfile.getOrcidActivities() != null) {
                if (orcidProfile.getOrcidActivities().getAffiliations() != null)
                    for (Activity act : orcidProfile.getOrcidActivities().getAffiliations().getAffiliation())
                        downGradeActivity(act);
                if (orcidProfile.getOrcidActivities().getFundings() != null)
                    for (Activity act : orcidProfile.getOrcidActivities().getFundings().getFundings())
                        downGradeActivity(act);
                if (orcidProfile.getOrcidActivities().getOrcidWorks() != null)
                    for (Activity act : orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork())
                        downGradeActivity(act);
            }
            if(orcidProfile.getOrcidInternal() != null) {
                orcidProfile.getOrcidInternal().setSalesforceId(null);
            }
        }
    }

    public void downGradeActivity(Activity act) {
        if (act.getLastModifiedDate() != null) act.setLastModifiedDate(null);
        if (act.getCreatedDate() != null) act.setCreatedDate(null);
    }
    
    private void upgradeProfile(OrcidProfile orcidProfile) {
        if (orcidProfile != null) {
            if (orcidProfile.getOrcidBio() != null)
                if (orcidProfile.getOrcidBio().getExternalIdentifiers() != null)
                    for (ExternalIdentifier externalIdentifier: orcidProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier()) { 
                        externalIdentifier.setExternalIdSource(externalIdentifier.getExternalIdOrcid());
                        externalIdentifier.setExternalIdOrcid(null);
                    }
            if(orcidProfile.getOrcidInternal() != null) 
                orcidProfile.getOrcidInternal().setSalesforceId(new SalesforceId());
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
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        upgradeProfile(orcidProfile);

        return orcidMessage;
    }    
}
