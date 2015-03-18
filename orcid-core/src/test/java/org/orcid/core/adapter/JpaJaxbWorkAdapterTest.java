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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record.summary.WorkSummary;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Will Simpson
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbWorkAdapterTest {

    @Resource
    private JpaJaxbWorkAdapter jpaJaxbWorkAdapter;

    @Test
    public void testToWorkEntity() throws JAXBException {
        Work work = getWork();
        assertNotNull(work);
        ProfileWorkEntity profileWorkEntity = jpaJaxbWorkAdapter.toProfileWorkEntity(work);
        assertNotNull(profileWorkEntity);
        assertEquals(Visibility.PRIVATE, profileWorkEntity.getVisibility());
        SourceEntity sourceEntity = profileWorkEntity.getSource();
        assertEquals("8888-8888-8888-8880", sourceEntity.getSourceId());
        WorkEntity workEntity = profileWorkEntity.getWork();
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
                "{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}}]}",
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
        ProfileWorkEntity pw = getProfileWorkEntity();
        assertNotNull(pw);
        Work w = jpaJaxbWorkAdapter.toWork(pw);
        assertNotNull(w);
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.CR.value(), w.getCountry().getValue().value());
        assertEquals("work:citation", w.getWorkCitation().getCitation());
        assertEquals("work:description", w.getShortDescription());
        assertEquals("work:journalTitle", w.getJournalTitle().getContent());
        assertEquals(CitationType.BIBTEX.value(), w.getWorkCitation().getWorkCitationType().value());
        assertEquals("12345", w.getPutCode());
        assertEquals(Visibility.LIMITED.value(), w.getVisibility().value());
        assertEquals("work:title", w.getWorkTitle().getTitle().getContent());
        assertEquals("work:subtitle", w.getWorkTitle().getSubtitle().getContent());
        assertEquals("work:translatedTitle", w.getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("ES", w.getWorkTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(WorkType.ARTISTIC_PERFORMANCE.value(), w.getWorkType().value());
        assertNotNull(w.getWorkExternalIdentifiers());
        assertNotNull(w.getWorkExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, w.getWorkExternalIdentifiers().getExternalIdentifier().size());
        org.orcid.jaxb.model.record.WorkExternalIdentifier workExtId = w.getWorkExternalIdentifiers().getExternalIdentifier().get(0);
        assertNotNull(workExtId.getWorkExternalIdentifierId());
        assertEquals("123", workExtId.getWorkExternalIdentifierId().getContent());
        assertNotNull(workExtId.getWorkExternalIdentifierType());
        assertEquals(WorkExternalIdentifierType.AGR.value(), workExtId.getWorkExternalIdentifierType().value());
    }

    @Test
    public void fromProfileWorkEntityToWorkSummaryTest() {
        ProfileWorkEntity pw = getProfileWorkEntity();
        assertNotNull(pw);
        WorkSummary ws = jpaJaxbWorkAdapter.toWorkSummary(pw);
        assertNotNull(ws);
        assertEquals("12345", ws.getPutCode());
        assertEquals(Visibility.LIMITED.value(), ws.getVisibility().value());
        assertEquals("1234567890", ws.getDisplayIndex());
        assertNotNull(ws.getExternalIdentifiers());
        assertNotNull(ws.getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, ws.getExternalIdentifiers().getExternalIdentifier().size());
        org.orcid.jaxb.model.record.WorkExternalIdentifier workExtId = ws.getExternalIdentifiers().getExternalIdentifier().get(0);
        assertNotNull(workExtId.getWorkExternalIdentifierId());
        assertEquals("123", workExtId.getWorkExternalIdentifierId().getContent());
        assertNotNull(workExtId.getWorkExternalIdentifierType());
        assertEquals(WorkExternalIdentifierType.AGR.value(), workExtId.getWorkExternalIdentifierType().value());
    }

    private Work getWork() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Work.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = getClass().getResourceAsStream("/record_2.0_rc1/samples/work-2.0_rc1.xml");
        return (Work) unmarshaller.unmarshal(inputStream);
    }

    private ProfileWorkEntity getProfileWorkEntity() {
        Date date = new Date();
        ProfileWorkEntity result = new ProfileWorkEntity();
        result.setDateCreated(date);
        result.setLastModified(date);
        result.setProfile(new ProfileEntity("0000-0000-0000-0001"));
        result.setVisibility(Visibility.LIMITED);
        result.setDisplayIndex(1234567890L);

        WorkEntity work = new WorkEntity();
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

        result.setWork(work);
        return result;
    }
}
