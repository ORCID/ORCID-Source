package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.pojo.ajaxForm.Work;
import org.orcid.pojo.ajaxForm.WorkExternalIdentifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class WorksControllerTest extends BaseControllerTest {
	
	@Resource
    WorksController worksController;
		
	private String _1000chars = null;
	
	
	@Before
    public void init() {
        assertNotNull(worksController);
	}
	
	@Test
    public void testFieldValidators() throws Exception {
		//Test work without language fields
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
	}
	
	private String buildLongWork(){
		if(_1000chars == null){
			_1000chars = new String();
			for(int i = 0; i < 1000; i++)
				_1000chars += 'a';
		}
		return _1000chars;
	}
}












