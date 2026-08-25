package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;

@RunWith(MockitoJUnitRunner.class)
public class SourceMapperV2Test {

    @Mock
    private OrcidUrlManager orcidUrlManager;

    @Mock
    private ClientDetailsManagerReadOnly clientDetailsManagerReadOnly;

    @Mock
    private SourceNameCacheManager sourceNameCacheManager;

    @Mock
    private SourceAwareEntity<?> entity;

    @InjectMocks
    private final SourceMapperV2 mapper = Mappers.getMapper(SourceMapperV2.class);

    @Before
    public void setUp() {
        when(orcidUrlManager.getBaseHost()).thenReturn("orcid.org");
        when(orcidUrlManager.getBaseUriHttp()).thenReturn("http://orcid.org");
    }

    @Test
    public void toSourceShouldReturnNullForNullEntity() {
        assertNull(mapper.toSource(null));
    }

    @Test
    public void toSourceShouldReturnNullForEmptySourceId() {
        when(entity.getElementSourceId()).thenReturn(null);
        assertNull(mapper.toSource(entity));
    }

    @Test
    public void toSourceShouldMapClientSource() {
        String clientId = "APP-1234567890123456";
        when(entity.getElementSourceId()).thenReturn(clientId);
        when(clientDetailsManagerReadOnly.isLegacyClientId(clientId)).thenReturn(false);
        when(sourceNameCacheManager.retrieve(clientId)).thenReturn("Test Client App");

        Source source = mapper.toSource(entity);

        assertNotNull(source);
        assertNotNull(source.getSourceClientId());
        assertNull(source.getSourceOrcid());
        assertEquals(clientId, source.getSourceClientId().getPath());
        assertEquals("http://orcid.org/client/" + clientId, source.getSourceClientId().getUri());
        assertEquals("orcid.org", source.getSourceClientId().getHost());
        assertEquals("Test Client App", source.getSourceName().getContent());
    }

    @Test
    public void toSourceShouldMapOrcidSource() {
        String userOrcid = "0000-0000-0000-0001";
        when(entity.getElementSourceId()).thenReturn(userOrcid);
        when(clientDetailsManagerReadOnly.isLegacyClientId(userOrcid)).thenReturn(false);
        when(sourceNameCacheManager.retrieve(userOrcid)).thenReturn("Test User");

        Source source = mapper.toSource(entity);

        assertNotNull(source);
        assertNotNull(source.getSourceOrcid());
        assertNull(source.getSourceClientId());
        assertEquals(userOrcid, source.getSourceOrcid().getPath());
        assertEquals("http://orcid.org/" + userOrcid, source.getSourceOrcid().getUri());
        assertEquals("orcid.org", source.getSourceOrcid().getHost());
        assertEquals("Test User", source.getSourceName().getContent());
    }
}