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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.beans.factory.InitializingBean;

import com.sun.jersey.api.client.ClientResponse;

public class OrcidClientDataHelper implements InitializingBean {

    public static final String ORCID_INTERNAL_NO_SPONSOR_XML = "/orcid-client/orcid-internal-no-sponsor-client.xml";
    public static final String ORCID_INTERNAL_SPONSOR_XML = "/orcid-client/orcid-internal-sponsor-message-client.xml";
    public static final String CLIENT_GROUP_SINGLE_FOR_OAUTH = "/orcid-client/orcid-client-group-single.xml";

    @Resource
    private OrcidClientGroupManager orcidClientGroupManager;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    private Unmarshaller unmarshaller;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    @SuppressWarnings( { "unchecked" })
    protected <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
        return TargetProxyHelper.getTargetObject(proxy, targetClass);
    }

    public void setOrcidClientGroupManager(OrcidClientGroupManager orcidClientGroupManager) {
        this.orcidClientGroupManager = orcidClientGroupManager;
    }

    private void init() throws Exception {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
    }

    protected OrcidMessage createFromXML(String xmlLoc) throws JAXBException {
        OrcidMessage emptyOrcid = (OrcidMessage) unmarshaller.unmarshal(OrcidClientDataHelper.class.getResourceAsStream(xmlLoc));
        emptyOrcid.getOrcidProfile().getOrcidBio().getContactDetails().addOrReplacePrimaryEmail(
                new Email("orcid.integration.test+" + System.currentTimeMillis() + "@semantico.com"));
        return emptyOrcid;
    }

    protected OrcidMessage createSponsor() throws JAXBException {
        OrcidMessage message = createFromXML(ORCID_INTERNAL_SPONSOR_XML);
        OrcidProfile orcidProfile = orcidProfileManager.createOrcidProfile(message.getOrcidProfile());
        message.setOrcidProfile(orcidProfile);
        return message;
    }

    protected String extractOrcidFromResponseCreated(ClientResponse response) {
        String orcidFromLocation = response.getLocation().getPath();
        orcidFromLocation = orcidFromLocation.replace("/orcid-profile", "");
        orcidFromLocation = orcidFromLocation.substring(orcidFromLocation.lastIndexOf("/") + 1);
        return orcidFromLocation;
    }

    protected OrcidClientGroup getOrcidClientGroup() {
        OrcidClientGroup group = OrcidClientGroup.unmarshall(getClass().getResourceAsStream(CLIENT_GROUP_SINGLE_FOR_OAUTH));
        return group;
    }

    protected OrcidClientGroup createAndPersistClientGroupSingle() {
        OrcidClientGroup unmarshalledGroup = getOrcidClientGroup();
        unmarshalledGroup.setEmail("orcid.integration.test.client+" + System.currentTimeMillis() + "@semantico.com");
        OrcidClientGroup createdGroup = orcidClientGroupManager.createOrUpdateOrcidClientGroup(unmarshalledGroup, ClientType.PREMIUM_CREATOR);
        return createdGroup;
    }

    protected void assertClientCredentialsForTesting(OrcidClientGroup orcidClientGroup) {
        assertNotNull(orcidClientGroup);

        assertEquals("Elsevier", orcidClientGroup.getGroupName());
        assertEquals("orcid-admin@elsevier.com", orcidClientGroup.getEmail());
        assertNotNull(orcidClientGroup.getGroupOrcid());
        List<OrcidClient> createdClients = orcidClientGroup.getOrcidClient();
        assertNotNull(createdClients);
        assertEquals(1, createdClients.size());

        OrcidClient complexityClient = createdClients.get(0);
        assertNotNull(complexityClient);
        assertEquals("http://www.journals.elsevier.com/ecological-complexity", complexityClient.getWebsite());
        assertEquals("An International Journal on Biocomplexity in the Environment and Theoretical Ecology", complexityClient.getShortDescription());
        List<RedirectUri> createdRedirectUris = complexityClient.getRedirectUris().getRedirectUri();
        assertNotNull(createdRedirectUris);
        assertEquals(1, createdRedirectUris.size());
        assertEquals("http://www.journals.elsevier.com/ecological-complexity/orcid-callback", createdRedirectUris.get(0));

        // Look up client details directly to check scopes
        ClientDetailsEntity complexityEntity = clientDetailsDao.find(complexityClient.getClientId());
        Set<String> clientScopeTypes = complexityEntity.getScope();
        assertNotNull(clientScopeTypes);
        assertTrue(clientScopeTypes.contains("/orcid-bio/update"));
        assertTrue(clientScopeTypes.contains("/orcid-profile/create"));
        assertTrue(clientScopeTypes.contains("/authenticate"));

    }

    protected OrcidWork createWork(String title) {
        OrcidWork orcidWork = new OrcidWork();
        orcidWork.setVisibility(Visibility.PUBLIC);
        return orcidWork;
    }

    protected void deleteClientId(String clientId) {
        if (!StringUtils.isBlank(clientId)) {
            clientDetailsDao.remove(clientId);
        }
    }

    protected void deleteGroupOrcid(String orcid) {
        if (!StringUtils.isBlank(orcid)) {
            orcidClientGroupManager.removeOrcidClientGroup(orcid);
        }
    }

    protected void deleteOrcidProfile(String orcid) throws Exception {
        if (!StringUtils.isBlank(orcid)) {
            orcidProfileManager.deleteProfile(orcid);
        }
    }

}
