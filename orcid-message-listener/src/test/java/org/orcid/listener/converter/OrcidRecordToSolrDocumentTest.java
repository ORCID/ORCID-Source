package org.orcid.listener.converter;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;
import org.junit.Test;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.listener.solr.OrcidRecordToSolrDocument;
import org.orcid.utils.solr.entities.OrcidSolrDocument;

public class OrcidRecordToSolrDocumentTest {
        
    @Test
    public void testNonSchemaExternalID() throws JAXBException{
        //as above, but with PDB identifier
        Record record = getRecord("/record_3.0/samples/read_samples/record-3.0.xml");
        OrcidRecordToSolrDocument v20 = new  OrcidRecordToSolrDocument(false);
        OrcidSolrDocument v20Doc = v20.convert(record,new ArrayList<Funding>(), new ArrayList<ResearchResource>());
        
        Assert.assertEquals("cite-self",v20Doc.getSelfIds().get("cit-self").get(0));
        Assert.assertEquals("pdb-value-self",v20Doc.getSelfIds().get("pdb-self").get(0));
        Assert.assertEquals("cit-part-of",v20Doc.getPartOfIds().get("cit-part-of").get(0));
        Assert.assertEquals("pdb-value-part-of",v20Doc.getPartOfIds().get("pdb-part-of").get(0));
        Assert.assertTrue(v20Doc.getDigitalObjectIds().containsAll(v20Doc.getSelfIds().get("doi-self")));
        Assert.assertTrue(v20Doc.getSelfIds().get("doi-self").containsAll(v20Doc.getDigitalObjectIds()));
        Assert.assertTrue(v20Doc.getOtherIdentifierType().contains("pdb-value-self"));
        Assert.assertTrue(v20Doc.getOtherIdentifierType().contains("pdb-value-part-of"));
    }
    
    @Test
    public void testOrgIDAndGrantNumber() throws JAXBException {
        Record record = getRecord("/v20record.xml");
        OrcidRecordToSolrDocument v20 = new  OrcidRecordToSolrDocument(false);
        OrcidSolrDocument v20Doc = v20.convert(record,new ArrayList<Funding>(), new ArrayList<ResearchResource>());
        
        Assert.assertTrue(v20Doc.getOrganisationIds().containsKey("ringgold-org-id"));
        Assert.assertTrue(v20Doc.getOrganisationIds().get("ringgold-org-id").contains("5488"));
        Assert.assertTrue(v20Doc.getOrganisationIds().get("ringgold-org-id").contains("4925"));
        Assert.assertTrue(v20Doc.getOrganisationNames().get("affiliation-org-name").contains("Open University"));
        Assert.assertTrue(v20Doc.getOrganisationNames().get("affiliation-org-name").contains("British Library"));
        /*
        Assert.assertTrue(v20Doc.getOrganisationNames().get("funding-org-name").contains("THOR - Technical and Human Infrastructure for Open Research"));
        Assert.assertTrue(v20Doc.getGrantNumbers().contains("H2020-EU.1.4.1.3."));
        */
    }
    
    private Record getRecord(String name) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Record.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = this.getClass().getResourceAsStream(name);
        return (Record) unmarshaller.unmarshal(inputStream);
    }

}
