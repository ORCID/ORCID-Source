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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class GroupIdRecordTest extends BlackBoxBaseRC2 {

    private static final List<String> VALID_GROUP_IDS = Arrays.asList( 
            "orcid-generated:this.is.a.test.", 
            "orcid-generated:this'is'a'test'", 
            "orcid-generated:this_is_a_test_", 
            "orcid-generated:this-is-a-test-",
            "orcid-generated:(this)(is)(a)(test)",
            "orcid-generated:this^is^a^test^",
            "orcid-generated:this~is~a~test~",
            "orcid-generated:this:is:a:test:",
            "orcid-generated:this/is/a/test/",
            "orcid-generated:this?is?a?test?",
            "orcid-generated:this#is#a#test#",
            "orcid-generated:this[is]a[test]",
            "orcid-generated:this@is@a@test@",
            "orcid-generated:this!is!a!test!",
            "orcid-generated:this$is$a$test$",
            "orcid-generated:this&is&a&test&",
            "orcid-generated:this*is*a*test*",
            "orcid-generated:this+is+a+test+",
            "orcid-generated:this,is,a,test,");
    
    public static final List<String> INVALID_GROUP_IDS = Arrays.asList(
            "orcid-generated:this{is}a{test}",
            "orcid-generated:this\\is\\a\\test\\",
            "orcid-generated:this\"is\"a\"test\"",
            "orcid-generated:this<is>a<test>",
            "orcid-generated:this¢is¢a¢test¢");
    
    @Test
    public void testGetGroupIdRecordsWithSeveralFormats() throws JSONException, InterruptedException, URISyntaxException, UnsupportedEncodingException {
        String token = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.GROUP_ID_RECORD_UPDATE);
        
        for(String groupId : VALID_GROUP_IDS) {            
            GroupIdRecord g1 = new GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(groupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV2ApiClient.createGroupIdRecord(g1, token);
            String r1LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v2.0_rc2/group-id-record/", "");
            g1.setPutCode(Long.valueOf(r1LocationPutCode));
            
            webDriver.get(getWebBaseUrl() + "/public/group/" + g1.getPutCode());
            WebElement preElement = webDriver.findElement(By.tagName("pre"));
            String groupElementString = preElement.getText();
            assertFalse(PojoUtil.isEmpty(groupElementString));
            GroupIdRecord groupFromWebPage = JsonUtils.readObjectFromJsonString(groupElementString, GroupIdRecord.class);
            assertNotNull(groupFromWebPage);
            assertEquals("Missing " + groupId, groupId, groupFromWebPage.getGroupId());
            
            memberV2ApiClient.deleteGroupIdRecord(g1.getPutCode(), token);
        }
        
        for(String invdalidGroupId : INVALID_GROUP_IDS) {            
            GroupIdRecord g1 = new GroupIdRecord();
            g1.setDescription("Description");
            g1.setGroupId(invdalidGroupId);
            g1.setName("Group # " + System.currentTimeMillis());
            g1.setType("publisher");
            ClientResponse r1 = memberV2ApiClient.createGroupIdRecord(g1, token);
            assertNotNull(r1);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), r1.getStatus());
        }        
    }
}
