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
package org.orcid.core.adapter;

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
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbExternalIdentifierAdapterTest {
    @Resource
    private JpaJaxbExternalIdentifierAdapter jpaJaxbExternalIdentifierAdapter;
    
    @Test
    public void testToExternalIdentifierEntity() throws JAXBException {
        ExternalIdentifierEntity entity = jpaJaxbExternalIdentifierAdapter.toExternalIdentifierEntity(getExternalIdentifier());
        assertNotNull(entity);
        assertEquals("common-name-1", entity.getExternalIdCommonName());
        assertEquals("id-reference-1", entity.getExternalIdReference());
        assertEquals("http://url/1", entity.getExternalIdUrl());
        assertEquals(Long.valueOf(1), entity.getId());
        assertNotNull(entity.getDateCreated());
        assertNotNull(entity.getLastModified());
        assertNotNull(entity.getSource());
        assertEquals("8888-8888-8888-8880", entity.getSource().getSourceId());        
    }

    @Test
    public void fromExternalIdentifierEntityToExternalIdentifier() {
        ExternalIdentifierEntity entity = getExternalIdentifierEntity();
        ExternalIdentifier extId = jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(entity);
        assertNotNull(extId);
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getCreatedDate().getValue());
        assertNotNull(extId.getLastModifiedDate());
        assertNotNull(extId.getLastModifiedDate().getValue());
        assertEquals("common-name", extId.getCommonName());               
        assertEquals("id-reference", extId.getReference());
        assertNotNull(extId.getUrl());
        assertEquals("http://myurl.com", extId.getUrl().getValue());        
        assertEquals(Long.valueOf(123), extId.getPutCode());
        assertNotNull(extId.getSource());
        assertEquals("APP-0000000000000000", extId.getSource().retrieveSourcePath());        
        assertEquals(Visibility.LIMITED.value(), extId.getVisibility().value());       
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());
    }      
    
    private ExternalIdentifier getExternalIdentifier() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { ExternalIdentifier.class });
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String name = "/record_2.0_rc2/samples/external-identifier-2.0_rc2.xml";             
        InputStream inputStream = getClass().getResourceAsStream(name);
        return (ExternalIdentifier) unmarshaller.unmarshal(inputStream);
    }
    
    private ExternalIdentifierEntity getExternalIdentifierEntity() {
        ExternalIdentifierEntity entity = new ExternalIdentifierEntity();
        entity.setDateCreated(new Date());
        entity.setLastModified(new Date());
        entity.setExternalIdCommonName("common-name");
        entity.setExternalIdReference("id-reference");        
        entity.setExternalIdUrl("http://myurl.com");
        entity.setId(123L);        
        entity.setSource(new SourceEntity("APP-0000000000000000"));
        entity.setVisibility(Visibility.LIMITED);
        return entity;
    }
}
