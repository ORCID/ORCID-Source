package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.exception.MissingGroupableExternalIDException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ActivitiesSummaryManager;
import org.orcid.core.manager.v3.ActivityManager;
import org.orcid.core.manager.v3.BibtexManager;
import org.orcid.core.manager.DOIManager;
import org.orcid.core.manager.v3.GroupingSuggestionManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.core.manager.v3.impl.BibtexManagerImpl;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.Actors;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.core.utils.v3.identifiers.PIDResolverService;
import org.orcid.frontend.web.pagination.WorksPaginator;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.common.Day;
import org.orcid.jaxb.model.v3.release.common.Month;
import org.orcid.jaxb.model.v3.release.common.PublicationDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceName;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.common.Year;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.IdentifierType;
import org.orcid.pojo.WorkExtended;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitleForm;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.springframework.test.util.ReflectionTestUtils;

import orcid.pojo.ajaxForm.WorkFormTest;

import org.togglz.junit.TogglzRule;

/**
 * The fixture reads that used to come from /data/WorksEntityData.xml are now
 * hand built model objects carrying the same values (work 5: public journal
 * article "Journal article A" dated 2011-02-01 with four grouped contributors;
 * work 6: limited, sourced by the record itself; work 7: private, sourced by
 * APP-5555555555555555).
 *
 * Two boundaries are deliberately not mocked. WorksController.getWorkInfo is
 * only prevented from serving another record's work by WorkDaoImpl.getWork's
 * "and orcid = :orcid" predicate, which no mock can demonstrate and which needs
 * a WorkDao database test. And testExportToBibtex keeps a real BibtexManagerImpl
 * over mocked leaves, because the expected BibTeX string is that class's
 * formatting and a mocked BibtexManager would make the assertion a tautology.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class WorksControllerTest {

    private static final String USER_ORCID = "4444-4444-4444-4446";
    private static final String CLIENT_SOURCE = "APP-5555555555555555";

    private WorksController worksController;

    private BibtexManager bibtexManager;

    @Mock
    private WorkManager workManagerMock;

    @Mock
    private ActivitiesSummaryManager activitiesSummaryManagerMock;

    @Mock
    private RecordNameManagerReadOnly recordNameManagerReadOnlyMock;

    @Mock
    private DOIManager doiManagerMock;

    @Mock
    private WorkManagerReadOnly workManagerReadOnly;

    @Mock
    private WorksPaginator worksPaginator;

    @Mock
    private IdentifierTypeManager identifierTypeManager;

    @Mock
    private LocaleManager localeManager;

    @Mock
    private ActivityManager activityManager;

    @Mock
    private LanguagesMap lm;

    @Mock
    private ProfileEntityManager profileEntityManager;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    private GroupingSuggestionManager groupingSuggestionManager;

    @Mock
    private PIDResolverService resolverService;

    @Mock
    private ContributorUtils contributorUtils;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Captor
    private ArgumentCaptor<List<Long>> idsCaptor;

    private int maxContributorsForUI = 50;

    private String _5000chars = null;

    @Before
    public void before() {
        worksController = new WorksController();

        bibtexManager = new BibtexManagerImpl();
        ReflectionTestUtils.setField(bibtexManager, "workManager", workManagerMock);
        ReflectionTestUtils.setField(bibtexManager, "activitiesManager", activitiesSummaryManagerMock);
        ReflectionTestUtils.setField(bibtexManager, "recordNameManagerReadOnly", recordNameManagerReadOnlyMock);
        ReflectionTestUtils.setField(bibtexManager, "doiManager", doiManagerMock);

        ReflectionTestUtils.setField(worksController, "workManager", workManagerMock);
        ReflectionTestUtils.setField(worksController, "workManagerReadOnly", workManagerReadOnly);
        ReflectionTestUtils.setField(worksController, "worksPaginator", worksPaginator);
        ReflectionTestUtils.setField(worksController, "identifierTypeManager", identifierTypeManager);
        ReflectionTestUtils.setField(worksController, "activityManager", activityManager);
        ReflectionTestUtils.setField(worksController, "lm", lm);
        ReflectionTestUtils.setField(worksController, "profileEntityCacheManager", profileEntityCacheManager);
        ReflectionTestUtils.setField(worksController, "bibtexManager", bibtexManager);
        ReflectionTestUtils.setField(worksController, "groupingSuggestionManager", groupingSuggestionManager);
        ReflectionTestUtils.setField(worksController, "resolverService", resolverService);
        ReflectionTestUtils.setField(worksController, "contributorUtils", contributorUtils);

        // WorksController re-declares localeManager (line 71) and
        // profileEntityManager (line 80) over the copies BaseController and
        // BaseWorkspaceController declare. Spring's @Resource fills every one of
        // them; anything that fills only the most derived one leaves
        // BaseController.getMessage() with a null localeManager.
        ReflectionTestUtils.setField(worksController, WorksController.class, "localeManager", localeManager, LocaleManager.class);
        ReflectionTestUtils.setField(worksController, BaseController.class, "localeManager", localeManager, LocaleManager.class);
        ReflectionTestUtils.setField(worksController, WorksController.class, "profileEntityManager", profileEntityManager, ProfileEntityManager.class);
        ReflectionTestUtils.setField(worksController, BaseWorkspaceController.class, "profileEntityManager", profileEntityManager, ProfileEntityManager.class);
        ReflectionTestUtils.setField(worksController, BaseController.class, "profileEntityManager", profileEntityManager, ProfileEntityManager.class);

        // @Value primitives: Mockito injects no int, and 0 silently truncates
        // every contributor list.
        ReflectionTestUtils.setField(worksController, "maxContributorsForUI", maxContributorsForUI);
        ReflectionTestUtils.setField(worksController, "resultsSizeForSearchWorksToFeature", 10);

        when(localeManager.resolveMessage(anyString(), any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(localeManager.getLocale()).thenReturn(Locale.ENGLISH);
        when(localeManager.getCountries(any(Locale.class))).thenReturn(new HashMap<String, String>());
        when(lm.buildLanguageMap(any(Locale.class), eq(false))).thenReturn(new HashMap<String, String>());
        when(identifierTypeManager.fetchIdentifierTypesByAPITypeName(any(Locale.class))).thenReturn(identifierTypes());
        when(profileEntityCacheManager.retrieve(USER_ORCID)).thenReturn(new ProfileEntity(USER_ORCID));

        Actors.user(USER_ORCID);
    }

    @After
    public void after() {
        Actors.clear();
    }

    /**
     * The put-code comes from the URL and the owner from the session, so the controller must hand
     * the manager the signed-in record rather than anything the request supplied.
     *
     * <p>
     * main proves the cross-record case over DBUnit by re-reading the victim's row; on mocks the
     * equivalent is that the orcid argument is the signed-in one. The predicate that actually stops
     * the delete lives in SQL and is proved by WorkDaoTest in the database stage.
     * </p>
     */
    @Test
    public void removeWorkPassesTheSignedInRecordToTheManagerTest() {
        worksController.removeWork("11");

        verify(workManagerMock).removeWorks(eq(USER_ORCID), eq(Arrays.asList(11L)));
    }

    /**
     * Changing visibility is a state change, so it must not be reachable by a GET: a GET is
     * not CSRF-protected and can be triggered by a link or an image tag.
     */
    @Test
    public void updateVisibilityIsNotReachableByGetTest() throws Exception {
        java.lang.reflect.Method method = WorksController.class.getMethod("updateVisibility", String.class, String.class);
        org.springframework.web.bind.annotation.RequestMapping mapping = method
                .getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
        assertNotNull(mapping);
        assertArrayEquals(new org.springframework.web.bind.annotation.RequestMethod[] {
                org.springframework.web.bind.annotation.RequestMethod.POST }, mapping.method());
    }

    @Test
    public void testGroupWorks() throws MissingGroupableExternalIDException {
        ArgumentCaptor<String> orcidCaptor = ArgumentCaptor.forClass(String.class);

        worksController.groupWorks("1,2,3,4");

        verify(workManagerMock).createNewWorkGroup(idsCaptor.capture(), orcidCaptor.capture());

        List<Long> ids = idsCaptor.getValue();
        assertEquals(4, ids.size());
        assertEquals(Long.valueOf(1l), ids.get(0));
        assertEquals(Long.valueOf(2l), ids.get(1));
        assertEquals(Long.valueOf(3l), ids.get(2));
        assertEquals(Long.valueOf(4l), ids.get(3));
        assertEquals(USER_ORCID, orcidCaptor.getValue());
    }

    @Test
    public void testGetWorkInfo() {
        when(workManagerMock.getWorkExtended(USER_ORCID, 5L)).thenReturn(workFive());

        WorkForm work = worksController.getWorkInfo(Long.valueOf("5"));
        assertNotNull(work);
        assertEquals("5", work.getPutCode().getValue());
        assertNotNull(work.getPublicationDate());
        assertEquals("2011", work.getPublicationDate().getYear());
        assertEquals("02", work.getPublicationDate().getMonth());
        assertEquals("01", work.getPublicationDate().getDay());
        assertNotNull(work.getTitle());
        assertEquals("Journal article A", work.getTitle().getValue());
        assertNotNull(work.getVisibility());
        assertEquals(Visibility.PUBLIC, work.getVisibility().getVisibility());
        assertEquals("journal-article", work.getWorkType().getValue());
        // The read is scoped to the signed in record; the enforcement of that
        // scope is WorkDaoImpl.getWork's orcid predicate.
        verify(workManagerMock).getWorkExtended(USER_ORCID, 5L);
    }

    @Test
    public void testGetWorksInfo() {
        when(workManagerMock.getWorksSummaryList(eq(USER_ORCID), any())).thenReturn(Arrays.asList(workFiveSummary(), workSixSummary()));

        List<WorkForm> works = worksController.getWorksInfo("5,6");
        assertNotNull(works);
        assertEquals(2, works.size());
        assertEquals("5", works.get(0).getPutCode().getValue());
        assertNotNull(works.get(0).getPublicationDate());
        assertEquals("2011", works.get(0).getPublicationDate().getYear());
        assertEquals("02", works.get(0).getPublicationDate().getMonth());
        assertEquals("01", works.get(0).getPublicationDate().getDay());
        assertNotNull(works.get(0).getTitle());
        assertEquals("Journal article A", works.get(0).getTitle().getValue());
        assertNotNull(works.get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, works.get(0).getVisibility().getVisibility());
        assertEquals("journal-article", works.get(0).getWorkType().getValue());

        verify(workManagerMock).getWorksSummaryList(eq(USER_ORCID), idsCaptor.capture());
        assertEquals(Arrays.asList(5L, 6L), idsCaptor.getValue());
    }

    /**
     * WARNING, and the reason this file cannot carry the assertion it looks like
     * it carries. WorkForm.valueOf copies contributorsGroupedByOrcid straight
     * through from WorkExtended, so with workManager stubbed the credit names and
     * the null emails below are the fixture's own values read back: they would
     * still hold if the email stripping in WorkManagerImpl/ContributorUtils were
     * deleted tomorrow. What is genuinely asserted here is WorkForm's own
     * mapping -- that getExtendedWorkForm carries the list over and derives
     * numberOfContributors from it rather than leaving it at zero. The
     * top_contributors_json deserialisation and the email stripping need a
     * WorkManagerImpl or adapter test in orcid-core; nothing in orcid-web can
     * stand in for one.
     */
    @Test
    public void testGetWorkInfoWithContributors() throws Exception {
        when(workManagerMock.getWorkExtended(USER_ORCID, 5L)).thenReturn(workFive());

        WorkForm work = worksController.getWorkInfo(Long.valueOf("5"));
        assertNotNull(work);
        assertNotNull(work.getContributorsGroupedByOrcid());
        assertEquals(4, work.getContributorsGroupedByOrcid().size());

        ContributorsRolesAndSequences contributor = work.getContributorsGroupedByOrcid().get(0);
        assertNull(contributor.getContributorEmail());
        assertEquals("Jaylen Kessler", contributor.getCreditName().getContent());

        contributor = work.getContributorsGroupedByOrcid().get(1);
        assertNull(contributor.getContributorEmail());
        assertEquals("John Smith", contributor.getCreditName().getContent());

        contributor = work.getContributorsGroupedByOrcid().get(2);
        assertNull(contributor.getContributorEmail());
        assertEquals("Not This Name", contributor.getCreditName().getContent());

        // contributor is an ORCID user with private name
        contributor = work.getContributorsGroupedByOrcid().get(3);
        assertNull(contributor.getContributorEmail());
        assertEquals("Not This Name", contributor.getCreditName().getContent());

        assertEquals(4, work.getNumberOfContributors());
    }

    /** Same caveat as testGetWorkInfoWithContributors. */
    @Test
    public void testGetWorkInfoWithContributorsGroupedByOrcid() throws Exception {
        when(workManagerMock.getWorkExtended(USER_ORCID, 5L)).thenReturn(workFive());

        WorkForm work = worksController.getWorkInfo(Long.valueOf("5"));
        assertNotNull(work);
        assertNotNull(work.getContributorsGroupedByOrcid());
        assertEquals(4, work.getContributorsGroupedByOrcid().size());

        ContributorsRolesAndSequences contributor = work.getContributorsGroupedByOrcid().get(0);
        assertNull(contributor.getContributorEmail());
        assertEquals("Jaylen Kessler", contributor.getCreditName().getContent());
        assertEquals("0000-0003-0172-7925", contributor.getContributorOrcid().getPath());

        contributor = work.getContributorsGroupedByOrcid().get(1);
        assertNull(contributor.getContributorEmail());
        assertEquals("John Smith", contributor.getCreditName().getContent());
        // A contributor with no ORCID iD stays in the group with a null iD
        // rather than being dropped or collapsed into another entry.
        assertNull(contributor.getContributorOrcid());

        contributor = work.getContributorsGroupedByOrcid().get(2);
        assertNull(contributor.getContributorEmail());
        assertEquals("Not This Name", contributor.getCreditName().getContent());
        assertEquals("0000-0000-0000-0003", contributor.getContributorOrcid().getPath());

        contributor = work.getContributorsGroupedByOrcid().get(3);
        assertNull(contributor.getContributorEmail());
        assertEquals("Not This Name", contributor.getCreditName().getContent());
        assertEquals("1000-0000-0000-0001", contributor.getContributorOrcid().getPath());
    }

    @Test
    public void testFieldValidators() throws Exception {
        Work work = WorkFormTest.getWork();
        WorkForm workForm = WorkForm.valueOf(work, maxContributorsForUI);

        worksController.workTitleValidate(workForm);
        assertEquals(0, workForm.getTitle().getErrors().size());

        worksController.workSubtitleValidate(workForm);
        assertEquals(0, workForm.getSubtitle().getErrors().size());

        worksController.workTranslatedTitleValidate(workForm);
        assertEquals(0, workForm.getTranslatedTitle().getErrors().size());

        worksController.workUrlValidate(workForm);
        assertEquals(0, workForm.getUrl().getErrors().size());

        worksController.workJournalTitleValidate(workForm);
        assertEquals(0, workForm.getJournalTitle().getErrors().size());

        worksController.workLanguageCodeValidate(workForm);
        assertEquals(0, workForm.getLanguageCode().getErrors().size());

        worksController.workdescriptionValidate(workForm);
        assertEquals(0, workForm.getShortDescription().getErrors().size());

        worksController.workWorkTypeValidate(workForm);
        assertEquals(0, workForm.getWorkType().getErrors().size());

        worksController.workWorkExternalIdentifiersValidate(workForm);
        for (ActivityExternalIdentifier wId : workForm.getWorkExternalIdentifiers()) {
            assertEquals(0, wId.getExternalIdentifierId().getErrors().size());
            assertEquals(0, wId.getExternalIdentifierType().getErrors().size());
        }

        worksController.workCitationValidate(workForm);
        assertEquals(0, workForm.getCitation().getCitation().getErrors().size());
        assertEquals(0, workForm.getCitation().getCitationType().getErrors().size());

        assertNotNull(workForm.getCountryCode());
        assertNotNull(workForm.getCountryCode().getValue());
        assertEquals(Iso3166Country.US.value(), workForm.getCountryCode().getValue());

        // Set wrong values to each field
        workForm.setTitle(Text.valueOf(buildLongWord()));
        workForm.setSubtitle(Text.valueOf(buildLongWord()));
        workForm.getTranslatedTitle().setContent(buildLongWord());
        workForm.getTranslatedTitle().setLanguageCode(buildLongWord());
        workForm.getUrl().setValue(buildLongWord());
        workForm.getJournalTitle().setValue(buildLongWord());
        workForm.getLanguageCode().setValue(buildLongWord());
        workForm.getShortDescription().setValue(buildLongWord());
        workForm.getWorkType().setValue(new String());

        worksController.workTitleValidate(workForm);
        assertEquals(1, workForm.getTitle().getErrors().size());

        worksController.workSubtitleValidate(workForm);
        assertEquals(1, workForm.getSubtitle().getErrors().size());

        worksController.workTranslatedTitleValidate(workForm);
        assertEquals(2, workForm.getTranslatedTitle().getErrors().size());

        worksController.workUrlValidate(workForm);
        assertEquals(2, workForm.getUrl().getErrors().size());

        worksController.workJournalTitleValidate(workForm);
        assertEquals(1, workForm.getJournalTitle().getErrors().size());

        worksController.workLanguageCodeValidate(workForm);
        assertEquals(1, workForm.getLanguageCode().getErrors().size());

        worksController.workdescriptionValidate(workForm);
        assertEquals(1, workForm.getShortDescription().getErrors().size());

        worksController.workWorkTypeValidate(workForm);
        assertEquals(1, workForm.getWorkType().getErrors().size());

    }

    private String buildLongWord() {
        if (_5000chars == null) {
            _5000chars = new String();
            for (int i = 0; i < 5001; i++)
                _5000chars += 'a';
        }
        return _5000chars;
    }

    @Test
    public void testAddWork() throws Exception {
        Work created = new Work();
        created.setPutCode(1000L);
        when(workManagerMock.createWork(eq(USER_ORCID), any(WorkForm.class))).thenReturn(created);

        WorkForm work = worksController.getWork(null);
        // Set title
        work.setTitle(Text.valueOf("Test add work"));
        work.setWorkType(Text.valueOf("artistic-performance"));
        ActivityExternalIdentifier wei = work.getWorkExternalIdentifiers().get(0);
        wei.setExternalIdentifierId(Text.valueOf("1"));
        wei.setExternalIdentifierType(Text.valueOf("doi"));
        if (!PojoUtil.isEmpty(work.getPutCode())) {
            work.setPutCode(Text.valueOf(""));
        }

        if (work.getCitation() != null && work.getCitation().getCitation() != null && PojoUtil.isEmpty(work.getCitation().getCitation())) {
            work.getCitation().setCitation(Text.valueOf("test"));
        }
        if (work.getCitation() != null && work.getCitation().getCitation() != null && PojoUtil.isEmpty(work.getCitation().getCitationType())) {
            work.getCitation().setCitationType(Text.valueOf("formatted-unspecified"));
        }

        work = worksController.postWork(null, work, false);
        assertNotNull(work);
        assertFalse(PojoUtil.isEmpty(work.getPutCode()));
        assertEquals(1, work.getWorkExternalIdentifiers().size());
        assertEquals("doi", work.getWorkExternalIdentifiers().get(0).getExternalIdentifierType().getValue());
        assertEquals("1", work.getWorkExternalIdentifiers().get(0).getExternalIdentifierId().getValue());

        // Whether the row survives a round trip is WorkManagerImpl/WorkDao
        // behaviour; what the controller owes is a create call for the signed in
        // record carrying the submitted form.
        ArgumentCaptor<WorkForm> submitted = ArgumentCaptor.forClass(WorkForm.class);
        verify(workManagerMock).createWork(eq(USER_ORCID), submitted.capture());
        assertEquals("Test add work", submitted.getValue().getTitle().getValue());
        assertEquals("artistic-performance", submitted.getValue().getWorkType().getValue());
        assertEquals(1, submitted.getValue().getWorkExternalIdentifiers().size());
        verify(workManagerMock, never()).updateWork(anyString(), any(WorkForm.class));
    }

    @Test
    public void testEditOtherSourceThrowsError() throws Exception {
        when(workManagerMock.getWorkExtended(USER_ORCID, 7L)).thenReturn(workSeven());
        when(workManagerMock.findWorks(USER_ORCID)).thenReturn(Arrays.asList(workSix(), workSeven()));

        WorkForm work = worksController.getWorkInfo(Long.valueOf("7"));
        assertEquals(CLIENT_SOURCE, work.getSource());

        boolean throwsError = false;
        String message = null;
        try {
            worksController.postWork(null, work, false);
        } catch (Exception e) {
            throwsError = true;
            message = e.getMessage();
        }
        assertTrue(throwsError);
        assertEquals("web.orcid.activity_incorrectsource.exception", message);
        verify(workManagerMock, never()).updateWork(anyString(), any(WorkForm.class));
        verify(workManagerMock, never()).createWork(anyString(), any(WorkForm.class));
    }

    @Test
    public void testUpdateWork() throws Exception {
        when(workManagerMock.getWorkExtended(USER_ORCID, 6L)).thenReturn(workSixExtended());
        when(workManagerMock.findWorks(USER_ORCID)).thenReturn(Arrays.asList(workSix(), workSeven()));

        WorkForm work = worksController.getWorkInfo(Long.valueOf("6"));
        // Set title
        work.setTitle(Text.valueOf("Test update work"));
        work.setSubtitle(Text.valueOf("Test update subtitle"));

        TranslatedTitleForm tTitle = new TranslatedTitleForm();
        tTitle.setContent("Test translated title");
        tTitle.setLanguageCode("en");

        work.setTranslatedTitle(tTitle);

        work.setWorkType(Text.valueOf("artistic-performance"));

        work.setWorkExternalIdentifiers(new ArrayList<ActivityExternalIdentifier>());

        ActivityExternalIdentifier wei1 = new ActivityExternalIdentifier();
        wei1.setExternalIdentifierId(Text.valueOf("1"));
        wei1.setExternalIdentifierType(Text.valueOf("doi"));
        wei1.setRelationship(Text.valueOf("self"));
        work.getWorkExternalIdentifiers().add(wei1);

        ActivityExternalIdentifier wei2 = new ActivityExternalIdentifier();
        wei2.setExternalIdentifierId(Text.valueOf("2"));
        wei2.setExternalIdentifierType(Text.valueOf("arxiv"));
        wei2.setRelationship(Text.valueOf("self"));
        work.getWorkExternalIdentifiers().add(wei2);

        work.getPublicationDate().setDay("2");
        work.getPublicationDate().setMonth("3");
        work.getPublicationDate().setYear("2014");

        worksController.validateWork(work);
        if (!work.getErrors().isEmpty()) {
            work.getErrors().forEach(n -> System.out.println(n));
            fail("invalid work update");
        }
        worksController.postWork(null, work, false);

        // The round trip these assertions used to make is WorkManagerImpl and
        // WorkDao behaviour; what the controller owes is that the whole edited
        // form reaches updateWork, scoped to the signed in record.
        ArgumentCaptor<WorkForm> updated = ArgumentCaptor.forClass(WorkForm.class);
        verify(workManagerMock).updateWork(eq(USER_ORCID), updated.capture());
        WorkForm updatedWork = updated.getValue();
        assertNotNull(updatedWork);
        assertEquals("6", updatedWork.getPutCode().getValue());
        assertEquals("Test update work", updatedWork.getTitle().getValue());
        assertEquals("Test update subtitle", updatedWork.getSubtitle().getValue());
        assertEquals("Test translated title", updatedWork.getTranslatedTitle().getContent());
        assertEquals("en", updatedWork.getTranslatedTitle().getLanguageCode());
        assertNotNull(updatedWork.getWorkExternalIdentifiers());
        assertEquals(2, updatedWork.getWorkExternalIdentifiers().size());

        List<ActivityExternalIdentifier> extIds = updatedWork.getWorkExternalIdentifiers();
        for (ActivityExternalIdentifier extId : extIds) {
            if (extId.getExternalIdentifierType().getValue().equals("doi") || extId.getExternalIdentifierType().getValue().equals("arxiv")) {
                if (extId.getExternalIdentifierType().getValue().equals("doi")) {
                    assertEquals("1", extId.getExternalIdentifierId().getValue());
                } else {
                    assertEquals("2", extId.getExternalIdentifierId().getValue());
                }

            } else {
                fail("Invalid external identifier found: " + extId.getExternalIdentifierType().getValue() + " : " + extId.getExternalIdentifierId().getValue());
            }

        }
    }

    @Test
    public void testExportToBibtex() throws Exception {
        String orcid = USER_ORCID;
        Work w1 = getWork(1);
        Work w2 = getWork(2);
        Work w3 = getWork(3);
        ActivitiesSummary as = getActivitySummaryWithWorks(3);

        when(recordNameManagerReadOnlyMock.fetchDisplayableCreditName(orcid)).thenReturn("B. Holiday");
        when(activitiesSummaryManagerMock.getActivitiesSummary(anyString(), anyBoolean())).thenReturn(as);
        when(workManagerMock.getWork(eq(orcid), eq(1L))).thenReturn(w1);
        when(workManagerMock.getWork(eq(orcid), eq(2L))).thenReturn(w2);
        when(workManagerMock.getWork(eq(orcid), eq(3L))).thenReturn(w3);

        assertEquals("@book{B._Holiday_1,\n" +
                "title={Work Title 1},\n" +
                "author={B. Holiday}\n" +
                "},\n" +
                "@book{B._Holiday_2,\n" +
                "title={Work Title 2},\n" +
                "author={B. Holiday}\n" +
                "},\n" +
                "@book{B._Holiday_3,\n" +
                "title={Work Title 3},\n" +
                "author={B. Holiday}\n" +
                "}", worksController.exportAsBibtex("1,2,3"));

        assertEquals("@book{B._Holiday_1,\n" +
                "title={Work Title 1},\n" +
                "author={B. Holiday}\n" +
                "},\n" +
                "@book{B._Holiday_3,\n" +
                "title={Work Title 3},\n" +
                "author={B. Holiday}\n" +
                "}", worksController.exportAsBibtex("1,3"));
        assertEquals("@book{B._Holiday_1,\n" +
                "title={Work Title 1},\n" +
                "author={B. Holiday}\n" +
                "}", worksController.exportAsBibtex("1"));

    }

    private ActivitiesSummary getActivitySummaryWithWorks(int numWorks) {
        ActivitiesSummary summary = new ActivitiesSummary();
        for (int i = 1; i <= numWorks; i++) {
            WorkSummary ws = new WorkSummary();
            ws.setPutCode(Long.valueOf(i));
            ws.setTitle(getWorkTitle(i));
            WorkGroup wg1 = new WorkGroup();
            wg1.getWorkSummary().add(ws);
            summary.getWorks().getWorkGroup().add(wg1);
        }
        return summary;
    }

    private Work getWork(long id) {
        Work w = new Work();
        w.setPutCode(id);
        WorkTitle wt = new WorkTitle();
        wt.setTitle(new Title("Work Title " + id));
        w.setWorkTitle(wt);
        w.setWorkType(WorkType.BOOK);
        return w;
    }

    private WorkTitle getWorkTitle(long id) {
        WorkTitle wt = new WorkTitle();
        wt.setTitle(new Title("Work Title " + id));
        return wt;
    }

    // ------------------------------------------------ /data/WorksEntityData.xml

    /**
     * work_id=5: orcid 4444-4444-4444-4446, sourced by APP-5555555555555555,
     * PUBLIC, "Journal article A", 2011-02-01, journal-article, and the four
     * contributors its top_contributors_json column carried.
     */
    private WorkExtended workFive() {
        WorkExtended work = new WorkExtended();
        work.setPutCode(5L);
        work.setWorkType(WorkType.JOURNAL_ARTICLE);
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("Journal article A"));
        work.setWorkTitle(title);
        work.setVisibility(Visibility.PUBLIC);
        work.setPublicationDate(new PublicationDate(new Year(2011), new Month(2), new Day(1)));
        work.setSource(clientSource());
        work.setContributorsGroupedByOrcid(Arrays.asList(contributor("Jaylen Kessler", "0000-0003-0172-7925"), contributor("John Smith", null),
                contributor("Not This Name", "0000-0000-0000-0003"), contributor("Not This Name", "1000-0000-0000-0001")));
        return work;
    }

    private WorkSummary workFiveSummary() {
        WorkSummary summary = new WorkSummary();
        summary.setPutCode(5L);
        summary.setType(WorkType.JOURNAL_ARTICLE);
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("Journal article A"));
        summary.setTitle(title);
        summary.setVisibility(Visibility.PUBLIC);
        summary.setPublicationDate(new PublicationDate(new Year(2011), new Month(2), new Day(1)));
        summary.setSource(clientSource());
        return summary;
    }

    /** work_id=6: LIMITED and sourced by the record itself. */
    private WorkSummary workSixSummary() {
        WorkSummary summary = new WorkSummary();
        summary.setPutCode(6L);
        summary.setType(WorkType.JOURNAL_ARTICLE);
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("Journal article B"));
        summary.setTitle(title);
        summary.setVisibility(Visibility.LIMITED);
        summary.setPublicationDate(new PublicationDate(new Year(2011), new Month(2), new Day(1)));
        summary.setSource(userSource());
        return summary;
    }

    private Work workSix() {
        Work work = new Work();
        work.setPutCode(6L);
        work.setWorkType(WorkType.JOURNAL_ARTICLE);
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("Journal article B"));
        work.setWorkTitle(title);
        work.setVisibility(Visibility.LIMITED);
        work.setPublicationDate(new PublicationDate(new Year(2011), new Month(2), new Day(1)));
        work.setSource(userSource());
        return work;
    }

    private WorkExtended workSixExtended() {
        WorkExtended work = new WorkExtended();
        work.setPutCode(6L);
        work.setWorkType(WorkType.JOURNAL_ARTICLE);
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("Journal article B"));
        work.setWorkTitle(title);
        work.setVisibility(Visibility.LIMITED);
        work.setPublicationDate(new PublicationDate(new Year(2011), new Month(2), new Day(1)));
        work.setSource(userSource());
        return work;
    }

    /** work_id=7: PRIVATE and sourced by a client, not by the record. */
    private WorkExtended workSeven() {
        WorkExtended work = new WorkExtended();
        work.setPutCode(7L);
        work.setWorkType(WorkType.JOURNAL_ARTICLE);
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("Journal article C"));
        work.setWorkTitle(title);
        work.setVisibility(Visibility.PRIVATE);
        work.setPublicationDate(new PublicationDate(new Year(2011), new Month(2), new Day(1)));
        work.setSource(clientSource());
        return work;
    }

    private Source clientSource() {
        Source source = new Source();
        source.setSourceClientId(new SourceClientId(CLIENT_SOURCE));
        source.setSourceName(new SourceName("Source Client 1"));
        return source;
    }

    private Source userSource() {
        Source source = new Source();
        source.setSourceOrcid(new SourceOrcid(USER_ORCID));
        source.setSourceName(new SourceName("Credit Name"));
        return source;
    }

    private ContributorsRolesAndSequences contributor(String creditName, String contributorOrcid) {
        ContributorsRolesAndSequences contributor = new ContributorsRolesAndSequences();
        contributor.setCreditName(new CreditName(creditName));
        if (contributorOrcid != null) {
            contributor.setContributorOrcid(new ContributorOrcid(contributorOrcid));
        }
        return contributor;
    }

    private Map<String, IdentifierType> identifierTypes() {
        Map<String, IdentifierType> types = new HashMap<String, IdentifierType>();
        // Only the key set is read by workWorkExternalIdentifiersValidate.
        types.put("agr", null);
        types.put("doi", null);
        types.put("arxiv", null);
        types.put("issn", null);
        return types;
    }
}
