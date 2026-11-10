package org.orcid.frontend.spring.session.redis;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.frontend.spring.configuration.OrcidBeanClassLoaderAware;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.web.context.request.RequestContextHolder;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class OrcidRedisIndexedSessionRepositoryTest {

    private static final String SESSION_ID = "session-1";
    private static final String ORCID = "0000-0000-0000-0001";

    @Mock
    private RedisOperations<Object, Object> redisOperations;

    @Mock
    private BoundHashOperations<Object, Object, Object> sessionHashOperations;

    @Mock
    private BoundSetOperations<Object, Object> principalSetOperations;

    @Mock
    private BoundSetOperations<Object, Object> expirationSetOperations;

    private OrcidRedisIndexedSessionRepository sessionRepository;

    @Before
    public void setUp() {
        RequestContextHolder.resetRequestAttributes();
        sessionRepository = new OrcidRedisIndexedSessionRepository(redisOperations);
    }

    @After
    public void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void deleteByIdShouldWorkWithoutHttpRequestContext() {
        Instant lastAccessedTime = Instant.now().minusSeconds(30);
        int maxInactiveInterval = 1800;
        long expirationTime = OrcidRedisSessionExpirationPolicy.roundUpToNextMinute(lastAccessedTime.plusSeconds(maxInactiveInterval).toEpochMilli());
        String sessionKey = "spring:session:sessions:" + SESSION_ID;
        String principalKey = "spring:session:index:" + FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME + ":" + ORCID;
        String expirationKey = "spring:session:expirations:" + expirationTime;
        String expiredSessionKey = "spring:session:sessions:expires:" + SESSION_ID;

        Map<Object, Object> sessionEntries = new HashMap<>();
        sessionEntries.put("creationTime", lastAccessedTime.minusSeconds(60).toEpochMilli());
        sessionEntries.put("maxInactiveInterval", maxInactiveInterval);
        sessionEntries.put("lastAccessedTime", lastAccessedTime.toEpochMilli());
        sessionEntries.put("sessionAttr:" + FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, ORCID);

        when(redisOperations.boundHashOps(eq(sessionKey))).thenReturn(sessionHashOperations);
        when(sessionHashOperations.entries()).thenReturn(sessionEntries);
        when(redisOperations.boundSetOps(anyString())).thenReturn(expirationSetOperations);
        when(redisOperations.boundSetOps(eq(expirationKey))).thenReturn(expirationSetOperations);
        when(redisOperations.boundSetOps(eq(principalKey))).thenReturn(principalSetOperations);

        sessionRepository.deleteById(SESSION_ID);

        verify(sessionHashOperations).putAll(anyMap());
        verify(redisOperations, atLeastOnce()).delete(eq(expiredSessionKey));
        verify(principalSetOperations).remove(eq(SESSION_ID));
        verify(expirationSetOperations).remove(eq("expires:" + SESSION_ID));
    }

    @Test
    public void evictSessionsForOrcidShouldDeleteAllIndexedSessions() {
        Map<String, OrcidRedisIndexedSessionRepository.RedisSession> activeSessions = new HashMap<>();
        activeSessions.put(SESSION_ID, null);
        activeSessions.put("session-2", null);

        OrcidRedisIndexedSessionRepository repository = Mockito.spy(sessionRepository);
        doReturn(activeSessions).when(repository).findByIndexNameAndIndexValue(
                FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, ORCID);
        doNothing().when(repository).deleteById(anyString());

        int evictedSessions = repository.evictSessionsForOrcid(ORCID);

        assertEquals(2, evictedSessions);
        verify(repository).deleteById(SESSION_ID);
        verify(repository).deleteById("session-2");
        verify(repository, times(2)).deleteById(anyString());
    }

    @Test
    public void evictSessionsForOrcidShouldNotDeleteSessionsForOtherOrcids() {
        String otherOrcid = "0000-0000-0000-000X";
        String otherSessionId = "other-session";

        Map<String, OrcidRedisIndexedSessionRepository.RedisSession> targetSessions = new HashMap<>();
        targetSessions.put(SESSION_ID, null);
        targetSessions.put("session-2", null);

        OrcidRedisIndexedSessionRepository repository = Mockito.spy(sessionRepository);
        doReturn(targetSessions).when(repository).findByIndexNameAndIndexValue(
                FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, ORCID);
        doNothing().when(repository).deleteById(anyString());

        int evictedSessions = repository.evictSessionsForOrcid(ORCID);

        assertEquals(2, evictedSessions);
        verify(repository).deleteById(SESSION_ID);
        verify(repository).deleteById("session-2");
        verify(repository, Mockito.never()).deleteById(otherSessionId);
        verify(repository, Mockito.never()).findByIndexNameAndIndexValue(
                FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, otherOrcid);
        verify(repository, times(2)).deleteById(anyString());
    }

    @Test
    public void evictSessionsForOrcidShouldIgnoreBlankOrcid() {
        OrcidRedisIndexedSessionRepository repository = Mockito.spy(sessionRepository);

        assertEquals(0, repository.evictSessionsForOrcid(" "));

        verify(repository, Mockito.never()).findByIndexNameAndIndexValue(
                FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, " ");
    }

    @Test
    public void springSessionSerializerShouldAllowJavaLongsForSessionDelta() {
        OrcidBeanClassLoaderAware beanClassLoaderAware = new OrcidBeanClassLoaderAware();
        beanClassLoaderAware.setBeanClassLoader(getClass().getClassLoader());

        RedisSerializer<Object> serializer = beanClassLoaderAware.springSessionDefaultRedisSerializer();
        Map<String, Object> sessionDelta = new HashMap<>();
        sessionDelta.put("lastAccessedTime", 123456789L);

        byte[] json = serializer.serialize(sessionDelta);
        Map<String, Object> deserialized = (Map<String, Object>) serializer.deserialize(json);

        assertEquals(Long.valueOf(123456789L), deserialized.get("lastAccessedTime"));
    }
}
