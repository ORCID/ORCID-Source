package org.orcid.core.utils;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class SourceEntityUtilsTest {

    @Test
    public void extractSourceFromEntityUsesPreloadedClientDetailsWhenAvailable() {
        ClientDetailsEntityCacheManager cacheManager = mock(ClientDetailsEntityCacheManager.class);
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setId("CLIENT");
        ReflectionTestUtils.setField(clientDetails, "lastModified", new Date());
        Map<String, ClientDetailsEntity> clientDetailsById = Collections.singletonMap("CLIENT", clientDetails);
        MinimizedWorkEntity work = new MinimizedWorkEntity();
        work.setClientSourceId("CLIENT");
        work.setOrcid("0000-0000-0000-0001");

        assertNotNull(SourceEntityUtils.extractSourceFromEntity(work, cacheManager, clientDetailsById));

        verify(cacheManager, never()).retrieve("CLIENT");
    }
}
