package org.orcid.core.manager.v3.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.naming.OperationNotSupportedException;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.ActivityTitleValidationException;
import org.orcid.core.exception.ActivityTypeValidationException;
import org.orcid.core.exception.InvalidDisambiguatedOrgException;
import org.orcid.core.exception.InvalidFuzzyDateException;
import org.orcid.core.exception.InvalidOrgException;
import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.utils.v3.SourceEntityUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.jaxb.model.v3.rc1.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc1.common.Amount;
import org.orcid.jaxb.model.v3.rc1.common.Contributor;
import org.orcid.jaxb.model.v3.rc1.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.rc1.common.Day;
import org.orcid.jaxb.model.v3.rc1.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc1.common.Month;
import org.orcid.jaxb.model.v3.rc1.common.MultipleOrganizationHolder;
import org.orcid.jaxb.model.v3.rc1.common.Organization;
import org.orcid.jaxb.model.v3.rc1.common.OrganizationHolder;
import org.orcid.jaxb.model.v3.rc1.common.PublicationDate;
import org.orcid.jaxb.model.v3.rc1.common.Source;
import org.orcid.jaxb.model.v3.rc1.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.common.Year;
import org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.rc1.record.Affiliation;
import org.orcid.jaxb.model.v3.rc1.record.CitationType;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.jaxb.model.v3.rc1.record.FundingTitle;
import org.orcid.jaxb.model.v3.rc1.record.PeerReview;
import org.orcid.jaxb.model.v3.rc1.record.PeerReviewType;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc1.record.ResearchResourceItem;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkContributors;
import org.orcid.jaxb.model.v3.rc1.record.WorkTitle;
import org.orcid.jaxb.model.v3.rc1.record.WorkType;
import org.orcid.persistence.constants.SiteConstants;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;

public class ActivityValidator {

    @Resource(name = "externalIDValidatorV3")
    private ExternalIDValidator externalIDValidator;

    @Resource
    private PIDNormalizationService norm;

    public void validateWork(Work work, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        WorkTitle title = work.getWorkTitle();
        if (title == null || title.getTitle() == null || PojoUtil.isEmpty(title.getTitle().getContent())) {
            throw new ActivityTitleValidationException();
        }

        if (work.getCountry() != null) {
            if (work.getCountry().getValue() == null) {
                Map<String, String> params = new HashMap<String, String>();
                String values = Arrays.stream(Iso3166Country.values()).map(element -> element.value()).collect(Collectors.joining(", "));
                params.put("type", "country");
                params.put("values", values);
                throw new ActivityTypeValidationException(params);
            }
        }

        // translated title language code
        if (title != null && title.getTranslatedTitle() != null) {
            String translatedTitle = title.getTranslatedTitle().getContent();
            String languageCode = title.getTranslatedTitle().getLanguageCode();

            if (PojoUtil.isEmpty(translatedTitle) && !PojoUtil.isEmpty(languageCode)) {
                throw new OrcidValidationException("Please specify a translated title or remove the language code");
            }

            // If translated title language code is null or invalid
            if (!PojoUtil.isEmpty(translatedTitle) && (PojoUtil.isEmpty(title.getTranslatedTitle().getLanguageCode())
                    || !Arrays.stream(SiteConstants.AVAILABLE_ISO_LANGUAGES).anyMatch(title.getTranslatedTitle().getLanguageCode()::equals))) {
                Map<String, String> params = new HashMap<String, String>();
                String values = Arrays.stream(SiteConstants.AVAILABLE_ISO_LANGUAGES).collect(Collectors.joining(", "));
                params.put("type", "translated title -> language code");
                params.put("values", values);
                throw new ActivityTypeValidationException(params);
            }
        }

        if (work.getWorkType() == null) {
            Map<String, String> params = new HashMap<String, String>();
            String values = Arrays.stream(WorkType.values()).map(element -> element.value()).collect(Collectors.joining(", "));
            params.put("type", "work type");
            params.put("values", values);
            throw new ActivityTypeValidationException(params);
        }

        if (!PojoUtil.isEmpty(work.getLanguageCode())) {
            if (!Arrays.stream(SiteConstants.AVAILABLE_ISO_LANGUAGES).anyMatch(work.getLanguageCode()::equals)) {
                Map<String, String> params = new HashMap<String, String>();
                String values = Arrays.stream(SiteConstants.AVAILABLE_ISO_LANGUAGES).collect(Collectors.joining(", "));
                params.put("type", "language code");
                params.put("values", values);
                throw new ActivityTypeValidationException(params);
            }
        }

        // publication date
        if (work.getPublicationDate() != null) {
            validateFuzzyDate(work.getPublicationDate());
            PublicationDate pd = work.getPublicationDate();
            Year year = pd.getYear();
            Month month = pd.getMonth();
            Day day = pd.getDay();

            if (year != null) {
                try {
                    Integer.valueOf(year.getValue());
                } catch (NumberFormatException n) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("type", "publication date -> year");
                    params.put("values", "integers");
                    throw new ActivityTypeValidationException(params);
                }

                if (year.getValue().length() != 4) {
                    throw new OrcidValidationException("Invalid year " + year.getValue() + " please specify a four digits value");
                }
            }

            if (month != null) {
                try {
                    Integer.valueOf(month.getValue());
                } catch (NumberFormatException n) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("type", "publication date -> month");
                    params.put("values", "integers");
                    throw new ActivityTypeValidationException(params);
                }

                if (month.getValue().length() != 2) {
                    throw new OrcidValidationException("Invalid month " + month.getValue() + " please specify a two digits value");
                }
            }

            if (day != null) {
                try {
                    Integer.valueOf(day.getValue());
                } catch (NumberFormatException n) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("type", "publication date -> day");
                    params.put("values", "integers");
                    throw new ActivityTypeValidationException(params);
                }

                if (day.getValue().length() != 2) {
                    throw new OrcidValidationException("Invalid day " + day.getValue() + " please specify a two digits value");
                }
            }

            // Check the date is valid
            boolean isYearEmpty = (year == null || year.getValue() == null) ? true : false;
            boolean isMonthEmpty = (month == null || month.getValue() == null) ? true : false;
            boolean isDayEmpty = (day == null || day.getValue() == null) ? true : false;
            if (isYearEmpty && (!isMonthEmpty || !isDayEmpty)) {
                throw new OrcidValidationException("Invalid date, please specify a year element");
            } else if (!isYearEmpty && isMonthEmpty && !isDayEmpty) {
                throw new OrcidValidationException("Invalid date, please specify a month element");
            } else if (isYearEmpty && isMonthEmpty && !isDayEmpty) {
                throw new OrcidValidationException("Invalid date, please specify a year and month elements");
            }
        }

        // citation
        if (work.getWorkCitation() != null) {
            String citation = work.getWorkCitation().getCitation();
            CitationType type = work.getWorkCitation().getWorkCitationType();
            if (type == null) {
                Map<String, String> params = new HashMap<String, String>();
                String values = Arrays.stream(CitationType.values()).map(element -> element.value()).collect(Collectors.joining(", "));
                params.put("type", "citation type");
                params.put("values", values);
                throw new ActivityTypeValidationException(params);
            }

            if (PojoUtil.isEmpty(citation)) {
                throw new OrcidValidationException("Please specify a citation or remove the parent tag");
            }
        }

        if (work.getWorkExternalIdentifiers() == null || work.getWorkExternalIdentifiers().getExternalIdentifier() == null
                || work.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
            throw new ActivityIdentifierValidationException();
        }

        if (work.getWorkContributors() != null) {
            WorkContributors contributors = work.getWorkContributors();
            if (!contributors.getContributor().isEmpty()) {
                for (Contributor contributor : contributors.getContributor()) {
                    if (contributor.getContributorOrcid() != null) {
                        ContributorOrcid contributorOrcid = contributor.getContributorOrcid();
                        if (!PojoUtil.isEmpty(contributorOrcid.getUri())) {
                            if (!OrcidStringUtils.isValidOrcid2_1Uri(contributorOrcid.getUri())) {
                                throw new OrcidValidationException("Invalid contributor URI");
                            }
                        }

                        if (!PojoUtil.isEmpty(contributorOrcid.getPath())) {
                            if (!OrcidStringUtils.isValidOrcid(contributorOrcid.getPath())) {
                                throw new OrcidValidationException("Invalid contributor ORCID");
                            }
                        }
                    }
                    if (contributor.getCreditName() != null) {
                        if (PojoUtil.isEmpty(contributor.getCreditName().getContent())) {
                            throw new OrcidValidationException("Please specify a contributor credit name or remove the empty tag");
                        }
                    }
                    if (contributor.getContributorEmail() != null) {
                        if (PojoUtil.isEmpty(contributor.getContributorEmail().getValue())) {
                            throw new OrcidValidationException("Please specify a contributor email or remove the empty tag");
                        }
                    }
                }
            }
        }

        if (work.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", SourceEntityUtils.getSourceName(sourceEntity));
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

    public void validateFunding(Funding funding, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        FundingTitle title = funding.getTitle();
        if (title == null || title.getTitle() == null || StringUtils.isEmpty(title.getTitle().getContent())) {
            throw new ActivityTitleValidationException();
        }

        // translated title language code
        if (title != null && title.getTranslatedTitle() != null && !PojoUtil.isEmpty(title.getTranslatedTitle().getContent())) {
            // If translated title language code is null or invalid
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

        if (funding.getAmount() != null) {
            Amount amount = funding.getAmount();
            if (PojoUtil.isEmpty(amount.getCurrencyCode()) && !PojoUtil.isEmpty(amount.getContent())) {
                throw new OrcidValidationException("Please specify a currency code");
            } else if (!PojoUtil.isEmpty(amount.getCurrencyCode()) && PojoUtil.isEmpty(amount.getContent())) {
                throw new OrcidValidationException("Please specify an amount or remove the amount tag");
            }
        }

        if (funding.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", SourceEntityUtils.getSourceName(sourceEntity));
            }
            throw new InvalidPutCodeException(params);
        }

        if (isApiRequest) {
            validateDisambiguatedOrg(funding);
            if (funding.getEndDate() != null) {
                validateFuzzyDate(funding.getEndDate());
            }
            if (funding.getStartDate() != null) {
                validateFuzzyDate(funding.getStartDate());
            }
        }

        // Check that we are not changing the visibility
        if (isApiRequest && !createFlag) {
            Visibility updatedVisibility = funding.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }

        externalIDValidator.validateFunding(funding.getExternalIdentifiers());
    }

    private void validateDisambiguatedOrg(OrganizationHolder organizationHolder) {
        if (organizationHolder.getOrganization() == null) {
            throw new InvalidOrgException();
        }

        Organization org = organizationHolder.getOrganization();
        if (org.getDisambiguatedOrganization() == null) {
            throw new InvalidDisambiguatedOrgException();
        }

        if (org.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier() == null
                || org.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier().isEmpty()) {
            throw new InvalidDisambiguatedOrgException();
        }
    }
    
    private void validateDisambiguatedOrg(MultipleOrganizationHolder organizationHolder) {
        if (organizationHolder.getOrganization() == null) {
            throw new InvalidOrgException();
        }

        for (Organization org : organizationHolder.getOrganization()){
            if (org.getDisambiguatedOrganization() == null) {
                throw new InvalidDisambiguatedOrgException();
            }

            if (org.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier() == null
                    || org.getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier().isEmpty()) {
                throw new InvalidDisambiguatedOrgException();
            }
        }
    }

    public void validateAffiliation(Affiliation affiliation, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        if (affiliation.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", SourceEntityUtils.getSourceName(sourceEntity));
            }
            throw new InvalidPutCodeException(params);
        }

        // Check that we are not changing the visibility
        if (isApiRequest && !createFlag) {
            Visibility updatedVisibility = affiliation.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }

        if (affiliation.getStartDate() == null) {
            throw new OrcidValidationException("Education start date is required");
        }

        if (isApiRequest) {
            validateDisambiguatedOrg(affiliation);
            if (affiliation.getEndDate() != null) {
                validateFuzzyDate(affiliation.getEndDate());
            }
            if (affiliation.getStartDate() != null) {
                validateFuzzyDate(affiliation.getStartDate());
            }
        }
    }

    public void validatePeerReview(PeerReview peerReview, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        if (peerReview.getExternalIdentifiers() == null || peerReview.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
            throw new ActivityIdentifierValidationException();
        }

        if (peerReview.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("clientName", SourceEntityUtils.getSourceName(sourceEntity));
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

        if (isApiRequest) {
            validateDisambiguatedOrg(peerReview);
            if (peerReview.getCompletionDate() != null) {
                validateFuzzyDate(peerReview.getCompletionDate());
            }
        }
    }

    public void validateGroupIdRecord(GroupIdRecord groupIdRecord, boolean createFlag, SourceEntity sourceEntity) {
        if (createFlag) {
            if (groupIdRecord.getPutCode() != null) {
                Map<String, String> params = new HashMap<String, String>();
                if (sourceEntity != null) {
                    params.put("clientName", SourceEntityUtils.getSourceName(sourceEntity));
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
                    // normalize the ids before checking equality
                    newId.setNormalized(new TransientNonEmptyString(norm.normalise(newId.getType(), newId.getValue())));
                    if (existingId.getNormalized() == null)
                        existingId.setNormalized(new TransientNonEmptyString(norm.normalise(existingId.getType(), existingId.getValue())));
                    if (areRelationshipsSameButNotBothPartOf(existingId.getRelationship(), newId.getRelationship()) && newId.equals(existingId)
                            && SourceEntityUtils.getSourceId(sourceEntity).equals(getExistingSource(existingSource))) {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("clientName", SourceEntityUtils.getSourceName(sourceEntity));
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
                            && SourceEntityUtils.getSourceId(sourceEntity).equals(getExistingSource(existingSource))) {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("clientName", SourceEntityUtils.getSourceName(sourceEntity));
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

    private static void validateVisibilityDoesntChange(Visibility updatedVisibility, Visibility originalVisibility) {
        if (updatedVisibility != null) {
            if (originalVisibility == null) {
                throw new VisibilityMismatchException();
            }
            if (!updatedVisibility.equals(originalVisibility)) {
                throw new VisibilityMismatchException();
            }
        }
    }

    public void validateResearchResource(ResearchResource rr, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        if (rr.getProposal().getExternalIdentifiers() == null || rr.getProposal().getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
            throw new ActivityIdentifierValidationException("Missing external ID in Research Resource Proposal");
        }
        externalIDValidator.validateWorkOrPeerReview(rr.getProposal().getExternalIdentifiers());
        if (isApiRequest)
            validateDisambiguatedOrg(rr.getProposal().getHosts());
        for (ResearchResourceItem i:rr.getResourceItems()){
            if (i.getExternalIdentifiers() == null || i.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
                throw new ActivityIdentifierValidationException("Missing external ID in Research Resource Item");
            }
            externalIDValidator.validateWorkOrPeerReview(i.getExternalIdentifiers());
            if (isApiRequest)
                validateDisambiguatedOrg(i.getHosts());
        }
        
        if (rr.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("clientName", SourceEntityUtils.getSourceName(sourceEntity));
            throw new InvalidPutCodeException(params);
        }

        // Check that we are not changing the visibility
        if (isApiRequest && !createFlag) {
            Visibility updatedVisibility = rr.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
    }

    private void validateFuzzyDate(FuzzyDate fuzzyDate) {
        String dateString = getDateString(fuzzyDate);
        DateTimeFormatter[] formatters = {
                new DateTimeFormatterBuilder().appendPattern("yyyy").parseDefaulting(ChronoField.MONTH_OF_YEAR, 1).parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                        .toFormatter(),
                new DateTimeFormatterBuilder().appendPattern("yyyy-MM").parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter(),
                new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").parseStrict().toFormatter() };
        
        boolean valid = false;
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                if (PojoUtil.isEmpty(fuzzyDate.getDay()) || localDate.getDayOfMonth() == Integer.parseInt(fuzzyDate.getDay().getValue())) {
                    valid = true;
                    break;
                }
            } catch (DateTimeParseException e) {
            }
        }
        if (!valid) {
            Map<String, String> params = new HashMap<>();
            params.put("dateString", dateString);
            throw new InvalidFuzzyDateException(params);
        }
    }

    private String getDateString(FuzzyDate fuzzyDate) {
        String year = fuzzyDate.getYear() != null ? fuzzyDate.getYear().getValue() : null;
        String month = fuzzyDate.getMonth() != null ? fuzzyDate.getMonth().getValue() : null;
        String day = fuzzyDate.getDay() != null ? fuzzyDate.getDay().getValue() : null;
        
        if (day != null) {
            return year + "-" + month + "-" + day;
        }
        if (month != null) {
            return year + "-" + month;
        }
        if (year != null) {
            return year;
        }
        
        return null;
    }
}
