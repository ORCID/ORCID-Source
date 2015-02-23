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
import static org.junit.Assert.assertNull;

import java.io.InputStream;

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
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
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
        assertNull(workEntity.getSubtitle());
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
                "{\"scope\":null,\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}}]}",
                workEntity.getExternalIdentifiersJson());
        assertEquals("http://tempuri.org", workEntity.getWorkUrl());
        assertEquals(
                "{\"contributor\":[{\"contributorOrcid\":{\"value\":null,\"valueAsString\":null,\"uri\":\"http://orcid.org/8888-8888-8888-8880\",\"path\":\"8888-8888-8888-8880\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"work:credit-name\",\"visibility\":\"PRIVATE\"},\"contributorEmail\":{\"value\":\"work@contributor.email\"},\"contributorAttributes\":{\"contributorSequence\":\"FIRST\",\"contributorRole\":\"AUTHOR\"}}]}",
                workEntity.getContributorsJson());
        assertEquals("en", workEntity.getLanguageCode());
        assertEquals(Iso3166Country.AF, workEntity.getIso2Country());
    }

    private Work getWork() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Work.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = getClass().getResourceAsStream("/record_2.0_rc1/samples/work-2.0_rc1.xml");
        return (Work) unmarshaller.unmarshal(inputStream);
    }
}
