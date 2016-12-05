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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.ActivityTitleValidationException;
import org.orcid.core.exception.ActivityTypeValidationException;
import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.jaxb.model.common_rc4.Amount;
import org.orcid.jaxb.model.common_rc4.Contributor;
import org.orcid.jaxb.model.common_rc4.ContributorOrcid;
import org.orcid.jaxb.model.common_rc4.Day;
import org.orcid.jaxb.model.common_rc4.Iso3166Country;
import org.orcid.jaxb.model.common_rc4.Month;
import org.orcid.jaxb.model.common_rc4.PublicationDate;
import org.orcid.jaxb.model.common_rc4.Source;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.common_rc4.Year;
import org.orcid.jaxb.model.groupid_rc4.GroupIdRecord;
import org.orcid.jaxb.model.record_rc4.CitationType;
import org.orcid.jaxb.model.record_rc4.Education;
import org.orcid.jaxb.model.record_rc4.Employment;
import org.orcid.jaxb.model.record_rc4.ExternalID;
import org.orcid.jaxb.model.record_rc4.ExternalIDs;
import org.orcid.jaxb.model.record_rc4.Funding;
import org.orcid.jaxb.model.record_rc4.FundingTitle;
import org.orcid.jaxb.model.record_rc4.PeerReview;
import org.orcid.jaxb.model.record_rc4.PeerReviewType;
import org.orcid.jaxb.model.record_rc4.Relationship;
import org.orcid.jaxb.model.record_rc4.Work;
import org.orcid.jaxb.model.record_rc4.WorkContributors;
import org.orcid.jaxb.model.record_rc4.WorkTitle;
import org.orcid.jaxb.model.record_rc4.WorkType;
import org.orcid.persistence.constants.SiteConstants;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;

public class ActivityValidator {

    @Resource
    private ExternalIDValidator externalIDValidator;

    public void validateWork(Work work, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, org.orcid.jaxb.model.message.Visibility originalVisibility) {
        WorkTitle title = work.getWorkTitle();
        if (title == null || title.getTitle() == null || StringUtils.isEmpty(title.getTitle().getContent())) {
            throw new ActivityTitleValidationException();
        }

        if(work.getCountry() != null) {
            if(work.getCountry().getValue() == null) {
                Map<String, String> params = new HashMap<String, String>();
                String values = Arrays.stream(Iso3166Country.values()).map(element -> element.value()).collect(Collectors.joining(", "));
                params.put("type", "country");
                params.put("values", values);
                throw new ActivityTypeValidationException(params);
            }
        }
        
        //translated title language code
        if(title != null && title.getTranslatedTitle() != null) {
            String translatedTitle = title.getTranslatedTitle().getContent();
            String languageCode = title.getTranslatedTitle().getLanguageCode();
            
            if(PojoUtil.isEmpty(translatedTitle) && !PojoUtil.isEmpty(languageCode)) {
                throw new OrcidValidationException("Please specify a translated title or remove the language code");
            }
            
            //If translated title language code is null or invalid
            if(!PojoUtil.isEmpty(translatedTitle) && (PojoUtil.isEmpty(title.getTranslatedTitle().getLanguageCode())
                    || !Arrays.stream(SiteConstants.AVAILABLE_ISO_LANGUAGES).anyMatch(title.getTranslatedTitle().getLanguageCode()::equals))) {
                Map<String, String> params = new HashMap<String, String>();
                String values = Arrays.stream(SiteConstants.AVAILABLE_ISO_LANGUAGES).collect(Collectors.joining(", "));
                params.put("type", "translated title -> language code");
                params.put("values", values);
                throw new ActivityTypeValidationException(params);
            }
        }
        
        if(work.getWorkType() == null) {
            Map<String, String> params = new HashMap<String, String>();
            String values = Arrays.stream(WorkType.values()).map(element -> element.value()).collect(Collectors.joining(", "));
            params.put("type", "work type");
            params.put("values", values);
            throw new ActivityTypeValidationException(params);
        }
        
        if(!PojoUtil.isEmpty(work.getLanguageCode())) {
            if(!Arrays.stream(SiteConstants.AVAILABLE_ISO_LANGUAGES).anyMatch(work.getLanguageCode()::equals)) {
                Map<String, String> params = new HashMap<String, String>();
                String values = Arrays.stream(SiteConstants.AVAILABLE_ISO_LANGUAGES).collect(Collectors.joining(", "));
                params.put("type", "language code");
                params.put("values", values);
                throw new ActivityTypeValidationException(params);
            }
        }                
        
        //publication date
        if(work.getPublicationDate() != null) {
            PublicationDate pd = work.getPublicationDate(); 
            Year year = pd.getYear();
            Month month = pd.getMonth();
            Day day = pd.getDay();
            
            if(year != null) {                
                try {
                    Integer.valueOf(year.getValue());                    
                } catch(NumberFormatException n) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("type", "publication date -> year");
                    params.put("values", "integers");
                    throw new ActivityTypeValidationException(params);
                }
                
                if(year.getValue().length() != 4) {
                    throw new OrcidValidationException("Invalid year " + year.getValue() + " please specify a four digits value");
                }
            }
            
            if(month != null) {
                try {
                    Integer.valueOf(month.getValue());
                } catch(NumberFormatException n) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("type", "publication date -> month");
                    params.put("values", "integers");
                    throw new ActivityTypeValidationException(params);
                }
                
                if(month.getValue().length() != 2) {
                    throw new OrcidValidationException("Invalid month " + month.getValue() + " please specify a two digits value");
                }
            }
            
            if(day != null) {
                try {
                    Integer.valueOf(day.getValue());
                } catch(NumberFormatException n) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("type", "publication date -> day");
                    params.put("values", "integers");
                    throw new ActivityTypeValidationException(params);
                }
                
                if(day.getValue().length() != 2) {
                    throw new OrcidValidationException("Invalid day " + day.getValue() + " please specify a two digits value");
                }
            }
            
            //Check the date is valid
            boolean isYearEmpty = (year == null || year.getValue() == null) ? true : false;
            boolean isMonthEmpty = (month == null || month.getValue() == null) ? true : false;
            boolean isDayEmpty = (day == null || day.getValue() == null) ? true : false;
            if(isYearEmpty && (!isMonthEmpty || !isDayEmpty)) {
                throw new OrcidValidationException("Invalid date, please specify a year element");
            } else if(!isYearEmpty && isMonthEmpty  && !isDayEmpty) {
                throw new OrcidValidationException("Invalid date, please specify a month element");
            } else if(isYearEmpty && isMonthEmpty && !isDayEmpty) {
                throw new OrcidValidationException("Invalid date, please specify a year and month elements");
            }
        }
        
        //citation
        if(work.getWorkCitation() != null) {
            String citation = work.getWorkCitation().getCitation();
            CitationType type = work.getWorkCitation().getWorkCitationType();
            if(type == null) {
                Map<String, String> params = new HashMap<String, String>();
                String values = Arrays.stream(CitationType.values()).map(element -> element.value()).collect(Collectors.joining(", "));
                params.put("type", "citation type");
                params.put("values", values);
                throw new ActivityTypeValidationException(params);
            }
            
            if(PojoUtil.isEmpty(citation)) {
                throw new OrcidValidationException("Please specify a citation or remove the parent tag");
            }
        }
        
        if (work.getWorkExternalIdentifiers() == null || work.getWorkExternalIdentifiers().getExternalIdentifier() == null
                || work.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
            throw new ActivityIdentifierValidationException();
        }

        if(work.getWorkContributors() != null) {
            WorkContributors contributors = work.getWorkContributors();
            if(!contributors.getContributor().isEmpty()) {
                for(Contributor contributor : contributors.getContributor()) {
                    if(contributor.getContributorOrcid() != null) {
                        ContributorOrcid contributorOrcid = contributor.getContributorOrcid();
                        if(!PojoUtil.isEmpty(contributorOrcid.getUri())) {
                            if(!OrcidStringUtils.isValidOrcidUri(contributorOrcid.getUri())) {
                                throw new OrcidValidationException("Invalid contributor URI");
                            }
                        }
                        
                        if(!PojoUtil.isEmpty(contributorOrcid.getPath())) {
                            if(!OrcidStringUtils.isValidOrcid(contributorOrcid.getPath())) {
                                throw new OrcidValidationException("Invalid contributor ORCID");
                            }
                        }
                    }
                    if(contributor.getCreditName() != null) {
                        if(PojoUtil.isEmpty(contributor.getCreditName().getContent())) {
                            throw new OrcidValidationException("Please specify a contributor credit name or remove the empty tag");
                        }
                    }
                    if(contributor.getContributorEmail() != null) {
                        if(PojoUtil.isEmpty(contributor.getContributorEmail().getValue())) {
                            throw new OrcidValidationException("Please specify a contributor email or remove the empty tag");
                        }
                    }
                }
            }
        }
        
        if (work.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", sourceEntity.getSourceName());
            }
            throw new InvalidPutCodeException(params);
        }

        // Check that we are not changing the visibility
        if (isApiRequest && !createFlag) {
            Visibility updatedVisibility = work.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }

        externalIDValidator.validateWorkOrPeerReview(work.getExternalIdentifiers());
    }

    public void validateFunding(Funding funding, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest,
            org.orcid.jaxb.model.message.Visibility originalVisibility) {
        FundingTitle title = funding.getTitle();
        if (title == null || title.getTitle() == null || StringUtils.isEmpty(title.getTitle().getContent())) {
            throw new ActivityTitleValidationException();
        }

        //translated title language code
        if(title != null && title.getTranslatedTitle() != null && !PojoUtil.isEmpty(title.getTranslatedTitle().getContent())) {
            //If translated title language code is null or invalid
            if (PojoUtil.isEmpty(title.getTranslatedTitle().getLanguageCode())
                    || !Arrays.stream(SiteConstants.AVAILABLE_ISO_LANGUAGES).anyMatch(title.getTranslatedTitle().getLanguageCode()::equals)) {
                Map<String, String> params = new HashMap<String, String>();
                String values = Arrays.stream(SiteConstants.AVAILABLE_ISO_LANGUAGES).collect(Collectors.joining(", "));
                params.put("type", "translated title -> language code");
                params.put("values", values);
                throw new ActivityTypeValidationException(params);
            }
        }
        
        if (isApiRequest) {
            if (funding.getExternalIdentifiers() == null || funding.getExternalIdentifiers().getExternalIdentifier() == null
                    || funding.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
                throw new ActivityIdentifierValidationException();
            }
        }

        if(funding.getAmount() != null) {
            Amount amount = funding.getAmount();
            if(PojoUtil.isEmpty(amount.getCurrencyCode()) && !PojoUtil.isEmpty(amount.getContent())) {
                throw new OrcidValidationException("Please specify a currency code");
            } else if(!PojoUtil.isEmpty(amount.getCurrencyCode()) && PojoUtil.isEmpty(amount.getContent())) {
                throw new OrcidValidationException("Please specify an amount or remove the amount tag");
            }
        }
        
        if (funding.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", sourceEntity.getSourceName());
            }
            throw new InvalidPutCodeException(params);
        }

        // Check that we are not changing the visibility
        if (isApiRequest && !createFlag) {
            Visibility updatedVisibility = funding.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }

        externalIDValidator.validateFunding(funding.getExternalIdentifiers());
    }

    public void validateEmployment(Employment employment, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest,
            org.orcid.jaxb.model.message.Visibility originalVisibility) {
        if (employment.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", sourceEntity.getSourceName());
            }
            throw new InvalidPutCodeException(params);
        }

        // Check that we are not changing the visibility
        if (isApiRequest && !createFlag) {
            Visibility updatedVisibility = employment.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
    }

    public void validateEducation(Education education, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest,
            org.orcid.jaxb.model.message.Visibility originalVisibility) {
        if (education.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", sourceEntity.getSourceName());
            }
            throw new InvalidPutCodeException(params);
        }

        // Check that we are not changing the visibility
        if (isApiRequest && !createFlag) {
            Visibility updatedVisibility = education.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
    }

    public void validatePeerReview(PeerReview peerReview, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest,
            org.orcid.jaxb.model.message.Visibility originalVisibility) {
        if (peerReview.getExternalIdentifiers() == null || peerReview.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
            throw new ActivityIdentifierValidationException();
        }

        if (peerReview.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("clientName", sourceEntity.getSourceName());
            throw new InvalidPutCodeException(params);
        }

        if (peerReview.getType() == null) {
            Map<String, String> params = new HashMap<String, String>();
            String peerReviewTypes = Arrays.stream(PeerReviewType.values()).map(element -> element.value()).collect(Collectors.joining(", "));
            params.put("type", "peer review type");
            params.put("values", peerReviewTypes);
            throw new ActivityTypeValidationException();
        }

        externalIDValidator.validateWorkOrPeerReview(peerReview.getExternalIdentifiers());

        if (peerReview.getSubjectExternalIdentifier() != null) {
            externalIDValidator.validateWorkOrPeerReview(peerReview.getSubjectExternalIdentifier());
        }

        // Check that we are not changing the visibility
        if (isApiRequest && !createFlag) {
            Visibility updatedVisibility = peerReview.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
    }

    public void validateGroupIdRecord(GroupIdRecord groupIdRecord, boolean createFlag, SourceEntity sourceEntity) {
        if (createFlag) {
            if (groupIdRecord.getPutCode() != null) {
                Map<String, String> params = new HashMap<String, String>();
                if (sourceEntity != null) {
                    params.put("clientName", sourceEntity.getSourceName());
                }
                throw new InvalidPutCodeException(params);
            }
        }

        Pattern validGroupIdRegexPattern = Pattern.compile("(ringgold:|issn:|orcid-generated:|fundref:|publons:)([0-9a-zA-Z\\^._~:/?#\\[\\]@!$&amp;'()*+,;=-]){2,}");
        Matcher matcher = validGroupIdRegexPattern.matcher(groupIdRecord.getGroupId());
        if (!matcher.matches()) {
            throw new OrcidValidationException("Invalid group-id: '" + groupIdRecord.getGroupId() + "'");
        }
    }

    public void checkExternalIdentifiersForDuplicates(ExternalIDs newExtIds, ExternalIDs existingExtIds, Source existingSource, SourceEntity sourceEntity) {
        if (existingExtIds != null && newExtIds != null) {
            for (ExternalID existingId : existingExtIds.getExternalIdentifier()) {
                for (ExternalID newId : newExtIds.getExternalIdentifier()) {
                    if (areRelationshipsSameButNotBothPartOf(existingId.getRelationship(), newId.getRelationship()) && newId.equals(existingId)
                            && sourceEntity.getSourceId().equals(getExistingSource(existingSource))) {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("clientName", sourceEntity.getSourceName());
                        throw new OrcidDuplicatedActivityException(params);
                    }
                }
            }
        }
    }

    private static boolean areRelationshipsSameButNotBothPartOf(Relationship r1, Relationship r2) {
        if (r1 == null && r2 == null)
            return true;
        if (r1 != null && r1.equals(r2) && !r1.equals(Relationship.PART_OF))
            return true;
        return false;
    }

    public void checkFundingExternalIdentifiersForDuplicates(ExternalIDs newExtIds, ExternalIDs existingExtIds, Source existingSource, SourceEntity sourceEntity) {
        if (existingExtIds != null && newExtIds != null) {
            for (ExternalID existingId : existingExtIds.getExternalIdentifier()) {
                for (ExternalID newId : newExtIds.getExternalIdentifier()) {
                    if (areRelationshipsSameButNotBothPartOf(existingId.getRelationship(), newId.getRelationship()) && newId.equals(existingId)
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
        if (updatedVisibility != null) {
            if (originalVisibility == null) {
                throw new VisibilityMismatchException();
            }
            if (!updatedVisibility.value().equals(originalVisibility.value())) {
                throw new VisibilityMismatchException();
            }
        }
    }
}
