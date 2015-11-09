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
package org.orcid.core.manager.validator;

import java.util.HashMap;
import java.util.Map;

import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.exception.PutCodeRequiredException;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc1.ResearcherUrl;
import org.orcid.persistence.constants.SiteConstants;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonValidator.class);
    
    public static void validateResearcherUrl(ResearcherUrl researcherUrl, SourceEntity sourceEntity, boolean createFlag) {
        if(researcherUrl == null)
            return;
        
        if(PojoUtil.isEmpty(researcherUrl.getUrl().getValue())) {
            String message = "Url field must not be empty";
            LOGGER.error(message);
            throw new OrcidValidationException(message);
        } else {
            if(SiteConstants.URL_MAX_LENGTH < researcherUrl.getUrl().getValue().length()) {
                String message = "Url field must not be longer than " + SiteConstants.URL_MAX_LENGTH + " characters";
                LOGGER.error(message);
                throw new OrcidValidationException(message);
            }
        }
        
        
        if(PojoUtil.isEmpty(researcherUrl.getUrlName())) {
            String message = "Url name must not be empty";
            LOGGER.error(message);
            throw new OrcidValidationException(message);
        } else {
            if(SiteConstants.URL_MAX_LENGTH < researcherUrl.getUrlName().length()) {
                String message = "Url name must not be longer than " + SiteConstants.URL_MAX_LENGTH + " characters";
                LOGGER.error(message);
                throw new OrcidValidationException(message);
            }
        }
        if(createFlag) {
            if(researcherUrl.getPutCode() != null) {
                Map<String, String> params = new HashMap<String, String>();
                if (sourceEntity != null) {
                    params.put("clientName", sourceEntity.getSourceName());
                }
                throw new InvalidPutCodeException(params);
            }                        
        } else {
            if(researcherUrl.getPutCode() == null) {
                Map<String, String> params = new HashMap<String, String>();                
                throw new PutCodeRequiredException(params);
            }
        }
    }
    
    public static void validateOtherName(OtherName otherName, SourceEntity sourceEntity, boolean createFlag) {
        if(createFlag) {
            if(otherName.getPutCode() != null) {
                Map<String, String> params = new HashMap<String, String>();
                if (sourceEntity != null) {
                    params.put("clientName", sourceEntity.getSourceName());
                }
                throw new InvalidPutCodeException(params);
            }                        
        } else {
            if(otherName.getPutCode() == null) {
                Map<String, String> params = new HashMap<String, String>();                
                throw new PutCodeRequiredException(params);
            }
        }
    }

}
