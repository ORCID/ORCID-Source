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
package org.orcid.integration.blackbox.api.v2.rc2;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class GroupIdRecordTest extends BlackBoxBaseRC2 {

    private static final List<String> GROUP_IDS = Arrays.asList("orcid-generated:this-is-a-hippen-separated-id", 
            "orcid-generated:this.is.a.dot.separated.id", 
            "orcid-generated:this'is'a'apostrophe'separated'id", 
            "orcid-generated:this_is_a_dash_separated_id", 
            "orcid-generated:this-is-a-dash-separated-id",
            "orcid-generated:(this)(is)(a)(test)",
            "orcid-generated:test.", 
            "orcid-generated:test,", 
            "orcid-generated:test-", 
            "orcid-generated:test_");
    
    private WebDriver webDriver;
    
    @Before
    public void before() {
        webDriver = new FirefoxDriver();        
        webDriver.manage().window().maximize();
    }

    @After
    public void after() {
        if(webDriver != null) {
            webDriver.quit();
        }
    }
    
    @Test
    public void testGetGroupIdRecordsWithSeveralFormats() throws JSONException, InterruptedException, URISyntaxException, UnsupportedEncodingException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);
        
        for(String groupId : GROUP_IDS) {            
            GroupIdRecord g1 = new GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(groupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV2ApiClient.createGroupIdRecord(g1, token);
            String r1LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v2.0_rc2/group-id-record/", "");
            g1.setPutCode(Long.valueOf(r1LocationPutCode));
            
            webDriver.get(getWebBaseUrl() + "/public/group/" + g1.getPutCode());
            String pageContent = webDriver.getPageSource();
            assertNotNull(pageContent);
            assertTrue("Missing " + groupId, pageContent.contains(groupId.replace("\"", "\\\"")));
            
            memberV2ApiClient.deleteGroupIdRecord(g1.getPutCode(), token);
        }
        
        
    }
}
