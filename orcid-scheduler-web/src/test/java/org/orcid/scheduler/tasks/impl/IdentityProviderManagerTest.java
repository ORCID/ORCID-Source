package org.orcid.scheduler.tasks.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.IdentityProviderEntity;
import org.orcid.persistence.jpa.entities.IdentityProviderNameEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-scheduler-context.xml" })
public class IdentityProviderManagerTest {

    private IdentityProviderLoaderImpl identityProviderLoaderImpl = new IdentityProviderLoaderImpl();

    @Test
    public void testCreateEntityFromXml() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(IdentityProviderManagerTest.class.getClassLoader().getResourceAsStream("org/orcid/core/manager/example_idp.xml"));
        Element idpElement = doc.getDocumentElement();
        IdentityProviderEntity result = identityProviderLoaderImpl.createEntityFromXml(idpElement);
        assertNotNull(result);
        assertEquals("https://idp.example.ch/idp/shibboleth", result.getProviderid());
        List<IdentityProviderNameEntity> names = result.getNames();
        assertNotNull(names);
        assertEquals(4, names.size());
        Map<String, IdentityProviderNameEntity> mapByLang = names.stream().collect(Collectors.toMap(IdentityProviderNameEntity::getLang, Function.identity()));
        assertEquals("Universität Example Display", mapByLang.get("de").getDisplayName());
        assertEquals("University of Example Display", mapByLang.get("en").getDisplayName());
        assertEquals("Université de Example Display", mapByLang.get("fr").getDisplayName());
        assertEquals("Università di Example Display", mapByLang.get("it").getDisplayName());
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
        Document doc = dBuilder.parse(IdentityProviderManagerTest.class.getClassLoader().getResourceAsStream("org/orcid/core/manager/example_idp_legacy.xml"));
        Element idpElement = doc.getDocumentElement();
        IdentityProviderEntity result = identityProviderLoaderImpl.createEntityFromXml(idpElement);
        assertNotNull(result);
        assertEquals("https://registry.shibboleth.example.ac.uk/idp", result.getProviderid());
        List<IdentityProviderNameEntity> names = result.getNames();
        assertNotNull(names);
        assertEquals(1, names.size());
        IdentityProviderNameEntity name = names.get(0);
        assertEquals("University of Example Display", name.getDisplayName());
        assertEquals("en", name.getLang());
        assertEquals("University of Example Display", result.getDisplayName());
        assertEquals("help@it.example.ac.uk", result.getSupportEmail());
        assertEquals("admin@it.example.ac.uk", result.getAdminEmail());
        assertEquals("sysdev@it.example.ac.uk", result.getTechEmail());
    }

}
