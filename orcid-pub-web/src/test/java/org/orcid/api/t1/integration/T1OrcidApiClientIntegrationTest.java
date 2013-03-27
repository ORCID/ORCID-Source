/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.OrcidApiConstants;
import org.orcid.api.common.OrcidApiService;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 12/04/2012
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-t1-client-context.xml" })
public class T1OrcidApiClientIntegrationTest {

    private static final String ORCID = "0000-0003-4654-1403";

    @Resource
    private OrcidApiService<ClientResponse> t1Client;

    @Test
    public void testStatus() {
        ClientResponse clientResponse = t1Client.viewStatusText();
        assertEquals(200, clientResponse.getStatus());
        assertEquals(MediaType.TEXT_PLAIN, clientResponse.getType().toString());
        assertEquals(OrcidApiConstants.STATUS_OK_MESSAGE, clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewBioDetailsHtml() throws Exception {
        ClientResponse clientResponse = t1Client.viewBioDetailsHtml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(MediaType.TEXT_HTML, clientResponse.getType().toString());
        assertNotNull(clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewBioDetailsXml() throws Exception {
        ClientResponse clientResponse = t1Client.viewBioDetailsXml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(OrcidApiConstants.VND_ORCID_XML, clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcid().getValue());
    }

    @Test
    public void testViewBioDetailsJson() throws Exception {
        ClientResponse clientResponse = t1Client.viewBioDetailsJson(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(OrcidApiConstants.VND_ORCID_JSON, clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcid().getValue());
    }

    @Test
    public void testViewExternalIdentifiersHtml() throws Exception {
        ClientResponse clientResponse = t1Client.viewExternalIdentifiersHtml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(MediaType.TEXT_HTML, clientResponse.getType().toString());
        assertNotNull(clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewExternalIdentifiersXml() throws Exception {
        ClientResponse clientResponse = t1Client.viewExternalIdentifiersXml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(OrcidApiConstants.VND_ORCID_XML, clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcid().getValue());
    }

    @Test
    public void testViewExternalIdentifiersJson() throws Exception {
        ClientResponse clientResponse = t1Client.viewExternalIdentifiersJson(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(OrcidApiConstants.VND_ORCID_JSON, clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcid().getValue());
    }

    @Test
    public void testViewFullDetailsHtml() throws Exception {
        ClientResponse clientResponse = t1Client.viewFullDetailsHtml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(MediaType.TEXT_HTML, clientResponse.getType().toString());
        assertNotNull(clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewFullDetailsXml() throws Exception {
        ClientResponse clientResponse = t1Client.viewFullDetailsXml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(OrcidApiConstants.VND_ORCID_XML, clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcid().getValue());
    }

    @Test
    public void testViewFullDetailsJson() throws Exception {
        ClientResponse clientResponse = t1Client.viewFullDetailsJson(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(OrcidApiConstants.VND_ORCID_JSON, clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcid().getValue());
    }

    @Test
    public void testViewWorksDetailsHtml() throws Exception {
        ClientResponse clientResponse = t1Client.viewWorksDetailsHtml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(MediaType.TEXT_HTML, clientResponse.getType().toString());
        assertNotNull(clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewWorksDetailsXml() throws Exception {
        ClientResponse clientResponse = t1Client.viewWorksDetailsXml(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(OrcidApiConstants.VND_ORCID_XML, clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcid().getValue());
    }

    @Test
    public void testViewWorksDetailsJson() throws Exception {
        ClientResponse clientResponse = t1Client.viewWorksDetailsJson(ORCID);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertEquals(OrcidApiConstants.VND_ORCID_JSON, clientResponse.getType().toString());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(ORCID, orcidMessage.getOrcidProfile().getOrcid().getValue());
    }

}
