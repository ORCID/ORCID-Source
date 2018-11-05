package org.orcid.core.adapter.jsonidentifier.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.jaxb.model.v3.rc2.record.Education;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;

public class JSONExternalIdentifiersConverterV3Test {

    @Mock
    private PIDNormalizationService norm;
    
    @Mock
    private LocaleManager localeManager;
    
    private JSONExternalIdentifiersConverterV3 converter;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(norm.normalise(Mockito.anyString(), Mockito.anyString())).thenReturn("blah");
        Mockito.when(localeManager.resolveMessage(Mockito.anyString(), Mockito.any())).thenReturn("blah");
        converter  = new JSONExternalIdentifiersConverterV3(norm, localeManager);
    }

    @Test
    public void testConvertTo() throws JAXBException {
        Education education = getEducation();
        assertEquals(
                "{\"externalIdentifier\":[{\"type\":\"GRANT_NUMBER\",\"value\":\"external-identifier-value\",\"url\":{\"value\":\"http://tempuri.org\"},\"relationship\":\"SELF\"},{\"type\":\"GRANT_NUMBER\",\"value\":\"external-identifier-value2\",\"url\":{\"value\":\"http://tempuri.org/2\"},\"relationship\":\"SELF\"}]}",
                converter.convertTo(education.getExternalIdentifiers(), null));
    }

    @Test
    public void testConvertFrom() {
        ExternalIDs externalIDs = converter.convertFrom("{\"externalIdentifier\":[{\"type\":\"GRANT_NUMBER\",\"value\":\"external-identifier-value\",\"url\":{\"value\":\"http://tempuri.org\"},\"relationship\":\"SELF\"},{\"type\":\"GRANT_NUMBER\",\"value\":\"external-identifier-value2\",\"url\":{\"value\":\"http://tempuri.org/2\"},\"relationship\":\"SELF\"}]}", null);
        assertNotNull(externalIDs);
        assertEquals(2, externalIDs.getExternalIdentifier().size());
        
        ExternalID externalID = externalIDs.getExternalIdentifier().get(0);
        assertEquals("grant_number", externalID.getType());
        assertEquals("external-identifier-value", externalID.getValue());
        assertEquals("http://tempuri.org", externalID.getUrl().getValue());
        
        externalID = externalIDs.getExternalIdentifier().get(1);
        assertEquals("grant_number", externalID.getType());
        assertEquals("external-identifier-value2", externalID.getValue());
        assertEquals("http://tempuri.org/2", externalID.getUrl().getValue());
    }

   
    private Education getEducation() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Education.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0_rc1/samples/read_samples/education-full-3.0_rc1.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Education) unmarshaller.unmarshal(inputStream);
    }

}
