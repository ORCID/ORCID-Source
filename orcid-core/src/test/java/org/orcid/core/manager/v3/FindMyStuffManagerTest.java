package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.adapter.v3.JpaJaxbNotificationAdapter;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.utils.v3.identifiers.finders.DataciteFinder;
import org.orcid.core.utils.v3.identifiers.finders.Finder;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.v3.rc2.common.Source;
import org.orcid.jaxb.model.v3.rc2.notification.Notification;
import org.orcid.jaxb.model.v3.rc2.notification.NotificationType;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.model.v3.rc2.notification.internal.NotificationFindMyStuff;
import org.orcid.persistence.dao.FindMyStuffHistoryDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.FindMyStuffHistoryEntity;
import org.orcid.persistence.jpa.entities.NotificationFindMyStuffEntity;
import org.orcid.pojo.FindMyStuffItem;
import org.orcid.pojo.FindMyStuffResult;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class FindMyStuffManagerTest extends DBUnitTest{

    @Mock
    WorkManagerReadOnly workManagerReadOnly;
    
    @Mock
    private NotificationManager notificationManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Mock
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Mock
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;
    
    @Resource(name = "jpaJaxbNotificationAdapterV3")
    private JpaJaxbNotificationAdapter notificationAdapter;

    @Mock
    private FindMyStuffHistoryDao findMyStuffHistoryDao;
    
    @Mock
    private DataciteFinder dataciteFinder;
    
    @Resource
    List<Finder> finders = new ArrayList<Finder>();
    
    @Resource 
    private FindMyStuffManager findMyStuffManager;
    
    @Before
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);  
        TargetProxyHelper.injectIntoProxy(findMyStuffManager, "workManagerReadOnly", workManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(findMyStuffManager, "notificationManager", notificationManager);
        TargetProxyHelper.injectIntoProxy(findMyStuffManager, "clientDetailsEntityCacheManager", clientDetailsEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(findMyStuffManager, "orcidOauth2TokenDetailService", orcidOauth2TokenDetailService);
        TargetProxyHelper.injectIntoProxy(findMyStuffManager, "findMyStuffHistoryDao", findMyStuffHistoryDao);
        
        //Finder mock
        List<Finder> f = Lists.newArrayList();
        f.add(dataciteFinder);
        FindMyStuffResult r = new FindMyStuffResult();
        r.setFinderName("DataciteFinder");
        r.setResults(Lists.newArrayList());
        FindMyStuffItem result1 = new FindMyStuffItem("id1", "doi", "");
        r.getResults().add(result1);
        when(dataciteFinder.getRelatedClientId()).thenReturn("x");
        when(dataciteFinder.isEnabled()).thenReturn(true);
        when(dataciteFinder.getFinderName()).thenReturn("DataciteFinder");
        when(dataciteFinder.find(Matchers.anyString(), Matchers.any())).thenReturn(r);
        TargetProxyHelper.injectIntoProxy(findMyStuffManager, "finders", f);
        
        //return uris
        ClientDetailsEntity cd = new ClientDetailsEntity();
        ClientRedirectUriEntity uri = new ClientRedirectUriEntity();
        uri.setRedirectUri("https://example.com/");
        uri.setRedirectUriType(RedirectUriType.FIND_MY_STUFF.value());
        uri.setPredefinedClientScope("/authenticate");
        SortedSet<ClientRedirectUriEntity> s = Sets.newTreeSet();
        s.add(uri);
        cd.setClientRegisteredRedirectUris(s);
        when(clientDetailsEntityCacheManager.retrieve("x")).thenReturn(cd);
        
        //notifications default behaviour (none)
        when(notificationManager.findByOrcid(Matchers.anyString(),Matchers.anyBoolean(),Matchers.anyInt(),Matchers.anyInt())).thenReturn(Lists.newArrayList());
        when(notificationManager.createFindMyStuffNotification(Matchers.anyString(),Matchers.anyString(),Matchers.anyString())).thenReturn(new NotificationFindMyStuffEntity());
    }
    
    @Test
    public void testFindIfAppropriate(){
        //new user
        when(orcidOauth2TokenDetailService.doesClientKnowUser(Matchers.anyString(), Matchers.anyString())).thenReturn(false);
        when(workManagerReadOnly.getAllExternalIDs(Matchers.contains(""))).thenReturn(new ExternalIDs());
        when(findMyStuffHistoryDao.findAll(Matchers.contains(""))).thenReturn(new ArrayList<FindMyStuffHistoryEntity>());
        
        List<FindMyStuffResult> r = findMyStuffManager.findIfAppropriate("0000-0000-0000-0000");
        
        assertEquals(r.get(0).getFinderName(),"DataciteFinder");
        assertEquals(r.get(0).getResults().size(),1);
        assertEquals(r.get(0).getResults().get(0).getId(),"id1");
        //check finder invoked
        verify(dataciteFinder, times(1)).find(Matchers.anyString(), Matchers.any());
        //check history persisted
        verify(findMyStuffHistoryDao, times(1)).persist(Matchers.any());
        //check notification created
        
    }
    
    @Test
    public void testFindIfAppropriateExistingHistory(){
        when(orcidOauth2TokenDetailService.doesClientKnowUser(Matchers.anyString(), Matchers.anyString())).thenReturn(false);
        when(workManagerReadOnly.getAllExternalIDs(Matchers.contains(""))).thenReturn(new ExternalIDs());
        //optedOut
        FindMyStuffHistoryEntity e = new FindMyStuffHistoryEntity();
        e.setFinderName("DataciteFinder");
        e.setOptOut(false);
        List<FindMyStuffHistoryEntity> list = Lists.newArrayList();
        list.add(e);
        when(findMyStuffHistoryDao.findAll(Matchers.contains(""))).thenReturn(list);
        
        findMyStuffManager.findIfAppropriate("0000-0000-0000-0000");
        //check finder invoked
        verify(dataciteFinder, times(1)).find(Matchers.anyString(), Matchers.any());
        //check history merged
        verify(findMyStuffHistoryDao, times(1)).merge(Matchers.any());
    }
    
    @Test
    public void testFindIfAppropriateOptedOut(){
        when(orcidOauth2TokenDetailService.doesClientKnowUser(Matchers.anyString(), Matchers.anyString())).thenReturn(false);
        when(workManagerReadOnly.getAllExternalIDs(Matchers.contains(""))).thenReturn(new ExternalIDs());
        //optedOut
        FindMyStuffHistoryEntity e = new FindMyStuffHistoryEntity();
        e.setFinderName("DataciteFinder");
        e.setOptOut(true);
        List<FindMyStuffHistoryEntity> list = Lists.newArrayList();
        list.add(e);
        when(findMyStuffHistoryDao.findAll(Matchers.contains(""))).thenReturn(list);
        
        findMyStuffManager.findIfAppropriate("0000-0000-0000-0000");
        //check finder not invoked
        verify(dataciteFinder, times(0)).find(Matchers.anyString(), Matchers.any());
        //check history not persisted/merged
        verify(findMyStuffHistoryDao, times(0)).merge(Matchers.any());
        verify(findMyStuffHistoryDao, times(0)).persist(Matchers.any());

    }
       
    @Test
    public void testFindIfAppropriateExistingWorks(){
        when(orcidOauth2TokenDetailService.doesClientKnowUser(Matchers.anyString(), Matchers.anyString())).thenReturn(false);
        //user with existing works
        ExternalID id = new ExternalID();
        ExternalIDs ids = new ExternalIDs();
        ids.getExternalIdentifier().add(id);
        when(workManagerReadOnly.getAllExternalIDs(Matchers.contains(""))).thenReturn(ids);
        when(findMyStuffHistoryDao.findAll(Matchers.contains(""))).thenReturn(new ArrayList<FindMyStuffHistoryEntity>());
        
        findMyStuffManager.findIfAppropriate("0000-0000-0000-0000");
        //check id passed to finder
        verify(dataciteFinder, times(1)).find(Matchers.anyString(), Matchers.eq(ids));
        //check history persisted
        verify(findMyStuffHistoryDao, times(1)).persist(Matchers.any());
    }
    
    @Test
    public void testFindIfAppropriateExistingPerms(){
        //user with existing permissions
        when(orcidOauth2TokenDetailService.doesClientKnowUser(Matchers.anyString(), Matchers.anyString())).thenReturn(true);
        when(workManagerReadOnly.getAllExternalIDs(Matchers.contains(""))).thenReturn(new ExternalIDs());
        when(findMyStuffHistoryDao.findAll(Matchers.contains(""))).thenReturn(new ArrayList<FindMyStuffHistoryEntity>());
        
        findMyStuffManager.findIfAppropriate("0000-0000-0000-0000");
        //check finder not invoked
        verify(dataciteFinder, times(0)).find(Matchers.anyString(), Matchers.any());
        //check history not persisted/merged
        verify(findMyStuffHistoryDao, times(0)).merge(Matchers.any());
        verify(findMyStuffHistoryDao, times(0)).persist(Matchers.any());
    }
    
    @Mock
    Source source;
    
    @Test
    public void testFindIfAppropriateExistingNotifications(){
        //new user
        when(orcidOauth2TokenDetailService.doesClientKnowUser(Matchers.anyString(), Matchers.anyString())).thenReturn(false);
        when(workManagerReadOnly.getAllExternalIDs(Matchers.contains(""))).thenReturn(new ExternalIDs());
        when(findMyStuffHistoryDao.findAll(Matchers.contains(""))).thenReturn(new ArrayList<FindMyStuffHistoryEntity>());
        NotificationFindMyStuff n = new NotificationFindMyStuff();
        n.setNotificationType(NotificationType.FIND_MY_STUFF);
        List<Notification> list = Lists.newArrayList();
        list.add(n);
        when(source.retrieveSourcePath()).thenReturn("x");
        TargetProxyHelper.injectIntoProxy(n, "source", source);
        
        when(notificationManager.findByOrcid(Matchers.anyString(),Matchers.anyBoolean(),Matchers.anyInt(),Matchers.anyInt())).thenReturn(list);
        
        findMyStuffManager.findIfAppropriate("0000-0000-0000-0000");
        //check finder invoked
        verify(dataciteFinder, times(1)).find(Matchers.anyString(), Matchers.any());
        //check history persisted
        verify(findMyStuffHistoryDao, times(1)).persist(Matchers.any());
        //check notification not created
        verify(notificationManager, times(0)).createFindMyStuffNotification(Matchers.anyString(),Matchers.anyString(),Matchers.anyString());
    }

}
