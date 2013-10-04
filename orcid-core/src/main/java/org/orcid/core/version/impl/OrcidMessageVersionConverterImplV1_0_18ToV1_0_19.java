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
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.WorkType;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidMessageVersionConverterImplV1_0_18ToV1_0_19 implements OrcidMessageVersionConverter {

    private static final String FROM_VERSION = "1.0.18";
    private static final String TARGET_VERSION = "1.0.19";

    @Override
    public String getFromVersion() {
        return FROM_VERSION;
    }

    @Override
    public String getToVersion() {
        return TARGET_VERSION;
    }

    @Override
    public OrcidMessage downgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(FROM_VERSION);
                
        OrcidProfile profile = orcidMessage.getOrcidProfile();
        if(profile != null){
            // Remove the work type
            OrcidActivities activites = profile.getOrcidActivities();
            if(activites != null){
                OrcidWorks works = activites.getOrcidWorks();
                if(works != null){
                    for(OrcidWork work : works.getOrcidWork()){
                        if(work.getWorkType() != null){
                            work.setWorkType(null);
                        }
                    }
                }
            }
            
            // Remove the deprecated message if exists
            if(profile.getOrcidDeprecated() != null) {
                profile.setOrcidDeprecated(null);
            }
        }                
                
        return orcidMessage;
    }

    @Override
    public OrcidMessage upgradeMessage(OrcidMessage orcidMessage) {
        if (orcidMessage == null) {
            return null;
        }
        orcidMessage.setMessageVersion(TARGET_VERSION);
        //Add work type to each work
        OrcidProfile profile = orcidMessage.getOrcidProfile();
        if(profile != null){
            OrcidActivities activites = profile.getOrcidActivities();
            if(activites != null){
                OrcidWorks works = activites.getOrcidWorks();
                if(works != null){
                    for(OrcidWork work : works.getOrcidWork()){
                        if(work.getWorkType() == null){
                            work.setWorkType(WorkType.UNDEFINED);
                        }
                    }
                }
            }
        }
        
        return orcidMessage;
    }
}
