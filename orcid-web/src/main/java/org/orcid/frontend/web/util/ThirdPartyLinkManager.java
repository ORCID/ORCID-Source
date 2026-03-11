package org.orcid.frontend.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.pojo.ajaxForm.ImportWizzardClientForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.SearchAndLinkWizardFormSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ThirdPartyLinkManager implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThirdPartyLinkManager.class);

    @Resource(name = "clientRedirectDaoReadOnly")
    private ClientRedirectDao clientRedirectDaoReadOnly;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Resource
    private RedisClient redisClient;

    @Value("${org.orcid.core.utils.cache.redis.works-search-and-link-wizard.ttl:3600}")
    private int worksSearchAndLinkWizardCacheTtl;

    private static final String SEARCH_AND_LINK_WIZARD_CACHE_KEY = "works-search-and-link-wizard-clients";
    private static final ObjectMapper LIST_MAPPER = new ObjectMapper();

    @Cacheable("import-works-clients")
    public List<ImportWizzardClientForm> findOrcidClientsWithPredefinedOauthScopeWorksImport(Locale locale) {
        LOGGER.debug("Generating IMPORT_WORKS_WIZARD list");
        return generateImportWizzardForm(RedirectUriType.IMPORT_WORKS_WIZARD, locale);
    }

    @Override
    public void afterPropertiesSet() {
        initCache();
    }

    /**
     * Initializes the works search-and-link wizard cache in Redis.
     * Called on Spring startup so the cache is warm before Tomcat accepts requests.
     */
    public void initCache() {
        getWorksSearchAndLinkWizardBaseList();
    }

    /**
     * Returns the works search-and-link wizard base list: from Redis when valid,
     * otherwise builds and populates Redis.
     */
    private List<SearchAndLinkWizardFormSummary> getWorksSearchAndLinkWizardBaseList() {
        String cached = redisClient.get(SEARCH_AND_LINK_WIZARD_CACHE_KEY);
        if (StringUtils.isNotBlank(cached)) {
            try {
                return LIST_MAPPER.readValue(cached, new TypeReference<List<SearchAndLinkWizardFormSummary>>() {});
            } catch (Exception e) {
                LOGGER.warn("Failed to deserialize works search-and-link wizard list from Redis, rebuilding", e);
                return populateCache();
            }
        }
        return populateCache();
    }

    private List<SearchAndLinkWizardFormSummary> populateCache() {
        List<SearchAndLinkWizardFormSummary> list = generateSearchAndLinkWizardFormBase(RedirectUriType.IMPORT_WORKS_WIZARD);
        redisClient.set(SEARCH_AND_LINK_WIZARD_CACHE_KEY, JsonUtils.convertToJsonString(list), worksSearchAndLinkWizardCacheTtl);
        return list;
    }

    /**
     * Returns works search-and-link wizard clients. Base list (id, name, redirectUri, scopes, redirectUriMetadata)
     * is cached in Redis; isConnected is computed per request from
     * orcidOauth2TokenDetailService.doesClientKnowUser(clientId, currentUserOrcid).
     */
    public List<SearchAndLinkWizardFormSummary> findSearchAndLinkWizardClients(String currentUserOrcid) {
        List<SearchAndLinkWizardFormSummary> list = getWorksSearchAndLinkWizardBaseList();
        for (SearchAndLinkWizardFormSummary form : list) {
            form.setConnected(StringUtils.isNotBlank(currentUserOrcid)
                    && orcidOauth2TokenDetailService.doesClientKnowUser(form.getId(), currentUserOrcid));
        }
        return list;
    }

    @Cacheable("import-funding-clients")
    public List<ImportWizzardClientForm> findOrcidClientsWithPredefinedOauthScopeFundingImport(Locale locale) {
        LOGGER.debug("Generating IMPORT_FUNDING_WIZARD list");
        return generateImportWizzardForm(RedirectUriType.IMPORT_FUNDING_WIZARD, locale);
    }

    @Cacheable("read-access-clients")
    public List<ImportWizzardClientForm> findOrcidClientsWithPredefinedOauthScopeReadAccess(Locale locale) {
        LOGGER.debug("Generating GRANT_READ_WIZARD list");
        return generateImportWizzardForm(RedirectUriType.GRANT_READ_WIZARD, locale);
    }
    
    @Cacheable("import-peer-review-clients")
    public List<ImportWizzardClientForm> findOrcidClientsWithPredefinedOauthScopePeerReviewImport(Locale locale) {
        LOGGER.debug("Generating IMPORT_PEER_REVIEW_WIZARD list");
        return generateImportWizzardForm(RedirectUriType.IMPORT_PEER_REVIEW_WIZARD, locale);
    }

    private List<ImportWizzardClientForm> generateImportWizzardForm(RedirectUriType rut, Locale locale) {
        List<ClientRedirectUriEntity> entitiesWithRedirectUriType = clientRedirectDaoReadOnly.findClientDetailsWithRedirectScope(rut.value());
        List<ImportWizzardClientForm> clients = new ArrayList<ImportWizzardClientForm>();
        for (ClientRedirectUriEntity entity : entitiesWithRedirectUriType) {
            String clientId = entity.getClientId();
            ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
            ImportWizzardClientForm clientForm = new ImportWizzardClientForm();
            clientForm.setId(clientDetails.getId());
            clientForm.setName(clientDetails.getClientName());
            clientForm.setDescription(clientDetails.getClientDescription());
            clientForm.setRedirectUri(entity.getRedirectUri());
            clientForm.setScopes(entity.getPredefinedClientScope());
            clientForm.setStatus(entity.getStatus().name());
            clientForm.setClientWebsite(clientDetails.getClientWebsite());
            // Backwards compatible: only include metadata when non-empty and not "{}"
            if (!PojoUtil.isEmpty(entity.getRedirectUriMetadata())) {
                JsonNode metadataNode = JsonUtils.readTree(entity.getRedirectUriMetadata());
                if (!(metadataNode != null && metadataNode.isObject() && metadataNode.size() == 0)) {
                    clientForm.setRedirectUriMetadata(metadataNode);
                }
            }
            if (RedirectUriType.IMPORT_WORKS_WIZARD.equals(rut)) {
                processImportWorksWizzard(entity, clientForm, locale);
            }
            clients.add(clientForm);
        }
        return clients;
    }
    
    private void processImportWorksWizzard(ClientRedirectUriEntity entity, ImportWizzardClientForm clientForm, Locale locale) {
        if (!PojoUtil.isEmpty(entity.getUriActType())) {
            JsonNode node = JsonUtils.readTree(entity.getUriActType());
            List<String> elements = new ArrayList<String>();
            if (node.has("import-works-wizard")) {
                node.get("import-works-wizard").forEach(x -> {
                    String value = x.asText();
                    switch (value) {
                    case "Articles":
                        elements.add(localeManager.resolveMessage("workspace.works.import_wizzard.work_type.articles", locale));
                        break;
                    case "Data":
                        elements.add(localeManager.resolveMessage("workspace.works.import_wizzard.work_type.data", locale));
                        break;
                    case "Books":
                        elements.add(localeManager.resolveMessage("workspace.works.import_wizzard.work_type.books", locale));
                        break;
                    case "Student Publications":
                        elements.add(localeManager.resolveMessage("workspace.works.import_wizzard.work_type.student_publications", locale));
                        break;
                    default:
                        elements.add(value);
                        break;
                    }
                });
            }
            clientForm.setActTypes(elements);
        }
        if (!PojoUtil.isEmpty(entity.getUriActType())) {
            JsonNode node = JsonUtils.readTree(entity.getUriGeoArea());
            List<String> elements = new ArrayList<String>();
            if (node.has("import-works-wizard")) {
                node.get("import-works-wizard").forEach(x -> {
                    String value = x.asText();
                    switch (value) {
                    case "Africa":
                        elements.add(localeManager.resolveMessage("workspace.works.import_wizzard.geo_area.africa", locale));
                        break;
                    case "Asia":
                        elements.add(localeManager.resolveMessage("workspace.works.import_wizzard.geo_area.asia", locale));
                        break;
                    case "Australia":
                        elements.add(localeManager.resolveMessage("workspace.works.import_wizzard.geo_area.australia", locale));
                        break;
                    case "Europe":
                        elements.add(localeManager.resolveMessage("workspace.works.import_wizzard.geo_area.europe", locale));
                        break;
                    case "Global":
                        elements.add(localeManager.resolveMessage("workspace.works.import_wizzard.geo_area.global", locale));
                        break;
                    case "North America":
                        elements.add(localeManager.resolveMessage("workspace.works.import_wizzard.geo_area.north_america", locale));
                        break;
                    case "South America":
                        elements.add(localeManager.resolveMessage("workspace.works.import_wizzard.geo_area.south_america", locale));
                        break;
                    default:
                        elements.add(value);
                        break;
                    }
                });
            }
            clientForm.setGeoAreas(elements);
        }
    }

    /**
     * Builds the base list of works search-and-link wizard clients (no isConnected set).
     * Used for Redis cache storage and when cache is disabled.
     */
    private List<SearchAndLinkWizardFormSummary> generateSearchAndLinkWizardFormBase(RedirectUriType rut) {
        List<ClientRedirectUriEntity> entitiesWithRedirectUriType = clientRedirectDaoReadOnly.findClientDetailsWithRedirectScope(rut.value());
        List<SearchAndLinkWizardFormSummary> clients = new ArrayList<>();
        for (ClientRedirectUriEntity entity : entitiesWithRedirectUriType) {
            String clientId = entity.getClientId();
            ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
            SearchAndLinkWizardFormSummary form = new SearchAndLinkWizardFormSummary();
            form.setId(clientDetails.getId());
            form.setName(clientDetails.getClientName());
            form.setRedirectUri(entity.getRedirectUri());
            form.setScopes(entity.getPredefinedClientScope());
            if (!PojoUtil.isEmpty(clientDetails.getClientDescription())) {
                form.setDescription(clientDetails.getClientDescription());
            }
            if (!PojoUtil.isEmpty(entity.getRedirectUriMetadata())) {
                JsonNode metadataNode = JsonUtils.readTree(entity.getRedirectUriMetadata());
                if (!(metadataNode != null && metadataNode.isObject() && metadataNode.size() == 0)) {
                    form.setRedirectUriMetadata(metadataNode);
                    if (metadataNode.has("defaultDescription") && metadataNode.get("defaultDescription").isTextual()) {
                        form.setDescription(metadataNode.get("defaultDescription").asText());
                    }
                }
            }
            clients.add(form);
        }
        return clients;
    }
}
