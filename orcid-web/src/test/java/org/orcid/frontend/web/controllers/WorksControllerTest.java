package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.orcid.core.exception.MissingGroupableExternalIDException;
import org.orcid.core.manager.v3.ActivitiesSummaryManager;
import org.orcid.core.manager.v3.BibtexManager;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.core.togglz.Features;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitleForm;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;

import orcid.pojo.ajaxForm.WorkFormTest;
import org.togglz.junit.TogglzRule;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:test-frontend-web-servlet.xml" })
public class WorksControllerTest extends BaseControllerTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/WebhookEntityData.xml", "/data/RecordNameEntityData.xml");

    @Resource
    WorksController worksController;

    @Resource(name = "bibtexManagerV3")
    BibtexManager bibtexManager;
    
    @Mock
    WorkManager workManagerMock;
    
    @Mock
    ActivitiesSummaryManager activitiesSummaryManagerMock;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);
    
    @Captor
    private ArgumentCaptor<List<Long>> idsCaptor;

    private int maxContributorsForUI = 50;

    private String _5000chars = null;

    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Lists.reverse(DATA_FILES));
    }
    
    @Test
    public void testGroupWorks() throws MissingGroupableExternalIDException {
        WorkManager oldWorkManager = (WorkManager) ReflectionTestUtils.getField(worksController, "workManager");
        WorkManager mockWorkManager = Mockito.mock(WorkManager.class);
        Mockito.doNothing().when(mockWorkManager).createNewWorkGroup(Mockito.anyList(), Mockito.anyString());
        ReflectionTestUtils.setField(worksController, "workManager", mockWorkManager);
        
        ArgumentCaptor<String> orcidCaptor = ArgumentCaptor.forClass(String.class);
        
        worksController.groupWorks("1,2,3,4");
        
        Mockito.verify(mockWorkManager).createNewWorkGroup(idsCaptor.capture(), orcidCaptor.capture());
        
        List<Long> ids = idsCaptor.getValue();
        assertEquals(4, ids.size());
        assertEquals(Long.valueOf(1l), ids.get(0));
        assertEquals(Long.valueOf(2l), ids.get(1));
        assertEquals(Long.valueOf(3l), ids.get(2));
        assertEquals(Long.valueOf(4l), ids.get(3));
        
        ReflectionTestUtils.setField(worksController, "workManager", oldWorkManager);
    }

    @Test
    public void testGetWorkInfo() {
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
    }

    @Test
    public void testGetWorksInfo() {
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
    }

    @Test
    public void testGetWorkInfoWithContributors() throws Exception {
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
        assertEquals("Credit Name", contributor.getCreditName().getContent());
        
        // contributor is an ORCID user with private name
        contributor = work.getContributorsGroupedByOrcid().get(3);
        assertNull(contributor.getContributorEmail());
        assertEquals("Name is private", contributor.getCreditName().getContent());
    }

    @Test
    public void testGetWorkInfoWithContributorsGroupedByOrcid() throws Exception {
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
        assertEquals("Credit Name", contributor.getCreditName().getContent());

        contributor = work.getContributorsGroupedByOrcid().get(3);
        assertNull(contributor.getContributorEmail());
        assertEquals("Name is private", contributor.getCreditName().getContent());
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
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

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

    }

    @Test
    public void testEditOtherSourceThrowsError() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        WorkForm work = worksController.getWorkInfo(Long.valueOf("7"));
        boolean throwsError = false;
        try {
            worksController.postWork(null, work, false);
        } catch (Exception e) {
            throwsError = true;
        }
        assertTrue(throwsError);
    }

    @Test
    public void testUpdateWork() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

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

        WorkForm updatedWork = worksController.getWorkInfo(Long.valueOf("6"));
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
        ReflectionTestUtils.setField(bibtexManager, "workManager", workManagerMock);                
        ReflectionTestUtils.setField(bibtexManager, "activitiesManager", activitiesSummaryManagerMock);
        
        String orcid = getAuthentication().getName();
        Work w1 = getWork(1);
        Work w2 = getWork(2);
        Work w3 = getWork(3);
        ActivitiesSummary as = getActivitySummaryWithWorks(3);
        
        when(activitiesSummaryManagerMock.getActivitiesSummary(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(as);
        when(workManagerMock.getWork(Mockito.eq(orcid), Mockito.eq(1L))).thenReturn(w1);
        when(workManagerMock.getWork(Mockito.eq(orcid), Mockito.eq(2L))).thenReturn(w2);
        when(workManagerMock.getWork(Mockito.eq(orcid), Mockito.eq(3L))).thenReturn(w3);
        
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
        ActivitiesSummary summary = new ActivitiesSummary ();
        for(int i = 1; i <= numWorks; i++) {
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
}

