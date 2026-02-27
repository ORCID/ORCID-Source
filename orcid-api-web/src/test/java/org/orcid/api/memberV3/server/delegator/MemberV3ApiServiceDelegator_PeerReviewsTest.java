package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_PeerReviewsTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPeerReviewsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReviews(ORCID);
    }

    @Test
    public void testViewPeerReviewReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        PeerReview peerReview = new PeerReview();
        peerReview.setPutCode(27L);
        when(peerReviewManagerReadOnly.getPeerReview(eq(ORCID), eq(27L))).thenReturn(peerReview);
        Response r = serviceDelegator.viewPeerReview(ORCID, 27L);
        PeerReview element = (PeerReview) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/peer-review/27", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPeerReviewsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        List<PeerReviewSummary> summaries = new ArrayList<>();
        when(peerReviewManagerReadOnly.getPeerReviewSummaryList(eq(ORCID))).thenReturn(summaries);
        when(peerReviewManager.groupPeerReviews(eq(summaries), anyBoolean())).thenReturn(new PeerReviews());
        
        Response r = serviceDelegator.viewPeerReviews(ORCID);
        PeerReviews element = (PeerReviews) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/peer-reviews", element.getPath());
    }
}
