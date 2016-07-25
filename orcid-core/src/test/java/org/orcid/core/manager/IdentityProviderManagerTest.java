package org.orcid.core.manager;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.orcid.core.manager.impl.IdentityProviderManagerImpl;
import org.orcid.persistence.jpa.entities.IdentityProviderEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 
 * @author Will Simpson
 *
 */
public class IdentityProviderManagerTest {

    private IdentityProviderManager identityProviderManager = new IdentityProviderManagerImpl();

    @Test
    public void testCreateEntityFromXml() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(getClass().getResourceAsStream("example_idp.xml"));
        Element idpElement = doc.getDocumentElement();
        IdentityProviderEntity result = identityProviderManager.createEntityFromXml(idpElement);
        assertNotNull(result);
        assertEquals("https://idp.example.ch/idp/shibboleth", result.getProviderid());
        assertEquals("Universität Example Display", result.getDisplayName());
        assertEquals("support@example.ch", result.getSupportEmail());
        assertNull(result.getAdminEmail());
        assertEquals("technical@example.ch", result.getTechEmail());
    }

    @Test
    public void testCreateEntityFromXmlWithLegacyNames() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(getClass().getResourceAsStream("example_idp_legacy.xml"));
        Element idpElement = doc.getDocumentElement();
        IdentityProviderEntity result = identityProviderManager.createEntityFromXml(idpElement);
        assertNotNull(result);
        assertEquals("https://registry.shibboleth.example.ac.uk/idp", result.getProviderid());
        // assertEquals("University of Example Display",
        // result.getDisplayName());
        assertEquals("help@it.example.ac.uk", result.getSupportEmail());
        assertEquals("admin@it.example.ac.uk", result.getAdminEmail());
        assertEquals("sysdev@it.example.ac.uk", result.getTechEmail());
    }

}
