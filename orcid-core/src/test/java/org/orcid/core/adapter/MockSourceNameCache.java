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
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.test.TargetProxyHelper;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class MockSourceNameCache {
    protected static String CLIENT_SOURCE_ID = "APP-0000000000000001";
    
    @Resource
    private JpaJaxbAddressAdapter adapter;        
    
    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    @Resource
    private RecordNameDao recordNameDao;
    
    @Resource
    private ClientDetailsDao clientDetailsDao;
    
    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnlyV3;
    
    @Mock
    protected RecordNameDao mockedRecordNameDao;
    
    @Mock
    protected ClientDetailsDao mockedClientDetailsDao;
    
    @Before
    public void init() throws Exception {         
        MockitoAnnotations.initMocks(this);
                
        when(mockedClientDetailsDao.existsAndIsNotPublicClient(CLIENT_SOURCE_ID)).thenReturn(true);
        
        when(mockedRecordNameDao.getRecordName(Mockito.anyString(), Mockito.anyLong())).thenAnswer(new Answer<RecordNameEntity>(){
            @Override
            public RecordNameEntity answer(InvocationOnMock invocation) throws Throwable {
                String id = (String)invocation.getArguments()[0];
                RecordNameEntity recordName = new RecordNameEntity();
                recordName.setLastModified(new Date());
                recordName.setCreditName("Credit name");
                recordName.setOrcid(id);
                recordName.setVisibility(Visibility.PUBLIC.name());
                return recordName;
            }
            
        });
        
        when(mockedClientDetailsDao.find((Matchers.<String> any()))).thenAnswer(new Answer<ClientDetailsEntity>(){
            @Override
            public ClientDetailsEntity answer(InvocationOnMock invocation) throws Throwable {
                String id = (String)invocation.getArguments()[0];
                ClientDetailsEntity client = new ClientDetailsEntity(id);
                client.setLastModified(new Date());
                client.setClientName("Client name");
                return client;
            }            
        });
        
        assertNotNull(sourceNameCacheManager);
        assertNotNull(recordNameManagerReadOnlyV3);
        TargetProxyHelper.injectIntoProxy(sourceNameCacheManager, "recordNameDao", mockedRecordNameDao);        
        TargetProxyHelper.injectIntoProxy(sourceNameCacheManager, "clientDetailsDao", mockedClientDetailsDao);
        TargetProxyHelper.injectIntoProxy(recordNameManagerReadOnlyV3, "recordNameDao", mockedRecordNameDao);
    }
    
    @After
    public void after() {
        //Restore the original beans
        TargetProxyHelper.injectIntoProxy(sourceNameCacheManager, "recordNameDao", recordNameDao);        
        TargetProxyHelper.injectIntoProxy(sourceNameCacheManager, "clientDetailsDao", clientDetailsDao);
        TargetProxyHelper.injectIntoProxy(recordNameManagerReadOnlyV3, "recordNameDao", recordNameDao);
    }
}
