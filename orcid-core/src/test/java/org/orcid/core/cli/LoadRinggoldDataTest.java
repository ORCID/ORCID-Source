/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;

public class LoadRinggoldDataTest {

    @Mock
    private OrgDisambiguatedExternalIdentifierDao mockOrgDisambiguatedExternalIdentifierDao;
    @Mock
    private OrgDisambiguatedDao mockOrgDisambiguatedDao;
    @Mock
    private OrgDao mockOrgDao;

    private LoadRinggoldData loader = new LoadRinggoldData();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        loader.setOrgDao(mockOrgDao);
        loader.setOrgDisambiguatedDao(mockOrgDisambiguatedDao);
        loader.setOrgDisambiguatedExternalIdentifierDao(mockOrgDisambiguatedExternalIdentifierDao);
    }

    @Test
    public void test_InvalidDirectory() {
        fail();
    }
    
    @Test
    public void test_InvalidZipFile() {
        fail();
    }
    
    @Test
    public void test_EmptyDB() throws URISyntaxException {
        when(mockOrgDisambiguatedDao.findBySourceIdAndSourceType(Matchers.anyString(), Matchers.eq("RINGGOLD"))).thenReturn(null);
        when(mockOrgDisambiguatedExternalIdentifierDao.exists(Matchers.anyLong(), Matchers.anyString(), Matchers.anyString())).thenReturn(false);
        when(mockOrgDao.findByNameCityRegionAndCountry(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.any())).thenReturn(null);

        Path path = Paths.get(getClass().getClassLoader().getResource("ringgold/test_1/").toURI());
        File testDirectory = path.toFile();
        assertTrue(testDirectory.exists());

        loader.setDirectory(testDirectory);
        loader.execute();

        ArgumentCaptor<OrgDisambiguatedEntity> orgDisambiguatedEntityCaptor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(mockOrgDisambiguatedDao, times(4)).persist(orgDisambiguatedEntityCaptor.capture());
        List<OrgDisambiguatedEntity> newOrgDisambiguatedEntities = orgDisambiguatedEntityCaptor.getAllValues();
        assertEquals(4, newOrgDisambiguatedEntities.size());

        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for (OrgDisambiguatedEntity entity : newOrgDisambiguatedEntities) {
            switch (entity.getSourceId()) {
            case "1":
                assertNull(entity.getSourceParentId());
                assertEquals("1. Name", entity.getName());
                assertEquals("City#1", entity.getCity());
                assertEquals("State#1", entity.getRegion());
                assertEquals(org.orcid.jaxb.model.message.Iso3166Country.US, entity.getCountry());
                assertEquals("type/1", entity.getOrgType());
                found1 = true;
                break;
            case "2":
                assertNull(entity.getSourceParentId());
                assertEquals("2. Name", entity.getName());
                assertEquals("City#2", entity.getCity());
                assertNull(entity.getRegion());
                assertEquals(org.orcid.jaxb.model.message.Iso3166Country.CR, entity.getCountry());
                assertEquals("type/2", entity.getOrgType());
                found2 = true;
                break;
            case "3":
                assertNull(entity.getSourceParentId());
                assertEquals("3. Name", entity.getName());
                assertEquals("City#3", entity.getCity());
                assertNull(entity.getRegion());
                assertEquals(org.orcid.jaxb.model.message.Iso3166Country.US, entity.getCountry());
                assertEquals("type/3", entity.getOrgType());
                found3 = true;
                break;
            case "4":
                assertEquals("1", entity.getSourceParentId());
                assertEquals("4. Name", entity.getName());
                assertEquals("City#4", entity.getCity());
                assertEquals("State#4", entity.getRegion());
                assertEquals(org.orcid.jaxb.model.message.Iso3166Country.CR, entity.getCountry());
                assertEquals("type/4", entity.getOrgType());
                found4 = true;
                break;
            default:
                fail("Invalid ringgold id found: " + entity.getSourceId());
                break;
            }
        }
        ;
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);

        ArgumentCaptor<OrgDisambiguatedExternalIdentifierEntity> orgDisambiguatedExaternalIdentifierEntityCaptor = ArgumentCaptor
                .forClass(OrgDisambiguatedExternalIdentifierEntity.class);
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(8)).persist(orgDisambiguatedExaternalIdentifierEntityCaptor.capture());
        List<OrgDisambiguatedExternalIdentifierEntity> extIds = orgDisambiguatedExaternalIdentifierEntityCaptor.getAllValues();
        assertEquals(8, extIds.size());
        found1 = found2 = found3 = found4 = false;
        boolean found5 = false, found6 = false, found7 = false, found8 = false;
        for (OrgDisambiguatedExternalIdentifierEntity entity : extIds) {
            switch (entity.getIdentifier()) {
            case "1":
                assertEquals("1", entity.getOrgDisambiguated().getSourceId());
                assertEquals("XXX1", entity.getIdentifierType());
                found1 = true;
                break;
            case "2":
                assertEquals("1", entity.getOrgDisambiguated().getSourceId());
                assertEquals("XXX2", entity.getIdentifierType());
                found2 = true;
                break;
            case "3":
                assertEquals("2", entity.getOrgDisambiguated().getSourceId());
                assertEquals("XXX3", entity.getIdentifierType());
                found3 = true;
                break;
            case "4":
                assertEquals("2", entity.getOrgDisambiguated().getSourceId());
                assertEquals("XXX4", entity.getIdentifierType());
                found4 = true;
                break;
            case "5":
                assertEquals("3", entity.getOrgDisambiguated().getSourceId());
                assertEquals("XXX5", entity.getIdentifierType());
                found5 = true;
                break;
            case "6":
                assertEquals("3", entity.getOrgDisambiguated().getSourceId());
                assertEquals("XXX6", entity.getIdentifierType());
                found6 = true;
                break;
            case "7":
                assertEquals("4", entity.getOrgDisambiguated().getSourceId());
                assertEquals("XXX7", entity.getIdentifierType());
                found7 = true;
                break;
            case "8":
                assertEquals("4", entity.getOrgDisambiguated().getSourceId());
                assertEquals("XXX8", entity.getIdentifierType());
                found8 = true;
                break;
            default:
                fail("Invalid ext id found: " + entity.getIdentifier());
                break;
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);
        assertTrue(found6);
        assertTrue(found7);
        assertTrue(found8);

        ArgumentCaptor<OrgEntity> orgEntityCaptor = ArgumentCaptor.forClass(OrgEntity.class);
        verify(mockOrgDao, times(2)).persist(orgEntityCaptor.capture());
        List<OrgEntity> orgs = orgEntityCaptor.getAllValues();
        assertEquals(2, orgs.size());
        found1 = found2 = false;
        for (OrgEntity entity : orgs) {
            switch (entity.getName()) {
            case "1. Alt Name":
                assertEquals("1", entity.getOrgDisambiguated().getSourceId());
                assertEquals("AltCity#1", entity.getCity());
                assertEquals(org.orcid.jaxb.model.message.Iso3166Country.MX, entity.getCountry());
                found1 = true;
                break;
            case "2. Alt Name":
                assertEquals("2", entity.getOrgDisambiguated().getSourceId());
                assertEquals("AltCity#2", entity.getCity());
                assertEquals(org.orcid.jaxb.model.message.Iso3166Country.BR, entity.getCountry());
                found2 = true;
                break;
            default:
                fail("Invalid org found: " + entity.getName());
                break;
            }
        }
        assertTrue(found1);
        assertTrue(found2);
    }

    @Test
    public void test_UpdateOneDisambiguatedOrg_AddAnOrg_AddAnExtId() {
        fail();
    }

    @Test
    public void test_DeprecateADisambiguatedOrg() {
        fail();
    }
    
    @Test
    public void test_DoNothing() {
        fail();
    }
}
