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
package org.orcid.integration.api.t2;

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
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.clientgroup.RedirectUris;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.beans.factory.InitializingBean;

import com.sun.jersey.api.client.ClientResponse;

public class OrcidClientDataHelper implements InitializingBean {

    public static final String ORCID_INTERNAL_NO_SPONSOR_XML = "/orcid-client/orcid-internal-no-sponsor-client.xml";
    public static final String ORCID_INTERNAL_SPONSOR_XML = "/orcid-client/orcid-internal-sponsor-message-client.xml";    

    @Resource
    private OrcidClientGroupManager orcidClientGroupManager;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    private Unmarshaller unmarshaller;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
    
    protected <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
        return TargetProxyHelper.getTargetObject(proxy, targetClass);
    }

    public void setOrcidClientGroupManager(OrcidClientGroupManager orcidClientGroupManager) {
        this.orcidClientGroupManager = orcidClientGroupManager;
    }

    public void setClientDetailsManager(ClientDetailsManager clientDetailsManager) {
        this.clientDetailsManager = clientDetailsManager;
    }

    public void setOrcidProfileManager(OrcidProfileManager orcidProfilerManager) {
        this.orcidProfileManager = orcidProfilerManager;
    }

    private void init() throws Exception {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
    }

    public OrcidMessage createFromXML(String xmlLoc) throws JAXBException {
        OrcidMessage emptyOrcid = (OrcidMessage) unmarshaller.unmarshal(OrcidClientDataHelper.class.getResourceAsStream(xmlLoc));
        emptyOrcid.getOrcidProfile().getOrcidBio().getContactDetails()
                .addOrReplacePrimaryEmail(new Email("orcid.integration.test+" + System.currentTimeMillis() + "@semantico.com"));
        return emptyOrcid;
    }

    public OrcidMessage createSponsor() throws JAXBException {
        OrcidMessage message = createFromXML(ORCID_INTERNAL_SPONSOR_XML);
        OrcidProfile orcidProfile = orcidProfileManager.createOrcidProfile(message.getOrcidProfile(), false, false);
        message.setOrcidProfile(orcidProfile);
        return message;
    }

    public String extractOrcidFromResponseCreated(ClientResponse response) {
        String orcidFromLocation = response.getLocation().getPath();
        orcidFromLocation = orcidFromLocation.replace("/orcid-profile", "");
        orcidFromLocation = orcidFromLocation.substring(orcidFromLocation.lastIndexOf("/") + 1);
        return orcidFromLocation;
    }

    public OrcidClientGroup getOrcidClientGroup() {
        OrcidClientGroup group = new OrcidClientGroup();
        group.setGroupName("Elsevier");
        group.setEmail("orcid-admin@elsevier.com");
        group.setType(MemberType.PREMIUM_INSTITUTION);
        OrcidClient client = new OrcidClient();
        client.setType(ClientType.PREMIUM_CREATOR);
        client.setDisplayName("Ecological Complexity");
        client.setShortDescription("An International Journal on Biocomplexity in the Environment and Theoretical Ecology");   
        client.setWebsite("http://www.journals.elsevier.com/ecological-complexity");
        RedirectUris rUris = new RedirectUris();
        RedirectUri rUri = new RedirectUri();
        rUri.setValue("http://www.journals.elsevier.com/ecological-complexity/orcid-callback");
        rUri.setType(RedirectUriType.DEFAULT);
        rUris.getRedirectUri().add(rUri);
        client.setRedirectUris(rUris);        
        group.getOrcidClient().add(client);        
        return group;
    }

    public OrcidClientGroup createAndPersistClientGroupSingle() {
        OrcidClientGroup unmarshalledGroup = getOrcidClientGroup();
        unmarshalledGroup.setEmail("orcid.integration.test.client+" + System.currentTimeMillis() + "@semantico.com");
        OrcidClientGroup createdGroup = orcidClientGroupManager.createOrUpdateOrcidClientGroup(unmarshalledGroup);
        return createdGroup;
    }

    public void assertClientCredentialsForTesting(OrcidClientGroup orcidClientGroup) {
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
        ClientDetailsEntity complexityEntity = clientDetailsManager.findByClientId(complexityClient.getClientId());
        Set<String> clientScopeTypes = complexityEntity.getScope();
        assertNotNull(clientScopeTypes);
        assertTrue(clientScopeTypes.contains("/orcid-bio/update"));
        assertTrue(clientScopeTypes.contains("/orcid-profile/create"));
        assertTrue(clientScopeTypes.contains("/authenticate"));

    }

    public OrcidWork createWork(String theTitle) {
        OrcidWork orcidWork = new OrcidWork();
        orcidWork.setWorkType(WorkType.JOURNAL_ARTICLE);
        Title title = new Title(theTitle);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(title);
        orcidWork.setWorkTitle(workTitle);
        orcidWork.setVisibility(Visibility.PUBLIC);
        WorkExternalIdentifiers workExternalIdentifiers = new WorkExternalIdentifiers();
        orcidWork.setWorkExternalIdentifiers(workExternalIdentifiers);
        WorkExternalIdentifier workExternalIdentifier = new WorkExternalIdentifier();
        workExternalIdentifiers.getWorkExternalIdentifier().add(workExternalIdentifier);
        workExternalIdentifier.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        workExternalIdentifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId("1234/abc123"));
        return orcidWork;
    }    

    public void deleteGroupOrcid(String orcid) {
        if (!StringUtils.isBlank(orcid)) {
            orcidClientGroupManager.removeOrcidClientGroup(orcid);
        }
    }

    public void deleteOrcidProfile(String orcid) throws Exception {
        if (!StringUtils.isBlank(orcid)) {           
            orcidProfileManager.deleteProfile(orcid);
        }
    }

}
