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
import org.orcid.core.adapter.impl.jsonidentifiers.WorkExternalIDsConverter;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.jaxb.model.client_v2.Client;
import org.orcid.jaxb.model.common_v2.FuzzyDate;
import org.orcid.jaxb.model.common_v2.PublicationDate;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.SourceClientId;
import org.orcid.jaxb.model.common_v2.SourceName;
import org.orcid.jaxb.model.common_v2.SourceOrcid;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.notification.amended_v2.NotificationAmended;
import org.orcid.jaxb.model.notification.custom_v2.NotificationCustom;
import org.orcid.jaxb.model.notification.permission_v2.AuthorizationUrl;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.FundingContributors;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.SourceAware;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkContributors;
import org.orcid.model.notification.institutional_sign_in_v2.NotificationInstitutionalConnection;
import org.orcid.model.record_correction.RecordCorrection;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.CompletionDateEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;
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
import org.orcid.persistence.jpa.entities.SourceAwareEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.utils.OrcidStringUtils;
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
    private ClientDetailsManagerReadOnly clientDetailsManagerReadOnly;

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

    @SuppressWarnings("unchecked")
    public void registerSourceConverters(MapperFactory mapperFactory, ClassMapBuilder<? extends SourceAware, ? extends SourceAwareEntity<?>> classMapBuilder) {
        @SuppressWarnings("rawtypes")
        SourceMapper sourceMapper = new SourceMapper();
        mapperFactory.classMap(SourceAware.class, SourceAwareEntity.class).customize(sourceMapper).register();
    }

    private class SourceMapper<T, U> extends CustomMapper<SourceAware, SourceAwareEntity<?>> {

        @Override
        public void mapAtoB(SourceAware a, SourceAwareEntity<?> b, MappingContext context) {
            Source source = a.getSource();
            if (source == null) {
                return;
            }
            String sourceId = source.retrieveSourcePath();
            if (StringUtils.isEmpty(sourceId)) {
                return;
            }
            if (isClient(sourceId)) {
                b.setClientSourceId(sourceId);
            } else {
                b.setSourceId(sourceId);
            }
        }

        @Override
        public void mapBtoA(SourceAwareEntity<?> b, SourceAware a, MappingContext context) {
            String sourceId = b.getElementSourceId();
            if (StringUtils.isEmpty(sourceId)) {
                return;
            }
            Source source = null;
            if (isClient(sourceId)) {
                source = createClientSource(sourceId);
            } else {
                source = createOrcidSource(sourceId);
            }
            a.setSource(source);
            source.setSourceName(new SourceName(sourceNameCacheManager.retrieve(sourceId)));
        }

        private boolean isClient(String sourceId) {
            return OrcidStringUtils.isClientId(sourceId) || clientDetailsManagerReadOnly.isLegacyClientId(sourceId);
        }

        private Source createClientSource(String sourceId) {
            Source source = new Source();
            SourceClientId sourceClientId = new SourceClientId();
            source.setSourceClientId(sourceClientId);
            sourceClientId.setHost(orcidUrlManager.getBaseHost());
            sourceClientId.setUri(orcidUrlManager.getBaseUriHttp() + "/client/" + sourceId);
            sourceClientId.setPath(sourceId);
            return source;
        }

        private Source createOrcidSource(String sourceId) {
            Source source = new Source();
            SourceOrcid sourceOrcid = new SourceOrcid();
            source.setSourceOrcid(sourceOrcid);
            sourceOrcid.setHost(orcidUrlManager.getBaseHost());
            sourceOrcid.setUri(orcidUrlManager.getBaseUriHttp() + "/" + sourceId);
            sourceOrcid.setPath(sourceId);
            return source;
        }

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
        fundingClassMap.fieldBToA("org.orgDisambiguated.id", "organization.disambiguatedOrganization.id");
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
        fundingSummaryClassMap.fieldBToA("org.orgDisambiguated.id", "organization.disambiguatedOrganization.id");

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
        educationClassMap.fieldBToA("org.name", "organization.name");
        educationClassMap.fieldBToA("org.city", "organization.address.city");
        educationClassMap.fieldBToA("org.region", "organization.address.region");
        educationClassMap.fieldBToA("org.country", "organization.address.country");
        educationClassMap.fieldBToA("org.orgDisambiguated.sourceId", "organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier");
        educationClassMap.fieldBToA("org.orgDisambiguated.sourceType", "organization.disambiguatedOrganization.disambiguationSource");
        educationClassMap.fieldBToA("org.orgDisambiguated.id", "organization.disambiguatedOrganization.id");
        educationClassMap.field("departmentName", "department");
        educationClassMap.field("roleTitle", "title");
        educationClassMap.register();

        ClassMapBuilder<EducationSummary, OrgAffiliationRelationEntity> educationSummaryClassMap = mapperFactory.classMap(EducationSummary.class,
                OrgAffiliationRelationEntity.class);
        addV2CommonFields(educationSummaryClassMap);
        registerSourceConverters(mapperFactory, educationSummaryClassMap);
        educationSummaryClassMap.fieldBToA("org.name", "organization.name");
        educationSummaryClassMap.fieldBToA("org.city", "organization.address.city");
        educationSummaryClassMap.fieldBToA("org.region", "organization.address.region");
        educationSummaryClassMap.fieldBToA("org.country", "organization.address.country");
        educationSummaryClassMap.fieldBToA("org.orgDisambiguated.sourceId", "organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier");
        educationSummaryClassMap.fieldBToA("org.orgDisambiguated.sourceType", "organization.disambiguatedOrganization.disambiguationSource");
        educationSummaryClassMap.fieldBToA("org.orgDisambiguated.id", "organization.disambiguatedOrganization.id");
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
        classMap.fieldBToA("org.name", "organization.name");
        classMap.fieldBToA("org.city", "organization.address.city");
        classMap.fieldBToA("org.region", "organization.address.region");
        classMap.fieldBToA("org.country", "organization.address.country");
        classMap.fieldBToA("org.orgDisambiguated.sourceId", "organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier");
        classMap.fieldBToA("org.orgDisambiguated.sourceType", "organization.disambiguatedOrganization.disambiguationSource");
        classMap.fieldBToA("org.orgDisambiguated.id", "organization.disambiguatedOrganization.id");
        classMap.field("departmentName", "department");
        classMap.field("roleTitle", "title");
        classMap.register();

        ClassMapBuilder<EmploymentSummary, OrgAffiliationRelationEntity> employmentSummaryClassMap = mapperFactory.classMap(EmploymentSummary.class,
                OrgAffiliationRelationEntity.class);
        addV2CommonFields(employmentSummaryClassMap);
        registerSourceConverters(mapperFactory, employmentSummaryClassMap);
        employmentSummaryClassMap.fieldBToA("org.name", "organization.name");
        employmentSummaryClassMap.fieldBToA("org.city", "organization.address.city");
        employmentSummaryClassMap.fieldBToA("org.region", "organization.address.region");
        employmentSummaryClassMap.fieldBToA("org.country", "organization.address.country");
        employmentSummaryClassMap.fieldBToA("org.orgDisambiguated.sourceId", "organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier");
        employmentSummaryClassMap.fieldBToA("org.orgDisambiguated.sourceType", "organization.disambiguatedOrganization.disambiguationSource");
        employmentSummaryClassMap.fieldBToA("org.orgDisambiguated.id", "organization.disambiguatedOrganization.id");
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
    
    public MapperFacade getInvalidRecordDataChangeMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ClassMapBuilder<RecordCorrection, InvalidRecordDataChangeEntity> classMap = mapperFactory.classMap(RecordCorrection.class, InvalidRecordDataChangeEntity.class);        
        classMap.fieldBToA("id", "sequence");
        classMap.fieldBToA("sqlUsedToUpdate", "sqlUsedToUpdate");
        classMap.fieldBToA("description", "description");
        classMap.fieldBToA("numChanged", "numChanged");
        classMap.fieldBToA("type", "type");
        classMap.byDefault();
        classMap.register();        
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
