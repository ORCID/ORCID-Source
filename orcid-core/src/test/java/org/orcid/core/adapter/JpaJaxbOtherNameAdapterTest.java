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

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.SourceDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.manager.cache.SourceEntityCacheManager;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbOtherNameAdapterTest extends BaseTest {
    @Resource
    private JpaJaxbOtherNameAdapter adapter;        
    
    @Resource 
    private SourceEntityCacheManager sourceEntityCacheManager;
    
    @Mock
    private ProfileDao profileDao;
    
    @Mock
    private ClientDetailsDao clientDetailsDao;
    
    @Mock
    private SourceDao sourceDao;
    
    @Before
    public void init() throws Exception {        
        when(profileDao.find((Matchers.<String> any()))).thenAnswer(new Answer<ProfileEntity>(){
            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                String id = (String)invocation.getArguments()[0];
                ProfileEntity profile = new ProfileEntity(id);
                profile.setLastModified(new Date());
                return profile;
            }
            
        });
        
        when(clientDetailsDao.find((Matchers.<String> any()))).thenAnswer(new Answer<ClientDetailsEntity>(){
            @Override
            public ClientDetailsEntity answer(InvocationOnMock invocation) throws Throwable {
                String id = (String)invocation.getArguments()[0];
                ClientDetailsEntity client = new ClientDetailsEntity(id);
                client.setLastModified(new Date());
                return client;
            }            
        });
        
        when(sourceDao.getLastModified((Matchers.<String> any()))).thenReturn(new Date());
        
        assertNotNull(sourceEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(sourceEntityCacheManager, "sourceDao", sourceDao);
        TargetProxyHelper.injectIntoProxy(sourceEntityCacheManager, "profileDao", profileDao);        
        TargetProxyHelper.injectIntoProxy(sourceEntityCacheManager, "clientDetailsDao", clientDetailsDao);
    }    
    
    @Test
    public void fromOtherNameToOtherNameEntityTest() throws JAXBException {                
        OtherName otherName = getOtherName();
        OtherNameEntity otherNameEntity = adapter.toOtherNameEntity(otherName);
        assertNotNull(otherNameEntity);
        assertNotNull(otherNameEntity.getDateCreated());
        assertNotNull(otherNameEntity.getLastModified());
        assertEquals("Other Name #1", otherNameEntity.getDisplayName());
        assertNotNull(otherNameEntity.getSource());
        assertEquals("8888-8888-8888-8880", otherNameEntity.getSource().getSourceId());
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
        String name = "/record_2.0_rc2/samples/other-name-2.0_rc2.xml";
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
        result.setSource(new SourceEntity("APP-000000001"));
        return result;
    }
}
