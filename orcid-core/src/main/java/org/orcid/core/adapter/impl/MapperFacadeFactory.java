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

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.adapter.impl.jsonidentifiers.ExternalIdentifierTypeConverter;
import org.orcid.core.adapter.impl.jsonidentifiers.FundingExternalIDsConverter;
import org.orcid.core.adapter.impl.jsonidentifiers.PeerReviewWorkExternalIDConverter;
import org.orcid.core.adapter.impl.jsonidentifiers.SingleWorkExternalIdentifierFromJsonConverter;
import org.orcid.core.adapter.impl.jsonidentifiers.SourceClientIdConverter;
import org.orcid.core.adapter.impl.jsonidentifiers.SourceNameConverter;
import org.orcid.core.adapter.impl.jsonidentifiers.SourceOrcidConverter;
import org.orcid.core.adapter.impl.jsonidentifiers.WorkExternalIDsConverter;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.client_rc4.Client;
import org.orcid.jaxb.model.common_rc4.FuzzyDate;
import org.orcid.jaxb.model.common_rc4.PublicationDate;
import org.orcid.jaxb.model.groupid_rc4.GroupIdRecord;
import org.orcid.jaxb.model.notification.amended_rc4.NotificationAmended;
import org.orcid.jaxb.model.notification.custom_rc4.NotificationCustom;
import org.orcid.jaxb.model.notification.permission_rc4.AuthorizationUrl;
import org.orcid.jaxb.model.notification.permission_rc4.Item;
import org.orcid.jaxb.model.notification.permission_rc4.NotificationPermission;
import org.orcid.jaxb.model.record.summary_rc4.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc4.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc4.WorkSummary;
import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.jaxb.model.record_rc4.Education;
import org.orcid.jaxb.model.record_rc4.Email;
import org.orcid.jaxb.model.record_rc4.Employment;
import org.orcid.jaxb.model.record_rc4.Funding;
import org.orcid.jaxb.model.record_rc4.FundingContributors;
import org.orcid.jaxb.model.record_rc4.Keyword;
import org.orcid.jaxb.model.record_rc4.Name;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.PeerReview;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.Work;
import org.orcid.jaxb.model.record_rc4.WorkContributors;
import org.orcid.model.notification.institutional_sign_in_rc4.NotificationInstitutionalConnection;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.CompletionDateEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;
import org.orcid.persistence.jpa.entities.NotificationWorkEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.springframework.beans.factory.FactoryBean;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.TypeFactory;

/**
 * 
 * @author Will Simpson
 * 
 */
public class MapperFacadeFactory implements FactoryBean<MapperFacade> {

    private static final String LAST_RESORT_IDENTITY_PROVIDER_NAME = "identity provider";

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private WorkDao workDao;
    
    @Resource
    private SourceNameCacheManager sourceNameCacheManager;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private IdentityProviderManager identityProviderManager;

    @Override
    public MapperFacade getObject() throws Exception {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        // Register converters
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("singleWorkExternalIdentifierFromJsonConverter", new SingleWorkExternalIdentifierFromJsonConverter());
        converterFactory.registerConverter("externalIdentifierIdConverter", new ExternalIdentifierTypeConverter());

        // Register factories
        mapperFactory.registerObjectFactory(new WorkEntityFactory(workDao), TypeFactory.<NotificationWorkEntity> valueOf(NotificationWorkEntity.class),
                TypeFactory.<Item> valueOf(Item.class));

        // Custom notification
        ClassMapBuilder<NotificationCustom, NotificationCustomEntity> notificationCustomClassMap = mapperFactory.classMap(NotificationCustom.class, NotificationCustomEntity.class); 
        registerSourceConverters(mapperFactory, notificationCustomClassMap);
        mapCommonFields(notificationCustomClassMap).register();        
        
        // Permission notification
        ClassMapBuilder<NotificationPermission, NotificationAddItemsEntity> notificationPermissionClassMap = mapperFactory.classMap(NotificationPermission.class, NotificationAddItemsEntity.class);
        registerSourceConverters(mapperFactory, notificationPermissionClassMap);
        mapCommonFields(
                notificationPermissionClassMap.field("authorizationUrl.uri", "authorizationUrl")
                        .field("items.items", "notificationItems").customize(new CustomMapper<NotificationPermission, NotificationAddItemsEntity>() {
                            @Override
                            public void mapAtoB(NotificationPermission notification, NotificationAddItemsEntity entity, MappingContext context) {
                                if (StringUtils.isBlank(entity.getAuthorizationUrl())) {
                                    String authUrl = orcidUrlManager.getBaseUrl() + notification.getAuthorizationUrl().getPath();
                                    // validate
                                    validateAndConvertToURI(authUrl);
                                    entity.setAuthorizationUrl(authUrl);
                                }                                
                            }

                            @Override
                            public void mapBtoA(NotificationAddItemsEntity entity, NotificationPermission notification, MappingContext context) {
                                AuthorizationUrl authUrl = notification.getAuthorizationUrl();
                                if (authUrl != null) {
                                    authUrl.setPath(extractFullPath(authUrl.getUri()));
                                    authUrl.setHost(orcidUrlManager.getBaseHost());
                                }
                            }
                        })).register();
        
        // Institutional sign in notification
        ClassMapBuilder<NotificationInstitutionalConnection, NotificationInstitutionalConnectionEntity> institutionalConnectionNotificationClassMap = mapperFactory.classMap(NotificationInstitutionalConnection.class, NotificationInstitutionalConnectionEntity.class);
        registerSourceConverters(mapperFactory, institutionalConnectionNotificationClassMap); 
        mapCommonFields(
                institutionalConnectionNotificationClassMap.field("authorizationUrl.uri", "authorizationUrl")
                        .customize(new CustomMapper<NotificationInstitutionalConnection, NotificationInstitutionalConnectionEntity>() {
                            @Override
                            public void mapAtoB(NotificationInstitutionalConnection notification, NotificationInstitutionalConnectionEntity entity, MappingContext context) {
                                if (StringUtils.isBlank(entity.getAuthorizationUrl())) {
                                    String authUrl = orcidUrlManager.getBaseUrl() + notification.getAuthorizationUrl().getPath();
                                    // validate
                                    validateAndConvertToURI(authUrl);
                                    entity.setAuthorizationUrl(authUrl);
                                }                                
                            }

                            @Override
                            public void mapBtoA(NotificationInstitutionalConnectionEntity entity, NotificationInstitutionalConnection notification, MappingContext context) {
                                AuthorizationUrl authUrl = notification.getAuthorizationUrl();
                                if (authUrl != null) {
                                    authUrl.setPath(extractFullPath(authUrl.getUri()));
                                    authUrl.setHost(orcidUrlManager.getBaseHost());
                                }
                                String providerId = entity.getAuthenticationProviderId();
                                if (StringUtils.isNotBlank(providerId)) {
                                    String idpName = identityProviderManager.retrieveIdentitifyProviderName(providerId);
                                    notification.setIdpName(idpName);
                                } else {
                                    notification.setIdpName(LAST_RESORT_IDENTITY_PROVIDER_NAME);
                                }
                            }
                        })).register();
        
        
        // Amend notification
        ClassMapBuilder<NotificationAmended, NotificationAmendedEntity> amendNotificationClassMap = mapperFactory.classMap(NotificationAmended.class, NotificationAmendedEntity.class);
        registerSourceConverters(mapperFactory, amendNotificationClassMap); 
        mapCommonFields(amendNotificationClassMap).register();
        mapperFactory.classMap(NotificationItemEntity.class, Item.class)
            .fieldMap("externalIdType", "externalIdentifier.type")
            .converter("externalIdentifierIdConverter")
            .add()
            .field("externalIdValue", "externalIdentifier.value")
            .byDefault()
            .register();                
        
        return mapperFactory.getMapperFacade();
    }

    private String extractFullPath(String uriString) {
        URI uri = validateAndConvertToURI(uriString);
        StringBuilder pathBuilder = new StringBuilder(uri.getRawPath());
        String query = uri.getRawQuery();
        if (query != null) {
            pathBuilder.append('?');
            pathBuilder.append(query);
        }
        String fragment = uri.getRawFragment();
        if (fragment != null) {
            pathBuilder.append(fragment);
        }
        return pathBuilder.toString();
    }

    private URI validateAndConvertToURI(String uriString) {
        try {
            URI uri = new URI(uriString);
            return uri;
        } catch (URISyntaxException e) {
            throw new OrcidValidationException("Problem parsing uri", e);
        }
    }

    public void registerSourceConverters(MapperFactory mapperFactory, ClassMapBuilder classMapBuilder) {
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("sourceOrcidConverter", new SourceOrcidConverter(orcidUrlManager));
        converterFactory.registerConverter("sourceClientIdConverter", new SourceClientIdConverter(orcidUrlManager));
        converterFactory.registerConverter("sourceNameConverter", new SourceNameConverter(sourceNameCacheManager));
        classMapBuilder.fieldMap("source.sourceOrcid", "sourceId").converter("sourceOrcidConverter").add();
        classMapBuilder.fieldMap("source.sourceClientId", "clientSourceId").converter("sourceClientIdConverter").add();
        classMapBuilder.fieldMap("source.sourceName", "elementSourceId").converter("sourceNameConverter").add();
    }
    
    public MapperFacade getExternalIdentifierMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassMapBuilder<PersonExternalIdentifier, ExternalIdentifierEntity> externalIdentifierClassMap = mapperFactory.classMap(PersonExternalIdentifier.class, ExternalIdentifierEntity.class);        
        addV2DateFields(externalIdentifierClassMap);        
        externalIdentifierClassMap.field("putCode", "id");
        externalIdentifierClassMap.field("type", "externalIdCommonName");
        externalIdentifierClassMap.field("value", "externalIdReference");
        externalIdentifierClassMap.field("url.value", "externalIdUrl");
        externalIdentifierClassMap.fieldBToA("displayIndex", "displayIndex");
        externalIdentifierClassMap.byDefault();
        registerSourceConverters(mapperFactory, externalIdentifierClassMap);
        
        //TODO: add relationship to database schema for people.
        externalIdentifierClassMap.register();
        return mapperFactory.getMapperFacade();
    }
    
    public MapperFacade getResearcherUrlMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassMapBuilder<ResearcherUrl, ResearcherUrlEntity> researcherUrlClassMap = mapperFactory.classMap(ResearcherUrl.class, ResearcherUrlEntity.class);        
        addV2DateFields(researcherUrlClassMap);
        registerSourceConverters(mapperFactory, researcherUrlClassMap);
        researcherUrlClassMap.field("putCode", "id");
        researcherUrlClassMap.field("url.value", "url");
        researcherUrlClassMap.field("urlName", "urlName");
        researcherUrlClassMap.fieldBToA("displayIndex", "displayIndex");
        researcherUrlClassMap.byDefault();
        researcherUrlClassMap.register();
        return mapperFactory.getMapperFacade();
    }
    
    public MapperFacade getOtherNameMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassMapBuilder<OtherName, OtherNameEntity> otherNameClassMap = mapperFactory.classMap(OtherName.class, OtherNameEntity.class);        
        addV2DateFields(otherNameClassMap);
        registerSourceConverters(mapperFactory, otherNameClassMap);
        otherNameClassMap.field("putCode", "id");
        otherNameClassMap.field("content", "displayName");
        otherNameClassMap.field("path", "profile.orcid");
        otherNameClassMap.fieldBToA("displayIndex", "displayIndex");
        otherNameClassMap.byDefault();
        otherNameClassMap.register();
        return mapperFactory.getMapperFacade();
    }
    
    public MapperFacade getKeywordMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassMapBuilder<Keyword, ProfileKeywordEntity> keywordClassMap = mapperFactory.classMap(Keyword.class, ProfileKeywordEntity.class);        
        addV2DateFields(keywordClassMap);
        registerSourceConverters(mapperFactory, keywordClassMap);
        keywordClassMap.field("putCode", "id");
        keywordClassMap.field("content", "keywordName");
        keywordClassMap.fieldBToA("displayIndex", "displayIndex");
        keywordClassMap.byDefault();
        keywordClassMap.register();        
        return mapperFactory.getMapperFacade();
    }
    
    public MapperFacade getAddressMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassMapBuilder<Address, AddressEntity> addressClassMap = mapperFactory.classMap(Address.class, AddressEntity.class);        
        addV2DateFields(addressClassMap);
        registerSourceConverters(mapperFactory, addressClassMap);
        addressClassMap.field("putCode", "id");
        addressClassMap.field("country.value", "iso2Country");
        addressClassMap.field("visibility", "visibility");
        addressClassMap.fieldBToA("displayIndex", "displayIndex");
        addressClassMap.byDefault();
        addressClassMap.register();        
        return mapperFactory.getMapperFacade();
    }
    
    public MapperFacade getEmailMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassMapBuilder<Email, EmailEntity> emailClassMap = mapperFactory.classMap(Email.class, EmailEntity.class);
        emailClassMap.byDefault();
        emailClassMap.field("email", "id");
        emailClassMap.field("primary", "primary");
        emailClassMap.field("verified", "verified");
        addV2DateFields(emailClassMap);
        registerSourceConverters(mapperFactory, emailClassMap);
        emailClassMap.register();
        return mapperFactory.getMapperFacade();
    }
    
    public MapperFacade getWorkMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("workExternalIdentifiersConverterId", new WorkExternalIDsConverter());
        converterFactory.registerConverter("workContributorsConverterId", new JsonOrikaConverter<WorkContributors>());
        
        ClassMapBuilder<Work, WorkEntity> workClassMap = mapperFactory.classMap(Work.class, WorkEntity.class);
        workClassMap.byDefault();
        workClassMap.field("putCode", "id");
        addV2DateFields(workClassMap);
        registerSourceConverters(mapperFactory, workClassMap);
        workClassMap.field("journalTitle.content", "journalTitle");
        workClassMap.field("workTitle.title.content", "title");
        workClassMap.field("workTitle.translatedTitle.content", "translatedTitle");
        workClassMap.field("workTitle.translatedTitle.languageCode", "translatedTitleLanguageCode");
        workClassMap.field("workTitle.subtitle.content", "subtitle");
        workClassMap.field("shortDescription", "description");
        workClassMap.field("workCitation.workCitationType", "citationType");
        workClassMap.field("workCitation.citation", "citation");
        workClassMap.field("workType", "workType");
        workClassMap.field("publicationDate", "publicationDate");        
        workClassMap.fieldMap("workExternalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        workClassMap.field("url.value", "workUrl");
        workClassMap.fieldMap("workContributors", "contributorsJson").converter("workContributorsConverterId").add();
        workClassMap.field("languageCode", "languageCode");
        workClassMap.field("country.value", "iso2Country");
        workClassMap.register();

        ClassMapBuilder<WorkSummary, WorkEntity> workSummaryClassMap = mapperFactory.classMap(WorkSummary.class, WorkEntity.class);
        registerSourceConverters(mapperFactory, workSummaryClassMap);
        workSummaryClassMap.field("putCode", "id");
        workSummaryClassMap.field("title.title.content", "title");
        workSummaryClassMap.field("title.translatedTitle.content", "translatedTitle");
        workSummaryClassMap.field("title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        workSummaryClassMap.field("type", "workType");
        workSummaryClassMap.field("publicationDate", "publicationDate");
        workSummaryClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        workSummaryClassMap.byDefault();
        workSummaryClassMap.register();

        ClassMapBuilder<WorkSummary, MinimizedWorkEntity> workSummaryMinimizedClassMap = mapperFactory.classMap(WorkSummary.class, MinimizedWorkEntity.class);
        addV2CommonFields(workSummaryMinimizedClassMap);
        registerSourceConverters(mapperFactory, workSummaryMinimizedClassMap);
        workSummaryMinimizedClassMap.field("title.title.content", "title");
        workSummaryMinimizedClassMap.field("title.translatedTitle.content", "translatedTitle");
        workSummaryMinimizedClassMap.field("title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        workSummaryMinimizedClassMap.field("type", "workType");
        workSummaryMinimizedClassMap.field("publicationDate.year.value", "publicationYear");
        workSummaryMinimizedClassMap.field("publicationDate.month.value", "publicationMonth");
        workSummaryMinimizedClassMap.field("publicationDate.day.value", "publicationDay");
        workSummaryMinimizedClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        workSummaryMinimizedClassMap.byDefault();
        workSummaryMinimizedClassMap.register();

        ClassMapBuilder<Work, MinimizedWorkEntity> minimizedWorkClassMap = mapperFactory.classMap(Work.class, MinimizedWorkEntity.class);
        minimizedWorkClassMap.byDefault();
        registerSourceConverters(mapperFactory, minimizedWorkClassMap);
        minimizedWorkClassMap.field("putCode", "id");
        minimizedWorkClassMap.field("journalTitle.content", "journalTitle");
        minimizedWorkClassMap.field("workTitle.title.content", "title");
        minimizedWorkClassMap.field("workTitle.translatedTitle.content", "translatedTitle");
        minimizedWorkClassMap.field("workTitle.translatedTitle.languageCode", "translatedTitleLanguageCode");
        minimizedWorkClassMap.field("workTitle.subtitle.content", "subtitle");
        minimizedWorkClassMap.field("shortDescription", "description");
        minimizedWorkClassMap.field("workType", "workType");
        minimizedWorkClassMap.field("publicationDate.year.value", "publicationYear");
        minimizedWorkClassMap.field("publicationDate.month.value", "publicationMonth");
        minimizedWorkClassMap.field("publicationDate.day.value", "publicationDay");
        minimizedWorkClassMap.fieldMap("workExternalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        minimizedWorkClassMap.field("url.value", "workUrl");
        minimizedWorkClassMap.register();

        mapperFactory.classMap(PublicationDate.class, PublicationDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day")
                .register();        

        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getFundingMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("fundingExternalIdentifiersConverterId", new FundingExternalIDsConverter());
        converterFactory.registerConverter("fundingContributorsConverterId", new JsonOrikaConverter<FundingContributors>());

        ClassMapBuilder<Funding, ProfileFundingEntity> fundingClassMap = mapperFactory.classMap(Funding.class, ProfileFundingEntity.class);
        addV2CommonFields(fundingClassMap);
        registerSourceConverters(mapperFactory, fundingClassMap);
        fundingClassMap.field("type", "type");
        fundingClassMap.field("organizationDefinedType.content", "organizationDefinedType");
        fundingClassMap.field("title.title.content", "title");
        fundingClassMap.field("title.translatedTitle.content", "translatedTitle");
        fundingClassMap.field("title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        fundingClassMap.field("description", "description");
        fundingClassMap.field("amount.content", "numericAmount");
        fundingClassMap.field("amount.currencyCode", "currencyCode");
        fundingClassMap.field("url.value", "url");
        fundingClassMap.fieldBToA("org.name", "organization.name");
        fundingClassMap.fieldBToA("org.city", "organization.address.city");
        fundingClassMap.fieldBToA("org.region", "organization.address.region");
        fundingClassMap.fieldBToA("org.country", "organization.address.country");
        fundingClassMap.fieldBToA("org.orgDisambiguated.sourceId", "organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier");
        fundingClassMap.fieldBToA("org.orgDisambiguated.sourceType", "organization.disambiguatedOrganization.disambiguationSource");
        fundingClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("fundingExternalIdentifiersConverterId").add();
        fundingClassMap.fieldMap("contributors", "contributorsJson").converter("fundingContributorsConverterId").add();
        fundingClassMap.register();

        ClassMapBuilder<FundingSummary, ProfileFundingEntity> fundingSummaryClassMap = mapperFactory.classMap(FundingSummary.class, ProfileFundingEntity.class);
        addV2CommonFields(fundingSummaryClassMap);
        registerSourceConverters(mapperFactory, fundingSummaryClassMap);
        fundingSummaryClassMap.field("type", "type");
        fundingSummaryClassMap.field("title.title.content", "title");
        fundingSummaryClassMap.field("title.translatedTitle.content", "translatedTitle");
        fundingSummaryClassMap.field("title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        fundingSummaryClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("fundingExternalIdentifiersConverterId").add();
        
        fundingSummaryClassMap.fieldBToA("org.name", "organization.name");
        fundingSummaryClassMap.fieldBToA("org.city", "organization.address.city");
        fundingSummaryClassMap.fieldBToA("org.region", "organization.address.region");
        fundingSummaryClassMap.fieldBToA("org.country", "organization.address.country");
        fundingSummaryClassMap.fieldBToA("org.orgDisambiguated.sourceId", "organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier");
        fundingSummaryClassMap.fieldBToA("org.orgDisambiguated.sourceType", "organization.disambiguatedOrganization.disambiguationSource");

        fundingSummaryClassMap.register();

        mapperFactory.classMap(FuzzyDate.class, StartDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day").register();
        mapperFactory.classMap(FuzzyDate.class, EndDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day").register();        
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getEducationMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassMapBuilder<Education, OrgAffiliationRelationEntity> educationClassMap = mapperFactory.classMap(Education.class, OrgAffiliationRelationEntity.class);
        addV2CommonFields(educationClassMap);
        registerSourceConverters(mapperFactory, educationClassMap);
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
        addV2CommonFields(educationSummaryClassMap);
        registerSourceConverters(mapperFactory, educationSummaryClassMap);
        educationSummaryClassMap.field("organization.name", "org.name");
        educationSummaryClassMap.field("organization.address.city", "org.city");
        educationSummaryClassMap.field("organization.address.region", "org.region");
        educationSummaryClassMap.field("organization.address.country", "org.country");
        educationSummaryClassMap.field("organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier", "org.orgDisambiguated.sourceId");
        educationSummaryClassMap.field("organization.disambiguatedOrganization.disambiguationSource", "org.orgDisambiguated.sourceType");
        educationSummaryClassMap.field("departmentName", "department");
        educationSummaryClassMap.field("roleTitle", "title");
        educationSummaryClassMap.register();

        mapperFactory.classMap(FuzzyDate.class, StartDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day").register();
        mapperFactory.classMap(FuzzyDate.class, EndDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day").register();        
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getEmploymentMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassMapBuilder<Employment, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(Employment.class, OrgAffiliationRelationEntity.class);
        addV2CommonFields(classMap);
        registerSourceConverters(mapperFactory, classMap);
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
        addV2CommonFields(employmentSummaryClassMap);
        registerSourceConverters(mapperFactory, employmentSummaryClassMap);
        employmentSummaryClassMap.field("organization.name", "org.name");
        employmentSummaryClassMap.field("organization.address.city", "org.city");
        employmentSummaryClassMap.field("organization.address.region", "org.region");
        employmentSummaryClassMap.field("organization.address.country", "org.country");
        employmentSummaryClassMap.field("organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier", "org.orgDisambiguated.sourceId");
        employmentSummaryClassMap.field("organization.disambiguatedOrganization.disambiguationSource", "org.orgDisambiguated.sourceType");
        employmentSummaryClassMap.field("departmentName", "department");
        employmentSummaryClassMap.field("roleTitle", "title");
        employmentSummaryClassMap.register();

        mapperFactory.classMap(FuzzyDate.class, StartDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day").register();
        mapperFactory.classMap(FuzzyDate.class, EndDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day").register();        
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getPeerReviewMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("workExternalIdentifiersConverterId", new WorkExternalIDsConverter());
        converterFactory.registerConverter("workExternalIdentifierConverterId", new PeerReviewWorkExternalIDConverter());
        //do same as work
        
        ClassMapBuilder<PeerReview, PeerReviewEntity> classMap = mapperFactory.classMap(PeerReview.class, PeerReviewEntity.class);
        addV2CommonFields(classMap);
        registerSourceConverters(mapperFactory, classMap);
        classMap.field("url.value", "url");
        classMap.field("organization.name", "org.name");
        classMap.field("organization.address.city", "org.city");
        classMap.field("organization.address.region", "org.region");
        classMap.field("organization.address.country", "org.country");
        classMap.field("organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier", "org.orgDisambiguated.sourceId");
        classMap.field("organization.disambiguatedOrganization.disambiguationSource", "org.orgDisambiguated.sourceType");
        classMap.field("groupId", "groupId");
        classMap.field("subjectType", "subjectType");
        classMap.field("subjectUrl.value", "subjectUrl");
        classMap.field("subjectName.title.content", "subjectName");
        classMap.field("subjectName.translatedTitle.content", "subjectTranslatedName");
        classMap.field("subjectName.translatedTitle.languageCode", "subjectTranslatedNameLanguageCode");
        classMap.field("subjectContainerName.content", "subjectContainerName");
        classMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        classMap.fieldMap("subjectExternalIdentifier", "subjectExternalIdentifiersJson").converter("workExternalIdentifierConverterId").add();

        classMap.register();

        ClassMapBuilder<PeerReviewSummary, PeerReviewEntity> peerReviewSummaryClassMap = mapperFactory.classMap(PeerReviewSummary.class, PeerReviewEntity.class);
        addV2CommonFields(peerReviewSummaryClassMap);
        registerSourceConverters(mapperFactory, peerReviewSummaryClassMap);
        peerReviewSummaryClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        peerReviewSummaryClassMap.field("organization.name", "org.name");
        peerReviewSummaryClassMap.field("organization.address.city", "org.city");
        peerReviewSummaryClassMap.field("organization.address.region", "org.region");
        peerReviewSummaryClassMap.field("organization.address.country", "org.country");
        peerReviewSummaryClassMap.field("organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier", "org.orgDisambiguated.sourceId");
        peerReviewSummaryClassMap.field("organization.disambiguatedOrganization.disambiguationSource", "org.orgDisambiguated.sourceType");        
        peerReviewSummaryClassMap.register();

        mapperFactory.classMap(FuzzyDate.class, CompletionDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day")
                .register();
        
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getGroupIdRecordMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        ClassMapBuilder<GroupIdRecord, GroupIdRecordEntity> classMap = mapperFactory.classMap(GroupIdRecord.class, GroupIdRecordEntity.class);
        addV2CommonFields(classMap);
        registerSourceConverters(mapperFactory, classMap);
        classMap.field("name", "groupName");
        classMap.field("groupId", "groupId");
        classMap.field("description", "groupDescription");
        classMap.field("type", "groupType");
        classMap.register();
        
        return mapperFactory.getMapperFacade();
    }
    
    public MapperFacade getClientMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassMapBuilder<Client, ClientDetailsEntity> clientClassMap = mapperFactory.classMap(Client.class, ClientDetailsEntity.class);        
        clientClassMap.field("name", "clientName");
        clientClassMap.field("description", "clientDescription");
        clientClassMap.byDefault();
        clientClassMap.register();        
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getNameMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassMapBuilder<Name, RecordNameEntity> nameClassMap = mapperFactory.classMap(Name.class, RecordNameEntity.class);        
        addV2DateFields(nameClassMap);        
        nameClassMap.field("creditName.content", "creditName");
        nameClassMap.field("givenNames.content", "givenNames");
        nameClassMap.field("familyName.content", "familyName");
        nameClassMap.field("path", "profile.id");
        nameClassMap.byDefault();
        nameClassMap.register();
        return mapperFactory.getMapperFacade();
    }
    
    private ClassMapBuilder<?, ?> mapCommonFields(ClassMapBuilder<?, ?> builder) {
        return builder.field("createdDate", "dateCreated").field("putCode", "id").byDefault();
    }

    private void addV2CommonFields(ClassMapBuilder<?, ?> classMap) {
        classMap.byDefault();
        classMap.field("putCode", "id");
        addV2DateFields(classMap);
    }

    private void addV2DateFields(ClassMapBuilder<?, ?> classMap) {
        classMap.field("createdDate.value", "dateCreated");
        classMap.field("lastModifiedDate.value", "lastModified");
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
