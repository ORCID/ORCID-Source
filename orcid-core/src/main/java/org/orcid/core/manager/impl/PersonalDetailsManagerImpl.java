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
package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.PersonalDetailsManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.jaxb.model.common.CreditName;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.record_rc2.FamilyName;
import org.orcid.jaxb.model.record_rc2.GivenNames;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class PersonalDetailsManagerImpl implements PersonalDetailsManager {

    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    @Resource
    private SourceManager sourceManager;
    
    @Resource
    private ProfileEntityCacheManager profileEntityManager;
    
    @Resource
    private OtherNameManager otherNamesManager;
    
    @Override
    public PersonalDetails getPersonalDetails(String orcid) {
        ProfileEntity profileEntity = profileEntityManager.retrieve(orcid);
        OtherNames otherNames = otherNamesManager.getOtherNamesV2(orcid);
        PersonalDetails personalDetails = new PersonalDetails();
        if(profileEntity != null) {
            if(!PojoUtil.isEmpty(profileEntity.getCreditName())) {
                Visibility visibility = null;
                if(!PojoUtil.isEmpty(profileEntity.getNamesVisibility().value())) {
                    visibility = Visibility.fromValue(profileEntity.getNamesVisibility().value());
                }
                personalDetails.setCreditName(new CreditName(profileEntity.getCreditName(), visibility));
            }
            if(!PojoUtil.isEmpty(profileEntity.getFamilyName())) {
                FamilyName familyName = new FamilyName();
                familyName.setContent(profileEntity.getFamilyName());
                personalDetails.setFamilyName(familyName);
            }
            if(!PojoUtil.isEmpty(profileEntity.getGivenNames())){
                GivenNames givenNames = new GivenNames();
                givenNames.setContent(profileEntity.getGivenNames());
                personalDetails.setGivenNames(givenNames);
            }
            personalDetails.setOtherNames(otherNames);
        }        
        return personalDetails;
    }    
}
