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
package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.OtherName;
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
        assertEquals("8888-8888-8888-8880", otherNameEntity.getElementSourceId());
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
        assertEquals("APP-000000001", otherName.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());
    }
    
    private OtherName getOtherName() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { OtherName.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0_rc4/samples/other-name-2.0_rc4.xml";
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
        result.setVisibility(Visibility.PUBLIC);
        result.setClientSourceId("APP-000000001");
        return result;
    }
}
