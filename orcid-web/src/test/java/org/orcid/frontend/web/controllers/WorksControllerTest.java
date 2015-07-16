/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitle;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.orcid.pojo.ajaxForm.WorkExternalIdentifier;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class WorksControllerTest extends BaseControllerTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", 
            "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml", "/data/WebhookEntityData.xml");

    @Resource
    WorksController worksController;

    @Resource
    protected OrcidProfileManager orcidProfileManager;
    
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
    public void testGetWorksJson() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        List<String> work_ids = worksController.getWorksJson(servletRequest);

        assertNotNull(work_ids);
        assertEquals(3, work_ids.size());
        assertTrue(work_ids.contains("5"));
        assertTrue(work_ids.contains("6"));
        assertTrue(work_ids.contains("7"));

    }

    @Test
    public void testGetWorkInfo() {
        WorkForm work = worksController.getWorkInfo("5");
        assertNotNull(work);
        assertEquals("5", work.getPutCode().getValue());
        assertNotNull(work.getPublicationDate());
        assertEquals("2011", work.getPublicationDate().getYear());
        assertEquals("02", work.getPublicationDate().getMonth());
        assertEquals("01", work.getPublicationDate().getDay());
        assertNotNull(work.getTitle());
        assertEquals("Journal article A", work.getTitle().getValue());
        assertNotNull(work.getVisibility());
        assertEquals(Visibility.PUBLIC, work.getVisibility());
        assertEquals("journal-article", work.getWorkType().getValue());
    }

    @Test
    public void testFieldValidators() throws Exception {
        // Test work without language fields
        JAXBContext context = JAXBContext.newInstance(OrcidWork.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        OrcidWork orcidWork = (OrcidWork) unmarshaller.unmarshal(getClass().getResourceAsStream("/orcid-work.xml"));
        assertNotNull(orcidWork);
        WorkForm work = WorkForm.valueOf(orcidWork);

        worksController.workTitleValidate(work);
        assertEquals(0, work.getTitle().getErrors().size());

        worksController.workSubtitleValidate(work);
        assertEquals(0, work.getSubtitle().getErrors().size());

        worksController.workTranslatedTitleValidate(work);
        assertEquals(0, work.getTranslatedTitle().getErrors().size());

        worksController.workUrlValidate(work);
        assertEquals(0, work.getUrl().getErrors().size());

        worksController.workJournalTitleValidate(work);
        assertEquals(0, work.getJournalTitle().getErrors().size());

        worksController.workLanguageCodeValidate(work);
        assertEquals(0, work.getLanguageCode().getErrors().size());

        worksController.workdescriptionValidate(work);
        assertEquals(0, work.getShortDescription().getErrors().size());

        worksController.workWorkTypeValidate(work);
        assertEquals(0, work.getWorkType().getErrors().size());

        worksController.workWorkExternalIdentifiersValidate(work);
        for (WorkExternalIdentifier wId : work.getWorkExternalIdentifiers()) {
            assertEquals(0, wId.getWorkExternalIdentifierId().getErrors().size());
            assertEquals(0, wId.getWorkExternalIdentifierType().getErrors().size());
        }

        worksController.workCitationValidate(work);
        assertEquals(0, work.getCitation().getCitation().getErrors().size());
        assertEquals(0, work.getCitation().getCitationType().getErrors().size());

        assertNotNull(orcidWork.getCountry());
        assertNotNull(orcidWork.getCountry().getValue());
        assertEquals(orcidWork.getCountry().getValue(), Iso3166Country.US);

        // Set wrong values to each field
        work.setTitle(Text.valueOf(buildLongWord()));
        work.setSubtitle(Text.valueOf(buildLongWord()));
        work.getTranslatedTitle().setContent(buildLongWord());
        work.getTranslatedTitle().setLanguageCode(buildLongWord());
        work.getUrl().setValue(buildLongWord());
        work.getJournalTitle().setValue(buildLongWord());
        work.getLanguageCode().setValue(buildLongWord());
        work.getShortDescription().setValue(buildLongWord());
        work.getWorkType().setValue(new String());

        worksController.workTitleValidate(work);
        assertEquals(1, work.getTitle().getErrors().size());

        worksController.workSubtitleValidate(work);
        assertEquals(1, work.getSubtitle().getErrors().size());

        worksController.workTranslatedTitleValidate(work);
        assertEquals(2, work.getTranslatedTitle().getErrors().size());

        worksController.workUrlValidate(work);
        assertEquals(2, work.getUrl().getErrors().size());

        worksController.workJournalTitleValidate(work);
        assertEquals(1, work.getJournalTitle().getErrors().size());

        worksController.workLanguageCodeValidate(work);
        assertEquals(1, work.getLanguageCode().getErrors().size());

        worksController.workdescriptionValidate(work);
        assertEquals(1, work.getShortDescription().getErrors().size());

        worksController.workWorkTypeValidate(work);
        assertEquals(1, work.getWorkType().getErrors().size());

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
        WorkExternalIdentifier wei = work.getWorkExternalIdentifiers().get(0);
        wei.setWorkExternalIdentifierId(Text.valueOf("1"));
        wei.setWorkExternalIdentifierType(Text.valueOf("doi"));
        if(!PojoUtil.isEmpty(work.getPutCode())) {
            work.setPutCode(Text.valueOf(""));
        }
        
        work = worksController.postWork(null, work);
        assertNotNull(work);
        assertFalse(PojoUtil.isEmpty(work.getPutCode()));
        assertEquals(1, work.getWorkExternalIdentifiers().size());
        assertEquals("doi", work.getWorkExternalIdentifiers().get(0).getWorkExternalIdentifierType().getValue());
        assertEquals("1", work.getWorkExternalIdentifiers().get(0).getWorkExternalIdentifierId().getValue());

    }

    @Test
    public void testEditOtherSourceThrowsError() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        WorkForm work = worksController.getWorkInfo("7");
        boolean throwsError = false;
        try {
            worksController.postWork(null, work);
        } catch (Exception e) {
            throwsError  = true;
        }
        assertTrue(throwsError);
    }

    @Test
    public void testUpdateWork() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        WorkForm work = worksController.getWorkInfo("6");
        // Set title
        work.setTitle(Text.valueOf("Test update work"));
        work.setSubtitle(Text.valueOf("Test update subtitle"));

        TranslatedTitle tTitle = new TranslatedTitle();
        tTitle.setContent("Test translated title");
        tTitle.setLanguageCode("EN");

        work.setTranslatedTitle(tTitle);

        work.setWorkType(Text.valueOf("artistic-performance"));

        work.setWorkExternalIdentifiers(new ArrayList<WorkExternalIdentifier>());

        WorkExternalIdentifier wei1 = new WorkExternalIdentifier();
        wei1.setWorkExternalIdentifierId(Text.valueOf("1"));
        wei1.setWorkExternalIdentifierType(Text.valueOf("doi"));
        work.getWorkExternalIdentifiers().add(wei1);

        WorkExternalIdentifier wei2 = new WorkExternalIdentifier();
        wei2.setWorkExternalIdentifierId(Text.valueOf("2"));
        wei2.setWorkExternalIdentifierType(Text.valueOf("arxiv"));
        work.getWorkExternalIdentifiers().add(wei2);

        work.getPublicationDate().setDay("2");
        work.getPublicationDate().setMonth("3");
        work.getPublicationDate().setYear("2014");

        worksController.postWork(null, work);

        WorkForm updatedWork = worksController.getWorkInfo("6");
        assertNotNull(updatedWork);
        assertEquals("6", updatedWork.getPutCode().getValue());
        assertEquals("Test update work", updatedWork.getTitle().getValue());
        assertEquals("Test update subtitle", updatedWork.getSubtitle().getValue());
        assertEquals("Test translated title", updatedWork.getTranslatedTitle().getContent());
        assertEquals("EN", updatedWork.getTranslatedTitle().getLanguageCode());
        assertNotNull(updatedWork.getWorkExternalIdentifiers());
        assertEquals(2, updatedWork.getWorkExternalIdentifiers().size());

        List<WorkExternalIdentifier> extIds = updatedWork.getWorkExternalIdentifiers();
        for (WorkExternalIdentifier extId : extIds) {
            if (extId.getWorkExternalIdentifierType().getValue().equals("doi") || extId.getWorkExternalIdentifierType().getValue().equals("arxiv")) {
                if (extId.getWorkExternalIdentifierType().getValue().equals("doi")) {
                    assertEquals("1", extId.getWorkExternalIdentifierId().getValue());
                } else {
                    assertEquals("2", extId.getWorkExternalIdentifierId().getValue());
                }

            } else {
                fail("Invalid external identifier found: " + extId.getWorkExternalIdentifierType().getValue() + " : " + extId.getWorkExternalIdentifierId().getValue());
            }

        }
    }
}
