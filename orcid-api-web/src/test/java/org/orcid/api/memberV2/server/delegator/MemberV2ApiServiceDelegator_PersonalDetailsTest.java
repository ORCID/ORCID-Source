package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.jaxb.model.record_v2.FamilyName;
import org.orcid.jaxb.model.record_v2.GivenNames;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.PersonalDetails;
import org.orcid.test.helper.Utils;

/**
 * The personal-details endpoint of the member v2 delegator, on mocks.
 *
 * <p>
 * {@code viewPersonalDetails} composes a name, a biography and the other names,
 * hands the lot to {@code checkAndFilter(String, PersonalDetails)}, then sets the
 * paths and recomputes the last-modified dates. Which of the three parts survive
 * the filter is decided in {@code OrcidSecurityManager_PersonTest}; that
 * overload is void and edits the object in place, so it cannot be demonstrated
 * against a mock.
 */
public class MemberV2ApiServiceDelegator_PersonalDetailsTest extends MemberV2ApiServiceDelegatorMockBase {

    @Test
    public void testViewPersonalDetailsReadPublic() {
        PersonalDetails personalDetails = personalDetails();
        when(personalDetailsManagerReadOnly.getPersonalDetails(ORCID)).thenReturn(personalDetails);

        Response r = serviceDelegator.viewPersonalDetails(ORCID);

        PersonalDetails element = (PersonalDetails) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/personal-details", element.getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, personalDetails);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPersonalDetailsWrongToken() {
        PersonalDetails personalDetails = personalDetails();
        when(personalDetailsManagerReadOnly.getPersonalDetails(ORCID)).thenReturn(personalDetails);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, personalDetails);

        try {
            serviceDelegator.viewPersonalDetails(ORCID);
        } finally {
            assertNull("nothing must be decorated once the guard has refused", personalDetails.getPath());
        }
    }

    @Test
    public void testReadPublicScope_PersonalDetails() {
        PersonalDetails mine = personalDetails();
        when(personalDetailsManagerReadOnly.getPersonalDetails(ORCID)).thenReturn(mine);
        // A read-public token on another record: checkAndFilter strips the
        // non-public parts in place. Modelled here as the object the security
        // manager would have left behind, because a mock filters nothing --
        // see OrcidSecurityManager_PersonTest for the filtering itself.
        String otherOrcid = "0000-0000-0000-0002";
        PersonalDetails stripped = new PersonalDetails();
        stripped.setOtherNames(new OtherNames());
        stripped.getOtherNames().setOtherNames(new ArrayList<>());
        when(personalDetailsManagerReadOnly.getPersonalDetails(otherOrcid)).thenReturn(stripped);

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

        r = serviceDelegator.viewPersonalDetails(otherOrcid);
        assertNotNull(r);
        assertEquals(PersonalDetails.class.getName(), r.getEntity().getClass().getName());
        p = (PersonalDetails) r.getEntity();
        assertNull(p.getBiography());
        assertNull(p.getName());
        assertNotNull(p.getOtherNames());
        assertTrue(p.getOtherNames().getOtherNames().isEmpty());
        // the delegator still decorates whatever survived the filter
        assertEquals("/0000-0000-0000-0002/personal-details", p.getPath());
        assertEquals("/0000-0000-0000-0002/other-names", p.getOtherNames().getPath());
    }

    @Test
    public void testViewPersonalDetails() {
        PersonalDetails personalDetails = personalDetails();
        personalDetails.getOtherNames().getOtherNames().add(otherName(16L, "Other Name SELF LIMITED", 3L, Visibility.LIMITED, userSource(ORCID)));
        when(personalDetailsManagerReadOnly.getPersonalDetails(ORCID)).thenReturn(personalDetails);

        Response response = serviceDelegator.viewPersonalDetails(ORCID);

        assertNotNull(response);
        PersonalDetails returned = (PersonalDetails) response.getEntity();
        assertNotNull(returned);
        assertEquals("/0000-0000-0000-0003/personal-details", returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertNotNull(returned.getBiography());
        Utils.verifyLastModified(returned.getBiography().getLastModifiedDate());
        assertEquals("Biography for 0000-0000-0000-0003", returned.getBiography().getContent());
        assertEquals(Visibility.PUBLIC.value(), returned.getBiography().getVisibility().value());
        assertEquals("/0000-0000-0000-0003/biography", returned.getBiography().getPath());
        assertNotNull(returned.getName());
        Utils.verifyLastModified(returned.getName().getLastModifiedDate());
        assertNotNull(returned.getName().getCreatedDate().getValue());
        assertEquals("Credit Name", returned.getName().getCreditName().getContent());
        assertEquals("Family Name", returned.getName().getFamilyName().getContent());
        assertEquals("Given Names", returned.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC.value(), returned.getName().getVisibility().value());
        assertNotNull(returned.getOtherNames());
        Utils.verifyLastModified(returned.getOtherNames().getLastModifiedDate());
        assertEquals(4, returned.getOtherNames().getOtherNames().size());

        for (OtherName otherName : returned.getOtherNames().getOtherNames()) {
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

        assertEquals("/0000-0000-0000-0003/other-names", returned.getOtherNames().getPath());
        assertEquals("/0000-0000-0000-0003/personal-details", returned.getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, personalDetails);
    }

    // ------------------------------------------------------------- fixtures

    private PersonalDetails personalDetails() {
        PersonalDetails personalDetails = new PersonalDetails();

        Biography biography = new Biography();
        biography.setContent("Biography for 0000-0000-0000-0003");
        biography.setVisibility(Visibility.PUBLIC);
        biography.setCreatedDate(createdDate());
        biography.setLastModifiedDate(lastModified());
        personalDetails.setBiography(biography);

        Name name = new Name();
        name.setCreditName(new CreditName("Credit Name"));
        name.setGivenNames(new GivenNames("Given Names"));
        name.setFamilyName(new FamilyName("Family Name"));
        name.setVisibility(Visibility.PUBLIC);
        name.setCreatedDate(createdDate());
        name.setLastModifiedDate(lastModified());
        personalDetails.setName(name);

        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<>(Arrays.asList(otherName(13L, "Other Name PUBLIC", 0L, Visibility.PUBLIC, clientSource(CLIENT_1)),
                otherName(14L, "Other Name LIMITED", 1L, Visibility.LIMITED, clientSource(CLIENT_1)),
                otherName(15L, "Other Name PRIVATE", 2L, Visibility.PRIVATE, clientSource(CLIENT_1)))));
        otherNames.setLastModifiedDate(lastModified());
        personalDetails.setOtherNames(otherNames);

        return personalDetails;
    }

    private OtherName otherName(Long putCode, String content, Long displayIndex, Visibility visibility, Source source) {
        OtherName otherName = new OtherName();
        otherName.setPutCode(putCode);
        otherName.setContent(content);
        otherName.setDisplayIndex(displayIndex);
        otherName.setVisibility(visibility);
        otherName.setSource(source);
        otherName.setCreatedDate(createdDate());
        otherName.setLastModifiedDate(lastModified());
        return otherName;
    }
}
