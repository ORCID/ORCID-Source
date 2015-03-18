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
package org.orcid.core.adapter.impl;

import javax.annotation.Resource;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.common.FuzzyDate;
import org.orcid.jaxb.model.common.PublicationDate;
import org.orcid.jaxb.model.common.SourceClientId;
import org.orcid.jaxb.model.common.SourceOrcid;
import org.orcid.jaxb.model.notification.addactivities.Activity;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;
import org.orcid.jaxb.model.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.notification.custom.NotificationCustom;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.FundingContributors;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.WorkContributors;
import org.orcid.jaxb.model.record.WorkExternalIdentifier;
import org.orcid.jaxb.model.record.WorkExternalIdentifiers;
import org.orcid.jaxb.model.record.summary.EducationSummary;
import org.orcid.jaxb.model.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.record.summary.FundingSummary;
import org.orcid.jaxb.model.record.summary.WorkSummary;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.NotificationActivityEntity;
import org.orcid.persistence.jpa.entities.NotificationAddActivitiesEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.orcid.persistence.jpa.entities.WorkExternalIdentifierEntity;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.beans.factory.FactoryBean;

/**
 * 
 * @author Will Simpson
 * 
 */
public class MapperFacadeFactory implements FactoryBean<MapperFacade> {

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Override
    public MapperFacade getObject() throws Exception {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("externalIdentifierIdConverter", new ExternalIdentifierTypeConverter());
        mapCommonFields(mapperFactory.classMap(NotificationCustomEntity.class, NotificationCustom.class)).register();
        mapCommonFields(mapperFactory.classMap(NotificationAddActivitiesEntity.class, NotificationAddActivities.class)).field("authorizationUrl", "authorizationUrl.uri")
                .field("notificationActivities", "activities.activities").register();
        mapCommonFields(mapperFactory.classMap(NotificationAmendedEntity.class, NotificationAmended.class)).register();
        mapperFactory.classMap(NotificationActivityEntity.class, Activity.class).fieldMap("externalIdType", "externalIdentifier.externalIdentifierType")
                .converter("externalIdentifierIdConverter").add().field("externalIdValue", "externalIdentifier.externalIdentifierId").byDefault().register();
        addV2SourceMapping(mapperFactory);
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getWorkMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("workExternalIdentifiersConverterId", new JsonOrikaConverter<WorkExternalIdentifiers>());
        converterFactory.registerConverter("workContributorsConverterId", new JsonOrikaConverter<WorkContributors>());

        ClassMapBuilder<Work, ProfileWorkEntity> workClassMap = mapperFactory.classMap(Work.class, ProfileWorkEntity.class);
        workClassMap.byDefault();
        workClassMap.field("putCode", "work.id");
        workClassMap.field("journalTitle.content", "work.journalTitle");
        workClassMap.field("workTitle.title.content", "work.title");
        workClassMap.field("workTitle.translatedTitle.content", "work.translatedTitle");
        workClassMap.field("workTitle.translatedTitle.languageCode", "work.translatedTitleLanguageCode");
        workClassMap.field("workTitle.subtitle.content", "work.subtitle");
        workClassMap.field("shortDescription", "work.description");
        workClassMap.field("workCitation.workCitationType", "work.citationType");
        workClassMap.field("workCitation.citation", "work.citation");
        workClassMap.field("workType", "work.workType");
        workClassMap.field("publicationDate", "work.publicationDate");
        workClassMap.fieldMap("workExternalIdentifiers", "work.externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        workClassMap.field("url.value", "work.workUrl");
        workClassMap.fieldMap("workContributors", "work.contributorsJson").converter("workContributorsConverterId").add();
        workClassMap.field("languageCode", "work.languageCode");
        workClassMap.field("country.value", "work.iso2Country");
        workClassMap.register();

        mapperFactory.classMap(PublicationDate.class, PublicationDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day")
                .register();
        mapperFactory.classMap(WorkExternalIdentifier.class, WorkExternalIdentifierEntity.class).field("workExternalIdentifierType", "identifierType").register();
        addV2SourceMapping(mapperFactory);

        ClassMapBuilder<WorkSummary, ProfileWorkEntity> workSummaryClassMap = mapperFactory.classMap(WorkSummary.class, ProfileWorkEntity.class);
        workSummaryClassMap.field("putCode", "work.id");
        workSummaryClassMap.field("title.title.content", "work.title");
        workSummaryClassMap.field("title.translatedTitle.content", "work.translatedTitle");
        workSummaryClassMap.field("title.translatedTitle.languageCode", "work.translatedTitleLanguageCode");
        workSummaryClassMap.field("type", "work.workType");
        workSummaryClassMap.field("publicationDate", "work.publicationDate");
        workSummaryClassMap.fieldMap("externalIdentifiers", "work.externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        workSummaryClassMap.byDefault();
        workSummaryClassMap.register();

        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getFundingMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("fundingExternalIdentifiersConverterId", new FundingExternalIdentifiersConverter());
        converterFactory.registerConverter("fundingContributorsConverterId", new JsonOrikaConverter<FundingContributors>());

        ClassMapBuilder<Funding, ProfileFundingEntity> fundingClassMap = mapperFactory.classMap(Funding.class, ProfileFundingEntity.class);
        fundingClassMap.byDefault();
        fundingClassMap.field("putCode", "id");
        fundingClassMap.field("type", "type");
        fundingClassMap.field("organizationDefinedType.content", "organizationDefinedType");
        fundingClassMap.field("title.title.content", "title");
        fundingClassMap.field("title.translatedTitle.content", "translatedTitle");
        fundingClassMap.field("title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        fundingClassMap.field("description", "description");
        fundingClassMap.field("amount.content", "numericAmount");
        fundingClassMap.field("amount.currencyCode", "currencyCode");
        fundingClassMap.field("url.value", "url");
        fundingClassMap.field("organization.name", "org.name");
        fundingClassMap.field("organization.address.city", "org.city");
        fundingClassMap.field("organization.address.region", "org.region");
        fundingClassMap.field("organization.address.country", "org.country");
        fundingClassMap.field("organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier", "org.orgDisambiguated.sourceId");
        fundingClassMap.field("organization.disambiguatedOrganization.disambiguationSource", "org.orgDisambiguated.sourceType");
        fundingClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("fundingExternalIdentifiersConverterId").add();
        fundingClassMap.fieldMap("contributors", "contributorsJson").converter("fundingContributorsConverterId").add();
        fundingClassMap.register();

        ClassMapBuilder<FundingSummary, ProfileFundingEntity> fundingSummaryClassMap = mapperFactory.classMap(FundingSummary.class, ProfileFundingEntity.class);
        fundingSummaryClassMap.field("putCode", "id");
        fundingSummaryClassMap.field("type", "type");
        fundingSummaryClassMap.field("title.title.content", "title");
        fundingSummaryClassMap.field("title.translatedTitle.content", "translatedTitle");
        fundingSummaryClassMap.field("title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        fundingSummaryClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("fundingExternalIdentifiersConverterId").add();
        fundingSummaryClassMap.byDefault();
        fundingSummaryClassMap.register();

        mapperFactory.classMap(FuzzyDate.class, StartDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day").register();
        mapperFactory.classMap(FuzzyDate.class, EndDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day").register();
        addV2SourceMapping(mapperFactory);
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getEducationMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassMapBuilder<Education, OrgAffiliationRelationEntity> educationClassMap = mapperFactory.classMap(Education.class, OrgAffiliationRelationEntity.class);
        educationClassMap.byDefault();
        educationClassMap.field("putCode", "id");
        educationClassMap.field("organization.name", "org.name");
        educationClassMap.field("organization.address.city", "org.city");
        educationClassMap.field("organization.address.region", "org.region");
        educationClassMap.field("organization.address.country", "org.country");
        educationClassMap.field("organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier", "org.orgDisambiguated.sourceId");
        educationClassMap.field("organization.disambiguatedOrganization.disambiguationSource", "org.orgDisambiguated.sourceType");
        educationClassMap.field("departmentName", "department");
        educationClassMap.field("roleTitle", "title");
        educationClassMap.register();

        ClassMapBuilder<EducationSummary, OrgAffiliationRelationEntity> educationSummaryClassMap = mapperFactory.classMap(EducationSummary.class,
                OrgAffiliationRelationEntity.class);
        educationSummaryClassMap.field("departmentName", "department");
        educationSummaryClassMap.field("roleTitle", "title");
        educationSummaryClassMap.field("putCode", "id");
        educationSummaryClassMap.byDefault();
        educationSummaryClassMap.register();

        mapperFactory.classMap(FuzzyDate.class, StartDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day").register();
        mapperFactory.classMap(FuzzyDate.class, EndDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day").register();
        addV2SourceMapping(mapperFactory);
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getEmploymentMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassMapBuilder<Employment, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(Employment.class, OrgAffiliationRelationEntity.class);
        classMap.byDefault();
        classMap.field("putCode", "id");
        classMap.field("organization.name", "org.name");
        classMap.field("organization.address.city", "org.city");
        classMap.field("organization.address.region", "org.region");
        classMap.field("organization.address.country", "org.country");
        classMap.field("organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier", "org.orgDisambiguated.sourceId");
        classMap.field("organization.disambiguatedOrganization.disambiguationSource", "org.orgDisambiguated.sourceType");
        classMap.field("departmentName", "department");
        classMap.field("roleTitle", "title");
        classMap.register();

        ClassMapBuilder<EmploymentSummary, OrgAffiliationRelationEntity> employmentSummaryClassMap = mapperFactory.classMap(EmploymentSummary.class,
                OrgAffiliationRelationEntity.class);
        employmentSummaryClassMap.field("departmentName", "department");
        employmentSummaryClassMap.field("roleTitle", "title");
        employmentSummaryClassMap.field("putCode", "id");
        employmentSummaryClassMap.byDefault();
        employmentSummaryClassMap.register();

        mapperFactory.classMap(FuzzyDate.class, StartDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day").register();
        mapperFactory.classMap(FuzzyDate.class, EndDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day").register();
        addV2SourceMapping(mapperFactory);
        return mapperFactory.getMapperFacade();
    }

    private ClassMapBuilder<?, ?> mapCommonFields(ClassMapBuilder<?, ?> builder) {
        return builder.field("dateCreated", "createdDate").field("id", "putCode").byDefault();
    }

    private void addV2SourceMapping(MapperFactory mapperFactory) {
        mapperFactory.classMap(org.orcid.jaxb.model.common.Source.class, SourceEntity.class).fieldAToB("sourceOrcid.path", "sourceProfile.id")
                .fieldAToB("sourceClientId.path", "sourceClient.id").customize(new CustomMapper<org.orcid.jaxb.model.common.Source, SourceEntity>() {
                    @Override
                    public void mapBtoA(SourceEntity sourceEntity, org.orcid.jaxb.model.common.Source source, MappingContext context) {
                        String sourceId = sourceEntity.getSourceId();
                        if (OrcidStringUtils.isClientId(sourceId)) {
                            SourceClientId sourceClientId = new SourceClientId(sourceId);
                            sourceClientId.setHost(orcidUrlManager.getBaseHost());
                            sourceClientId.setUri(orcidUrlManager.getBaseUrl() + "/client/" + sourceId);
                            source.setSourceClientId(sourceClientId);
                        } else {
                            SourceOrcid sourceOrcid = new SourceOrcid(sourceId);
                            sourceOrcid.setHost(orcidUrlManager.getBaseHost());
                            sourceOrcid.setUri(orcidUrlManager.getBaseUriHttp() + "/" + sourceId);
                            source.setSourceOrcid(sourceOrcid);
                        }
                    }
                }).byDefault().register();
    }

    @Override
    public Class<?> getObjectType() {
        return MapperFacade.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
