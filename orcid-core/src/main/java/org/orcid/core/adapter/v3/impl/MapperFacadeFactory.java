package org.orcid.core.adapter.v3.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.adapter.mapstruct.AdditionalInfoJsonMapper;
import org.orcid.core.adapter.mapstruct.ClientMapperV3;
import org.orcid.core.adapter.mapstruct.ExternalIdentifierTypeMapper;
import org.orcid.core.adapter.mapstruct.FundingContributorsMapperV3;
import org.orcid.core.adapter.mapstruct.FundingMapperV3;
import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV3;
import org.orcid.core.adapter.mapstruct.JSONExternalIdentifiersMapperV3;
import org.orcid.core.adapter.mapstruct.JSONFundingExternalIdentifiersMapperV3;
import org.orcid.core.adapter.mapstruct.JSONPeerReviewWorkExternalIdentifierMapperV3;
import org.orcid.core.adapter.mapstruct.JSONWorkExternalIdentifiersMapperV3;
import org.orcid.core.adapter.mapstruct.MiscMapperV3;
import org.orcid.core.adapter.mapstruct.NotificationMapperV3;
import org.orcid.core.adapter.mapstruct.OrgMapperV3;
import org.orcid.core.adapter.mapstruct.PeerReviewMapperV3;
import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.mapstruct.WorkMapperV3;
import org.orcid.core.adapter.v3.converter.ContributorsRolesAndSequencesConverter;
import org.orcid.core.adapter.v3.converter.WorkContributorsConverter;
import org.orcid.core.contributors.roles.fundings.FundingContributorRoleConverter;
import org.orcid.core.contributors.roles.works.WorkContributorRoleConverter;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverService;
import org.orcid.jaxb.model.v3.release.client.Client;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.PublicationDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationAdministrative;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationCustom;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationServiceAnnouncement;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationTip;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Affiliation;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.ResearchResourceItem;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.SourceAware;
import org.orcid.jaxb.model.v3.release.record.Spam;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.model.record_correction.RecordCorrection;
import org.orcid.model.v3.release.notification.institutional_sign_in.NotificationInstitutionalConnection;
import org.orcid.model.v3.release.notification.internal.NotificationFindMyStuff;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.CompletionDateEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;
import org.orcid.persistence.jpa.entities.MinimizedExtendedWorkEntity;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAdministrativeEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.orcid.persistence.jpa.entities.NotificationFindMyStuffEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;
import org.orcid.persistence.jpa.entities.NotificationItemEntity;
import org.orcid.persistence.jpa.entities.NotificationServiceAnnouncementEntity;
import org.orcid.persistence.jpa.entities.NotificationTipEntity;
import org.orcid.persistence.jpa.entities.NotificationWorkEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceItemEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;
import org.orcid.persistence.jpa.entities.SpamEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.WorkExtended;
import org.orcid.pojo.WorkSummaryExtended;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Lazy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.ObjectFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.TypeFactory;

/**
 * 
 * @author Will Simpson
 * 
 */
public class MapperFacadeFactory implements FactoryBean<MapperFacade> {

    private static final String LAST_RESORT_IDENTITY_PROVIDER_NAME = "identity provider";
    private static final AdditionalInfoJsonMapper ADDITIONAL_INFO_JSON_MAPPER = AdditionalInfoJsonMapper.INSTANCE;
    private static final ClientMapperV3 CLIENT_MAPPER_V3 = ClientMapperV3.INSTANCE;
    private static final FuzzyDateMapperV3 FUZZY_DATE_MAPPER_V3 = FuzzyDateMapperV3.INSTANCE;
    private static final FundingMapperV3 FUNDING_MAPPER_V3 = FundingMapperV3.INSTANCE;
    private static final MiscMapperV3 MISC_MAPPER_V3 = MiscMapperV3.INSTANCE;
    private static final NotificationMapperV3 NOTIFICATION_MAPPER_V3 = NotificationMapperV3.INSTANCE;
    private static final PeerReviewMapperV3 PEER_REVIEW_MAPPER_V3 = PeerReviewMapperV3.INSTANCE;
    private static final SourceMapperV3 SOURCE_MAPPER_V3 = SourceMapperV3.INSTANCE;
    private static final WorkMapperV3 WORK_MAPPER_V3 = WorkMapperV3.INSTANCE;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private WorkDao workDao;

    @Resource
    private IdentityProviderManager identityProviderManager;

    @Resource(name = "encryptionManager")
    private EncryptionManager encryptionManager;

    @Resource(name = "PIDNormalizationService")
    private PIDNormalizationService norm;

    @Resource(name = "PIDResolverService")
    private PIDResolverService resolverService;
    
    @Resource
    private LocaleManager localeManager;
    
    @Resource
    private WorkContributorRoleConverter workContributorsRoleConverter;
    
    @Resource
    private FundingContributorRoleConverter fundingContributorsRoleConverter;

    @Resource
    @Lazy
    private SourceEntityUtils sourceEntityUtils;

    @Resource
    private ContributorsRolesAndSequencesConverter contributorsRolesAndSequencesConverter;

    @Resource
    private JSONFundingExternalIdentifiersMapperV3 jsonFundingExternalIdentifiersMapperV3;

    @Resource
    private JSONExternalIdentifiersMapperV3 jsonExternalIdentifiersMapperV3;

    @Resource
    private JSONPeerReviewWorkExternalIdentifierMapperV3 jsonPeerReviewWorkExternalIdentifierMapperV3;

    @Resource
    private JSONWorkExternalIdentifiersMapperV3 jsonWorkExternalIdentifiersMapperV3;

    @Resource
    private org.orcid.core.adapter.v3.converter.WorkContributorsConverter workContributorsConverter;

    private MapperFactory getNewMapperFactory() {
        // Keep a fresh MapperFactory per facade build to avoid shared mutable Orika registrations.
        return new DefaultMapperFactory.Builder().compilerStrategy(new EclipseJdtCompilerStrategy())
                .dumpStateOnException(false)
                .build();
    }

    @Override
    public MapperFacade getObject() throws Exception {
        MapperFactory mapperFactory = getNewMapperFactory();

        // Register converters
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("externalIdentifierIdConverter", externalIdentifierIdConverter());

        // Register factories
        mapperFactory.registerObjectFactory(new ObjectFactory<NotificationWorkEntity>() {
            @Override
            public NotificationWorkEntity create(Object source, MappingContext mappingContext) {
                NotificationWorkEntity nwe = new NotificationWorkEntity();
                String putCode = ((Item) source).getPutCode();
                if (putCode != null) {
                    WorkEntity work = workDao.find(Long.valueOf(putCode));
                    nwe.setWork(work);
                }
                return nwe;
            }
        }, TypeFactory.<NotificationWorkEntity> valueOf(NotificationWorkEntity.class), TypeFactory.<Item> valueOf(Item.class));

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
                            String authUrl = NOTIFICATION_MAPPER_V3.buildAuthorizationUrlIfBlank(entity.getAuthorizationUrl(),
                                    notification.getAuthorizationUrl().getPath(), orcidUrlManager.getBaseUrl());
                            // validate
                            validateAndConvertToURI(authUrl);
                            entity.setAuthorizationUrl(authUrl);
                        }
                    }

                    @Override
                    public void mapBtoA(NotificationAddItemsEntity entity, NotificationPermission notification, MappingContext context) {
                        NOTIFICATION_MAPPER_V3.mapPermissionBtoA(entity, notification, extractFullPath(notification.getAuthorizationUrl().getUri()),
                                orcidUrlManager.getBaseHost());
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
                            String authUrl = NOTIFICATION_MAPPER_V3.buildAuthorizationUrlIfBlank(entity.getAuthorizationUrl(),
                                    notification.getAuthorizationUrl().getPath(), orcidUrlManager.getBaseUrl());
                            // validate
                            validateAndConvertToURI(authUrl);
                            entity.setAuthorizationUrl(authUrl);
                        }
                    }

                    @Override
                    public void mapBtoA(NotificationInstitutionalConnectionEntity entity, NotificationInstitutionalConnection notification, MappingContext context) {
                        NOTIFICATION_MAPPER_V3.mapInstitutionalBtoA(entity, notification, extractFullPath(notification.getAuthorizationUrl().getUri()),
                                orcidUrlManager.getBaseHost(), identityProviderManager, LAST_RESORT_IDENTITY_PROVIDER_NAME);
                    }
                })).register();

        // Find my stuff notification
        ClassMapBuilder<NotificationFindMyStuff, NotificationFindMyStuffEntity> findMyStuffNotificationClassMap = mapperFactory.classMap(NotificationFindMyStuff.class,
                NotificationFindMyStuffEntity.class);
        registerSourceConverters(mapperFactory, institutionalConnectionNotificationClassMap);
        mapCommonFields(findMyStuffNotificationClassMap.field("authorizationUrl.uri", "authorizationUrl")
                .customize(new CustomMapper<NotificationFindMyStuff, NotificationFindMyStuffEntity>() {
                    @Override
                    public void mapAtoB(NotificationFindMyStuff notification, NotificationFindMyStuffEntity entity, MappingContext context) {
                        if (StringUtils.isBlank(entity.getAuthorizationUrl())) {
                            String authUrl = orcidUrlManager.getBaseUrl() + notification.getAuthorizationUrl().getPath();
                            validateAndConvertToURI(authUrl);
                            NOTIFICATION_MAPPER_V3.mapFindMyStuffAtoB(notification, entity, entity.getAuthorizationUrl(), authUrl);
                        }
                    }

                    @Override
                    public void mapBtoA(NotificationFindMyStuffEntity entity, NotificationFindMyStuff notification, MappingContext context) {
                        NOTIFICATION_MAPPER_V3.mapFindMyStuffBtoA(entity, notification, extractFullPath(notification.getAuthorizationUrl().getUri()),
                                orcidUrlManager.getBaseHost());
                    }
                })).register();

        // Amend notification
        ClassMapBuilder<NotificationAmended, NotificationAmendedEntity> amendNotificationClassMap = mapperFactory.classMap(NotificationAmended.class,
                NotificationAmendedEntity.class);
        registerSourceConverters(mapperFactory, amendNotificationClassMap);
        amendNotificationClassMap.field("items.items", "notificationItems");
        mapCommonFields(amendNotificationClassMap).register();

        ClassMapBuilder<NotificationItemEntity, Item> itemClassMap = mapperFactory.classMap(NotificationItemEntity.class, Item.class);
        itemClassMap.fieldMap("externalIdType", "externalIdentifier.type").converter("externalIdentifierIdConverter").add();
        itemClassMap.field("externalIdValue", "externalIdentifier.value");
        itemClassMap.field("externalIdUrl", "externalIdentifier.url.value");
        itemClassMap.field("externalIdRelationship", "externalIdentifier.relationship");

        itemClassMap.customize(new CustomMapper<NotificationItemEntity, Item>() {
                    @Override
                    public void mapAtoB(NotificationItemEntity entity, Item item, MappingContext context) {
                        NOTIFICATION_MAPPER_V3.mapItemAtoB(entity, item, ADDITIONAL_INFO_JSON_MAPPER);
                    }

                    @Override
                    public void mapBtoA(Item item, NotificationItemEntity entity, MappingContext context) {
                        NOTIFICATION_MAPPER_V3.mapItemBtoA(item, entity, ADDITIONAL_INFO_JSON_MAPPER);
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
        @SuppressWarnings("unchecked")
        @Override
        public void mapBtoA(SourceAwareEntity<?> b, SourceAware a, MappingContext context) {
            if (b == null || a == null) {
                return;
            }

            Source source = null;
            if (context != null && context.getProperty(SourceEntityUtils.SOURCE_MAP) != null) {
                // The source map is set in the context, so we can use it to set the source.
                source = SOURCE_MAPPER_V3.toSource(b, (Map<String, Source>) context.getProperty(SourceEntityUtils.SOURCE_MAP), sourceEntityUtils);
            } else {
                source = SOURCE_MAPPER_V3.toSource(b, null, sourceEntityUtils);
            }

            a.setSource(source);
        }
    }

    public MapperFacade getExternalIdentifierMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", visibilityConverter());

        ClassMapBuilder<PersonExternalIdentifier, ExternalIdentifierEntity> externalIdentifierClassMap = mapperFactory.classMap(PersonExternalIdentifier.class,
                ExternalIdentifierEntity.class);
        addV3DateFields(externalIdentifierClassMap);
        externalIdentifierClassMap.field("putCode", "id");
        externalIdentifierClassMap.field("type", "externalIdCommonName");
        externalIdentifierClassMap.field("value", "externalIdReference");
        externalIdentifierClassMap.field("url.value", "externalIdUrl");
        externalIdentifierClassMap.fieldBToA("displayIndex", "displayIndex");
        externalIdentifierClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        externalIdentifierClassMap.customize(new CustomMapper<PersonExternalIdentifier, ExternalIdentifierEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(PersonExternalIdentifier a, ExternalIdentifierEntity b, MappingContext context) {
                MISC_MAPPER_V3.mapExternalIdentifierAtoB(a, b);
            }
        });
        externalIdentifierClassMap.byDefault();
        registerSourceConverters(mapperFactory, externalIdentifierClassMap);

        // TODO: add relationship to database schema for people.
        externalIdentifierClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getResearcherUrlMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", visibilityConverter());

        ClassMapBuilder<ResearcherUrl, ResearcherUrlEntity> researcherUrlClassMap = mapperFactory.classMap(ResearcherUrl.class, ResearcherUrlEntity.class);
        addV3DateFields(researcherUrlClassMap);
        registerSourceConverters(mapperFactory, researcherUrlClassMap);
        researcherUrlClassMap.field("putCode", "id");
        researcherUrlClassMap.field("url.value", "url");
        researcherUrlClassMap.field("urlName", "urlName");
        researcherUrlClassMap.fieldBToA("displayIndex", "displayIndex");
        researcherUrlClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        researcherUrlClassMap.customize(new CustomMapper<ResearcherUrl, ResearcherUrlEntity>() {
            @Override
            public void mapAtoB(ResearcherUrl a, ResearcherUrlEntity b, MappingContext context) {
                b.setUrlName(normalizeBlank(a.getUrlName()));
            }

            @Override
            public void mapBtoA(ResearcherUrlEntity b, ResearcherUrl a, MappingContext context) {
                a.setUrlName(normalizeBlank(b.getUrlName()));
            }
        });
        researcherUrlClassMap.byDefault();
        researcherUrlClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getOtherNameMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", visibilityConverter());

        ClassMapBuilder<OtherName, OtherNameEntity> otherNameClassMap = mapperFactory.classMap(OtherName.class, OtherNameEntity.class);
        addV3DateFields(otherNameClassMap);
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
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", visibilityConverter());

        ClassMapBuilder<Keyword, ProfileKeywordEntity> keywordClassMap = mapperFactory.classMap(Keyword.class, ProfileKeywordEntity.class);
        addV3DateFields(keywordClassMap);
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
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", visibilityConverter());

        ClassMapBuilder<Address, AddressEntity> addressClassMap = mapperFactory.classMap(Address.class, AddressEntity.class);
        addV3DateFields(addressClassMap);
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
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", visibilityConverter());
        ClassMapBuilder<Email, EmailEntity> emailClassMap = mapperFactory.classMap(Email.class, EmailEntity.class);
        emailClassMap.byDefault();
        emailClassMap.field("email", "email");
        emailClassMap.field("primary", "primary");
        emailClassMap.field("verified", "verified");
        emailClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        emailClassMap.field("verificationDate.value", "dateVerified");
        addV3DateFields(emailClassMap);
        registerSourceConverters(mapperFactory, emailClassMap);
        emailClassMap.register();
        return mapperFactory.getMapperFacade();
    }
    
    private void registerOrgClassMappings(MapperFactory mapperFactory) {
        ClassMapBuilder<Organization, OrgEntity> orgClassMap = mapperFactory.classMap(Organization.class, OrgEntity.class);
        orgClassMap.fieldBToA("name", "name");
        orgClassMap.fieldBToA("city", "address.city");
        orgClassMap.fieldBToA("country", "address.country");
        orgClassMap.fieldBToA("region", "address.region").customize(new CustomMapper<Organization, OrgEntity>() {
            
            @Override
            public void mapBtoA(OrgEntity b, Organization a, MappingContext context) {
                MISC_MAPPER_V3.mapOrgBtoA(b, a);
            }
            
        });
        
        orgClassMap.fieldBToA("orgDisambiguated.sourceId", "disambiguatedOrganization.disambiguatedOrganizationIdentifier");
        orgClassMap.fieldBToA("orgDisambiguated.sourceType", "disambiguatedOrganization.disambiguationSource");
        orgClassMap.fieldBToA("orgDisambiguated.id", "disambiguatedOrganization.id");
        orgClassMap.register();
    }
    
    public MapperFacade getWorkMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();

        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("workExternalIdentifiersConverterId", workExternalIdentifiersV3Converter());
        converterFactory.registerConverter("workContributorsConverterId", workContributorsConverterGlue());
        converterFactory.registerConverter("contributorsRolesAndSequencesConverter", contributorsRolesAndSequencesConverterGlue());
        converterFactory.registerConverter("visibilityConverter", visibilityConverter());

        ClassMapBuilder<Work, WorkEntity> workClassMap = mapperFactory.classMap(Work.class, WorkEntity.class);
        workClassMap.field("putCode", "id");
        addV3DateFields(workClassMap);
        registerSourceConverters(mapperFactory, workClassMap);
        workClassMap.field("shortDescription", "description");
        workClassMap.fieldMap("workExternalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        workClassMap.fieldMap("workContributors", "contributorsJson").converter("workContributorsConverterId").add();
        workClassMap.field("languageCode", "languageCode");
        workClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        workClassMap.field("url.value", "workUrl");
        workClassMap.field("country.value", "iso2Country");
        workClassMap.field("workTitle.title.content", "title");
        workClassMap.field("workTitle.translatedTitle.content", "translatedTitle");
        workClassMap.field("workTitle.translatedTitle.languageCode", "translatedTitleLanguageCode");
        workClassMap.field("workTitle.subtitle.content", "subtitle");
        workClassMap.field("workCitation.workCitationType", "citationType");
        workClassMap.field("workCitation.citation", "citation");
        workClassMap.exclude("workType").exclude("journalTitle").customize(new CustomMapper<Work, WorkEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(Work a, WorkEntity b, MappingContext context) {
                WORK_MAPPER_V3.mapWorkAtoB(a, b);
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(WorkEntity b, Work a, MappingContext context) {
                WORK_MAPPER_V3.mapWorkBtoA(b, a);
            }

        });
        workClassMap.byDefault();
        workClassMap.register();

        ClassMapBuilder<WorkSummary, WorkEntity> workSummaryClassMap = mapperFactory.classMap(WorkSummary.class, WorkEntity.class);
        addV3CommonFields(workSummaryClassMap);
        registerSourceConverters(mapperFactory, workSummaryClassMap);
        workSummaryClassMap.field("putCode", "id");
        workSummaryClassMap.field("title.title.content", "title");
        workSummaryClassMap.field("title.translatedTitle.content", "translatedTitle");
        workSummaryClassMap.field("title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        workSummaryClassMap.exclude("workType").exclude("journalTitle").customize(new CustomMapper<WorkSummary, WorkEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(WorkSummary a, WorkEntity b, MappingContext context) {
                WORK_MAPPER_V3.mapWorkSummaryAtoB(a, b);
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(WorkEntity b, WorkSummary a, MappingContext context) {
                WORK_MAPPER_V3.mapWorkSummaryBtoA(b, a);
            }

        });
        workSummaryClassMap.field("publicationDate", "publicationDate");
        workSummaryClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        workSummaryClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        workSummaryClassMap.field("url.value", "workUrl");
        workSummaryClassMap.byDefault();
        workSummaryClassMap.register();

        ClassMapBuilder<WorkSummary, MinimizedWorkEntity> workSummaryMinimizedClassMap = mapperFactory.classMap(WorkSummary.class, MinimizedWorkEntity.class);
        addV3CommonFields(workSummaryMinimizedClassMap);
        registerSourceConverters(mapperFactory, workSummaryMinimizedClassMap);
        workSummaryMinimizedClassMap.field("title.title.content", "title");
        workSummaryMinimizedClassMap.field("title.translatedTitle.content", "translatedTitle");
        workSummaryMinimizedClassMap.field("title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        workSummaryMinimizedClassMap.exclude("workType").exclude("journalTitle").customize(new CustomMapper<WorkSummary, MinimizedWorkEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(WorkSummary a, MinimizedWorkEntity b, MappingContext context) {
                WORK_MAPPER_V3.mapWorkSummaryMinimizedAtoB(a, b);
                
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(MinimizedWorkEntity b, WorkSummary a, MappingContext context) {
                WORK_MAPPER_V3.mapWorkSummaryMinimizedBtoA(b, a);
            }

        });
        ;
        workSummaryMinimizedClassMap.field("publicationDate.year.value", "publicationYear");
        workSummaryMinimizedClassMap.field("publicationDate.month.value", "publicationMonth");
        workSummaryMinimizedClassMap.field("publicationDate.day.value", "publicationDay");
        workSummaryMinimizedClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        workSummaryMinimizedClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        workSummaryMinimizedClassMap.field("url.value", "workUrl");
        workSummaryMinimizedClassMap.byDefault();
        workSummaryMinimizedClassMap.register();

        ClassMapBuilder<WorkSummaryExtended, MinimizedExtendedWorkEntity> workSummaryExtendedMinimizedClassMap = mapperFactory.classMap(WorkSummaryExtended.class, MinimizedExtendedWorkEntity.class);
        addV3CommonFields(workSummaryExtendedMinimizedClassMap);
        registerSourceConverters(mapperFactory, workSummaryExtendedMinimizedClassMap);
        workSummaryExtendedMinimizedClassMap.field("title.title.content", "title");
        
        workSummaryExtendedMinimizedClassMap.fieldMap("contributors", "contributorsJson").converter("workContributorsConverterId").add();
        workSummaryExtendedMinimizedClassMap.field("title.translatedTitle.content", "translatedTitle");
        workSummaryExtendedMinimizedClassMap.field("title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        workSummaryExtendedMinimizedClassMap.exclude("workType").exclude("journalTitle").customize(new CustomMapper<WorkSummaryExtended, MinimizedExtendedWorkEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(WorkSummaryExtended a, MinimizedExtendedWorkEntity b, MappingContext context) {
                WORK_MAPPER_V3.mapWorkSummaryExtendedMinimizedAtoB(a, b);
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(MinimizedExtendedWorkEntity b, WorkSummaryExtended a, MappingContext context) {
                WORK_MAPPER_V3.mapWorkSummaryExtendedMinimizedBtoA(b, a);
            }
        });

        workSummaryExtendedMinimizedClassMap.field("publicationDate.year.value", "publicationYear");
        workSummaryExtendedMinimizedClassMap.field("publicationDate.month.value", "publicationMonth");
        workSummaryExtendedMinimizedClassMap.field("publicationDate.day.value", "publicationDay");
        workSummaryExtendedMinimizedClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        workSummaryExtendedMinimizedClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        workSummaryExtendedMinimizedClassMap.field("url.value", "workUrl");
        workSummaryExtendedMinimizedClassMap.byDefault();
        workSummaryExtendedMinimizedClassMap.register();

        ClassMapBuilder<Work, MinimizedWorkEntity> minimizedWorkClassMap = mapperFactory.classMap(Work.class, MinimizedWorkEntity.class);
        registerSourceConverters(mapperFactory, minimizedWorkClassMap);
        minimizedWorkClassMap.field("putCode", "id");
        minimizedWorkClassMap.field("journalTitle.content", "journalTitle");
        minimizedWorkClassMap.field("workTitle.title.content", "title");
        minimizedWorkClassMap.field("workTitle.translatedTitle.content", "translatedTitle");
        minimizedWorkClassMap.field("workTitle.translatedTitle.languageCode", "translatedTitleLanguageCode");
        minimizedWorkClassMap.field("workTitle.subtitle.content", "subtitle");
        minimizedWorkClassMap.field("shortDescription", "description");
        minimizedWorkClassMap.exclude("workType").exclude("journalTitle").customize(new CustomMapper<Work, MinimizedWorkEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(Work a, MinimizedWorkEntity b, MappingContext context) {
                WORK_MAPPER_V3.mapMinimizedWorkAtoB(a, b);
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(MinimizedWorkEntity b, Work a, MappingContext context) {
                WORK_MAPPER_V3.mapMinimizedWorkBtoA(b, a);
            }

        });
        minimizedWorkClassMap.field("publicationDate.year.value", "publicationYear");
        minimizedWorkClassMap.field("publicationDate.month.value", "publicationMonth");
        minimizedWorkClassMap.field("publicationDate.day.value", "publicationDay");
        minimizedWorkClassMap.fieldMap("workExternalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        minimizedWorkClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        minimizedWorkClassMap.field("url.value", "workUrl");
        minimizedWorkClassMap.byDefault();
        minimizedWorkClassMap.register();

        mapperFactory.classMap(PublicationDate.class, PublicationDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day")
                .register();

        ClassMapBuilder<WorkExtended, WorkEntity> workExtendedClassMap = mapperFactory.classMap(WorkExtended.class, WorkEntity.class);
        workExtendedClassMap.field("putCode", "id");
        addV3DateFields(workClassMap);
        registerSourceConverters(mapperFactory, workClassMap);
        workExtendedClassMap.field("shortDescription", "description");
        workExtendedClassMap.fieldMap("workExternalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        workExtendedClassMap.field("languageCode", "languageCode");
        workExtendedClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        workExtendedClassMap.field("url.value", "workUrl");
        workExtendedClassMap.field("country.value", "iso2Country");
        workExtendedClassMap.field("workTitle.title.content", "title");
        workExtendedClassMap.field("workTitle.translatedTitle.content", "translatedTitle");
        workExtendedClassMap.field("workTitle.translatedTitle.languageCode", "translatedTitleLanguageCode");
        workExtendedClassMap.field("workTitle.subtitle.content", "subtitle");
        workExtendedClassMap.field("workCitation.workCitationType", "citationType");
        workExtendedClassMap.field("workCitation.citation", "citation");
        workExtendedClassMap.exclude("workType").exclude("journalTitle").customize(new CustomMapper<WorkExtended, WorkEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(WorkExtended a, WorkEntity b, MappingContext context) {
                WORK_MAPPER_V3.mapWorkExtendedAtoB(a, b);
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(WorkEntity b, WorkExtended a, MappingContext context) {
                WORK_MAPPER_V3.mapWorkExtendedBtoA(b, a, contributorsRolesAndSequencesConverter);
            }

        });
        workExtendedClassMap.byDefault();
        workExtendedClassMap.register();


        mapFuzzyDateToPublicationDateEntity(mapperFactory);

        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getFundingMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("fundingExternalIdentifiersConverterId", fundingExternalIdentifiersConverter());
        FundingContributorsMapperV3 fundingContributorsMapper = new FundingContributorsMapperV3(fundingContributorsRoleConverter);
        converterFactory.registerConverter("fundingContributorsConverterId", fundingContributorsConverter(fundingContributorsMapper));
        converterFactory.registerConverter("visibilityConverter", visibilityConverter());
        converterFactory.registerConverter("orgConverter", orgConverter());

        ClassMapBuilder<Funding, ProfileFundingEntity> fundingClassMap = mapperFactory.classMap(Funding.class, ProfileFundingEntity.class);
        addV3CommonFields(fundingClassMap);
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
        
        fundingClassMap.fieldMap("organization", "org").converter("orgConverter").add();
        
        fundingClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("fundingExternalIdentifiersConverterId").add();
        fundingClassMap.fieldMap("contributors", "contributorsJson").converter("fundingContributorsConverterId").add();
        fundingClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        fundingClassMap.customize(new CustomMapper<Funding, ProfileFundingEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(Funding a, ProfileFundingEntity b, MappingContext context) {
                FUNDING_MAPPER_V3.mapFundingCustomFields(a, b);
            }
        });

        fundingClassMap.byDefault();
        fundingClassMap.register();

        ClassMapBuilder<FundingSummary, ProfileFundingEntity> fundingSummaryClassMap = mapperFactory.classMap(FundingSummary.class, ProfileFundingEntity.class);
        addV3CommonFields(fundingSummaryClassMap);
        registerSourceConverters(mapperFactory, fundingSummaryClassMap);
        fundingSummaryClassMap.field("type", "type");
        fundingSummaryClassMap.field("title.title.content", "title");
        fundingSummaryClassMap.field("title.translatedTitle.content", "translatedTitle");
        fundingSummaryClassMap.field("title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        fundingSummaryClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("fundingExternalIdentifiersConverterId").add();
        fundingSummaryClassMap.field("url.value", "url");
        fundingSummaryClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        
        fundingSummaryClassMap.fieldMap("organization", "org").converter("orgConverter").add();
        mapFuzzyDateToStartDateEntityAndEndDateEntity(mapperFactory);
        
        fundingSummaryClassMap.byDefault();
        fundingSummaryClassMap.register();

        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getEducationMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();

        ClassMapBuilder<Education, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(Education.class, OrgAffiliationRelationEntity.class);

        ClassMapBuilder<EducationSummary, OrgAffiliationRelationEntity> summaryClassMap = mapperFactory.classMap(EducationSummary.class,
                OrgAffiliationRelationEntity.class);

        return generateMapperFacadeForAffiliation(mapperFactory, classMap, summaryClassMap);
    }

    public MapperFacade getEmploymentMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();

        ClassMapBuilder<Employment, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(Employment.class, OrgAffiliationRelationEntity.class);

        ClassMapBuilder<EmploymentSummary, OrgAffiliationRelationEntity> summaryClassMap = mapperFactory.classMap(EmploymentSummary.class,
                OrgAffiliationRelationEntity.class);

        return generateMapperFacadeForAffiliation(mapperFactory, classMap, summaryClassMap);
    }

    public MapperFacade getDistinctionMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();

        ClassMapBuilder<Distinction, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(Distinction.class, OrgAffiliationRelationEntity.class);

        ClassMapBuilder<DistinctionSummary, OrgAffiliationRelationEntity> summaryClassMap = mapperFactory.classMap(DistinctionSummary.class,
                OrgAffiliationRelationEntity.class);

        return generateMapperFacadeForAffiliation(mapperFactory, classMap, summaryClassMap);
    }

    public MapperFacade getInvitedPositionMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();

        ClassMapBuilder<InvitedPosition, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(InvitedPosition.class, OrgAffiliationRelationEntity.class);

        ClassMapBuilder<InvitedPositionSummary, OrgAffiliationRelationEntity> summaryClassMap = mapperFactory.classMap(InvitedPositionSummary.class,
                OrgAffiliationRelationEntity.class);

        return generateMapperFacadeForAffiliation(mapperFactory, classMap, summaryClassMap);
    }

    public MapperFacade getMembershipMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();

        ClassMapBuilder<Membership, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(Membership.class, OrgAffiliationRelationEntity.class);

        ClassMapBuilder<MembershipSummary, OrgAffiliationRelationEntity> summaryClassMap = mapperFactory.classMap(MembershipSummary.class,
                OrgAffiliationRelationEntity.class);

        return generateMapperFacadeForAffiliation(mapperFactory, classMap, summaryClassMap);
    }

    public MapperFacade getQualificationMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();

        ClassMapBuilder<Qualification, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(Qualification.class, OrgAffiliationRelationEntity.class);

        ClassMapBuilder<QualificationSummary, OrgAffiliationRelationEntity> summaryClassMap = mapperFactory.classMap(QualificationSummary.class,
                OrgAffiliationRelationEntity.class);

        return generateMapperFacadeForAffiliation(mapperFactory, classMap, summaryClassMap);
    }

    public MapperFacade getServiceMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();

        ClassMapBuilder<Service, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(Service.class, OrgAffiliationRelationEntity.class);

        ClassMapBuilder<ServiceSummary, OrgAffiliationRelationEntity> summaryClassMap = mapperFactory.classMap(ServiceSummary.class, OrgAffiliationRelationEntity.class);

        return generateMapperFacadeForAffiliation(mapperFactory, classMap, summaryClassMap);
    }

    /**
     * Configure fields for affiliations
     */
    private MapperFacade generateMapperFacadeForAffiliation(MapperFactory mapperFactory, ClassMapBuilder<? extends Affiliation, OrgAffiliationRelationEntity> classMap,
            ClassMapBuilder<? extends AffiliationSummary, OrgAffiliationRelationEntity> summaryClassMap) {
        
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("externalIdentifiersConverterId", externalIdentifiersConverter());
        converterFactory.registerConverter("visibilityConverter", visibilityConverter());
        converterFactory.registerConverter("orgConverter", orgConverter());
        
        // Configure element class map
        addV3CommonFields(classMap);
        registerSourceConverters(mapperFactory, classMap);

        classMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("externalIdentifiersConverterId").add();
        classMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();

        classMap.field("departmentName", "department");
        classMap.field("roleTitle", "title");
        classMap.fieldAToB("url.value", "url");
        classMap.fieldBToA("url", "url.value");
        
        classMap.fieldMap("organization", "org").converter("orgConverter").add();
        classMap.byDefault();
        classMap.register();

        // Configure element summary class map
        addV3CommonFields(summaryClassMap);
        registerSourceConverters(mapperFactory, summaryClassMap);
        summaryClassMap.field("departmentName", "department");
        summaryClassMap.field("roleTitle", "title");
        summaryClassMap.field("displayIndex", "displayIndex");
        summaryClassMap.fieldAToB("url.value", "url");
        summaryClassMap.fieldBToA("url", "url.value");
        summaryClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("externalIdentifiersConverterId").add();
        summaryClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        summaryClassMap.fieldMap("organization", "org").converter("orgConverter").add();
        summaryClassMap.byDefault();
        summaryClassMap.register();

        mapFuzzyDateToStartDateEntityAndEndDateEntity(mapperFactory);
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getPeerReviewMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();

        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("workExternalIdentifiersConverterId", workExternalIdentifiersV3Converter());
        converterFactory.registerConverter("workExternalIdentifierConverterId", peerReviewWorkExternalIdentifierConverter());
        converterFactory.registerConverter("visibilityConverter", visibilityConverter());
        converterFactory.registerConverter("orgConverter", orgConverter());

        // do same as work

        ClassMapBuilder<PeerReview, PeerReviewEntity> classMap = mapperFactory.classMap(PeerReview.class, PeerReviewEntity.class);
        addV3CommonFields(classMap);
        registerSourceConverters(mapperFactory, classMap);
        classMap.field("url.value", "url");
        classMap.field("groupId", "groupId");
        classMap.field("subjectUrl.value", "subjectUrl");
        classMap.field("subjectType", "subjectType");
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
                PEER_REVIEW_MAPPER_V3.mapPeerReviewAtoB(a, b);
            }
        });
        
        classMap.fieldMap("organization", "org").converter("orgConverter").add();

        classMap.byDefault();
        classMap.register();

        ClassMapBuilder<PeerReviewSummary, PeerReviewEntity> peerReviewSummaryClassMap = mapperFactory.classMap(PeerReviewSummary.class, PeerReviewEntity.class);
        addV3CommonFields(peerReviewSummaryClassMap);
        registerSourceConverters(mapperFactory, peerReviewSummaryClassMap);
        peerReviewSummaryClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        peerReviewSummaryClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        peerReviewSummaryClassMap.fieldMap("organization", "org").converter("orgConverter").add();
        peerReviewSummaryClassMap.byDefault();
        peerReviewSummaryClassMap.register();

        mapperFactory.classMap(FuzzyDate.class, CompletionDateEntity.class).field("year.value", "year").field("month.value", "month").field("day.value", "day")
                .register();

        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getResearchResourceMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        registerOrgClassMappings(mapperFactory);
        
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("workExternalIdentifiersConverterId", workExternalIdentifiersV3Converter());
        converterFactory.registerConverter("visibilityConverter", visibilityConverter());
        mapFuzzyDateToStartDateEntityAndEndDateEntity(mapperFactory);

        ClassMapBuilder<ResearchResource, ResearchResourceEntity> classMap = mapperFactory.classMap(ResearchResource.class, ResearchResourceEntity.class);
        addV3CommonFields(classMap);
        registerSourceConverters(mapperFactory, classMap);
        classMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        classMap.field("proposal.title.title.content", "title");
        classMap.field("proposal.title.translatedTitle.content", "translatedTitle");
        classMap.field("proposal.title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        classMap.fieldMap("proposal.externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        classMap.field("proposal.url.value", "url");
        classMap.field("proposal.startDate", "startDate");
        classMap.field("proposal.endDate", "endDate");
        classMap.field("proposal.hosts.organization", "hosts");
        classMap.customize(new CustomMapper<ResearchResource, ResearchResourceEntity>() {
            /**
             * From model object to database object
             */
            @Override
            public void mapAtoB(ResearchResource a, ResearchResourceEntity b, MappingContext context) {
            MISC_MAPPER_V3.mapResearchResourceAtoB(a, b);
            }
        });
        classMap.byDefault();
        classMap.register();

        ClassMapBuilder<ResearchResourceSummary, ResearchResourceEntity> summaryClassMap = mapperFactory.classMap(ResearchResourceSummary.class,
                ResearchResourceEntity.class);
        addV3CommonFields(summaryClassMap);
        registerSourceConverters(mapperFactory, summaryClassMap);
        summaryClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        summaryClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        summaryClassMap.field("proposal.title.title.content", "title");
        summaryClassMap.field("proposal.title.translatedTitle.content", "translatedTitle");
        summaryClassMap.field("proposal.title.translatedTitle.languageCode", "translatedTitleLanguageCode");
        summaryClassMap.fieldMap("proposal.externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        summaryClassMap.field("proposal.url.value", "url");
        summaryClassMap.field("proposal.startDate", "startDate");
        summaryClassMap.field("proposal.endDate", "endDate");
        summaryClassMap.field("proposal.hosts.organization", "hosts");
        summaryClassMap.byDefault();
        summaryClassMap.register();

        ClassMapBuilder<ResearchResourceItem, ResearchResourceItemEntity> itemClassMap = mapperFactory.classMap(ResearchResourceItem.class,
                ResearchResourceItemEntity.class);
        itemClassMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("workExternalIdentifiersConverterId").add();
        // itemClassMap.field("id", "id");
        // TODO: what do we do about IDs?
        itemClassMap.field("resourceName", "resourceName");
        itemClassMap.field("resourceType", "resourceType");
        itemClassMap.field("url.value", "url");
        itemClassMap.field("hosts.organization", "hosts");
        itemClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getGroupIdRecordMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();

        ClassMapBuilder<GroupIdRecord, GroupIdRecordEntity> classMap = mapperFactory.classMap(GroupIdRecord.class, GroupIdRecordEntity.class);
        addV3CommonFields(classMap);
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
        clientClassMap.fieldBToA("userOBOEnabled", "userOBOEnabled");

        clientClassMap.customize(new CustomMapper<Client, ClientDetailsEntity>() {
            /**
             * On the way in, from Client to ClientDetailsEntity, we need to
             * care about mapping the redirect uri's, since all config features
             * will not change from UI requests
             */
            @Override
            public void mapAtoB(Client a, ClientDetailsEntity b, MappingContext context) {
                CLIENT_MAPPER_V3.syncRedirectUrisFromClient(a, b);
            }

            /**
             * On the way out, from ClientDetailsEntity to Client, we just need
             * to care about mapping the redirect uri's and the primary client
             * secret since all config features will not be visible on the UI
             */
            @Override
            public void mapBtoA(ClientDetailsEntity b, Client a, MappingContext context) {
                CLIENT_MAPPER_V3.populateClientFromEntity(b, a, encryptionManager);
            }
        });
        clientClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getNameMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", visibilityConverter());

        ClassMapBuilder<Name, RecordNameEntity> nameClassMap = mapperFactory.classMap(Name.class, RecordNameEntity.class);
        addV3DateFields(nameClassMap);
        nameClassMap.field("creditName.content", "creditName");
        nameClassMap.field("givenNames.content", "givenNames");
        nameClassMap.field("familyName.content", "familyName");
        nameClassMap.field("path", "orcid");
        nameClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();
        nameClassMap.customize(new CustomMapper<Name, RecordNameEntity>() {
            @Override
            public void mapAtoB(Name a, RecordNameEntity b, MappingContext context) {
                b.setCreditName(normalizeBlank(a.getCreditName() == null ? null : a.getCreditName().getContent()));
                b.setGivenNames(normalizeBlank(a.getGivenNames() == null ? null : a.getGivenNames().getContent()));
                b.setFamilyName(normalizeBlank(a.getFamilyName() == null ? null : a.getFamilyName().getContent()));
            }

            @Override
            public void mapBtoA(RecordNameEntity b, Name a, MappingContext context) {
                a.setCreditName(toCreditNameV3(b.getCreditName()));
                a.setGivenNames(toGivenNamesV3(b.getGivenNames()));
                a.setFamilyName(toFamilyNameV3(b.getFamilyName()));
            }
        });

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


    public MapperFacade getSpamMapperFacade() {
        MapperFactory mapperFactory = getNewMapperFactory();
        ClassMapBuilder<Spam, SpamEntity> classMap = mapperFactory.classMap(Spam.class, SpamEntity.class);                       
        classMap.fieldBToA("sourceType", "sourceType");
        classMap.fieldBToA("spamCounter", "spamCounter");
        addV3DateFields(classMap);
        classMap.byDefault();
        classMap.register();
        return mapperFactory.getMapperFacade();
    }
    
    
    private ClassMapBuilder<?, ?> mapCommonFields(ClassMapBuilder<?, ?> builder) {
        return builder.field("createdDate", "dateCreated").field("putCode", "id").byDefault();
    }

    private void addV3CommonFields(ClassMapBuilder<?, ?> classMap) {
        classMap.field("putCode", "id");
        addV3DateFields(classMap);
    }

    private void addV3DateFields(ClassMapBuilder<?, ?> classMap) {
        classMap.fieldBToA("dateCreated", "createdDate.value");
        classMap.fieldBToA("lastModified", "lastModifiedDate.value");
    }
    
    private void mapFuzzyDateToPublicationDateEntity(MapperFactory mapperFactory) {
        mapperFactory.classMap(FuzzyDate.class, PublicationDateEntity.class).customize(new CustomMapper<FuzzyDate, PublicationDateEntity>() {
            @Override
            public void mapAtoB(FuzzyDate fuzzyDate, PublicationDateEntity entity, MappingContext context) {
                FUZZY_DATE_MAPPER_V3.fuzzyDateToPublicationDateEntity(fuzzyDate, entity);
            }

            @Override
            public void mapBtoA(PublicationDateEntity entity, FuzzyDate fuzzyDate, MappingContext context) {
                FUZZY_DATE_MAPPER_V3.publicationDateEntityToFuzzyDate(entity, fuzzyDate);
            }
        }).register();
        
        
    }

    private void mapFuzzyDateToStartDateEntityAndEndDateEntity(MapperFactory mapperFactory) {
        mapperFactory.classMap(FuzzyDate.class, StartDateEntity.class).customize(new CustomMapper<FuzzyDate, StartDateEntity>() {
            @Override
            public void mapAtoB(FuzzyDate fuzzyDate, StartDateEntity entity, MappingContext context) {
                FUZZY_DATE_MAPPER_V3.fuzzyDateToStartDateEntity(fuzzyDate, entity);
            }

            @Override
            public void mapBtoA(StartDateEntity entity, FuzzyDate fuzzyDate, MappingContext context) {
                FUZZY_DATE_MAPPER_V3.startDateEntityToFuzzyDate(entity, fuzzyDate);
            }
        }).register();

        mapperFactory.classMap(FuzzyDate.class, EndDateEntity.class).customize(new CustomMapper<FuzzyDate, EndDateEntity>() {
            @Override
            public void mapAtoB(FuzzyDate fuzzyDate, EndDateEntity entity, MappingContext context) {
                FUZZY_DATE_MAPPER_V3.fuzzyDateToEndDateEntity(fuzzyDate, entity);
            }

            @Override
            public void mapBtoA(EndDateEntity entity, FuzzyDate fuzzyDate, MappingContext context) {
                FUZZY_DATE_MAPPER_V3.endDateEntityToFuzzyDate(entity, fuzzyDate);
            }
        }).register();
    }


    private String normalizeBlank(String value) {
        if (value != null && value.trim().isEmpty()) {
            return null;
        }
        return value;
    }

    private org.orcid.jaxb.model.v3.release.common.CreditName toCreditNameV3(String value) {
        String normalized = normalizeBlank(value);
        if (normalized == null) {
            return null;
        }
        org.orcid.jaxb.model.v3.release.common.CreditName creditName = new org.orcid.jaxb.model.v3.release.common.CreditName();
        creditName.setContent(normalized);
        return creditName;
    }

    private org.orcid.jaxb.model.v3.release.record.GivenNames toGivenNamesV3(String value) {
        String normalized = normalizeBlank(value);
        if (normalized == null) {
            return null;
        }
        return new org.orcid.jaxb.model.v3.release.record.GivenNames(normalized);
    }

    private org.orcid.jaxb.model.v3.release.record.FamilyName toFamilyNameV3(String value) {
        String normalized = normalizeBlank(value);
        if (normalized == null) {
            return null;
        }
        return new org.orcid.jaxb.model.v3.release.record.FamilyName(normalized);
    }

    /**
     * Inline Orika glue wrapping the Orika-free {@link VisibilityMapperV3}; kept local to this
     * (already Orika-based) factory rather than the mapstruct package.
     */
    private static ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.common.Visibility, String> visibilityConverter() {
        return new ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.common.Visibility, String>() {
            @Override
            public String convertTo(org.orcid.jaxb.model.v3.release.common.Visibility source, ma.glasnost.orika.metadata.Type<String> destinationType) {
                return VisibilityMapperV3.INSTANCE.convertTo(source);
            }

            @Override
            public org.orcid.jaxb.model.v3.release.common.Visibility convertFrom(String source, ma.glasnost.orika.metadata.Type<org.orcid.jaxb.model.v3.release.common.Visibility> destinationType) {
                return VisibilityMapperV3.INSTANCE.convertFrom(source);
            }
        };
    }

    /**
     * Inline Orika glue wrapping the Orika-free {@link OrgMapperV3}; kept local to this
     * (already Orika-based) factory rather than the mapstruct package.
     */
    private static ma.glasnost.orika.converter.BidirectionalConverter<Organization, OrgEntity> orgConverter() {
        return new ma.glasnost.orika.converter.BidirectionalConverter<Organization, OrgEntity>() {
            @Override
            public OrgEntity convertTo(Organization source, ma.glasnost.orika.metadata.Type<OrgEntity> destinationType) {
                return OrgMapperV3.INSTANCE.convertTo(source);
            }

            @Override
            public Organization convertFrom(OrgEntity source, ma.glasnost.orika.metadata.Type<Organization> destinationType) {
                return OrgMapperV3.INSTANCE.convertFrom(source);
            }
        };
    }

    /**
     * Inline Orika glue wrapping the Orika-free {@link FundingContributorsMapperV3}; kept local
     * to this (already Orika-based) factory rather than the mapstruct package.
     */
    private static ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.record.FundingContributors, String> fundingContributorsConverter(
            FundingContributorsMapperV3 mapper) {
        return new ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.record.FundingContributors, String>() {
            @Override
            public String convertTo(org.orcid.jaxb.model.v3.release.record.FundingContributors source, ma.glasnost.orika.metadata.Type<String> destinationType) {
                return mapper.convertTo(source);
            }

            @Override
            public org.orcid.jaxb.model.v3.release.record.FundingContributors convertFrom(String source,
                    ma.glasnost.orika.metadata.Type<org.orcid.jaxb.model.v3.release.record.FundingContributors> destinationType) {
                return mapper.convertFrom(source);
            }
        };
    }

    /**
     * Inline Orika glue wrapping the Spring-managed {@link JSONWorkExternalIdentifiersMapperV3}
     * (needs real Spring injection for its ExternalIdentifierTypeMapper/PIDNormalizationService/
     * PIDResolverService/LocaleManager dependencies).
     */
    private ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.record.ExternalIDs, String> workExternalIdentifiersV3Converter() {
        return new ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.record.ExternalIDs, String>() {
            @Override
            public String convertTo(org.orcid.jaxb.model.v3.release.record.ExternalIDs source, ma.glasnost.orika.metadata.Type<String> destinationType) {
                return jsonWorkExternalIdentifiersMapperV3.convertTo(source);
            }

            @Override
            public org.orcid.jaxb.model.v3.release.record.ExternalIDs convertFrom(String source,
                    ma.glasnost.orika.metadata.Type<org.orcid.jaxb.model.v3.release.record.ExternalIDs> destinationType) {
                return jsonWorkExternalIdentifiersMapperV3.convertFrom(source);
            }
        };
    }

    /**
     * Inline Orika glue wrapping the Spring-managed {@link ContributorsRolesAndSequencesConverter}
     * (needs real Spring injection for its workContributorRoleConverter dependency).
     */
    private ma.glasnost.orika.converter.BidirectionalConverter<java.util.List<org.orcid.pojo.ContributorsRolesAndSequences>, String> contributorsRolesAndSequencesConverterGlue() {
        return new ma.glasnost.orika.converter.BidirectionalConverter<java.util.List<org.orcid.pojo.ContributorsRolesAndSequences>, String>() {
            @Override
            public String convertTo(java.util.List<org.orcid.pojo.ContributorsRolesAndSequences> source, ma.glasnost.orika.metadata.Type<String> destinationType) {
                return contributorsRolesAndSequencesConverter.convertTo(source);
            }

            @Override
            public java.util.List<org.orcid.pojo.ContributorsRolesAndSequences> convertFrom(String source,
                    ma.glasnost.orika.metadata.Type<java.util.List<org.orcid.pojo.ContributorsRolesAndSequences>> destinationType) {
                return contributorsRolesAndSequencesConverter.convertFrom(source);
            }
        };
    }

    /**
     * Inline Orika glue wrapping the Spring-managed {@link org.orcid.core.adapter.v3.converter.WorkContributorsConverter}
     * (needs real Spring injection for its ContributorRoleConverter dependency).
     */
    private ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.record.WorkContributors, String> workContributorsConverterGlue() {
        return new ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.record.WorkContributors, String>() {
            @Override
            public String convertTo(org.orcid.jaxb.model.v3.release.record.WorkContributors source, ma.glasnost.orika.metadata.Type<String> destinationType) {
                return workContributorsConverter.convertTo(source);
            }

            @Override
            public org.orcid.jaxb.model.v3.release.record.WorkContributors convertFrom(String source,
                    ma.glasnost.orika.metadata.Type<org.orcid.jaxb.model.v3.release.record.WorkContributors> destinationType) {
                return workContributorsConverter.convertFrom(source);
            }
        };
    }

    @Override
    public Class<?> getObjectType() {
        return MapperFacade.class;
    }

    /**
     * Inline Orika glue wrapping the Orika-free {@link org.orcid.core.adapter.mapstruct.jsonidentifier.ExternalIdentifierTypeMapper};
     * kept local to this (already Orika-based) factory rather than the mapstruct package.
     */
    private static ma.glasnost.orika.converter.BidirectionalConverter<String, String> externalIdentifierIdConverter() {
        return new ma.glasnost.orika.converter.BidirectionalConverter<String, String>() {
            @Override
            public String convertTo(String source, ma.glasnost.orika.metadata.Type<String> destinationType) {
                return ExternalIdentifierTypeMapper.INSTANCE.convertTo(source);
            }

            @Override
            public String convertFrom(String source, ma.glasnost.orika.metadata.Type<String> destinationType) {
                return ExternalIdentifierTypeMapper.INSTANCE.convertFrom(source);
            }
        };
    }

    /**
     * Inline Orika glue wrapping the Spring-managed {@link JSONFundingExternalIdentifiersMapperV3}
     * (needs real Spring injection for its ExternalIdentifierTypeMapper dependency).
     */
    private ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.record.ExternalIDs, String> fundingExternalIdentifiersConverter() {
        return new ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.record.ExternalIDs, String>() {
            @Override
            public String convertTo(org.orcid.jaxb.model.v3.release.record.ExternalIDs source, ma.glasnost.orika.metadata.Type<String> destinationType) {
                return jsonFundingExternalIdentifiersMapperV3.convertTo(source);
            }

            @Override
            public org.orcid.jaxb.model.v3.release.record.ExternalIDs convertFrom(String source,
                    ma.glasnost.orika.metadata.Type<org.orcid.jaxb.model.v3.release.record.ExternalIDs> destinationType) {
                return jsonFundingExternalIdentifiersMapperV3.convertFrom(source);
            }
        };
    }

    /**
     * Inline Orika glue wrapping the Spring-managed {@link JSONExternalIdentifiersMapperV3}
     * (needs real Spring injection for its ExternalIdentifierTypeMapper/PIDNormalizationService/
     * LocaleManager dependencies).
     */
    private ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.record.ExternalIDs, String> externalIdentifiersConverter() {
        return new ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.record.ExternalIDs, String>() {
            @Override
            public String convertTo(org.orcid.jaxb.model.v3.release.record.ExternalIDs source, ma.glasnost.orika.metadata.Type<String> destinationType) {
                return jsonExternalIdentifiersMapperV3.convertTo(source);
            }

            @Override
            public org.orcid.jaxb.model.v3.release.record.ExternalIDs convertFrom(String source,
                    ma.glasnost.orika.metadata.Type<org.orcid.jaxb.model.v3.release.record.ExternalIDs> destinationType) {
                return jsonExternalIdentifiersMapperV3.convertFrom(source);
            }
        };
    }

    /**
     * Inline Orika glue wrapping the Spring-managed {@link JSONPeerReviewWorkExternalIdentifierMapperV3}
     * (needs real Spring injection for its ExternalIdentifierTypeMapper dependency).
     */
    private ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.record.ExternalID, String> peerReviewWorkExternalIdentifierConverter() {
        return new ma.glasnost.orika.converter.BidirectionalConverter<org.orcid.jaxb.model.v3.release.record.ExternalID, String>() {
            @Override
            public String convertTo(org.orcid.jaxb.model.v3.release.record.ExternalID source, ma.glasnost.orika.metadata.Type<String> destinationType) {
                return jsonPeerReviewWorkExternalIdentifierMapperV3.convertTo(source);
            }

            @Override
            public org.orcid.jaxb.model.v3.release.record.ExternalID convertFrom(String source,
                    ma.glasnost.orika.metadata.Type<org.orcid.jaxb.model.v3.release.record.ExternalID> destinationType) {
                return jsonPeerReviewWorkExternalIdentifierMapperV3.convertFrom(source);
            }
        };
    }


    @Override
    public boolean isSingleton() {
        return true;
    }

}
