package org.orcid.core.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.adapter.impl.Jpa2JaxbAdapterImpl;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.WorkEntityCacheManager;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.OrcidIdBase;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.BiographyEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class Jpa2JaxbAdapterTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/BiographyEntityData.xml", "/data/RecordNameEntityData.xml", "/data/WorksEntityData.xml", "/data/OrgsEntityData.xml",
            "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewEntityData.xml", "/data/ProfileFundingEntityData.xml");
    private final String userOrcid = "0000-0000-0000-0003";

    @Resource
    private ProfileDao profileDao;

    @Resource
    private Jpa2JaxbAdapter adapter;

    @Resource(name = "workEntityCacheManager")
    private WorkEntityCacheManager realWorkEntityCacheManager;

    @Mock
    private WorkEntityCacheManager mockWorkEntityCacheManager;

    Jpa2JaxbAdapterImpl jpa2JaxbAdapterImpl;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);
        jpa2JaxbAdapterImpl = TargetProxyHelper.getTargetObject(adapter, Jpa2JaxbAdapterImpl.class);
        jpa2JaxbAdapterImpl.setWorkEntityCacheManager(mockWorkEntityCacheManager);
    }

    @After
    public void replaceMocks() {
        jpa2JaxbAdapterImpl.setWorkEntityCacheManager(realWorkEntityCacheManager);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }

    @Test
    @Transactional
    public void checkSourceOnAllElements() {
        ProfileEntity profileEntity = profileDao.find(userOrcid);
        assertNotNull(profileEntity);
        assertEquals(userOrcid, profileEntity.getId());
        OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity, LoadOptions.ALL);
        assertNotNull(orcidProfile);
        testOrcidIdentifier(orcidProfile.getOrcidIdentifier());
        assertNotNull(orcidProfile.getOrcidActivities());

        // Check works
        OrcidWorks orcidWorks = orcidProfile.getOrcidActivities().getOrcidWorks();
        if (orcidWorks != null && orcidWorks.getOrcidWork() != null && !orcidWorks.getOrcidWork().isEmpty()) {
            for (OrcidWork work : orcidWorks.getOrcidWork()) {
                checkSource(work.getSource(), null);
            }
        }

        // Check affiliations
        Affiliations affiliations = orcidProfile.getOrcidActivities().getAffiliations();
        if (affiliations != null && affiliations.getAffiliation() != null && !affiliations.getAffiliation().isEmpty()) {
            for (Affiliation affiliation : affiliations.getAffiliation()) {
                checkSource(affiliation.getSource(), null);
            }
        }

        // Check fundings
        FundingList fundings = orcidProfile.getOrcidActivities().getFundings();
        if (fundings != null && fundings.getFundings() != null && !fundings.getFundings().isEmpty()) {
            for (Funding funding : fundings.getFundings()) {
                checkSource(funding.getSource(), null);
            }
        }

        assertNotNull(orcidProfile.getOrcidBio());

        // Check external identifiers
        ExternalIdentifiers extIds = orcidProfile.getOrcidBio().getExternalIdentifiers();
        if (extIds != null && extIds.getExternalIdentifier() != null && !extIds.getExternalIdentifier().isEmpty()) {
            for (ExternalIdentifier extId : extIds.getExternalIdentifier()) {
                checkSource(extId.getSource(), null);
            }
        }
    }

    @Test
    public void testGetOrcidIdBase() {
        // Check client
        OrcidIdBase base = adapter.getOrcidIdBase("APP-0000000000000001");
        assertNotNull(base);
        assertNotNull(base.getHost());
        assertNotNull(base.getPath());
        assertNotNull(base.getUri());
        assertEquals("http://orcid.org/client/APP-0000000000000001", base.getUri());
        assertEquals("orcid.org", base.getHost());
        assertEquals("APP-0000000000000001", base.getPath());

        // Check user
        base = adapter.getOrcidIdBase("0000-0000-0000-0000");
        assertNotNull(base);
        assertNotNull(base.getHost());
        assertNotNull(base.getPath());
        assertNotNull(base.getUri());
        assertEquals("http://orcid.org/0000-0000-0000-0000", base.getUri());
        assertEquals("orcid.org", base.getHost());
        assertEquals("0000-0000-0000-0000", base.getPath());
    }

    @SuppressWarnings("deprecation")
    private void checkSource(Source source, String sourceId) {
        assertNotNull(source);
        String sourcePath = PojoUtil.isEmpty(sourceId) ? source.retrieveSourcePath() : sourceId;
        assertNotNull(sourcePath);
        source.getSourceDate();
        if (OrcidStringUtils.isValidOrcid(source.retrieveSourcePath())) {
            assertNotNull(source.getSourceOrcid());
            assertNotNull(source.getSourceOrcid().getHost());
            assertNotNull(source.getSourceOrcid().getPath());
            assertNotNull(source.getSourceOrcid().getUri());
            assertEquals("http://orcid.org/" + sourcePath, source.getSourceOrcid().getUri());
            assertEquals("orcid.org", source.getSourceOrcid().getHost());
            assertEquals(sourcePath, source.getSourceOrcid().getPath());
        } else {
            assertNotNull(source.getSourceClientId());
            assertNotNull(source.getSourceClientId().getHost());
            assertNotNull(source.getSourceClientId().getPath());
            assertNotNull(source.getSourceClientId().getUri());
            assertEquals("http://orcid.org/client/" + sourcePath, source.getSourceClientId().getUri());
            assertEquals("orcid.org", source.getSourceClientId().getHost());
            assertEquals(sourcePath, source.getSourceClientId().getPath());
        }
    }

    private void testOrcidIdentifier(OrcidIdentifier identifier) {
        assertNotNull(identifier);
        assertFalse(PojoUtil.isEmpty(identifier.getHost()));
        assertFalse(PojoUtil.isEmpty(identifier.getPath()));
        assertFalse(PojoUtil.isEmpty(identifier.getUri()));
        assertEquals("http://orcid.org/" + userOrcid, identifier.getUri());
        assertEquals("orcid.org", identifier.getHost());
        assertEquals(userOrcid, identifier.getPath());
    }

    @Test
    public void testProfileEntityToOrcidMessage() {
        String userOrcid = "0000-0000-0000-1234";
        String clientId = "APP-5555555555555555";
        ProfileEntity profile = new ProfileEntity(userOrcid);

        // Set default visibility
        profile.setActivitiesVisibilityDefault(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);

        // Set name
        RecordNameEntity name = new RecordNameEntity();
        name.setCreditName("My credit name");
        name.setFamilyName("My family name");
        name.setGivenNames("My given names");
        name.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        profile.setRecordNameEntity(name);

        // Set biography
        BiographyEntity bio = new BiographyEntity();
        bio.setBiography("This is my biography");
        bio.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        profile.setBiographyEntity(bio);

        // Set other names
        TreeSet<OtherNameEntity> otherNames = new TreeSet<OtherNameEntity>();
        OtherNameEntity otherName = new OtherNameEntity();
        otherName.setDisplayName("My other name");
        otherName.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        otherName.setDisplayIndex(20000L);
        otherName.setClientSourceId(clientId);
        otherName.setId(24816L);
        otherNames.add(otherName);
        profile.setOtherNames(otherNames);

        // Set address
        Set<AddressEntity> addresses = new HashSet<AddressEntity>();
        AddressEntity address = new AddressEntity();
        address.setIso2Country(Iso3166Country.US);
        address.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        address.setDisplayIndex(20000L);
        address.setClientSourceId(clientId);
        address.setId(24816L);
        addresses.add(address);
        profile.setAddresses(addresses);

        // Set keywords
        TreeSet<ProfileKeywordEntity> keywords = new TreeSet<ProfileKeywordEntity>();
        ProfileKeywordEntity keyword = new ProfileKeywordEntity();
        keyword.setKeywordName("My keyword");
        keyword.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        keyword.setDisplayIndex(20000L);
        keyword.setClientSourceId(clientId);
        keyword.setId(24816L);
        keywords.add(keyword);
        profile.setKeywords(keywords);

        // Set researcher urls
        TreeSet<ResearcherUrlEntity> rUrls = new TreeSet<ResearcherUrlEntity>();
        ResearcherUrlEntity rUrl = new ResearcherUrlEntity();
        rUrl.setUrl("http://orcid.org");
        rUrl.setUrlName("My rUrl");
        rUrl.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        rUrl.setDisplayIndex(20000L);
        rUrl.setClientSourceId(clientId);
        rUrl.setId(24816L);
        rUrls.add(rUrl);
        profile.setResearcherUrls(rUrls);

        // Set external identifiers
        Set<ExternalIdentifierEntity> extIds = new HashSet<ExternalIdentifierEntity>();
        ExternalIdentifierEntity extId = new ExternalIdentifierEntity();
        extId.setExternalIdCommonName("My common name");
        extId.setExternalIdReference("My refrence");
        extId.setExternalIdUrl("http://orcid.org");
        extId.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        extId.setDisplayIndex(20000L);
        extId.setClientSourceId(clientId);
        extId.setId(24816L);
        extIds.add(extId);
        profile.setExternalIdentifiers(extIds);

        // Set works
        TreeSet<WorkEntity> works = new TreeSet<WorkEntity>();
        WorkEntity work = new WorkEntity();
        work.setWorkType(WorkType.OTHER);
        work.setTitle("My work title");
        work.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        work.setDisplayIndex(20000L);
        work.setClientSourceId(clientId);
        work.setId(24816L);
        work.setOrcid(profile.getId());
        works.add(work);
        
        when(mockWorkEntityCacheManager.retrieveFullWorks(userOrcid, 0)).thenReturn(new ArrayList<>(works));

        // Existing org
        OrgEntity newOrg = new OrgEntity();
        newOrg.setId(1234L);
        newOrg.setCity("San Jose");
        newOrg.setCountry(org.orcid.jaxb.model.message.Iso3166Country.CR);
        newOrg.setName("My org name");

        // Set funding
        TreeSet<ProfileFundingEntity> fundings = new TreeSet<ProfileFundingEntity>();
        ProfileFundingEntity funding = new ProfileFundingEntity();
        funding.setOrg(newOrg);
        funding.setTitle("My funding title");
        funding.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        funding.setDisplayIndex(20000L);
        funding.setClientSourceId(clientId);
        funding.setId(24816L);
        fundings.add(funding);
        profile.setProfileFunding(fundings);

        // Set affiliations
        TreeSet<OrgAffiliationRelationEntity> affiliations = new TreeSet<OrgAffiliationRelationEntity>();
        OrgAffiliationRelationEntity affiliation = new OrgAffiliationRelationEntity();
        affiliation.setAffiliationType(org.orcid.jaxb.model.v3.dev1.record.AffiliationType.EDUCATION);
        affiliation.setOrg(newOrg);
        affiliation.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        affiliation.setClientSourceId(clientId);
        affiliation.setId(24816L);
        affiliations.add(affiliation);
        profile.setOrgAffiliationRelations(affiliations);

        OrcidProfile orcidProfile = adapter.toOrcidProfile(profile, LoadOptions.ALL);
        // Check profile
        assertNotNull(orcidProfile);
        assertNotNull(orcidProfile.getOrcidIdentifier());
        assertEquals("http://orcid.org/" + userOrcid, orcidProfile.getOrcidIdentifier().getUri());
        assertEquals("orcid.org", orcidProfile.getOrcidIdentifier().getHost());
        assertEquals(userOrcid, orcidProfile.getOrcidIdentifier().getPath());

        // Check activities
        assertNotNull(orcidProfile.getOrcidActivities());

        // Check works
        assertNotNull(orcidProfile.getOrcidActivities().getOrcidWorks());
        assertNotNull(orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(1, orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().size());
        assertEquals("My work title", orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkTitle().getTitle().getContent());
        assertEquals("24816", orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getPutCode());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getVisibility());
        checkSource(orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getSource(), clientId);

        // Check fundings
        assertNotNull(orcidProfile.getOrcidActivities().getFundings());
        assertNotNull(orcidProfile.getOrcidActivities().getFundings().getFundings());
        assertEquals(1, orcidProfile.getOrcidActivities().getFundings().getFundings().size());
        assertEquals("My funding title", orcidProfile.getOrcidActivities().getFundings().getFundings().get(0).getTitle().getTitle().getContent());
        assertEquals("24816", orcidProfile.getOrcidActivities().getFundings().getFundings().get(0).getPutCode());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, orcidProfile.getOrcidActivities().getFundings().getFundings().get(0).getVisibility());
        checkSource(orcidProfile.getOrcidActivities().getFundings().getFundings().get(0).getSource(), clientId);

        // Check affiliations
        assertNotNull(orcidProfile.getOrcidActivities().getAffiliations());
        assertNotNull(orcidProfile.getOrcidActivities().getAffiliations().getAffiliation());
        assertEquals(1, orcidProfile.getOrcidActivities().getAffiliations().getAffiliation().size());
        assertEquals(AffiliationType.EDUCATION, orcidProfile.getOrcidActivities().getAffiliations().getAffiliation().get(0).getType());
        assertEquals("My org name", orcidProfile.getOrcidActivities().getAffiliations().getAffiliation().get(0).getOrganization().getName());
        assertEquals("San Jose", orcidProfile.getOrcidActivities().getAffiliations().getAffiliation().get(0).getOrganization().getAddress().getCity());
        assertEquals(org.orcid.jaxb.model.message.Iso3166Country.CR,
                orcidProfile.getOrcidActivities().getAffiliations().getAffiliation().get(0).getOrganization().getAddress().getCountry());
        assertEquals("24816", orcidProfile.getOrcidActivities().getAffiliations().getAffiliation().get(0).getPutCode());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, orcidProfile.getOrcidActivities().getAffiliations().getAffiliation().get(0).getVisibility());
        checkSource(orcidProfile.getOrcidActivities().getAffiliations().getAffiliation().get(0).getSource(), clientId);

        // Check biography
        assertNotNull(orcidProfile.getOrcidBio());

        // Check name
        assertNotNull(orcidProfile.getOrcidBio().getPersonalDetails());
        assertNotNull(orcidProfile.getOrcidBio().getPersonalDetails().getCreditName());
        assertEquals("My credit name", orcidProfile.getOrcidBio().getPersonalDetails().getCreditName().getContent());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, orcidProfile.getOrcidBio().getPersonalDetails().getCreditName().getVisibility());

        assertNotNull(orcidProfile.getOrcidBio().getPersonalDetails().getGivenNames());
        assertEquals("My given names", orcidProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, orcidProfile.getOrcidBio().getPersonalDetails().getGivenNames().getVisibility());

        assertNotNull(orcidProfile.getOrcidBio().getPersonalDetails().getFamilyName());
        assertEquals("My family name", orcidProfile.getOrcidBio().getPersonalDetails().getFamilyName().getContent());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, orcidProfile.getOrcidBio().getPersonalDetails().getFamilyName().getVisibility());

        // Check other names
        assertNotNull(orcidProfile.getOrcidBio().getPersonalDetails().getOtherNames());
        assertNotNull(orcidProfile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, orcidProfile.getOrcidBio().getPersonalDetails().getOtherNames().getVisibility());
        assertEquals(1, orcidProfile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().size());

        // Check biography
        assertNotNull(orcidProfile.getOrcidBio().getBiography());
        assertEquals("This is my biography", orcidProfile.getOrcidBio().getBiography().getContent());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, orcidProfile.getOrcidBio().getBiography().getVisibility());

        // Check address
        assertNotNull(orcidProfile.getOrcidBio().getContactDetails());
        assertNotNull(orcidProfile.getOrcidBio().getContactDetails().getAddress());
        assertNotNull(orcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry());
        assertEquals(org.orcid.jaxb.model.message.Iso3166Country.US, orcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, orcidProfile.getOrcidBio().getContactDetails().getAddress().getCountry().getVisibility());

        // Check keywords
        assertNotNull(orcidProfile.getOrcidBio().getKeywords());
        assertNotNull(orcidProfile.getOrcidBio().getKeywords().getKeyword());
        assertEquals(1, orcidProfile.getOrcidBio().getKeywords().getKeyword().size());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, orcidProfile.getOrcidBio().getKeywords().getVisibility());
        assertEquals("My keyword", orcidProfile.getOrcidBio().getKeywords().getKeyword().get(0).getContent());

        // Check researcher urls
        assertNotNull(orcidProfile.getOrcidBio().getResearcherUrls());
        assertNotNull(orcidProfile.getOrcidBio().getResearcherUrls().getResearcherUrl());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, orcidProfile.getOrcidBio().getResearcherUrls().getVisibility());
        assertEquals(1, orcidProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("My rUrl", orcidProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrlName().getContent());
        assertEquals("http://orcid.org", orcidProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());

        // Check external identifiers
        assertNotNull(orcidProfile.getOrcidBio().getExternalIdentifiers());
        assertNotNull(orcidProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, orcidProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("My common name", orcidProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals("My refrence", orcidProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdReference().getContent());
        assertEquals("http://orcid.org", orcidProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdUrl().getValue());
        assertEquals(org.orcid.jaxb.model.message.Visibility.PUBLIC, orcidProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getVisibility());
        checkSource(orcidProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getSource(), clientId);
    }

}
