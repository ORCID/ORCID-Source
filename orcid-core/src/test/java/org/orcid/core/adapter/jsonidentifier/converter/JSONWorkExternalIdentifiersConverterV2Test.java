package org.orcid.core.adapter.jsonidentifier.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.utils.DateUtils;

public class JSONWorkExternalIdentifiersConverterV2Test {

    private JSONWorkExternalIdentifiersConverterV2 converter = new JSONWorkExternalIdentifiersConverterV2();

    @Test
    public void testConvertTo() throws JAXBException {
        Work work = getWork();
        assertEquals("{\"workExternalIdentifier\":[{\"relationship\":\"SELF\",\"url\":{\"value\":\"http://orcid.org\"},\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"work:external-identifier-id\"}}]}",
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
        assertEquals(org.orcid.jaxb.model.message.WorkExternalIdentifierType.AGR.value(), externalID.getType());
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
        work.setVisibility(Visibility.LIMITED.name());
        work.setDisplayIndex(1234567890L);
        work.setClientSourceId("APP-5555555555555555");
        work.setCitation("work:citation");
        work.setCitationType(CitationType.BIBTEX.name());
        work.setDateCreated(date);
        work.setDescription("work:description");
        work.setId(12345L);
        work.setIso2Country(Iso3166Country.CR.name());
        work.setJournalTitle("work:journalTitle");
        work.setLanguageCode("EN");
        work.setLastModified(date);
        work.setPublicationDate(new PublicationDateEntity(2000, 1, 1));
        work.setSubtitle("work:subtitle");
        work.setTitle("work:title");
        work.setTranslatedTitle("work:translatedTitle");
        work.setTranslatedTitleLanguageCode("ES");
        work.setWorkType(WorkType.ARTISTIC_PERFORMANCE.name());
        work.setWorkUrl("work:url");
        work.setContributorsJson("{\"contributor\":[]}");
        work.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"123\"}}]}");
        return work;
    }

}
