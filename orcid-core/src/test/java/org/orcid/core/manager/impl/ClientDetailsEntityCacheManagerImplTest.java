package org.orcid.core.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ehcache.Cache;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class ClientDetailsEntityCacheManagerImplTest {

    private ClientDetailsEntityCacheManagerImpl cacheManager;

    @Mock
    private ClientDetailsManager clientDetailsManager;

    @Mock
    private Cache<Object, ClientDetailsEntity> clientDetailsCache;

    @Mock
    private Cache<Object, ClientDetailsEntity> clientDetailsIdPCache;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        cacheManager = new ClientDetailsEntityCacheManagerImpl();
        ReflectionTestUtils.setField(cacheManager, "clientDetailsManager", clientDetailsManager);
        ReflectionTestUtils.setField(cacheManager, "clientDetailsCache", clientDetailsCache);
        ReflectionTestUtils.setField(cacheManager, "clientDetailsIdPCache", clientDetailsIdPCache);
    }

    @Test
    public void retrieveAllReturnsFreshCachedClientsWithoutSingleClientValidation() {
        Date lastModified = new Date();
        ClientDetailsEntity cached = client("A", lastModified);
        when(clientDetailsManager.getLastModifiedByClientIds(Arrays.asList("A"))).thenReturn(java.util.Collections.singletonMap("A", lastModified));
        when(clientDetailsCache.get(any())).thenReturn(cached);

        Map<String, ClientDetailsEntity> result = cacheManager.retrieveAll(Arrays.asList("A"));

        assertEquals(1, result.size());
        assertSame(cached, result.get("A"));
        verify(clientDetailsManager).getLastModifiedByClientIds(Arrays.asList("A"));
        verify(clientDetailsManager, never()).getLastModified("A");
        verify(clientDetailsManager, never()).findByClientIds(any(List.class));
    }

    @Test
    public void retrieveAllReloadsStaleClientsInBulk() {
        Date oldLastModified = new Date(1L);
        Date newLastModified = new Date(2L);
        ClientDetailsEntity cached = client("A", oldLastModified);
        ClientDetailsEntity fresh = client("A", newLastModified);
        when(clientDetailsManager.getLastModifiedByClientIds(Arrays.asList("A"))).thenReturn(java.util.Collections.singletonMap("A", newLastModified));
        when(clientDetailsCache.get(any())).thenReturn(cached);
        when(clientDetailsManager.findByClientIds(Arrays.asList("A"))).thenReturn(Arrays.asList(fresh));

        Map<String, ClientDetailsEntity> result = cacheManager.retrieveAll(Arrays.asList("A"));

        assertEquals(1, result.size());
        assertSame(fresh, result.get("A"));
        verify(clientDetailsManager).findByClientIds(Arrays.asList("A"));
        verify(clientDetailsCache).put(any(), any(ClientDetailsEntity.class));
    }

    private ClientDetailsEntity client(String clientId, Date lastModified) {
        ClientDetailsEntity entity = new ClientDetailsEntity();
        entity.setId(clientId);
        ReflectionTestUtils.setField(entity, "lastModified", lastModified);
        return entity;
    }
}
