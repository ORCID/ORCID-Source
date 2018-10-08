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

import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitleForm;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;

import orcid.pojo.ajaxForm.WorkFormTest;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class WorksControllerTest extends BaseControllerTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/WebhookEntityData.xml", "/data/RecordNameEntityData.xml");

    @Resource
    WorksController worksController;

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    @Captor
    private ArgumentCaptor<List<Long>> idsCaptor;

    private String _5000chars = null;

    @Before
    public void init() {
        orcidProfileManager.updateLastModifiedDate("4444-4444-4444-4446");
        assertNotNull(worksController);
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Lists.reverse(DATA_FILES));
    }
    
    @Test
    public void testGroupWorks() {
        WorkManager oldWorkManager = (WorkManager) ReflectionTestUtils.getField(worksController, "workManager");
        WorkManager mockWorkManager = Mockito.mock(WorkManager.class);
        Mockito.doNothing().when(mockWorkManager).createNewWorkGroup(Mockito.anyList(), Mockito.anyString());
        ReflectionTestUtils.setField(worksController, "workManager", mockWorkManager);
        
        ArgumentCaptor<String> orcidCaptor = ArgumentCaptor.forClass(String.class);
        
        worksController.groupWorks("1,2,3,4");
        
        Mockito.verify(mockWorkManager).setPreferredAndCreateGroup(Mockito.eq(1L), idsCaptor.capture(), orcidCaptor.capture());
        
        List<Long> ids = idsCaptor.getValue();
        assertEquals(3, ids.size());
        assertEquals(Long.valueOf(2l), ids.get(0));
        assertEquals(Long.valueOf(3l), ids.get(1));
        assertEquals(Long.valueOf(4l), ids.get(2));
        
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
    public void testGetWorkInfoWithContributors() throws Exception {
        WorkForm work = worksController.getWorkInfo(Long.valueOf("5"));
        assertNotNull(work);
        assertNotNull(work.getContributors());
        assertEquals(4, work.getContributors().size());

        Contributor contributor = work.getContributors().get(0);
        assertNull(contributor.getEmail());
        assertEquals("Jaylen Kessler", contributor.getCreditName().getValue());

        contributor = work.getContributors().get(1);
        assertNull(contributor.getEmail());
        assertEquals("John Smith", contributor.getCreditName().getValue());

        contributor = work.getContributors().get(2);
        assertNull(contributor.getEmail());
        assertEquals("Credit Name", contributor.getCreditName().getValue());
        
        // contributor is an ORCID user with private name
        contributor = work.getContributors().get(3);
        assertNull(contributor.getEmail());
        assertNull(contributor.getCreditName().getValue());
    }

    @Test
    public void testFieldValidators() throws Exception {
        Work work = WorkFormTest.getWork();
        WorkForm workForm = WorkForm.valueOf(work);

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

        work = worksController.postWork(null, work);
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
            worksController.postWork(null, work);
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
        tTitle.setLanguageCode("EN");

        work.setTranslatedTitle(tTitle);

        work.setWorkType(Text.valueOf("artistic-performance"));

        work.setWorkExternalIdentifiers(new ArrayList<ActivityExternalIdentifier>());

        ActivityExternalIdentifier wei1 = new ActivityExternalIdentifier();
        wei1.setExternalIdentifierId(Text.valueOf("1"));
        wei1.setExternalIdentifierType(Text.valueOf("doi"));
        work.getWorkExternalIdentifiers().add(wei1);

        ActivityExternalIdentifier wei2 = new ActivityExternalIdentifier();
        wei2.setExternalIdentifierId(Text.valueOf("2"));
        wei2.setExternalIdentifierType(Text.valueOf("arxiv"));
        work.getWorkExternalIdentifiers().add(wei2);

        work.getPublicationDate().setDay("2");
        work.getPublicationDate().setMonth("3");
        work.getPublicationDate().setYear("2014");

        worksController.validateWork(work);
        if (!work.getErrors().isEmpty()) {
            work.getErrors().forEach(n -> System.out.println(n));
            fail("invalid work update");
        }
        worksController.postWork(null, work);

        WorkForm updatedWork = worksController.getWorkInfo(Long.valueOf("6"));
        assertNotNull(updatedWork);
        assertEquals("6", updatedWork.getPutCode().getValue());
        assertEquals("Test update work", updatedWork.getTitle().getValue());
        assertEquals("Test update subtitle", updatedWork.getSubtitle().getValue());
        assertEquals("Test translated title", updatedWork.getTranslatedTitle().getContent());
        assertEquals("EN", updatedWork.getTranslatedTitle().getLanguageCode());
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
}
