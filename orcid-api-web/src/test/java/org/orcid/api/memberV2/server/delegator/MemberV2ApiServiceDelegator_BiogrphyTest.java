package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.Biography;

/**
 * The biography endpoint of the member v2 delegator, on mocks.
 *
 * <p>
 * {@code viewBiography} is the simplest read in the family: fetch, guard, set the
 * path. Whether a biography of a given visibility may be shown to a given token
 * is decided by {@code OrcidSecurityManager} and proved in
 * {@code OrcidSecurityManager_generalTest}.
 */
public class MemberV2ApiServiceDelegator_BiogrphyTest extends MemberV2ApiServiceDelegatorMockBase {

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewBiographyWrongToken() {
        Biography biography = biography(Visibility.PUBLIC);
        when(biographyManagerReadOnly.getBiography(ORCID)).thenReturn(biography);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, biography,
                ScopePathType.ORCID_BIO_READ_LIMITED);

        try {
            serviceDelegator.viewBiography(ORCID);
        } finally {
            assertNull("the biography must not be decorated once the guard has refused", biography.getPath());
        }
    }

    @Test
    public void testViewBiographyReadPublic() {
        Biography biography = biography(Visibility.PUBLIC);
        when(biographyManagerReadOnly.getBiography(ORCID)).thenReturn(biography);

        Response r = serviceDelegator.viewBiography(ORCID);

        Biography element = (Biography) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/biography", element.getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, biography, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test
    public void testReadPublicScope_Biography() {
        Biography mine = biography(Visibility.PUBLIC);
        Biography someoneElses = biography(Visibility.LIMITED);
        when(biographyManagerReadOnly.getBiography(ORCID)).thenReturn(mine);
        when(biographyManagerReadOnly.getBiography("0000-0000-0000-0002")).thenReturn(someoneElses);
        // Refused per biography rather than with a blanket matcher: refusing every
        // biography would also refuse the one this token may read, and the first
        // half of the test would pass for the wrong reason.
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter("0000-0000-0000-0002",
                someoneElses, ScopePathType.ORCID_BIO_READ_LIMITED);

        Response r = serviceDelegator.viewBiography(ORCID);
        assertNotNull(r);
        assertEquals(Biography.class.getName(), r.getEntity().getClass().getName());

        try {
            // Bio for 0000-0000-0000-0002 should be limited
            String otherOrcid = "0000-0000-0000-0002";
            r = serviceDelegator.viewBiography(otherOrcid);
            fail();
        } catch (OrcidUnauthorizedException e) {

        }
    }

    private Biography biography(Visibility visibility) {
        Biography biography = new Biography();
        biography.setContent("Biography for 0000-0000-0000-0003");
        biography.setVisibility(visibility);
        biography.setCreatedDate(createdDate());
        biography.setLastModifiedDate(lastModified());
        return biography;
    }
}
