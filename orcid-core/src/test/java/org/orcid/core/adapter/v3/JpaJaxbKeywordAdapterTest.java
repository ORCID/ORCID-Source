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
import org.orcid.core.adapter.v3.JpaJaxbKeywordAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.record.Keyword;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbKeywordAdapterTest extends MockSourceNameCache {
    @Resource
    private JpaJaxbKeywordAdapter adapter;
    
    @Test
    public void fromKeywordToProfileKeywordEntityTest() throws JAXBException {
        Keyword keyword = getKeyword();
        ProfileKeywordEntity entity = adapter.toProfileKeywordEntity(keyword);
        assertNotNull(entity);
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getLastModified());
        assertEquals(Long.valueOf(1), entity.getId());
        assertEquals("keyword1", entity.getKeywordName());        
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.name(), entity.getVisibility());
        
        // Source
        assertNull(entity.getSourceId());        
        assertNull(entity.getClientSourceId());        
        assertNull(entity.getElementSourceId());
    }
    
    @Test
    public void fromProfileKeywordEntityToKeywordTest() {
        ProfileKeywordEntity entity = getProfileKeywordEntity();
        Keyword keyword = adapter.toKeyword(entity);
        assertNotNull(keyword);
        assertEquals("keyword-1", keyword.getContent());
        assertNotNull(keyword.getCreatedDate());
        assertNotNull(keyword.getCreatedDate().getValue());
        assertNotNull(keyword.getLastModifiedDate());
        assertNotNull(keyword.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(1), keyword.getPutCode());
        assertNotNull(keyword.getSource());
        assertEquals("APP-000000000000", keyword.getSource().retrieveSourcePath());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());
    }
    
    private Keyword getKeyword() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Keyword.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_3.0_dev1/samples/read_samples/keyword-3.0_dev1.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Keyword) unmarshaller.unmarshal(inputStream); 
    }
    
    private ProfileKeywordEntity getProfileKeywordEntity() {
        ProfileKeywordEntity entity = new ProfileKeywordEntity();
        entity.setDateCreated(new Date());
        entity.setLastModified(new Date());
        entity.setId(Long.valueOf(1));
        entity.setKeywordName("keyword-1");
        entity.setProfile(new ProfileEntity("0000-0000-0000-0000"));
        entity.setClientSourceId("APP-000000000000");
        entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.name());
        return entity;
    }
}
