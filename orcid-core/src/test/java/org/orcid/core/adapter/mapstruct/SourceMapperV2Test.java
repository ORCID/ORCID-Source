package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

public class SourceMapperV2Test {

    @Test
    public void toSourceShouldReturnNullWhenNoElementSourceId() {
        ExternalIdentifierEntity entity = new ExternalIdentifierEntity();
        OrcidUrlManager urlManager = mock(OrcidUrlManager.class);
        ClientDetailsManagerReadOnly clientDetailsManager = mock(ClientDetailsManagerReadOnly.class);
        SourceNameCacheManager sourceNameCacheManager = mock(SourceNameCacheManager.class);

        Source source = SourceMapperV2.INSTANCE.toSource(entity, urlManager, clientDetailsManager, sourceNameCacheManager);

        assertNull(source);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void toSourceShouldMapClientSource() {
        ExternalIdentifierEntity entity = new ExternalIdentifierEntity();
        entity.setClientSourceId("APP-12345");

        OrcidUrlManager urlManager = mock(OrcidUrlManager.class);
        when(urlManager.getBaseHost()).thenReturn("sandbox.orcid.org");
        when(urlManager.getBaseUriHttp()).thenReturn("http://sandbox.orcid.org");

        ClientDetailsManagerReadOnly clientDetailsManager = mock(ClientDetailsManagerReadOnly.class);
        when(clientDetailsManager.isLegacyClientId("APP-12345")).thenReturn(false);

        SourceNameCacheManager sourceNameCacheManager = mock(SourceNameCacheManager.class);
        when(sourceNameCacheManager.retrieve("APP-12345")).thenReturn("Test Client");

        Source source = SourceMapperV2.INSTANCE.toSource(entity, urlManager, clientDetailsManager, sourceNameCacheManager);

        assertNotNull(source);
        assertNotNull(source.getSourceClientId());
        assertEquals("APP-12345", source.getSourceClientId().getPath());
        assertEquals("http://sandbox.orcid.org/client/APP-12345", source.getSourceClientId().getUri());
        assertEquals("sandbox.orcid.org", source.getSourceClientId().getHost());
        assertEquals("Test Client", source.getSourceName().getContent());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void toSourceShouldMapOrcidSource() {
        ExternalIdentifierEntity entity = new ExternalIdentifierEntity();
        entity.setSourceId("0000-0001-2345-6789");

        OrcidUrlManager urlManager = mock(OrcidUrlManager.class);
        when(urlManager.getBaseHost()).thenReturn("sandbox.orcid.org");
        when(urlManager.getBaseUriHttp()).thenReturn("http://sandbox.orcid.org");

        ClientDetailsManagerReadOnly clientDetailsManager = mock(ClientDetailsManagerReadOnly.class);
        when(clientDetailsManager.isLegacyClientId("0000-0001-2345-6789")).thenReturn(false);

        SourceNameCacheManager sourceNameCacheManager = mock(SourceNameCacheManager.class);
        when(sourceNameCacheManager.retrieve("0000-0001-2345-6789")).thenReturn("Test User");

        Source source = SourceMapperV2.INSTANCE.toSource(entity, urlManager, clientDetailsManager, sourceNameCacheManager);

        assertNotNull(source);
        assertNotNull(source.getSourceOrcid());
        assertEquals("0000-0001-2345-6789", source.getSourceOrcid().getPath());
        assertEquals("http://sandbox.orcid.org/0000-0001-2345-6789", source.getSourceOrcid().getUri());
        assertEquals("sandbox.orcid.org", source.getSourceOrcid().getHost());
        assertEquals("Test User", source.getSourceName().getContent());
    }
}
