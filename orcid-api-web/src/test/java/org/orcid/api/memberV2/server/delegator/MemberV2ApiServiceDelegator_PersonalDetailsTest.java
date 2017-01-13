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
package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.groupid_rc4.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.jaxb.model.record_rc4.Education;
import org.orcid.jaxb.model.record_rc4.Employment;
import org.orcid.jaxb.model.record_rc4.Funding;
import org.orcid.jaxb.model.record_rc4.Keyword;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.PeerReview;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.PersonalDetails;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.Work;
import org.orcid.jaxb.model.record_rc4.WorkBulk;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV2ApiServiceDelegator_PersonalDetailsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV2ApiServiceDelegator")
    protected MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, WorkBulk, Address, Keyword> serviceDelegator;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test
    public void testViewPersonalDetailsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPersonalDetails(ORCID);
        PersonalDetails element = (PersonalDetails) r.getEntity();
        assertNotNull(element);
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPersonalDetailsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPersonalDetails(ORCID);
    }

    @Test
    public void testReadPublicScope_PersonalDetails() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPersonalDetails(ORCID);
        assertNotNull(r);
        assertEquals(PersonalDetails.class.getName(), r.getEntity().getClass().getName());
        PersonalDetails p = (PersonalDetails) r.getEntity();
        Utils.verifyLastModified(p.getLastModifiedDate());
        Utils.verifyLastModified(p.getBiography().getLastModifiedDate());
        Utils.verifyLastModified(p.getName().getLastModifiedDate());
        Utils.verifyLastModified(p.getOtherNames().getLastModifiedDate());
        assertEquals("Biography for 0000-0000-0000-0003", p.getBiography().getContent());
        assertEquals("Credit Name", p.getName().getCreditName().getContent());
        assertEquals("Given Names", p.getName().getGivenNames().getContent());
        assertEquals("Family Name", p.getName().getFamilyName().getContent());
        assertEquals(3, p.getOtherNames().getOtherNames().size());

        boolean found13 = false, found14 = false, found15 = false;
        for (OtherName element : p.getOtherNames().getOtherNames()) {
            if (element.getPutCode() == 13) {
                found13 = true;
            } else if (element.getPutCode() == 14) {
                found14 = true;
            } else if (element.getPutCode() == 15) {
                found15 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found13);
        assertTrue(found14);
        assertTrue(found15);

        String otherOrcid = "0000-0000-0000-0002";
        SecurityContextTestUtils.setUpSecurityContext(otherOrcid, ScopePathType.READ_PUBLIC);
        r = serviceDelegator.viewPersonalDetails(otherOrcid);
        assertNotNull(r);
        assertEquals(PersonalDetails.class.getName(), r.getEntity().getClass().getName());
        p = (PersonalDetails) r.getEntity();
        assertNull(p.getBiography());
        assertNull(p.getName());
        assertNull(p.getOtherNames());
    }

    @Test
    public void testViewPersonalDetails() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewPersonalDetails(ORCID);
        assertNotNull(response);
        PersonalDetails personalDetails = (PersonalDetails) response.getEntity();
        assertNotNull(personalDetails);
        Utils.verifyLastModified(personalDetails.getLastModifiedDate());
        assertNotNull(personalDetails.getBiography());
        Utils.verifyLastModified(personalDetails.getBiography().getLastModifiedDate());
        assertEquals("Biography for 0000-0000-0000-0003", personalDetails.getBiography().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());
        assertEquals("/0000-0000-0000-0003/biography", personalDetails.getBiography().getPath());
        assertNotNull(personalDetails.getName());
        Utils.verifyLastModified(personalDetails.getName().getLastModifiedDate());
        assertNotNull(personalDetails.getName().getCreatedDate().getValue());
        assertEquals("Credit Name", personalDetails.getName().getCreditName().getContent());
        assertEquals("Family Name", personalDetails.getName().getFamilyName().getContent());
        assertEquals("Given Names", personalDetails.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getName().getVisibility().value());
        assertNotNull(personalDetails.getOtherNames());
        Utils.verifyLastModified(personalDetails.getOtherNames().getLastModifiedDate());
        assertEquals(4, personalDetails.getOtherNames().getOtherNames().size());

        for (OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            Utils.verifyLastModified(otherName.getLastModifiedDate());
            if (otherName.getPutCode().equals(Long.valueOf(13))) {
                assertEquals("Other Name PUBLIC", otherName.getContent());
                assertEquals(Long.valueOf(0), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(14))) {
                assertEquals("Other Name LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(1), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/14", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(15))) {
                assertEquals("Other Name PRIVATE", otherName.getContent());
                assertEquals(Long.valueOf(2), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/15", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(16))) {
                assertEquals("Other Name SELF LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(3), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/16", otherName.getPath());
                assertEquals("0000-0000-0000-0003", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());
            } else {
                fail("Invalid put code found: " + otherName.getPutCode());
            }
        }

        assertEquals("/0000-0000-0000-0003/other-names", personalDetails.getOtherNames().getPath());
        assertEquals("/0000-0000-0000-0003/personal-details", personalDetails.getPath());
    }
}
