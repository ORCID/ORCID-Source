package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.orcid.core.BaseTest;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.ResearchResourceDao;
import org.orcid.persistence.dao.impl.OrgDaoImpl;
import org.orcid.persistence.jpa.entities.AmbiguousOrgEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrgManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
            "/data/OrgsEntityData.xml");

    @Resource(name = "orgManagerV3")
    private OrgManager orgManager;

    @Resource
    private OrgDao orgDao;

    @Mock
    private ProfileFundingDao profileFundingDao;

    @Mock
    private OrgAffiliationRelationDao orgAffiliationRelationDao;

    @Mock
    private ResearchResourceDao researchResourceDao;

    @Mock
    private PeerReviewDao peerReviewDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(orgManager, "profileFundingDao", profileFundingDao);
        ReflectionTestUtils.setField(orgManager, "orgAffiliationRelationDao", orgAffiliationRelationDao);
        ReflectionTestUtils.setField(orgManager, "researchResourceDao", researchResourceDao);
        ReflectionTestUtils.setField(orgManager, "peerReviewDao", peerReviewDao);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test
    public void getAmbiguousOrgs() {
        List<AmbiguousOrgEntity> orgs = orgManager.getAmbiguousOrgs(0, Integer.MAX_VALUE);
        assertNotNull(orgs);
        assertEquals(2, orgs.size());
    }

    @Test
    public void testWriteAmbiguousOrgs() throws IOException {
        StringWriter writer = new StringWriter();

        orgManager.writeAmbiguousOrgs(writer);
        String result = writer.toString();

        String expected = IOUtils.toString(getClass().getResource("/org/orcid/core/manager/expected_ambiguous_orgs.csv"));
        assertEquals(expected, result);
    }

    @Test
    public void testGetOrgs() {
        List<OrgEntity> orgs = orgManager.getOrgs("an", 0, 10);
        assertNotNull(orgs);
        assertEquals(2, orgs.size());
        assertEquals("An Institution", orgs.get(0).resolveName());
        assertEquals("Another Institution", orgs.get(1).resolveName());
    }

    @Test
    public void testUpdateDisambiguatedOrgReferencesNoReferencesToNewOrg() {
        OrgDao mockOrgDao = Mockito.mock(OrgDaoImpl.class);
        ReflectionTestUtils.setField(orgManager, "orgDao", mockOrgDao);

        Long deprecatedOrg = 1L;
        Long replacementOrg = 2L;
        Mockito.when(mockOrgDao.findByOrgDisambiguatedId(Mockito.eq(deprecatedOrg))).thenReturn(getListOfOrgs(deprecatedOrg));
        Mockito.when(mockOrgDao.findByOrgDisambiguatedId(Mockito.eq(replacementOrg))).thenReturn(new ArrayList<>());

        orgManager.updateDisambiguatedOrgReferences(deprecatedOrg, replacementOrg);

        // all orgs should be updated in one query as no constraint violation
        // complications
        Mockito.verify(mockOrgDao, Mockito.times(1)).updateOrgDisambiguatedId(deprecatedOrg, replacementOrg);

        ReflectionTestUtils.setField(orgManager, "orgDao", orgDao);
    }

    @Test
    public void testUpdateDisambiguatedOrgReferencesSomeReferencesToNewOrg() {
        OrgDao mockOrgDao = Mockito.mock(OrgDaoImpl.class);
        ReflectionTestUtils.setField(orgManager, "orgDao", mockOrgDao);

        Long deprecatedOrg = 1L;
        Long replacementOrg = 2L;

        List<OrgEntity> referencingDeprecated = getListOfOrgs(deprecatedOrg);

        Mockito.when(mockOrgDao.findByOrgDisambiguatedId(Mockito.eq(deprecatedOrg))).thenReturn(referencingDeprecated);
        Mockito.when(mockOrgDao.findByOrgDisambiguatedId(Mockito.eq(replacementOrg))).thenReturn(getListOfOrgs(replacementOrg));

        Mockito.doNothing().when(mockOrgDao).remove(Mockito.anyLong());
        Mockito.when(mockOrgDao.merge(Mockito.any(OrgEntity.class))).thenReturn(null);

        Mockito.when(researchResourceDao.getResearchResourcesReferencingOrgs(Mockito.anyList())).thenReturn(getListOfResearchResourceIds());
        Mockito.when(researchResourceDao.find(Mockito.eq(0L))).thenReturn(getResearchResource(0));
        Mockito.when(researchResourceDao.find(Mockito.eq(1L))).thenReturn(getResearchResource(1));
        Mockito.when(researchResourceDao.find(Mockito.eq(2L))).thenReturn(getResearchResource(2));
        Mockito.when(researchResourceDao.find(Mockito.eq(3L))).thenReturn(getResearchResource(3));
        Mockito.when(researchResourceDao.find(Mockito.eq(4L))).thenReturn(getResearchResource(4));
        Mockito.when(profileFundingDao.getFundingsReferencingOrgs(Mockito.anyList())).thenReturn(getListOfProfileFundings());
        Mockito.when(peerReviewDao.getPeerReviewsReferencingOrgs(Mockito.anyList())).thenReturn(getListOfPeerReviews());
        Mockito.when(orgAffiliationRelationDao.getOrgAffiliationRelationsReferencingOrgs(Mockito.anyList())).thenReturn(getListOfOrgAffiliationRelations());

        // referencingDeprecated list of orgs should be examined individually to
        // check for potential duplicates
        for (int i = 0; i < referencingDeprecated.size(); i++) {
            OrgEntity entity = referencingDeprecated.get(i);
            OrgEntity org = getOrgEntity(entity.getId().intValue(), replacementOrg);

            // make sure not everything has a potential duplicate
            if (i == 1 || i == 4) {
                org = null;
            }

            Mockito.when(mockOrgDao.findByAddressAndDisambiguatedOrg(Mockito.eq(entity.getName()), Mockito.eq(entity.getCity()), Mockito.eq(entity.getRegion()),
                    Mockito.eq(entity.getCountry()), Mockito.any(OrgDisambiguatedEntity.class))).thenReturn(org);
        }

        orgManager.updateDisambiguatedOrgReferences(deprecatedOrg, replacementOrg);

        Mockito.verify(mockOrgDao, Mockito.times(8)).remove(Mockito.anyLong()); // where
                                                                                // duplicate
                                                                                // found
        Mockito.verify(mockOrgDao, Mockito.times(2)).merge(Mockito.any(OrgEntity.class)); // no
                                                                                          // duplicate
                                                                                          // found

        Mockito.verify(researchResourceDao, Mockito.times(5 * 8)).merge(Mockito.any(ResearchResourceEntity.class));
        Mockito.verify(profileFundingDao, Mockito.times(5 * 8)).merge(Mockito.any(ProfileFundingEntity.class));
        Mockito.verify(peerReviewDao, Mockito.times(5 * 8)).merge(Mockito.any(PeerReviewEntity.class));
        Mockito.verify(orgAffiliationRelationDao, Mockito.times(5 * 8)).merge(Mockito.any(OrgAffiliationRelationEntity.class));

        ReflectionTestUtils.setField(orgManager, "orgDao", orgDao);
    }

    private ResearchResourceEntity getResearchResource(int i) {
        ResearchResourceEntity entity = new ResearchResourceEntity();
        entity.setHosts(new ArrayList<>());
        entity.setResourceItems(new ArrayList<>());
        entity.setId(Long.valueOf(i));
        return entity;
    }

    private List<BigInteger> getListOfResearchResourceIds() {
        List<BigInteger> ids = new ArrayList<>();
        for (long i = 0; i < 5; i++) {
            ids.add(BigInteger.valueOf(i));
        }
        return ids;
    }

    private List<PeerReviewEntity> getListOfPeerReviews() {
        List<PeerReviewEntity> entities = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            PeerReviewEntity entity = new PeerReviewEntity();
            entity.setId(Long.valueOf(i));
            entities.add(entity);
        }
        return entities;
    }

    private List<ProfileFundingEntity> getListOfProfileFundings() {
        List<ProfileFundingEntity> entities = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ProfileFundingEntity entity = new ProfileFundingEntity();
            entity.setId(Long.valueOf(i));
            entities.add(entity);
        }
        return entities;
    }

    private List<OrgAffiliationRelationEntity> getListOfOrgAffiliationRelations() {
        List<OrgAffiliationRelationEntity> entities = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            OrgAffiliationRelationEntity entity = new OrgAffiliationRelationEntity();
            entity.setId(Long.valueOf(i));
            entities.add(entity);
        }
        return entities;
    }

    private List<OrgEntity> getListOfOrgs(Long orgDisambiguatedId) {
        List<OrgEntity> orgs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            orgs.add(getOrgEntity(i, orgDisambiguatedId));
        }
        return orgs;
    }

    private OrgEntity getOrgEntity(int identifier, Long orgDisambiguatedId) {
        OrgEntity org = new OrgEntity();
        org.setId(Long.valueOf(identifier));
        org.setCity("city" + identifier);
        org.setName("name" + identifier);
        org.setRegion("region" + identifier);
        org.setCountry("country" + identifier);

        OrgDisambiguatedEntity orgDisambiguated = new OrgDisambiguatedEntity();
        orgDisambiguated.setId(orgDisambiguatedId);
        org.setOrgDisambiguated(orgDisambiguated);

        return org;
    }

}
