package org.orcid.frontend.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.utils.cache.redis.RedisClient;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DisposableEmailsServiceTest {

    @Mock
    private RedisClient redisClient;

    @InjectMocks
    private DisposableEmailsService service;

    @Before
    public void before() {
        when(redisClient.getList("disposable-domains")).thenReturn(Arrays.asList("mailinator.com", "Trashmail.NET"));
        service.enable();
        service.init();
    }

    @Test
    public void domainNotInListReturnsFalseTest() {
        assertFalse(service.isDisposableEmail("user@orcid.org"));
        assertFalse(service.isDisposableEmail("user@notmailinator.com"));
    }

    @Test
    public void domainInListReturnsTrueTest() {
        assertTrue(service.isDisposableEmail("user@mailinator.com"));
        assertTrue(service.isDisposableEmail("USER@MAILINATOR.COM"));
        assertTrue(service.isDisposableEmail("user@trashmail.net"));
    }

    @Test
    public void subdomainOfDomainInListReturnsTrueTest() {
        assertTrue(service.isDisposableEmail("user@sub.mailinator.com"));
        assertTrue(service.isDisposableEmail("user@a.b.mailinator.com"));
    }

    @Test
    public void invalidEmailsReturnFalseTest() {
        assertFalse(service.isDisposableEmail(null));
        assertFalse(service.isDisposableEmail("no-at-sign"));
        assertFalse(service.isDisposableEmail("user@"));
    }

    @Test
    public void refreshKeepsCurrentCacheWhenRedisReturnsNothingTest() {
        when(redisClient.getList("disposable-domains")).thenReturn(Collections.emptyList());
        service.refreshCache();
        assertTrue(service.isDisposableEmail("user@mailinator.com"));
    }

    @Test
    public void refreshReplacesCacheTest() {
        when(redisClient.getList("disposable-domains")).thenReturn(Arrays.asList("example.org"));
        service.refreshCache();
        assertTrue(service.isDisposableEmail("user@example.org"));
        assertFalse(service.isDisposableEmail("user@mailinator.com"));
    }
}
