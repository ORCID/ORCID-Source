package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.persistence.dao.impl.WebhookDaoImpl;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;
import org.orcid.test.TargetProxyHelper;

public class WebhookDaoTest {

    @InjectMocks
    private WebhookDaoImpl webhookDao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<WebhookEntity> typedQuery;

    @Mock
    private TypedQuery<BigInteger> countTypedQuery;

    @Mock
    private Query nativeQuery;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(webhookDao, "maxAttemptCount", 25);
    }

    @Test
    public void testMergeFindAndRemove() {
        String orcid = "1234-1234-1234-1234";
        String uri = "http://semantico.com/orcid/1234";
        WebhookEntity webhook = new WebhookEntity();
        webhook.setProfile(orcid);
        webhook.setUri(uri);

        WebhookEntityPk pk = new WebhookEntityPk(orcid, uri);

        when(entityManager.merge(any(WebhookEntity.class))).thenReturn(webhook);
        when(entityManager.find(eq(WebhookEntity.class), eq(pk))).thenReturn(webhook);

        WebhookEntity merged = webhookDao.merge(webhook);
        assertNotNull(merged);
        assertEquals(orcid, merged.getProfile());

        WebhookEntity retrieved = webhookDao.find(pk);
        assertNotNull(retrieved);
        assertEquals(orcid, retrieved.getProfile());

        webhookDao.remove(pk);
        verify(entityManager).remove(webhook);
    }

    @Test
    public void testFindWebhooksReadyToProcess() {
        Date now = new Date();
        WebhookEntity webhook = new WebhookEntity();
        webhook.setProfile("4444-4444-4444-4443");
        
        when(entityManager.createNamedQuery(WebhookEntity.FIND_WEBHOOKS_READY_TO_PROCESS, WebhookEntity.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(webhook));

        List<WebhookEntity> results = webhookDao.findWebhooksReadyToProcess(now, 5, 10, Set.of());
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("4444-4444-4444-4443", results.get(0).getProfile());

        verify(typedQuery).setParameter("retryDelayMinutes", 5);
        verify(typedQuery).setParameter("maxAttemptCount", 25);
        verify(typedQuery).setParameter(eq("clientsToExclude"), any());
        verify(typedQuery).setMaxResults(10);
    }

    @Test
    public void testCountWebhooksReadyToProcess() {
        Date now = new Date();
        when(entityManager.createNamedQuery(WebhookEntity.COUNT_WEBHOOKS_READY_TO_PROCESS, BigInteger.class)).thenReturn(countTypedQuery);
        when(countTypedQuery.getSingleResult()).thenReturn(BigInteger.valueOf(1L));

        long count = webhookDao.countWebhooksReadyToProcess(now, 5);
        
        assertEquals(1L, count);
        verify(countTypedQuery).setParameter("retryDelayMinutes", 5);
        verify(countTypedQuery).setParameter("maxAttemptCount", 25);
    }

    @Test
    public void testMarkAsSent() {
        String orcid = "orcid";
        String uri = "uri";
        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.executeUpdate()).thenReturn(1);

        boolean result = webhookDao.markAsSent(orcid, uri);
        
        assertTrue(result);
        verify(nativeQuery).setParameter("orcid", orcid);
        verify(nativeQuery).setParameter("uri", uri);
    }

    @Test
    public void testMarkAsFailed() {
        String orcid = "orcid";
        String uri = "uri";
        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.executeUpdate()).thenReturn(1);

        boolean result = webhookDao.markAsFailed(orcid, uri);
        
        assertTrue(result);
        verify(nativeQuery).setParameter("orcid", orcid);
        verify(nativeQuery).setParameter("uri", uri);
    }
}
