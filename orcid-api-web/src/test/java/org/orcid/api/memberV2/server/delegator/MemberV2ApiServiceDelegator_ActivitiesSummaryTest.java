package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.api.common.util.ActivityUtils;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.common_v2.TranslatedTitle;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.test.helper.Utils;
import org.orcid.utils.DateUtils;

/**
 * The activities endpoint of the member v2 delegator, on mocks.
 *
 * <p>
 * {@code viewActivities} fetches the summary, hands it to
 * {@code checkAndFilter(String, ActivitiesSummary)}, cleans empty fields, sets a
 * path on the summary and on every activity in it, recomputes the last-modified
 * dates and resolves the source names. All of that is asserted below.
 *
 * <p>
 * Which sections of the summary survive a given scope is not, and cannot be:
 * that overload of {@code checkAndFilter} removes activities from the object it
 * is given and returns void, so against a mock every section always survives.
 * The five {@code testViewActitivies_*ReadLimited_NoSource} cases below
 * therefore assert the shape of the summary the security manager would have left
 * behind, and the scope-by-scope rules they used to prove live in
 * {@code OrcidSecurityManager_ActivitiesSummaryTest}.
 */
public class MemberV2ApiServiceDelegator_ActivitiesSummaryTest extends MemberV2ApiServiceDelegatorMockBase {

    @Test
    public void testViewActivitiesReadPublic() {
        ActivitiesSummary summary = activitiesSummary();
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID)).thenReturn(summary);

        Response r = serviceDelegator.viewActivities(ORCID);

        ActivitiesSummary as = (ActivitiesSummary) r.getEntity();
        assertNotNull(as);
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, summary);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewActivitiesWrongToken() {
        ActivitiesSummary summary = activitiesSummary();
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID)).thenReturn(summary);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, summary);

        try {
            serviceDelegator.viewActivities(ORCID);
        } finally {
            assertNull("nothing must be decorated once the guard has refused", summary.getPath());
        }
    }

    @Test
    public void testReadPublicScope_Activities() {
        ActivitiesSummary summary = activitiesSummary();
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID)).thenReturn(summary);

        Response r = serviceDelegator.viewActivities(ORCID);

        ActivitiesSummary as = (ActivitiesSummary) r.getEntity();
        assertActivitiesDecorated(as);
        verify(orcidSecurityManager).checkAndFilter(ORCID, summary);
    }

    @Test
    public void testViewActitivies() {
        ActivitiesSummary summary = activitiesSummary();
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID)).thenReturn(summary);

        Response response = serviceDelegator.viewActivities(ORCID);

        assertNotNull(response);
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertActivitiesDecorated(as);
        // the source name of every activity is resolved through SourceUtils
        assertEquals(CLIENT_1_NAME, as.getEducations().getSummaries().get(0).getSource().getSourceName().getContent());
        assertEquals(CLIENT_1_NAME, as.getEmployments().getSummaries().get(0).getSource().getSourceName().getContent());
        assertEquals(CLIENT_1_NAME, as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getSource().getSourceName().getContent());
        assertEquals(CLIENT_1_NAME, as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getSource().getSourceName().getContent());
        assertEquals(CLIENT_1_NAME, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, summary);
    }

    @Test
    public void testCleanEmptyFieldsOnActivities() {
        LastModifiedDate lmd = new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis()));
        Works works = new Works();
        works.setLastModifiedDate(lmd);
        WorkGroup group = new WorkGroup();
        group.setLastModifiedDate(lmd);
        for (int i = 0; i < 5; i++) {
            WorkSummary summary = new WorkSummary();
            summary.setLastModifiedDate(lmd);
            WorkTitle title = new WorkTitle();
            title.setTitle(new Title("Work " + i));
            title.setTranslatedTitle(new TranslatedTitle("", ""));
            summary.setTitle(title);
            group.getWorkSummary().add(summary);
        }
        works.getWorkGroup().add(group);
        ActivitiesSummary as = new ActivitiesSummary();
        as.setWorks(works);

        ActivityUtils.cleanEmptyFields(as);

        assertNotNull(as);
        assertNotNull(as.getWorks());
        Utils.verifyLastModified(as.getWorks().getLastModifiedDate());
        assertNotNull(as.getWorks().getWorkGroup());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertNotNull(as.getWorks().getWorkGroup().get(0).getWorkSummary());
        Utils.verifyLastModified(as.getWorks().getWorkGroup().get(0).getLastModifiedDate());
        assertEquals(5, as.getWorks().getWorkGroup().get(0).getWorkSummary().size());
        for (WorkSummary summary : as.getWorks().getWorkGroup().get(0).getWorkSummary()) {
            Utils.verifyLastModified(summary.getLastModifiedDate());
            assertNotNull(summary.getTitle());
            assertNotNull(summary.getTitle().getTitle());
            assertTrue(summary.getTitle().getTitle().getContent().startsWith("Work "));
            assertNull(summary.getTitle().getTranslatedTitle());
        }
    }

    @Test
    public void testViewActitivies_AffiliationsReadLimited_NoSource() {
        // /affiliations/read-limited alone: the guard keeps the affiliations and
        // empties everything else. Modelled, not proved -- see the class comment.
        ActivitiesSummary summary = activitiesSummary();
        summary.getWorks().getWorkGroup().clear();
        summary.getFundings().getFundingGroup().clear();
        summary.getPeerReviews().getPeerReviewGroup().clear();
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID)).thenReturn(summary);

        Response response = serviceDelegator.viewActivities(ORCID);

        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());
        assertEquals(1, as.getEducations().getSummaries().size());
        assertEquals("/0000-0000-0000-0003/education/20", as.getEducations().getSummaries().get(0).getPath());
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertEquals("/0000-0000-0000-0003/employment/17", as.getEmployments().getSummaries().get(0).getPath());
        assertTrue(as.getWorks().getWorkGroup().isEmpty());
        assertTrue(as.getFundings().getFundingGroup().isEmpty());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().isEmpty());
        verify(orcidSecurityManager).checkAndFilter(ORCID, summary);
    }

    @Test
    public void testViewActitivies_FundingReadLimited_NoSource() {
        ActivitiesSummary summary = activitiesSummary();
        summary.getEducations().getSummaries().clear();
        summary.getEmployments().getSummaries().clear();
        summary.getWorks().getWorkGroup().clear();
        summary.getPeerReviews().getPeerReviewGroup().clear();
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID)).thenReturn(summary);

        Response response = serviceDelegator.viewActivities(ORCID);

        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals("/0000-0000-0000-0003/funding/10", as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPath());
        assertTrue(as.getEducations().getSummaries().isEmpty());
        assertTrue(as.getEmployments().getSummaries().isEmpty());
        assertTrue(as.getWorks().getWorkGroup().isEmpty());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().isEmpty());
        verify(orcidSecurityManager).checkAndFilter(ORCID, summary);
    }

    @Test
    public void testViewActitivies_PeerReviewReadLimited_NoSource() {
        ActivitiesSummary summary = activitiesSummary();
        summary.getEducations().getSummaries().clear();
        summary.getEmployments().getSummaries().clear();
        summary.getWorks().getWorkGroup().clear();
        summary.getFundings().getFundingGroup().clear();
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID)).thenReturn(summary);

        Response response = serviceDelegator.viewActivities(ORCID);

        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals("/0000-0000-0000-0003/peer-review/9", as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPath());
        assertTrue(as.getEducations().getSummaries().isEmpty());
        assertTrue(as.getEmployments().getSummaries().isEmpty());
        assertTrue(as.getWorks().getWorkGroup().isEmpty());
        assertTrue(as.getFundings().getFundingGroup().isEmpty());
        verify(orcidSecurityManager).checkAndFilter(ORCID, summary);
    }

    @Test
    public void testViewActitivies_WorksReadLimited_NoSource() {
        ActivitiesSummary summary = activitiesSummary();
        summary.getEducations().getSummaries().clear();
        summary.getEmployments().getSummaries().clear();
        summary.getFundings().getFundingGroup().clear();
        summary.getPeerReviews().getPeerReviewGroup().clear();
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID)).thenReturn(summary);

        Response response = serviceDelegator.viewActivities(ORCID);

        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals("/0000-0000-0000-0003/work/11", as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPath());
        assertTrue(as.getEducations().getSummaries().isEmpty());
        assertTrue(as.getEmployments().getSummaries().isEmpty());
        assertTrue(as.getFundings().getFundingGroup().isEmpty());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().isEmpty());
        verify(orcidSecurityManager).checkAndFilter(ORCID, summary);
    }

    @Test
    public void testViewActitivies_NoReadLimited_NoSource() {
        // No read-limited scope and not the source: the guard empties every
        // section. The delegator must still return a well-formed, decorated
        // summary rather than fail on the empty collections.
        ActivitiesSummary summary = activitiesSummary();
        summary.getEducations().getSummaries().clear();
        summary.getEmployments().getSummaries().clear();
        summary.getWorks().getWorkGroup().clear();
        summary.getFundings().getFundingGroup().clear();
        summary.getPeerReviews().getPeerReviewGroup().clear();
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID)).thenReturn(summary);

        Response response = serviceDelegator.viewActivities(ORCID);

        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());
        assertTrue(as.getEducations().getSummaries().isEmpty());
        assertTrue(as.getEmployments().getSummaries().isEmpty());
        assertTrue(as.getWorks().getWorkGroup().isEmpty());
        assertTrue(as.getFundings().getFundingGroup().isEmpty());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().isEmpty());
        verify(orcidSecurityManager).checkAndFilter(ORCID, summary);
    }

    // ------------------------------------------------------------- helpers

    /**
     * Everything {@code viewActivities} adds to the summary it was handed: a path
     * on the container, a path on every section, a path on every activity, and a
     * recomputed last-modified date.
     */
    private void assertActivitiesDecorated(ActivitiesSummary as) {
        assertNotNull(as);
        assertNotNull(as.getPath());
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());
        Utils.verifyLastModified(as.getLastModifiedDate());

        assertNotNull(as.getEducations());
        assertEquals("/0000-0000-0000-0003/educations", as.getEducations().getPath());
        assertEquals(1, as.getEducations().getSummaries().size());
        assertEquals("/0000-0000-0000-0003/education/20", as.getEducations().getSummaries().get(0).getPath());

        assertNotNull(as.getEmployments());
        assertEquals("/0000-0000-0000-0003/employments", as.getEmployments().getPath());
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertEquals("/0000-0000-0000-0003/employment/17", as.getEmployments().getSummaries().get(0).getPath());

        assertNotNull(as.getFundings());
        assertEquals("/0000-0000-0000-0003/fundings", as.getFundings().getPath());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals("/0000-0000-0000-0003/funding/10", as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPath());

        assertNotNull(as.getPeerReviews());
        assertEquals("/0000-0000-0000-0003/peer-reviews", as.getPeerReviews().getPath());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals("/0000-0000-0000-0003/peer-review/9", as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPath());

        assertNotNull(as.getWorks());
        assertEquals("/0000-0000-0000-0003/works", as.getWorks().getPath());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals("/0000-0000-0000-0003/work/11", as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPath());
    }
}
