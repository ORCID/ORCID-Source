package org.orcid.core.adapter.v2.latest;

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
import org.orcid.core.adapter.JpaJaxbWorkAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Will Simpson
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class JpaJaxbWorkAdapterTest extends MockSourceNameCache {

    @Resource
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
        assertNull(workEntity.getDateCreated());
        assertNull(workEntity.getLastModified());
        assertEquals(Visibility.PRIVATE.name(), workEntity.getVisibility());
        assertNotNull(workEntity);
        assertEquals(123, workEntity.getId().longValue());
        assertEquals("common:title", workEntity.getTitle());
        assertTrue(PojoUtil.isEmpty(workEntity.getSubtitle()));
        assertEquals("common:translated-title", workEntity.getTranslatedTitle());
        assertEquals("en", workEntity.getTranslatedTitleLanguageCode());
        assertEquals("work:short-description", workEntity.getDescription());
        assertEquals(CitationType.FORMATTED_UNSPECIFIED.name(), workEntity.getCitationType());
        assertEquals(WorkType.ARTISTIC_PERFORMANCE.name(), workEntity.getWorkType());
        PublicationDateEntity publicationDateEntity = workEntity.getPublicationDate();
        assertNotNull(publicationDateEntity);
        assertEquals(1948, publicationDateEntity.getYear().intValue());
        assertEquals(02, publicationDateEntity.getMonth().intValue());
        assertEquals(02, publicationDateEntity.getDay().intValue());
        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}}]}",
                workEntity.getExternalIdentifiersJson());
        assertEquals("http://tempuri.org", workEntity.getWorkUrl());
        assertEquals(
                "{\"contributor\":[{\"contributorOrcid\":{\"uri\":\"http://orcid.org/8888-8888-8888-8880\",\"path\":\"8888-8888-8888-8880\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"work:credit-name\"},\"contributorEmail\":{\"value\":\"work@contributor.email\"},\"contributorAttributes\":{\"contributorSequence\":\"FIRST\",\"contributorRole\":\"AUTHOR\"}}]}",
                workEntity.getContributorsJson());
        assertEquals("en", workEntity.getLanguageCode());
        assertEquals(Iso3166Country.AF.name(), workEntity.getIso2Country());
        
        // Source
        assertNull(workEntity.getSourceId());        
        assertNull(workEntity.getClientSourceId());        
        assertNull(workEntity.getElementSourceId());
    }
    
    @Test
    public void clearFieldsFromWorkToWorkEntityTest() throws IllegalAccessException {
        WorkEntity workEntity = getWorkEntity();
        // Verify values are not null
        assertNotNull(workEntity.getCitation());
        assertNotNull(workEntity.getCitationType());
        assertNotNull(workEntity.getIso2Country());
        assertNotNull(workEntity.getJournalTitle());
        assertNotNull(workEntity.getTranslatedTitle());
        assertNotNull(workEntity.getTranslatedTitleLanguageCode());
        assertNotNull(workEntity.getSubtitle());
        
        Work work = jpaJaxbWorkAdapter.toWork(workEntity);
        // Verify values are not null
        assertNotNull(work.getCreatedDate().getValue());
        assertNotNull(work.getLastModifiedDate().getValue());
        assertNotNull(work.getWorkCitation());
        assertNotNull(work.getWorkCitation().getCitation());
        assertNotNull(work.getWorkCitation().getWorkCitationType());        
        assertNotNull(work.getCountry());
        assertNotNull(work.getCountry().getValue());
        assertNotNull(work.getJournalTitle());
        assertNotNull(work.getJournalTitle().getContent());
        assertNotNull(work.getUrl());
        assertNotNull(work.getUrl().getValue());
        assertNotNull(work.getWorkTitle().getTranslatedTitle());
        assertNotNull(work.getWorkTitle().getTranslatedTitle().getContent());
        assertNotNull(work.getWorkTitle().getTranslatedTitle().getLanguageCode());
        assertNotNull(work.getWorkTitle().getSubtitle());
        assertNotNull(work.getWorkTitle().getSubtitle().getContent()); 
        
        // Now clear values on work
        work.setWorkCitation(null);
        work.setCountry(null);
        work.setJournalTitle(null);
        work.setUrl(null);
        work.getWorkTitle().setTranslatedTitle(null);
        work.getWorkTitle().setSubtitle(null);
        
        // Update work entity
        jpaJaxbWorkAdapter.toWorkEntity(work, workEntity);
        
        // Verify citation, country, journal title, url, translated title and subtitle get nullified
        assertNull(workEntity.getCitation());
        assertNull(workEntity.getCitationType());
        assertNull(workEntity.getIso2Country());
        assertNull(workEntity.getJournalTitle());
        assertNull(workEntity.getTranslatedTitle());
        assertNull(workEntity.getTranslatedTitleLanguageCode());
        assertNull(workEntity.getSubtitle());       
        
        // Verify the rest of the fields haven't changed
        WorkEntity workEntity2 = getWorkEntity();
        
        assertEquals(workEntity2.getAddedToProfileDate(), workEntity.getAddedToProfileDate());
        assertEquals(workEntity2.getAssertionOriginClientSourceId(), workEntity.getAssertionOriginClientSourceId());
        assertEquals(workEntity2.getClientSourceId(), workEntity.getClientSourceId());
        assertEquals(workEntity2.getContributorsJson(), workEntity.getContributorsJson());
        assertEquals(workEntity2.getDateCreated(), workEntity.getDateCreated());
        assertEquals(workEntity2.getDescription(), workEntity.getDescription());
        assertEquals(workEntity2.getDisplayIndex(), workEntity.getDisplayIndex());
        assertEquals(workEntity2.getElementAssertionOriginSourceId(), workEntity.getElementAssertionOriginSourceId());
        assertEquals(workEntity2.getElementSourceId(), workEntity.getElementSourceId());
        assertEquals(workEntity2.getId(), workEntity.getId());
        assertEquals(workEntity2.getLanguageCode(), workEntity.getLanguageCode());
        assertEquals(workEntity2.getLastModified(), workEntity.getLastModified());
        assertEquals(workEntity2.getOrcid(), workEntity.getOrcid());
        assertEquals(workEntity2.getPublicationYear(), workEntity.getPublicationYear());
        assertEquals(workEntity2.getPublicationMonth(), workEntity.getPublicationMonth());
        assertEquals(workEntity2.getPublicationDay(), workEntity.getPublicationDay());
        assertEquals(workEntity2.getSourceId(), workEntity.getSourceId());
        assertEquals(workEntity2.getTitle(), workEntity.getTitle());
        assertEquals(workEntity2.getVisibility(), workEntity.getVisibility());
        assertEquals(workEntity2.getWorkType(), workEntity.getWorkType());        
    
        String filteredExtIds = "{\"workExternalIdentifier\":["
                + "{\"relationship\":\"SELF\",\"url\":null,\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"123\"}},"
                + "{\"relationship\":\"PART_OF\",\"url\":null,\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"abc\"}}]}";
        
        assertEquals(filteredExtIds, workEntity.getExternalIdentifiersJson());        
    }

    @Test
    public void fromWorkEntityToWorkTest() throws IllegalAccessException {
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
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.CR.value(), w.getCountry().getValue().value());
        assertEquals("work:citation", w.getWorkCitation().getCitation());
        assertEquals("work:description", w.getShortDescription());
        assertEquals("work:journalTitle", w.getJournalTitle().getContent());
        assertEquals(CitationType.BIBTEX.value(), w.getWorkCitation().getWorkCitationType().value());
        assertEquals(Long.valueOf(12345), w.getPutCode());
        assertEquals(Visibility.LIMITED.value(), w.getVisibility().value());
        assertEquals("work:title", w.getWorkTitle().getTitle().getContent());
        assertEquals("work:subtitle", w.getWorkTitle().getSubtitle().getContent());
        assertEquals("work:translatedTitle", w.getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("ES", w.getWorkTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(WorkType.ARTISTIC_PERFORMANCE.value(), w.getWorkType().value());
        assertNotNull(w.getWorkExternalIdentifiers());
        assertNotNull(w.getWorkExternalIdentifiers().getExternalIdentifier());
        assertEquals(2, w.getWorkExternalIdentifiers().getExternalIdentifier().size());
        
        ExternalID workExtId1 = w.getWorkExternalIdentifiers().getExternalIdentifier().get(0);
        assertNotNull(workExtId1.getValue());
        assertEquals("123", workExtId1.getValue());
        assertNotNull(workExtId1.getType());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value(), workExtId1.getType());
        assertEquals(Relationship.SELF, workExtId1.getRelationship());
        
        ExternalID workExtId2 = w.getWorkExternalIdentifiers().getExternalIdentifier().get(1);
        assertNotNull(workExtId2.getValue());
        assertEquals("abc", workExtId2.getValue());
        assertNotNull(workExtId2.getType());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value(), workExtId2.getType());
        assertEquals(Relationship.PART_OF, workExtId2.getRelationship());
        
        String sourcePath = w.getSource().retrieveSourcePath();
        assertNotNull(sourcePath);
        assertEquals(CLIENT_SOURCE_ID, sourcePath);
        // Identifier URIs should always be http, event if base url is https
        assertEquals("http://testserver.orcid.org/client/APP-0000000000000001", w.getSource().retriveSourceUri());
    }

    @Test
    public void fromWorkEntityToWorkSummaryTest() throws IllegalAccessException {
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
        assertEquals(Relationship.SELF, workExtId1.getRelationship());
        
        ExternalID workExtId2 = ws.getExternalIdentifiers().getExternalIdentifier().get(1);
        assertNotNull(workExtId2.getValue());
        assertEquals("abc", workExtId2.getValue());
        assertNotNull(workExtId2.getType());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value(), workExtId2.getType());
        assertEquals(Relationship.PART_OF, workExtId2.getRelationship());
    }

    @Test
    public void dissertationThesisToDissertationTest() throws IllegalAccessException {
        WorkEntity work = getWorkEntity();
        work.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name());
        
        WorkSummary ws = jpaJaxbWorkAdapter.toWorkSummary(work);
        assertNotNull(ws);
        assertEquals(WorkType.DISSERTATION, ws.getType());
        
        Work w = jpaJaxbWorkAdapter.toWork(work);
        assertNotNull(w);
        assertEquals(WorkType.DISSERTATION, w.getWorkType());        
    
        MinimizedWorkEntity mWork = new MinimizedWorkEntity();
        mWork.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name());
        List<WorkSummary> summaries = jpaJaxbWorkAdapter.toWorkSummaryFromMinimized(Arrays.asList(mWork));
        assertEquals(WorkType.DISSERTATION, summaries.get(0).getType());
    }
    
    @Test
    public void dissertationToDisertationThesisTest() throws JAXBException {
        Work w = getWork(true);
        w.setWorkType(WorkType.DISSERTATION);
        
        WorkEntity we = jpaJaxbWorkAdapter.toWorkEntity(w);
        assertNotNull(we);
        assertEquals(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name(), we.getWorkType());
    }
    
    @Test
    public void dissertationToDissertationTest() throws IllegalAccessException {
        WorkEntity work = getWorkEntity();
        work.setWorkType(WorkType.DISSERTATION.name());
        
        WorkSummary ws = jpaJaxbWorkAdapter.toWorkSummary(work);
        assertNotNull(ws);
        assertEquals(WorkType.DISSERTATION, ws.getType());
        
        Work w = jpaJaxbWorkAdapter.toWork(work);
        assertNotNull(w);
        assertEquals(WorkType.DISSERTATION, w.getWorkType());        
    
        MinimizedWorkEntity mWork = new MinimizedWorkEntity();
        mWork.setWorkType(WorkType.DISSERTATION.name());
        List<WorkSummary> summaries = jpaJaxbWorkAdapter.toWorkSummaryFromMinimized(Arrays.asList(mWork));
        assertEquals(WorkType.DISSERTATION, summaries.get(0).getType());
    }
    
    private Work getWork(boolean full) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Work.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/work-2.0.xml";
        if (full) {
            name = "/record_2.0/samples/read_samples/work-full-2.0.xml";
        }
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Work) unmarshaller.unmarshal(inputStream);
    }

    private WorkEntity getWorkEntity() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        WorkEntity work = new WorkEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(work, date);
        work.setOrcid("0000-0000-0000-0001");
        work.setVisibility(Visibility.LIMITED.name());
        work.setDisplayIndex(1234567890L);
        work.setClientSourceId(CLIENT_SOURCE_ID);        
        work.setCitation("work:citation");
        work.setCitationType(CitationType.BIBTEX.name());
        work.setDescription("work:description");
        work.setId(12345L);
        work.setIso2Country(Iso3166Country.CR.name());
        work.setJournalTitle("work:journalTitle");
        work.setLanguageCode("EN");
        work.setPublicationDate(new PublicationDateEntity(2000, 1, 1));
        work.setSubtitle("work:subtitle");
        work.setTitle("work:title");
        work.setTranslatedTitle("work:translatedTitle");
        work.setTranslatedTitleLanguageCode("ES");
        work.setWorkType(WorkType.ARTISTIC_PERFORMANCE.name());
        work.setWorkUrl("work:url");
        work.setContributorsJson("{\"contributor\":[]}");
        work.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"relationship\":\"VERSION_OF\",\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"123\"}},"
                + "{\"relationship\":\"SELF\",\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"123\"}},"
                + "{\"relationship\":\"PART_OF\",\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"abc\"}},"
                + "{\"relationship\":\"VERSION_OF\",\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"456\"}}]}");
        return work;
    }
    
    
    @Test
    public void clearPublicationDateFieldsForWorkTest() throws IllegalAccessException {
        WorkEntity work = getWorkEntity();
        assertNotNull(work);
        work.setPublicationDate(new PublicationDateEntity(2000, null, null));
        jpaJaxbWorkAdapter.toWork(work);
        assertNull(work.getPublicationMonth());
        assertNull(work.getPublicationDay());
        assertEquals(Integer.valueOf(2000), work.getPublicationYear());
        work.setPublicationDate(new PublicationDateEntity(null, null, null));
        jpaJaxbWorkAdapter.toWork(work);
        assertNull(work.getPublicationYear());
    }
     
}
