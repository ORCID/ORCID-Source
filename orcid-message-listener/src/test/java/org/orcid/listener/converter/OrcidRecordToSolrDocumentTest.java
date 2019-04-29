package org.orcid.listener.converter;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.listener.solr.OrcidRecordToSolrDocument;
import org.orcid.utils.solr.entities.OrcidSolrDocument;

public class OrcidRecordToSolrDocumentTest {
        
    @Test
    public void convertTest() throws JAXBException{
        //as above, but with PDB identifier
        Record record = getRecord("/record_3.0/samples/read_samples/record-3.0.xml");
        OrcidRecordToSolrDocument v3 = new  OrcidRecordToSolrDocument(false);
        OrcidSolrDocument v3Doc = v3.convert(record,new ArrayList<Funding>(), new ArrayList<ResearchResource>());
        
        assertEquals("8888-8888-8888-8880", v3Doc.getOrcid());
        assertEquals("credit-name", v3Doc.getCreditName());
        assertEquals("give-names", v3Doc.getGivenNames());
        assertEquals("family-name", v3Doc.getFamilyName());
        assertEquals("give-names family-name", v3Doc.getGivenAndFamilyNames());
        assertEquals(1, v3Doc.getEmailAddresses().size());
        assertEquals("user1@email.com", v3Doc.getEmailAddresses().get(0));
        assertEquals(1, v3Doc.getExternalIdReferences().size());
        assertEquals("value-1", v3Doc.getExternalIdReferences().get(0));
        assertEquals(1, v3Doc.getExternalIdSources().size());
        assertEquals("8888-8888-8888-8880", v3Doc.getExternalIdSources().get(0));
        assertEquals(1, v3Doc.getExternalIdReferences().size());
        assertEquals("value-1", v3Doc.getExternalIdReferences().get(0));
        assertEquals(1, v3Doc.getExternalIdTypeAndValue().size());
        assertEquals("type-1=value-1", v3Doc.getExternalIdTypeAndValue().get(0));
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);
        assertEquals("", v3Doc);        
    }
        
    private Record getRecord(String name) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Record.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = this.getClass().getResourceAsStream(name);
        return (Record) unmarshaller.unmarshal(inputStream);
    }

}
