package org.orcid.frontend.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.pojo.ajaxForm.ImportWizzardClientForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import com.fasterxml.jackson.databind.JsonNode;

public class ThirdPartyLinkManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThirdPartyLinkManager.class);

    @Resource(name = "clientRedirectDaoReadOnly")
    private ClientRedirectDao clientRedirectDaoReadOnly;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private LocaleManager localeManager;

    @Cacheable("import-works-clients")
    public List<ImportWizzardClientForm> findOrcidClientsWithPredefinedOauthScopeWorksImport(Locale locale) {
        LOGGER.debug("Generating IMPORT_WORKS_WIZARD list");
        return generateImportWizzardForm(RedirectUriType.IMPORT_WORKS_WIZARD, locale);
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
}
