package org.orcid.core.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.orgs.extId.normalizer.impl.ISNIOrgDisambiguatedExternalIdNormalizer;
import org.orcid.persistence.dao.OrgDisambiguatedExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class NormalizeISNIsTest {

    @Mock
    private OrgDisambiguatedExternalIdentifierDao orgDisambiguatedExternalIdentifierDao;

    @InjectMocks
    private NormalizeISNIs normalizeISNIs;

    @Captor
    private ArgumentCaptor<OrgDisambiguatedExternalIdentifierEntity> captor;

    private ISNIOrgDisambiguatedExternalIdNormalizer normalizer = new ISNIOrgDisambiguatedExternalIdNormalizer();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(normalizeISNIs, "normalizer", normalizer);
        Mockito.when(orgDisambiguatedExternalIdentifierDao.findISNIsOfIncorrectLength(Mockito.eq(200)))
                .thenReturn(getListOfBadOrgDisambiguatedExternalIdentifierEntities()).thenReturn(new ArrayList<>());
    }

    @Test
    public void testNormalizeISNIs() {
        normalizeISNIs.normalizeISNIs();
        Mockito.verify(orgDisambiguatedExternalIdentifierDao, Mockito.times(9)).merge(captor.capture());

        List<OrgDisambiguatedExternalIdentifierEntity> values = captor.getAllValues();
        assertEquals(9, values.size());
        assertEquals("1234567812345678", values.get(0).getIdentifier());
        assertEquals("1234567812345678", values.get(1).getIdentifier());
        assertEquals("1234567812345678", values.get(2).getIdentifier());
        assertEquals("0000000012345678", values.get(3).getIdentifier());
        assertEquals("0000000012345678", values.get(4).getIdentifier());
        assertEquals("0000000012345678", values.get(5).getIdentifier());
        assertEquals("0000000000001234", values.get(6).getIdentifier());
        assertEquals("0000000000000000", values.get(7).getIdentifier());
        assertNull(values.get(8).getIdentifier());
    }

    private List<OrgDisambiguatedExternalIdentifierEntity> getListOfBadOrgDisambiguatedExternalIdentifierEntities() {
        List<OrgDisambiguatedExternalIdentifierEntity> identifiers = new ArrayList<>();
        identifiers.add(getOrgDisambiguatedExternalIdentifierEntity("1234 5678 1234 5678"));
        identifiers.add(getOrgDisambiguatedExternalIdentifierEntity("1234-5678-1234-5678"));
        identifiers.add(getOrgDisambiguatedExternalIdentifierEntity("oops1234-5678-1234-5678"));
        identifiers.add(getOrgDisambiguatedExternalIdentifierEntity("1234-5678"));
        identifiers.add(getOrgDisambiguatedExternalIdentifierEntity("12345678"));
        identifiers.add(getOrgDisambiguatedExternalIdentifierEntity("001234-5678-"));
        identifiers.add(getOrgDisambiguatedExternalIdentifierEntity("1234"));
        identifiers.add(getOrgDisambiguatedExternalIdentifierEntity(""));
        identifiers.add(getOrgDisambiguatedExternalIdentifierEntity(null));
        return identifiers;
    }

    private OrgDisambiguatedExternalIdentifierEntity getOrgDisambiguatedExternalIdentifierEntity(String identifier) {
        OrgDisambiguatedExternalIdentifierEntity entity = new OrgDisambiguatedExternalIdentifierEntity();
        entity.setIdentifierType("ISNI");
        entity.setIdentifier(identifier);
        return entity;
    }

}
