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
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.persistence.constants.SiteConstants;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonValidator.class);
    
    public static void validateResearcherUrl(ResearcherUrl researcherUrl, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
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
        
        //Check that we are not changing the visibility
        if(isApiRequest && !createFlag) {
            Visibility updatedVisibility = researcherUrl.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
    }
    
    public static void validateOtherName(OtherName otherName, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
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
        
        //Check that we are not changing the visibility
        if(isApiRequest && !createFlag) {
            Visibility updatedVisibility = otherName.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
    }
    
    public static void validateExternalIdentifier(PersonExternalIdentifier externalIdentifier, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        //Validate common name not empty
        if(PojoUtil.isEmpty(externalIdentifier.getType())) {
            String message = "Common name field must not be empty";
            LOGGER.error(message);
            throw new OrcidValidationException(message);
        } else {
            if(SiteConstants.MAX_LENGTH_255 < externalIdentifier.getType().length()) {
                String message = "Common name field must not be longer than " + SiteConstants.MAX_LENGTH_255 + " characters";
                LOGGER.error(message);
                throw new OrcidValidationException(message);
            }
        }
                
        //Validate reference not empty
        if(PojoUtil.isEmpty(externalIdentifier.getValue())) {
            String message = "Reference field must not be empty";
            LOGGER.error(message);
            throw new OrcidValidationException(message);
        } else {
            if(SiteConstants.MAX_LENGTH_255 < externalIdentifier.getValue().length()) {
                String message = "Reference field must not be longer than " + SiteConstants.MAX_LENGTH_255 + " characters";
                LOGGER.error(message);
                throw new OrcidValidationException(message);
            }
        }
        
        //Validate url not empty        
        if(PojoUtil.isEmpty(externalIdentifier.getUrl())) {
            String message = "Reference field must not be empty";
            LOGGER.error(message);
            throw new OrcidValidationException(message);
        } else {
            if(SiteConstants.URL_MAX_LENGTH < externalIdentifier.getUrl().getValue().length()) {
                String message = "Url must not be longer than " + SiteConstants.URL_MAX_LENGTH + " characters";
                LOGGER.error(message);
                throw new OrcidValidationException(message);
            }
        }
        
        if(createFlag) {
            if(externalIdentifier.getPutCode() != null) {
                Map<String, String> params = new HashMap<String, String>();
                if (sourceEntity != null) {
                    params.put("clientName", sourceEntity.getSourceName());
                }
                throw new InvalidPutCodeException(params);
            }                        
        } else {
            if(externalIdentifier.getPutCode() == null) {
                Map<String, String> params = new HashMap<String, String>();                
                throw new PutCodeRequiredException(params);
            }
        }
        
        //Check that we are not changing the visibility
        if(isApiRequest && !createFlag) {
            Visibility updatedVisibility = externalIdentifier.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }        
    }
    
    public static void validateKeyword(Keyword keyword, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        if(createFlag) {
            if(keyword.getPutCode() != null) {
                Map<String, String> params = new HashMap<String, String>();
                if (sourceEntity != null) {
                    params.put("clientName", sourceEntity.getSourceName());
                }
                throw new InvalidPutCodeException(params);
            }                        
        } else {
            if(keyword.getPutCode() == null) {
                Map<String, String> params = new HashMap<String, String>();                
                throw new PutCodeRequiredException(params);
            }
        }
        
        if(PojoUtil.isEmpty(keyword.getContent())) {
            String message = "Keyword cannot be null";
            LOGGER.error(message);
            throw new OrcidValidationException(message);
        }
        
        //Check that we are not changing the visibility
        if(isApiRequest && !createFlag) {
            Visibility updatedVisibility = keyword.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
    }
    
    public static void validateAddress(Address address, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        if(createFlag) {
            if(address.getPutCode() != null) {
                Map<String, String> params = new HashMap<String, String>();
                if (sourceEntity != null) {
                    params.put("clientName", sourceEntity.getSourceName());
                }
                throw new InvalidPutCodeException(params);
            }                        
        } else {
            if(address.getPutCode() == null) {
                Map<String, String> params = new HashMap<String, String>();                
                throw new PutCodeRequiredException(params);
            }
        }
        
        if(address.getCountry() == null || address.getCountry().getValue() == null) {
            String message = "Country cannot be null";
            LOGGER.error(message);
            throw new OrcidValidationException(message);
        }
        
        if(address.getVisibility() == null) {
            String message = "Visibility cannot be null";
            LOGGER.error(message);
            throw new OrcidValidationException(message);
        }
        
        //Check that we are not changing the visibility
        if(isApiRequest && !createFlag) {
            Visibility updatedVisibility = address.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
    }

    private static void validateVisibilityDoesntChange(Visibility updatedVisibility, Visibility originalVisibility) {
        if(updatedVisibility != null) {
            if(originalVisibility == null) {
                throw new VisibilityMismatchException();
            }
            if(!updatedVisibility.value().equals(originalVisibility.value())) {
                throw new VisibilityMismatchException();
            }
        }        
    }
        
}
