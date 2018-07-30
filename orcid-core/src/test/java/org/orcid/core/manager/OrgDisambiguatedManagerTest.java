package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.test.TargetProxyHelper;

public class OrgDisambiguatedManagerTest extends BaseTest {
    @Resource
    private OrgDisambiguatedManager orgDisambiguatedManager;

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDaoReadOnly;

    @Mock
    private OrgDisambiguatedDao mockOrgDisambiguatedDaoReadOnly;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(orgDisambiguatedManager, "orgDisambiguatedDaoReadOnly", mockOrgDisambiguatedDaoReadOnly);
        when(mockOrgDisambiguatedDaoReadOnly.find(1L)).thenReturn(getOrgDisambiguatedEntity(true));
        when(mockOrgDisambiguatedDaoReadOnly.findBySourceIdAndSourceType("sourceId","sourceType")).thenReturn(getOrgDisambiguatedEntity(true));
        when(mockOrgDisambiguatedDaoReadOnly.find(2L)).thenReturn(getOrgDisambiguatedEntity(false));
    }

    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(orgDisambiguatedManager, "orgDisambiguatedDaoReadOnly", orgDisambiguatedDaoReadOnly);
    }

    @Test
    public void findInDBTest() {
        OrgDisambiguated org = orgDisambiguatedManager.findInDB(1L);
        assertOrgValues(org);
        assertNotNull(org.getOrgDisambiguatedExternalIdentifiers());
        assertEquals(3, org.getOrgDisambiguatedExternalIdentifiers().size());

        assertEquals("type1", org.getOrgDisambiguatedExternalIdentifiers().get(0).getIdentifierType());
        assertEquals("type1_identifier1", org.getOrgDisambiguatedExternalIdentifiers().get(0).getPreferred());
        assertEquals(3, org.getOrgDisambiguatedExternalIdentifiers().get(0).getAll().size());
        String[] extIds = (String[]) org.getOrgDisambiguatedExternalIdentifiers().get(0).getAll().toArray(new String[3]);
        assertEquals("type1_identifier1", extIds[0]);
        assertEquals("type1_identifier2", extIds[1]);
        assertEquals("type1_identifier3", extIds[2]);

        assertEquals("type2", org.getOrgDisambiguatedExternalIdentifiers().get(1).getIdentifierType());
        assertEquals("type2_identifier2", org.getOrgDisambiguatedExternalIdentifiers().get(1).getPreferred());
        assertEquals(3, org.getOrgDisambiguatedExternalIdentifiers().get(1).getAll().size());
        extIds = (String[]) org.getOrgDisambiguatedExternalIdentifiers().get(1).getAll().toArray(new String[3]);
        assertEquals("type2_identifier1", extIds[0]);
        assertEquals("type2_identifier2", extIds[1]);
        assertEquals("type2_identifier3", extIds[2]);

        assertEquals("type3", org.getOrgDisambiguatedExternalIdentifiers().get(2).getIdentifierType());
        assertEquals("type3_identifier3", org.getOrgDisambiguatedExternalIdentifiers().get(2).getPreferred());
        assertEquals(3, org.getOrgDisambiguatedExternalIdentifiers().get(2).getAll().size());
        extIds = (String[]) org.getOrgDisambiguatedExternalIdentifiers().get(2).getAll().toArray(new String[3]);
        assertEquals("type3_identifier1", extIds[0]);
        assertEquals("type3_identifier2", extIds[1]);
        assertEquals("type3_identifier3", extIds[2]);
    }
    
    @Test
    public void findByTypeAndValueTest(){
        //only tests mocks!
        //1l {sourceId=sourceId, country=US, orgType=orgType, countryForDisplay=org.orcid.persistence.jpa.entities.CountryIsoEntity.US, disambiguatedAffiliationIdentifier=null, city=city, sourceType=sourceType, region=region, value=name, url=url}
        OrgDisambiguated org = orgDisambiguatedManager.findInDB("sourceId","sourceType");
        assertEquals("sourceId", org.getSourceId());
        assertEquals("sourceType", org.getSourceType());
        assertEquals("city", org.getCity());
        assertEquals("US", org.getCountry());
        assertEquals("org.orcid.persistence.jpa.entities.CountryIsoEntity.US", org.getCountryForDisplay());
        assertEquals("orgType", org.getOrgType());
        assertEquals("region", org.getRegion());
        assertEquals("url", org.getUrl());
        assertEquals("name", org.getValue());
        
        assertEquals("type1", org.getOrgDisambiguatedExternalIdentifiers().get(0).getIdentifierType());
        assertEquals("type1_identifier1", org.getOrgDisambiguatedExternalIdentifiers().get(0).getPreferred());
        assertEquals(3, org.getOrgDisambiguatedExternalIdentifiers().get(0).getAll().size());
        String[] extIds = (String[]) org.getOrgDisambiguatedExternalIdentifiers().get(0).getAll().toArray(new String[3]);
        assertEquals("type1_identifier1", extIds[0]);
        assertEquals("type1_identifier2", extIds[1]);
        assertEquals("type1_identifier3", extIds[2]);

        assertEquals("type2", org.getOrgDisambiguatedExternalIdentifiers().get(1).getIdentifierType());
        assertEquals("type2_identifier2", org.getOrgDisambiguatedExternalIdentifiers().get(1).getPreferred());
        assertEquals(3, org.getOrgDisambiguatedExternalIdentifiers().get(1).getAll().size());
        extIds = (String[]) org.getOrgDisambiguatedExternalIdentifiers().get(1).getAll().toArray(new String[3]);
        assertEquals("type2_identifier1", extIds[0]);
        assertEquals("type2_identifier2", extIds[1]);
        assertEquals("type2_identifier3", extIds[2]);

        assertEquals("type3", org.getOrgDisambiguatedExternalIdentifiers().get(2).getIdentifierType());
        assertEquals("type3_identifier3", org.getOrgDisambiguatedExternalIdentifiers().get(2).getPreferred());
        assertEquals(3, org.getOrgDisambiguatedExternalIdentifiers().get(2).getAll().size());
        extIds = (String[]) org.getOrgDisambiguatedExternalIdentifiers().get(2).getAll().toArray(new String[3]);
        assertEquals("type3_identifier1", extIds[0]);
        assertEquals("type3_identifier2", extIds[1]);
        assertEquals("type3_identifier3", extIds[2]);        
    }

    @Test
    public void findInDB_NoOrgDisambiguatedExternalIdentifiersFoundTest() {
        OrgDisambiguated org = orgDisambiguatedManager.findInDB(2L);
        assertOrgValues(org);
        assertNull(org.getOrgDisambiguatedExternalIdentifiers());
    }

    private void assertOrgValues(OrgDisambiguated org) {
        assertNotNull(org);
        assertEquals("city", org.getCity());
        assertEquals("US", org.getCountry());
        assertEquals("name", org.getValue());
        assertEquals("orgType", org.getOrgType());
        assertEquals("region", org.getRegion());
        assertEquals("sourceId", org.getSourceId());
        assertEquals("sourceType", org.getSourceType());
        assertEquals("url", org.getUrl());
    }

    private OrgDisambiguatedEntity getOrgDisambiguatedEntity(Boolean withExtIds) {
        OrgDisambiguatedEntity org = new OrgDisambiguatedEntity();
        org.setCity("city");
        org.setCountry(Iso3166Country.US.name());
        org.setName("name");
        org.setOrgType("orgType");
        org.setRegion("region");
        org.setSourceId("sourceId");
        org.setSourceType("sourceType");
        org.setUrl("url");

        if (withExtIds) {
            Set<OrgDisambiguatedExternalIdentifierEntity> orgDisambiguatedExternalIdentifiers = new HashSet<OrgDisambiguatedExternalIdentifierEntity>();
            orgDisambiguatedExternalIdentifiers.add(getOrgDisambiguatedExternalIdentifierEntity("type1", "type1_identifier1", true));
            orgDisambiguatedExternalIdentifiers.add(getOrgDisambiguatedExternalIdentifierEntity("type1", "type1_identifier2", false));
            orgDisambiguatedExternalIdentifiers.add(getOrgDisambiguatedExternalIdentifierEntity("type1", "type1_identifier3", false));

            orgDisambiguatedExternalIdentifiers.add(getOrgDisambiguatedExternalIdentifierEntity("type2", "type2_identifier1", false));
            orgDisambiguatedExternalIdentifiers.add(getOrgDisambiguatedExternalIdentifierEntity("type2", "type2_identifier2", true));
            orgDisambiguatedExternalIdentifiers.add(getOrgDisambiguatedExternalIdentifierEntity("type2", "type2_identifier3", false));

            orgDisambiguatedExternalIdentifiers.add(getOrgDisambiguatedExternalIdentifierEntity("type3", "type3_identifier1", false));
            orgDisambiguatedExternalIdentifiers.add(getOrgDisambiguatedExternalIdentifierEntity("type3", "type3_identifier2", false));
            orgDisambiguatedExternalIdentifiers.add(getOrgDisambiguatedExternalIdentifierEntity("type3", "type3_identifier3", true));

            org.setExternalIdentifiers(orgDisambiguatedExternalIdentifiers);
        }
        return org;
    }

    private OrgDisambiguatedExternalIdentifierEntity getOrgDisambiguatedExternalIdentifierEntity(String identifierType, String identifier, Boolean preferred) {
        OrgDisambiguatedExternalIdentifierEntity entity = new OrgDisambiguatedExternalIdentifierEntity();
        entity.setIdentifierType(identifierType);
        entity.setIdentifier(identifier);
        entity.setPreferred(preferred);
        return entity;
    }
}
