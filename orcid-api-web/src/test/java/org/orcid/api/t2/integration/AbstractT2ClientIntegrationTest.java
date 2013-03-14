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
package org.orcid.api.t2.integration;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.orcid.api.t2.T2OrcidApiService;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidWork;

import com.sun.jersey.api.client.ClientResponse;

public abstract class AbstractT2ClientIntegrationTest {

    @Resource
    private T2OrcidApiService<ClientResponse> t2Client;
    private Unmarshaller unmarshaller;

    @Before
    public void init() throws Exception {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
    }

    protected ClientResponse createFullOrcidXml() throws JAXBException {
        OrcidMessage message = getInternalFullOrcidMessage(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        ClientResponse response = t2Client.createProfileXML(message);
        return response;
    }

    protected OrcidMessage getInternalFullOrcidMessage(String xmlLoc) throws JAXBException {
        OrcidMessage emptyOrcid = (OrcidMessage) unmarshaller.unmarshal(T2OrcidApiClientIntegrationTest.class.getResourceAsStream(xmlLoc));
        emptyOrcid.getOrcidProfile().getOrcidBio().getContactDetails().addOrReplacePrimaryEmail(
                new Email("orcid.integration.test+" + System.currentTimeMillis() + "@semantico.com"));
        return emptyOrcid;
    }

    protected OrcidMessage getInternalSponsor() throws JAXBException {
        OrcidMessage emptyOrcid = (OrcidMessage) unmarshaller.unmarshal(T2OrcidApiClientIntegrationTest.class
                .getResourceAsStream(OrcidClientDataHelper.ORCID_INTERNAL_SPONSOR_XML));
        emptyOrcid.getOrcidProfile().getOrcidBio().getContactDetails().addOrReplacePrimaryEmail(
                new Email("orcid.integration.test.sponsor+" + System.currentTimeMillis() + "@semantico.com"));
        return emptyOrcid;
    }

    protected String extractOrcidFromResponseCreated(ClientResponse response) {
        String orcidFromLocation = response.getLocation().getPath();
        orcidFromLocation = orcidFromLocation.replace("/orcid-profile", "");
        orcidFromLocation = orcidFromLocation.substring(orcidFromLocation.lastIndexOf("/") + 1);
        return orcidFromLocation;
    }

    protected OrcidWork createWork(String title) {
        OrcidWork orcidWork = new OrcidWork();
        return orcidWork;
    }
}
