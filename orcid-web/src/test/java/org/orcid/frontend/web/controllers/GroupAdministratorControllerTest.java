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
package org.orcid.frontend.web.controllers;

/**
 * Copyright 2012-2013 ORCID
 * 
 * @author Angel Montenegro (amontenegro) Date: 29/08/2013
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class GroupAdministratorControllerTest extends BaseControllerTest {

    @Resource
    GroupAdministratorController controller;

    @Resource
    private ProfileDao profileDao;

    @Resource
    protected OrcidProfileManager orcidProfileManager;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @Before
    public void init() {
        assertNotNull(controller);
        assertNotNull(profileDao);
    }
    
    @Test
    @Transactional("transactionManager")
    @Rollback(true)
    public void emptyClientTest(){
        Client client = controller.getClient();       
        client = controller.createClient(client);
        assertNotNull(client);
        List<String> errors = client.getErrors(); 
        assertEquals(3, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.display_name.empty")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.website.empty")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.short_description.empty")));
    }
    
    @Test
    @Transactional("transactionManager")
    @Rollback(true)
    public void invalidClientTest() {
        //Test invalid fields
        Client client = controller.getClient();   
        String _151chars = new String();
        for(int i = 0; i < 151; i++) _151chars += "a";
        client.setDisplayName(Text.valueOf(_151chars));
        client.setShortDescription(Text.valueOf("description"));
        client.setWebsite(Text.valueOf("http://site.com"));        
        client = controller.createClient(client);
        List<String> errors = client.getErrors(); 
        assertEquals(1, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.display_name.150")));             
        
        //Test invalid redirect uris
        client = controller.getClient();
        client.setDisplayName(Text.valueOf("Name"));
        client.setShortDescription(Text.valueOf("Description"));        
        client.setWebsite(Text.valueOf("mysite.com"));
        
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri one = new RedirectUri();
        one.setType(Text.valueOf("default"));
        one.setValue(new Text());
        redirectUris.add(one);
        client.setRedirectUris(redirectUris);
        client = controller.createClient(client);
        errors = client.getErrors(); 
        assertEquals(1, errors.size());
        assertTrue(errors.contains(controller.getMessage("common.invalid_url")));        
        
        RedirectUri two = new RedirectUri();
        two.setType(Text.valueOf("grant-read-wizard"));
        two.setValue(new Text());
        redirectUris = new ArrayList<RedirectUri>();
        redirectUris.add(two);
        client.setRedirectUris(redirectUris);
        
        client = controller.createClient(client);
        errors = client.getErrors(); 
        assertEquals(2, errors.size());
        assertTrue(errors.contains(controller.getMessage("common.invalid_url")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.empty_scopes")));
    }
}

















