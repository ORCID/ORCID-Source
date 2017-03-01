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
import static org.junit.Assert.fail;

import java.util.List;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.integration.blackbox.api.v2.release.MemberV2ApiClientImpl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class EmailTest extends BlackBoxBaseV2Release {
    @Resource(name = "memberV2ApiClient_rc2")
    private org.orcid.integration.blackbox.api.v2.rc2.MemberV2ApiClientImpl memberV2ApiClient_rc2;
    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient_rc2;

    @Resource(name = "memberV2ApiClient_rc3")
    private org.orcid.integration.blackbox.api.v2.rc3.MemberV2ApiClientImpl memberV2ApiClient_rc3;
    @Resource(name = "publicV2ApiClient_rc3")
    private PublicV2ApiClientImpl publicV2ApiClient_rc3;

    @Resource(name = "memberV2ApiClient_rc4")
    private org.orcid.integration.blackbox.api.v2.rc4.MemberV2ApiClientImpl memberV2ApiClient_rc4;
    @Resource(name = "publicV2ApiClient_rc4")
    private PublicV2ApiClientImpl publicV2ApiClient_rc4;

    @Resource(name = "memberV2ApiClient")
    private MemberV2ApiClientImpl memberV2ApiClient_release;
    @Resource(name = "publicV2ApiClient")
    private PublicV2ApiClientImpl publicV2ApiClient_release;

    private String limitedEmailValue = "limited@test.orcid.org";

    @Before
    public void setUpData() {
        signout();
        signin();
        showAccountSettingsPage();
        openEditEmailsSectionOnAccountSettingsPage();
        updatePrimaryEmailVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        removePopOver();
        try {
            updateEmailVisibility(limitedEmailValue, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        } catch (Exception e) {
            addEmail(limitedEmailValue, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        }                
    }

    /**
     * --------- -- -- -- RC2 -- -- -- ---------
     * 
     */
    /**
     * PRECONDITIONS: The primary email must be public
     */
    @Test
    public void testGetWithPublicAPI_rc2() {
        ClientResponse getAllResponse = publicV2ApiClient_rc2.viewEmailXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_rc2.Emails emails = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc2.Emails.class);
        assertListContainsEmail_rc2(getUser1UserName(), org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, emails);
    }

    /**
     * PRECONDITIONS: The primary email must be public The user must have a
     * limited email limited@email.com
     * 
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void testGetWithMembersAPI_rc2() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        ClientResponse getAllResponse = memberV2ApiClient_rc2.getEmails(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_rc2.Emails emails = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc2.Emails.class);
        assertListContainsEmail_rc2(getUser1UserName(), org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, emails);
        assertListContainsEmail_rc2("limited@test.orcid.org", org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, emails);
    }

    public static void assertListContainsEmail_rc2(String emailString, org.orcid.jaxb.model.common_rc2.Visibility visibility,
            org.orcid.jaxb.model.record_rc2.Emails emails) {
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertFalse(emails.getEmails().isEmpty());
        for (org.orcid.jaxb.model.record_rc2.Email email : emails.getEmails()) {
            if (email.getEmail().equals(emailString)) {
                assertEquals(visibility, email.getVisibility());
                return;
            }
        }
        fail();
    }

    /**
     * --------- -- -- -- RC3 -- -- -- ---------
     * 
     */
    /**
     * PRECONDITIONS: The primary email must be public
     */
    @Test
    public void testGetWithPublicAPI_rc3() {
        ClientResponse getAllResponse = publicV2ApiClient_rc3.viewEmailXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_rc3.Emails emails = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc3.Emails.class);
        assertListContainsEmail_rc3(getUser1UserName(), org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, emails);
    }

    /**
     * PRECONDITIONS: The primary email must be public The user must have a
     * limited email limited@email.com
     * 
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void testGetWithMembersAPI_rc3() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        ClientResponse getAllResponse = memberV2ApiClient_rc3.getEmails(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_rc3.Emails emails = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc3.Emails.class);
        assertListContainsEmail_rc3(getUser1UserName(), org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, emails);
        assertListContainsEmail_rc3("limited@test.orcid.org", org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, emails);
    }

    public static void assertListContainsEmail_rc3(String emailString, org.orcid.jaxb.model.common_rc3.Visibility visibility,
            org.orcid.jaxb.model.record_rc3.Emails emails) {
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertFalse(emails.getEmails().isEmpty());
        for (org.orcid.jaxb.model.record_rc3.Email email : emails.getEmails()) {
            if (email.getEmail().equals(emailString)) {
                assertEquals(visibility, email.getVisibility());
                return;
            }
        }
        fail();
    }

    /**
     * --------- -- -- -- RC4 -- -- -- ---------
     * 
     */
    /**
     * PRECONDITIONS: The primary email must be public
     */
    @Test
    public void testGetWithPublicAPI_rc4() {
        ClientResponse getAllResponse = publicV2ApiClient_rc4.viewEmailXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_rc4.Emails emails = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc4.Emails.class);
        assertListContainsEmail_rc4(getUser1UserName(), org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, emails);
    }

    /**
     * PRECONDITIONS: The primary email must be public The user must have a
     * limited email limited@email.com
     * 
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void testGetWithMembersAPI_rc4() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        ClientResponse getAllResponse = memberV2ApiClient_rc4.getEmails(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_rc4.Emails emails = getAllResponse.getEntity(org.orcid.jaxb.model.record_rc4.Emails.class);
        assertListContainsEmail_rc4(getUser1UserName(), org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, emails);
        assertListContainsEmail_rc4("limited@test.orcid.org", org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, emails);
    }

    public static void assertListContainsEmail_rc4(String emailString, org.orcid.jaxb.model.common_rc4.Visibility visibility,
            org.orcid.jaxb.model.record_rc4.Emails emails) {
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertFalse(emails.getEmails().isEmpty());
        for (org.orcid.jaxb.model.record_rc4.Email email : emails.getEmails()) {
            if (email.getEmail().equals(emailString)) {
                assertEquals(visibility, email.getVisibility());
                return;
            }
        }
        fail();
    }

    /**
     * --------- -- -- -- release -- -- -- ---------
     * 
     */

    /**
     * PRECONDITIONS: The primary email must be public
     */
    @Test
    public void testGetWithPublicAPI_release() {
        ClientResponse getAllResponse = publicV2ApiClient_release.viewEmailXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_v2.Emails emails = getAllResponse.getEntity(org.orcid.jaxb.model.record_v2.Emails.class);
        assertListContainsEmail_release(getUser1UserName(), org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, emails);
    }

    /**
     * PRECONDITIONS: The primary email must be public The user must have a
     * limited email limited@email.com
     * 
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void testGetWithMembersAPI_release() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        ClientResponse getAllResponse = memberV2ApiClient_release.getEmails(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        org.orcid.jaxb.model.record_v2.Emails emails = getAllResponse.getEntity(org.orcid.jaxb.model.record_v2.Emails.class);
        assertListContainsEmail_release(getUser1UserName(), org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, emails);
        assertListContainsEmail_release("limited@test.orcid.org", org.orcid.jaxb.model.common_v2.Visibility.LIMITED, emails);
    }

    public static void assertListContainsEmail_release(String emailString, org.orcid.jaxb.model.common_v2.Visibility visibility,
            org.orcid.jaxb.model.record_v2.Emails emails) {
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertFalse(emails.getEmails().isEmpty());
        for (org.orcid.jaxb.model.record_v2.Email email : emails.getEmails()) {
            if (email.getEmail().equals(emailString)) {
                assertEquals(visibility, email.getVisibility());
                return;
            }
        }
        fail();
    }

    public String getAccessToken() throws InterruptedException, JSONException {
        List<String> scopes = getScopes(ScopePathType.READ_LIMITED);
        return getAccessToken(scopes);
    }
}
