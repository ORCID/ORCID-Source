package org.orcid.core.adapter.v2.latest;

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
import org.orcid.core.adapter.JpaJaxbOtherNameAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class JpaJaxbOtherNameAdapterTest extends MockSourceNameCache {
    @Resource
    private JpaJaxbOtherNameAdapter adapter;        
    
    @Test
    public void fromOtherNameToOtherNameEntityTest() throws JAXBException {                
        OtherName otherName = getOtherName();
        assertNotNull(otherName);
        assertNotNull(otherName.getCreatedDate());
        assertNotNull(otherName.getLastModifiedDate());
        OtherNameEntity otherNameEntity = adapter.toOtherNameEntity(otherName);
        assertNotNull(otherNameEntity);
        assertNull(otherNameEntity.getDateCreated());
        assertNull(otherNameEntity.getLastModified());
        assertEquals("Other Name #1", otherNameEntity.getDisplayName());        
        // Source
        assertNull(otherNameEntity.getSourceId());        
        assertNull(otherNameEntity.getClientSourceId());        
        assertNull(otherNameEntity.getElementSourceId());    
    }
    
    @Test
    public void fromOtherNameEntityToOtherNameTest() throws IllegalAccessException {                
        OtherNameEntity entity = getOtherNameEntity();
        OtherName otherName = adapter.toOtherName(entity);
        assertNotNull(otherName);
        assertNotNull(otherName.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(otherName.getCreatedDate().getValue()));
        assertNotNull(otherName.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(otherName.getLastModifiedDate().getValue()));
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
    
    private OtherNameEntity getOtherNameEntity() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        OtherNameEntity result = new OtherNameEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(result, date);
        result.setId(Long.valueOf(1));
        result.setDisplayName("display-name");
        result.setOrcid("0000-0000-0000-0000");
        result.setVisibility(Visibility.PUBLIC.name());
        result.setClientSourceId(CLIENT_SOURCE_ID);
        return result;
    }
}
