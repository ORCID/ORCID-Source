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
import org.orcid.core.adapter.JpaJaxbKeywordAdapter;
import org.orcid.core.adapter.MockSourceNameCache;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
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
public class JpaJaxbKeywordAdapterTest extends MockSourceNameCache {
    @Resource
    private JpaJaxbKeywordAdapter adapter;
    
    @Test
    public void fromKeywordToProfileKeywordEntityTest() throws JAXBException {
        Keyword keyword = getKeyword();
        assertNotNull(keyword);
        assertNotNull(keyword.getCreatedDate());
        assertNotNull(keyword.getLastModifiedDate());
        
        ProfileKeywordEntity entity = adapter.toProfileKeywordEntity(keyword);
        assertNotNull(entity);
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());
        assertEquals(Long.valueOf(1), entity.getId());
        assertEquals("keyword1", entity.getKeywordName());        
        assertEquals(Visibility.PUBLIC.name(), entity.getVisibility());
        
        // Source
        assertNull(entity.getSourceId());        
        assertNull(entity.getClientSourceId());        
        assertNull(entity.getElementSourceId());
    }
    
    @Test
    public void fromProfileKeywordEntityToKeywordTest() throws IllegalAccessException {
        ProfileKeywordEntity entity = getProfileKeywordEntity();
        Keyword keyword = adapter.toKeyword(entity);
        assertNotNull(keyword);
        assertNotNull(keyword.getCreatedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(keyword.getCreatedDate().getValue()));
        assertNotNull(keyword.getLastModifiedDate());
        assertEquals(DateUtils.convertToDate("2015-06-05T10:15:20"), DateUtils.convertToDate(keyword.getLastModifiedDate().getValue()));
        assertEquals("keyword-1", keyword.getContent());
        assertNotNull(keyword.getCreatedDate());
        assertNotNull(keyword.getCreatedDate().getValue());
        assertNotNull(keyword.getLastModifiedDate());
        assertNotNull(keyword.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(1), keyword.getPutCode());
        assertNotNull(keyword.getSource());
        assertEquals(CLIENT_SOURCE_ID, keyword.getSource().retrieveSourcePath());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());
    }
    
    private Keyword getKeyword() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { Keyword.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0/samples/read_samples/keyword-2.0.xml";
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (Keyword) unmarshaller.unmarshal(inputStream); 
    }
    
    private ProfileKeywordEntity getProfileKeywordEntity() throws IllegalAccessException {
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        ProfileKeywordEntity entity = new ProfileKeywordEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(entity, date);
        entity.setId(Long.valueOf(1));
        entity.setKeywordName("keyword-1");
        entity.setOrcid("0000-0000-0000-0000");
        entity.setClientSourceId(CLIENT_SOURCE_ID);
        entity.setVisibility(Visibility.LIMITED.name());
        return entity;
    }
}
