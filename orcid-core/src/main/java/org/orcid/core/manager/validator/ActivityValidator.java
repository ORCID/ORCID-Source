package org.orcid.core.manager.validator;

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

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.ActivityTitleValidationException;
import org.orcid.core.exception.ActivityTypeValidationException;
import org.orcid.core.exception.InvalidFuzzyDateException;
import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.common.LanguageCode;
import org.orcid.jaxb.model.common_v2.Amount;
import org.orcid.jaxb.model.common_v2.Contributor;
import org.orcid.jaxb.model.common_v2.ContributorOrcid;
import org.orcid.jaxb.model.common_v2.Day;
import org.orcid.jaxb.model.common_v2.FuzzyDate;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Month;
import org.orcid.jaxb.model.common_v2.PublicationDate;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.common_v2.Year;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.record_v2.Activity;
import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.ExternalIdentifiersAwareActivity;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.FundingTitle;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PeerReviewType;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkContributors;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;

public class ActivityValidator {

    @Resource
    private ExternalIDValidator externalIDValidator;

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
                    || !Arrays.stream(LanguageCode.getValues()).anyMatch(title.getTranslatedTitle().getLanguageCode()::equals))) {
                Map<String, String> params = new HashMap<String, String>();
                String values = Arrays.stream(LanguageCode.getValues()).collect(Collectors.joining(", "));
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
            if (!Arrays.stream(LanguageCode.getValues()).anyMatch(work.getLanguageCode()::equals)) {
                Map<String, String> params = new HashMap<String, String>();
                String values = Arrays.stream(LanguageCode.getValues()).collect(Collectors.joining(", "));
                params.put("type", "language code");
                params.put("values", values);
                throw new ActivityTypeValidationException(params);
            }
        }

        // publication date
        if (work.getPublicationDate() != null) {
            if (isApiRequest) {
                validateFuzzyDate(work.getPublicationDate());
            }
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
                            if (!OrcidStringUtils.isValidOrcidUri(contributorOrcid.getUri())) {
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
                    || !Arrays.stream(LanguageCode.getValues()).anyMatch(title.getTranslatedTitle().getLanguageCode()::equals)) {
                Map<String, String> params = new HashMap<String, String>();
                String values = Arrays.stream(LanguageCode.getValues()).collect(Collectors.joining(", "));
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

        // Check that we are not changing the visibility
        if (isApiRequest && !createFlag) {
            Visibility updatedVisibility = funding.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
        
        if (isApiRequest) {
            if (funding.getEndDate() != null) {
                validateFuzzyDate(funding.getEndDate());
            }
            if (funding.getStartDate() != null) {
                validateFuzzyDate(funding.getStartDate());
            }
        }

        externalIDValidator.validateFunding(funding.getExternalIdentifiers());
    }

    public void validateEmployment(Employment employment, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        if (employment.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", SourceEntityUtils.getSourceName(sourceEntity));
            }
            throw new InvalidPutCodeException(params);
        }

        // Check that we are not changing the visibility
        if (isApiRequest && !createFlag) {
            Visibility updatedVisibility = employment.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }

        if (isApiRequest) {
            if (employment.getEndDate() != null) {
                validateFuzzyDate(employment.getEndDate());
            }
            if (employment.getStartDate() != null) {
                validateFuzzyDate(employment.getStartDate());
            }
        }
    }

    public void validateEducation(Education education, SourceEntity sourceEntity, boolean createFlag, boolean isApiRequest, Visibility originalVisibility) {
        if (education.getPutCode() != null && createFlag) {
            Map<String, String> params = new HashMap<String, String>();
            if (sourceEntity != null) {
                params.put("clientName", SourceEntityUtils.getSourceName(sourceEntity));
            }
            throw new InvalidPutCodeException(params);
        }

        // Check that we are not changing the visibility
        if (isApiRequest && !createFlag) {
            Visibility updatedVisibility = education.getVisibility();
            validateVisibilityDoesntChange(updatedVisibility, originalVisibility);
        }
        
        if (isApiRequest) {
            if (education.getEndDate() != null) {
                validateFuzzyDate(education.getEndDate());
            }
            if (education.getStartDate() != null) {
                validateFuzzyDate(education.getStartDate());
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

    public void checkExternalIdentifiersForDuplicates(ExternalIdentifiersAwareActivity theNew, ExternalIdentifiersAwareActivity theExisting, Source existingSource, SourceEntity sourceEntity) {
        ExternalIDs newExtIds = theNew.getExternalIdentifiers();
        ExternalIDs existingExtIds = theExisting.getExternalIdentifiers();
    
        if (existingExtIds != null && newExtIds != null) {
            for (ExternalID existingId : existingExtIds.getExternalIdentifier()) {
                for (ExternalID newId : newExtIds.getExternalIdentifier()) {
                    if (areRelationshipsSameButNotBothPartOf(existingId.getRelationship(), newId.getRelationship()) && newId.equals(existingId)
                            && SourceEntityUtils.getSourceId(sourceEntity).equals(getExistingSource(existingSource))) {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("clientName", SourceEntityUtils.getSourceName(sourceEntity));
                        params.put("putCode", String.valueOf(theExisting.getPutCode()));
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
