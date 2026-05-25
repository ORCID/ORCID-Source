package org.orcid.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceName;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class SourceEntityUtilsTest {

    @Mock
    private RecordNameManagerReadOnly recordNameManagerReadOnlyV3;

    @Mock
    private SourceNameCacheManager sourceNameCacheManager;

    @Mock
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Mock
    private OrcidUrlManager orcidUrlManager;

    @InjectMocks
    private SourceEntityUtils sourceEntityUtils;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(orcidUrlManager.getBaseHost()).thenReturn("orcid.org");
        when(orcidUrlManager.getBaseUrl()).thenReturn("https://orcid.org");
    }

    @Test
    public void testGetSourceId() {
        SourceEntity sourceEntity = new SourceEntity();
        
        // Test cached
        sourceEntity.setCachedSourceId("cached-id");
        assertEquals("cached-id", SourceEntityUtils.getSourceId(sourceEntity));
        
        // Test client
        sourceEntity.setCachedSourceId(null);
        ClientDetailsEntity client = new ClientDetailsEntity("client-id");
        sourceEntity.setSourceClient(client);
        assertEquals("client-id", SourceEntityUtils.getSourceId(sourceEntity));
        
        // Test profile
        sourceEntity.setSourceClient(null);
        ProfileEntity profile = new ProfileEntity("orcid-id");
        sourceEntity.setSourceProfile(profile);
        assertEquals("orcid-id", SourceEntityUtils.getSourceId(sourceEntity));
        
        // Test null
        sourceEntity.setSourceProfile(null);
        assertNull(SourceEntityUtils.getSourceId(sourceEntity));
    }

    @Test
    public void testGetSourceNameFromSource() {
        Source source = new Source();
        assertNull(SourceEntityUtils.getSourceName(source));
        
        source.setSourceName(new SourceName("Source Name"));
        assertEquals("Source Name", SourceEntityUtils.getSourceName(source));
        
        source.setSourceName(new SourceName(""));
        assertNull(SourceEntityUtils.getSourceName(source));
    }

    @Test
    public void testGetSourceKey() {
        // Mocking SourceAwareEntity since it's abstract
        SourceAwareEntity<?> entity = mock(SourceAwareEntity.class);
        when(entity.getSourceId()).thenReturn("orcid");
        when(entity.getClientSourceId()).thenReturn("client");
        when(entity.getAssertionOriginClientSourceId()).thenReturn("assertion");
        
        assertEquals("orcid|client|assertion", SourceEntityUtils.getSourceKey(entity));
        
        when(entity.getSourceId()).thenReturn(null);
        when(entity.getClientSourceId()).thenReturn("");
        when(entity.getAssertionOriginClientSourceId()).thenReturn(null);
        assertEquals("-|-|-", SourceEntityUtils.getSourceKey(entity));
    }

    @Test
    public void testGetSourceNameFromEntity() {
        SourceEntity sourceEntity = new SourceEntity();
        
        // Test cached
        sourceEntity.setCachedSourceName("cached-name");
        assertEquals("cached-name", sourceEntityUtils.getSourceName(sourceEntity));
        
        // Test client
        sourceEntity.setCachedSourceName(null);
        ClientDetailsEntity client = new ClientDetailsEntity("client-id", "Client Name");
        sourceEntity.setSourceClient(client);
        assertEquals("Client Name", sourceEntityUtils.getSourceName(sourceEntity));
        
        // Test profile
        sourceEntity.setSourceClient(null);
        ProfileEntity profile = new ProfileEntity("orcid-id");
        sourceEntity.setSourceProfile(profile);
        when(recordNameManagerReadOnlyV3.fetchDisplayablePublicName("orcid-id")).thenReturn("Profile Name");
        assertEquals("Profile Name", sourceEntityUtils.getSourceName(sourceEntity));
        
        // Test null
        sourceEntity.setSourceProfile(null);
        assertNull(sourceEntityUtils.getSourceName(sourceEntity));
    }

    @Test
    public void testPopulateSourceAwareEntityFromSource() {
        Source source = new Source();
        source.setSourceOrcid(new SourceOrcid("orcid-id"));
        source.setSourceClientId(new SourceClientId("client-id"));
        source.setAssertionOriginClientId(new SourceClientId("assertion-id"));
        
        SourceAwareEntity<?> entity = new AddressEntity(); // Using a concrete subclass
        sourceEntityUtils.populateSourceAwareEntityFromSource(source, entity);
        
        assertEquals("orcid-id", entity.getSourceId());
        assertEquals("client-id", entity.getClientSourceId());
        assertEquals("assertion-id", entity.getAssertionOriginClientSourceId());
    }

    @Test
    public void testExtractSourceFromEntity() {
        AddressEntity entity = new AddressEntity();
        entity.setSourceId("orcid-id");
        entity.setClientSourceId("client-id");
        entity.setAssertionOriginClientSourceId("assertion-id");
        entity.setOrcid("user-orcid");
        
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setUserOBOEnabled(true);
        when(clientDetailsEntityCacheManager.retrieve("client-id")).thenReturn(clientDetails);
        
        Source source = sourceEntityUtils.extractSourceFromEntity(entity);
        
        assertEquals("orcid-id", source.getSourceOrcid().getPath());
        assertEquals("client-id", source.getSourceClientId().getPath());
        assertEquals("assertion-id", source.getAssertionOriginClientId().getPath());
        assertEquals("user-orcid", source.getAssertionOriginOrcid().getPath());
        
        // Test without OBO
        clientDetails.setUserOBOEnabled(false);
        source = sourceEntityUtils.extractSourceFromEntity(entity);
        assertNull(source.getAssertionOriginOrcid());
    }

    @Test
    public void testExtractSourceFromProfileComplete() {
        ProfileEntity profile = new ProfileEntity("user-orcid");
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceProfile(new ProfileEntity("source-orcid"));
        sourceEntity.setSourceClient(new ClientDetailsEntity("source-client"));
        profile.setSource(sourceEntity);
        
        when(sourceNameCacheManager.retrieve("source-orcid")).thenReturn("Source Orcid Name");
        when(sourceNameCacheManager.retrieve("source-client")).thenReturn("Source Client Name");
        
        Source source = sourceEntityUtils.extractSourceFromProfileComplete(profile);
        
        assertEquals("source-orcid", source.getSourceOrcid().getPath());
        assertEquals("orcid.org", source.getSourceOrcid().getHost());
        assertEquals("https://orcid.org/source-orcid", source.getSourceOrcid().getUri());
        
        assertEquals("source-client", source.getSourceClientId().getPath());
        assertEquals("orcid.org", source.getSourceClientId().getHost());
        assertEquals("https://orcid.org/client/source-client", source.getSourceClientId().getUri());
        
        assertEquals("Source Client Name", source.getSourceName().getContent());
    }

    @Test
    public void testExtractSourceFromEntityComplete() {
        AddressEntity entity = new AddressEntity();
        entity.setSourceId("orcid-id");
        entity.setClientSourceId("client-id");
        
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setUserOBOEnabled(false);
        when(clientDetailsEntityCacheManager.retrieve("client-id")).thenReturn(clientDetails);
        
        when(sourceNameCacheManager.retrieve("orcid-id")).thenReturn("Orcid Name");
        when(sourceNameCacheManager.retrieve("client-id")).thenReturn("Client Name");
        
        Source source = sourceEntityUtils.extractSourceFromEntityComplete(entity);
        
        assertEquals("orcid-id", source.getSourceOrcid().getPath());
        assertEquals("client-id", source.getSourceClientId().getPath());
        assertEquals("Client Name", source.getSourceName().getContent());
    }

    @Test
    public void testIsTheSameSource_SourceOrcid() {
        Source s1 = new Source();
        s1.setSourceOrcid(new SourceOrcid("orcid-1"));
        
        Source s2 = new Source();
        s2.setSourceOrcid(new SourceOrcid("orcid-1"));
        
        assertTrue(sourceEntityUtils.isTheSameSource(s1, s2));
        
        s2.setSourceOrcid(new SourceOrcid("orcid-2"));
        assertFalse(sourceEntityUtils.isTheSameSource(s1, s2));
        
        AddressEntity entity = new AddressEntity();
        entity.setSourceId("orcid-1");
        assertTrue(sourceEntityUtils.isTheSameSource(s1, entity));
        
        entity.setSourceId("orcid-2");
        assertFalse(sourceEntityUtils.isTheSameSource(s1, entity));
    }

    @Test
    public void testIsTheSameSource_SourceClientId() {
        ClientDetailsEntity clientDetails1 = new ClientDetailsEntity();
        clientDetails1.setId("client-1");
        clientDetails1.setUserOBOEnabled(true);
        when(clientDetailsEntityCacheManager.retrieve("client-1")).thenReturn(clientDetails1);

        Source s1 = new Source();
        s1.setSourceClientId(new SourceClientId("client-1"));

        Source s2 = new Source();
        s2.setSourceClientId(new SourceClientId("client-1"));

        assertTrue(sourceEntityUtils.isTheSameSource(s1, s2));

        Source s3 = new Source();
        s3.setSourceClientId(new SourceClientId("client-1"));
        s3.setAssertionOriginOrcid(new SourceOrcid("orcid-1"));

        AddressEntity entity = new AddressEntity();
        entity.setOrcid("orcid-1");
        entity.setClientSourceId("client-1");
        assertTrue(sourceEntityUtils.isTheSameSource(s3, entity));

        entity.setClientSourceId("client-2");
        assertFalse(sourceEntityUtils.isTheSameSource(s3, entity));
    }
}
