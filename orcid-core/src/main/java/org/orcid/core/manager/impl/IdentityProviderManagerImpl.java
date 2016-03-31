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
package org.orcid.core.manager.impl;

import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.utils.NamespaceMap;
import org.orcid.persistence.dao.IdentityProviderDao;
import org.orcid.persistence.jpa.entities.IdentityProviderEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private Pattern mailtoPattern = Pattern.compile("^mailto:");

    @Override
    public void loadIdentityProviders() {
        String[] metadataUrls = StringUtils.split(metadataUrlsString);
        XPath xpath = createXPath();
        XPathExpression entityDescriptorXpath = compileXPath(xpath, "//saml2:EntityDescriptor");
        XPathExpression displayNameXpath = compileXPath(xpath, "string(.//mdui:DisplayName[1])");
        XPathExpression supportContactXpath = compileXPath(xpath, "string((.//saml2:ContactPerson[@contactType='support'])[1]/saml2:EmailAddress[1])");
        for (String metadataUrl : metadataUrls) {
            Document document = downloadMetadata(metadataUrl);
            NodeList nodes = evaluateXPathNodeList(entityDescriptorXpath, document);
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                String entityId = element.getAttribute("entityID");
                String displayName = evaluateXPathString(displayNameXpath, element);
                String supportEmail = tidyEmail(evaluateXPathString(supportContactXpath, element));
                LOGGER.info("Found entityID: {}, displayName: {}, supportEmail: {}", new Object[] { entityId, displayName, supportEmail });
                saveOrUpdateIdentityProvider(entityId, displayName, supportEmail);
            }
        }
    }

    private Document downloadMetadata(String metadataUrl) {
        LOGGER.info("About to download idp metadata from {}", metadataUrl);
        Client client = Client.create();
        WebResource resource = client.resource(metadataUrl);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        Document document = response.getEntity(Document.class);
        return document;
    }

    private void saveOrUpdateIdentityProvider(String entityId, String displayName, String supportEmail) {
        IdentityProviderEntity entity = identityProviderDao.findByProviderid(entityId);
        if (entity == null) {
            entity = new IdentityProviderEntity();
        }
        entity.setProviderid(entityId);
        entity.setDisplayName(displayName);
        entity.setSupportEmail(supportEmail);
        identityProviderDao.merge(entity);
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
        namespaceMap.putNamespace("saml2", "urn:oasis:names:tc:SAML:2.0:metadata");
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

}
