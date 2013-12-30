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

import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;

/**
 * 
 * @author rcpeters
 * 
 */
public class OrcidMessageVersionConverterImplV1_2_rc1ToV1_2_rc2 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.2_rc1";
    private static final String TO_VERSION = "1.2_rc2";

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
        	if(orcidProfile.getOrcidHistory() !=null 
        			&& orcidProfile.getOrcidHistory().getCreationMethod() != null) {
        		CreationMethod c = orcidProfile.getOrcidHistory().getCreationMethod(); 
        		if (c.equals(CreationMethod.MEMBER_REFERRED) || c.equals(CreationMethod.DIRECT))
        			orcidProfile.getOrcidHistory().setCreationMethod(CreationMethod.WEBSITE);
        	}
        	
        	orcidProfile.setOrcidFundings(null);
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

    @Override
    public OrcidMessage upgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(TO_VERSION);
        return orcidMessage;
    }

}
