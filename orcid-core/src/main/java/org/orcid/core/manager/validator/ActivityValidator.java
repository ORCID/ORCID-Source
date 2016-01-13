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
import org.orcid.jaxb.model.common_rc2.Source;
import org.orcid.jaxb.model.groupid.GroupIdRecord;
import org.orcid.jaxb.model.record_rc2.Education;
import org.orcid.jaxb.model.record_rc2.Employment;
import org.orcid.jaxb.model.record_rc2.Funding;
import org.orcid.jaxb.model.record_rc2.FundingExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.FundingExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.FundingTitle;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.Relationship;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.jaxb.model.record_rc2.WorkExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.WorkExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.WorkTitle;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class ActivityValidator {

    public static void validateWork(Work work, boolean createFlag, SourceEntity sourceEntity) {
        WorkTitle title = work.getWorkTitle();
        if (title == null || title.getTitle() == null || StringUtils.isEmpty(title.getTitle().getContent())) {
            throw new ActivityTitleValidationException();
        }

        if (work.getWorkExternalIdentifiers() == null || work.getWorkExternalIdentifiers().getWorkExternalIdentifier() == null
                || work.getWorkExternalIdentifiers().getWorkExternalIdentifier().isEmpty()) {
            throw new ActivityIdentifierValidationException();
        }

        if (work.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", sourceEntity.getSourceName());
            }
            throw new InvalidPutCodeException(params);
        }
    }

    public static void validateFunding(Funding funding, SourceEntity sourceEntity, boolean createFlag) {
        FundingTitle title = funding.getTitle();
        if (title == null || title.getTitle() == null || StringUtils.isEmpty(title.getTitle().getContent())) {
            throw new ActivityTitleValidationException();
        }

        if (funding.getExternalIdentifiers() == null || funding.getExternalIdentifiers().getExternalIdentifier() == null
                || funding.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
            throw new ActivityIdentifierValidationException();
        }

        if (funding.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", sourceEntity.getSourceName());
            }
            throw new InvalidPutCodeException(params);
        }
    }

    public static void validateEmployment(Employment employment, SourceEntity sourceEntity) {
        if (employment.getPutCode() != null) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", sourceEntity.getSourceName());
            }
            throw new InvalidPutCodeException(params);
        }
    }

    public static void validateEducation(Education education, SourceEntity sourceEntity) {
        if (education.getPutCode() != null) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", sourceEntity.getSourceName());
            }
            throw new InvalidPutCodeException(params);
        }
    }

    public static void validatePeerReview(PeerReview peerReview, SourceEntity sourceEntity) {
        if (peerReview.getExternalIdentifiers() == null || peerReview.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
            throw new ActivityIdentifierValidationException();
        }

        if (peerReview.getPutCode() != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("clientName", sourceEntity.getSourceName());
            throw new InvalidPutCodeException(params);
        }
        
        if(peerReview.getType() == null) {
            throw new ActivityTypeValidationException();
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

    public static void checkExternalIdentifiers(WorkExternalIdentifiers newExtIds, WorkExternalIdentifiers existingExtIds, Source existingSource,
            SourceEntity sourceEntity) {
        if (existingExtIds != null && newExtIds != null) {
            for (WorkExternalIdentifier existingId : existingExtIds.getExternalIdentifier()) {
                for (WorkExternalIdentifier newId : newExtIds.getExternalIdentifier()) {
                    if (isDupRelationship(newId, existingId) &&
                    		isDupValue(newId, existingId) &&
                    		isDupType(newId, existingId) &&
                    		sourceEntity.getSourceId().equals(getExistingSource(existingSource))) {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("clientName", sourceEntity.getSourceName());
                        throw new OrcidDuplicatedActivityException(params);
                    }
                }
            }
        }
    }

	private static boolean isDupRelationship(WorkExternalIdentifier newId, WorkExternalIdentifier existingId) {
    	return existingId.getRelationship() != null && existingId.getRelationship().equals(Relationship.SELF) &&
    			newId.getRelationship() != null && newId.getRelationship().equals(Relationship.SELF);
	}
	
    private static boolean isDupValue(WorkExternalIdentifier newId, WorkExternalIdentifier existingId) {
		return existingId.getWorkExternalIdentifierId() != null && existingId.getWorkExternalIdentifierId().getContent() != null
				&& newId.getWorkExternalIdentifierId() != null && newId.getWorkExternalIdentifierId().getContent() != null
				&& newId.getWorkExternalIdentifierId().getContent().equals(existingId.getWorkExternalIdentifierId().getContent());
	}
    
    private static boolean isDupType(WorkExternalIdentifier newId, WorkExternalIdentifier existingId) {
		return existingId.getWorkExternalIdentifierType() != null && newId.getWorkExternalIdentifierType() != null
				&& newId.getWorkExternalIdentifierType().equals(existingId.getWorkExternalIdentifierType());
	}

	public static void checkFundingExternalIdentifiers(FundingExternalIdentifiers newExtIds, FundingExternalIdentifiers existingExtIds, Source existingSource,
            SourceEntity sourceEntity) {
        if (existingExtIds != null && newExtIds != null) {
            for (FundingExternalIdentifier existingId : existingExtIds.getExternalIdentifier()) {
                for (FundingExternalIdentifier newId : newExtIds.getExternalIdentifier()) {
                    if (existingId.getRelationship().equals(Relationship.SELF) && newId.getRelationship().equals(Relationship.SELF)
                            && newId.getValue().equals(existingId.getValue()) && newId.getType().equals(existingId.getType())
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
}
