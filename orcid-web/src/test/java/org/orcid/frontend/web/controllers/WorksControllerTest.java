/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

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
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Work;
import org.orcid.pojo.ajaxForm.WorkExternalIdentifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.orcid.jaxb.model.message.Visibility;

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class WorksControllerTest extends BaseControllerTest {

	private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml",
            "/data/WorksEntityData.xml", "/data/ProfileWorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/WebhookEntityData.xml");    
	
    @Resource
    WorksController worksController;

    private String _5000chars = null;

    @Before
    public void init() {
        assertNotNull(worksController);
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(DATA_FILES, null);
    }
    
    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Lists.reverse(DATA_FILES), null);
    }
    
    @Test
    public void testGetWorksJson() throws Exception {
    	HttpServletRequest servletRequest = mock(HttpServletRequest.class);    	
    	HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        
        List<String> work_ids = worksController.getWorksJson(servletRequest);
        
        assertNotNull(work_ids);
        assertEquals(3, work_ids.size());
        assertTrue(work_ids.contains("1"));
        assertTrue(work_ids.contains("2"));
        assertTrue(work_ids.contains("3"));
        
    }
    
    @Test
    public void testGetWorkInfo(){
    	Work work = worksController.getWorkInfo("1");
    	assertNotNull(work);
    	assertEquals("1", work.getPutCode().getValue());        
        assertNotNull(work.getPublicationDate());        
        assertEquals("2011", work.getPublicationDate().getYear());
        assertEquals("02", work.getPublicationDate().getMonth());
        assertEquals("01", work.getPublicationDate().getDay());              
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("A day in the life", work.getWorkTitle().getTitle().getValue());
        assertNotNull(work.getWorkTitle().getSubtitle());
        assertEquals("Life in a day", work.getWorkTitle().getSubtitle().getValue());
        assertNotNull(work.getShortDescription());
        assertNotNull(work.getShortDescription().getValue());
        assertEquals("Very interesting book about days and life", work.getShortDescription().getValue());
        assertNotNull(work.getVisibility());
        assertEquals(Visibility.PUBLIC, work.getVisibility());
        assertNotNull(work.getCitation());
        assertNotNull(work.getCitation().getCitation());        
        assertEquals("Bobby Ewing, ", work.getCitation().getCitation().getValue());
        assertNotNull(work.getWorkType());
        assertEquals("book", work.getWorkType().getValue());               
    }
    
    @Test
    public void testFieldValidators() throws Exception {
        // Test work without language fields
        JAXBContext context = JAXBContext.newInstance(OrcidWork.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        OrcidWork orcidWork = (OrcidWork) unmarshaller.unmarshal(getClass().getResourceAsStream("/orcid-work.xml"));
        assertNotNull(orcidWork);
        Work work = Work.valueOf(orcidWork);

        worksController.workWorkTitleTitleValidate(work);
        assertEquals(0, work.getWorkTitle().getTitle().getErrors().size());

        worksController.workWorkTitleSubtitleValidate(work);
        assertEquals(0, work.getWorkTitle().getSubtitle().getErrors().size());

        worksController.workWorkTitleTranslatedTitleValidate(work);
        assertEquals(0, work.getWorkTitle().getTranslatedTitle().getErrors().size());

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
        work.getWorkTitle().setTitle(Text.valueOf(buildLongWord()));
        work.getWorkTitle().setSubtitle(Text.valueOf(buildLongWord()));
        work.getWorkTitle().getTranslatedTitle().setContent(buildLongWord());
        work.getWorkTitle().getTranslatedTitle().setLanguageCode(buildLongWord());
        work.getUrl().setValue(buildLongWord());
        work.getJournalTitle().setValue(buildLongWord());
        work.getLanguageCode().setValue(buildLongWord());
        work.getShortDescription().setValue(buildLongWord());
        work.getWorkType().setValue(new String());

        worksController.workWorkTitleTitleValidate(work);
        assertEquals(1, work.getWorkTitle().getTitle().getErrors().size());

        worksController.workWorkTitleSubtitleValidate(work);
        assertEquals(1, work.getWorkTitle().getSubtitle().getErrors().size());

        worksController.workWorkTitleTranslatedTitleValidate(work);
        assertEquals(2, work.getWorkTitle().getTranslatedTitle().getErrors().size());

        worksController.workUrlValidate(work);
        assertEquals(1, work.getUrl().getErrors().size());

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
}
