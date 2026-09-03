package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.FamilyName;
import org.orcid.jaxb.model.v3.release.record.GivenNames;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.PersonalDetails;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the personal-details endpoint of the member V3 API.
 *
 * <p>
 * {@code checkAndFilter(orcid, personalDetails)} nulls the biography and name
 * and prunes the other names in place; which of them a caller may see is the
 * security manager's table and is proved by orcid-core's
 * {@code OrcidSecurityManager_PersonTest}. Here the filter is stood in for where
 * a test needs one, and what is asserted is the delegator's own work: which
 * manager it asks, the paths it stamps, and that it returns the object the
 * filter mutated.
 */
public class MemberV3ApiServiceDelegator_PersonalDetailsTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final String OTHER_ORCID = "0000-0000-0000-0002";

    private PersonalDetails personalDetails(Source source) {
        PersonalDetails personalDetails = new PersonalDetails();

        Biography biography = new Biography();
        biography.setContent("Biography for 0000-0000-0000-0003");
        biography.setVisibility(Visibility.PUBLIC);
        biography.setLastModifiedDate(lastModified());
        personalDetails.setBiography(biography);

        Name name = new Name();
        name.setCreditName(new CreditName("Credit Name"));
        name.setGivenNames(new GivenNames("Given Names"));
        name.setFamilyName(new FamilyName("Family Name"));
        name.setVisibility(Visibility.PUBLIC);
        name.setCreatedDate(created());
        name.setLastModifiedDate(lastModified());
        personalDetails.setName(name);

        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<>(Arrays.asList(otherName(13L, "Other Name PUBLIC", 0L, Visibility.PUBLIC, source),
                otherName(14L, "Other Name LIMITED", 1L, Visibility.LIMITED, source), otherName(15L, "Other Name PRIVATE", 2L, Visibility.PRIVATE, source),
                otherName(16L, "Other Name SELF LIMITED", 3L, Visibility.LIMITED, userSource(ORCID)))));
        personalDetails.setOtherNames(otherNames);

        return personalDetails;
    }

    private OtherName otherName(long putCode, String content, Long displayIndex, Visibility visibility, Source source) {
        OtherName element = new OtherName();
        element.setPutCode(putCode);
        element.setContent(content);
        element.setDisplayIndex(displayIndex);
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    /**
     * Stands in for {@code OrcidSecurityManagerImpl}'s in-place filter over
     * personal details: it drops everything that is not public. The rule is
     * proved in orcid-core.
     */
    private void keepOnlyPublic(String orcid) {
        doAnswer(invocation -> {
            PersonalDetails personalDetails = invocation.getArgument(1);
            personalDetails.getOtherNames().getOtherNames().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            return null;
        }).when(orcidSecurityManager).checkAndFilter(eq(orcid), any(PersonalDetails.class));
    }

    @Test
    public void testViewPersonalDetailsReadPublic() {
        when(personalDetailsManagerReadOnly.getPersonalDetails(ORCID)).thenReturn(personalDetails(clientSource(CLIENT_1)));
        keepOnlyPublic(ORCID);

        Response r = serviceDelegator.viewPersonalDetails(ORCID);
        PersonalDetails element = (PersonalDetails) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/personal-details", element.getPath());
        assertEquals("/0000-0000-0000-0003/other-names", element.getOtherNames().getPath());
        assertEquals("/0000-0000-0000-0003/biography", element.getBiography().getPath());
        // Only the public other name survives the filter, and it is the filtered
        // list that comes back.
        assertEquals(1, element.getOtherNames().getOtherNames().size());
        assertEquals(Visibility.PUBLIC, element.getOtherNames().getOtherNames().get(0).getVisibility());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPersonalDetailsWrongToken() {
        when(personalDetailsManagerReadOnly.getPersonalDetails(ORCID)).thenReturn(personalDetails(clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID),
                any(PersonalDetails.class));

        serviceDelegator.viewPersonalDetails(ORCID);
    }

    @Test
    public void testReadPublicScope_PersonalDetails() {
        when(personalDetailsManagerReadOnly.getPersonalDetails(ORCID)).thenReturn(personalDetails(clientSource(CLIENT_1)));
        // The read-public scope leaves the public biography, name and other names
        // in place; the three private-ish other names are dropped.
        doAnswer(invocation -> {
            PersonalDetails details = invocation.getArgument(1);
            details.getOtherNames().getOtherNames().removeIf(e -> e.getPutCode() > 15L);
            return null;
        }).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(PersonalDetails.class));

        Response r = serviceDelegator.viewPersonalDetails(ORCID);
        assertNotNull(r);
        assertEquals(PersonalDetails.class.getName(), r.getEntity().getClass().getName());
        PersonalDetails p = (PersonalDetails) r.getEntity();
        assertEquals("/0000-0000-0000-0003/personal-details", p.getPath());
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

        // A record whose name and biography are not public comes back with them
        // removed; the delegator must return what the filter left, not the
        // object it read.
        PersonalDetails otherRecord = personalDetails(clientSource(CLIENT_1));
        when(personalDetailsManagerReadOnly.getPersonalDetails(OTHER_ORCID)).thenReturn(otherRecord);
        doAnswer(invocation -> {
            PersonalDetails details = invocation.getArgument(1);
            details.setBiography(null);
            details.setName(null);
            details.getOtherNames().getOtherNames().clear();
            return null;
        }).when(orcidSecurityManager).checkAndFilter(eq(OTHER_ORCID), any(PersonalDetails.class));

        r = serviceDelegator.viewPersonalDetails(OTHER_ORCID);
        assertNotNull(r);
        assertEquals(PersonalDetails.class.getName(), r.getEntity().getClass().getName());
        p = (PersonalDetails) r.getEntity();
        assertNull(p.getBiography());
        assertNull(p.getName());
        assertNotNull(p.getOtherNames());
        assertTrue(p.getOtherNames().getOtherNames().isEmpty());
    }

    @Test
    public void testViewPersonalDetails() {
        when(personalDetailsManagerReadOnly.getPersonalDetails(ORCID)).thenReturn(personalDetails(clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewPersonalDetails(ORCID);
        assertNotNull(response);
        PersonalDetails personalDetails = (PersonalDetails) response.getEntity();
        assertNotNull(personalDetails);
        assertEquals("/0000-0000-0000-0003/personal-details", personalDetails.getPath());
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
                assertEquals(CLIENT_1, otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(14))) {
                assertEquals("Other Name LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(1), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/14", otherName.getPath());
                assertEquals(CLIENT_1, otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(15))) {
                assertEquals("Other Name PRIVATE", otherName.getContent());
                assertEquals(Long.valueOf(2), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/15", otherName.getPath());
                assertEquals(CLIENT_1, otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(16))) {
                assertEquals("Other Name SELF LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(3), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/16", otherName.getPath());
                assertEquals(ORCID, otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());
            } else {
                fail("Invalid put code found: " + otherName.getPutCode());
            }
        }

        assertEquals("/0000-0000-0000-0003/other-names", personalDetails.getOtherNames().getPath());
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(orcidSecurityManager).checkAndFilter(ORCID, personalDetails);
    }
}
