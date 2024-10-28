package org.orcid.core.adapter.v3.impl;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.adapter.converter.EmptyStringToNullConverter;
import org.orcid.core.adapter.impl.WorkEntityFactory;
import org.orcid.core.adapter.jsonidentifier.converter.ExternalIdentifierTypeConverter;
import org.orcid.core.adapter.jsonidentifier.converter.JSONExternalIdentifiersConverterV3;
import org.orcid.core.adapter.jsonidentifier.converter.JSONFundingExternalIdentifiersConverterV3;
import org.orcid.core.adapter.jsonidentifier.converter.JSONPeerReviewWorkExternalIdentifierConverterV3;
import org.orcid.core.adapter.jsonidentifier.converter.JSONWorkExternalIdentifiersConverterV3;
import org.orcid.core.adapter.v3.converter.ContributorsRolesAndSequencesConverter;
import org.orcid.core.adapter.v3.converter.CreditNameConverter;
import org.orcid.core.adapter.v3.converter.FamilyNameConverter;
import org.orcid.core.adapter.v3.converter.FundingContributorsConverter;
import org.orcid.core.adapter.v3.converter.GivenNamesConverter;
import org.orcid.core.adapter.v3.converter.OrgConverter;
import org.orcid.core.adapter.v3.converter.VisibilityConverter;
import org.orcid.core.adapter.v3.converter.WorkContributorsConverter;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.contributors.roles.fundings.FundingContributorRoleConverter;
import org.orcid.core.contributors.roles.works.WorkContributorRoleConverter;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.read_only.ClientDetailsManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverService;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.client.Client;
import org.orcid.jaxb.model.v3.release.client.ClientRedirectUri;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.jaxb.model.v3.release.common.Day;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.Month;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.release.common.PublicationDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.Year;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationAdministrative;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationCustom;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationServiceAnnouncement;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationTip;
import org.orcid.jaxb.model.v3.release.notification.permission.AuthorizationUrl;
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
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriStatus;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
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
import org.orcid.pojo.ajaxForm.PojoUtil;
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

    @Resource(name = "clientDetailsManagerReadOnlyV3")
    private ClientDetailsManagerReadOnly clientDetailsManagerReadOnly;

    @Resource
    private IdentityProviderManager identityProviderManager;

    @Resource
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

    @Override
    public MapperFacade getObject() throws Exception {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

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
                            // validate
                            validateAndConvertToURI(authUrl);
                            entity.setAuthorizationUrl(authUrl);
                            entity.setAuthenticationProviderId(notification.getServiceProviderId());
                        }
                    }

                    @Override
                    public void mapBtoA(NotificationFindMyStuffEntity entity, NotificationFindMyStuff notification, MappingContext context) {
                        AuthorizationUrl authUrl = notification.getAuthorizationUrl();
                        if (authUrl != null) {
                            authUrl.setPath(extractFullPath(authUrl.getUri()));
                            authUrl.setHost(orcidUrlManager.getBaseHost());
                        }
                        notification.setServiceProviderId(entity.getAuthenticationProviderId());
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
            Source source = SourceEntityUtils.extractSourceFromEntityComplete(b, sourceNameCacheManager, orcidUrlManager, clientDetailsEntityCacheManager);
            a.setSource(source);
        }
    }

    public MapperFacade getExternalIdentifierMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());

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
                b.setExternalIdUrl(a.getUrl() == null ? null : a.getUrl().getValue());
            }
        });
        externalIdentifierClassMap.byDefault();
        registerSourceConverters(mapperFactory, externalIdentifierClassMap);

        // TODO: add relationship to database schema for people.
        externalIdentifierClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getResearcherUrlMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());
        mapperFactory.getConverterFactory().registerConverter("emptyStringToNullConverter", new EmptyStringToNullConverter());

        ClassMapBuilder<ResearcherUrl, ResearcherUrlEntity> researcherUrlClassMap = mapperFactory.classMap(ResearcherUrl.class, ResearcherUrlEntity.class);
        addV3DateFields(researcherUrlClassMap);
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
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());

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
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());

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
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());

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
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());
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
                if (b.getRegion() != null && !b.getRegion().isEmpty()) {
                    if (a.getAddress() == null) {
                        a.setAddress(new OrganizationAddress());
                    }
                    a.getAddress().setRegion(b.getRegion());
                }
            }
            
        });
        
        orgClassMap.fieldBToA("orgDisambiguated.sourceId", "disambiguatedOrganization.disambiguatedOrganizationIdentifier");
        orgClassMap.fieldBToA("orgDisambiguated.sourceType", "disambiguatedOrganization.disambiguationSource");
        orgClassMap.fieldBToA("orgDisambiguated.id", "disambiguatedOrganization.id");
        orgClassMap.register();
    }
    
    public MapperFacade getWorkMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        WorkContributorsConverter wcc = new WorkContributorsConverter(workContributorsRoleConverter);
        ContributorsRolesAndSequencesConverter contributorsRolesAndSequencesConverter = new ContributorsRolesAndSequencesConverter(workContributorsRoleConverter);

        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("workExternalIdentifiersConverterId", new JSONWorkExternalIdentifiersConverterV3(norm, resolverService, localeManager));
        converterFactory.registerConverter("workContributorsConverterId", wcc);
        converterFactory.registerConverter("contributorsRolesAndSequencesConverter", contributorsRolesAndSequencesConverter);
        converterFactory.registerConverter("visibilityConverter", new VisibilityConverter());

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
                b.setWorkType(a.getWorkType().name());
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
                a.setWorkType(WorkType.valueOf(b.getWorkType()));
                a.setJournalTitle(b.getJournalTitle() != null && !b.getJournalTitle().isEmpty() ? new Title(b.getJournalTitle()) : null);
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
                b.setWorkType(a.getType().name());
                b.setJournalTitle(a.getJournalTitle() != null && a.getJournalTitle().getContent() != null ? a.getJournalTitle().getContent() : null);
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(WorkEntity b, WorkSummary a, MappingContext context) {
                a.setType(WorkType.valueOf(b.getWorkType()));
                a.setJournalTitle(b.getJournalTitle() != null && !b.getJournalTitle().isEmpty() ? new Title(b.getJournalTitle()) : null);
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
                b.setWorkType(a.getType().name());
                b.setJournalTitle(a.getJournalTitle() != null && a.getJournalTitle().getContent() != null ? a.getJournalTitle().getContent() : null);
                
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(MinimizedWorkEntity b, WorkSummary a, MappingContext context) {
                a.setType(WorkType.valueOf(b.getWorkType()));
                a.setJournalTitle(b.getJournalTitle() != null && !b.getJournalTitle().isEmpty() ? new Title(b.getJournalTitle()) : null);
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
                b.setWorkType(a.getType().name());
                b.setJournalTitle(a.getJournalTitle() != null && a.getJournalTitle().getContent() != null ? a.getJournalTitle().getContent() : null);                
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(MinimizedExtendedWorkEntity b, WorkSummaryExtended a, MappingContext context) {
                a.setType(WorkType.valueOf(b.getWorkType()));
                a.setJournalTitle(b.getJournalTitle() != null && !b.getJournalTitle().isEmpty() ? new Title(b.getJournalTitle()) : null);               
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
                b.setWorkType(a.getWorkType().name());
                b.setJournalTitle(a.getJournalTitle() != null && a.getJournalTitle().getContent() != null ? a.getJournalTitle().getContent() : null);
            }

            /**
             * From database to model object
             */
            @Override
            public void mapBtoA(MinimizedWorkEntity b, Work a, MappingContext context) {
                a.setWorkType(WorkType.valueOf(b.getWorkType()));
                a.setJournalTitle(b.getJournalTitle() != null && !b.getJournalTitle().isEmpty() ? new Title(b.getJournalTitle()) : null);
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
                b.setWorkType(a.getWorkType().name());
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
            public void mapBtoA(WorkEntity b, WorkExtended a, MappingContext context) {
                a.setWorkType(WorkType.valueOf(b.getWorkType()));
                a.setJournalTitle(b.getJournalTitle() != null && !b.getJournalTitle().isEmpty() ? new Title(b.getJournalTitle()) : null);

                if (!PojoUtil.isEmpty(b.getTopContributorsJson())) {
                    a.setContributorsGroupedByOrcid(contributorsRolesAndSequencesConverter.getContributorsRolesAndSequencesList(b.getTopContributorsJson()));
                }
            }

        });
        workExtendedClassMap.byDefault();
        workExtendedClassMap.register();


        mapFuzzyDateToPublicationDateEntity(mapperFactory);

        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getFundingMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("fundingExternalIdentifiersConverterId", new JSONFundingExternalIdentifiersConverterV3());
        converterFactory.registerConverter("fundingContributorsConverterId", new FundingContributorsConverter(fundingContributorsRoleConverter));
        converterFactory.registerConverter("visibilityConverter", new VisibilityConverter());
        converterFactory.registerConverter("orgConverter", new OrgConverter());

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
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        ClassMapBuilder<Education, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(Education.class, OrgAffiliationRelationEntity.class);

        ClassMapBuilder<EducationSummary, OrgAffiliationRelationEntity> summaryClassMap = mapperFactory.classMap(EducationSummary.class,
                OrgAffiliationRelationEntity.class);

        return generateMapperFacadeForAffiliation(mapperFactory, classMap, summaryClassMap);
    }

    public MapperFacade getEmploymentMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        ClassMapBuilder<Employment, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(Employment.class, OrgAffiliationRelationEntity.class);

        ClassMapBuilder<EmploymentSummary, OrgAffiliationRelationEntity> summaryClassMap = mapperFactory.classMap(EmploymentSummary.class,
                OrgAffiliationRelationEntity.class);

        return generateMapperFacadeForAffiliation(mapperFactory, classMap, summaryClassMap);
    }

    public MapperFacade getDistinctionMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        ClassMapBuilder<Distinction, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(Distinction.class, OrgAffiliationRelationEntity.class);

        ClassMapBuilder<DistinctionSummary, OrgAffiliationRelationEntity> summaryClassMap = mapperFactory.classMap(DistinctionSummary.class,
                OrgAffiliationRelationEntity.class);

        return generateMapperFacadeForAffiliation(mapperFactory, classMap, summaryClassMap);
    }

    public MapperFacade getInvitedPositionMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        ClassMapBuilder<InvitedPosition, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(InvitedPosition.class, OrgAffiliationRelationEntity.class);

        ClassMapBuilder<InvitedPositionSummary, OrgAffiliationRelationEntity> summaryClassMap = mapperFactory.classMap(InvitedPositionSummary.class,
                OrgAffiliationRelationEntity.class);

        return generateMapperFacadeForAffiliation(mapperFactory, classMap, summaryClassMap);
    }

    public MapperFacade getMembershipMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        ClassMapBuilder<Membership, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(Membership.class, OrgAffiliationRelationEntity.class);

        ClassMapBuilder<MembershipSummary, OrgAffiliationRelationEntity> summaryClassMap = mapperFactory.classMap(MembershipSummary.class,
                OrgAffiliationRelationEntity.class);

        return generateMapperFacadeForAffiliation(mapperFactory, classMap, summaryClassMap);
    }

    public MapperFacade getQualificationMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        ClassMapBuilder<Qualification, OrgAffiliationRelationEntity> classMap = mapperFactory.classMap(Qualification.class, OrgAffiliationRelationEntity.class);

        ClassMapBuilder<QualificationSummary, OrgAffiliationRelationEntity> summaryClassMap = mapperFactory.classMap(QualificationSummary.class,
                OrgAffiliationRelationEntity.class);

        return generateMapperFacadeForAffiliation(mapperFactory, classMap, summaryClassMap);
    }

    public MapperFacade getServiceMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

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
        converterFactory.registerConverter("externalIdentifiersConverterId", new JSONExternalIdentifiersConverterV3(norm, localeManager));
        converterFactory.registerConverter("visibilityConverter", new VisibilityConverter());
        converterFactory.registerConverter("orgConverter", new OrgConverter());
        
        // Configure element class map
        addV3CommonFields(classMap);
        registerSourceConverters(mapperFactory, classMap);

        classMap.fieldMap("externalIdentifiers", "externalIdentifiersJson").converter("externalIdentifiersConverterId").add();
        classMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();

        classMap.field("departmentName", "department");
        classMap.field("roleTitle", "title");
        classMap.fieldAToB("url.value", "url");
        classMap.fieldBToA("url", "url.value");

        class AffiliationUrlMapper<T, U> extends CustomMapper<Affiliation, OrgAffiliationRelationEntity> {
            @Override
            public void mapAtoB(Affiliation a, OrgAffiliationRelationEntity b, MappingContext context) {
                b.setUrl(a.getUrl() == null ? null : a.getUrl().getValue());
            }
        }

        @SuppressWarnings("rawtypes")
        AffiliationUrlMapper affiliationUrlMapper = new AffiliationUrlMapper();
        classMap.customize(affiliationUrlMapper);
        
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
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("workExternalIdentifiersConverterId", new JSONWorkExternalIdentifiersConverterV3(norm, resolverService, localeManager));
        converterFactory.registerConverter("workExternalIdentifierConverterId", new JSONPeerReviewWorkExternalIdentifierConverterV3());
        converterFactory.registerConverter("visibilityConverter", new VisibilityConverter());
        converterFactory.registerConverter("orgConverter", new OrgConverter());

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
                b.setUrl(a.getUrl() == null ? null : a.getUrl().getValue());
                b.setSubjectUrl(a.getSubjectUrl() == null ? null : a.getSubjectUrl().getValue());
                b.setSubjectName(
                        (a.getSubjectName() == null || a.getSubjectName().getTitle() == null) ? null : a.getSubjectName().getTitle().getContent());
                b.setSubjectTranslatedName(
                        (a.getSubjectName() == null || a.getSubjectName().getTranslatedTitle() == null) ? null : a.getSubjectName().getTranslatedTitle().getContent());
                b.setSubjectTranslatedNameLanguageCode((a.getSubjectName() == null || a.getSubjectName().getTranslatedTitle() == null) ? null
                        : a.getSubjectName().getTranslatedTitle().getLanguageCode());
                b.setSubjectContainerName(a.getSubjectContainerName() == null ? null : a.getSubjectContainerName().getContent());
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
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        registerOrgClassMappings(mapperFactory);
        
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("workExternalIdentifiersConverterId", new JSONWorkExternalIdentifiersConverterV3(norm, resolverService, localeManager));
        converterFactory.registerConverter("visibilityConverter", new VisibilityConverter());
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
                b.setTranslatedTitle((a.getProposal() == null || a.getProposal().getTitle() == null || a.getProposal().getTitle().getTranslatedTitle() == null) ? null
                        : a.getProposal().getTitle().getTranslatedTitle().getContent());
                b.setTranslatedTitleLanguageCode(
                        (a.getProposal() == null || a.getProposal().getTitle() == null || a.getProposal().getTitle().getTranslatedTitle() == null) ? null
                                : a.getProposal().getTitle().getTranslatedTitle().getLanguageCode());
                b.setUrl((a.getProposal() == null || a.getProposal().getUrl() == null) ? null : a.getProposal().getUrl().getValue());
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
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

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
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
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
                            existingEntity.setStatus(ClientRedirectUriStatus.valueOf(cru.getStatus()));
                            b.getClientRegisteredRedirectUris().add(existingEntity);
                        } else {
                            ClientRedirectUriEntity newEntity = new ClientRedirectUriEntity();
                            newEntity.setClientDetailsEntity(b);
                            newEntity.setPredefinedClientScope(ScopePathType.getScopesAsSingleString(cru.getPredefinedClientScopes()));
                            newEntity.setRedirectUri(cru.getRedirectUri());
                            newEntity.setRedirectUriType(cru.getRedirectUriType());
                            newEntity.setUriActType(cru.getUriActType());
                            newEntity.setUriGeoArea(cru.getUriGeoArea());
                            newEntity.setStatus(ClientRedirectUriStatus.valueOf(cru.getStatus()));
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
                        element.setStatus(entity.getStatus().name());
                        a.getClientRedirectUris().add(element);
                    }
                }
                if (b.getAuthorizedGrantTypes() != null && b.getAuthorizedGrantTypes().contains(OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE)) {
                    a.setOboEnabled(true);
                } else {
                    a.setOboEnabled(false);
                }
            }
        });
        clientClassMap.register();
        return mapperFactory.getMapperFacade();
    }

    public MapperFacade getNameMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.getConverterFactory().registerConverter("visibilityConverter", new VisibilityConverter());
        mapperFactory.getConverterFactory().registerConverter("familyNameConverter", new FamilyNameConverter());
        mapperFactory.getConverterFactory().registerConverter("givenNamesConverter", new GivenNamesConverter());
        mapperFactory.getConverterFactory().registerConverter("creditNameConverter", new CreditNameConverter());

        ClassMapBuilder<Name, RecordNameEntity> nameClassMap = mapperFactory.classMap(Name.class, RecordNameEntity.class);
        addV3DateFields(nameClassMap);
        nameClassMap.fieldMap("creditName", "creditName").converter("creditNameConverter").add();
        ;
        nameClassMap.fieldMap("givenNames", "givenNames").converter("givenNamesConverter").add();
        ;
        nameClassMap.fieldMap("familyName", "familyName").converter("familyNameConverter").add();
        ;
        nameClassMap.field("path", "orcid");
        nameClassMap.fieldMap("visibility", "visibility").converter("visibilityConverter").add();

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


    public MapperFacade getSpamMapperFacade() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
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

}
