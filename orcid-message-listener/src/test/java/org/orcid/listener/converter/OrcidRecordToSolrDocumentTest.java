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
package org.orcid.listener.converter;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Assert;
import org.junit.Test;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.record_rc4.Record;
import org.orcid.listener.converters.OrcidProfileToSolrDocument;
import org.orcid.listener.converters.OrcidRecordToSolrDocument;
import org.orcid.utils.solr.entities.OrcidSolrDocument;

public class OrcidRecordToSolrDocumentTest {

    @Test
    public void test12SameAs20() throws IOException, SolrServerException, JAXBException{
        OrcidProfileToSolrDocument v12 = new  OrcidProfileToSolrDocument();
        OrcidRecordToSolrDocument v20 = new  OrcidRecordToSolrDocument(false);
        Record record = getRecord("/v20record.xml");
        OrcidMessage message = getOrcidMessage();
        OrcidSolrDocument v12Doc = v12.convert(message.getOrcidProfile());
        OrcidSolrDocument v20Doc = v20.convert(record,message.getOrcidProfile().toString());
        System.out.println(v12Doc);
        System.out.println(v20Doc);
        Assert.assertEquals(v12Doc.getOrcid(), v20Doc.getOrcid());
        Assert.assertEquals(v12Doc.getFamilyName(), v20Doc.getFamilyName());
        Assert.assertEquals(v12Doc.getGivenNames(), v20Doc.getGivenNames());
        Assert.assertEquals(v12Doc.getGivenAndFamilyNames(), v20Doc.getGivenAndFamilyNames());
        Assert.assertTrue(v12Doc.getDigitalObjectIds().containsAll(v20Doc.getDigitalObjectIds()));
        Assert.assertTrue(v20Doc.getDigitalObjectIds().containsAll(v12Doc.getDigitalObjectIds()));
        Assert.assertTrue(v12Doc.getWorkTitles().containsAll(v20Doc.getWorkTitles()));
        Assert.assertTrue(v20Doc.getWorkTitles().containsAll(v12Doc.getWorkTitles()));

        Assert.assertTrue(v12Doc.getCit().containsAll(v20Doc.getCit()));
        Assert.assertTrue(v20Doc.getCit().containsAll(v12Doc.getCit()));
        Assert.assertTrue(v12Doc.getAgr().containsAll(v20Doc.getAgr()));
        Assert.assertTrue(v20Doc.getAgr().containsAll(v12Doc.getAgr()));
        
        Assert.assertEquals(v12Doc.getProfileLastModifiedDate(), v20Doc.getProfileLastModifiedDate());
        Assert.assertEquals(v12Doc.getProfileSubmissionDate(), v20Doc.getProfileSubmissionDate());
    }
    
    @Test
    public void testNonSchemaExternalID() throws JAXBException{
        //as above, but with PDB identifier
        Record record = getRecord("/v20recordWithPDB.xml");
        OrcidRecordToSolrDocument v20 = new  OrcidRecordToSolrDocument(false);
        OrcidSolrDocument v20Doc = v20.convert(record,"");
        System.out.println(v20Doc);
        
        Assert.assertEquals("cite-self",v20Doc.getSelfIds().get("cit-self").get(0));
        Assert.assertEquals("pdb-value-self",v20Doc.getSelfIds().get("pdb-self").get(0));
        Assert.assertEquals("cit-part-of",v20Doc.getPartOfIds().get("cit-part-of").get(0));
        Assert.assertEquals("pdb-value-part-of",v20Doc.getPartOfIds().get("pdb-part-of").get(0));
        Assert.assertTrue(v20Doc.getDigitalObjectIds().containsAll(v20Doc.getSelfIds().get("doi-self")));
        Assert.assertTrue(v20Doc.getSelfIds().get("doi-self").containsAll(v20Doc.getDigitalObjectIds()));
        Assert.assertTrue(v20Doc.getOtherIdentifierType().contains("pdb-value-self"));
        Assert.assertTrue(v20Doc.getOtherIdentifierType().contains("pdb-value-part-of"));
    }
    
    private OrcidMessage getOrcidMessage() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance("org.orcid.jaxb.model.message");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = this.getClass().getResourceAsStream("/v12profile.xml");
        return (OrcidMessage) unmarshaller.unmarshal(inputStream);
    }
    
    private Record getRecord(String name) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Record.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = this.getClass().getResourceAsStream(name);
        return (Record) unmarshaller.unmarshal(inputStream);
    }

}
