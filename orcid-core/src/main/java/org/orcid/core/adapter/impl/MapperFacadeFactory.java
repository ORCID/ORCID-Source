package org.orcid.core.adapter.impl;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.adapter.converter.CreditNameConverter;
import org.orcid.core.adapter.converter.EmptyStringToNullConverter;
import org.orcid.core.adapter.converter.FamilyNameConverter;
import org.orcid.core.adapter.converter.FundingContributorsConverter;
import org.orcid.core.adapter.converter.GivenNamesConverter;
import org.orcid.core.adapter.converter.PeerReviewSubjectTypeConverter;
import org.orcid.core.adapter.converter.VisibilityConverter;
import org.orcid.core.adapter.converter.WorkContributorsConverter;
import org.orcid.core.adapter.jsonidentifier.converter.ExternalIdentifierTypeConverter;
import org.orcid.core.adapter.jsonidentifier.converter.JSONFundingExternalIdentifiersConverterV2;
import org.orcid.core.adapter.jsonidentifier.converter.JSONPeerReviewWorkExternalIdentifierConverterV2;
import org.orcid.core.adapter.jsonidentifier.converter.JSONWorkExternalIdentifiersConverterV2;
import org.orcid.core.contributors.roles.fundings.FundingContributorRoleConverter;
import org.orcid.core.contributors.roles.works.WorkContributorRoleConverter;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.client_v2.Client;
import org.orcid.jaxb.model.client_v2.ClientRedirectUri;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.jaxb.model.common_v2.Day;
import org.orcid.jaxb.model.common_v2.FuzzyDate;
import org.orcid.jaxb.model.common_v2.Month;
import org.orcid.jaxb.model.common_v2.PublicationDate;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.SourceClientId;
import org.orcid.jaxb.model.common_v2.SourceName;
import org.orcid.jaxb.model.common_v2.SourceOrcid;
import org.orcid.jaxb.model.common_v2.Year;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.notification.amended_v2.NotificationAmended;
import org.orcid.jaxb.model.notification.custom_v2.NotificationAdministrative;
import org.orcid.jaxb.model.notification.custom_v2.NotificationCustom;
import org.orcid.jaxb.model.notification.custom_v2.NotificationServiceAnnouncement;
import org.orcid.jaxb.model.notification.custom_v2.NotificationTip;
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
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.SourceAware;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.model.notification.institutional_sign_in_v2.NotificationInstitutionalConnection;
import org.orcid.model.record_correction.RecordCorrection;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.orcid.persistence.jpa.entities.CompletionDateEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAdministrativeEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;
import org.orcid.persistence.jpa.entities.NotificationServiceAnnouncementEntity;
import org.orcid.persistence.jpa.entities.NotificationTipEntity;
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
import org.orcid.pojo.ajaxForm.PojoUtil;
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

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private WorkContributorRoleConverter workContributorsRoleConverter;

    @Resource
    private FundingContributorRoleConverter fundingContributorsRoleConverter;

    public MapperFacadeFactory(WorkContributorRoleConverter workContributorsRoleConverter, FundingContributorRoleConverter fundingContributorsRoleConverter) {
        this.workContributorsRoleConverter = workContributorsRoleConverter;
        this.fundingContributorsRoleConverter = fundingContributorsRoleConverter;
    }

    private MapperFactory getNewMapperFactory() {
        return new DefaultMapperFactory.Builder().build();
    }

    @Override
    public MapperFacade getObject() throws Exception {
        MapperFactory mapperFactory = getNewMapperFactory();

        // Register converters
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("externalIdentifierIdConverter", new ExternalIdentifierTypeConverter());

        // Register factories
        mapperFactory.registerObjectFactory(new WorkEntityFactory(workDao), TypeFactory.<NotificationWorkEntity> valueOf(NotificationWorkEntity.class),
                TypeFactory.<Item> valueOf(Item.class));

        // Custom notification
        ClassMapBuilder<NotificationCustom, NotificationCustomEntity> notificationCustomClassMap = mapperFactory.classMap(NotificationCustom.class,
                NotificationCustomEntity.class);
        registerSourceConverters(mapperFactory, notificationCustomClassMap);
        mapCommonFields(notificationCustomClassMap).register();

        // Service Announcement notification
        ClassMapBuilder<NotificationServiceAnnouncement, NotificationServiceAnnouncementEntity> notificationServiceAnnouncementClassMap = mapperFactory
                .classMap(NotificationServiceAnnouncement.class, NotificationServiceAnnouncementEntity.class);
        registerSourceConverters(mapperFactory, notificationServiceAnnouncementClassMap);
        mapCommonFields(notificationServiceAnnouncementClassMap).register();

        // Tip notification
        ClassMapBuilder<NotificationTip, NotificationTipEntity> notificationTipClassMap = mapperFactory.classMap(NotificationTip.class, NotificationTipEntity.class);
        registerSourceConverters(mapperFactory, notificationTipClassMap);
        mapCommonFields(notificationTipClassMap).register();

        // Administrative notification
        ClassMapBuilder<NotificationAdministrative, NotificationAdministrativeEntity> notificationAdministrativeClassMap = mapperFactory
                .classMap(NotificationAdministrative.class, NotificationAdministrativeEntity.class);
        registerSourceConverters(mapperFactory, notificationAdministrativeClassMap);
        mapCommonFields(notificationAdministrativeClassMap).register();

        // Permission notification
        ClassMapBuilder<NotificationPermission, NotificationAddItemsEntity> notificationPermissionClassMap = mapperFactory.classMap(NotificationPermission.class,
                NotificationAddItemsEntity.class);
        registerSourceConverters(mapperFactory, notificationPermissionClassMap);
        mapCommonFields(notificationPermissionClassMap.field("authorizationUrl.uri", "authorizationUrl").field("items.items", "notificationItems")
                .customize(new CustomMapper<NotificationPermission, NotificationAddItemsEntity>() {
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
        ClassMapBuilder<NotificationInstitutionalConnection, NotificationInstitutionalConnectionEntity> institutionalConnectionNotificationClassMap = mapperFactory
                .classMap(NotificationInstitutionalConnection.class, NotificationInstitutionalConnectionEntity.class);
        registerSourceConverters(mapperFactory, institutionalConnectionNotificationClassMap);
        mapCommonFields(institutionalConnectionNotificationClassMap.field("authorizationUrl.uri", "authorizationUrl")
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
        ClassMapBuilder<NotificationAmended, NotificationAmendedEntity> amendNotificationClassMap = mapperFactory.classMap(NotificationAmended.class,
                NotificationAmendedEntity.class);

        registerSourceConverters(mapperFactory, amendNotificationClassMap);
        amendNotificationClassMap.field("items.items", "notificationItems");

        mapCommonFields(amendNotificationClassMap.exclude("amendedSection").customize(new CustomMapper<NotificationAmended, NotificationAmendedEntity>() {
            @Override
            public void mapAtoB(NotificationAmended a, NotificationAmendedEntity b, MappingContext context) {
                if (a.getAmendedSection() != null) {
                    switch (a.getAmendedSection()) {
                    case AFFILIATION:
                        b.setAmendedSection(AmendedSection.AFFILIATION.name());
                        break;
                    case BIO:
                        b.setAmendedSection(AmendedSection.BIO.name());
                        break;
                    case EDUCATION:
                        b.setAmendedSection(AmendedSection.EDUCATION.name());
                        break;
                    case EMPLOYMENT:
                        b.setAmendedSection(AmendedSection.EMPLOYMENT.name());
                        break;
                    case EXTERNAL_IDENTIFIERS:
                        b.setAmendedSection(AmendedSection.EXTERNAL_IDENTIFIERS.name());
                        break;
                    case FUNDING:
                        b.setAmendedSection(AmendedSection.FUNDING.name());
                        break;
                    case PEER_REVIEW:
                        b.setAmendedSection(AmendedSection.PEER_REVIEW.name());
                        break;
                    case PREFERENCES:
                        b.setAmendedSection(AmendedSection.PREFERENCES.name());
                        break;
                    case UNKNOWN:
                        b.setAmendedSection(AmendedSection.UNKNOWN.name());
                        break;
                    case RESEARCH_RESOURCE:
                        b.setAmendedSection(AmendedSection.RESEARCH_RESOURCE.name());
                        break;
                    case WORK:
                        b.setAmendedSection(AmendedSection.WORK.name());
                        break;
                    default:
                        b.setAmendedSection(AmendedSection.UNKNOWN.name());
                        break;
                    }
                }
            }

            /**
             * From database to model object, map amended sections for new
             * affiliation types as AFFILIATION
             */
            @Override
            public void mapBtoA(NotificationAmendedEntity b, NotificationAmended a, MappingContext context) {
                if (b.getAmendedSection() != null) {
                    if (AmendedSection.AFFILIATION.name().equals(b.getAmendedSection()) || AmendedSection.DISTINCTION.name().equals(b.getAmendedSection())
                            || AmendedSection.INVITED_POSITION.name().equals(b.getAmendedSection()) || AmendedSection.MEMBERSHIP.name().equals(b.getAmendedSection())
                            || AmendedSection.QUALIFICATION.name().equals(b.getAmendedSection()) || AmendedSection.SERVICE.name().equals(b.getAmendedSection())) {
                        a.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.AFFILIATION);
                    } else if (AmendedSection.BIO.name().equals(b.getAmendedSection())) {
                        a.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.BIO);
                    } else if (AmendedSection.EDUCATION.name().equals(b.getAmendedSection())) {
                        a.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.EDUCATION);
                    } else if (AmendedSection.EMPLOYMENT.name().equals(b.getAmendedSection())) {
                        a.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.EMPLOYMENT);
                    } else if (AmendedSection.EXTERNAL_IDENTIFIERS.name().equals(b.getAmendedSection())) {
                        a.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.EXTERNAL_IDENTIFIERS);
                    } else if (AmendedSection.FUNDING.name().equals(b.getAmendedSection())) {
                        a.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.FUNDING);
                    } else if (AmendedSection.PEER_REVIEW.name().equals(b.getAmendedSection())) {
                        a.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.PEER_REVIEW);
                    } else if (AmendedSection.PREFERENCES.name().equals(b.getAmendedSection())) {
                        a.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.PREFERENCES);
                    } else if (AmendedSection.UNKNOWN.name().equals(b.getAmendedSection())) {
                        a.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.UNKNOWN);
                    } else if (AmendedSection.RESEARCH_RESOURCE.name().equals(b.getAmendedSection())) {
                        a.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.RESEARCH_RESOURCE);
                    } else if (AmendedSection.WORK.name().equals(b.getAmendedSection())) {
                        a.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.WORK);
                    } else {
                        a.setAmendedSection(org.orcid.jaxb.model.notification.amended_v2.AmendedSection.UNKNOWN);
                    }
                }
            }
        })).register();

        mapperFactory.classMap(NotificationItemEntity.class, Item.class).fieldMap("externalIdType", "externalIdentifier.type").converter("externalIdentifierIdConverter")
                .add().field("externalIdValue", "externalIdentifier.value").customize(new CustomMapper<NotificationItemEntity, Item>() {
                    @Override
                    public void mapAtoB(NotificationItemEntity entity, Item item, MappingContext context) {
                        if (!PojoUtil.isEmpty(entity.getAdditionalInfo())) {
                            Map map = JsonUtils.readObjectFromJsonString(entity.getAdditionalInfo(), HashMap.class);
                            item.setAdditionalInfo(map);
                        }
                    }

                    @Override
                    public void mapBtoA(Item item, NotificationItemEntity entity, MappingContext context) {
                        if (item.getAdditionalInfo() != null) {
                            entity.setAdditionalInfo(JsonUtils.convertToJsonString(item.getAdditionalInfo()));
                        }
                    }
                }).exclude("additionalInfo").byDefault().register();

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
        MapperFactory mapperFactory = getNewMapperFactory();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());

        ClassMapBuilder<PersonExternalIdentifier, ExternalIdentifierEntity> externalIdentifierClassMap = mapperFactory.classMap(PersonExternalIdentifier.class,
                ExternalIdentifierEntity.class);
        addV2DateFields(externalIdentifierClassMap);
        externalIdentifierClassMap.field("putCode", "id");
        externalIdentifierClassMap.field("type", "externalIdCommonName");
        externalIdentifierClassMap.field("value", "externalIdReference");
        externalIdentifierClassMap.field("url.value", "externalIdUrl");
        externalIdentifierClassMap.fieldBToA("displayIndex", "displayIndex");
        externalIdentifierClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        externalIdentifierClassMap.byDefault();
        registerSourceConverters(mapperFactory, externalIdentifierClassMap);

        // TODO: add relationship to database schema for people.
        externalIdentifierClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getResearcherUrlMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());
        mapperFactory.getConverterFactory().registerConverter("emptyStringToNullConverter", new EmptyStringToNullConverter());

        ClassMapBuilder<ResearcherUrl, ResearcherUrlEntity> researcherUrlClassMap = mapperFactory.classMap(ResearcherUrl.class, ResearcherUrlEntity.class);
        addV2DateFields(researcherUrlClassMap);
        registerSourceConverters(mapperFactory, researcherUrlClassMap);
        researcherUrlClassMap.field("putCode", "id");
        researcherUrlClassMap.field("url.value", "url");
        researcherUrlClassMap.fieldMap("urlName", "urlName").converter("emptyStringToNullConverter").add();
        researcherUrlClassMap.fieldBToA("displayIndex", "displayIndex");
        researcherUrlClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        researcherUrlClassMap.byDefault();
        researcherUrlClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getOtherNameMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());

        ClassMapBuilder<OtherName, OtherNameEntity> otherNameClassMap = mapperFactory.classMap(OtherName.class, OtherNameEntity.class);
        addV2DateFields(otherNameClassMap);
        registerSourceConverters(mapperFactory, otherNameClassMap);
        otherNameClassMap.field("putCode", "id");
        otherNameClassMap.field("content", "displayName");
        otherNameClassMap.fieldBToA("displayIndex", "displayIndex");
        otherNameClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        otherNameClassMap.byDefault();
        otherNameClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getKeywordMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());

        ClassMapBuilder<Keyword, ProfileKeywordEntity> keywordClassMap = mapperFactory.classMap(Keyword.class, ProfileKeywordEntity.class);
        addV2DateFields(keywordClassMap);
        registerSourceConverters(mapperFactory, keywordClassMap);
        keywordClassMap.field("putCode", "id");
        keywordClassMap.field("content", "keywordName");
        keywordClassMap.fieldBToA("displayIndex", "displayIndex");
        keywordClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        keywordClassMap.byDefault();
        keywordClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getAddressMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());

        ClassMapBuilder<Address, AddressEntity> addressClassMap = mapperFactory.classMap(Address.class, AddressEntity.class);
        addV2DateFields(addressClassMap);
        registerSourceConverters(mapperFactory, addressClassMap);
        addressClassMap.field("putCode", "id");
        addressClassMap.field("country.value", "iso2Country");
        addressClassMap.field("visibility", "visibility");
        addressClassMap.fieldBToA("displayIndex", "displayIndex");
        addressClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        addressClassMap.byDefault();
        addressClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getEmailMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());

        ClassMapBuilder<Email, EmailEntity> emailClassMap = mapperFactory.classMap(Email.class, EmailEntity.class);
        emailClassMap.byDefault();
        emailClassMap.field("email", "email");
        emailClassMap.field("primary", "primary");
        emailClassMap.field("verified", "verified");
        emailClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();

        addV2DateFields(emailClassMap);
        registerSourceConverters(mapperFactory, emailClassMap);
        emailClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getWorkMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();

        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("workExternalIdentifiersConverterId", new JSONWorkExternalIdentifiersConverterV2());
        converterFactory.registerConverter("workContributorsConverterId", new WorkContributorsConverter(workContributorsRoleConverter));
        converterFactory.registerConverter("visibilityConverter", new VisibilityConverter());

        ClassMapBuilder<Work, WorkEntity> workClassMap = mapperFactory.classMap(Work.class, WorkEntity.class);
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
        workClassMap.fieldMap("workExternalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        workClassMap.field("url.value", "workUrl");
        workClassMap.fieldMap("workContributors", "contributorsJson").converter("workContributorsConverterId").add();
        workClassMap.field("languageCode", "languageCode");
        workClassMap.field("country.value", "iso2Country");
        workClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        workClassMap.exclude("workType").customize(new CustomMapper<Work, WorkEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(Work a, WorkEntity b, MappingContext context) {
                // Starting with 3.0_rc2 dissertation will be migrated to
                // dissertation-thesis
                if (WorkType.DISSERTATION.equals(a.getWorkType())) {
                    b.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name());
                }
                else {
                    b.setWorkType(a.getWorkType().name());
                }
                b.setWorkUrl(a.getUrl() == null ? null : a.getUrl().getValue());
                b.setIso2Country(a.getCountry() == null ? null : a.getCountry().getValue().toString());
                b.setJournalTitle(a.getJournalTitle() == null ? null : a.getJournalTitle().getContent());
                b.setTranslatedTitle(
                        (a.getWorkTitle() == null || a.getWorkTitle().getTranslatedTitle() == null) ? null : a.getWorkTitle().getTranslatedTitle().getContent());
                b.setTranslatedTitleLanguageCode(
                        (a.getWorkTitle() == null || a.getWorkTitle().getTranslatedTitle() == null) ? null : a.getWorkTitle().getTranslatedTitle().getLanguageCode());
                b.setSubtitle((a.getWorkTitle() == null || a.getWorkTitle().getSubtitle() == null) ? null : a.getWorkTitle().getSubtitle().getContent());
                b.setCitation(a.getWorkCitation() == null ? null : a.getWorkCitation().getCitation());
                b.setCitationType(
                        (a.getWorkCitation() == null || a.getWorkCitation().getWorkCitationType() == null) ? null : a.getWorkCitation().getWorkCitationType().toString());
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(WorkEntity b, Work a, MappingContext context) {
                if(WorkType.REVIEW.equals(getWorkType(b.getWorkType()))){
                    a.setWorkType(WorkType.OTHER);
                }
                else {
                    a.setWorkType(getWorkType(b.getWorkType()));
                }
            }

        });

        workClassMap.byDefault();
        workClassMap.register();

        ClassMapBuilder<WorkSummary, WorkEntity> workSummaryClassMap = mapperFactory.classMap(WorkSummary.class, WorkEntity.class);
        registerSourceConverters(mapperFactory, workSummaryClassMap);
        workSummaryClassMap.field("putCode", "id");
        workSummaryClassMap.field("title.title.content", "title");
        workSummaryClassMap.field("title.translatedTitle.content", "translatedTitle");
        workSummaryClassMap.field("title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        workSummaryClassMap.customize(new CustomMapper<WorkSummary, WorkEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(WorkSummary a, WorkEntity b, MappingContext context) {
                // Starting with 3.0_rc2 dissertation will be migrated to
                // dissertation-thesis
                if (WorkType.DISSERTATION.equals(a.getType())) {
                    b.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name());
                } else {
                    b.setWorkType(a.getType().name());
                }
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(WorkEntity b, WorkSummary a, MappingContext context) {
                a.setType(getWorkType(b.getWorkType()));
            }

        });
        workSummaryClassMap.field("publicationDate", "publicationDate");
        workSummaryClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        workSummaryClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        workSummaryClassMap.byDefault();
        workSummaryClassMap.register();

        ClassMapBuilder<WorkSummary, MinimizedWorkEntity> workSummaryMinimizedClassMap = mapperFactory.classMap(WorkSummary.class, MinimizedWorkEntity.class);
        addV2CommonFields(workSummaryMinimizedClassMap);
        registerSourceConverters(mapperFactory, workSummaryMinimizedClassMap);
        workSummaryMinimizedClassMap.field("title.title.content", "title");
        workSummaryMinimizedClassMap.field("title.translatedTitle.content", "translatedTitle");
        workSummaryMinimizedClassMap.field("title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        workSummaryMinimizedClassMap.field("publicationDate.year.value", "publicationYear");
        workSummaryMinimizedClassMap.field("publicationDate.month.value", "publicationMonth");
        workSummaryMinimizedClassMap.field("publicationDate.day.value", "publicationDay");
        workSummaryMinimizedClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        workSummaryMinimizedClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        workSummaryMinimizedClassMap.customize(new CustomMapper<WorkSummary, MinimizedWorkEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(WorkSummary a, MinimizedWorkEntity b, MappingContext context) {
                // Starting with 3.0_rc2 dissertation will be migrated to
                // dissertation-thesis
                if (WorkType.DISSERTATION.equals(a.getType())) {
                    b.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name());
                } else {
                    b.setWorkType(a.getType().name());
                }
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(MinimizedWorkEntity b, WorkSummary a, MappingContext context) {
                a.setType(getWorkType(b.getWorkType()));
            }

        });
        workSummaryMinimizedClassMap.byDefault();
        workSummaryMinimizedClassMap.register();

        ClassMapBuilder<Work, MinimizedWorkEntity> minimizedWorkClassMap = mapperFactory.classMap(Work.class, MinimizedWorkEntity.class);
        registerSourceConverters(mapperFactory, minimizedWorkClassMap);
        minimizedWorkClassMap.field("putCode", "id");
        minimizedWorkClassMap.field("journalTitle.content", "journalTitle");
        minimizedWorkClassMap.field("workTitle.title.content", "title");
        minimizedWorkClassMap.field("workTitle.translatedTitle.content", "translatedTitle");
        minimizedWorkClassMap.field("workTitle.translatedTitle.languageCode", "translatedTitleLanguageCode");
        minimizedWorkClassMap.field("workTitle.subtitle.content", "subtitle");
        minimizedWorkClassMap.field("shortDescription", "description");
        minimizedWorkClassMap.exclude("workType").customize(new CustomMapper<Work, MinimizedWorkEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(Work a, MinimizedWorkEntity b, MappingContext context) {
                // Starting with 3.0_rc2 dissertation will be migrated to
                // dissertation-thesis
                if (WorkType.DISSERTATION.equals(a.getWorkType())) {
                    b.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name());
                } else {
                    b.setWorkType(a.getWorkType().name());
                }
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(MinimizedWorkEntity b, Work a, MappingContext context) {
                a.setWorkType(getWorkType(b.getWorkType()));
            }

        });
        minimizedWorkClassMap.field("publicationDate.year.value", "publicationYear");
        minimizedWorkClassMap.field("publicationDate.month.value", "publicationMonth");
        minimizedWorkClassMap.field("publicationDate.day.value", "publicationDay");
        minimizedWorkClassMap.fieldMap("workExternalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        minimizedWorkClassMap.field("url.value", "workUrl");
        minimizedWorkClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        minimizedWorkClassMap.byDefault();
        minimizedWorkClassMap.register();

        mapperFactory.classMap(PublicationDate.class, PublicationDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day")
                .register();
        
        mapFuzzyDateToPublicationDateEntity(mapperFactory);
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getFundingMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("fundingExternalIdentifiersConverterId", new JSONFundingExternalIdentifiersConverterV2());
        converterFactory.registerConverter("fundingContributorsConverterId", new FundingContributorsConverter(fundingContributorsRoleConverter));
        converterFactory.registerConverter("visibilityConverter", new VisibilityConverter());

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
        fundingClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        fundingClassMap.customize(new CustomMapper<Funding, ProfileFundingEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(Funding a, ProfileFundingEntity b, MappingContext context) {
                b.setOrganizationDefinedType(a.getOrganizationDefinedType() == null ? null : a.getOrganizationDefinedType().getContent());
                b.setUrl(a.getUrl() == null ? null : a.getUrl().getValue());
                b.setTranslatedTitle((a.getTitle() == null || a.getTitle().getTranslatedTitle() == null) ? null : a.getTitle().getTranslatedTitle().getContent());
                b.setTranslatedTitleLanguageCode(
                        (a.getTitle() == null || a.getTitle().getTranslatedTitle() == null) ? null : a.getTitle().getTranslatedTitle().getLanguageCode());
                b.setNumericAmount((a.getAmount() == null || a.getAmount().getContent() == null) ? null : BigDecimal.valueOf(Double.valueOf(a.getAmount().getContent())));
                b.setCurrencyCode((a.getAmount() == null || a.getAmount().getContent() == null) ? null : a.getAmount().getCurrencyCode());
            }
        });
        fundingClassMap.byDefault();
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
        fundingSummaryClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        fundingSummaryClassMap.byDefault();
        fundingSummaryClassMap.register();

        mapFuzzyDateToStartDateEntityAndEndDateEntity(mapperFactory);
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getEducationMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());
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
        educationClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        educationClassMap.byDefault();
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
        educationSummaryClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        educationSummaryClassMap.byDefault();
        educationSummaryClassMap.register();

        mapFuzzyDateToStartDateEntityAndEndDateEntity(mapperFactory);
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getEmploymentMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());
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
        classMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        classMap.byDefault();
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
        employmentSummaryClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        employmentSummaryClassMap.byDefault();
        employmentSummaryClassMap.register();

        mapFuzzyDateToStartDateEntityAndEndDateEntity(mapperFactory);
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getPeerReviewMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();

        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("workExternalIdentifiersConverterId", new JSONWorkExternalIdentifiersConverterV2());
        converterFactory.registerConverter("workExternalIdentifierConverterId", new JSONPeerReviewWorkExternalIdentifierConverterV2());
        converterFactory.registerConverter("visibilityConverter", new VisibilityConverter());
        converterFactory.registerConverter("peerReviewSubjectTypeConverter", new PeerReviewSubjectTypeConverter());

        ClassMapBuilder<PeerReview, PeerReviewEntity> classMap = mapperFactory.classMap(PeerReview.class, PeerReviewEntity.class);
        classMap.fieldMap("subjectType", "subjectType").converter("peerReviewSubjectTypeConverter").add();
        classMap.field("url.value", "url");
        classMap.field("organization.name", "org.name");
        classMap.field("organization.address.city", "org.city");
        classMap.field("organization.address.region", "org.region");
        classMap.field("organization.address.country", "org.country");
        classMap.field("organization.disambiguatedOrganization.disambiguatedOrganizationIdentifier", "org.orgDisambiguated.sourceId");
        classMap.field("organization.disambiguatedOrganization.disambiguationSource", "org.orgDisambiguated.sourceType");
        classMap.field("groupId", "groupId");
        classMap.field("subjectUrl.value", "subjectUrl");
        classMap.field("subjectName.title.content", "subjectName");
        classMap.field("subjectName.translatedTitle.content", "subjectTranslatedName");
        classMap.field("subjectName.translatedTitle.languageCode", "subjectTranslatedNameLanguageCode");
        classMap.field("subjectContainerName.content", "subjectContainerName");
        classMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        classMap.fieldMap("subjectExternalIdentifier", "subjectExternalIdentifiersJson").converter("workExternalIdentifierConverterId").add();
        classMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        classMap.customize(new CustomMapper<PeerReview, PeerReviewEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(PeerReview a, PeerReviewEntity b, MappingContext context) {
                b.setUrl(a.getUrl() == null ? null : a.getUrl().getValue());
                b.setSubjectUrl(a.getSubjectUrl() == null ? null : a.getSubjectUrl().getValue());
                b.setSubjectName(
                        (a.getSubjectName() == null || a.getSubjectName().getTitle() == null) ? null : a.getSubjectName().getTitle().getContent());
                b.setSubjectTranslatedName(
                        (a.getSubjectName() == null || a.getSubjectName().getTranslatedTitle() == null) ? null : a.getSubjectName().getTranslatedTitle().getContent());
                b.setSubjectTranslatedNameLanguageCode((a.getSubjectName() == null || a.getSubjectName().getTranslatedTitle() == null) ? null
                        : a.getSubjectName().getTranslatedTitle().getLanguageCode());
                b.setSubjectContainerName(a.getSubjectContainerName() == null ? null : a.getSubjectContainerName().getContent());
                if(WorkType.DISSERTATION.equals(a.getSubjectType())) {
                    b.setSubjectType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name());
                } else {
                    if(a.getSubjectType() != null) {
                        b.setSubjectType(a.getSubjectType().name());
                    }
                }
            }
        });

        registerSourceConverters(mapperFactory, classMap);
        addV2CommonFields(classMap);
        classMap.byDefault();
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
        peerReviewSummaryClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        peerReviewSummaryClassMap.byDefault();
        peerReviewSummaryClassMap.register();

        mapperFactory.classMap(FuzzyDate.class, CompletionDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day")
                .register();

        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getGroupIdRecordMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();

        ClassMapBuilder<GroupIdRecord, GroupIdRecordEntity> classMap = mapperFactory.classMap(GroupIdRecord.class, GroupIdRecordEntity.class);
        addV2CommonFields(classMap);
        registerSourceConverters(mapperFactory, classMap);
        classMap.field("name", "groupName");
        classMap.field("groupId", "groupId");
        classMap.field("description", "groupDescription");
        classMap.field("type", "groupType");
        classMap.byDefault();
        classMap.register();

        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getClientMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        ClassMapBuilder<ClientSummary, ClientDetailsEntity> clientSummaryClassMap = mapperFactory.classMap(ClientSummary.class, ClientDetailsEntity.class);
        clientSummaryClassMap.field("name", "clientName");
        clientSummaryClassMap.field("description", "clientDescription");
        clientSummaryClassMap.byDefault();
        clientSummaryClassMap.register();

        ClassMapBuilder<Client, ClientDetailsEntity> clientClassMap = mapperFactory.classMap(Client.class, ClientDetailsEntity.class);
        clientClassMap.field("name", "clientName");
        clientClassMap.field("description", "clientDescription");
        clientClassMap.field("website", "clientWebsite");
        clientClassMap.field("allowAutoDeprecate", "allowAutoDeprecate");

        clientClassMap.fieldBToA("clientId", "id");
        clientClassMap.fieldBToA("clientType", "clientType");
        clientClassMap.fieldBToA("groupProfileId", "groupProfileId");
        clientClassMap.fieldBToA("authenticationProviderId", "authenticationProviderId");
        clientClassMap.fieldBToA("persistentTokensEnabled", "persistentTokensEnabled");

        clientClassMap.customize(new CustomMapper<Client, ClientDetailsEntity>() {
            /**
             * On the way in, from Client to ClientDetailsEntity, we need to
             * care about mapping the redirect uri's, since all config features
             * will not change from UI requests
             */
            @Override
            public void mapAtoB(Client a, ClientDetailsEntity b, MappingContext context) {
                Map<String, ClientRedirectUriEntity> existingRedirectUriEntitiesMap = new HashMap<String, ClientRedirectUriEntity>();
                if (b.getClientRegisteredRedirectUris() != null && !b.getClientRegisteredRedirectUris().isEmpty()) {
                    existingRedirectUriEntitiesMap = ClientRedirectUriEntity.mapByUriAndType(b.getClientRegisteredRedirectUris());
                }
                if (b.getClientRegisteredRedirectUris() != null) {
                    b.getClientRegisteredRedirectUris().clear();
                } else {
                    b.setClientRegisteredRedirectUris(new TreeSet<ClientRedirectUriEntity>());
                }

                if (a.getClientRedirectUris() != null) {
                    for (ClientRedirectUri cru : a.getClientRedirectUris()) {
                        String rUriKey = ClientRedirectUriEntity.getUriAndTypeKey(cru.getRedirectUri(), cru.getRedirectUriType());
                        if (existingRedirectUriEntitiesMap.containsKey(rUriKey)) {
                            ClientRedirectUriEntity existingEntity = existingRedirectUriEntitiesMap.get(rUriKey);
                            existingEntity.setPredefinedClientScope(ScopePathType.getScopesAsSingleString(cru.getPredefinedClientScopes()));
                            existingEntity.setUriActType(cru.getUriActType());
                            existingEntity.setUriGeoArea(cru.getUriGeoArea());
                            b.getClientRegisteredRedirectUris().add(existingEntity);
                        } else {
                            ClientRedirectUriEntity newEntity = new ClientRedirectUriEntity();
                            newEntity.setClientDetailsEntity(b);
                            newEntity.setPredefinedClientScope(ScopePathType.getScopesAsSingleString(cru.getPredefinedClientScopes()));
                            newEntity.setRedirectUri(cru.getRedirectUri());
                            newEntity.setRedirectUriType(cru.getRedirectUriType());
                            newEntity.setUriActType(cru.getUriActType());
                            newEntity.setUriGeoArea(cru.getUriGeoArea());
                            b.getClientRegisteredRedirectUris().add(newEntity);
                        }
                    }
                }
            }

            /**
             * On the way out, from ClientDetailsEntity to Client, we just need
             * to care about mapping the redirect uri's and the primary client
             * secret since all config features will not be visible on the UI
             */
            @Override
            public void mapBtoA(ClientDetailsEntity b, Client a, MappingContext context) {
                if (b.getClientSecrets() != null) {
                    for (ClientSecretEntity entity : b.getClientSecrets()) {
                        if (entity.isPrimary()) {
                            a.setDecryptedSecret(encryptionManager.decryptForInternalUse(entity.getClientSecret()));
                        }
                    }
                }
                if (b.getRegisteredRedirectUri() != null) {
                    a.setClientRedirectUris(new HashSet<ClientRedirectUri>());
                    for (ClientRedirectUriEntity entity : b.getClientRegisteredRedirectUris()) {
                        ClientRedirectUri element = new ClientRedirectUri();
                        element.setRedirectUri(entity.getRedirectUri());
                        element.setRedirectUriType(entity.getRedirectUriType());
                        element.setUriActType(entity.getUriActType());
                        element.setUriGeoArea(entity.getUriGeoArea());
                        element.setPredefinedClientScopes(ScopePathType.getScopesFromSpaceSeparatedString(entity.getPredefinedClientScope()));
                        a.getClientRedirectUris().add(element);
                    }
                }
            }
        });
        clientClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getNameMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());
        mapperFactory.getConverterFactory().registerConverter("emptyStringToNullConverter", new EmptyStringToNullConverter());
        mapperFactory.getConverterFactory().registerConverter("familyNameConverter", new FamilyNameConverter());
        mapperFactory.getConverterFactory().registerConverter("givenNamesConverter", new GivenNamesConverter());
        mapperFactory.getConverterFactory().registerConverter("creditNameConverter", new CreditNameConverter());

        ClassMapBuilder<Name, RecordNameEntity> nameClassMap = mapperFactory.classMap(Name.class, RecordNameEntity.class);
        addV2DateFields(nameClassMap);
        nameClassMap.fieldMap("creditName", "creditName").converter("creditNameConverter").add();
        nameClassMap.fieldMap("givenNames", "givenNames").converter("givenNamesConverter").add();
        nameClassMap.fieldMap("familyName", "familyName").converter("familyNameConverter").add();
        nameClassMap.field("path", "orcid");
        nameClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        nameClassMap.byDefault();
        nameClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getInvalidRecordDataChangeMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
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
        classMap.field("putCode", "id");
        addV2DateFields(classMap);
    }

    private void addV2DateFields(ClassMapBuilder<?, ?> classMap) {
        classMap.fieldBToA("dateCreated", "createdDate.value");
        classMap.fieldBToA("lastModified", "lastModifiedDate.value");
    }

    private void mapFuzzyDateToPublicationDateEntity(MapperFactory mapperFactory) {
        mapperFactory.classMap(FuzzyDate.class, PublicationDateEntity.class).customize(new CustomMapper<FuzzyDate, PublicationDateEntity>() {
            @Override
            public void mapAtoB(FuzzyDate fuzzyDate, PublicationDateEntity entity, MappingContext context) {
                if (fuzzyDate.getYear() != null) {
                    entity.setYear(Integer.valueOf(fuzzyDate.getYear().getValue()));
                } else {
                    entity.setYear(null);
                }

                if (fuzzyDate.getMonth() != null) {
                    entity.setMonth(Integer.valueOf(fuzzyDate.getMonth().getValue()));
                } else {
                    entity.setMonth(null);
                }

                if (fuzzyDate.getDay() != null) {
                    entity.setDay(Integer.valueOf(fuzzyDate.getDay().getValue()));
                } else {
                    entity.setDay(null);
                }
            }

            @Override
            public void mapBtoA(PublicationDateEntity entity, FuzzyDate fuzzyDate, MappingContext context) {
                if (entity.getYear() != null) {
                    fuzzyDate.setYear(new Year(entity.getYear()));
                } else {
                    fuzzyDate.setYear(null);
                }

                if (entity.getMonth() != null) {
                    fuzzyDate.setMonth(new Month(entity.getMonth()));
                } else {
                    fuzzyDate.setMonth(null);
                }

                if (entity.getDay() != null) {
                    fuzzyDate.setDay(new Day(entity.getDay()));
                } else {
                    fuzzyDate.setDay(null);
                }
            }
        }).register();
        
        
    }
    
    private void mapFuzzyDateToStartDateEntityAndEndDateEntity(MapperFactory mapperFactory) {
        mapperFactory.classMap(FuzzyDate.class, StartDateEntity.class).customize(new CustomMapper<FuzzyDate, StartDateEntity>() {
            @Override
            public void mapAtoB(FuzzyDate fuzzyDate, StartDateEntity entity, MappingContext context) {
                if (fuzzyDate.getYear() != null) {
                    entity.setYear(Integer.valueOf(fuzzyDate.getYear().getValue()));
                } else {
                    entity.setYear(null);
                }

                if (fuzzyDate.getMonth() != null) {
                    entity.setMonth(Integer.valueOf(fuzzyDate.getMonth().getValue()));
                } else {
                    entity.setMonth(null);
                }

                if (fuzzyDate.getDay() != null) {
                    entity.setDay(Integer.valueOf(fuzzyDate.getDay().getValue()));
                } else {
                    entity.setDay(null);
                }
            }

            @Override
            public void mapBtoA(StartDateEntity entity, FuzzyDate fuzzyDate, MappingContext context) {
                if (entity.getYear() != null) {
                    fuzzyDate.setYear(new Year(entity.getYear()));
                } else {
                    fuzzyDate.setYear(null);
                }

                if (entity.getMonth() != null) {
                    fuzzyDate.setMonth(new Month(entity.getMonth()));
                } else {
                    fuzzyDate.setMonth(null);
                }

                if (entity.getDay() != null) {
                    fuzzyDate.setDay(new Day(entity.getDay()));
                } else {
                    fuzzyDate.setDay(null);
                }
            }
        }).register();

        mapperFactory.classMap(FuzzyDate.class, EndDateEntity.class).customize(new CustomMapper<FuzzyDate, EndDateEntity>() {
            @Override
            public void mapAtoB(FuzzyDate fuzzyDate, EndDateEntity entity, MappingContext context) {
                if (fuzzyDate.getYear() != null) {
                    entity.setYear(Integer.valueOf(fuzzyDate.getYear().getValue()));
                } else {
                    entity.setYear(null);
                }

                if (fuzzyDate.getMonth() != null) {
                    entity.setMonth(Integer.valueOf(fuzzyDate.getMonth().getValue()));
                } else {
                    entity.setMonth(null);
                }

                if (fuzzyDate.getDay() != null) {
                    entity.setDay(Integer.valueOf(fuzzyDate.getDay().getValue()));
                } else {
                    entity.setDay(null);
                }
            }

            @Override
            public void mapBtoA(EndDateEntity entity, FuzzyDate fuzzyDate, MappingContext context) {
                if (entity.getYear() != null) {
                    fuzzyDate.setYear(new Year(entity.getYear()));
                } else {
                    fuzzyDate.setYear(null);
                }

                if (entity.getMonth() != null) {
                    fuzzyDate.setMonth(new Month(entity.getMonth()));
                } else {
                    fuzzyDate.setMonth(null);
                }

                if (entity.getDay() != null) {
                    fuzzyDate.setDay(new Day(entity.getDay()));
                } else {
                    fuzzyDate.setDay(null);
                }
            }
        }).register();
    }

    @Override
    public Class<?> getObjectType() {
        return MapperFacade.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private WorkType getWorkType(String name) {
        if (org.orcid.jaxb.model.common.WorkType.SOFTWARE.name().equals(name) || org.orcid.jaxb.model.common.WorkType.PREPRINT.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.PHYSICAL_OBJECT.name().equals(name) || org.orcid.jaxb.model.common.WorkType.ANNOTATION.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.DATA_MANAGEMENT_PLAN.name().equals(name) || org.orcid.jaxb.model.common.WorkType.CONFERENCE_OUTPUT.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.CONFERENCE_PRESENTATION.name().equals(name) || org.orcid.jaxb.model.common.WorkType.CONFERENCE_PROCEEDINGS.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.TRANSCRIPTION.name().equals(name) || org.orcid.jaxb.model.common.WorkType.BLOG_POST.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.DESIGN.name().equals(name) || org.orcid.jaxb.model.common.WorkType.IMAGE.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.MOVING_IMAGE.name().equals(name) || org.orcid.jaxb.model.common.WorkType.MUSICAL_COMPOSITION.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.SOUND.name().equals(name) || org.orcid.jaxb.model.common.WorkType.CARTOGRAPHIC_MATERIAL.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.CLINICAL_STUDY.name().equals(name) || org.orcid.jaxb.model.common.WorkType.LEARNING_OBJECT.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.PUBLIC_SPEECH.name().equals(name)) {
            return WorkType.OTHER;
        }

        // dissertation-thesis is a new work type supported from 3.0_rc2, for
        // previous versions, it should be downgraded to dissertation
        if (org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name().equals(name)) {
            return WorkType.DISSERTATION;
        }

        return WorkType.valueOf(name);
    }

}
