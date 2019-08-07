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
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbOtherNameAdapterTest extends MockSourceNameCache {
    @Resource
    private JpaJaxbOtherNameAdapter adapter;        
    
    @Test
    public void fromOtherNameToOtherNameEntityTest() throws JAXBException {                
        OtherName otherName = getOtherName();
        OtherNameEntity otherNameEntity = adapter.toOtherNameEntity(otherName);
        assertNotNull(otherNameEntity);
        assertNotNull(otherNameEntity.getDateCreated());
        assertNotNull(otherNameEntity.getLastModified());
        assertEquals("Other Name #1", otherNameEntity.getDisplayName());        
        // Source
        assertNull(otherNameEntity.getSourceId());        
        assertNull(otherNameEntity.getClientSourceId());        
        assertNull(otherNameEntity.getElementSourceId());    
    }
    
    @Test
    public void fromOtherNameEntityToOtherNameTest() {                
        OtherNameEntity entity = getOtherNameEntity();
        OtherName otherName = adapter.toOtherName(entity);
        assertNotNull(otherName);
        assertEquals("display-name", otherName.getContent());
        assertNotNull(otherName.getCreatedDate());
        assertNotNull(otherName.getLastModifiedDate());
        assertEquals(Long.valueOf(1), otherName.getPutCode());
        assertNotNull(otherName.getSource());
        assertEquals(CLIENT_SOURCE_ID, otherName.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());
    }
    
    private OtherName getOtherName() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { OtherName.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/other-name-2.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (OtherName) unmarshaller.unmarshal(inputStream);
    }
    
    private OtherNameEntity getOtherNameEntity() {
        OtherNameEntity result = new OtherNameEntity();
        result.setId(Long.valueOf(1));
        result.setDateCreated(new Date());
        result.setLastModified(new Date());
        result.setDisplayName("display-name");
        result.setProfile(new ProfileEntity("0000-0000-0000-0000"));
        result.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.name());
        result.setClientSourceId(CLIENT_SOURCE_ID);
        return result;
    }
}
