package org.orcid.api.memberV3.server.delegator;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.v3.dev1.common.Iso3166Country;
import org.orcid.jaxb.model.v3.dev1.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.dev1.record.Address;
import org.orcid.jaxb.model.v3.dev1.record.Addresses;
import org.orcid.jaxb.model.v3.dev1.record.Distinction;
import org.orcid.jaxb.model.v3.dev1.record.Education;
import org.orcid.jaxb.model.v3.dev1.record.Employment;
import org.orcid.jaxb.model.v3.dev1.record.Funding;
import org.orcid.jaxb.model.v3.dev1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.dev1.record.Keyword;
import org.orcid.jaxb.model.v3.dev1.record.Membership;
import org.orcid.jaxb.model.v3.dev1.record.OtherName;
import org.orcid.jaxb.model.v3.dev1.record.PeerReview;
import org.orcid.jaxb.model.v3.dev1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.dev1.record.Qualification;
import org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.dev1.record.Service;
import org.orcid.jaxb.model.v3.dev1.record.Work;
import org.orcid.jaxb.model.v3.dev1.record.WorkBulk;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_AddressesTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegatorV3_0_dev1")
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword> serviceDelegator;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewAddressesWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewAddresses(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewAddressWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewAddress(ORCID, 10L);
    }

    @Test
    public void testViewAddressesReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewAddresses(ORCID);
        Addresses element = (Addresses) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/address", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewAddressReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewAddress(ORCID, 9L);
        Address element = (Address) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/address/9", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testReadPublicScope_Address() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        // Public works
        Response r = serviceDelegator.viewAddresses(ORCID);
        assertNotNull(r);
        assertEquals(Addresses.class.getName(), r.getEntity().getClass().getName());
        Addresses a = (Addresses) r.getEntity();
        assertNotNull(a);
        assertEquals("/0000-0000-0000-0003/address", a.getPath());
        Utils.verifyLastModified(a.getLastModifiedDate());
        assertEquals(3, a.getAddress().size());
        boolean found9 = false, found10 = false, found11 = false;
        for (Address address : a.getAddress()) {
            if (address.getPutCode() == 9) {
                found9 = true;
            } else if (address.getPutCode() == 10) {
                found10 = true;
            } else if (address.getPutCode() == 11) {
                found11 = true;
            } else {
                fail("Invalid put code " + address.getPutCode());
            }

        }
        assertTrue(found9);
        assertTrue(found10);
        assertTrue(found11);

        r = serviceDelegator.viewAddress(ORCID, 9L);
        assertNotNull(r);
        assertEquals(Address.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source should work
        serviceDelegator.viewAddress(ORCID, 10L);

        try {
            // Limited am not the source should fail
            serviceDelegator.viewAddress(ORCID, 12L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private where am the source should work
        serviceDelegator.viewAddress(ORCID, 11L);
        try {
            // Private am not the source should fail
            serviceDelegator.viewAddress(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testViewAddresses() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewAddresses("4444-4444-4444-4447");
        assertNotNull(response);
        Addresses addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertEquals("/4444-4444-4444-4447/address", addresses.getPath());
        Utils.verifyLastModified(addresses.getLastModifiedDate());
        assertNotNull(addresses.getAddress());
        assertEquals(3, addresses.getAddress().size());

        for (Address address : addresses.getAddress()) {
            Utils.verifyLastModified(address.getLastModifiedDate());
            assertThat(address.getPutCode(), anyOf(is(2L), is(3L), is(4L)));
            assertThat(address.getCountry().getValue(), anyOf(is(Iso3166Country.CR), is(Iso3166Country.US)));
            if (address.getPutCode() == 2L) {
                assertEquals(Visibility.PUBLIC, address.getVisibility());
                assertEquals("4444-4444-4444-4447", address.getSource().retrieveSourcePath());
            } else if (address.getPutCode() == 3L) {
                assertEquals(Visibility.LIMITED, address.getVisibility());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
            } else if (address.getPutCode() == 4L) {
                assertEquals(Visibility.PRIVATE, address.getVisibility());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4447", 2L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals("/4444-4444-4444-4447/address/2", address.getPath());
        Utils.verifyLastModified(address.getLastModifiedDate());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
        assertEquals("4444-4444-4444-4447", address.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
    }

    @Test
    public void testViewLimitedAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4447", 3L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals("/4444-4444-4444-4447/address/3", address.getPath());
        Utils.verifyLastModified(address.getLastModifiedDate());
        assertEquals(Visibility.LIMITED, address.getVisibility());
        assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.CR, address.getCountry().getValue());
    }

    @Test
    public void testViewPrivateAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4447", 4L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals("/4444-4444-4444-4447/address/4", address.getPath());
        Utils.verifyLastModified(address.getLastModifiedDate());
        assertEquals(Visibility.PRIVATE, address.getVisibility());
        assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.CR, address.getCountry().getValue());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateAddressWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewAddress("4444-4444-4444-4447", 5L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewAddressThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewAddress("4444-4444-4444-4447", 1L);
        fail();
    }

    @Test
    public void testAddAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.createAddress("4444-4444-4444-4442", Utils.getAddress());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewAddress("4444-4444-4444-4442", putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Address newAddress = (Address) response.getEntity();
        assertNotNull(newAddress);
        Utils.verifyLastModified(newAddress.getLastModifiedDate());
        assertEquals(Iso3166Country.ES, newAddress.getCountry().getValue());
        assertEquals(Visibility.LIMITED, newAddress.getVisibility());
        assertNotNull(newAddress.getSource());
        assertEquals("APP-5555555555555555", newAddress.getSource().retrieveSourcePath());
        assertNotNull(newAddress.getCreatedDate());
        Utils.verifyLastModified(newAddress.getLastModifiedDate());

        // Remove it
        response = serviceDelegator.deleteAddress("4444-4444-4444-4442", putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4442", 1L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        Utils.verifyLastModified(address.getLastModifiedDate());
        LastModifiedDate before = address.getLastModifiedDate();
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        address.getCountry().setValue(Iso3166Country.PA);

        response = serviceDelegator.updateAddress("4444-4444-4444-4442", 1L, address);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewAddress("4444-4444-4444-4442", 1L);
        assertNotNull(response);
        address = (Address) response.getEntity();
        assertNotNull(address);
        Utils.verifyLastModified(address.getLastModifiedDate());
        LastModifiedDate after = address.getLastModifiedDate();
        assertTrue(after.after(before));
        assertEquals(Iso3166Country.PA, address.getCountry().getValue());

        // Set it back to US again
        address.getCountry().setValue(Iso3166Country.US);
        response = serviceDelegator.updateAddress("4444-4444-4444-4442", 1L, address);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.viewAddress("4444-4444-4444-4442", 1L);
        address = (Address) response.getEntity();
        assertNotNull(address);
        Utils.verifyLastModified(address.getLastModifiedDate());
        assertNotNull(address.getLastModifiedDate());
        assertTrue(address.getLastModifiedDate().after(after));
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateAddressYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4447", 2L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
        assertNotNull(address.getSource());
        assertEquals("4444-4444-4444-4447", address.getSource().retrieveSourcePath());

        address.getCountry().setValue(Iso3166Country.BR);

        serviceDelegator.updateAddress("4444-4444-4444-4447", 2L, address);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateAddressChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4442", 1L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        address.setVisibility(Visibility.PRIVATE);

        response = serviceDelegator.updateAddress("4444-4444-4444-4442", 1L, address);
        fail();
    }

    @Test
    public void testUpdateAddressLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4442", 1L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        address.setVisibility(null);

        response = serviceDelegator.updateAddress("4444-4444-4444-4442", 1L, address);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PUBLIC, address.getVisibility());
    }

    @Test
    public void testDeleteAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4499", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddresses("4444-4444-4444-4499");
        assertNotNull(response);
        Addresses addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertEquals(1, addresses.getAddress().size());
        Long putCode = addresses.getAddress().get(0).getPutCode();
        response = serviceDelegator.deleteAddress("4444-4444-4444-4499", putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        response = serviceDelegator.viewAddresses("4444-4444-4444-4499");
        assertNotNull(response);
        addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertTrue(addresses.getAddress().isEmpty());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteAddressYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_UPDATE);
        serviceDelegator.deleteAddress("4444-4444-4444-4447", 5L);
        fail();
    }
}
