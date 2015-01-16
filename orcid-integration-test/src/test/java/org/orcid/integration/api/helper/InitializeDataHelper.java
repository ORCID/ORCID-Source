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
package org.orcid.integration.api.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.frontend.web.controllers.AdminController;
import org.orcid.frontend.web.controllers.ManageMembersController;
import org.orcid.frontend.web.controllers.RegistrationController;
import org.orcid.jaxb.model.clientgroup.GroupType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.Group;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * 
 * @author Angel Montenegro
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
public class InitializeDataHelper {

    @Resource
    private ManageMembersController manageMembers;
    
    @Resource
    private OrcidClientGroupManager orcidClientGroupManager;
    
    @Resource
    private AdminController adminController;
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private OrcidProfileManager orcidProfileManager;
    
    @Resource 
    private ClientDetailsDao clientDetailsDao;
    
    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    protected String webBaseUrl;
    
    public void deleteProfile(String orcid) throws Exception {
        adminController.deactivateOrcidAccount(orcid);
        profileDao.removeProfile(orcid);
    }
    public void deleteClient(String clientId) throws Exception {
        clientDetailsDao.removeClient(clientId);
    }
    
    public Group createMember(GroupType type) throws Exception {
        String name = type.value() + System.currentTimeMillis() + "@orcid-integration-test.com";
        Group group = new Group();        
        group.setEmail(Text.valueOf(name));
        group.setGroupName(Text.valueOf(name));
        group.setType(Text.valueOf(type.value()));        
        group = manageMembers.createMember(group);
        assertEquals(0, group.getErrors().size());
        assertFalse(PojoUtil.isEmpty(group.getGroupOrcid()));
        return group;
    }
    
    public OrcidClient createClient(String groupOrcid) throws Exception {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("client_for_" + groupOrcid));
        client.setShortDescription(Text.valueOf("Description test"));
        client.setWebsite(Text.valueOf("www." + groupOrcid + ".com"));
        RedirectUri rUri = new RedirectUri();
        rUri.setValue(Text.valueOf(getRedirectUri()));
        List<RedirectUri> rUris = new ArrayList<RedirectUri>();                
        rUris.add(rUri);
        client.setRedirectUris(rUris);        

        OrcidClient orcidClient = client.toOrcidClient();
        orcidClient = orcidClientGroupManager.createAndPersistClientProfile(groupOrcid, orcidClient);
        
        assertNotNull(orcidClient);        
        assertFalse(PojoUtil.isEmpty(orcidClient.getClientId()));
        return orcidClient;
    }
    
    public OrcidProfile createProfile(String email, String password) throws Exception {
        Text emailText = Text.valueOf(email);
        Text passwordText = Text.valueOf(password);
        Registration registration = new Registration();
        registration.setGivenNames(emailText);
        registration.setEmail(emailText);
        registration.setEmailConfirm(emailText);
        registration.setPassword(passwordText);
        registration.setPasswordConfirm(passwordText);
        OrcidProfile orcidProfile = RegistrationController.toProfile(registration);
        orcidProfile = orcidProfileManager.createOrcidProfile(orcidProfile);
        return orcidProfile;
    }
    
    protected String getRedirectUri() {
        return webBaseUrl + "/oauth/playground";
    }
}
