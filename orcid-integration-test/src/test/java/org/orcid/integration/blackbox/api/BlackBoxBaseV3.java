package org.orcid.integration.blackbox.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.orcid.integration.api.helper.APIRequestType;
import org.orcid.integration.blackbox.api.v3.rc1.MemberV3Rc1ApiClientImpl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecord;

import com.sun.jersey.api.client.ClientResponse;

public class BlackBoxBaseV3 extends BlackBoxBase {
    @Resource(name = "memberV3_0_rc1ApiClient")
    protected MemberV3Rc1ApiClientImpl memberV3Rc1ApiClient;
    
    protected static List<GroupIdRecord> groupRecords = null;
    
    /**
     * Create group ids
     * */
    public List<GroupIdRecord> createGroupIdsV3() throws JSONException {
        //Use the existing ones
        if(groupRecords != null && !groupRecords.isEmpty()) 
            return groupRecords;
        
        groupRecords = new ArrayList<GroupIdRecord>();
        
        String token = getClientCredentialsAccessToken(ScopePathType.GROUP_ID_RECORD_UPDATE, getClient1ClientId(), getClient1ClientSecret(), APIRequestType.MEMBER);
        GroupIdRecord g1 = new GroupIdRecord();
        g1.setDescription("Description");
        g1.setGroupId("orcid-generated:01" + System.currentTimeMillis());
        g1.setName("Group # 1");
        g1.setType("publisher");
        
        GroupIdRecord g2 = new GroupIdRecord();
        g2.setDescription("Description");
        g2.setGroupId("orcid-generated:02" + System.currentTimeMillis());
        g2.setName("Group # 2");
        g2.setType("publisher");                
        
        ClientResponse r1 = memberV3Rc1ApiClient.createGroupIdRecord(g1, token); 
        
        String r1LocationPutCode = r1.getLocation().getPath().replace("/orcid-api-web/v3.0_rc1/group-id-record/", "");
        g1.setPutCode(Long.valueOf(r1LocationPutCode));
        groupRecords.add(g1);
        
        ClientResponse r2 = memberV3Rc1ApiClient.createGroupIdRecord(g2, token);
        String r2LocationPutCode = r2.getLocation().getPath().replace("/orcid-api-web/v3.0_rc1/group-id-record/", "");
        g2.setPutCode(Long.valueOf(r2LocationPutCode));
        groupRecords.add(g2);
        
        return groupRecords;
    }
    
}