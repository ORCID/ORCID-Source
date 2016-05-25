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
package org.orcid.core;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
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
public class MockSourceBase extends BaseTest {
    @Mock
    protected ProfileDao profileDao;
    
    @Mock
    protected ClientDetailsDao clientDetailsDao;
    
    @Resource 
    private SourceEntityCacheManager sourceEntityCacheManager;
    
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
        
        when(profileDao.retrieveLastModifiedDate(Matchers.<String> any())).thenReturn(new Date());
        
        when(clientDetailsDao.find((Matchers.<String> any()))).thenAnswer(new Answer<ClientDetailsEntity>(){
            @Override
            public ClientDetailsEntity answer(InvocationOnMock invocation) throws Throwable {
                String id = (String)invocation.getArguments()[0];
                ClientDetailsEntity client = new ClientDetailsEntity(id);
                client.setLastModified(new Date());
                return client;
            }            
        });
        
        when(clientDetailsDao.getLastModifiedIfNotPublicClient(Matchers.<String> any())).thenReturn(new Date());
        
        assertNotNull(sourceEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(sourceEntityCacheManager, "profileDao", profileDao);        
        TargetProxyHelper.injectIntoProxy(sourceEntityCacheManager, "clientDetailsDao", clientDetailsDao);
    }
}
