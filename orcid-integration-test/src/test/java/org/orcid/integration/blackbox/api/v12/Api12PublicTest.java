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
package org.orcid.integration.blackbox.api.v12;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class Api12PublicTest extends BlackBoxBaseV2Release {
    
    @Resource(name = "t1OAuthClient_1_2")
    protected T1OAuthOrcidApiClientImpl t1OAuthClient;
    
    @Test
    public void testViewBioDetailsHtml() throws Exception {
        ClientResponse clientResponse = t1OAuthClient.viewBioDetailsHtml(getUser1OrcidId());
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertNotNull(clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewBioDetailsXml() throws Exception {
        ClientResponse clientResponse = t1OAuthClient.viewBioDetailsXml(getUser1OrcidId());
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(getUser1OrcidId(), orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewBioDetailsJson() throws Exception {
        ClientResponse clientResponse = t1OAuthClient.viewBioDetailsJson(getUser1OrcidId());
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(getUser1OrcidId(), orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewExternalIdentifiersHtml() throws Exception {
        ClientResponse clientResponse = t1OAuthClient.viewExternalIdentifiersHtml(getUser1OrcidId());
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertNotNull(clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewExternalIdentifiersXml() throws Exception {
        ClientResponse clientResponse = t1OAuthClient.viewExternalIdentifiersXml(getUser1OrcidId());
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(getUser1OrcidId(), orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewExternalIdentifiersJson() throws Exception {
        ClientResponse clientResponse = t1OAuthClient.viewExternalIdentifiersJson(getUser1OrcidId());
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(getUser1OrcidId(), orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewFullDetailsHtml() throws Exception {
        ClientResponse clientResponse = t1OAuthClient.viewFullDetailsHtml(getUser1OrcidId());
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertNotNull(clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewFullDetailsXml() throws Exception {
        ClientResponse clientResponse = t1OAuthClient.viewFullDetailsXml(getUser1OrcidId());
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(getUser1OrcidId(), orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewFullDetailsJson() throws Exception {
        ClientResponse clientResponse = t1OAuthClient.viewFullDetailsJson(getUser1OrcidId());
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(getUser1OrcidId(), orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewWorksDetailsHtml() throws Exception {
        ClientResponse clientResponse = t1OAuthClient.viewWorksDetailsHtml(getUser1OrcidId());
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        assertNotNull(clientResponse.getEntity(String.class));
    }

    @Test
    public void testViewWorksDetailsXml() throws Exception {
        ClientResponse clientResponse = t1OAuthClient.viewWorksDetailsXml(getUser1OrcidId());
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(getUser1OrcidId(), orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewWorksDetailsJson() throws Exception {
        ClientResponse clientResponse = t1OAuthClient.viewWorksDetailsJson(getUser1OrcidId());
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertEquals(getUser1OrcidId(), orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
    }

}
