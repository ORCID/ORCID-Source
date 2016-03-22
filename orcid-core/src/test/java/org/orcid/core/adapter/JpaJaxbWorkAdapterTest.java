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
package org.orcid.core.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
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
public class JpaJaxbWorkAdapterTest {

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
        assertEquals(Visibility.PRIVATE, workEntity.getVisibility());
        SourceEntity sourceEntity = workEntity.getSource();
        assertEquals("8888-8888-8888-8880", sourceEntity.getSourceId());
        assertNotNull(workEntity);
        assertEquals(123, workEntity.getId().longValue());
        assertEquals("common:title", workEntity.getTitle());
        assertTrue(PojoUtil.isEmpty(workEntity.getSubtitle()));
        assertEquals("common:translated-title", workEntity.getTranslatedTitle());
        assertEquals("en", workEntity.getTranslatedTitleLanguageCode());
        assertEquals("work:short-description", workEntity.getDescription());
        assertEquals(CitationType.FORMATTED_UNSPECIFIED, workEntity.getCitationType());
        assertEquals(WorkType.ARTISTIC_PERFORMANCE, workEntity.getWorkType());
        PublicationDateEntity publicationDateEntity = workEntity.getPublicationDate();
        assertNotNull(publicationDateEntity);
        assertEquals(1848, publicationDateEntity.getYear().intValue());
        assertEquals(02, publicationDateEntity.getMonth().intValue());
        assertEquals(02, publicationDateEntity.getDay().intValue());
        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}}]}",
                workEntity.getExternalIdentifiersJson());
        assertEquals("http://tempuri.org", workEntity.getWorkUrl());
        assertEquals(
                "{\"contributor\":[{\"contributorOrcid\":{\"uri\":\"http://orcid.org/8888-8888-8888-8880\",\"path\":\"8888-8888-8888-8880\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"work:credit-name\",\"visibility\":\"PRIVATE\"},\"contributorEmail\":{\"value\":\"work@contributor.email\"},\"contributorAttributes\":{\"contributorSequence\":\"FIRST\",\"contributorRole\":\"AUTHOR\"}}]}",
                workEntity.getContributorsJson());
        assertEquals("en", workEntity.getLanguageCode());
        assertEquals(Iso3166Country.AF, workEntity.getIso2Country());
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
        assertEquals(org.orcid.jaxb.model.common_rc2.Iso3166Country.CR.value(), w.getCountry().getValue().value());
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
        assertEquals(1, w.getWorkExternalIdentifiers().getExternalIdentifier().size());
        ExternalID workExtId = w.getWorkExternalIdentifiers().getExternalIdentifier().get(0);
        assertNotNull(workExtId.getValue());
        assertEquals("123", workExtId.getValue());
        assertNotNull(workExtId.getType());
        assertEquals(ExternalIDType.AGR.value(), workExtId.getType());
        String sourcePath = w.getSource().retrieveSourcePath();
        assertNotNull(sourcePath);
        assertEquals("APP-5555555555555555", sourcePath);
        // Identifier URIs should always be http, event if base url is https
        assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", w.getSource().retriveSourceUri());
    }

    @Test
    public void fromProfileWorkEntityToWorkSummaryTest() {
        WorkEntity work = getWorkEntity();
        assertNotNull(work);
        WorkSummary ws = jpaJaxbWorkAdapter.toWorkSummary(work);
        assertNotNull(ws);
        assertEquals(Long.valueOf(12345), ws.getPutCode());
        assertEquals(Visibility.LIMITED.value(), ws.getVisibility().value());
        assertEquals("1234567890", ws.getDisplayIndex());
        assertNotNull(ws.getExternalIdentifiers());
        assertNotNull(ws.getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, ws.getExternalIdentifiers().getExternalIdentifier().size());
        ExternalID workExtId = ws.getExternalIdentifiers().getExternalIdentifier().get(0);
        assertNotNull(workExtId.getValue());
        assertEquals("123", workExtId.getValue());
        assertNotNull(workExtId.getType());
        assertEquals(ExternalIDType.AGR.value(), workExtId.getType());
    }

    private Work getWork(boolean full) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Work.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0_rc2/samples/work-2.0_rc2.xml";
        if (full) {
            name = "/record_2.0_rc2/samples/work-full-2.0_rc2.xml";
        }
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Work) unmarshaller.unmarshal(inputStream);
    }


    private WorkEntity getWorkEntity() {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        WorkEntity work = new WorkEntity();
        work.setDateCreated(date);
        work.setLastModified(date);
        work.setProfile(new ProfileEntity("0000-0000-0000-0001"));
        work.setVisibility(Visibility.LIMITED);
        work.setDisplayIndex(1234567890L);
        work.setSource(new SourceEntity("APP-5555555555555555"));        
        work.setCitation("work:citation");
        work.setCitationType(CitationType.BIBTEX);
        work.setDateCreated(date);
        work.setDescription("work:description");
        work.setId(12345L);
        work.setIso2Country(org.orcid.jaxb.model.message.Iso3166Country.CR);
        work.setJournalTitle("work:journalTitle");
        work.setLanguageCode("EN");
        work.setLastModified(date);
        work.setPublicationDate(new PublicationDateEntity(2000, 1, 1));
        work.setSubtitle("work:subtitle");
        work.setTitle("work:title");
        work.setTranslatedTitle("work:translatedTitle");
        work.setTranslatedTitleLanguageCode("ES");
        work.setWorkType(WorkType.ARTISTIC_PERFORMANCE);
        work.setWorkUrl("work:url");
        work.setContributorsJson("{\"contributor\":[]}");
        work.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"123\"}}]}");
        return work;
    }
}
