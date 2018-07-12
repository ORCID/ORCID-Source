package org.orcid.core.utils.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ActivityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.jaxb.model.v3.rc1.common.Contributor;
import org.orcid.jaxb.model.v3.rc1.common.ContributorEmail;
import org.orcid.jaxb.model.v3.rc1.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.rc1.common.CreditName;
import org.orcid.jaxb.model.v3.rc1.common.Title;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.jaxb.model.v3.rc1.record.FundingContributor;
import org.orcid.jaxb.model.v3.rc1.record.FundingContributors;
import org.orcid.jaxb.model.v3.rc1.record.FundingTitle;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkContributors;
import org.orcid.jaxb.model.v3.rc1.record.WorkTitle;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;

public class ContributorUtilsTest {
    
    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Mock
    private ActivityManager cacheManager;
    
    @Mock
    private ProfileEntityManager profileEntityManager;
    
    @Mock
    private RecordNameDao recordNameDao;

    @InjectMocks
    private ContributorUtils contributorUtils;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        contributorUtils.setCacheManager(cacheManager);
        contributorUtils.setProfileEntityCacheManager(profileEntityCacheManager);
        contributorUtils.setProfileEntityManager(profileEntityManager);
        contributorUtils.setRecordNameDao(recordNameDao);
    }
    
    @Test
    public void testFilterContributorPrivateDataForWorkWithPrivateName() {
        when(profileEntityManager.orcidExists(anyString())).thenReturn(true);
        when(recordNameDao.getRecordNames(any(List.class))).then(new Answer<List<RecordNameEntity>>(){

            @Override
            public List<RecordNameEntity> answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return getRecordNameEntities((List) args[0]);
            }
            
        });
        when(cacheManager.getPublicCreditName(any(RecordNameEntity.class))).thenReturn(null);        
        
        Work work = getWorkWithOrcidContributor();
        contributorUtils.filterContributorPrivateData(work);
        
        Contributor contributor = work.getWorkContributors().getContributor().get(0);
        assertNull(contributor.getContributorEmail());
        assertEquals("", contributor.getCreditName().getContent());
    }
    
    @Test
    public void testFilterContributorPrivateDataForWorkWithPublicName() {
        when(profileEntityManager.orcidExists(anyString())).thenReturn(true);
        when(cacheManager.getPublicCreditName(any(RecordNameEntity.class))).thenReturn("a public name");
        when(recordNameDao.getRecordNames(any(List.class))).then(new Answer<List<RecordNameEntity>>(){

            @Override
            public List<RecordNameEntity> answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return getRecordNameEntities((List) args[0]);
            }
            
        });
        
        Work work = getWorkWithOrcidContributor();
        contributorUtils.filterContributorPrivateData(work);
        
        Contributor contributor = work.getWorkContributors().getContributor().get(0);
        assertNull(contributor.getContributorEmail());
        assertEquals("a public name", contributor.getCreditName().getContent());
    }
    
    @Test
    public void testFilterContributorPrivateDataForWorkWithInvalidOrcidRecord() {
        when(profileEntityManager.orcidExists(anyString())).thenReturn(false);
        
        Work work = getWorkWithOrcidContributor();
        contributorUtils.filterContributorPrivateData(work);
        
        Contributor contributor = work.getWorkContributors().getContributor().get(0);
        assertNull(contributor.getContributorEmail());
        assertEquals("original credit name", contributor.getCreditName().getContent());
    }
    
    @Test
    public void testFilterContributorPrivateDataForWorkWithNoOrcidRecord() {
        Work work = getWorkWithContributorWithoutOrcid();
        contributorUtils.filterContributorPrivateData(work);
        
        Contributor contributor = work.getWorkContributors().getContributor().get(0);
        assertNull(contributor.getContributorEmail());
        assertEquals("original credit name", contributor.getCreditName().getContent());
    }
    
    @Test
    public void testFilterContributorPrivateDataForWorkWithoutContributors() {
        Work work = getWorkWithoutContributors();
        contributorUtils.filterContributorPrivateData(work);
        assertNotNull(work); // test no failures
        assertEquals(getWorkWithoutContributors(), work);
    }
    
    @Test
    public void testFilterContributorPrivateDataForFundingWithPrivateName() {
        when(profileEntityManager.orcidExists(anyString())).thenReturn(true);
        when(profileEntityCacheManager.retrieve(anyString())).thenReturn(new ProfileEntity());
        when(cacheManager.getPublicCreditName(any(ProfileEntity.class))).thenReturn(null);
        
        Funding funding = getFundingWithOrcidContributor();
        contributorUtils.filterContributorPrivateData(funding);
        
        FundingContributor contributor = funding.getContributors().getContributor().get(0);
        assertNull(contributor.getContributorEmail());
        assertEquals("", contributor.getCreditName().getContent());
    }
    
    @Test
    public void testFilterContributorPrivateDataForFundingWithPublicName() {
        when(profileEntityManager.orcidExists(anyString())).thenReturn(true);
        when(profileEntityCacheManager.retrieve(anyString())).thenReturn(new ProfileEntity());
        when(cacheManager.getPublicCreditName(any(ProfileEntity.class))).thenReturn("a public name");
        
        Funding funding = getFundingWithOrcidContributor();
        contributorUtils.filterContributorPrivateData(funding);
        
        FundingContributor contributor = funding.getContributors().getContributor().get(0);
        assertNull(contributor.getContributorEmail());
        assertEquals("a public name", contributor.getCreditName().getContent());
    }
    
    @Test
    public void testFilterContributorPrivateDataForFundingWithInvalidOrcidRecord() {
        when(profileEntityManager.orcidExists(anyString())).thenReturn(false);
        
        Funding funding = getFundingWithOrcidContributor();
        contributorUtils.filterContributorPrivateData(funding);
        
        FundingContributor contributor = funding.getContributors().getContributor().get(0);
        assertNull(contributor.getContributorEmail());
        assertEquals("original credit name", contributor.getCreditName().getContent());
    }
    
    @Test
    public void testFilterContributorPrivateDataForFundingWithNoOrcidRecord() {
        Funding funding = getFundingWithContributorWithoutOrcid();
        contributorUtils.filterContributorPrivateData(funding);
        
        FundingContributor contributor = funding.getContributors().getContributor().get(0);
        assertNull(contributor.getContributorEmail());
        assertEquals("original credit name", contributor.getCreditName().getContent());
    }
    
    @Test
    public void testFilterContributorPrivateDataForFundingWithoutContributors() {
        Funding funding = getFundingWithoutContributors();
        contributorUtils.filterContributorPrivateData(funding);
        assertNotNull(funding); // test no failures
    }

    private Work getWorkWithoutContributors() {
        Work work = new Work();
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("work without contributors"));
        work.setWorkTitle(workTitle);
        return work;
    }

    private Work getWorkWithContributorWithoutOrcid() {
        Work work = new Work();
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("work with contributor without ORCID record"));
        work.setWorkTitle(workTitle);
        work.setWorkContributors(getWorkContributorWithoutOrcid());
        return work;
    }

    private Work getWorkWithOrcidContributor() {
        Work work = new Work();
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("work with contributor who has ORCID record"));
        work.setWorkTitle(workTitle);
        work.setWorkContributors(getWorkContributorWithOrcid());
        return work;
    }

    private WorkContributors getWorkContributorWithOrcid() {
        ContributorOrcid contributorOrcid = new ContributorOrcid();
        contributorOrcid.setPath("0000-0003-4902-6327");
        contributorOrcid.setHost("orcid.org");
        contributorOrcid.setUri("http://orcid.org/0000-0003-4902-6327");

        Contributor contributor = new Contributor();
        contributor.setContributorOrcid(contributorOrcid);
        contributor.setContributorEmail(new ContributorEmail("never@show.this"));
        contributor.setCreditName(new CreditName("original credit name"));
        
        return new WorkContributors(Arrays.asList(contributor));
    }
    
    private WorkContributors getWorkContributorWithoutOrcid() {
        Contributor contributor = new Contributor();
        contributor.setContributorEmail(new ContributorEmail("never@show.this"));
        contributor.setCreditName(new CreditName("original credit name"));
        
        return new WorkContributors(Arrays.asList(contributor));
    }
    
    private Funding getFundingWithoutContributors() {
        Funding funding = new Funding();
        FundingTitle fundingTitle = new FundingTitle();
        fundingTitle.setTitle(new Title("funding without contributors"));
        funding.setTitle(fundingTitle);
        return funding;
    }

    private Funding getFundingWithContributorWithoutOrcid() {
        Funding funding = new Funding();
        FundingTitle fundingTitle = new FundingTitle();
        fundingTitle.setTitle(new Title("funding with contributor without ORCID record"));
        funding.setTitle(fundingTitle);
        funding.setContributors(getFundingContributorWithoutOrcid());
        return funding;
    }

    private Funding getFundingWithOrcidContributor() {
        Funding funding = new Funding();
        FundingTitle fundingTitle = new FundingTitle();
        fundingTitle.setTitle(new Title("work with contributor who has ORCID record"));
        funding.setTitle(fundingTitle);
        funding.setContributors(getFundingContributorWithOrcid());
        return funding;
    }

    private FundingContributors getFundingContributorWithOrcid() {
        ContributorOrcid contributorOrcid = new ContributorOrcid();
        contributorOrcid.setPath("0000-0003-4902-6327");
        contributorOrcid.setHost("orcid.org");
        contributorOrcid.setUri("http://orcid.org/0000-0003-4902-6327");

        FundingContributor contributor = new FundingContributor();
        contributor.setContributorOrcid(contributorOrcid);
        contributor.setContributorEmail(new ContributorEmail("never@show.this"));
        contributor.setCreditName(new CreditName("original credit name"));
        
        FundingContributors fundingContributors = new FundingContributors();
        fundingContributors.getContributor().add(contributor);
        return fundingContributors;
    }
    
    private FundingContributors getFundingContributorWithoutOrcid() {
        FundingContributor contributor = new FundingContributor();
        contributor.setContributorEmail(new ContributorEmail("never@show.this"));
        contributor.setCreditName(new CreditName("original credit name"));
        
        FundingContributors fundingContributors = new FundingContributors();
        fundingContributors.getContributor().add(contributor);
        return fundingContributors;
    }
    
    private List<RecordNameEntity> getRecordNameEntities(List<String> orcidIds){
        List<RecordNameEntity> records = new ArrayList<RecordNameEntity>();
        for(String orcid : orcidIds) {
            RecordNameEntity e = new RecordNameEntity();
            e.setProfile(new ProfileEntity(orcid));
            records.add(e);
        }
        return records;
    }
    
}
