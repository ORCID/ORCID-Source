package org.orcid.record.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.orcid.jaxb.test.utils.OrcidTranslator;

public class V3RoundTripTest {
    
    /** Checks we can round trip the XML->Record->XML->Record (with schema validation!)
     * Also checks we can write JSON.
     * 
     * @throws JAXBException
     * @throws IOException
     */
    @Test
    public void testReadXmlThenWriteThenReadV3_0RC1() throws JAXBException, IOException {
        OrcidTranslator<org.orcid.jaxb.model.v3.rc1.record.Record> t = OrcidTranslator.v3_0RC1();
        Reader reader = new InputStreamReader(getClass().getResourceAsStream("/record_3.0_rc1/samples/read_samples/record-3.0_rc1.xml"));
        
        //read the xml
        org.orcid.jaxb.model.v3.rc1.record.Record r = t.readXmlRecord(reader);
        assertEquals("8888-8888-8888-8880", r.getOrcidIdentifier().getPath());
        //write as JSON
        StringWriter sw = new StringWriter();
        t.writeJsonRecord(sw, r);
        assertTrue(sw.toString().contains("\"path\" : \"8888-8888-8888-8880\","));
        //write as XML;
        StringWriter sw2 = new StringWriter();
        t.writeXmlRecord(sw2, r);
        assertTrue(sw2.toString().contains("<common:path>8888-8888-8888-8880</common:path>"));
        
        //Read written XML
        Reader reader2 = new StringReader(sw2.toString());
        org.orcid.jaxb.model.v3.rc1.record.Record r2 = t.readXmlRecord(reader2);
        assertEquals("8888-8888-8888-8880", r2.getOrcidIdentifier().getPath());
    }
    
}
