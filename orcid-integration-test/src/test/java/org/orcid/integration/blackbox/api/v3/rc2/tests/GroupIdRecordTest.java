package org.orcid.integration.blackbox.api.v3.rc2.tests;

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
import org.orcid.integration.api.pub.PublicV3ApiClientImpl;
import org.orcid.integration.blackbox.api.v3.rc2.BlackBoxBaseV3_0_rc2;
import org.orcid.integration.blackbox.api.v3.rc2.MemberV3Rc2ApiClientImpl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class GroupIdRecordTest extends BlackBoxBaseV3_0_rc2 {

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
    
    @Resource(name = "memberV3_0_rc2ApiClient")
    private MemberV3Rc2ApiClientImpl memberV3Rc2ApiClient;
    
    @Resource(name = "publicV3_0_rc2ApiClient")
    private PublicV3ApiClientImpl publicV3ApiClientImpl;
    
    @Before
    public void cleanUpOldTest() throws JSONException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);

        // clean up group IDs before test
        int page = 1;
        org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecords groupsContainer = memberV3Rc2ApiClient.getGroupIdRecords(100, page, token).getEntity(org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecords.class);
        
        while (groupsContainer.getTotal() > 0) {
            for (org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord groupIdRecord : groupsContainer.getGroupIdRecord())
                if (groupIdRecord.getGroupId().startsWith("orcid-generated:bb_test:"))
                    putsToDelete.add(groupIdRecord.getPutCode());
            page++;
            groupsContainer = memberV3Rc2ApiClient.getGroupIdRecords(100, page, token).getEntity(org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecords.class);
        }
        for (Long putCode : putsToDelete) {
            memberV3Rc2ApiClient.deleteGroupIdRecord(putCode, token);
        }
        putsToDelete.clear();
    }
    
    @After
    public void cleanUpGroupIds() throws JSONException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);

        for (Long putCode : putsToDelete) {
            memberV3Rc2ApiClient.deleteGroupIdRecord(putCode, token);
        }        
    }

    @Test
    public void testGetGroupIdRecordsWithSeveralFormats_v2_1() throws JSONException, InterruptedException, URISyntaxException, UnsupportedEncodingException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);
        
        for(String groupId : VALID_GROUP_IDS) {            
            org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord g1 = new org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(groupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV3Rc2ApiClient.createGroupIdRecord(g1, token);
            String r1LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v3.0_rc2/group-id-record/", "");
            g1.setPutCode(Long.valueOf(r1LocationPutCode));
            
            webDriver.get(getWebBaseUrl() + "/public/group/" + g1.getPutCode());
            WebElement preElement = webDriver.findElement(By.tagName("pre"));
            String groupElementString = preElement.getText();
            assertFalse(PojoUtil.isEmpty(groupElementString));
            org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord groupFromWebPage = JsonUtils.readObjectFromJsonString(groupElementString, org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord.class);
            assertNotNull(groupFromWebPage);
            assertEquals("Missing " + groupId, groupId, groupFromWebPage.getGroupId());
            
            putsToDelete.add(g1.getPutCode());
        }
        
        for(String invdalidGroupId : INVALID_GROUP_IDS) {            
            org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord g1 = new org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(invdalidGroupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV3Rc2ApiClient.createGroupIdRecord(g1, token);
            assertNotNull(r1);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r1.getStatus());
        }        
    }
    
    @Test
    public void testCreateAndView() throws JSONException, InterruptedException, URISyntaxException, UnsupportedEncodingException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);
        String groupId = "orcid-generated:test#" + System.currentTimeMillis();
        org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord g1 = new org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord();        
        g1.setDescription("Description");
        g1.setGroupId(groupId);
        g1.setName(groupId);
        g1.setType("publisher");

        ClientResponse r1 = memberV3Rc2ApiClient.createGroupIdRecord(g1, token);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), r1.getStatus());
        String r1LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v3.0_rc2/group-id-record/", "");
        Long putCode = Long.valueOf(r1LocationPutCode);
        
        
        ClientResponse result = memberV3Rc2ApiClient.getGroupIdRecord(putCode, token);
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord v3_0_rc2 = result.getEntity(org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord.class);
        assertEquals(putCode, v3_0_rc2.getPutCode());
        assertEquals("publisher", v3_0_rc2.getType());
        assertEquals("Description", v3_0_rc2.getDescription());
        assertEquals(groupId, v3_0_rc2.getGroupId());
        assertEquals(groupId, v3_0_rc2.getName());
        assertNotNull(v3_0_rc2.getSource());
        assertEquals(this.getClient1ClientId(), v3_0_rc2.retrieveSourcePath());
    }   
}
