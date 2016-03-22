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

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.ActivityTitleValidationException;
import org.orcid.core.exception.ActivityTypeValidationException;
import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.jaxb.model.common_rc2.Source;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecord;
import org.orcid.jaxb.model.record_rc2.Education;
import org.orcid.jaxb.model.record_rc2.Employment;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;
import org.orcid.jaxb.model.record_rc2.Funding;
import org.orcid.jaxb.model.record_rc2.FundingTitle;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.Relationship;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.jaxb.model.record_rc2.WorkTitle;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class ActivityValidator {

    public static void validateWork(Work work, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, org.orcid.jaxb.model.message.Visibility originalVisibility) {
        WorkTitle title = work.getWorkTitle();
        if (title == null || title.getTitle() == null || StringUtils.isEmpty(title.getTitle().getContent())) {
            throw new ActivityTitleValidationException();
        }

        if (work.getWorkExternalIdentifiers() == null || work.getWorkExternalIdentifiers().getExternalIdentifier() == null
                || work.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
            throw new ActivityIdentifierValidationException();
        }

        if (work.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", sourceEntity.getSourceName());
            }
            throw new InvalidPutCodeException(params);
        }
        
        //Check that we are not changing the visibility
        if(isApiRequest && !createFlag) {
            Visibility updatedVisibility = work.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
        
        ExternalIDValidator.getInstance().validateWorkOrPeerReview(work.getExternalIdentifiers());            
    }

    public static void validateFunding(Funding funding, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, org.orcid.jaxb.model.message.Visibility originalVisibility) {
        FundingTitle title = funding.getTitle();
        if (title == null || title.getTitle() == null || StringUtils.isEmpty(title.getTitle().getContent())) {
            throw new ActivityTitleValidationException();
        }

        if (isApiRequest) {
            if (funding.getExternalIdentifiers() == null || funding.getExternalIdentifiers().getExternalIdentifier() == null
                    || funding.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
                throw new ActivityIdentifierValidationException();
            }
        }

        if (funding.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", sourceEntity.getSourceName());
            }
            throw new InvalidPutCodeException(params);
        }
        
        //Check that we are not changing the visibility
        if(isApiRequest && !createFlag) {
            Visibility updatedVisibility = funding.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
        
        ExternalIDValidator.getInstance().validateFunding(funding.getExternalIdentifiers());
    }

    public static void validateEmployment(Employment employment, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, org.orcid.jaxb.model.message.Visibility originalVisibility) {
        if (employment.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", sourceEntity.getSourceName());
            }
            throw new InvalidPutCodeException(params);
        }
        
        //Check that we are not changing the visibility
        if(isApiRequest && !createFlag) {
            Visibility updatedVisibility = employment.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
    }

    public static void validateEducation(Education education, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, org.orcid.jaxb.model.message.Visibility originalVisibility) {
        if (education.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", sourceEntity.getSourceName());
            }
            throw new InvalidPutCodeException(params);
        }
        
        //Check that we are not changing the visibility
        if(isApiRequest && !createFlag) {
            Visibility updatedVisibility = education.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
    }

    public static void validatePeerReview(PeerReview peerReview, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, org.orcid.jaxb.model.message.Visibility originalVisibility) {
        if (peerReview.getExternalIdentifiers() == null || peerReview.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
            throw new ActivityIdentifierValidationException();
        }

        if (peerReview.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("clientName", sourceEntity.getSourceName());
            throw new InvalidPutCodeException(params);
        }

        if (peerReview.getType() == null) {
            throw new ActivityTypeValidationException();
        }
        
        ExternalIDValidator.getInstance().validateWorkOrPeerReview(peerReview.getExternalIdentifiers());
        
        if (peerReview.getSubjectExternalIdentifier() != null){
            ExternalIDValidator.getInstance().validateWorkOrPeerReview(peerReview.getSubjectExternalIdentifier());            
        }
        
        //Check that we are not changing the visibility
        if(isApiRequest && !createFlag) {
            Visibility updatedVisibility = peerReview.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
    }

    public static void validateCreateGroupRecord(GroupIdRecord groupIdRecord, SourceEntity sourceEntity) {
        if (groupIdRecord.getPutCode() != null) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", sourceEntity.getSourceName());
            }
            throw new InvalidPutCodeException(params);
        }
    }

    public static void checkExternalIdentifiersForDuplicates(ExternalIDs newExtIds, ExternalIDs existingExtIds, Source existingSource,
            SourceEntity sourceEntity) {
        if (existingExtIds != null && newExtIds != null) {
            for (ExternalID existingId : existingExtIds.getExternalIdentifier()) {
                for (ExternalID newId : newExtIds.getExternalIdentifier()) {
                   if (areRelationshipsSameButNotBothPartOf(existingId.getRelationship(), newId.getRelationship())
                            && newId.equals(existingId)  
                            && sourceEntity.getSourceId().equals(getExistingSource(existingSource))) {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("clientName", sourceEntity.getSourceName());
                        throw new OrcidDuplicatedActivityException(params);
                    }
                }
            }
        }
    }
    
    private static boolean areRelationshipsSameButNotBothPartOf(Relationship r1, Relationship r2){
        if (r1 == null && r2 == null)
            return true;
        if (r1 != null && r1.equals(r2) && !r1.equals(Relationship.PART_OF))
            return true;
        return false;
    }

    public static void checkFundingExternalIdentifiersForDuplicates(ExternalIDs newExtIds, ExternalIDs existingExtIds, Source existingSource,
            SourceEntity sourceEntity) {
        if (existingExtIds != null && newExtIds != null) {
            for (ExternalID existingId : existingExtIds.getExternalIdentifier()) {
                for (ExternalID newId : newExtIds.getExternalIdentifier()) {
                    if (areRelationshipsSameButNotBothPartOf(existingId.getRelationship(), newId.getRelationship())
                            && newId.equals(existingId) 
                            && sourceEntity.getSourceId().equals(getExistingSource(existingSource))) {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("clientName", sourceEntity.getSourceName());
                        throw new OrcidDuplicatedActivityException(params);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static String getExistingSource(Source source) {
        if (source != null) {
            return (source.getSourceClientId() != null) ? source.getSourceClientId().getPath() : source.getSourceOrcid().getPath();
        }
        return null;
    }
    
    private static void validateVisibilityDoesntChange(Visibility updatedVisibility, org.orcid.jaxb.model.message.Visibility originalVisibility) {
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
