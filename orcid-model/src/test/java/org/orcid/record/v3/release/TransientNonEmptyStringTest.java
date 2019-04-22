package org.orcid.record.v3.release;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Test;
import org.orcid.jaxb.model.v3.release.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.xml.sax.SAXException;

public class TransientNonEmptyStringTest {
    
    /**
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <common:external-ids xmlns:internal="http://www.orcid.org/ns/internal" xmlns:address="http://www.orcid.org/ns/address" xmlns:email="http://www.orcid.org/ns/email" xmlns:history="http://www.orcid.org/ns/history" xmlns:employment="http://www.orcid.org/ns/employment" xmlns:person="http://www.orcid.org/ns/person" xmlns:education="http://www.orcid.org/ns/education" xmlns:other-name="http://www.orcid.org/ns/other-name" xmlns:personal-details="http://www.orcid.org/ns/personal-details" xmlns:bulk="http://www.orcid.org/ns/bulk" xmlns:common="http://www.orcid.org/ns/common" xmlns:record="http://www.orcid.org/ns/record" xmlns:keyword="http://www.orcid.org/ns/keyword" xmlns:activities="http://www.orcid.org/ns/activities" xmlns:deprecated="http://www.orcid.org/ns/deprecated" xmlns:external-identifier="http://www.orcid.org/ns/external-identifier" xmlns:funding="http://www.orcid.org/ns/funding" xmlns:error="http://www.orcid.org/ns/error" xmlns:preferences="http://www.orcid.org/ns/preferences" xmlns:work="http://www.orcid.org/ns/work" xmlns:researcher-url="http://www.orcid.org/ns/researcher-url" xmlns:peer-review="http://www.orcid.org/ns/peer-review">
        <common:external-id>
            <common:external-id-type>doi</common:external-id-type>
            <common:external-id-value>value</common:external-id-value>
            <common:external-id-normalized transient="true">normalized-value</common:external-id-normalized>
        </common:external-id>
    </common:external-ids>
     * @throws JAXBException
     * @throws SAXException 
     * @throws IOException 
     */
    @Test
    public void testMarshal() throws JAXBException, SAXException, IOException{
        ExternalIDs ids = new ExternalIDs();        
        ExternalID id = new ExternalID();
        id.setType("doi");
        id.setValue("value");
        id.setNormalized(new TransientNonEmptyString("normalized-value"));
        ids.getExternalIdentifier().add(id);
        JAXBContext context = JAXBContext.newInstance(ExternalIDs.class);
        Marshaller marshaller = context.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(ids, sw);
        assertTrue(sw.toString().contains("<common:external-id-normalized transient=\"true\">normalized-value</common:external-id-normalized>"));
        
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(getClass().getResource("/common_3.0/common-3.0.xsd"));
        Validator validator = schema.newValidator();
        validator.validate(new JAXBSource( context, ids ));
    }
    
    @Test
    public void testUnmarshal(){
        ExternalIDs ids = unmarshallFromPath("/common_3.0/samples/common-3.0_external-identifier.xml", ExternalIDs.class); 
        assertEquals(ids.getExternalIdentifier().get(0).getNormalized().getValue(),"normalized-value");
    }
    
    private <T> T unmarshallFromPath(String path, Class<T> type) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            Object obj = unmarshall(reader, type);
            return (T) obj;
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
}
