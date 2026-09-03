package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.orcid.api.common.util.v3.ActivityUtils;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.ExceedMaxNumberOfPutCodesException;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.common.CitationType;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.v3.release.common.Contributor;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Subtitle;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.TranslatedTitle;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.Citation;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.WorkContributors;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.test.helper.v3.Utils;
import org.orcid.utils.DateUtils;

/**
 * Mocked boundary tests for the work endpoints of the member V3 API.
 *
 * <p>
 * The bulk endpoints are where mocking costs the most: {@code checkAndFilter}
 * replaces a work the caller may not read with an {@code OrcidError} in place,
 * and with the security manager mocked that substitution does not happen. The
 * "index 3 is an OrcidError" assertions therefore no longer prove the filter --
 * they prove the delegator passes through whatever the filter left, and set the
 * path on the elements that are still works. The filter itself is proved by
 * orcid-core's {@code OrcidSecurityManager_WorkBulkTest}.
 */
public class MemberV3ApiServiceDelegator_WorksTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.ORCID_WORKS_READ_LIMITED;

    private static final String USER_4443 = "4444-4444-4444-4443";
    private static final String USER_4445 = "4444-4444-4444-4445";
    private static final String USER_4446 = "4444-4444-4444-4446";
    private static final String USER_4447 = "4444-4444-4444-4447";
    private static final String USER_4499 = "4444-4444-4444-4499";

    /**
     * The limit is enforced inside {@code WorkManagerReadOnly.findWorkBulk}; the
     * delegator does not read the property at all, so this is only the size the
     * test builds a request of.
     */
    private static final long BULK_READ_SIZE = 100;

    private Work work(long putCode, String title, WorkType type, Visibility visibility, Source source) {
        Work work = new Work();
        work.setPutCode(putCode);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        work.setWorkTitle(workTitle);
        work.setWorkType(type);
        work.setVisibility(visibility);
        work.setSource(source);
        work.setLastModifiedDate(lastModified());
        work.setWorkExternalIdentifiers(externalIds(String.valueOf(putCode)));
        return work;
    }

    private WorkSummary summary(long putCode, String title, Visibility visibility, Source source) {
        WorkSummary summary = new WorkSummary();
        summary.setPutCode(putCode);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        summary.setTitle(workTitle);
        summary.setType(WorkType.JOURNAL_ARTICLE);
        summary.setVisibility(visibility);
        summary.setSource(source);
        summary.setLastModifiedDate(lastModified());
        return summary;
    }

    private ExternalIDs externalIds(String value) {
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType(WorkExternalIdentifierType.AGR.value());
        extId.setUrl(new Url("http://thisIsANewUrl.com"));
        extId.setValue(value);
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(extId);
        return extIds;
    }

    private WorkGroup group(String externalIdValue, WorkSummary summary) {
        WorkGroup group = new WorkGroup();
        group.getIdentifiers().getExternalIdentifier().add(externalIds(externalIdValue).getExternalIdentifier().get(0));
        group.getWorkSummary().add(summary);
        return group;
    }

    private Works works(WorkGroup... groups) {
        Works works = new Works();
        works.getWorkGroup().addAll(Arrays.asList(groups));
        return works;
    }

    private ActivitiesSummary activitiesWithWorks(Works works) {
        ActivitiesSummary activities = emptyActivitiesSummary();
        activities.setWorks(works);
        return activities;
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewWorkWrongToken() {
        when(workManagerReadOnly.getWork(ORCID, 11L)).thenReturn(work(11L, "PUBLIC", WorkType.JOURNAL_ARTICLE, Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Work.class), eq(SCOPE));

        serviceDelegator.viewWork(ORCID, 11L);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewWorkSummaryWrongToken() {
        when(workManagerReadOnly.getWorkSummary(ORCID, 11L)).thenReturn(summary(11L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(WorkSummary.class), eq(SCOPE));

        serviceDelegator.viewWorkSummary(ORCID, 11L);
    }

    @Test
    public void testCreateBulkWorksWithBlankTitles() {
        Long time = System.currentTimeMillis();
        WorkBulk bulk = bulkOfFive(time, true);

        // The blank title is rejected inside WorkManager, which replaces that
        // element with an OrcidError; ActivityValidatorTest proves that rule.
        WorkBulk stored = new WorkBulk();
        stored.getBulk().add(new OrcidError());
        for (int i = 1; i < 5; i++) {
            stored.getBulk().add(work(i, "title " + i, WorkType.BOOK, Visibility.PUBLIC, clientSource(CLIENT_1)));
        }
        when(workManager.createWorks(eq(ORCID), any(WorkBulk.class))).thenReturn(stored);

        Response response = serviceDelegator.createWorks(ORCID, bulk);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        bulk = (WorkBulk) response.getEntity();
        assertNotNull(bulk);
        assertEquals(5, bulk.getBulk().size());

        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.ORCID_WORKS_CREATE, ScopePathType.ORCID_WORKS_UPDATE);
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                assertTrue(bulk.getBulk().get(i) instanceof OrcidError);
            } else {
                assertTrue(bulk.getBulk().get(i) instanceof Work);
                serviceDelegator.deleteWork(ORCID, ((Work) bulk.getBulk().get(i)).getPutCode());
            }
        }
        verify(workManager).checkSourceAndRemoveWork(ORCID, 4L);
    }

    private WorkBulk bulkOfFive(Long time, boolean firstTitleBlank) {
        WorkBulk bulk = new WorkBulk();
        for (int i = 0; i < 5; i++) {
            Work work = new Work();
            WorkTitle title = new WorkTitle();
            title.setTitle(firstTitleBlank && i == 0 ? new Title(" ") : new Title("Bulk work " + i + " " + time));
            work.setWorkTitle(title);

            ExternalIDs extIds = new ExternalIDs();
            ExternalID extId = new ExternalID();
            extId.setRelationship(Relationship.SELF);
            extId.setType("doi");
            extId.setUrl(new Url("http://doi/" + i + "/" + time));
            extId.setValue("doi-" + i + "-" + time);
            extIds.getExternalIdentifier().add(extId);
            work.setWorkExternalIdentifiers(extIds);

            work.setWorkType(WorkType.BOOK);
            work.setSource(clientSource(CLIENT_2));
            bulk.getBulk().add(work);
        }
        return bulk;
    }

    @Test
    public void testViewWorkReadPublic() {
        Work stored = work(11L, "PUBLIC", WorkType.JOURNAL_ARTICLE, Visibility.PUBLIC, clientSource(CLIENT_1));
        WorkContributors contributors = new WorkContributors();
        Contributor contributor = new Contributor();
        contributor.setContributorOrcid(new ContributorOrcid("0000-0000-0000-0000"));
        contributors.getContributor().add(contributor);
        stored.setWorkContributors(contributors);
        when(workManagerReadOnly.getWork(ORCID, 11L)).thenReturn(stored);

        Response r = serviceDelegator.viewWork(ORCID, 11L);
        Work work = (Work) r.getEntity();
        assertNotNull(work);
        assertEquals("/0000-0000-0000-0003/work/11", work.getPath());
        assertNotNull(work.getLastModifiedDate());
        assertNotNull(work.getLastModifiedDate().getValue());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("PUBLIC", work.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(11), work.getPutCode());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals(CLIENT_1, work.getSource().retrieveSourcePath());
        assertEquals(CLIENT_1_NAME, work.getSource().getSourceName().getContent());
        assertNotNull(work.getWorkContributors());
        assertEquals(1, work.getWorkContributors().getContributor().size());
        assertEquals("0000-0000-0000-0000", work.getWorkContributors().getContributor().get(0).getContributorOrcid().getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, work, SCOPE);
    }

    @Test
    public void testViewWorkSummaryReadPublic() {
        WorkSummary stored = summary(11L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(workManagerReadOnly.getWorkSummary(ORCID, 11L)).thenReturn(stored);

        Response r = serviceDelegator.viewWorkSummary(ORCID, 11L);
        WorkSummary element = (WorkSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/work/11", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPublicWork() {
        when(workManagerReadOnly.getWork(USER_4446, 5L))
                .thenReturn(work(5L, "Journal article A", WorkType.JOURNAL_ARTICLE, Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewWork(USER_4446, 5L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertEquals("/4444-4444-4444-4446/work/5", work.getPath());
        Utils.verifyLastModified(work.getLastModifiedDate());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Journal article A", work.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(5), work.getPutCode());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals(Visibility.PUBLIC.value(), work.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(USER_4446, work, SCOPE);
    }

    @Test
    public void testViewLimitedWork() {
        when(workManagerReadOnly.getWork(USER_4446, 6L))
                .thenReturn(work(6L, "Journal article B", WorkType.JOURNAL_ARTICLE, Visibility.LIMITED, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewWork(USER_4446, 6L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertEquals("/4444-4444-4444-4446/work/6", work.getPath());
        Utils.verifyLastModified(work.getLastModifiedDate());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Journal article B", work.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(6), work.getPutCode());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals(Visibility.LIMITED.value(), work.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(USER_4446, work, SCOPE);
    }

    @Test
    public void testViewPrivateWork() {
        when(workManagerReadOnly.getWork(USER_4446, 7L))
                .thenReturn(work(7L, "Journal article C", WorkType.JOURNAL_ARTICLE, Visibility.PRIVATE, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewWork(USER_4446, 7L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        Utils.verifyLastModified(work.getLastModifiedDate());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Journal article C", work.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(7), work.getPutCode());
        assertEquals("/4444-4444-4444-4446/work/7", work.getPath());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals(Visibility.PRIVATE.value(), work.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(USER_4446, work, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateWorkYouAreNotTheSourceOf() {
        Work stored = work(8L, "Journal article D", WorkType.JOURNAL_ARTICLE, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(workManagerReadOnly.getWork(USER_4446, 8L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(USER_4446, stored, SCOPE);

        serviceDelegator.viewWork(USER_4446, 8L);
        fail();
    }

    /**
     * The rule this proves -- that work 5 cannot be read through record 4443 --
     * lives in a SQL WHERE clause ({@code WorkDaoImpl.getWork}), so with a mocked
     * manager only the pass-through survives here. The predicate itself is proved
     * by MemberV3ApiServiceDelegatorDatabaseRulesTest in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewWorkThatDontBelongToTheUser() {
        when(workManagerReadOnly.getWork(USER_4443, 5L)).thenThrow(new NoResultException());

        serviceDelegator.viewWork(USER_4443, 5L);
        fail();
    }

    @Test
    public void viewWorksTest() {
        WorkSummary pub = summary(11L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1));
        WorkSummary limited = summary(12L, "LIMITED", Visibility.LIMITED, clientSource(CLIENT_1));
        WorkSummary priv = summary(13L, "PRIVATE", Visibility.PRIVATE, clientSource(CLIENT_1));
        WorkSummary selfLimited = summary(14L, "SELF LIMITED", Visibility.LIMITED, userSource(ORCID));
        List<WorkSummary> summaries = Arrays.asList(pub, limited, priv, selfLimited);
        when(workManagerReadOnly.getWorksSummaryList(ORCID)).thenReturn(summaries);
        when(workManager.groupWorks(summaries, false)).thenReturn(works(group("1", pub), group("2", limited), group("3", priv), group("4", selfLimited)));

        Response r = serviceDelegator.viewWorks(ORCID);
        assertNotNull(r);
        Works works = (Works) r.getEntity();
        assertNotNull(works);
        assertEquals("/0000-0000-0000-0003/works", works.getPath());
        Utils.verifyLastModified(works.getLastModifiedDate());
        assertNotNull(works.getWorkGroup());
        assertEquals(4, works.getWorkGroup().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false;

        for (WorkGroup workGroup : works.getWorkGroup()) {
            Utils.verifyLastModified(workGroup.getLastModifiedDate());
            assertNotNull(workGroup.getIdentifiers());
            assertNotNull(workGroup.getIdentifiers().getExternalIdentifier());
            assertEquals(1, workGroup.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(workGroup.getWorkSummary());
            assertEquals(1, workGroup.getWorkSummary().size());
            WorkSummary summary = workGroup.getWorkSummary().get(0);
            Utils.verifyLastModified(summary.getLastModifiedDate());
            assertNotNull(summary.getTitle());
            assertNotNull(summary.getTitle().getTitle());
            switch (workGroup.getIdentifiers().getExternalIdentifier().get(0).getValue()) {
            case "1":
                assertEquals("PUBLIC", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(11), summary.getPutCode());
                assertEquals("/0000-0000-0000-0003/work/11", summary.getPath());
                found1 = true;
                break;
            case "2":
                assertEquals("LIMITED", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(12), summary.getPutCode());
                found2 = true;
                break;
            case "3":
                assertEquals("PRIVATE", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(13), summary.getPutCode());
                found3 = true;
                break;
            case "4":
                assertEquals("SELF LIMITED", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(14), summary.getPutCode());
                found4 = true;
                break;
            default:
                fail("Invalid external id found: " + workGroup.getIdentifiers().getExternalIdentifier().get(0).getValue());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testReadPublicScope_Works() {
        when(workManagerReadOnly.getWork(ORCID, 11L)).thenReturn(work(11L, "PUBLIC", WorkType.JOURNAL_ARTICLE, Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(workManagerReadOnly.getWorkSummary(ORCID, 11L)).thenReturn(summary(11L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(workManagerReadOnly.getWork(ORCID, 12L)).thenReturn(work(12L, "LIMITED", WorkType.JOURNAL_ARTICLE, Visibility.LIMITED, clientSource(CLIENT_1)));
        when(workManagerReadOnly.getWorkSummary(ORCID, 12L)).thenReturn(summary(12L, "LIMITED", Visibility.LIMITED, clientSource(CLIENT_1)));
        when(workManagerReadOnly.getWork(ORCID, 13L)).thenReturn(work(13L, "PRIVATE", WorkType.JOURNAL_ARTICLE, Visibility.PRIVATE, clientSource(CLIENT_1)));
        when(workManagerReadOnly.getWorkSummary(ORCID, 13L)).thenReturn(summary(13L, "PRIVATE", Visibility.PRIVATE, clientSource(CLIENT_1)));

        Work limitedOtherSource = work(14L, "SELF LIMITED", WorkType.JOURNAL_ARTICLE, Visibility.LIMITED, userSource(ORCID));
        WorkSummary limitedOtherSourceSummary = summary(14L, "SELF LIMITED", Visibility.LIMITED, userSource(ORCID));
        Work privateOtherSource = work(15L, "SELF PRIVATE", WorkType.JOURNAL_ARTICLE, Visibility.PRIVATE, userSource(ORCID));
        when(workManagerReadOnly.getWork(ORCID, 14L)).thenReturn(limitedOtherSource);
        when(workManagerReadOnly.getWorkSummary(ORCID, 14L)).thenReturn(limitedOtherSourceSummary);
        when(workManagerReadOnly.getWork(ORCID, 15L)).thenReturn(privateOtherSource);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, limitedOtherSource, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, limitedOtherSourceSummary, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, privateOtherSource, SCOPE);

        Response r = serviceDelegator.viewWork(ORCID, 11L);
        assertNotNull(r);
        assertEquals(Work.class.getName(), r.getEntity().getClass().getName());
        r = serviceDelegator.viewWorkSummary(ORCID, 11L);
        assertNotNull(r);
        assertEquals(WorkSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewWork(ORCID, 12L);
        serviceDelegator.viewWorkSummary(ORCID, 12L);
        // Limited that am not the source of should fail
        try {
            serviceDelegator.viewWork(ORCID, 14L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
        try {
            serviceDelegator.viewWorkSummary(ORCID, 14L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }

        // Private that am the source of should work
        serviceDelegator.viewWork(ORCID, 13L);
        serviceDelegator.viewWorkSummary(ORCID, 13L);
        // Private that am not the source of should fail
        try {
            serviceDelegator.viewWork(ORCID, 15L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
        try {
            serviceDelegator.viewWork(ORCID, 15L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Touches no collaborator: {@code ActivityUtils.cleanEmptyFields} is static
     * and runs for real, so this stays exactly as it was.
     */
    @Test
    public void testCleanEmptyFieldsOnWorks() {
        LastModifiedDate lmd = new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis()));
        Work work = new Work();
        work.setLastModifiedDate(lmd);
        work.setWorkCitation(new Citation("", CitationType.FORMATTED_UNSPECIFIED));
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("My Work"));
        title.setSubtitle(new Subtitle("My subtitle"));
        title.setTranslatedTitle(new TranslatedTitle(""));
        work.setWorkTitle(title);

        ActivityUtils.cleanEmptyFields(work);

        assertNotNull(work);
        Utils.verifyLastModified(work.getLastModifiedDate());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertNotNull(work.getWorkTitle().getSubtitle());
        assertEquals("My Work", work.getWorkTitle().getTitle().getContent());
        assertEquals("My subtitle", work.getWorkTitle().getSubtitle().getContent());

        assertNull(work.getWorkCitation());
        assertNull(work.getWorkTitle().getTranslatedTitle());
    }

    @Test
    public void testAddWork() {
        String title = "work # 1 " + System.currentTimeMillis();
        WorkSummary existing = summary(1L, "A Book With Contributors JSON", Visibility.PUBLIC, clientSource(CLIENT_1));
        WorkSummary added = summary(1000L, title, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(eq(USER_4445), eq(false))).thenReturn(activitiesWithWorks(works(group("1", existing))))
                .thenReturn(activitiesWithWorks(works(group("1", existing), group("2", added))));
        when(workManager.createWork(eq(USER_4445), any(Work.class), eq(true)))
                .thenReturn(work(1000L, title, WorkType.BOOK, Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewActivities(USER_4445);
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getWorks());
        assertNotNull(summary.getWorks().getWorkGroup());
        assertEquals(1, summary.getWorks().getWorkGroup().size());
        Utils.verifyLastModified(summary.getWorks().getLastModifiedDate());
        assertEquals(1, summary.getWorks().getWorkGroup().get(0).getWorkSummary().size());

        Work work = Utils.getWork(title);

        response = serviceDelegator.createWork(USER_4445, work);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);
        assertEquals(Long.valueOf(1000L), putCode);
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4445, ScopePathType.ORCID_WORKS_CREATE, ScopePathType.ORCID_WORKS_UPDATE);

        response = serviceDelegator.viewActivities(USER_4445);
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getWorks());
        assertNotNull(summary.getWorks().getWorkGroup());
        assertEquals(2, summary.getWorks().getWorkGroup().size());

        boolean haveOld = false;
        boolean haveNew = false;

        for (WorkGroup group : summary.getWorks().getWorkGroup()) {
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertNotNull(group.getWorkSummary());
            WorkSummary workSummary = group.getWorkSummary().get(0);
            Utils.verifyLastModified(workSummary.getLastModifiedDate());
            assertNotNull(workSummary.getTitle());
            assertNotNull(workSummary.getTitle().getTitle());
            if ("A Book With Contributors JSON".equals(workSummary.getTitle().getTitle().getContent())) {
                haveOld = true;
            } else if (title.equals(workSummary.getTitle().getTitle().getContent())) {
                haveNew = true;
            }
        }
        assertTrue(haveOld);
        assertTrue(haveNew);

        // Delete them
        serviceDelegator.deleteWork(USER_4445, putCode);
        verify(workManager).checkSourceAndRemoveWork(USER_4445, 1000L);
    }

    @Test
    public void testCreateWorksWithBulkAllOK() {
        Long time = System.currentTimeMillis();
        WorkBulk bulk = bulkOfFive(time, false);

        WorkBulk stored = new WorkBulk();
        List<BulkElement> elements = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Work w = work(i + 1, "Bulk work " + i + " " + time, WorkType.BOOK, Visibility.PUBLIC, clientSource(CLIENT_1));
            w.setWorkExternalIdentifiers(doiExternalIds("doi-" + i + "-" + time));
            elements.add(w);
            when(workManagerReadOnly.getWork(ORCID, (long) (i + 1))).thenReturn(w);
        }
        stored.setBulk(elements);
        when(workManager.createWorks(eq(ORCID), any(WorkBulk.class))).thenReturn(stored);

        Response response = serviceDelegator.createWorks(ORCID, bulk);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        bulk = (WorkBulk) response.getEntity();
        assertNotNull(bulk);
        assertEquals(5, bulk.getBulk().size());

        // A client supplied source must never reach the manager, on any element.
        ArgumentCaptor<WorkBulk> captor = ArgumentCaptor.forClass(WorkBulk.class);
        verify(workManager).createWorks(eq(ORCID), captor.capture());
        for (BulkElement element : captor.getValue().getBulk()) {
            assertNull(((Work) element).getSource());
        }

        for (int i = 0; i < 5; i++) {
            assertTrue(Work.class.isAssignableFrom(bulk.getBulk().get(i).getClass()));
            Work w = (Work) bulk.getBulk().get(i);
            Utils.verifyLastModified(w.getLastModifiedDate());
            assertNotNull(w.getPutCode());
            assertTrue(0L < w.getPutCode());
            assertEquals("Bulk work " + i + " " + time, w.getWorkTitle().getTitle().getContent());
            assertNotNull(w.getExternalIdentifiers().getExternalIdentifier());
            assertEquals("doi-" + i + "-" + time, w.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());

            Response r = serviceDelegator.viewWork(ORCID, w.getPutCode());
            assertNotNull(r);
            assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
            assertEquals("Bulk work " + i + " " + time, ((Work) r.getEntity()).getWorkTitle().getTitle().getContent());

            // Delete the work
            r = serviceDelegator.deleteWork(ORCID, w.getPutCode());
            assertNotNull(r);
            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
        }
    }

    private ExternalIDs doiExternalIds(String value) {
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://doi/" + value));
        extId.setValue(value);
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(extId);
        return extIds;
    }

    @Test
    public void testUpdateWork() {
        Work stored = work(1L, "A day in the life", WorkType.BOOK, Visibility.PUBLIC, clientSource(CLIENT_1));
        Work updated = work(1L, "Updated work title", WorkType.EDITED_BOOK, Visibility.PUBLIC, clientSource(CLIENT_1));
        Work rolledBack = work(1L, "A day in the life", WorkType.BOOK, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(workManagerReadOnly.getWork(USER_4443, 1L)).thenReturn(stored).thenReturn(updated);
        when(workManager.updateWork(eq(USER_4443), any(Work.class), eq(true))).thenReturn(updated).thenReturn(rolledBack);

        Response response = serviceDelegator.viewWork(USER_4443, 1L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        Utils.verifyLastModified(work.getLastModifiedDate());
        assertEquals(Long.valueOf(1), work.getPutCode());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("A day in the life", work.getWorkTitle().getTitle().getContent());
        assertEquals(WorkType.BOOK, work.getWorkType());
        assertEquals(Visibility.PUBLIC, work.getVisibility());

        work.setWorkType(WorkType.EDITED_BOOK);
        work.getWorkTitle().getTitle().setContent("Updated work title");

        response = serviceDelegator.updateWork(USER_4443, 1L, work);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4443, ScopePathType.ORCID_WORKS_UPDATE);

        response = serviceDelegator.viewWork(USER_4443, 1L);
        assertNotNull(response);
        work = (Work) response.getEntity();
        assertNotNull(work);
        Utils.verifyLastModified(work.getLastModifiedDate());
        assertEquals(Long.valueOf(1), work.getPutCode());
        assertEquals("Updated work title", work.getWorkTitle().getTitle().getContent());
        assertEquals(WorkType.EDITED_BOOK, work.getWorkType());

        // Rollback changes so we dont break other tests
        work.setWorkType(WorkType.BOOK);
        work.getWorkTitle().getTitle().setContent("A day in the life");
        response = serviceDelegator.updateWork(USER_4443, 1L, work);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("A day in the life", ((Work) response.getEntity()).getWorkTitle().getTitle().getContent());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateWorkYouAreNotTheSourceOf() {
        when(workManagerReadOnly.getWork(USER_4443, 2L))
                .thenReturn(work(2L, "Another day in the life", WorkType.BOOK, Visibility.PUBLIC, clientSource(CLIENT_2)));
        doThrow(new WrongSourceException(new HashMap<String, String>())).when(workManager).updateWork(eq(USER_4443), any(Work.class), eq(true));

        Response response = serviceDelegator.viewWork(USER_4443, 2L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        Utils.verifyLastModified(work.getLastModifiedDate());
        assertEquals(Long.valueOf(2), work.getPutCode());
        assertEquals("Another day in the life", work.getWorkTitle().getTitle().getContent());
        assertEquals(WorkType.BOOK, work.getWorkType());

        work.setWorkType(WorkType.EDITED_BOOK);
        work.getWorkTitle().getTitle().setContent("Updated work title");
        work.setWorkExternalIdentifiers(externalIds("ext-id-" + System.currentTimeMillis()));

        serviceDelegator.updateWork(USER_4443, 2L, work);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateWorkChangingVisibilityTest() {
        when(workManagerReadOnly.getWork(USER_4445, 3L)).thenReturn(work(3L, "LIMITED", WorkType.BOOK, Visibility.LIMITED, clientSource(CLIENT_1)));
        doThrow(new VisibilityMismatchException()).when(workManager).updateWork(eq(USER_4445), any(Work.class), eq(true));

        Response response = serviceDelegator.viewWork(USER_4445, 3L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertEquals(Visibility.LIMITED, work.getVisibility());

        work.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateWork(USER_4445, 3L, work);
        fail();
    }

    @Test
    public void testUpdateWorkLeavingVisibilityNullTest() {
        when(workManagerReadOnly.getWork(USER_4447, 10L)).thenReturn(work(10L, "PUBLIC", WorkType.BOOK, Visibility.PUBLIC, clientSource(CLIENT_1)));
        // Restoring the stored visibility is the manager's job and is proved
        // there; here the delegator must simply return what it produced.
        when(workManager.updateWork(eq(USER_4447), any(Work.class), eq(true)))
                .thenReturn(work(10L, "PUBLIC", WorkType.BOOK, Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewWork(USER_4447, 10L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertEquals(Visibility.PUBLIC, work.getVisibility());

        work.setVisibility(null);

        response = serviceDelegator.updateWork(USER_4447, 10L, work);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        work = (Work) response.getEntity();
        assertNotNull(work);
        Utils.verifyLastModified(work.getLastModifiedDate());
        assertEquals(Visibility.PUBLIC, work.getVisibility());
    }

    @Test
    public void testDeleteWork() {
        when(workManagerReadOnly.getWork(USER_4447, 9L)).thenReturn(work(9L, "PUBLIC", WorkType.BOOK, Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewWork(USER_4447, 9L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);

        response = serviceDelegator.deleteWork(USER_4447, 9L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4447, ScopePathType.ORCID_WORKS_UPDATE);
        verify(workManager).checkSourceAndRemoveWork(USER_4447, 9L);
    }

    @Test
    public void testAddWorkWithInvalidExtIdTypeFail() {
        Work work = Utils.getWork("work # 1 " + System.currentTimeMillis());
        // The external identifier type table is enforced by ExternalIDValidator
        // inside the manager and proved by ExternalIDValidatorTest; the
        // delegator's contract is to let the exception through untouched.
        doThrow(new ActivityIdentifierValidationException()).doReturn(work(1000L, "work # 1", WorkType.BOOK, Visibility.PUBLIC, clientSource(CLIENT_1)))
                .when(workManager).createWork(eq(USER_4499), any(Work.class), eq(true));

        try {
            work.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
            serviceDelegator.createWork(USER_4499, work);
            fail();
        } catch (ActivityIdentifierValidationException e) {

        } catch (Exception e) {
            fail();
        }

        // Assert that it could be created with a valid value
        work.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        Response response = serviceDelegator.createWork(USER_4499, work);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);

        // Delete it to roll back the test data
        response = serviceDelegator.deleteWork(USER_4499, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(workManager).checkSourceAndRemoveWork(USER_4499, 1000L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteWorkYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(new HashMap<String, String>())).when(workManager).checkSourceAndRemoveWork(USER_4446, 8L);

        serviceDelegator.deleteWork(USER_4446, 8L);
        fail();
    }

    @Test
    public void testViewBulkWorks() {
        when(profileEntityManager.orcidExists(ORCID)).thenReturn(true);
        // The fourth element is already an OrcidError because the security
        // manager replaced it; that substitution is proved by orcid-core's
        // OrcidSecurityManager_WorkBulkTest, not here.
        WorkBulk stored = new WorkBulk();
        stored.getBulk().add(work(11L, "PUBLIC", WorkType.JOURNAL_ARTICLE, Visibility.PUBLIC, clientSource(CLIENT_1)));
        stored.getBulk().add(work(12L, "LIMITED", WorkType.JOURNAL_ARTICLE, Visibility.LIMITED, clientSource(CLIENT_1)));
        stored.getBulk().add(work(13L, "PRIVATE", WorkType.JOURNAL_ARTICLE, Visibility.PRIVATE, clientSource(CLIENT_1)));
        stored.getBulk().add(new OrcidError());
        when(workManagerReadOnly.findWorkBulk(ORCID, "11,12,13,16")).thenReturn(stored);

        Response response = serviceDelegator.viewBulkWorks(ORCID, "11,12,13,16");
        WorkBulk workBulk = (WorkBulk) response.getEntity();
        assertNotNull(workBulk);
        assertNotNull(workBulk.getBulk());
        assertEquals(4, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof Work);
        assertTrue(workBulk.getBulk().get(1) instanceof Work);
        assertTrue(workBulk.getBulk().get(2) instanceof Work);
        assertTrue(workBulk.getBulk().get(3) instanceof OrcidError);
        assertEquals("/0000-0000-0000-0003/work/11", ((Work) workBulk.getBulk().get(0)).getPath());
        assertEquals("/0000-0000-0000-0003/work/12", ((Work) workBulk.getBulk().get(1)).getPath());
        assertEquals("/0000-0000-0000-0003/work/13", ((Work) workBulk.getBulk().get(2)).getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, workBulk, SCOPE);
    }

    @Test
    public void testViewBulkWorksWithBadPutCode() {
        when(profileEntityManager.orcidExists(ORCID)).thenReturn(true);
        WorkBulk stored = new WorkBulk();
        stored.getBulk().add(work(11L, "PUBLIC", WorkType.JOURNAL_ARTICLE, Visibility.PUBLIC, clientSource(CLIENT_1)));
        stored.getBulk().add(work(12L, "LIMITED", WorkType.JOURNAL_ARTICLE, Visibility.LIMITED, clientSource(CLIENT_1)));
        stored.getBulk().add(work(13L, "PRIVATE", WorkType.JOURNAL_ARTICLE, Visibility.PRIVATE, clientSource(CLIENT_1)));
        stored.getBulk().add(new OrcidError());
        when(workManagerReadOnly.findWorkBulk(ORCID, "11,12,13,9999")).thenReturn(stored);

        Response response = serviceDelegator.viewBulkWorks(ORCID, "11,12,13,9999");
        WorkBulk workBulk = (WorkBulk) response.getEntity();
        assertNotNull(workBulk);
        assertNotNull(workBulk.getBulk());
        assertEquals(4, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof Work);
        assertTrue(workBulk.getBulk().get(1) instanceof Work);
        assertTrue(workBulk.getBulk().get(2) instanceof Work);
        assertTrue(workBulk.getBulk().get(3) instanceof OrcidError); // bad put code
    }

    @Test(expected = OrcidNoResultException.class)
    public void testViewBulkWorksWithBadOrcid() {
        when(profileEntityManager.orcidExists("non-existent")).thenReturn(false);

        serviceDelegator.viewBulkWorks("non-existent", "11,12,13");
    }

    @Test(expected = ExceedMaxNumberOfPutCodesException.class)
    public void testViewBulkWorksWithTooManyPutCodes() {
        when(profileEntityManager.orcidExists(ORCID)).thenReturn(true);
        StringBuilder tooManyPutCodes = new StringBuilder("0");
        for (int i = 1; i <= BULK_READ_SIZE; i++) {
            tooManyPutCodes.append(",").append(i);
        }
        // The limit lives in WorkManagerReadOnly.findWorkBulk together with the
        // org.orcid.core.works.bulk.read.max property; the delegator only has to
        // let the exception through.
        when(workManagerReadOnly.findWorkBulk(eq(ORCID), anyString())).thenThrow(new ExceedMaxNumberOfPutCodesException((int) BULK_READ_SIZE));

        serviceDelegator.viewBulkWorks(ORCID, tooManyPutCodes.toString());
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewBulkWrongToken() {
        when(profileEntityManager.orcidExists(ORCID)).thenReturn(true);
        WorkBulk stored = new WorkBulk();
        stored.getBulk().add(work(11L, "PUBLIC", WorkType.JOURNAL_ARTICLE, Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(workManagerReadOnly.findWorkBulk(ORCID, "11,12,13")).thenReturn(stored);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(WorkBulk.class), eq(SCOPE));

        serviceDelegator.viewBulkWorks(ORCID, "11,12,13");
    }
}
