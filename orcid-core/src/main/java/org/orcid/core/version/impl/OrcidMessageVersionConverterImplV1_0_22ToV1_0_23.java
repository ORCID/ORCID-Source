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

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.WorkTitle;

/**
 * 
 * @author rcpeters
 * 
 */
public class OrcidMessageVersionConverterImplV1_0_22ToV1_0_23 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.0.22";
    private static final String TO_VERSION = "1.0.23";
    
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

        return orcidMessage;
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
            
            // Only education and employment affiliations are allowed
            if (orcidActivities != null && orcidActivities.getAffiliations() != null) {
                Affiliations affs = orcidActivities.getAffiliations();
                if (affs != null) {
                    for(Affiliation aff:affs.getAffiliation()) {
                        if (aff.getAffiliationType() == null)
                            affs.getAffiliation().remove(aff);
                        else if ( !(aff.getAffiliationType().equals(AffiliationType.EDUCATION) || aff.getAffiliationType().equals(AffiliationType.EMPLOYMENT)))
                            affs.getAffiliation().remove(aff);
                    }
                }
            }
        }
        return orcidMessage;
    }

}
