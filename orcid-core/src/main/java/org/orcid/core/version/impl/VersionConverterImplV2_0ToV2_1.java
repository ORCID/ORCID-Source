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
package org.orcid.core.version.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverter;
import org.orcid.core.version.V2VersionObjectFactory;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.SourceOrcid;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecords;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record.summary_v2.FundingGroup;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.Fundings;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviews;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.jaxb.model.record_v2.SourceAware;
import org.orcid.jaxb.model.record_v2.Work;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class VersionConverterImplV2_0ToV2_1 implements V2VersionConverter {

    private static final String LOWER_VERSION = "2.0";
    private static final String UPPER_VERSION = "2.1";

    private static MapperFacade mapper;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private V2VersionObjectFactory v2VersionObjectFactory;

    @Override
    public String getLowerVersion() {
        return LOWER_VERSION;
    }

    @Override
    public String getUpperVersion() {
        return UPPER_VERSION;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public VersionConverterImplV2_0ToV2_1() {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        SourceMapper sourceMapper = new SourceMapper();
        
        //GROUP ID
        mapperFactory.classMap(GroupIdRecords.class, GroupIdRecords.class).byDefault().register();
        mapperFactory.classMap(GroupIdRecord.class, GroupIdRecord.class).byDefault().register();
        
        //ExternalIDs
        mapperFactory.classMap(ExternalIDs.class, ExternalIDs.class).byDefault().register();
        mapperFactory.classMap(ExternalID.class, ExternalID.class).customize(sourceMapper).byDefault().register();
        
        // Other names
        mapperFactory.classMap(OtherNames.class, OtherNames.class).byDefault().register();
        mapperFactory.classMap(OtherName.class, OtherName.class).customize(sourceMapper).byDefault().register();

        //Keywords
        mapperFactory.classMap(Keywords.class, Keywords.class).byDefault().register();
        mapperFactory.classMap(Keyword.class, Keyword.class).customize(sourceMapper).byDefault().register();
        
        //Address
        mapperFactory.classMap(Addresses.class, Addresses.class).byDefault().register();
        mapperFactory.classMap(Address.class, Address.class).customize(sourceMapper).byDefault().register();
        
        //ResearcherUrl
        mapperFactory.classMap(ResearcherUrls.class, ResearcherUrls.class).byDefault().register();
        mapperFactory.classMap(ResearcherUrl.class, ResearcherUrl.class).customize(sourceMapper).byDefault().register();
        
        //Person External ID
        mapperFactory.classMap(PersonExternalIdentifiers.class, PersonExternalIdentifiers.class).byDefault().register();
        mapperFactory.classMap(PersonExternalIdentifier.class, PersonExternalIdentifier.class).customize(sourceMapper).byDefault().register();
        
        //Emails
        mapperFactory.classMap(Emails.class, Emails.class).byDefault().register();
        mapperFactory.classMap(Email.class, Email.class).customize(sourceMapper).byDefault().register();
        
        // WORK         
        mapperFactory.classMap(WorkGroup.class, WorkGroup.class).byDefault().register();
        mapperFactory.classMap(Works.class, Works.class).byDefault().register();
        mapperFactory.classMap(Work.class, Work.class).customize(sourceMapper).byDefault().register();
        mapperFactory.classMap(WorkSummary.class, WorkSummary.class).customize(sourceMapper).byDefault().register();
        
        //FUNDING        
        mapperFactory.classMap(FundingGroup.class, FundingGroup.class).byDefault().register();
        mapperFactory.classMap(Fundings.class, Fundings.class).byDefault().register();
        mapperFactory.classMap(Funding.class, Funding.class).customize(sourceMapper).byDefault().register();
        mapperFactory.classMap(FundingSummary.class, FundingSummary.class).customize(sourceMapper).byDefault().register();
        
        //EDUCATION
        mapperFactory.classMap(org.orcid.jaxb.model.record_v2.Educations.class, org.orcid.jaxb.model.record_v2.Educations.class).byDefault().register();
        mapperFactory.classMap(Educations.class, Educations.class).byDefault().register();
        mapperFactory.classMap(Education.class, Education.class).customize(sourceMapper).byDefault().register();        
        mapperFactory.classMap(EducationSummary.class, EducationSummary.class).customize(sourceMapper).byDefault().register();
        
        //EMPLOYMENT
        mapperFactory.classMap(org.orcid.jaxb.model.record_v2.Employments.class, org.orcid.jaxb.model.record_v2.Employments.class).byDefault().register();
        mapperFactory.classMap(Employments.class, Employments.class).byDefault().register();
        mapperFactory.classMap(Employment.class, Employment.class).customize(sourceMapper).byDefault().register();        
        mapperFactory.classMap(EmploymentSummary.class, EmploymentSummary.class).customize(sourceMapper).byDefault().register();
        
        //PEER REVIEW
        mapperFactory.classMap(PeerReviews.class, PeerReviews.class).byDefault().register();
        mapperFactory.classMap(PeerReview.class, PeerReview.class).customize(sourceMapper).byDefault().register();        
        mapperFactory.classMap(PeerReviewSummary.class, PeerReviewSummary.class).customize(sourceMapper).byDefault().register();                
        
        //NOTIFICATIONS
        mapperFactory.classMap(NotificationPermission.class, org.orcid.jaxb.model.notification.permission_v2.NotificationPermission.class).byDefault().register();
        
        //Person
        mapperFactory.classMap(Person.class, Person.class).byDefault().register();
        
        //Record
        mapperFactory.classMap(Record.class, Record.class).byDefault().register();
        
        mapper = mapperFactory.getMapperFacade();
    }

    private class SourceMapper<Y, A> extends CustomMapper<SourceAware, SourceAware> {

        @SuppressWarnings("deprecation")
        @Override
        public void mapAtoB(SourceAware a, SourceAware b, MappingContext context) {
            Source source = a.getSource();
            if (source == null) {
                return;
            }

            if (source.getSourceClientId() != null) {
                b.setSource(source);
            } else if (source.getSourceOrcid() != null) {
                String path = source.getSourceOrcid().getPath();
                SourceOrcid sourceOrcid = new SourceOrcid();
                sourceOrcid.setHost(orcidUrlManager.getBaseHost());
                sourceOrcid.setUri(orcidUrlManager.getBaseUrl() + "/" + path);
                sourceOrcid.setPath(path);
                Source s = new Source();
                s.setSourceOrcid(sourceOrcid);
                s.setSourceName(a.getSource().getSourceName());
                b.setSource(s);
            }
        }
    }

    @Override
    public V2Convertible downgrade(V2Convertible objectToDowngrade) {
        Object objectToConvert = objectToDowngrade.getObjectToConvert();
        Object targetObject = v2VersionObjectFactory.createEquivalentInstance(objectToConvert, LOWER_VERSION);
        mapper.map(objectToConvert, targetObject);
        return new V2Convertible(targetObject, LOWER_VERSION);
    }

    @Override
    public V2Convertible upgrade(V2Convertible objectToUpgrade) {
        Object objectToConvert = objectToUpgrade.getObjectToConvert();
        Object targetObject = v2VersionObjectFactory.createEquivalentInstance(objectToConvert, UPPER_VERSION);
        mapper.map(objectToConvert, targetObject);
        return new V2Convertible(targetObject, UPPER_VERSION);
    }
}
