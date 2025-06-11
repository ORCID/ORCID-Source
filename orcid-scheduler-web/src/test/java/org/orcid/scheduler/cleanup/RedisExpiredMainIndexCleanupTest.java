package org.orcid.scheduler.cleanup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RedisExpiredMainIndexCleanupTest {
    private final String index0 = "spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:0000-0000-0000-0000-0000";
    private final String index1 = "spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:0000-0000-0000-0000-0001";
    private final String index2 = "spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:0000-0000-0000-0000-0002";
    private final String index3 = "spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:0000-0000-0000-0000-0003";
    private final String index4 = "spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:0000-0000-0000-0000-0004";
    private final Set<String> sessions0 = Set.of("a0", "b0", "b1", "b2");
    private final Set<String> sessions1 = Set.of("b3", "a1");
    private final Set<String> sessions2 = Set.of("a2", "a3");
    private final Set<String> sessions3 = Set.of("b4", "b5");
    private final Set<String> sessions4 = Set.of();

    @Mock
    Jedis jedisMock;

    @Mock
    private JedisPool jedisPoolMock;

    @Mock
    private JedisPoolBuilder poolBuilderMock;

    @InjectMocks
    private RedisExpiredMainIndexCleanup cleanup;

    @Before
    public void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        when(poolBuilderMock.build()).thenReturn(jedisPoolMock);
        when(jedisPoolMock.getResource()).thenReturn(jedisMock);
        FieldSetter.setField(cleanup, cleanup.getClass().getDeclaredField("jedisMaxPoolSize"), 1);

        /*
        * Active sessions are:
        * a0, a1, a2, a3
        *
        * Main idexes will look like this:
        *
        * spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:0000-0000-0000-0000-0000:
        *   - a0
        *   - b0
        *   - b1
        *   - b2
        * spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:0000-0000-0000-0000-0001:
        *   - a1
        *   - b3
        * spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:0000-0000-0000-0000-0002:
        *   - a2
        *   - a3
        * spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:0000-0000-0000-0000-0003:
        *   - b4
        *   - b5
        * spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:0000-0000-0000-0000-0004:
        *   [EMPTY]
        * */
        Set<String> activeSessions = Set.of("spring:session:sessions:a0","spring:session:sessions:a1","spring:session:sessions:a2","spring:session:sessions:a3");
        Set<String> mainIndex = Set.of(index0, index1, index2, index3, index4);

        when(jedisMock.keys(eq("spring:session:sessions:*"))).thenReturn(activeSessions);
        when(jedisMock.keys(eq("spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:*"))).thenReturn(mainIndex);
        when(jedisMock.smembers(index0)).thenReturn(sessions0);
        when(jedisMock.smembers(index1)).thenReturn(sessions1);
        when(jedisMock.smembers(index2)).thenReturn(sessions2);
        when(jedisMock.smembers(index3)).thenReturn(sessions3).thenReturn(Set.of());
        when(jedisMock.smembers(index4)).thenReturn(sessions4);
    }

    @Test
    public void executeTest() throws ExecutionException, InterruptedException {
        cleanup.execute();
        verify(jedisMock, times(1)).keys("spring:session:sessions:*");
        verify(jedisMock, times(1)).keys("spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:*");
        // Each index is read twice, one to get the entries, one to check if it can removed
        verify(jedisMock, times(2)).smembers(index0);
        verify(jedisMock, times(2)).smembers(index1);
        verify(jedisMock, times(2)).smembers(index2);
        verify(jedisMock, times(2)).smembers(index3);
        // This is the only one that will be checked one time, since it is empty, it will be removed right away
        verify(jedisMock, times(1)).smembers(index4);
        verify(jedisMock, times(1)).srem(index0, "b0");
        verify(jedisMock, times(1)).srem(index0, "b1");
        verify(jedisMock, times(1)).srem(index0, "b2");
        // Check index0
        verify(jedisMock, never()).srem(index0, "a0");
        verify(jedisMock, never()).del(index0);
        // Check index1
        verify(jedisMock, times(1)).srem(index1, "b3");
        verify(jedisMock, never()).srem(index1, "a1");
        verify(jedisMock, never()).del(index1);
        // Check index2
        verify(jedisMock, never()).srem(eq(index2), anyString());
        verify(jedisMock, never()).del(index2);
        // Check index3
        verify(jedisMock, times(1)).srem(index3, "b4");
        verify(jedisMock, times(1)).srem(index3, "b5");
        verify(jedisMock, times(1)).del(index3);
        // Check index4
        verify(jedisMock, never()).srem(eq(index4), anyString());
        verify(jedisMock, times(1)).del(index4);
    }
}
