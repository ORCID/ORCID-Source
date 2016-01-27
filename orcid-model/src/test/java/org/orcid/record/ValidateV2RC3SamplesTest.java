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
package org.orcid.record;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Test;
import org.orcid.jaxb.model.common_rc3.Url;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.notification.custom.MarshallingTest;
import org.orcid.jaxb.model.record_rc3.Relationship;
import org.orcid.jaxb.model.record_rc3.ExternalID;
import org.orcid.jaxb.model.record_rc3.Funding;
import org.orcid.jaxb.model.record_rc3.PeerReview;
import org.orcid.jaxb.model.record_rc3.Person;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc3.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ValidateV2RC3SamplesTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateV2RC3SamplesTest.class);

    String[] sampleNames = { "funding", "person", "record", "work", "peer-review","activities" };

    @Test
    public void Test() throws SAXException, IOException {
        for (String name : sampleNames) {
            validateSampleXML(name);
        }
    }
    
    @Test
    public void testFunding() throws SAXException, IOException, JAXBException, ParserConfigurationException{
        Funding funding = (Funding) unmarshallFromPath("/record_2.0_rc3/samples/funding-2.0_rc3.xml", Funding.class);
        assertEquals("funding:organization-defined-type",funding.getOrganizationDefinedType().getContent());
        assertNotNull(funding.getExternalIdentifiers());
        assertNotNull(funding.getExternalIdentifiers().getExternalIdentifiers());
        Assert.notEmpty(funding.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals("grant_number",funding.getExternalIdentifiers().getExternalIdentifiers().get(0).getType());
        assertEquals("funding:external-identifier-value",funding.getExternalIdentifiers().getExternalIdentifiers().get(0).getValue());
        assertEquals(new Url("http://tempuri.org"),funding.getExternalIdentifiers().getExternalIdentifiers().get(0).getUrl());
        assertEquals(Relationship.SELF, funding.getExternalIdentifiers().getExternalIdentifiers().get(0).getRelationship());
        Validator validator = getValidator("funding");
        validator.validate(marshall(Funding.class, funding));
        validator.validate(marshallToDOM(Funding.class, funding));        
    }
    
    /**
     * <external-identifier:external-identifiers>
                <external-identifier:external-identifier visibility="public" put-code="1">
                        <common:external-id-type>type-1</common:external-id-type>
                        <common:external-id-value>value-1</common:external-id-value>
                        <common:external-id-url>http://url.com/1</common:external-id-url>
                        <common:created-date>2001-12-31T12:00:00</common:created-date>
                        <common:last-modified-date>2001-12-31T12:00:00</common:last-modified-date>
                        <common:source>
                                <common:source-orcid>
                                        <common:uri>http://orcid.org/8888-8888-8888-8880</common:uri>
                                        <common:path>8888-8888-8888-8880</common:path>
                                        <common:host>orcid.org</common:host>
                                </common:source-orcid>
                                <common:source-name />
                        </common:source>
                </external-identifier:external-identifier>
        </external-identifier:external-identifiers>
        
     * @throws SAXException
     * @throws IOException
     * @throws JAXBException
     * @throws ParserConfigurationException
     */
    @Test
    public void testPerson() throws SAXException, IOException, JAXBException, ParserConfigurationException{
        Person person = (Person) unmarshallFromPath("/record_2.0_rc3/samples/person-2.0_rc3.xml", Person.class);        
        assertEquals("credit-name",person.getName().getCreditName().getContent());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifier().size());
        PersonExternalIdentifier id = person.getExternalIdentifiers().getExternalIdentifier().get(0);
        assertEquals("type-1",id.getType());
        assertEquals("value-1",id.getValue());
        assertEquals(new Url("http://url.com/1"),id.getUrl());
        assertNull(id.getRelationship());
        assertNotNull(id.getCreatedDate().getValue());
        assertNotNull(id.getLastModifiedDate().getValue());
        assertEquals(new Long(1),id.getPutCode());
        assertEquals(Visibility.PUBLIC,id.getVisibility());
        Validator validator = getValidator("person");
        validator.validate(marshall(Person.class, person));
        validator.validate(marshallToDOM(Person.class, person));

    }

    /**
     * <common:external-ids>
                <common:external-id>
                        <common:external-id-type>agr</common:external-id-type>
                        <common:external-id-value>work:external-identifier-id</common:external-id-value>                        
                        <common:external-id-url>http://orcid.org</common:external-id-url>
                        <common:external-id-relationship>self</common:external-id-relationship>
                </common:external-id>
        </common:external-ids>
        
     * @throws SAXException
     * @throws IOException
     * @throws JAXBException
     * @throws ParserConfigurationException
     */
    @Test
    public void testWork() throws SAXException, IOException, JAXBException, ParserConfigurationException{
        Work work = (Work) unmarshallFromPath("/record_2.0_rc3/samples/work-2.0_rc3.xml", Work.class);                
        ExternalID id = work.getExternalIdentifiers().getExternalIdentifiers().get(0);
        assertEquals("agr",id.getType());
        assertEquals("work:external-identifier-id",id.getValue());
        assertEquals(new Url("http://orcid.org"),id.getUrl());
        assertEquals(Relationship.SELF,id.getRelationship());
        Validator validator = getValidator("work");
        validator.validate(marshall(Work.class, work));
        validator.validate(marshallToDOM(Work.class, work));
        
        work = (Work) unmarshallFromPath("/record_2.0_rc3/samples/work-full-2.0_rc3.xml", Work.class);                
        id = work.getExternalIdentifiers().getExternalIdentifiers().get(0);
        assertEquals("agr",id.getType());
        assertEquals("work:external-identifier-id",id.getValue());
        assertEquals(new Url("http://orcid.org"),id.getUrl());
        assertEquals(Relationship.SELF,id.getRelationship());
        validator.validate(marshall(Work.class, work));
        validator.validate(marshallToDOM(Work.class, work));

    }

    /** Test both types of identifier here
     * 
     * @throws SAXException
     * @throws IOException
     * @throws JAXBException
     * @throws ParserConfigurationException
     */
    @Test
    public void testPeerReview() throws SAXException, IOException, JAXBException, ParserConfigurationException{
        PeerReview peerReview = (PeerReview) unmarshallFromPath("/record_2.0_rc3/samples/peer-review-2.0_rc3.xml", PeerReview.class);
        
        ExternalID id = peerReview.getExternalIdentifiers().getExternalIdentifiers().get(0);
        assertEquals("source-work-id",id.getType());
        assertEquals("work:external-identifier-id",id.getValue());
        assertEquals(new Url("http://orcid.org"),id.getUrl());
        assertEquals(Relationship.SELF,id.getRelationship());        

        ExternalID subjectid = peerReview.getSubjectExternalIdentifier();
        assertEquals("doi",subjectid.getType());
        assertEquals("peer-review:subject-external-identifier-id",subjectid.getValue());
        assertEquals(new Url("http://orcid.org"),subjectid.getUrl());
        assertEquals(Relationship.SELF,subjectid.getRelationship());        

        Validator validator = getValidator("peer-review");
        validator.validate(marshall(PeerReview.class, peerReview));
        validator.validate(marshallToDOM(PeerReview.class, peerReview));   
    }

    @Test
    public void testActivities(){
        //?
    }
    @Test
    public void testRecord(){
        //?
    }

    public void validateSampleXML(String name) throws SAXException, IOException {
        System.out.println("validating sample: " + name);
        Source source = getInputStream("/record_2.0_rc3/samples/" + name + "-2.0_rc3.xml");
        Validator validator = getValidator(name);
        validator.validate(source);
    }

    private Source getInputStream(String loc) {
        InputStream inputStream = MarshallingTest.class.getResourceAsStream(loc);
        Source source = new StreamSource(inputStream);
        return source;
    }

    public Validator getValidator(String name) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(getClass().getResource("/record_2.0_rc3/" + name + "-2.0_rc3.xsd"));
        Validator validator = schema.newValidator();
        return validator;
    }
    
    private Object unmarshallFromPath(String path, Class<?> type) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            Object obj = unmarshall(reader, type);
            return obj;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Object unmarshall(Reader reader, Class<?> type) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
    }
    
    /** Marshals to a JAXBSource backed by SAX
     * 
     * @param type
     * @param obj
     * @return
     */
    private Source marshall(Class<?> type, Object obj) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            JAXBSource source = new JAXBSource( context, obj );
            return source;
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
    }
    
    /** Marshals to a DOM
     * 
     * @param type
     * @param obj
     * @return
     */
    private Source marshallToDOM(Class<?> type, Object obj) throws JAXBException, ParserConfigurationException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        JAXBContext context = JAXBContext.newInstance(type);
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(obj, document);
        DOMSource source = new DOMSource(document);
        return source;
    }
}
