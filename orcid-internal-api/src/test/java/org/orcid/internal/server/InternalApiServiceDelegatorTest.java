package org.orcid.internal.server;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.security.AccessControlException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.internal.server.delegator.InternalApiServiceDelegator;
import org.orcid.internal.util.EmailResponse;
import org.orcid.internal.util.LastModifiedResponse;
import org.orcid.internal.util.MemberInfo;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.test.DBUnitTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-internal-api-context.xml" })
public class InternalApiServiceDelegatorTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml");

    private static final String USER_ORCID = "5555-5555-5555-5558";
    
    @Resource
    private InternalApiServiceDelegator internalApiServiceDelegator;
    
    @Resource(name = "profileEntityManagerReadOnlyV3")
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnly;
    
    @Before
    public void before() throws Exception {
        initDBUnitData(DATA_FILES);
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.INTERNAL_PERSON_LAST_MODIFIED);
    }

    @Test
    public void viewStatusTextTest() {
        Response response = internalApiServiceDelegator.viewStatusText();
        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals("OK I am here", String.valueOf(response.getEntity()));
    }
    
    @Test
    public void viewLastModifiedTest() {
        Response response = internalApiServiceDelegator.viewPersonLastModified(USER_ORCID);
        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity().getClass().isAssignableFrom(LastModifiedResponse.class));
        LastModifiedResponse obj = (LastModifiedResponse) response.getEntity();
        assertNotNull(obj);
        assertEquals(USER_ORCID, obj.getOrcid());
        assertEquals(profileEntityManagerReadOnly.getLastModifiedDate(USER_ORCID).toString(), obj.getLastModified());
    }
    
    @Test
    public void viewMemberInfoTest() {
        String memberId = "5555-5555-5555-5558";
        Response response = internalApiServiceDelegator.viewMemberInfo(memberId);
        assertNotNull(response);
        MemberInfo info = (MemberInfo) response.getEntity();
        assertNotNull(info);
        assertEquals(memberId, info.getId());
        assertNotNull(info.getName());
        assertNotNull(info.getClients());
        assertFalse(info.getClients().isEmpty());
        
        response = internalApiServiceDelegator.viewMemberInfo(info.getName());
        assertNotNull(response);
        MemberInfo infoByName = (MemberInfo) response.getEntity();
        assertNotNull(infoByName);
        assertEquals(memberId, infoByName.getId());
        assertEquals(info, infoByName);        
        
        response = internalApiServiceDelegator.viewMemberInfo("invalid name");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());        
        OrcidError error = (OrcidError)response.getEntity();
        assertNotNull(error);
        assertEquals(new Integer(0), error.getErrorCode());
        assertEquals("Member id or name not found for: invalid name", error.getDeveloperMessage());                        
    }
    
    @Test
    public void findOrcidByEmailTest() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.INTERNAL);
        Response response = internalApiServiceDelegator.findOrcidByEmail("5555-5555-5555-5558@user.com");
        assertNotNull(response);
        EmailResponse info = (EmailResponse) response.getEntity();
        assertEquals(HttpStatus.FOUND, info.getStatus());
        assertEquals("5555-5555-5555-5558@user.com", info.getEmail());
        assertEquals(USER_ORCID, info.getOrcid());
        
        
        response = internalApiServiceDelegator.findOrcidByEmail("invalid@email.com");
        assertNotNull(response);
        info = (EmailResponse) response.getEntity();
        assertEquals(HttpStatus.NOT_FOUND, info.getStatus());
        assertEquals("invalid@email.com", info.getEmail());
        assertEquals("", info.getOrcid());
    }
    
    @Test(expected = AccessControlException.class)
    public void findOrcidByEmailWrongScopeTest() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.INTERNAL_PERSON_LAST_MODIFIED);
        internalApiServiceDelegator.findOrcidByEmail("5555-5555-5555-5558@user.com");        
    }
}
