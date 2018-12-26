package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.CitationType;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.Relationship;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Will Simpson
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbWorkAdapterTest extends MockSourceNameCache {

    @Resource(name = "jpaJaxbWorkAdapterV3")
    private JpaJaxbWorkAdapter jpaJaxbWorkAdapter;

    @Resource
    private OrcidUrlManager orcidUrlManager;
    private String originalBaseUrl;
    
    @Before
    public void before(){
        originalBaseUrl = orcidUrlManager.getBaseUrl();
    }
    
    @After
    public void after(){
        orcidUrlManager.setBaseUrl(originalBaseUrl);
    }

    @Test
    public void testToWorkEntity() throws JAXBException {
        Work work = getWork(true);
        assertNotNull(work);
        WorkEntity workEntity = jpaJaxbWorkAdapter.toWorkEntity(work);
        assertNotNull(workEntity);
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name(), workEntity.getVisibility());
        assertNotNull(workEntity);
        assertEquals(123, workEntity.getId().longValue());
        assertEquals("common:title", workEntity.getTitle());
        assertTrue(PojoUtil.isEmpty(workEntity.getSubtitle()));
        assertEquals("common:translated-title", workEntity.getTranslatedTitle());
        assertEquals("en", workEntity.getTranslatedTitleLanguageCode());
        assertEquals("work:short-description", workEntity.getDescription());
        assertEquals(org.orcid.jaxb.model.record_v2.CitationType.FORMATTED_UNSPECIFIED.name(), workEntity.getCitationType());
        assertEquals(org.orcid.jaxb.model.record_v2.WorkType.ARTISTIC_PERFORMANCE.name(), workEntity.getWorkType());
        PublicationDateEntity publicationDateEntity = workEntity.getPublicationDate();
        assertNotNull(publicationDateEntity);
        assertEquals(1948, publicationDateEntity.getYear().intValue());
        assertEquals(02, publicationDateEntity.getMonth().intValue());
        assertEquals(02, publicationDateEntity.getDay().intValue());
        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":\"VERSION_OF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}},{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"work:doi\"}}]}",
                workEntity.getExternalIdentifiersJson());
        assertEquals("http://tempuri.org", workEntity.getWorkUrl());
        assertEquals(
                "{\"contributor\":[{\"contributorOrcid\":{\"uri\":\"https://orcid.org/8888-8888-8888-8880\",\"path\":\"8888-8888-8888-8880\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"work:credit-name\"},\"contributorEmail\":{\"value\":\"work@contributor.email\"},\"contributorAttributes\":{\"contributorSequence\":\"FIRST\",\"contributorRole\":\"AUTHOR\"}}]}",
                workEntity.getContributorsJson());
        assertEquals("en", workEntity.getLanguageCode());
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.AF.name(), workEntity.getIso2Country());
        
        // Source
        assertNull(workEntity.getSourceId());        
        assertNull(workEntity.getClientSourceId());        
        assertNull(workEntity.getElementSourceId());
    }

    @Test
    public void fromProfileWorkEntityToWorkTest() {
        // Set base url to https to ensure source URI is converted to http
        orcidUrlManager.setBaseUrl("https://testserver.orcid.org");
        WorkEntity work = getWorkEntity();
        assertNotNull(work);
        Work w = jpaJaxbWorkAdapter.toWork(work);
        assertNotNull(w);
        assertNotNull(w.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(w.getCreatedDate().getValue()));
        assertNotNull(w.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(w.getLastModifiedDate().getValue()));
        assertEquals(Iso3166Country.CR.value(), w.getCountry().getValue().value());
        assertEquals("work:citation", w.getWorkCitation().getCitation());
        assertEquals("work:description", w.getShortDescription());
        assertEquals("work:journalTitle", w.getJournalTitle().getContent());
        assertEquals(CitationType.BIBTEX.value(), w.getWorkCitation().getWorkCitationType().value());
        assertEquals(Long.valueOf(12345), w.getPutCode());
        assertEquals(Visibility.LIMITED.value(), w.getVisibility().value());
        assertEquals("work:title", w.getWorkTitle().getTitle().getContent());
        assertEquals("work:subtitle", w.getWorkTitle().getSubtitle().getContent());
        assertEquals("work:translatedTitle", w.getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("es", w.getWorkTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(WorkType.ARTISTIC_PERFORMANCE.value(), w.getWorkType().value());
        assertNotNull(w.getWorkExternalIdentifiers());
        assertNotNull(w.getWorkExternalIdentifiers().getExternalIdentifier());
        assertEquals(2, w.getWorkExternalIdentifiers().getExternalIdentifier().size());
        
        ExternalID workExtId1 = w.getWorkExternalIdentifiers().getExternalIdentifier().get(0);
        assertNotNull(workExtId1.getValue());
        assertEquals("123", workExtId1.getValue());
        assertNotNull(workExtId1.getType());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value(), workExtId1.getType());
        assertEquals(Relationship.VERSION_OF, workExtId1.getRelationship());
        
        ExternalID workExtId2 = w.getWorkExternalIdentifiers().getExternalIdentifier().get(1);
        assertNotNull(workExtId2.getValue());
        assertEquals("abc", workExtId2.getValue());
        assertNotNull(workExtId2.getType());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value(), workExtId2.getType());
        assertEquals(Relationship.SELF, workExtId2.getRelationship());
        
        String sourcePath = w.getSource().retrieveSourcePath();
        assertNotNull(sourcePath);
        assertEquals("APP-5555555555555555", sourcePath);
        
        // Identifier URIs should always be http, event if base url is https
        assertEquals("https://testserver.orcid.org/client/APP-5555555555555555", w.getSource().retriveSourceUri());
    }

    @Test
    public void fromWorkEntityToWorkSummaryTest() {
        WorkEntity work = getWorkEntity();
        assertNotNull(work);
        WorkSummary ws = jpaJaxbWorkAdapter.toWorkSummary(work);
        assertNotNull(ws);
        assertEquals(Long.valueOf(12345), ws.getPutCode());
        assertEquals(Visibility.LIMITED.value(), ws.getVisibility().value());
        assertEquals("1234567890", ws.getDisplayIndex());
        assertNotNull(ws.getExternalIdentifiers());
        assertNotNull(ws.getExternalIdentifiers().getExternalIdentifier());
        assertEquals(2, ws.getExternalIdentifiers().getExternalIdentifier().size());
        ExternalID workExtId1 = ws.getExternalIdentifiers().getExternalIdentifier().get(0);
        assertNotNull(workExtId1.getValue());
        assertEquals("123", workExtId1.getValue());
        assertNotNull(workExtId1.getType());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value(), workExtId1.getType());
        assertEquals(Relationship.VERSION_OF, workExtId1.getRelationship());
        
        ExternalID workExtId2 = ws.getExternalIdentifiers().getExternalIdentifier().get(1);
        assertNotNull(workExtId2.getValue());
        assertEquals("abc", workExtId2.getValue());
        assertNotNull(workExtId2.getType());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value(), workExtId2.getType());
        assertEquals(Relationship.SELF, workExtId2.getRelationship());
        
        
        assertEquals("work:journalTitle", ws.getJournalTitle().getContent());
        assertEquals("work:url",ws.getUrl().getValue());
    }

    @Test
    public void dissertationThesisToDissertationThesisTest() {
        WorkEntity work = getWorkEntity();
        work.setWorkType(WorkType.DISSERTATION_THESIS.name());
        
        WorkSummary ws = jpaJaxbWorkAdapter.toWorkSummary(work);
        assertNotNull(ws);
        assertEquals(WorkType.DISSERTATION_THESIS, ws.getType());
        
        Work w = jpaJaxbWorkAdapter.toWork(work);
        assertNotNull(w);
        assertEquals(WorkType.DISSERTATION_THESIS, w.getWorkType());        
    
        MinimizedWorkEntity mWork = new MinimizedWorkEntity();
        mWork.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name());
        List<WorkSummary> summaries = jpaJaxbWorkAdapter.toWorkSummaryFromMinimized(Arrays.asList(mWork));
        assertEquals(WorkType.DISSERTATION_THESIS, summaries.get(0).getType());
    }
    
    @Test
    public void dissertationToDisertationThesisTest() throws JAXBException {
        Work w = getWork(true);
        w.setWorkType(WorkType.DISSERTATION_THESIS);
        
        WorkEntity we = jpaJaxbWorkAdapter.toWorkEntity(w);
        assertNotNull(we);
        assertEquals(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name(), we.getWorkType());
    }
    
    @Test
    public void dissertationToDissertationThesisTest() {
        WorkEntity work = getWorkEntity();
        work.setWorkType(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION.name());
        
        WorkSummary ws = jpaJaxbWorkAdapter.toWorkSummary(work);
        assertNotNull(ws);
        assertEquals(WorkType.DISSERTATION_THESIS, ws.getType());
        
        Work w = jpaJaxbWorkAdapter.toWork(work);
        assertNotNull(w);
        assertEquals(WorkType.DISSERTATION_THESIS, w.getWorkType());        
    
        MinimizedWorkEntity mWork = new MinimizedWorkEntity();
        mWork.setWorkType(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION.name());
        List<WorkSummary> summaries = jpaJaxbWorkAdapter.toWorkSummaryFromMinimized(Arrays.asList(mWork));
        assertEquals(WorkType.DISSERTATION_THESIS, summaries.get(0).getType());
    }
    
    private Work getWork(boolean full) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Work.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0_rc2/samples/read_samples/work-3.0_rc2.xml";
        if (full) {
            name = "/record_3.0_rc2/samples/read_samples/work-full-3.0_rc2.xml";
        }
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Work) unmarshaller.unmarshal(inputStream);
    }

    private WorkEntity getWorkEntity() {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        WorkEntity work = new WorkEntity();
        work.setDateCreated(date);
        work.setLastModified(date);
        work.setOrcid("0000-0000-0000-0001");
        work.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.name());
        work.setDisplayIndex(1234567890L);
        work.setClientSourceId("APP-5555555555555555");        
        work.setCitation("work:citation");
        work.setCitationType(org.orcid.jaxb.model.record_v2.CitationType.BIBTEX.name());
        work.setDateCreated(date);
        work.setDescription("work:description");
        work.setId(12345L);
        work.setIso2Country(org.orcid.jaxb.model.common_v2.Iso3166Country.CR.name());
        work.setJournalTitle("work:journalTitle");
        work.setLanguageCode("en");
        work.setLastModified(date);
        work.setPublicationDate(new PublicationDateEntity(2000, 1, 1));
        work.setSubtitle("work:subtitle");
        work.setTitle("work:title");
        work.setTranslatedTitle("work:translatedTitle");
        work.setTranslatedTitleLanguageCode("es");
        work.setWorkType(org.orcid.jaxb.model.record_v2.WorkType.ARTISTIC_PERFORMANCE.name());
        work.setWorkUrl("work:url");
        work.setContributorsJson("{\"contributor\":[]}");
        work.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"relationship\":\"VERSION_OF\",\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"123\"}},"
                + "{\"relationship\":\"SELF\",\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"abc\"}}]}");
        return work;
    }
}
