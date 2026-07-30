package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.common.FundingType;
import org.orcid.jaxb.model.common.PeerReviewType;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.v3.release.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.Deprecated;
import org.orcid.jaxb.model.v3.release.record.History;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.FamilyName;
import org.orcid.jaxb.model.v3.release.record.GivenNames;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.ActivityGroup;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResources;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Services;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PublicRecordApiControllerTest {

    private static final String ORCID = "0000-0000-0000-0001";

    @Mock
    private RecordManagerReadOnly recordManagerReadOnly;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PublicRecordApiController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testViewRecord() throws Exception {
        Record record = new Record();
        when(recordManagerReadOnly.getPublicRecord(eq(ORCID), anyBoolean())).thenReturn(record);

        String result = controller.viewRecord(request, ORCID);

        assertNotNull(result);
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(request).setAttribute(SourceEntityUtils.DO_NOT_POPULATE_SOURCES, true);
        verify(recordManagerReadOnly).getPublicRecord(ORCID, false);
        
        ObjectMapper mapper = new ObjectMapper();
        com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule module = new com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule();
        module.setPriority(com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule.Priority.PRIMARY);
        mapper.registerModule(module);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
        mapper.addMixIn(Record.class, RecordMixin.class);
        mapper.addMixIn(Deprecated.class, DeprecatedMixin.class);
        mapper.addMixIn(History.class, HistoryMixin.class);
        mapper.addMixIn(ActivitiesSummary.class, ActivitiesSummaryMixin.class);
        mapper.addMixIn(OrcidIdentifier.class, OrcidIdentifierMixin.class);
        mapper.addMixIn(AvailableLocales.class, AvailableLocalesMixin.class);
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
        mapper.addMixIn(Works.class, WorksMixin.class);
        mapper.addMixIn(WorkGroup.class, WorkGroupMixin.class);
        mapper.addMixIn(ResearchResources.class, ResearchResourcesMixin.class);
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
        mapper.addMixIn(OtherNames.class, OtherNamesMixin.class);
        mapper.addMixIn(ResearcherUrls.class, ResearcherUrlsMixin.class);
        mapper.addMixIn(Emails.class, EmailsMixin.class);
        String expectedJson = mapper.writeValueAsString(record);
        assertEquals(expectedJson, result);
    }

    @Test(expected = SecurityException.class)
    public void testViewRecordSecurityException() throws Exception {
        doThrow(new SecurityException()).when(orcidSecurityManager).checkProfile(ORCID);
        controller.viewRecord(request, ORCID);
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
    private abstract static class AvailableLocalesMixin {
        @JsonValue
        public abstract String value();
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
    @JsonPropertyOrder({ "last-modified-date", "keyword", "path" })
    private abstract static class KeywordsMixin {
        @JsonProperty("keyword")
        public abstract java.util.List<?> getKeywords();
    }

}
