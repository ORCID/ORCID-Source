package org.orcid.core.adapter.jsonidentifier.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.jaxb.model.v3.dev1.common.TransientError;
import org.orcid.jaxb.model.v3.dev1.record.ExternalID;
import org.orcid.jaxb.model.v3.dev1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.dev1.record.Work;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JSONWorkExternalIdentifiersConverterV3Test {

    @Resource
    PIDNormalizationService norm;
    @Resource
    LocaleManager localeManager;
    private JSONWorkExternalIdentifiersConverterV3 converter;

    @Before
    public void initMocks(){
        converter = new JSONWorkExternalIdentifiersConverterV3(norm,localeManager);
    }
    
    @Test
    public void testConvertTo() throws JAXBException {
        Work work = getWork();
        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}}]}",
                converter.convertTo(work.getExternalIdentifiers(), null));
    }

    @Test
    public void testConvertFrom() {
        WorkEntity workEntity = getWorkEntity();
        ExternalIDs entityIDs = converter.convertFrom(workEntity.getExternalIdentifiersJson(), null);
        assertEquals(1, entityIDs.getExternalIdentifier().size());

        ExternalID externalID = entityIDs.getExternalIdentifier().get(0);
        assertEquals("123", externalID.getValue());
        assertNotNull(externalID.getType());
        assertEquals("123",externalID.getNormalized().getValue());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value(), externalID.getType());
    }

    @Test
    public void testConvertFromNormalize() {
        WorkEntity workEntity = getWorkEntity();
        workEntity.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"doi:10.1/123\"}}]}");
        ExternalIDs entityIDs = converter.convertFrom(workEntity.getExternalIdentifiersJson(), null);
        assertEquals(1, entityIDs.getExternalIdentifier().size());
        ExternalID externalID = entityIDs.getExternalIdentifier().get(0);
        assertEquals("doi:10.1/123", externalID.getValue());
        assertEquals("10.1/123",externalID.getNormalized().getValue());
        assertNotNull(externalID.getType());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.DOI.value(), externalID.getType());
    }
    
    @Test
    public void testConvertFromNormalizeError() {
        WorkEntity workEntity = getWorkEntity();
        workEntity.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"123\"}}]}");
        ExternalIDs entityIDs = converter.convertFrom(workEntity.getExternalIdentifiersJson(), null);
        assertEquals(1, entityIDs.getExternalIdentifier().size());
        ExternalID externalID = entityIDs.getExternalIdentifier().get(0);
        assertEquals("123", externalID.getValue());
        assertEquals("",externalID.getNormalized().getValue());
        assertEquals("8001",externalID.getNormalizedError().getErrorCode());
        assertEquals("Cannot normalize identifier value doi:123",externalID.getNormalizedError().getErrorMessage());
        assertNotNull(externalID.getType());
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.DOI.value(), externalID.getType());
    }

    private Work getWork() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Work.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/work-full-2.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Work) unmarshaller.unmarshal(inputStream);
    }

    private WorkEntity getWorkEntity() {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        WorkEntity work = new WorkEntity();
        work.setDateCreated(date);
        work.setLastModified(date);
        work.setOrcid("0000-0000-0000-0001");
        work.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        work.setDisplayIndex(1234567890L);
        work.setClientSourceId("APP-5555555555555555");
        work.setCitation("work:citation");
        work.setCitationType(org.orcid.jaxb.model.record_v2.CitationType.BIBTEX);
        work.setDateCreated(date);
        work.setDescription("work:description");
        work.setId(12345L);
        work.setIso2Country(org.orcid.jaxb.model.common_v2.Iso3166Country.CR);
        work.setJournalTitle("work:journalTitle");
        work.setLanguageCode("EN");
        work.setLastModified(date);
        work.setPublicationDate(new PublicationDateEntity(2000, 1, 1));
        work.setSubtitle("work:subtitle");
        work.setTitle("work:title");
        work.setTranslatedTitle("work:translatedTitle");
        work.setTranslatedTitleLanguageCode("ES");
        work.setWorkType(org.orcid.jaxb.model.record_v2.WorkType.ARTISTIC_PERFORMANCE);
        work.setWorkUrl("work:url");
        work.setContributorsJson("{\"contributor\":[]}");
        work.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"123\"}}]}");
        return work;
    }

}
