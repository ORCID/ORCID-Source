package org.orcid.core.adapter;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.test.TargetProxyHelper;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class MockSourceNameCache {
    @Resource
    private JpaJaxbAddressAdapter adapter;        
    
    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    @Resource
    private RecordNameDao recordNameDao;
    
    @Resource
    private ClientDetailsDao clientDetailsDao;
    
    @Mock
    protected RecordNameDao mockedRecordNameDao;
    
    @Mock
    protected ClientDetailsDao mockedClientDetailsDao;
    
    @Before
    public void init() throws Exception {         
        MockitoAnnotations.initMocks(this);
        
        when(mockedRecordNameDao.getRecordName(Mockito.anyString(), Mockito.anyLong())).thenAnswer(new Answer<RecordNameEntity>(){
            @Override
            public RecordNameEntity answer(InvocationOnMock invocation) throws Throwable {
                String id = (String)invocation.getArguments()[0];
                RecordNameEntity recordName = new RecordNameEntity();
                recordName.setLastModified(new Date());
                recordName.setCreditName("Credit name");
                recordName.setProfile(new ProfileEntity(id));
                recordName.setVisibility(Visibility.PUBLIC);
                return recordName;
            }
            
        });
        
        when(mockedClientDetailsDao.find((Matchers.<String> any()))).thenAnswer(new Answer<ClientDetailsEntity>(){
            @Override
            public ClientDetailsEntity answer(InvocationOnMock invocation) throws Throwable {
                String id = (String)invocation.getArguments()[0];
                ClientDetailsEntity client = new ClientDetailsEntity(id);
                client.setLastModified(new Date());
                return client;
            }            
        });
        
        assertNotNull(sourceNameCacheManager);
        TargetProxyHelper.injectIntoProxy(sourceNameCacheManager, "recordNameDao", mockedRecordNameDao);        
        TargetProxyHelper.injectIntoProxy(sourceNameCacheManager, "clientDetailsDao", mockedClientDetailsDao);
    }
    
    @After
    public void after() {
        //Restore the original beans
        TargetProxyHelper.injectIntoProxy(sourceNameCacheManager, "recordNameDao", recordNameDao);        
        TargetProxyHelper.injectIntoProxy(sourceNameCacheManager, "clientDetailsDao", clientDetailsDao);
    }
}
