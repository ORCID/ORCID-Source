package org.orcid.frontend.web.controllers;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.annotation.Resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.apache.commons.lang3.StringUtils;
import org.orcid.api.publicV3.server.security.PublicAPISecurityManagerV3;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.common.FundingType;
import org.orcid.jaxb.model.common.PeerReviewType;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.record.*;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.Country;
import org.orcid.jaxb.model.v3.release.record.Deprecated;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.jaxb.model.v3.release.record.summary.ActivityGroup;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceGroup;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResources;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Services;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class PublicRecordApiController {

    @Resource(name = "recordManagerReadOnlyV3")
    private RecordManagerReadOnly recordManagerReadOnly;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "publicAPISecurityManagerV3")
    private PublicAPISecurityManagerV3 publicAPISecurityManagerV3;

    @Resource(name = "groupIdRecordManagerReadOnlyV3")
    private GroupIdRecordManagerReadOnly groupIdRecordManagerReadOnlyV3;

    private final ObjectMapper mapper;

    private final boolean filterVersionOfIdentifiers = false;

    public PublicRecordApiController() {
        this.mapper = new ObjectMapper();
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        module.setPriority(JaxbAnnotationModule.Priority.PRIMARY);
        mapper.registerModule(module);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
        mapper.addMixIn(Record.class, RecordMixin.class);
        mapper.addMixIn(Deprecated.class, DeprecatedMixin.class);
        mapper.addMixIn(History.class, HistoryMixin.class);
        mapper.addMixIn(ActivitiesSummary.class, ActivitiesSummaryMixin.class);
        mapper.addMixIn(OrcidIdentifier.class, OrcidIdentifierMixin.class);
        mapper.addMixIn(Source.class, SourceMixin.class);
        mapper.addMixIn(Organization.class, OrganizationMixin.class);
        mapper.addMixIn(DisambiguatedOrganization.class, DisambiguatedOrganizationMixin.class);
        mapper.addMixIn(Distinctions.class, DistinctionsMixin.class);
        mapper.addMixIn(Educations.class, EducationsMixin.class);
        mapper.addMixIn(Employments.class, EmploymentsMixin.class);
        mapper.addMixIn(InvitedPositions.class, InvitedPositionsMixin.class);
        mapper.addMixIn(Memberships.class, MembershipsMixin.class);
        mapper.addMixIn(Qualifications.class, QualificationsMixin.class);
        mapper.addMixIn(Services.class, ServicesMixin.class);
        mapper.addMixIn(Fundings.class, FundingsMixin.class);
        mapper.addMixIn(FundingGroup.class, FundingGroupMixin.class);
        mapper.addMixIn(PeerReviews.class, PeerReviewsMixin.class);
        mapper.addMixIn(PeerReviewGroup.class, PeerReviewGroupMixin.class);
        mapper.addMixIn(PeerReviewDuplicateGroup.class, PeerReviewDuplicateGroupMixin.class);
        mapper.addMixIn(Works.class, WorksMixin.class);
        mapper.addMixIn(WorkGroup.class, WorkGroupMixin.class);
        mapper.addMixIn(ResearchResources.class, ResearchResourcesMixin.class);
        mapper.addMixIn(ResearchResourceGroup.class, ResearchResourceGroupMixin.class);
        mapper.addMixIn(AffiliationSummary.class, AffiliationSummaryMixin.class);
        mapper.addMixIn(DistinctionSummary.class, DistinctionSummaryMixin.class);
        mapper.addMixIn(EducationSummary.class, EducationSummaryMixin.class);
        mapper.addMixIn(EmploymentSummary.class, EmploymentSummaryMixin.class);
        mapper.addMixIn(InvitedPositionSummary.class, InvitedPositionSummaryMixin.class);
        mapper.addMixIn(MembershipSummary.class, MembershipSummaryMixin.class);
        mapper.addMixIn(QualificationSummary.class, QualificationSummaryMixin.class);
        mapper.addMixIn(ServiceSummary.class, ServiceSummaryMixin.class);
        mapper.addMixIn(AffiliationGroup.class, AffiliationGroupMixin.class);
        mapper.addMixIn(WorkSummary.class, WorkSummaryMixin.class);
        mapper.addMixIn(PeerReviewSummary.class, PeerReviewSummaryMixin.class);
        mapper.addMixIn(FundingSummary.class, FundingSummaryMixin.class);
        mapper.addMixIn(ActivityGroup.class, ActivityGroupMixin.class);
        mapper.addMixIn(ExternalIDs.class, ExternalIDsMixin.class);
        mapper.addMixIn(ExternalID.class, ExternalIDMixin.class);
        mapper.addMixIn(Title.class, TitleMixin.class);
        mapper.addMixIn(Relationship.class, EnumValueMixin.class);
        mapper.addMixIn(WorkType.class, EnumValueMixin.class);
        mapper.addMixIn(PeerReviewType.class, EnumValueMixin.class);
        mapper.addMixIn(FundingType.class, EnumValueMixin.class);
        mapper.addMixIn(Visibility.class, VisibilityMixin.class);
        mapper.addMixIn(GivenNames.class, ValueMixin.class);
        mapper.addMixIn(FamilyName.class, ValueMixin.class);
        mapper.addMixIn(CreditName.class, ValueMixin.class);
        mapper.addMixIn(Keywords.class, KeywordsMixin.class);
        mapper.addMixIn(Person.class, PersonMixin.class);
        mapper.addMixIn(PersonExternalIdentifiers.class, PersonExternalIdentifiersMixin.class);
        mapper.addMixIn(PersonExternalIdentifier.class, PersonExternalIdentifierMixin.class);
        mapper.addMixIn(OtherNames.class, OtherNamesMixin.class);
        mapper.addMixIn(ResearcherUrls.class, ResearcherUrlsMixin.class);
        mapper.addMixIn(Emails.class, EmailsMixin.class);
        mapper.addMixIn(Name.class, NameMixin.class);
        mapper.addMixIn(Country.class, CountryMixin.class);
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/record", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
            OrcidApiConstants.ORCID_JSON, OrcidApiConstants.VND_ORCID_JSON })
    public @ResponseBody String viewRecord(HttpServletRequest request, @PathVariable("orcid") String orcid) throws JsonProcessingException {
        orcidSecurityManager.checkProfile(orcid);
        request.setAttribute(SourceEntityUtils.DO_NOT_POPULATE_SOURCES, true);
        Record publicRecord = recordManagerReadOnly.getPublicRecord(orcid, filterVersionOfIdentifiers);
        if(publicRecord != null && publicRecord.getActivitiesSummary() != null && publicRecord.getActivitiesSummary().getPeerReviews() != null && publicRecord.getActivitiesSummary().getPeerReviews().getPeerReviewGroup() != null) {
            for(PeerReviewGroup prg : publicRecord.getActivitiesSummary().getPeerReviews().getPeerReviewGroup()) {
                for(PeerReviewDuplicateGroup prdg : prg.getPeerReviewGroup()) {
                    for(PeerReviewSummary summary : prdg.getPeerReviewSummary()) {
                        // This is a hack, the peer reviews come from the DB with the `group_id`, however, we need to display the name of that group id,
                        // which is in the group_id_record.group_name, so, we will replace the review-group-id with the group_name
                        String groupId = summary.getGroupId();
                        if(StringUtils.isNotBlank(groupId)) {
                            Optional<GroupIdRecord> opt = groupIdRecordManagerReadOnlyV3.findByGroupId(groupId);
                            opt.ifPresent(groupIdRecord -> summary.setGroupId(groupIdRecord.getName()));
                        }
                    }
                }
            }
        }
        return mapper.writeValueAsString(publicRecord);
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "orcid-identifier", "preferences", "history", "person", "activities-summary", "path" })
    private abstract static class RecordMixin {
        @JsonIgnore
        public abstract Object getDeprecated();

        @JsonIgnore
        public abstract Object getOrcidType();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "deprecated-date", "primary-record" })
    private abstract static class DeprecatedMixin {
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "creation-method", "completion-date", "submission-date", "last-modified-date", "claimed", "source", "deactivation-date",
            "verified-email", "verified-primary-email" })
    private abstract static class HistoryMixin {
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "distinctions", "educations", "employments", "fundings", "invited-positions", "memberships",
            "peer-reviews", "qualifications", "works", "services", "research-resources", "path" })
    private abstract static class ActivitiesSummaryMixin {
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "uri", "path", "host" })
    private abstract static class OrcidIdentifierMixin {
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "source-orcid", "source-client-id", "source-name", "assertion-origin-orcid", "assertion-origin-client-id", "assertion-origin-name" })
    private abstract static class SourceMixin {
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "name", "address", "disambiguated-organization" })
    private abstract static class OrganizationMixin {
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "disambiguated-organization-identifier", "disambiguation-source" })
    private abstract static class DisambiguatedOrganizationMixin {
        @JsonIgnore
        public abstract Long getId();

        @JsonIgnore
        public abstract java.util.List<?> getExternalIdentifiers();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "affiliation-group", "path" })
    private abstract static class DistinctionsMixin {
        @JsonProperty("affiliation-group")
        public abstract java.util.Collection<?> getDistinctionGroups();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "affiliation-group", "path" })
    private abstract static class EducationsMixin {
        @JsonProperty("affiliation-group")
        public abstract java.util.Collection<?> getEducationGroups();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "affiliation-group", "path" })
    private abstract static class EmploymentsMixin {
        @JsonProperty("affiliation-group")
        public abstract java.util.Collection<?> getEmploymentGroups();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "affiliation-group", "path" })
    private abstract static class InvitedPositionsMixin {
        @JsonProperty("affiliation-group")
        public abstract java.util.Collection<?> getInvitedPositionGroups();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "affiliation-group", "path" })
    private abstract static class MembershipsMixin {
        @JsonProperty("affiliation-group")
        public abstract java.util.Collection<?> getMembershipGroups();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "affiliation-group", "path" })
    private abstract static class QualificationsMixin {
        @JsonProperty("affiliation-group")
        public abstract java.util.Collection<?> getQualificationGroups();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "affiliation-group", "path" })
    private abstract static class ServicesMixin {
        @JsonProperty("affiliation-group")
        public abstract java.util.Collection<?> getServiceGroups();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "group", "path" })
    private abstract static class FundingsMixin {
        @JsonProperty("group")
        public abstract java.util.List<?> getFundingGroup();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "external-ids", "funding-summary", "path" })
    private abstract static class FundingGroupMixin {
        @JsonProperty("external-ids")
        public abstract Object getIdentifiers();

        @JsonProperty("funding-summary")
        public abstract java.util.List<?> getFundingSummary();

        @JsonIgnore
        public abstract java.util.Collection<?> getActivities();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "group" })
    private abstract static class PeerReviewsMixin {
        @JsonProperty("group")
        public abstract java.util.List<?> getPeerReviewGroup();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "external-ids", "peer-review-group" })
    private abstract static class PeerReviewGroupMixin {
        @JsonProperty("external-ids")
        public abstract Object getIdentifiers();

        @JsonProperty("peer-review-group")
        public abstract java.util.List<?> getPeerReviewGroup();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "external-ids", "peer-review-summary" })
    private abstract static class PeerReviewDuplicateGroupMixin {
        @JsonProperty("external-ids")
        public abstract Object getIdentifiers();

        @JsonProperty("peer-review-summary")
        public abstract java.util.List<?> getPeerReviewSummary();

        @JsonIgnore
        public abstract java.util.Collection<?> getActivities();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "group", "path" })
    private abstract static class WorksMixin {
        @JsonProperty("group")
        public abstract java.util.List<?> getWorkGroup();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "external-ids", "work-summary", "path" })
    private abstract static class WorkGroupMixin {
        @JsonProperty("external-ids")
        public abstract Object getIdentifiers();

        @JsonProperty("work-summary")
        public abstract java.util.List<?> getWorkSummary();

        @JsonIgnore
        public abstract java.util.Collection<?> getActivities();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "group" })
    private abstract static class ResearchResourcesMixin {
        @JsonProperty("group")
        public abstract java.util.List<?> getResearchResourceGroup();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "external-ids" })
    private abstract static class ResearchResourceGroupMixin {
        @JsonProperty("external-ids")
        public abstract Object getIdentifiers();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "created-date", "last-modified-date", "source", "put-code", "department-name", "role-title", "start-date", "end-date",
            "organization", "url", "external-ids", "display-index", "visibility", "path" })
    private abstract static class AffiliationSummaryMixin {
        @JsonProperty("external-ids")
        public abstract Object getExternalIdentifiers();

        @JsonIgnore
        public abstract Object getExternalIDs();
    }

    @SuppressWarnings("unused")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeName("distinction-summary")
    private abstract static class DistinctionSummaryMixin {
    }

    @SuppressWarnings("unused")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeName("education-summary")
    private abstract static class EducationSummaryMixin {
    }

    @SuppressWarnings("unused")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeName("employment-summary")
    private abstract static class EmploymentSummaryMixin {
    }

    @SuppressWarnings("unused")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeName("invited-position-summary")
    private abstract static class InvitedPositionSummaryMixin {
    }

    @SuppressWarnings("unused")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeName("membership-summary")
    private abstract static class MembershipSummaryMixin {
    }

    @SuppressWarnings("unused")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeName("qualification-summary")
    private abstract static class QualificationSummaryMixin {
    }

    @SuppressWarnings("unused")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonTypeName("service-summary")
    private abstract static class ServiceSummaryMixin {
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "external-ids", "summaries", "path" })
    private abstract static class AffiliationGroupMixin {
        @JsonProperty("summaries")
        public abstract java.util.List<?> getActivities();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "put-code", "created-date", "last-modified-date", "source", "title", "external-ids", "url", "type", "publication-date",
            "journal-title", "visibility", "path", "display-index" })
    private abstract static class WorkSummaryMixin {
        @JsonProperty("external-ids")
        public abstract Object getExternalIdentifiers();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "external-ids" })
    private abstract static class PeerReviewSummaryMixin {
        @JsonProperty("external-ids")
        public abstract Object getExternalIdentifiers();

        @JsonProperty("convening-organization")
        public abstract Object getOrganization();

        @JsonProperty("review-group-id")
        public abstract String getGroupId();

        @JsonProperty("reviewer-role")
        public abstract Object getRole();

        @JsonProperty("review-type")
        public abstract Object getType();

        @JsonProperty("review-url")
        public abstract Object getUrl();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "created-date", "last-modified-date", "source", "title", "external-ids", "url", "type", "start-date", "end-date",
            "organization", "visibility", "put-code", "path", "display-index" })
    private abstract static class FundingSummaryMixin {
        @JsonProperty("external-ids")
        public abstract Object getExternalIdentifiers();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "external-ids" })
    private abstract static class ActivityGroupMixin {
        @JsonProperty("external-ids")
        public abstract Object getIdentifiers();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "external-id" })
    private abstract static class ExternalIDsMixin {
        @JsonProperty("external-id")
        public abstract java.util.List<?> getExternalIdentifier();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "value" })
    private abstract static class ValueMixin {
        @JsonProperty("value")
        public abstract String getContent();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "value" })
    private abstract static class TitleMixin {
        @JsonProperty("value")
        public abstract String getContent();
    }

    @SuppressWarnings("unused")
    private abstract static class EnumValueMixin {
        @JsonValue
        public abstract String value();
    }

    @SuppressWarnings("unused")
    private abstract static class VisibilityMixin {
        @JsonValue
        public abstract String value();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "external-id-type", "external-id-value", "external-id-normalized", "external-id-normalized-error", "external-id-url",
            "external-id-relationship" })
    private abstract static class ExternalIDMixin {
        @JsonProperty("external-id-type")
        public abstract String getType();

        @JsonProperty("external-id-value")
        public abstract String getValue();

        @JsonProperty("external-id-normalized")
        public abstract Object getNormalized();

        @JsonProperty("external-id-normalized-error")
        public abstract Object getNormalizedError();

        @JsonProperty("external-id-url")
        public abstract Object getUrl();

        @JsonProperty("external-id-relationship")
        public abstract Object getRelationship();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "external-identifier", "path" })
    private abstract static class PersonExternalIdentifiersMixin {
        @JsonProperty("external-identifier")
        public abstract java.util.List<?> getExternalIdentifiers();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "external-id-type", "external-id-value", "external-id-url", "external-id-relationship" })
    private abstract static class PersonExternalIdentifierMixin {
        @JsonProperty("external-id-type")
        public abstract String getType();

        @JsonProperty("external-id-value")
        public abstract String getValue();

        @JsonProperty("external-id-url")
        public abstract Object getUrl();

        @JsonProperty("external-id-relationship")
        public abstract Object getRelationship();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "other-name", "path" })
    private abstract static class OtherNamesMixin {
        @JsonProperty("other-name")
        public abstract java.util.List<?> getOtherNames();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "researcher-url", "path" })
    private abstract static class ResearcherUrlsMixin {
        @JsonProperty("researcher-url")
        public abstract java.util.List<?> getResearcherUrls();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "email", "path" })
    private abstract static class EmailsMixin {
        @JsonProperty("email")
        public abstract java.util.List<?> getEmails();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "name", "other-names", "biography", "researcher-urls", "emails", "addresses", "keywords", "external-identifiers",
            "path" })
    private abstract static class PersonMixin {
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "created-date", "last-modified-date", "given-names", "family-name", "credit-name", "source", "visibility",
            "path" })
    private abstract static class NameMixin {
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "value" })
    private abstract static class CountryMixin {
        @JsonProperty("value")
        public abstract Object getValue();
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "created-date", "last-modified-date", "content", "visibility",
            "path" })
    private abstract static class BiographyMixin {
    }

    @SuppressWarnings("unused")
    @JsonPropertyOrder({ "last-modified-date", "keyword", "path" })
    private abstract static class KeywordsMixin {
        @JsonProperty("keyword")
        public abstract java.util.List<?> getKeywords();
    }
}
