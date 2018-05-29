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
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.integration.blackbox.api.v2.release.MemberV2ApiClientImpl;
import org.orcid.integration.blackbox.api.v2_1.release.MemberV2_1ApiClientImpl;
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

    @Resource(name = "memberV2_1ApiClient")
    private MemberV2_1ApiClientImpl memberV2_1ApiClient_release;
    @Resource(name = "publicV2_1ApiClient")
    private PublicV2ApiClientImpl publicV2_1ApiClient_release;
    
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
    
    /**
     * --------- -- -- -- V2.1 -- -- -- ---------
     * 
     */
    @Test
    public void testGetGroupIdRecordsWithSeveralFormats_v2_1() throws JSONException, InterruptedException, URISyntaxException, UnsupportedEncodingException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);
        
        for(String groupId : VALID_GROUP_IDS) {            
            org.orcid.jaxb.model.groupid_v2.GroupIdRecord g1 = new org.orcid.jaxb.model.groupid_v2.GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(groupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV2_1ApiClient_release.createGroupIdRecord(g1, token);
            String r1LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v2.1/group-id-record/", "");
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
            ClientResponse r1 = memberV2_1ApiClient_release.createGroupIdRecord(g1, token);
            assertNotNull(r1);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r1.getStatus());
        }        
    }
    
    /**
     * --------- -- -- -- ALL -- -- -- ---------
     * 
     */
    @Test
    public void testCreateAndView() throws JSONException, InterruptedException, URISyntaxException, UnsupportedEncodingException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);
        String groupId = "orcid-generated:test#" + System.currentTimeMillis();
        org.orcid.jaxb.model.groupid_v2.GroupIdRecord g1 = new org.orcid.jaxb.model.groupid_v2.GroupIdRecord();        
        g1.setDescription("Description");
        g1.setGroupId(groupId);
        g1.setName(groupId);
        g1.setType("publisher");
        //Create one
        ClientResponse r1 = memberV2ApiClient_release.createGroupIdRecord(g1, token);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), r1.getStatus());
        String r1LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v2.0/group-id-record/", "");
        Long putCode = Long.valueOf(r1LocationPutCode);
        
        //View it with RC2
        ClientResponse rc2Result = memberV2ApiClient_rc2.getGroupIdRecord(putCode, token);
        assertEquals(Response.Status.OK.getStatusCode(), rc2Result.getStatus());
        org.orcid.jaxb.model.groupid_rc2.GroupIdRecord rc2 = rc2Result.getEntity(org.orcid.jaxb.model.groupid_rc2.GroupIdRecord.class);
        assertEquals(putCode, rc2.getPutCode());
        assertEquals("publisher", rc2.getType());
        assertEquals("Description", rc2.getDescription());
        assertEquals(groupId, rc2.getGroupId());
        assertEquals(groupId, rc2.getName());
        assertNotNull(rc2.getSource());
        assertEquals(this.getClient1ClientId(), rc2.retrieveSourcePath());
        
        //View it with RC3
        ClientResponse rc3Result = memberV2ApiClient_rc3.getGroupIdRecord(putCode, token);
        assertEquals(Response.Status.OK.getStatusCode(), rc3Result.getStatus());
        org.orcid.jaxb.model.groupid_rc3.GroupIdRecord rc3 = rc3Result.getEntity(org.orcid.jaxb.model.groupid_rc3.GroupIdRecord.class);
        assertEquals(putCode, rc3.getPutCode());
        assertEquals("publisher", rc3.getType());
        assertEquals("Description", rc3.getDescription());
        assertEquals(groupId, rc3.getGroupId());
        assertEquals(groupId, rc3.getName());
        assertNotNull(rc3.getSource());
        assertEquals(this.getClient1ClientId(), rc3.retrieveSourcePath());
        
        //View it with RC4
        ClientResponse rc4Result = memberV2ApiClient_rc4.getGroupIdRecord(putCode, token);
        assertEquals(Response.Status.OK.getStatusCode(), rc4Result.getStatus());
        org.orcid.jaxb.model.groupid_rc4.GroupIdRecord rc4 = rc4Result.getEntity(org.orcid.jaxb.model.groupid_rc4.GroupIdRecord.class);
        assertEquals(putCode, rc4.getPutCode());
        assertEquals("publisher", rc4.getType());
        assertEquals("Description", rc4.getDescription());
        assertEquals(groupId, rc4.getGroupId());
        assertEquals(groupId, rc4.getName());
        assertNotNull(rc4.getSource());
        assertEquals(this.getClient1ClientId(), rc4.retrieveSourcePath());
        
        //View it with release
        ClientResponse v2Result = memberV2ApiClient_release.getGroupIdRecord(putCode, token);
        assertEquals(Response.Status.OK.getStatusCode(), v2Result.getStatus());
        org.orcid.jaxb.model.groupid_v2.GroupIdRecord v2 = v2Result.getEntity(org.orcid.jaxb.model.groupid_v2.GroupIdRecord.class);
        assertEquals(putCode, v2.getPutCode());
        assertEquals("publisher", v2.getType());
        assertEquals("Description", v2.getDescription());
        assertEquals(groupId, v2.getGroupId());
        assertEquals(groupId, v2.getName());
        assertNotNull(v2.getSource());
        assertEquals(this.getClient1ClientId(), v2.retrieveSourcePath());
        
        //View it with V2.1
        ClientResponse v2_1Result = memberV2_1ApiClient_release.getGroupIdRecord(putCode, token);
        assertEquals(Response.Status.OK.getStatusCode(), v2_1Result.getStatus());
        org.orcid.jaxb.model.groupid_v2.GroupIdRecord v2_1 = v2_1Result.getEntity(org.orcid.jaxb.model.groupid_v2.GroupIdRecord.class);
        assertEquals(putCode, v2_1.getPutCode());
        assertEquals("publisher", v2_1.getType());
        assertEquals("Description", v2_1.getDescription());
        assertEquals(groupId, v2_1.getGroupId());
        assertEquals(groupId, v2_1.getName());
        assertNotNull(v2_1.getSource());
        assertEquals(this.getClient1ClientId(), v2_1.retrieveSourcePath());
    }   
}
