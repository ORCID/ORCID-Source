package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.v3.JpaJaxbExternalIdentifierAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbExternalIdentifierAdapterTest extends MockSourceNameCache {
    @Resource(name = "jpaJaxbExternalIdentifierAdapterV3")
    private JpaJaxbExternalIdentifierAdapter jpaJaxbExternalIdentifierAdapter;
    
    @Test
    public void testToExternalIdentifierEntity() throws JAXBException {
        ExternalIdentifierEntity entity = jpaJaxbExternalIdentifierAdapter.toExternalIdentifierEntity(getExternalIdentifier());
        assertNotNull(entity);
        assertEquals("A-0003", entity.getExternalIdCommonName());
        assertEquals("A-0003", entity.getExternalIdReference());
        assertEquals("http://ext-id/A-0003", entity.getExternalIdUrl());
        assertEquals(Long.valueOf(1), entity.getId());
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getLastModified());        
        
        // Source
        assertNull(entity.getSourceId());        
        assertNull(entity.getClientSourceId());        
        assertNull(entity.getElementSourceId());    
    }

    @Test
    public void fromExternalIdentifierEntityToExternalIdentifier() {
        ExternalIdentifierEntity entity = getExternalIdentifierEntity();
        PersonExternalIdentifier extId = jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(entity);
        assertNotNull(extId);
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getCreatedDate().getValue());
        assertNotNull(extId.getLastModifiedDate());
        assertNotNull(extId.getLastModifiedDate().getValue());
        assertEquals("common-name", extId.getType());               
        assertEquals("id-reference", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://myurl.com", extId.getUrl().getValue());        
        assertEquals(Long.valueOf(123), extId.getPutCode());
        assertNotNull(extId.getSource());
        assertEquals("APP-0000000000000000", extId.getSource().retrieveSourcePath());        
        assertEquals(Visibility.LIMITED.value(), extId.getVisibility().value());       
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());
    }      
    
    private PersonExternalIdentifier getExternalIdentifier() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { PersonExternalIdentifier.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/external-identifier-2.0.xml";             
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (PersonExternalIdentifier) unmarshaller.unmarshal(inputStream);
    }
    
    private ExternalIdentifierEntity getExternalIdentifierEntity() {
        ExternalIdentifierEntity entity = new ExternalIdentifierEntity();
        entity.setDateCreated(new Date());
        entity.setLastModified(new Date());
        entity.setExternalIdCommonName("common-name");
        entity.setExternalIdReference("id-reference");        
        entity.setExternalIdUrl("http://myurl.com");
        entity.setId(123L);        
        entity.setClientSourceId("APP-0000000000000000");
        entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.name());
        return entity;
    }
}
