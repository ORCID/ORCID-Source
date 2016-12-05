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
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.test.TargetProxyHelper;

public class SourceNameCacheManagerTest extends BaseTest {

    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    @Mock
    private RecordNameDao mock_recordNameDao;
    
    @Mock
    private ClientDetailsDao mock_clientDetailsDao;
    
    @Resource
    private RecordNameDao recordNameDao;
    
    @Resource
    private ClientDetailsDao clientDetailsDao;
             
    private static final String OLD_FORMAT_CLIENT_ID = "9999"; 
    private static final String INVALID_USER = "0000";
    private static final String USER_PUBLIC_NAME = "0001";
    private static final String USER_LIMITED_NAME = "0002";
    private static final String USER_PRIVATE_NAME = "0003";
    
    @Before
    public void before() {
        when(mock_clientDetailsDao.existsAndIsNotPublicClient(OLD_FORMAT_CLIENT_ID)).thenReturn(true);
        when(mock_clientDetailsDao.existsAndIsNotPublicClient(AdditionalMatchers.not(Matchers.eq(OLD_FORMAT_CLIENT_ID)))).thenReturn(false);
                
        when(mock_recordNameDao.getRecordName(Matchers.eq(USER_PUBLIC_NAME))).thenAnswer(new Answer<RecordNameEntity>(){
            @Override
            public RecordNameEntity answer(InvocationOnMock invocation) throws Throwable {
                String id = (String)invocation.getArguments()[0];
                RecordNameEntity recordName = new RecordNameEntity();
                recordName.setLastModified(new Date());
                recordName.setCreditName("Credit name for " + id);
                recordName.setProfile(new ProfileEntity(id));
                recordName.setVisibility(Visibility.PUBLIC);
                return recordName;
            }            
        });
        
        when(mock_recordNameDao.getRecordName(Matchers.eq(USER_LIMITED_NAME))).thenAnswer(new Answer<RecordNameEntity>(){
            @Override
            public RecordNameEntity answer(InvocationOnMock invocation) throws Throwable {
                String id = (String)invocation.getArguments()[0];
                RecordNameEntity recordName = new RecordNameEntity();
                recordName.setLastModified(new Date());
                recordName.setCreditName("Credit name for " + id);
                recordName.setProfile(new ProfileEntity(id));
                recordName.setVisibility(Visibility.LIMITED);
                return recordName;
            }            
        });
        
        when(mock_recordNameDao.getRecordName(Matchers.eq(USER_PRIVATE_NAME))).thenAnswer(new Answer<RecordNameEntity>(){
            @Override
            public RecordNameEntity answer(InvocationOnMock invocation) throws Throwable {
                String id = (String)invocation.getArguments()[0];
                RecordNameEntity recordName = new RecordNameEntity();
                recordName.setLastModified(new Date());
                recordName.setCreditName("Credit name for " + id);
                recordName.setProfile(new ProfileEntity(id));
                recordName.setVisibility(Visibility.PRIVATE);
                return recordName;
            }            
        });
        
        
        //Set up a client with the old id format and a user in the profile table, to be sure that the name is picked from the client details table
        when(mock_recordNameDao.getRecordName(Matchers.eq(OLD_FORMAT_CLIENT_ID))).thenAnswer(new Answer<RecordNameEntity>(){
            @Override
            public RecordNameEntity answer(InvocationOnMock invocation) throws Throwable {
                String id = (String)invocation.getArguments()[0];
                RecordNameEntity recordName = new RecordNameEntity();
                recordName.setLastModified(new Date());
                recordName.setCreditName("Am a USER!!!!");
                recordName.setProfile(new ProfileEntity(id));
                recordName.setVisibility(Visibility.PUBLIC);
                return recordName;
            }            
        });
        
        when(mock_clientDetailsDao.find(Matchers.eq(OLD_FORMAT_CLIENT_ID))).thenAnswer(new Answer<ClientDetailsEntity>(){
            @Override
            public ClientDetailsEntity answer(InvocationOnMock invocation) throws Throwable {
                String id = (String)invocation.getArguments()[0];
                ClientDetailsEntity clientDetails = new ClientDetailsEntity();
                clientDetails.setId(id);
                clientDetails.setClientName("Am a CLIENT!!!!");
                return clientDetails;
            }            
        });                        
        
        assertNotNull(sourceNameCacheManager);
        TargetProxyHelper.injectIntoProxy(sourceNameCacheManager, "recordNameDao", mock_recordNameDao);        
        TargetProxyHelper.injectIntoProxy(sourceNameCacheManager, "clientDetailsDao", mock_clientDetailsDao);
    }      
    
    @After
    public void after() {
      //Restore the original beans
        TargetProxyHelper.injectIntoProxy(sourceNameCacheManager, "recordNameDao", recordNameDao);        
        TargetProxyHelper.injectIntoProxy(sourceNameCacheManager, "clientDetailsDao", clientDetailsDao);
    }
 
    @Test    
    public void testRecordNameDaoIsCalledOnlyOnceIfNotRemoved() {
        String user1 = USER_PUBLIC_NAME;
        String name1 = sourceNameCacheManager.retrieve(user1);        
        assertEquals("Credit name for " + user1, name1);      
        name1 = sourceNameCacheManager.retrieve(user1);
        assertEquals("Credit name for " + user1, name1);
        name1 = sourceNameCacheManager.retrieve(user1);
        assertEquals("Credit name for " + user1, name1);
        name1 = sourceNameCacheManager.retrieve(user1);
        assertEquals("Credit name for " + user1, name1);
        verify(mock_recordNameDao, times(1)).getRecordName(user1);
        verify(mock_clientDetailsDao, times(1)).existsAndIsNotPublicClient(user1);                        
        //Remove it from cache
        sourceNameCacheManager.remove(user1);
    }
    
    @Test    
    public void testRecordNameDaoIsCalledOnlyIfRemoved() {
        String user1 = USER_PUBLIC_NAME;
        String name1 = sourceNameCacheManager.retrieve(user1);
        assertEquals("Credit name for " + user1, name1);
        sourceNameCacheManager.remove(user1);
        name1 = sourceNameCacheManager.retrieve(user1);
        assertEquals("Credit name for " + user1, name1);
        sourceNameCacheManager.remove(user1);
        name1 = sourceNameCacheManager.retrieve(user1);
        verify(mock_recordNameDao, times(3)).getRecordName(user1);
        verify(mock_clientDetailsDao, times(3)).existsAndIsNotPublicClient(user1);
        //Remove it from cache
        sourceNameCacheManager.remove(user1);
    }
    
    @Test
    public void testLimitedNameDontThrowException() {
        String user1 = USER_LIMITED_NAME;
        String name1 = sourceNameCacheManager.retrieve(user1);
        assertNull(name1);
    }
    
    @Test
    public void testPrivateNameDontThrowException() {
        String user1 = USER_PRIVATE_NAME;
        String name1 = sourceNameCacheManager.retrieve(user1);
        assertNull(name1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUserThrowException() {
        sourceNameCacheManager.retrieve(INVALID_USER);
    }
    
    @Test
    public void testEnsureOldClientsReturnNameFromClientTable() {
        String user1 = OLD_FORMAT_CLIENT_ID;
        String name1 = sourceNameCacheManager.retrieve(user1);
        assertEquals("Am a CLIENT!!!!", name1);        
    }    
}
