package org.orcid.core.manager.v3.validator;

import java.util.HashMap;
import java.util.Map;

import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.exception.PutCodeRequiredException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.common.VisibilityType;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.persistence.constants.SiteConstants;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonValidator.class);
    
    public static void validateResearcherUrl(ResearcherUrl researcherUrl, Source activeSource, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
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
        
        if(!PojoUtil.isEmpty(researcherUrl.getUrlName())) {
            if(SiteConstants.URL_MAX_LENGTH < researcherUrl.getUrlName().length()) {
                String message = "Url name must not be longer than " + SiteConstants.URL_MAX_LENGTH + " characters";
                LOGGER.error(message);
                throw new OrcidValidationException(message);
            }
        }
        if(createFlag) {
            if(researcherUrl.getPutCode() != null) {
                throw InvalidPutCodeException.forSource(activeSource);
            }                        
        } else {
            if(researcherUrl.getPutCode() == null) {
                Map<String, String> params = new HashMap<String, String>();                
                throw new PutCodeRequiredException(params);
            }
        }
        
        //Check that we are not changing the visibility
        validateAndFixVisibility(researcherUrl, createFlag, isApiRequest, originalVisibility);
    }
    
    public static void validateOtherName(OtherName otherName, Source activeSource, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        if(createFlag) {
            if(otherName.getPutCode() != null) {
                throw InvalidPutCodeException.forSource(activeSource);
            }                        
        } else {
            if(otherName.getPutCode() == null) {
                Map<String, String> params = new HashMap<String, String>();                
                throw new PutCodeRequiredException(params);
            }
        }
        
        //Check that we are not changing the visibility
        validateAndFixVisibility(otherName, createFlag, isApiRequest, originalVisibility);
    }
    
    public static void validateExternalIdentifier(PersonExternalIdentifier externalIdentifier, Source activeSource, boolean createFlag, boolean isApiRequest, Visibility originalVisibility, boolean requireRelationshipOnExternalIdentifier) {
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
        
        //Validate relationship
        if(requireRelationshipOnExternalIdentifier) {
            if(externalIdentifier.getRelationship() == null || !Relationship.SELF.equals(externalIdentifier.getRelationship())) {
                String message = "Relationship field should be self for person identifiers";
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
                throw InvalidPutCodeException.forSource(activeSource);
            }                        
        } else {
            if(externalIdentifier.getPutCode() == null) {
                Map<String, String> params = new HashMap<String, String>();                
                throw new PutCodeRequiredException(params);
            }
        }
        
        //Check that we are not changing the visibility
        validateAndFixVisibility(externalIdentifier, createFlag, isApiRequest, originalVisibility);        
    }
    
    public static void validateKeyword(Keyword keyword, Source activeSource, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        if(createFlag) {
            if(keyword.getPutCode() != null) {
                throw InvalidPutCodeException.forSource(activeSource);
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
        validateAndFixVisibility(keyword, createFlag, isApiRequest, originalVisibility);
    }
    
    public static void validateAddress(Address address, Source activeSource, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        if(createFlag) {
            if(address.getPutCode() != null) {
                throw InvalidPutCodeException.forSource(activeSource);
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
        
        //Check that we are not changing the visibility
        validateAndFixVisibility(address, createFlag, isApiRequest, originalVisibility);
    }

    /**
     * Validates the the incoming request doesn't change the visibility of an
     * updated element. It also checks if the incoming visibility is null and
     * fix it to the original visibility. This method will be executed only in
     * the case that is not a new element (createFlag is false) and is an api
     * request (isApiRequest is true)
     * 
     * @param element
     * @param createFlag
     * @param isApiRequest
     * @param originalVisibility
     * 
     */
    private static void validateAndFixVisibility(VisibilityType element, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        if (isApiRequest && !createFlag) {
            Visibility updatedVisibility = element.getVisibility();
            if (updatedVisibility == null) {
                element.setVisibility(originalVisibility);
            } else {
                validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
            }
        }
    }
    
    /**
     * Throws an exception if the updatedVisibility is different than the original visibility
     * 
     * @param updatedVisibility
     * @param originalVisibility
     * */    
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
