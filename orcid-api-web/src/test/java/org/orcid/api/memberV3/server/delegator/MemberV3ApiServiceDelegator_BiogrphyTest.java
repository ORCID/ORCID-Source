package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidNoBioException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Biography;

/**
 * Mocked boundary tests for the biography endpoint of the member V3 API.
 *
 * <p>
 * Whether a caller may see a limited biography is the security manager's
 * decision and is proved by orcid-core's {@code OrcidSecurityManager_PersonTest};
 * what the delegator owes is to look the biography up, refuse with
 * {@code OrcidNoBioException} when there is none, hand it to the security
 * manager with {@code ORCID_BIO_READ_LIMITED}, stamp the path and return it.
 */
public class MemberV3ApiServiceDelegator_BiogrphyTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final String OTHER_ORCID = "0000-0000-0000-0002";

    private Biography biography(String content, Visibility visibility) {
        Biography biography = new Biography();
        biography.setContent(content);
        biography.setVisibility(visibility);
        biography.setLastModifiedDate(lastModified());
        biography.setCreatedDate(created());
        return biography;
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewBiographyWrongToken() {
        when(biographyManagerReadOnly.getBiography(ORCID)).thenReturn(biography("Biography for 0000-0000-0000-0003", Visibility.PUBLIC));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID),
                any(Biography.class), eq(ScopePathType.ORCID_BIO_READ_LIMITED));

        serviceDelegator.viewBiography(ORCID);
    }

    @Test
    public void testViewBiographyReadPublic() {
        Biography stored = biography("Biography for 0000-0000-0000-0003", Visibility.PUBLIC);
        when(biographyManagerReadOnly.getBiography(ORCID)).thenReturn(stored);

        Response r = serviceDelegator.viewBiography(ORCID);
        Biography element = (Biography) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/biography", element.getPath());
        assertEquals("Biography for 0000-0000-0000-0003", element.getContent());
        assertEquals(Visibility.PUBLIC, element.getVisibility());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test
    public void testReadPublicScope_Biography() {
        when(biographyManagerReadOnly.getBiography(ORCID)).thenReturn(biography("Biography for 0000-0000-0000-0003", Visibility.PUBLIC));
        // The other record's biography is limited, so the security manager
        // refuses; the delegator must let that out unwrapped.
        Biography limited = biography("Biography for 0000-0000-0000-0002", Visibility.LIMITED);
        when(biographyManagerReadOnly.getBiography(OTHER_ORCID)).thenReturn(limited);
        doThrow(new OrcidUnauthorizedException("The client application is not authorized")).when(orcidSecurityManager).checkAndFilter(OTHER_ORCID, limited,
                ScopePathType.ORCID_BIO_READ_LIMITED);

        Response r = serviceDelegator.viewBiography(ORCID);
        assertNotNull(r);
        assertEquals(Biography.class.getName(), r.getEntity().getClass().getName());

        try {
            // Bio for 0000-0000-0000-0002 should be limited
            r = serviceDelegator.viewBiography(OTHER_ORCID);
            fail();
        } catch (OrcidUnauthorizedException e) {

        }
    }

    /**
     * Added with the conversion: a record with no biography must be refused
     * before the security manager is asked, which is the one branch of
     * {@code viewBiography} the delegator owns outright.
     */
    @Test(expected = OrcidNoBioException.class)
    public void testViewBiographyNoBiography() {
        when(biographyManagerReadOnly.getBiography(ORCID)).thenReturn(null);

        serviceDelegator.viewBiography(ORCID);
    }
}
