package org.orcid.record;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.orcid.jaxb.model.groupid_rc1.GroupIdRecord;
import org.orcid.jaxb.model.notification.custom.MarshallingTest;
import org.xml.sax.SAXException;

public class ValidateV2RC1SamplesTest {

    String[] sampleNames = { "activities", "deprecated", "education", "employment", "error", "funding", "history", "person", "preferences", "record", "search", "work" };

    @Test
    public void Test() throws SAXException, IOException {
        for (String name : sampleNames) {
            validateSampleXML(name);
        }
    }

    @Test
    public void validateGroupIdValue() throws SAXException, IOException, JAXBException {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(getClass().getResource("/group-id-2.0_rc1/group-id-2.0_rc1.xsd"));
        Validator validator = schema.newValidator();        
        
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] upperAlphabet = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();
        char[] numbers = "0123456789".toCharArray();
        char[] validCharacters = "^._~:/?#[]@!$&'()*+,;=-".toCharArray();        
        //All valid characters
        char[] allValid = ArrayUtils.addAll(alphabet, upperAlphabet);
        allValid = ArrayUtils.addAll(allValid, numbers);
        allValid = ArrayUtils.addAll(allValid, validCharacters);        
        
        String invalidCharactersString = "{}\"<>\\"; 
        char[] invalidCharacters = invalidCharactersString.toCharArray();
        //All valid and non valid characters
        char[] allWithInvalids = ArrayUtils.addAll(allValid, invalidCharacters);
        
        GroupIdRecord g1 = new GroupIdRecord();
        g1.setDescription("description");
        g1.setType("newsletter");
        
        System.out.println("Validating group_id agains a list of 3000 valid values");
        
        for (int i = 0; i < 3000; i++) {
            String randomValue = "orcid-generated:" + RandomStringUtils.random(200, allValid);
            g1.setName(randomValue);
            g1.setGroupId(randomValue);
            JAXBContext context;
            context = JAXBContext.newInstance(GroupIdRecord.class);
            Source source = new JAXBSource(context, g1);
            try {
                validator.validate(source);
            } catch(Exception e) {
                fail("fail validating: " + randomValue + " on iteration " + i);
            }
        }
        
        System.out.println("Validating group_id agains a list of 3000 invalid values");
        
        for(int i = 0; i < 3000; i++) {
            String randomValue = "orcid-generated:" + RandomStringUtils.random(200, allWithInvalids);
            boolean regenerateString = true;
            do {
                for(int j = 0; j < randomValue.length(); j++) {
                    if(invalidCharactersString.contains(String.valueOf(randomValue.charAt(j)))) {
                        regenerateString = false;
                        break;
                    }
                }
                
                if(regenerateString) {
                    randomValue += RandomStringUtils.random(3, invalidCharacters);
                    regenerateString = false;
                }                
            } while(regenerateString);                            
            g1.setName(randomValue);
            g1.setGroupId(randomValue);
            JAXBContext context;
            context = JAXBContext.newInstance(GroupIdRecord.class);
            Source source = new JAXBSource(context, g1);
            try {
                validator.validate(source);
                fail(randomValue + " should not be vaild according to the XSD on iteration " + i);               
            } catch(Exception e) {
                
            }
        }
        
    }
    
    public void validateSampleXML(String name) throws SAXException, IOException {
        Source source = getInputStream("/record_2.0_rc1/samples/" + name + "-2.0_rc1.xml");
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
        Schema schema = factory.newSchema(getClass().getResource("/record_2.0_rc1/" + name + "-2.0_rc1.xsd"));
        Validator validator = schema.newValidator();
        return validator;
    }

}
