package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.JpaJaxbNameAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.FamilyName;
import org.orcid.jaxb.model.record_v2.GivenNames;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbNameAdapterTest extends MockSourceNameCache {
    @Resource
    private JpaJaxbNameAdapter adapter;        
    
    @Test
    public void fromNameToRecordNameEntityTest() throws JAXBException {                
        Name name = new Name();
        name.setCreditName(new CreditName("Credit Name"));
        name.setFamilyName(new FamilyName("Family Name"));
        name.setGivenNames(new GivenNames("Given Names"));
        name.setPath("0000-0000-0000-0000");
        name.setVisibility(Visibility.PUBLIC);
        name.setSource(new Source("0000-0000-0000-0000"));
        
        RecordNameEntity entity = adapter.toRecordNameEntity(name);
        assertNotNull(entity);
        assertEquals("Credit Name", entity.getCreditName());        
        assertEquals("Family Name", entity.getFamilyName());
        assertEquals("Given Names", entity.getGivenNames());
        assertEquals(Visibility.PUBLIC, entity.getVisibility());
        assertNotNull(entity.getProfile());
        assertEquals("0000-0000-0000-0000", entity.getProfile().getId());        
    }
        
    @Test
    public void fromOtherNameEntityToOtherNameTest() {                
        RecordNameEntity entity = new RecordNameEntity();
        entity.setCreditName("Credit Name");
        entity.setFamilyName("Family Name");
        entity.setGivenNames("Given Names");
        entity.setVisibility(Visibility.PUBLIC);
        entity.setProfile(new ProfileEntity("0000-0000-0000-0000"));
        
        Name name = adapter.toName(entity);
        assertNotNull(name);
        assertEquals("Credit Name", name.getCreditName().getContent());
        assertEquals("Family Name", name.getFamilyName().getContent());
        assertEquals("Given Names", name.getGivenNames().getContent());
        assertEquals("0000-0000-0000-0000", name.getPath());
        assertEquals(Visibility.PUBLIC, name.getVisibility());               
    }    
}
