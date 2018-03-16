package org.orcid.core.adapter.jsonidentifier.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkType;

public class JSONWorkExternalIdentifiersConverterV1Test {

    private JSONWorkExternalIdentifiersConverterV1 converter = new JSONWorkExternalIdentifiersConverterV1();

    @Test
    public void testConvertTo() throws JAXBException {
        WorkExternalIdentifiers workExternalIdentifiers = getWorkIdentifiers();
        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":\"self\",\"url\":null,\"workExternalIdentifierType\":\"doi\",\"workExternalIdentifierId\":{\"content\":\"work1-doi1\"}},{\"relationship\":\"self\",\"url\":null,\"workExternalIdentifierType\":\"pmid\",\"workExternalIdentifierId\":{\"content\":\"work1-pmid\"}},{\"relationship\":\"self\",\"url\":null,\"workExternalIdentifierType\":\"source-work-id\",\"workExternalIdentifierId\":{\"content\":\"work1-source-id\"}}]}",
                converter.convertTo(workExternalIdentifiers, WorkType.BOOK));

    }

    @Test
    public void testConvertFrom() {
        WorkExternalIdentifiers externalIdentifiers = converter.convertFrom(
                "{\"workExternalIdentifier\":[{\"relationship\":\"self\",\"url\":null,\"workExternalIdentifierType\":\"doi\",\"workExternalIdentifierId\":{\"content\":\"work1-doi1\"}},{\"relationship\":\"self\",\"url\":null,\"workExternalIdentifierType\":\"pmid\",\"workExternalIdentifierId\":{\"content\":\"work1-pmid\"}},{\"relationship\":\"self\",\"url\":null,\"workExternalIdentifierType\":\"source-work-id\",\"workExternalIdentifierId\":{\"content\":\"work1-source-id\"}}]}");
        assertNotNull(externalIdentifiers);
        assertEquals(3, externalIdentifiers.getWorkExternalIdentifier().size());
        
        WorkExternalIdentifier workExternalIdentifier = externalIdentifiers.getWorkExternalIdentifier().get(0);
        assertEquals(WorkExternalIdentifierType.DOI, workExternalIdentifier.getWorkExternalIdentifierType());
        assertEquals("work1-doi1", workExternalIdentifier.getWorkExternalIdentifierId().getContent());
        
        workExternalIdentifier = externalIdentifiers.getWorkExternalIdentifier().get(1);
        assertEquals(WorkExternalIdentifierType.PMID, workExternalIdentifier.getWorkExternalIdentifierType());
        assertEquals("work1-pmid", workExternalIdentifier.getWorkExternalIdentifierId().getContent());
        
        workExternalIdentifier = externalIdentifiers.getWorkExternalIdentifier().get(2);
        assertEquals(WorkExternalIdentifierType.SOURCE_WORK_ID, workExternalIdentifier.getWorkExternalIdentifierType());
        assertEquals("work1-source-id", workExternalIdentifier.getWorkExternalIdentifierId().getContent());
    }

    private WorkExternalIdentifiers getWorkIdentifiers() {
        WorkExternalIdentifiers workExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier workExternalIdentifier1 = new WorkExternalIdentifier();
        workExternalIdentifier1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        workExternalIdentifier1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work1-doi1"));
        WorkExternalIdentifier workExternalIdentifier2 = new WorkExternalIdentifier();
        workExternalIdentifier2.setWorkExternalIdentifierType(WorkExternalIdentifierType.PMID);
        workExternalIdentifier2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work1-pmid"));
        WorkExternalIdentifier workExternalIdentifier3 = new WorkExternalIdentifier();
        workExternalIdentifier3.setWorkExternalIdentifierType(WorkExternalIdentifierType.SOURCE_WORK_ID);
        workExternalIdentifier3.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work1-source-id"));
        workExternalIdentifiers.getWorkExternalIdentifier().add(workExternalIdentifier1);
        workExternalIdentifiers.getWorkExternalIdentifier().add(workExternalIdentifier2);
        workExternalIdentifiers.getWorkExternalIdentifier().add(workExternalIdentifier3);
        return workExternalIdentifiers;
    }

}
