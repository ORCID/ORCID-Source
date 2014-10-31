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
package org.orcid.api.t1.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.OrcidApiConstants;
import org.orcid.api.common.OrcidApiService;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.test.DBUnitTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Declan Newman (declan) Date: 12/04/2012
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-t1-client-context.xml" })
public class T1OrcidApiClientIntegrationTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ProfileWorksEntityData.xml",
            "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml", "/data/WebhookEntityData.xml");

    private static String ORCID = "4444-4444-4444-4441";

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Arrays.asList("/data/WebhookEntityData.xml", "/data/Oauth2TokenDetailsData.xml", "/data/ClientDetailsEntityData.xml",
                "/data/ProfileWorksEntityData.xml", "/data/WorksEntityData.xml", "/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml",
                "/data/EmptyEntityData.xml"));
    }

    @Resource
    private OrcidApiService<ClientResponse> t1Client;

    @Test
    public void testStatus() {
        ClientResponse clientResponse = t1Client.viewStatusText();
        assertEquals(200, clientResponse.getStatus());
        assertEquals(OrcidApiConstants.STATUS_OK_MESSAGE, clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewBioDetailsHtml() throws Exception {
        ClientResponse clientResponse = t1Client.viewBioDetailsHtml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertNotNull(clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewBioDetailsXml() throws Exception {
        ClientResponse clientResponse = t1Client.viewBioDetailsXml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewBioDetailsJson() throws Exception {
        ClientResponse clientResponse = t1Client.viewBioDetailsJson(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewExternalIdentifiersHtml() throws Exception {
        ClientResponse clientResponse = t1Client.viewExternalIdentifiersHtml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertNotNull(clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewExternalIdentifiersXml() throws Exception {
        ClientResponse clientResponse = t1Client.viewExternalIdentifiersXml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewExternalIdentifiersJson() throws Exception {
        ClientResponse clientResponse = t1Client.viewExternalIdentifiersJson(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewFullDetailsHtml() throws Exception {
        ClientResponse clientResponse = t1Client.viewFullDetailsHtml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertNotNull(clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewFullDetailsXml() throws Exception {
        ClientResponse clientResponse = t1Client.viewFullDetailsXml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewFullDetailsJson() throws Exception {
        ClientResponse clientResponse = t1Client.viewFullDetailsJson(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewWorksDetailsHtml() throws Exception {
        ClientResponse clientResponse = t1Client.viewWorksDetailsHtml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertNotNull(clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewWorksDetailsXml() throws Exception {
        ClientResponse clientResponse = t1Client.viewWorksDetailsXml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewWorksDetailsJson() throws Exception {
        ClientResponse clientResponse = t1Client.viewWorksDetailsJson(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

}
