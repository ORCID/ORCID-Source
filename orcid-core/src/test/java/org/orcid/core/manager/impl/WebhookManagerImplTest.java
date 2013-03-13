package org.orcid.core.manager.impl;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.WebhookManager;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class WebhookManagerImplTest {

	@Resource
    private WebhookManager webhookManager;

	private ClientDetailsEntity clientDetails;
	
    @Before
    public void init() {
        assertNotNull(webhookManager);
        ProfileEntity profile = new ProfileEntity();
        profile.setId("0000-0000-0000-0001");
        clientDetails = new ClientDetailsEntity();
        clientDetails.setProfileEntity(profile);
        clientDetails.setId("123456789");
        
        assertNotNull(clientDetails.getProfileEntity());
        assertNotNull(clientDetails.getId());
    }
    
    @Test
    public void testValidUriOnWebhook(){
    	WebhookEntity webhook = new WebhookEntity();
    	webhook.setClientDetails(clientDetails);
    	webhook.setUri("http://qa-1.orcid.org");
    	webhookManager.processWebhook(webhook);
    	assertEquals(webhook.getFailedAttemptCount(), 0);
    }
	
    @Test
    public void testUnexsistingUriOnWebhook(){
    	WebhookEntity webhook = new WebhookEntity();
    	webhook.setClientDetails(clientDetails);
    	webhook.setUri("http://unexisting.orcid.com");
    	webhookManager.processWebhook(webhook);
    	assertEquals(webhook.getFailedAttemptCount(), 1);
    	for(int i  = 0; i < 3; i++){
    		webhookManager.processWebhook(webhook);
    	}
    	assertEquals(webhook.getFailedAttemptCount(), 4);
    }
    
    @Test
    public void testInvalidUriOnWebhook(){
    	WebhookEntity webhook = new WebhookEntity();
    	webhook.setClientDetails(clientDetails);
    	webhook.setUri("http123://qa-1.orcid.org");
    	webhookManager.processWebhook(webhook);
    	assertEquals(webhook.getFailedAttemptCount(), 1);
    	for(int i  = 0; i < 3; i++){
    		webhookManager.processWebhook(webhook);
    	}
    	assertEquals(webhook.getFailedAttemptCount(), 4);
    }
    
    @Test
    public void testFailAttemptCounterReset(){
    	WebhookEntity webhook = new WebhookEntity();
    	webhook.setClientDetails(clientDetails);
    	webhook.setUri("http123://qa-1.orcid.org");
    	webhookManager.processWebhook(webhook);
    	assertEquals(webhook.getFailedAttemptCount(), 1);
    	
    	webhook.setUri("http://unexisting.orcid.com");    	
    	webhookManager.processWebhook(webhook);
    	assertEquals(webhook.getFailedAttemptCount(), 2);
    	
    	webhook.setUri("http://qa-1.orcid.org");    	
    	webhookManager.processWebhook(webhook);
    	assertEquals(webhook.getFailedAttemptCount(), 0);
    }
}
