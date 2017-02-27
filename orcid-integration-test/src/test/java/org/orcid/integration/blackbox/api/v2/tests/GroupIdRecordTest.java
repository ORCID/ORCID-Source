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
package org.orcid.integration.blackbox.api.v2.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.orcid.core.utils.JsonUtils;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.integration.blackbox.api.v2.release.MemberV2ApiClientImpl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class GroupIdRecordTest extends BlackBoxBaseV2Release {

    private static final List<String> VALID_GROUP_IDS = Arrays.asList( 
            "orcid-generated:bb_test:this.is.a.test.", 
            "orcid-generated:bb_test:this'is'a'test'", 
            "orcid-generated:bb_test:this_is_a_test_", 
            "orcid-generated:bb_test:this-is-a-test-",
            "orcid-generated:bb_test:(this)(is)(a)(test)",
            "orcid-generated:bb_test:this^is^a^test^",
            "orcid-generated:bb_test:this~is~a~test~",
            "orcid-generated:bb_test:this:is:a:test:",
            "orcid-generated:bb_test:this/is/a/test/",
            "orcid-generated:bb_test:this?is?a?test?",
            "orcid-generated:bb_test:this#is#a#test#",
            "orcid-generated:bb_test:this[is]a[test]",
            "orcid-generated:bb_test:this@is@a@test@",
            "orcid-generated:bb_test:this!is!a!test!",
            "orcid-generated:bb_test:this$is$a$test$",
            "orcid-generated:bb_test:this&is&a&test&",
            "orcid-generated:bb_test:this*is*a*test*",
            "orcid-generated:bb_test:this+is+a+test+",
            "orcid-generated:bb_test:this,is,a,test,");
    
    public static final List<String> INVALID_GROUP_IDS = Arrays.asList(
            "orcid-generated:this{is}a{test}",
            "orcid-generated:this\\is\\a\\test\\",
            "orcid-generated:this\"is\"a\"test\"",
            "orcid-generated:this<is>a<test>",
            "orcid-generated:this¢is¢a¢test¢");

    ArrayList<Long> putsToDelete = new ArrayList<Long>();
    
    @Resource(name = "memberV2ApiClient_rc2")
    private org.orcid.integration.blackbox.api.v2.rc2.MemberV2ApiClientImpl memberV2ApiClient_rc2;
    
    @Resource(name = "memberV2ApiClient_rc3")
    private org.orcid.integration.blackbox.api.v2.rc3.MemberV2ApiClientImpl memberV2ApiClient_rc3;
    
    @Resource(name = "memberV2ApiClient_rc4")
    private org.orcid.integration.blackbox.api.v2.rc4.MemberV2ApiClientImpl memberV2ApiClient_rc4;
    
    @Resource(name = "memberV2ApiClient")
    private MemberV2ApiClientImpl memberV2ApiClient_release;    
    
    @Before
    public void cleanUpOldTest() throws JSONException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);

        // clean up group IDs before test
        int page = 1;
        org.orcid.jaxb.model.groupid_v2.GroupIdRecords groupsContainer = memberV2ApiClient_release.getGroupIdRecords(100, page, token).getEntity(org.orcid.jaxb.model.groupid_v2.GroupIdRecords.class);
        
        while (groupsContainer.getTotal() > 0) {
            for (org.orcid.jaxb.model.groupid_v2.GroupIdRecord groupIdRecord : groupsContainer.getGroupIdRecord())
                if (groupIdRecord.getGroupId().startsWith("orcid-generated:bb_test:"))
                    putsToDelete.add(groupIdRecord.getPutCode());
            page++;
            groupsContainer = memberV2ApiClient_release.getGroupIdRecords(100, page, token).getEntity(org.orcid.jaxb.model.groupid_v2.GroupIdRecords.class);
        }
        for (Long putCode : putsToDelete) {
            memberV2ApiClient_release.deleteGroupIdRecord(putCode, token);
        }
        putsToDelete.clear();
    }
    
    @After
    public void cleanUpGroupIds() throws JSONException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);

        for (Long putCode : putsToDelete) {
            memberV2ApiClient_release.deleteGroupIdRecord(putCode, token);
        }        
    }

    /**
     * --------- -- -- -- RC2 -- -- -- ---------
     * 
     */
    @Test
    public void testGetGroupIdRecordsWithSeveralFormats_rc2() throws JSONException, InterruptedException, URISyntaxException, UnsupportedEncodingException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);
        
        for(String groupId : VALID_GROUP_IDS) {            
            org.orcid.jaxb.model.groupid_rc2.GroupIdRecord g1 = new org.orcid.jaxb.model.groupid_rc2.GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(groupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV2ApiClient_rc2.createGroupIdRecord(g1, token);
            String r1LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v2.0_rc2/group-id-record/", "");
            g1.setPutCode(Long.valueOf(r1LocationPutCode));
            
            webDriver.get(getWebBaseUrl() + "/public/group/" + g1.getPutCode());
            WebElement preElement = webDriver.findElement(By.tagName("pre"));
            String groupElementString = preElement.getText();
            assertFalse(PojoUtil.isEmpty(groupElementString));
            org.orcid.jaxb.model.groupid_rc2.GroupIdRecord groupFromWebPage = JsonUtils.readObjectFromJsonString(groupElementString, org.orcid.jaxb.model.groupid_rc2.GroupIdRecord.class);
            assertNotNull(groupFromWebPage);
            assertEquals("Missing " + groupId, groupId, groupFromWebPage.getGroupId());
            
            putsToDelete.add(g1.getPutCode());
        }
        
        for(String invdalidGroupId : INVALID_GROUP_IDS) {            
            org.orcid.jaxb.model.groupid_rc2.GroupIdRecord g1 = new org.orcid.jaxb.model.groupid_rc2.GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(invdalidGroupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV2ApiClient_rc2.createGroupIdRecord(g1, token);
            assertNotNull(r1);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r1.getStatus());
        }        
    }
    
    /**
     * --------- -- -- -- RC3 -- -- -- ---------
     * 
     */
    @Test
    public void testGetGroupIdRecordsWithSeveralFormats_rc3() throws JSONException, InterruptedException, URISyntaxException, UnsupportedEncodingException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);
        
        for(String groupId : VALID_GROUP_IDS) {            
            org.orcid.jaxb.model.groupid_rc3.GroupIdRecord g1 = new org.orcid.jaxb.model.groupid_rc3.GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(groupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV2ApiClient_rc3.createGroupIdRecord(g1, token);
            String r1LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v2.0_rc3/group-id-record/", "");
            g1.setPutCode(Long.valueOf(r1LocationPutCode));
            
            webDriver.get(getWebBaseUrl() + "/public/group/" + g1.getPutCode());
            WebElement preElement = webDriver.findElement(By.tagName("pre"));
            String groupElementString = preElement.getText();
            assertFalse(PojoUtil.isEmpty(groupElementString));
            org.orcid.jaxb.model.groupid_rc3.GroupIdRecord groupFromWebPage = JsonUtils.readObjectFromJsonString(groupElementString, org.orcid.jaxb.model.groupid_rc3.GroupIdRecord.class);
            assertNotNull(groupFromWebPage);
            assertEquals("Missing " + groupId, groupId, groupFromWebPage.getGroupId());
            
            putsToDelete.add(g1.getPutCode());
        }
        
        for(String invdalidGroupId : INVALID_GROUP_IDS) {            
            org.orcid.jaxb.model.groupid_rc3.GroupIdRecord g1 = new org.orcid.jaxb.model.groupid_rc3.GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(invdalidGroupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV2ApiClient_rc3.createGroupIdRecord(g1, token);
            assertNotNull(r1);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r1.getStatus());
        }        
    }
    
    @Test
    public void testGetGroupByName_rc3() throws JSONException{
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);
        org.orcid.jaxb.model.groupid_rc3.GroupIdRecord g1 = new org.orcid.jaxb.model.groupid_rc3.GroupIdRecord();
        g1.setDescription("Description");
        g1.setGroupId("orcid-generated:1234_rc3");
        g1.setName("Group1234_rc3");
        g1.setType("publisher");
        
        ClientResponse checkIfPresent = memberV2ApiClient_rc3.getGroupIdByName("Group1234_rc3",token);
        if (checkIfPresent.getStatus() == Response.Status.OK.getStatusCode()){            
            memberV2ApiClient_rc3.deleteGroupIdRecord(checkIfPresent.getEntity(org.orcid.jaxb.model.groupid_rc3.GroupIdRecord.class).getPutCode(), token);
        }
        
        ClientResponse r1 = memberV2ApiClient_rc3.createGroupIdRecord(g1, token);        
        ClientResponse r2 = memberV2ApiClient_rc3.getGroupIdByName("Group1234_rc3",token);
        String r2LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v2.0_rc3/group-id-record/", "");
        org.orcid.jaxb.model.groupid_rc3.GroupIdRecord record = r2.getEntity(org.orcid.jaxb.model.groupid_rc3.GroupIdRecord.class);
        assertEquals(r2LocationPutCode, String.valueOf(record.getPutCode()));
        putsToDelete.add(record.getPutCode());

        ClientResponse r3 = memberV2ApiClient_rc3.getGroupIdByName("GroupXXXX",token);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), r3.getStatus());
    }
    
    /**
     * --------- -- -- -- RC4 -- -- -- ---------
     * 
     */
    @Test
    public void testGetGroupIdRecordsWithSeveralFormats_rc4() throws JSONException, InterruptedException, URISyntaxException, UnsupportedEncodingException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);
        
        for(String groupId : VALID_GROUP_IDS) {            
            org.orcid.jaxb.model.groupid_rc4.GroupIdRecord g1 = new org.orcid.jaxb.model.groupid_rc4.GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(groupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV2ApiClient_rc4.createGroupIdRecord(g1, token);
            String r1LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v2.0_rc4/group-id-record/", "");
            g1.setPutCode(Long.valueOf(r1LocationPutCode));
            
            webDriver.get(getWebBaseUrl() + "/public/group/" + g1.getPutCode());
            WebElement preElement = webDriver.findElement(By.tagName("pre"));
            String groupElementString = preElement.getText();
            assertFalse(PojoUtil.isEmpty(groupElementString));
            org.orcid.jaxb.model.groupid_rc4.GroupIdRecord groupFromWebPage = JsonUtils.readObjectFromJsonString(groupElementString, org.orcid.jaxb.model.groupid_rc4.GroupIdRecord.class);
            assertNotNull(groupFromWebPage);
            assertEquals("Missing " + groupId, groupId, groupFromWebPage.getGroupId());
            
            putsToDelete.add(g1.getPutCode());
        }
        
        for(String invdalidGroupId : INVALID_GROUP_IDS) {            
            org.orcid.jaxb.model.groupid_rc4.GroupIdRecord g1 = new org.orcid.jaxb.model.groupid_rc4.GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(invdalidGroupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV2ApiClient_rc4.createGroupIdRecord(g1, token);
            assertNotNull(r1);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r1.getStatus());
        }        
    }    
        
    /**
     * --------- -- -- -- Release -- -- -- ---------
     * 
     */
    @Test
    public void testGetGroupIdRecordsWithSeveralFormats_v2() throws JSONException, InterruptedException, URISyntaxException, UnsupportedEncodingException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);
        
        for(String groupId : VALID_GROUP_IDS) {            
            org.orcid.jaxb.model.groupid_v2.GroupIdRecord g1 = new org.orcid.jaxb.model.groupid_v2.GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(groupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV2ApiClient_release.createGroupIdRecord(g1, token);
            String r1LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v2.0/group-id-record/", "");
            g1.setPutCode(Long.valueOf(r1LocationPutCode));
            
            webDriver.get(getWebBaseUrl() + "/public/group/" + g1.getPutCode());
            WebElement preElement = webDriver.findElement(By.tagName("pre"));
            String groupElementString = preElement.getText();
            assertFalse(PojoUtil.isEmpty(groupElementString));
            org.orcid.jaxb.model.groupid_v2.GroupIdRecord groupFromWebPage = JsonUtils.readObjectFromJsonString(groupElementString, org.orcid.jaxb.model.groupid_v2.GroupIdRecord.class);
            assertNotNull(groupFromWebPage);
            assertEquals("Missing " + groupId, groupId, groupFromWebPage.getGroupId());
            
            putsToDelete.add(g1.getPutCode());
        }
        
        for(String invdalidGroupId : INVALID_GROUP_IDS) {            
            org.orcid.jaxb.model.groupid_v2.GroupIdRecord g1 = new org.orcid.jaxb.model.groupid_v2.GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(invdalidGroupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV2ApiClient_release.createGroupIdRecord(g1, token);
            assertNotNull(r1);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r1.getStatus());
        }        
    }         
}
