package org.orcid.core.version.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.version.V3Convertible;
import org.orcid.core.version.V3VersionConverter;
import org.orcid.core.version.V3VersionObjectFactory;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.v3.rc1.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.rc1.common.OrcidIdBase;
import org.orcid.jaxb.model.v3.rc1.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.rc1.common.SourceClientId;
import org.orcid.jaxb.model.v3.rc1.common.SourceOrcid;
import org.orcid.jaxb.model.v3.rc1.error.OrcidError;
import org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecords;
import org.orcid.jaxb.model.v3.rc1.record.Address;
import org.orcid.jaxb.model.v3.rc1.record.Addresses;
import org.orcid.jaxb.model.v3.rc1.record.Education;
import org.orcid.jaxb.model.v3.rc1.record.Email;
import org.orcid.jaxb.model.v3.rc1.record.Emails;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.jaxb.model.v3.rc1.record.Keyword;
import org.orcid.jaxb.model.v3.rc1.record.Keywords;
import org.orcid.jaxb.model.v3.rc1.record.OtherName;
import org.orcid.jaxb.model.v3.rc1.record.OtherNames;
import org.orcid.jaxb.model.v3.rc1.record.PeerReview;
import org.orcid.jaxb.model.v3.rc1.record.Person;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.rc1.record.Record;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Educations;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Employments;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Fundings;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Works;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

public class VersionConverterImplV3_0_rc1ToV3_0_rc2 implements V3VersionConverter {

    private static final String LOWER_VERSION = "3.0_rc1";
    private static final String UPPER_VERSION = "3.0_rc2";

    private static MapperFacade mapper;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private V3VersionObjectFactory v3VersionObjectFactory;

    @Override
    public String getLowerVersion() {
        return LOWER_VERSION;
    }

    @Override
    public String getUpperVersion() {
        return UPPER_VERSION;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public VersionConverterImplV3_0_rc1ToV3_0_rc2() {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        OrcidIdBaseMapper orcidIdBaseMapper = new OrcidIdBaseMapper();

        // GROUP ID
        mapperFactory.classMap(GroupIdRecords.class, org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecords.class).byDefault().register();
        mapperFactory.classMap(GroupIdRecord.class, org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord.class).byDefault().register();

        // ExternalIDs
        mapperFactory.classMap(ExternalIDs.class, org.orcid.jaxb.model.v3.rc2.record.ExternalIDs.class).byDefault().register();
        mapperFactory.classMap(ExternalID.class, org.orcid.jaxb.model.v3.rc2.record.ExternalID.class).byDefault().register();

        // Contributor
        mapperFactory.classMap(ContributorOrcid.class, org.orcid.jaxb.model.v3.rc2.common.ContributorOrcid.class).customize(orcidIdBaseMapper).register();

        // Source Orcid
        mapperFactory.classMap(SourceOrcid.class, org.orcid.jaxb.model.v3.rc2.common.SourceOrcid.class).customize(orcidIdBaseMapper).register();

        // Source client ID
        mapperFactory.classMap(SourceClientId.class, org.orcid.jaxb.model.v3.rc2.common.SourceClientId.class).customize(orcidIdBaseMapper).register();

        // Orcid identifier
        mapperFactory.classMap(OrcidIdentifier.class, org.orcid.jaxb.model.v3.rc2.common.OrcidIdentifier.class).customize(orcidIdBaseMapper).register();

        // Other names
        mapperFactory.classMap(OtherNames.class, org.orcid.jaxb.model.v3.rc2.record.OtherNames.class).byDefault().register();
        mapperFactory.classMap(OtherName.class, org.orcid.jaxb.model.v3.rc2.record.OtherName.class).byDefault().register();

        // Keywords
        mapperFactory.classMap(Keywords.class, org.orcid.jaxb.model.v3.rc2.record.Keywords.class).byDefault().register();
        mapperFactory.classMap(Keyword.class, org.orcid.jaxb.model.v3.rc2.record.Keyword.class).byDefault().register();

        // Address
        mapperFactory.classMap(Addresses.class, org.orcid.jaxb.model.v3.rc2.record.Addresses.class).byDefault().register();
        mapperFactory.classMap(Address.class, org.orcid.jaxb.model.v3.rc2.record.Address.class).byDefault().register();

        // ResearcherUrl
        mapperFactory.classMap(ResearcherUrls.class, org.orcid.jaxb.model.v3.rc2.record.ResearcherUrls.class).byDefault().register();
        mapperFactory.classMap(ResearcherUrl.class, org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl.class).byDefault().register();

        // Person External ID
        mapperFactory.classMap(PersonExternalIdentifiers.class, org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers.class).byDefault().register();
        mapperFactory.classMap(PersonExternalIdentifier.class, org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier.class).byDefault().register();

        // Emails
        mapperFactory.classMap(Emails.class, org.orcid.jaxb.model.v3.rc2.record.Emails.class).byDefault().register();
        mapperFactory.classMap(Email.class, org.orcid.jaxb.model.v3.rc2.record.Email.class).field("source", "source").field("lastModifiedDate", "lastModifiedDate")
                .field("createdDate", "createdDate").field("email", "email").field("path", "path").field("visibility", "visibility").customize(new EmailMapper())
                .register();

        // WORK
        mapperFactory.classMap(WorkGroup.class, org.orcid.jaxb.model.v3.rc2.record.summary.WorkGroup.class).byDefault().register();
        mapperFactory.classMap(Works.class, org.orcid.jaxb.model.v3.rc2.record.summary.Works.class).byDefault().register();
        mapperFactory.classMap(Work.class, org.orcid.jaxb.model.v3.rc2.record.Work.class).byDefault().register();
        mapperFactory.classMap(WorkSummary.class, org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary.class).byDefault().register();

        // FUNDING
        mapperFactory.classMap(FundingGroup.class, org.orcid.jaxb.model.v3.rc2.record.summary.FundingGroup.class).byDefault().register();
        mapperFactory.classMap(Fundings.class, org.orcid.jaxb.model.v3.rc2.record.summary.Fundings.class).byDefault().register();
        mapperFactory.classMap(Funding.class, org.orcid.jaxb.model.v3.rc2.record.Funding.class).byDefault().register();
        mapperFactory.classMap(FundingSummary.class, org.orcid.jaxb.model.v3.rc2.record.summary.FundingSummary.class).byDefault().register();

        // EDUCATION
        mapperFactory.classMap(org.orcid.jaxb.model.v3.rc1.record.Educations.class, org.orcid.jaxb.model.v3.rc2.record.Educations.class).byDefault().register();
        mapperFactory.classMap(Educations.class, org.orcid.jaxb.model.v3.rc2.record.summary.Educations.class).byDefault().register();
        mapperFactory.classMap(Education.class, org.orcid.jaxb.model.v3.rc2.record.Education.class).byDefault().register();
        mapperFactory.classMap(EducationSummary.class, org.orcid.jaxb.model.v3.rc2.record.summary.EducationSummary.class).byDefault().register();

        // EMPLOYMENT
        mapperFactory.classMap(org.orcid.jaxb.model.v3.rc1.record.Employments.class, org.orcid.jaxb.model.v3.rc2.record.Employments.class).byDefault().register();
        mapperFactory.classMap(Employments.class, org.orcid.jaxb.model.v3.rc2.record.summary.Employments.class).byDefault().register();
        mapperFactory.classMap(Employment.class, org.orcid.jaxb.model.v3.rc2.record.Employment.class).byDefault().register();
        mapperFactory.classMap(EmploymentSummary.class, org.orcid.jaxb.model.v3.rc2.record.summary.EmploymentSummary.class).byDefault().register();

        // RESEARCH RESOURCES
        mapperFactory.classMap(org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResources.class, org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResources.class)
                .byDefault().register();
        mapperFactory.classMap(org.orcid.jaxb.model.v3.rc1.record.ResearchResourceProposal.class, org.orcid.jaxb.model.v3.rc2.record.ResearchResourceProposal.class)
                .byDefault().register();
        mapperFactory
                .classMap(org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceGroup.class, org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceGroup.class)
                .byDefault().register();
        mapperFactory.classMap(org.orcid.jaxb.model.v3.rc1.record.ResearchResource.class, org.orcid.jaxb.model.v3.rc2.record.ResearchResource.class).byDefault()
                .register();
        mapperFactory.classMap(org.orcid.jaxb.model.v3.rc1.record.ResearchResourceHosts.class, org.orcid.jaxb.model.v3.rc2.record.ResearchResourceHosts.class).byDefault()
                .register();
        mapperFactory.classMap(org.orcid.jaxb.model.v3.rc1.record.ResearchResourceItems.class, org.orcid.jaxb.model.v3.rc2.record.ResearchResourceItems.class).byDefault()
                .register();
        mapperFactory.classMap(org.orcid.jaxb.model.v3.rc1.record.ResearchResourceItem.class, org.orcid.jaxb.model.v3.rc2.record.ResearchResourceItem.class).byDefault()
                .register();
        mapperFactory.classMap(org.orcid.jaxb.model.v3.rc1.record.ResearchResourceTitle.class, org.orcid.jaxb.model.v3.rc2.record.ResearchResource.class).byDefault()
                .register();
        ClassMapBuilder<org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary, org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceSummary> rrSummaryClassMap = mapperFactory
                .classMap(org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary.class,
                        org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceSummary.class);
        rrSummaryClassMap.field("source", "source").field("lastModifiedDate", "lastModifiedDate").field("createdDate", "createdDate").field("proposal", "proposal")
                .field("putCode", "putCode").field("path", "path").field("visibility", "visibility").field("displayIndex", "displayIndex");
        rrSummaryClassMap.register();

        // PEER REVIEW
        mapperFactory.classMap(PeerReviews.class, org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviews.class).byDefault().register();
        mapperFactory.classMap(PeerReview.class, org.orcid.jaxb.model.v3.rc2.record.PeerReview.class).byDefault().register();
        mapperFactory.classMap(PeerReviewSummary.class, org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviewSummary.class).byDefault().register();

        // NOTIFICATIONS
        mapperFactory.classMap(NotificationPermission.class, org.orcid.jaxb.model.v3.rc2.notification.permission.NotificationPermission.class).byDefault().register();

        // Person
        mapperFactory.classMap(Person.class, org.orcid.jaxb.model.v3.rc2.record.Person.class).byDefault().register();

        // Record
        mapperFactory.classMap(Record.class, org.orcid.jaxb.model.v3.rc2.record.Record.class).byDefault().register();

        // OrcidError
        mapperFactory.classMap(OrcidError.class, org.orcid.jaxb.model.v3.rc2.error.OrcidError.class).byDefault().register();

        mapper = mapperFactory.getMapperFacade();
    }

    @Override
    public V3Convertible downgrade(V3Convertible objectToDowngrade) {
        Object objectToConvert = objectToDowngrade.getObjectToConvert();
        Object targetObject = v3VersionObjectFactory.createEquivalentInstance(objectToConvert, LOWER_VERSION);
        MappingContext context = new MappingContext.Factory().getContext();
        context.setProperty("downgrade", true);
        mapper.map(objectToConvert, targetObject, context);
        return new V3Convertible(targetObject, LOWER_VERSION);
    }

    @Override
    public V3Convertible upgrade(V3Convertible objectToUpgrade) {
        Object objectToConvert = objectToUpgrade.getObjectToConvert();
        Object targetObject = v3VersionObjectFactory.createEquivalentInstance(objectToConvert, UPPER_VERSION);
        MappingContext context = new MappingContext.Factory().getContext();
        context.setProperty("downgrade", false);
        mapper.map(objectToConvert, targetObject, context);
        return new V3Convertible(targetObject, UPPER_VERSION);
    }

    private class OrcidIdBaseMapper<Y, A> extends CustomMapper<OrcidIdBase, org.orcid.jaxb.model.v3.rc2.common.OrcidIdBase> {

        @Override
        public void mapBtoA(org.orcid.jaxb.model.v3.rc2.common.OrcidIdBase b, OrcidIdBase a, MappingContext context) {
            a.setHost(b.getHost());
            a.setPath(b.getPath());
            a.setUri(orcidUrlManager.getBaseUrl() + (b.getClass().isAssignableFrom(org.orcid.jaxb.model.v3.rc2.common.SourceClientId.class) ? "/client/" : "/") + b.getPath());
        }
    }

    private class EmailMapper extends CustomMapper<Email, org.orcid.jaxb.model.v3.rc2.record.Email> {

        @Override
        public void mapBtoA(org.orcid.jaxb.model.v3.rc2.record.Email b, Email a, MappingContext context) {
            a.setCurrent(b.isCurrent());
            a.setPrimary(b.isPrimary());
            a.setVerified(b.isVerified());
        }

    }
}
