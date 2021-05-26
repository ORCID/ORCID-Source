package org.orcid.core.version.impl;

import javax.annotation.Resource;

import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.version.V3Convertible;
import org.orcid.core.version.V3VersionConverter;
import org.orcid.core.version.V3VersionObjectFactory;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.common.OrcidIdBase;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecords;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

public class VersionConverterImplV3_0_rc2ToV3_0 implements V3VersionConverter {

    private static final String LOWER_VERSION = "3.0_rc2";
    private static final String UPPER_VERSION = "3.0";

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
    public VersionConverterImplV3_0_rc2ToV3_0() {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        OrcidIdBaseMapper orcidIdBaseMapper = new OrcidIdBaseMapper();
        
        WorkContributorRoleMapper workContributorRoleMapper = new WorkContributorRoleMapper();
        
        FundingContributorRoleMapper fundingContributorRoleMapper = new FundingContributorRoleMapper();
        
        ExtIDsMapper extIDsMapper = new ExtIDsMapper();

        // GROUP ID
        mapperFactory.classMap(GroupIdRecords.class, org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecords.class).byDefault().register();
        mapperFactory.classMap(GroupIdRecord.class, org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord.class).byDefault().register();

        // ExternalIDs
        mapperFactory.classMap(ExternalIDs.class, org.orcid.jaxb.model.v3.rc2.record.ExternalIDs.class).customize(extIDsMapper).register();
        
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
        mapperFactory.classMap(org.orcid.jaxb.model.v3.rc2.record.Emails.class, Emails.class).byDefault().register();
        mapperFactory.classMap(org.orcid.jaxb.model.v3.rc2.record.Email.class, Email.class).field("source", "source").field("lastModifiedDate", "lastModifiedDate")
        .field("createdDate", "createdDate").field("email", "email").field("path", "path").field("visibility", "visibility").customize(new EmailMapper())
        .register();

        // WORK
        mapperFactory.classMap(WorkGroup.class, org.orcid.jaxb.model.v3.rc2.record.summary.WorkGroup.class).byDefault().register();
        mapperFactory.classMap(Works.class, org.orcid.jaxb.model.v3.rc2.record.summary.Works.class).byDefault().register();
        mapperFactory.classMap(Work.class, org.orcid.jaxb.model.v3.rc2.record.Work.class).byDefault().register();
        mapperFactory.classMap(WorkSummary.class, org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary.class).byDefault().register();

        // WORK CONTRIBUTORS
        mapperFactory.classMap(org.orcid.jaxb.model.v3.release.common.ContributorAttributes.class, 
                org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes.class).customize(workContributorRoleMapper).register();
        
        // FUNDING
        mapperFactory.classMap(FundingGroup.class, org.orcid.jaxb.model.v3.rc2.record.summary.FundingGroup.class).byDefault().register();
        mapperFactory.classMap(Fundings.class, org.orcid.jaxb.model.v3.rc2.record.summary.Fundings.class).byDefault().register();
        mapperFactory.classMap(Funding.class, org.orcid.jaxb.model.v3.rc2.record.Funding.class).byDefault().register();
        mapperFactory.classMap(FundingSummary.class, org.orcid.jaxb.model.v3.rc2.record.summary.FundingSummary.class).byDefault().register();

        // FUNDING CONTRIBUTORS
        mapperFactory.classMap(org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes.class,
                org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes.class).customize(fundingContributorRoleMapper).register();

        // EDUCATION
        mapperFactory.classMap(org.orcid.jaxb.model.v3.release.record.Educations.class, org.orcid.jaxb.model.v3.rc2.record.Educations.class).byDefault().register();
        mapperFactory.classMap(Educations.class, org.orcid.jaxb.model.v3.rc2.record.summary.Educations.class).byDefault().register();
        mapperFactory.classMap(Education.class, org.orcid.jaxb.model.v3.rc2.record.Education.class).byDefault().register();
        mapperFactory.classMap(EducationSummary.class, org.orcid.jaxb.model.v3.rc2.record.summary.EducationSummary.class).byDefault().register();

        // EMPLOYMENT
        mapperFactory.classMap(org.orcid.jaxb.model.v3.release.record.Employments.class, org.orcid.jaxb.model.v3.rc2.record.Employments.class).byDefault().register();
        mapperFactory.classMap(Employments.class, org.orcid.jaxb.model.v3.rc2.record.summary.Employments.class).byDefault().register();
        mapperFactory.classMap(Employment.class, org.orcid.jaxb.model.v3.rc2.record.Employment.class).byDefault().register();
        mapperFactory.classMap(EmploymentSummary.class, org.orcid.jaxb.model.v3.rc2.record.summary.EmploymentSummary.class).byDefault().register();

        // RESEARCH RESOURCES
        mapperFactory.classMap(org.orcid.jaxb.model.v3.release.record.summary.ResearchResources.class, org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResources.class)
                .byDefault().register();
        mapperFactory.classMap(org.orcid.jaxb.model.v3.release.record.ResearchResourceProposal.class, org.orcid.jaxb.model.v3.rc2.record.ResearchResourceProposal.class)
                .byDefault().register();
        mapperFactory
                .classMap(org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceGroup.class, org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceGroup.class)
                .byDefault().register();
        mapperFactory.classMap(org.orcid.jaxb.model.v3.release.record.ResearchResource.class, org.orcid.jaxb.model.v3.rc2.record.ResearchResource.class).byDefault()
                .register();
        mapperFactory.classMap(org.orcid.jaxb.model.v3.release.record.ResearchResourceHosts.class, org.orcid.jaxb.model.v3.rc2.record.ResearchResourceHosts.class).byDefault()
                .register();
        mapperFactory.classMap(org.orcid.jaxb.model.v3.release.record.ResearchResourceItems.class, org.orcid.jaxb.model.v3.rc2.record.ResearchResourceItems.class).byDefault()
                .register();
        mapperFactory.classMap(org.orcid.jaxb.model.v3.release.record.ResearchResourceItem.class, org.orcid.jaxb.model.v3.rc2.record.ResearchResourceItem.class).byDefault()
                .register();
        mapperFactory.classMap(org.orcid.jaxb.model.v3.release.record.ResearchResourceTitle.class, org.orcid.jaxb.model.v3.rc2.record.ResearchResource.class).byDefault()
                .register();
        ClassMapBuilder<ResearchResourceSummary, org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceSummary> rrSummaryClassMap = mapperFactory
                .classMap(ResearchResourceSummary.class,
                        org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceSummary.class);
        rrSummaryClassMap.byDefault().register();

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
        
        @Override
        public void mapAtoB(OrcidIdBase a, org.orcid.jaxb.model.v3.rc2.common.OrcidIdBase b, MappingContext context) {
            b.setHost(a.getHost());
            b.setPath(a.getPath());
            b.setUri(orcidUrlManager.getBaseUrl() + (a.getClass().isAssignableFrom(SourceClientId.class) ? "/client/" : "/") + a.getPath());
        }
    }
    
    private class EmailMapper extends CustomMapper<org.orcid.jaxb.model.v3.rc2.record.Email, Email> {

        @Override
        public void mapBtoA(Email b, org.orcid.jaxb.model.v3.rc2.record.Email a, MappingContext context) {
            if(b.isCurrent() != null) {
                a.setCurrent(b.isCurrent());
            }
            if(b.isPrimary() != null) {
                a.setPrimary(b.isPrimary());                
            }
            if(b.isVerified() != null) {
                a.setVerified(b.isVerified());
            }
        }

    }
    
    private class WorkContributorRoleMapper
            extends CustomMapper<org.orcid.jaxb.model.v3.release.common.ContributorAttributes, org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes> {
        @Override
        public void mapAtoB(org.orcid.jaxb.model.v3.release.common.ContributorAttributes v3Attributes,
                org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes v3rc2Attributes, MappingContext context) {
            if (v3Attributes.getContributorSequence() != null) {
                v3rc2Attributes.setContributorSequence(v3Attributes.getContributorSequence());
            }

            if (v3Attributes.getContributorRole() != null) {
                try {
                    v3rc2Attributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.fromValue(v3Attributes.getContributorRole()));
                } catch (IllegalArgumentException iae) {
                    if (CreditRole.WRITING_ORIGINAL_DRAFT.value().equals(v3Attributes.getContributorRole())) {
                        v3rc2Attributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.AUTHOR);
                    } else if (CreditRole.WRITING_REVIEW_EDITING.value().equals(v3Attributes.getContributorRole())) {
                        v3rc2Attributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.EDITOR);
                    } else if (CreditRole.INVESTIGATION.value().equals(v3Attributes.getContributorRole())) {
                        v3rc2Attributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.CO_INVESTIGATOR);
                    } else if (CreditRole.SUPERVISION.value().equals(v3Attributes.getContributorRole())) {
                        v3rc2Attributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.PRINCIPAL_INVESTIGATOR);
                    }
                    // If no mapping is found, leave it null
                }
            }
        }

        @Override
        public void mapBtoA(org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes v3rc2Attributes,
                org.orcid.jaxb.model.v3.release.common.ContributorAttributes v3Attributes, MappingContext context) {
            if (v3rc2Attributes.getContributorSequence() != null) {
                v3Attributes.setContributorSequence(v3rc2Attributes.getContributorSequence());
            }

            if (v3rc2Attributes.getContributorRole() != null) {
                v3Attributes.setContributorRole(v3rc2Attributes.getContributorRole().value());
            }
        }
    }
    
    private class FundingContributorRoleMapper
            extends CustomMapper<org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes, org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes> {
        @Override
        public void mapAtoB(org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes v3Attributes,
                org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes v3rc2Attributes, MappingContext context) {

            if (v3Attributes.getContributorRole() != null) {
                try {
                    v3rc2Attributes.setContributorRole(org.orcid.jaxb.model.common.FundingContributorRole.fromValue(v3Attributes.getContributorRole()));
                } catch (IllegalArgumentException iae) {
                    if (CreditRole.SUPERVISION.value().equals(v3Attributes.getContributorRole())) {
                        v3rc2Attributes.setContributorRole(org.orcid.jaxb.model.common.FundingContributorRole.LEAD);
                    }
                    // If no mapping is found, leave it null
                }
            }
        }

        @Override
        public void mapBtoA(org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes v3rc2Attributes,
                org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes v3Attributes, MappingContext context) {
            if (v3rc2Attributes.getContributorRole() != null) {
                v3Attributes.setContributorRole(v3rc2Attributes.getContributorRole().value());
            }
        }
    }   
    
    private class ExtIDsMapper<Y, A> extends CustomMapper<ExternalIDs, org.orcid.jaxb.model.v3.rc2.record.ExternalIDs> {

        @Override
        public void mapBtoA(org.orcid.jaxb.model.v3.rc2.record.ExternalIDs b, ExternalIDs a, MappingContext context) {            
            b.getExternalIdentifier().forEach(rc2ExtId -> {
                ExternalID v3ExtId = new ExternalID();
                v3ExtId.setRelationship(rc2ExtId.getRelationship());
                v3ExtId.setType(rc2ExtId.getType());                
                if(rc2ExtId.getUrl() != null) {
                    v3ExtId.setUrl(new Url(rc2ExtId.getUrl().getValue()));
                }
                v3ExtId.setValue(rc2ExtId.getValue());
                a.getExternalIdentifier().add(v3ExtId);
            });                        
        }
        
        @Override
        public void mapAtoB(ExternalIDs a, org.orcid.jaxb.model.v3.rc2.record.ExternalIDs b, MappingContext context) {
            a.getExternalIdentifier().stream()
            .filter(e -> !org.orcid.jaxb.model.common.Relationship.FUNDED_BY.equals(e.getRelationship()))
            .forEach(v3ExtId -> { 
                org.orcid.jaxb.model.v3.rc2.record.ExternalID rc2ExtId = new org.orcid.jaxb.model.v3.rc2.record.ExternalID();
                rc2ExtId.setRelationship(v3ExtId.getRelationship());
                rc2ExtId.setType(v3ExtId.getType());
                if(v3ExtId.getUrl() != null) {
                    rc2ExtId.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url(v3ExtId.getUrl().getValue()));
                }
                rc2ExtId.setValue(v3ExtId.getValue());
                
                if(v3ExtId.getNormalized() != null) {                    
                    rc2ExtId.setNormalized(new org.orcid.jaxb.model.v3.rc2.common.TransientNonEmptyString(v3ExtId.getNormalized().getValue()));  
                }
                
                if(v3ExtId.getNormalizedError() != null) {
                    rc2ExtId.setNormalizedError(new org.orcid.jaxb.model.v3.rc2.common.TransientError(v3ExtId.getNormalizedError().getErrorCode(), v3ExtId.getNormalizedError().getErrorMessage()));
                }
                
                if(v3ExtId.getNormalizedUrl() != null) {
                    rc2ExtId.setNormalizedUrl(new org.orcid.jaxb.model.v3.rc2.common.TransientNonEmptyString(v3ExtId.getNormalizedUrl().getValue()));
                }
                
                if(v3ExtId.getNormalizedUrlError() != null) {
                    rc2ExtId.setNormalizedUrlError(new org.orcid.jaxb.model.v3.rc2.common.TransientError(v3ExtId.getNormalizedUrlError().getErrorCode(), v3ExtId.getNormalizedUrlError().getErrorMessage()));
                }
                
                b.getExternalIdentifier().add(rc2ExtId);
            });
        }
    }    
}
