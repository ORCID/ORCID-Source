package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.utils.NamespaceMap;
import org.orcid.persistence.dao.IdentityProviderDao;
import org.orcid.persistence.jpa.entities.IdentityProviderEntity;
import org.orcid.persistence.jpa.entities.IdentityProviderNameEntity;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * 
 * @author Will Simpson
 *
 */
public class IdentityProviderManagerImpl implements IdentityProviderManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityProviderManagerImpl.class);

    @Value("${org.orcid.core.idpMetadataUrlsSpaceSeparated:http://www.testshib.org/metadata/testshib-providers.xml https://engine.surfconext.nl/authentication/idp/metadata}")
    private String metadataUrlsString;

    @Resource
    private IdentityProviderDao identityProviderDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "identityProviderNameCache")
    private Cache<IdentityProviderNameCacheKey, String> identityProviderNameCache;

    private String releaseName = ReleaseNameUtils.getReleaseName();

    private Pattern mailtoPattern = Pattern.compile("^mailto:");

    @Override
    public String retrieveIdentitifyProviderName(String providerid) {
        return retrieveIdentitifyProviderName(providerid, localeManager.getLocale());
    }

    @Override
    public String retrieveIdentitifyProviderName(String providerid, Locale locale) {
        return identityProviderNameCache.get(new IdentityProviderNameCacheKey(providerid, locale, releaseName));
    }

    @Override
    @Transactional
    public String retrieveFreshIdentitifyProviderName(String providerid, Locale locale) {
        IdentityProviderEntity idp = identityProviderDao.findByProviderid(providerid);
        List<IdentityProviderNameEntity> names = idp.getNames();
        if (names != null) {
            Optional<IdentityProviderNameEntity> idpNameEntity = names.stream().filter(n -> n.getLang().equals(locale.getLanguage())).findFirst();
            if (idpNameEntity.isPresent()) {
                return idpNameEntity.get().getDisplayName();
            }
        }
        return idp.getDisplayName();
    }

    @Override
    public String retrieveContactEmailByProviderid(String providerid) {
        IdentityProviderEntity idp = identityProviderDao.findByProviderid(providerid);
        if (idp == null) {
            return null;
        }
        String supportEmail = idp.getSupportEmail();
        if (supportEmail != null) {
            return supportEmail;
        }
        List<String> otherEmails = new ArrayList<>(2);
        otherEmails.add(idp.getAdminEmail());
        otherEmails.add(idp.getTechEmail());
        return String.join(";", otherEmails.stream().filter(e -> e != null).collect(Collectors.toList()));
    }

    @Override
    public void loadIdentityProviders() {
        String[] metadataUrls = StringUtils.split(metadataUrlsString);
        XPath xpath = createXPath();
        XPathExpression entityDescriptorXpath = compileXPath(xpath, "//md:EntityDescriptor");

        for (String metadataUrl : metadataUrls) {
            Document document = downloadMetadata(metadataUrl);
            NodeList nodes = evaluateXPathNodeList(entityDescriptorXpath, document);
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                IdentityProviderEntity incoming = createEntityFromXml(element);
                LOGGER.info("Found identity provider: {}", incoming.toShortString());
                saveOrUpdateIdentityProvider(incoming);
            }
        }
    }

    @Override
    public IdentityProviderEntity createEntityFromXml(Element idpElement) {
        XPath xpath = createXPath();
        XPathExpression mainDisplayNameXpath = compileXPath(xpath, "string(.//md:IDPSSODescriptor//mdui:DisplayName[1])");
        XPathExpression displayNamesXpath = compileXPath(xpath, ".//md:IDPSSODescriptor//mdui:DisplayName");
        XPathExpression legacyMainDisplayNameXpath = compileXPath(xpath, "string(.//md:OrganizationDisplayName[1])");
        XPathExpression legacyDisplayNamesXpath = compileXPath(xpath, ".//md:OrganizationDisplayName");
        XPathExpression supportContactXpath = compileXPath(xpath, "string((.//md:ContactPerson[@contactType='support'])[1]/md:EmailAddress[1])");
        XPathExpression adminContactXpath = compileXPath(xpath, "string((.//md:ContactPerson[@contactType='administrative'])[1]/md:EmailAddress[1])");
        XPathExpression techContactXpath = compileXPath(xpath, "string((.//md:ContactPerson[@contactType='technical'])[1]/md:EmailAddress[1])");

        String entityId = idpElement.getAttribute("entityID");
        String mainDisplayName = evaluateXPathString(mainDisplayNameXpath, idpElement);
        if (StringUtils.isBlank(mainDisplayName)) {
            mainDisplayName = evaluateXPathString(legacyMainDisplayNameXpath, idpElement);
        }
        String supportEmail = tidyEmail(evaluateXPathString(supportContactXpath, idpElement));
        String adminEmail = tidyEmail(evaluateXPathString(adminContactXpath, idpElement));
        String techEmail = tidyEmail(evaluateXPathString(techContactXpath, idpElement));
        List<IdentityProviderNameEntity> nameEntities = createNameEntitiesFromXml(displayNamesXpath, idpElement);
        if (nameEntities == null || nameEntities.isEmpty()) {
            nameEntities = createNameEntitiesFromXml(legacyDisplayNamesXpath, idpElement);
        }

        IdentityProviderEntity identityProviderEntity = new IdentityProviderEntity();
        identityProviderEntity.setProviderid(entityId);
        identityProviderEntity.setDisplayName(mainDisplayName);
        identityProviderEntity.setNames(nameEntities);
        identityProviderEntity.setSupportEmail(supportEmail);
        identityProviderEntity.setAdminEmail(adminEmail);
        identityProviderEntity.setTechEmail(techEmail);
        return identityProviderEntity;
    }

    private List<IdentityProviderNameEntity> createNameEntitiesFromXml(XPathExpression displayNamesXpath, Element idpElement) {
        List<IdentityProviderNameEntity> nameEntities = new ArrayList<>();
        NodeList displayNames = evaluateXPathNodeList(displayNamesXpath, idpElement);
        if (displayNames != null) {
            for (int i = 0; i < displayNames.getLength(); i++) {
                Element displayNameElement = (Element) displayNames.item(i);
                String lang = displayNameElement.getAttribute("xml:lang");
                String displayName = displayNameElement.getTextContent();
                IdentityProviderNameEntity nameEntity = new IdentityProviderNameEntity();
                nameEntity.setLang(lang);
                nameEntity.setDisplayName(displayName);
                nameEntities.add(nameEntity);
            }
        }
        return nameEntities;
    }

    private Document downloadMetadata(String metadataUrl) {
        LOGGER.info("About to download idp metadata from {}", metadataUrl);
        Client client = Client.create();
        WebResource resource = client.resource(metadataUrl);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        Document document = response.getEntity(Document.class);
        return document;
    }

    private void saveOrUpdateIdentityProvider(IdentityProviderEntity incoming) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                saveOrUpdateIdentityProviderInTransaction(incoming);
            }
        });
    }

    private void saveOrUpdateIdentityProviderInTransaction(IdentityProviderEntity incoming) {
        IdentityProviderEntity existing = identityProviderDao.findByProviderid(incoming.getProviderid());
        if (existing == null) {
            incoming.getNames().stream().forEach(i -> i.setIdentityProvider(incoming));
            identityProviderDao.persist(incoming);
        } else {
            existing.setProviderid(incoming.getProviderid());
            existing.setDisplayName(incoming.getDisplayName());
            existing.setSupportEmail(incoming.getSupportEmail());
            existing.setAdminEmail(incoming.getAdminEmail());
            existing.setTechEmail(incoming.getTechEmail());
            mergeNames(incoming, existing);
            identityProviderDao.merge(existing);
        }
    }

    private void mergeNames(IdentityProviderEntity incoming, IdentityProviderEntity existing) {
        List<IdentityProviderNameEntity> existingNames = existing.getNames();
        List<IdentityProviderNameEntity> incomingNames = incoming.getNames();
        if (existingNames != null) {
            Map<String, IdentityProviderNameEntity> incomingNamesMappedByLang = incomingNames.stream()
                    .collect(Collectors.toMap(IdentityProviderNameEntity::getLang, Function.identity()));
            // Update existing name entities
            existingNames.stream().forEach(e -> {
                IdentityProviderNameEntity incomingName = incomingNamesMappedByLang.get(e.getLang());
                if (incomingName != null) {
                    e.setDisplayName(incoming.getDisplayName());
                }
            });
            // Remove existing names that are not in the incoming
            // list
            existingNames.removeIf(e -> {
                return incomingNames.stream().noneMatch(i -> {
                    String existingDisplayName = e.getDisplayName();
                    return i.getDisplayName().equals(existingDisplayName);
                });
            });
            // Add new names
            Map<String, IdentityProviderNameEntity> existingNamesMappedByLang = existingNames.stream()
                    .collect(Collectors.toMap(IdentityProviderNameEntity::getLang, Function.identity()));
            incomingNames.stream().forEach(i -> {
                if (!existingNamesMappedByLang.containsKey(i.getLang())) {
                    i.setIdentityProvider(existing);
                    existingNames.add(i);
                }
            });
        } else {
            incomingNames.stream().forEach(i -> i.setIdentityProvider(incoming));
            existing.setNames(incomingNames);
        }
    }

    private NodeList evaluateXPathNodeList(XPathExpression xpathExpression, Node node) {
        try {
            return (NodeList) xpathExpression.evaluate(node, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Problem evaluating xpath node list", e);
        }
    }

    private String evaluateXPathString(XPathExpression xpathExpression, Node node) {
        try {
            return (String) xpathExpression.evaluate(node, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Problem evaluating xpath string", e);
        }
    }

    private XPathExpression compileXPath(XPath xpath, String expression) {
        try {
            return xpath.compile(expression);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Problem compiling xpath: " + expression, e);
        }
    }

    private XPath createXPath() {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NamespaceMap namespaceMap = new NamespaceMap();
        namespaceMap.putNamespace("md", "urn:oasis:names:tc:SAML:2.0:metadata");
        namespaceMap.putNamespace("mdui", "urn:oasis:names:tc:SAML:metadata:ui");
        xpath.setNamespaceContext(namespaceMap);
        return xpath;
    }

    private String tidyEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return null;
        }
        return mailtoPattern.matcher(email).replaceAll("").trim();
    }

    @Override
    public void incrementFailedCount(String shibIdentityProvider) {
        identityProviderDao.incrementFailedCount(shibIdentityProvider);
    }

}
