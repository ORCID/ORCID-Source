package org.orcid.core.version;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.orcid.core.BaseTest;

/**
 * 
 * @author Will Simpson
 *
 */
public class V2VersionObjectFactoryTest extends BaseTest {

    @Resource
    V2VersionObjectFactory v2VersionObjectFactory;

    //WORKS
    @Test
    public void testWorkMapping_rc1_to_rc2() {
        org.orcid.jaxb.model.record_rc1.Work workRc1 = new org.orcid.jaxb.model.record_rc1.Work();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc1, "2.0_rc2");
        assertNotNull(result);
        assertTrue("Result should be rc2", result instanceof org.orcid.jaxb.model.record_rc2.Work);
    }

    @Test
    public void testWorkMapping_rc1_to_rc3() {
        org.orcid.jaxb.model.record_rc1.Work workRc1 = new org.orcid.jaxb.model.record_rc1.Work();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc1, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.Work);
    }
    
    @Test
    public void testWorkMapping_rc1_to_rc4() {
        org.orcid.jaxb.model.record_rc1.Work workRc1 = new org.orcid.jaxb.model.record_rc1.Work();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc1, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Work);
    }
    
    @Test
    public void testWorkMapping_rc1_to_v2() {
        org.orcid.jaxb.model.record_rc1.Work workRc1 = new org.orcid.jaxb.model.record_rc1.Work();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc1, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Work);
    }
    
    @Test
    public void testWorkMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record_rc2.Work workRc2 = new org.orcid.jaxb.model.record_rc2.Work();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.Work);
    }

    @Test
    public void testWorkMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record_rc2.Work workRc2 = new org.orcid.jaxb.model.record_rc2.Work();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Work);
    }
    
    @Test
    public void testWorkMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record_rc2.Work workRc2 = new org.orcid.jaxb.model.record_rc2.Work();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Work);
    }
    
    @Test
    public void testWorkMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record_rc3.Work workRc3 = new org.orcid.jaxb.model.record_rc3.Work();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Work);
    }
    
    @Test
    public void testWorkMapping_rc3_to_v2() {
        org.orcid.jaxb.model.record_rc3.Work workRc3 = new org.orcid.jaxb.model.record_rc3.Work();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc3, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Work);
    }
    
    @Test
    public void testWorkMapping_rc4_to_v2() {
        org.orcid.jaxb.model.record_rc4.Work workRc4 = new org.orcid.jaxb.model.record_rc4.Work();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Work);
    }
    
    @Test
    public void testWorkSummaryMapping_rc1_to_rc2() {
        org.orcid.jaxb.model.record.summary_rc1.WorkSummary workRc1 = new org.orcid.jaxb.model.record.summary_rc1.WorkSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc1, "2.0_rc2");
        assertNotNull(result);
        assertTrue("Result should be rc2", result instanceof org.orcid.jaxb.model.record.summary_rc2.WorkSummary);
    }

    @Test
    public void testWorkSummaryMapping_rc1_to_rc3() {
        org.orcid.jaxb.model.record.summary_rc1.WorkSummary workRc1 = new org.orcid.jaxb.model.record.summary_rc1.WorkSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc1, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record.summary_rc3.WorkSummary);
    }
    
    @Test
    public void testWorkSummaryMapping_rc1_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc1.WorkSummary workRc1 = new org.orcid.jaxb.model.record.summary_rc1.WorkSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc1, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.WorkSummary);
    }
    
    @Test
    public void testWorkSummaryMapping_rc1_to_v2() {
        org.orcid.jaxb.model.record.summary_rc1.WorkSummary workRc1 = new org.orcid.jaxb.model.record.summary_rc1.WorkSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc1, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.WorkSummary);
    }
    
    @Test
    public void testWorkSummaryMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record.summary_rc2.WorkSummary workRc2 = new org.orcid.jaxb.model.record.summary_rc2.WorkSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record.summary_rc3.WorkSummary);
    }
    
    @Test
    public void testWorkSummaryMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc2.WorkSummary workRc2 = new org.orcid.jaxb.model.record.summary_rc2.WorkSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.WorkSummary);
    }
    
    @Test
    public void testWorkSummaryMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record.summary_rc2.WorkSummary workRc2 = new org.orcid.jaxb.model.record.summary_rc2.WorkSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.WorkSummary);
    }
    
    @Test
    public void testWorkSummaryMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc3.WorkSummary workRc3 = new org.orcid.jaxb.model.record.summary_rc3.WorkSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.WorkSummary);
    }
    
    @Test
    public void testWorkSummaryMapping_rc4_to_v2() {
        org.orcid.jaxb.model.record.summary_rc4.WorkSummary workRc4 = new org.orcid.jaxb.model.record.summary_rc4.WorkSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(workRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.WorkSummary);
    }
    
    //FUNDINGS
    @Test
    public void testFundingMapping_rc1_to_rc2() {
        org.orcid.jaxb.model.record_rc1.Funding fundingRc1 = new org.orcid.jaxb.model.record_rc1.Funding();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingRc1, "2.0_rc2");
        assertNotNull(result);
        assertTrue("Result should be rc2", result instanceof org.orcid.jaxb.model.record_rc2.Funding);
    }
    
    @Test
    public void testFundingMapping_rc1_to_rc3() {
        org.orcid.jaxb.model.record_rc1.Funding fundingRc1 = new org.orcid.jaxb.model.record_rc1.Funding();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingRc1, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.Funding);
    }
    
    @Test
    public void testFundingMapping_rc1_to_rc4() {
        org.orcid.jaxb.model.record_rc1.Funding fundingRc1 = new org.orcid.jaxb.model.record_rc1.Funding();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingRc1, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Funding);
    }
    
    @Test
    public void testFundingMapping_rc1_to_v2() {
        org.orcid.jaxb.model.record_rc1.Funding fundingRc1 = new org.orcid.jaxb.model.record_rc1.Funding();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingRc1, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Funding);
    }
    
    @Test
    public void testFundingMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record_rc2.Funding fundingRc2 = new org.orcid.jaxb.model.record_rc2.Funding();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.Funding);
    }
    
    @Test
    public void testFundingMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record_rc2.Funding fundingRc2 = new org.orcid.jaxb.model.record_rc2.Funding();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Funding);
    }
    
    @Test
    public void testFundingMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record_rc2.Funding fundingRc2 = new org.orcid.jaxb.model.record_rc2.Funding();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Funding);
    }
    
    @Test
    public void testFundingMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record_rc3.Funding fundingRc3 = new org.orcid.jaxb.model.record_rc3.Funding();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Funding);
    }    
    
    @Test
    public void testFundingMapping_rc3_to_v2() {
        org.orcid.jaxb.model.record_rc3.Funding fundingRc3 = new org.orcid.jaxb.model.record_rc3.Funding();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingRc3, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Funding);
    }
    
    @Test
    public void testFundingMapping_rc4_to_v2() {
        org.orcid.jaxb.model.record_rc4.Funding fundingRc4 = new org.orcid.jaxb.model.record_rc4.Funding();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Funding);
    }
    
    @Test
    public void testFundingSummaryMapping_rc1_to_rc2() {
        org.orcid.jaxb.model.record.summary_rc1.FundingSummary fundingSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.FundingSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingSummaryRc1, "2.0_rc2");
        assertNotNull(result);
        assertTrue("Result should be rc2", result instanceof org.orcid.jaxb.model.record.summary_rc2.FundingSummary);
    }
    
    @Test
    public void testFundingSummaryMapping_rc1_to_rc3() {
        org.orcid.jaxb.model.record.summary_rc1.FundingSummary fundingSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.FundingSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingSummaryRc1, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record.summary_rc3.FundingSummary);
    }
    
    @Test
    public void testFundingSummaryMapping_rc1_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc1.FundingSummary fundingSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.FundingSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingSummaryRc1, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.FundingSummary);
    }
    
    @Test
    public void testFundingSummaryMapping_rc1_to_v2() {
        org.orcid.jaxb.model.record.summary_rc1.FundingSummary fundingSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.FundingSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingSummaryRc1, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.FundingSummary);
    }    
    
    @Test
    public void testFundingSummaryMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record.summary_rc2.FundingSummary fundingSummaryRc2 = new org.orcid.jaxb.model.record.summary_rc2.FundingSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingSummaryRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record.summary_rc3.FundingSummary);
    }
    
    @Test
    public void testFundingSummaryMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc2.FundingSummary fundingSummaryRc2 = new org.orcid.jaxb.model.record.summary_rc2.FundingSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingSummaryRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.FundingSummary);
    }
    
    @Test
    public void testFundingSummaryMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record.summary_rc2.FundingSummary fundingSummaryRc2 = new org.orcid.jaxb.model.record.summary_rc2.FundingSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingSummaryRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.FundingSummary);
    }
    
    @Test
    public void testFundingSummaryMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc3.FundingSummary fundingSummaryRc3 = new org.orcid.jaxb.model.record.summary_rc3.FundingSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingSummaryRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.FundingSummary);
    }
    
    @Test
    public void testFundingSummaryMapping_rc3_to_v2() {
        org.orcid.jaxb.model.record.summary_rc3.FundingSummary fundingSummaryRc3 = new org.orcid.jaxb.model.record.summary_rc3.FundingSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingSummaryRc3, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.FundingSummary);
    }
    
    @Test
    public void testFundingSummaryMapping_rc4_to_v2() {
        org.orcid.jaxb.model.record.summary_rc4.FundingSummary fundingSummaryRc4 = new org.orcid.jaxb.model.record.summary_rc4.FundingSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(fundingSummaryRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.FundingSummary);
    }
    
    //EDUCATIONS
    @Test
    public void testEducationMapping_rc1_to_rc2() {
        org.orcid.jaxb.model.record_rc1.Education educationRc1 = new org.orcid.jaxb.model.record_rc1.Education();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationRc1, "2.0_rc2");
        assertNotNull(result);
        assertTrue("Result should be rc2", result instanceof org.orcid.jaxb.model.record_rc2.Education);
    }
    
    @Test
    public void testEducationMapping_rc1_to_rc3() {
        org.orcid.jaxb.model.record_rc1.Education educationRc1 = new org.orcid.jaxb.model.record_rc1.Education();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationRc1, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.Education);
    }
    
    @Test
    public void testEducationMapping_rc1_to_rc4() {
        org.orcid.jaxb.model.record_rc1.Education educationRc1 = new org.orcid.jaxb.model.record_rc1.Education();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationRc1, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Education);
    }
    
    @Test
    public void testEducationMapping_rc1_to_v2() {
        org.orcid.jaxb.model.record_rc1.Education educationRc1 = new org.orcid.jaxb.model.record_rc1.Education();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationRc1, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Education);
    }
    
    @Test
    public void testEducationMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record_rc2.Education educationRc2 = new org.orcid.jaxb.model.record_rc2.Education();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.Education);
    }
    
    @Test
    public void testEducationMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record_rc2.Education educationRc2 = new org.orcid.jaxb.model.record_rc2.Education();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Education);
    }
    
    @Test
    public void testEducationMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record_rc2.Education educationRc2 = new org.orcid.jaxb.model.record_rc2.Education();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Education);
    }
    
    @Test
    public void testEducationMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record_rc3.Education educationRc3 = new org.orcid.jaxb.model.record_rc3.Education();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Education);
    }
    
    @Test
    public void testEducationMapping_rc3_to_v2() {
        org.orcid.jaxb.model.record_rc3.Education educationRc3 = new org.orcid.jaxb.model.record_rc3.Education();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationRc3, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Education);
    }
    
    @Test
    public void testEducationMapping_rc43_to_v2() {
        org.orcid.jaxb.model.record_rc4.Education educationRc4 = new org.orcid.jaxb.model.record_rc4.Education();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Education);
    }
    
    @Test
    public void testEducationSummaryMapping_rc1_to_rc2() {
        org.orcid.jaxb.model.record.summary_rc1.EducationSummary educationSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.EducationSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationSummaryRc1, "2.0_rc2");
        assertNotNull(result);
        assertTrue("Result should be rc2", result instanceof org.orcid.jaxb.model.record.summary_rc2.EducationSummary);
    }
    
    @Test
    public void testEducationSummaryMapping_rc1_to_rc3() {
        org.orcid.jaxb.model.record.summary_rc1.EducationSummary educationSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.EducationSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationSummaryRc1, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record.summary_rc3.EducationSummary);
    }
    
    @Test
    public void testEducationSummaryMapping_rc1_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc1.EducationSummary educationSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.EducationSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationSummaryRc1, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.EducationSummary);
    }
    
    @Test
    public void testEducationSummaryMapping_rc1_to_v2() {
        org.orcid.jaxb.model.record.summary_rc1.EducationSummary educationSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.EducationSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationSummaryRc1, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.EducationSummary);
    }
    
    @Test
    public void testEducationSummaryMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record.summary_rc2.EducationSummary educationSummaryRc2 = new org.orcid.jaxb.model.record.summary_rc2.EducationSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationSummaryRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record.summary_rc3.EducationSummary);
    }
    
    @Test
    public void testEducationSummaryMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc2.EducationSummary educationSummaryRc2 = new org.orcid.jaxb.model.record.summary_rc2.EducationSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationSummaryRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.EducationSummary);
    }
    
    @Test
    public void testEducationSummaryMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record.summary_rc2.EducationSummary educationSummaryRc2 = new org.orcid.jaxb.model.record.summary_rc2.EducationSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationSummaryRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.EducationSummary);
    }
    
    @Test
    public void testEducationSummaryMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc3.EducationSummary educationSummaryRc3 = new org.orcid.jaxb.model.record.summary_rc3.EducationSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationSummaryRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.EducationSummary);
    }
    
    @Test
    public void testEducationSummaryMapping_rc3_to_v2() {
        org.orcid.jaxb.model.record.summary_rc3.EducationSummary educationSummaryRc3 = new org.orcid.jaxb.model.record.summary_rc3.EducationSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationSummaryRc3, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.EducationSummary);
    }
    
    @Test
    public void testEducationSummaryMapping_rc4_to_v2() {
        org.orcid.jaxb.model.record.summary_rc4.EducationSummary educationSummaryRc4 = new org.orcid.jaxb.model.record.summary_rc4.EducationSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(educationSummaryRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.EducationSummary);
    }
    
    //EMPLOYMENTS
    @Test
    public void testEmploymentMapping_rc1_to_rc2() {
        org.orcid.jaxb.model.record_rc1.Employment employmentRc1 = new org.orcid.jaxb.model.record_rc1.Employment();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentRc1, "2.0_rc2");
        assertNotNull(result);
        assertTrue("Result should be rc2", result instanceof org.orcid.jaxb.model.record_rc2.Employment);
    }
    
    @Test
    public void testEmploymentMapping_rc1_to_rc3() {
        org.orcid.jaxb.model.record_rc1.Employment employmentRc1 = new org.orcid.jaxb.model.record_rc1.Employment();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentRc1, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.Employment);
    }
    
    @Test
    public void testEmploymentMapping_rc1_to_rc4() {
        org.orcid.jaxb.model.record_rc1.Employment employmentRc1 = new org.orcid.jaxb.model.record_rc1.Employment();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentRc1, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Employment);
    }
    
    @Test
    public void testEmploymentMapping_rc1_to_v2() {
        org.orcid.jaxb.model.record_rc1.Employment employmentRc1 = new org.orcid.jaxb.model.record_rc1.Employment();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentRc1, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Employment);
    }
    
    @Test
    public void testEmploymentMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record_rc2.Employment employmentRc2 = new org.orcid.jaxb.model.record_rc2.Employment();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.Employment);
    }
    
    @Test
    public void testEmploymentMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record_rc2.Employment employmentRc2 = new org.orcid.jaxb.model.record_rc2.Employment();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Employment);
    }
    
    @Test
    public void testEmploymentMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record_rc2.Employment employmentRc2 = new org.orcid.jaxb.model.record_rc2.Employment();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Employment);
    }
    
    @Test
    public void testEmploymentMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record_rc3.Employment employmentRc3 = new org.orcid.jaxb.model.record_rc3.Employment();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Employment);
    }
    
    @Test
    public void testEmploymentMapping_rc3_to_v2() {
        org.orcid.jaxb.model.record_rc3.Employment employmentRc3 = new org.orcid.jaxb.model.record_rc3.Employment();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentRc3, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Employment);
    }
        
    @Test
    public void testEmploymentMapping_rc4_to_v2() {
        org.orcid.jaxb.model.record_rc4.Employment employmentRc4 = new org.orcid.jaxb.model.record_rc4.Employment();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Employment);
    }
        
    @Test
    public void testEmploymentSummaryMapping_rc1_to_rc2() {
        org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary employmentSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentSummaryRc1, "2.0_rc2");
        assertNotNull(result);
        assertTrue("Result should be rc2", result instanceof org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary);
    }
    
    @Test
    public void testEmploymentSummaryMapping_rc1_to_rc3() {
        org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary employmentSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentSummaryRc1, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary);
    }
    
    @Test
    public void testEmploymentSummaryMapping_rc1_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary employmentSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentSummaryRc1, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary);
    }
    
    @Test
    public void testEmploymentSummaryMapping_rc1_to_v2() {
        org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary employmentSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentSummaryRc1, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.EmploymentSummary);
    }
    
    @Test
    public void testEmploymentSummaryMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary employmentSummaryRc2 = new org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentSummaryRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary);
    }
    
    @Test
    public void testEmploymentSummaryMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary employmentSummaryRc2 = new org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentSummaryRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary);
    }
    
    @Test
    public void testEmploymentSummaryMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary employmentSummaryRc2 = new org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentSummaryRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.EmploymentSummary);
    }
    
    @Test
    public void testEmploymentSummaryMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary employmentSummaryRc3 = new org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentSummaryRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary);
    }
    
    @Test
    public void testEmploymentSummaryMapping_rc3_to_v2() {
        org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary employmentSummaryRc3 = new org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentSummaryRc3, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.EmploymentSummary);
    }
    
    @Test
    public void testEmploymentSummaryMapping_rc4_to_v2() {
        org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary employmentSummaryRc4 = new org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(employmentSummaryRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.EmploymentSummary);
    }
    
    //PEER REVIEWS
    @Test
    public void testPeerReviewMapping_rc1_to_rc2() {
        org.orcid.jaxb.model.record_rc1.PeerReview peerReviewRc1 = new org.orcid.jaxb.model.record_rc1.PeerReview();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewRc1, "2.0_rc2");
        assertNotNull(result);
        assertTrue("Result should be rc2", result instanceof org.orcid.jaxb.model.record_rc2.PeerReview);
    }
    
    @Test
    public void testPeerReviewMapping_rc1_to_rc3() {
        org.orcid.jaxb.model.record_rc1.PeerReview peerReviewRc1 = new org.orcid.jaxb.model.record_rc1.PeerReview();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewRc1, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.PeerReview);
    }
    
    @Test
    public void testPeerReviewMapping_rc1_to_rc4() {
        org.orcid.jaxb.model.record_rc1.PeerReview peerReviewRc1 = new org.orcid.jaxb.model.record_rc1.PeerReview();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewRc1, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.PeerReview);
    }
    
    @Test
    public void testPeerReviewMapping_rc1_to_v2() {
        org.orcid.jaxb.model.record_rc1.PeerReview peerReviewRc1 = new org.orcid.jaxb.model.record_rc1.PeerReview();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewRc1, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.PeerReview);
    }
    
    @Test
    public void testPeerReviewMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record_rc2.PeerReview peerReviewRc2 = new org.orcid.jaxb.model.record_rc2.PeerReview();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.PeerReview);
    }
    
    @Test
    public void testPeerReviewMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record_rc2.PeerReview peerReviewRc2 = new org.orcid.jaxb.model.record_rc2.PeerReview();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.PeerReview);
    }
    
    @Test
    public void testPeerReviewMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record_rc2.PeerReview peerReviewRc2 = new org.orcid.jaxb.model.record_rc2.PeerReview();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.PeerReview);
    }
    
    @Test
    public void testPeerReviewMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record_rc3.PeerReview peerReviewRc3 = new org.orcid.jaxb.model.record_rc3.PeerReview();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.PeerReview);
    }
    
    @Test
    public void testPeerReviewMapping_rc3_to_v2() {
        org.orcid.jaxb.model.record_rc3.PeerReview peerReviewRc3 = new org.orcid.jaxb.model.record_rc3.PeerReview();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewRc3, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.PeerReview);
    }
    
    @Test
    public void testPeerReviewMapping_rc4_to_v2() {
        org.orcid.jaxb.model.record_rc4.PeerReview peerReviewRc4 = new org.orcid.jaxb.model.record_rc4.PeerReview();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_v2.PeerReview);
    }
    
    @Test
    public void testPeerReviewSummaryMapping_rc1_to_rc2() {
        org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary peerReviewSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewSummaryRc1, "2.0_rc2");
        assertNotNull(result);
        assertTrue("Result should be rc2", result instanceof org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary);
    }
    
    @Test
    public void testPeerReviewSummaryMapping_rc1_to_rc3() {
        org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary peerReviewSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewSummaryRc1, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary);
    }
    
    @Test
    public void testPeerReviewSummaryMapping_rc1_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary peerReviewSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewSummaryRc1, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary);
    }
    
    @Test
    public void testPeerReviewSummaryMapping_rc1_to_v2() {
        org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary peerReviewSummaryRc1 = new org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewSummaryRc1, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary);
    }
    
    @Test
    public void testPeerReviewSummaryMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary peerReviewSummaryRc2 = new org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewSummaryRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary);
    }
    
    @Test
    public void testPeerReviewSummaryMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary peerReviewSummaryRc2 = new org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewSummaryRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary);
    }
    
    @Test
    public void testPeerReviewSummaryMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary peerReviewSummaryRc2 = new org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewSummaryRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary);
    }
    
    @Test
    public void testPeerReviewSummaryMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary peerReviewSummaryRc3 = new org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewSummaryRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary);
    }
    
    @Test
    public void testPeerReviewSummaryMapping_rc3_to_v2() {
        org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary peerReviewSummaryRc3 = new org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewSummaryRc3, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary);
    }
    
    @Test
    public void testPeerReviewSummaryMapping_rc4_to_v2() {
        org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary peerReviewSummaryRc4 = new org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary();
        Object result = v2VersionObjectFactory.createEquivalentInstance(peerReviewSummaryRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary);
    }
    //OTHER NAMES
    @Test
    public void testOtherNameMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record_rc2.OtherName otherNameRc2 = new org.orcid.jaxb.model.record_rc2.OtherName();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(otherNameRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.OtherName);
    }
    
    @Test
    public void testOtherNameMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record_rc2.OtherName otherNameRc2 = new org.orcid.jaxb.model.record_rc2.OtherName();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(otherNameRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.OtherName);
    }
    
    @Test
    public void testOtherNameMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record_rc2.OtherName otherNameRc2 = new org.orcid.jaxb.model.record_rc2.OtherName();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(otherNameRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.OtherName);
    }
    
    @Test
    public void testOtherNameMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record_rc3.OtherName otherNameRc3 = new org.orcid.jaxb.model.record_rc3.OtherName();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(otherNameRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.OtherName);
    }
    
    @Test
    public void testOtherNameMapping_rc3_to_v2() {
        org.orcid.jaxb.model.record_rc3.OtherName otherNameRc3 = new org.orcid.jaxb.model.record_rc3.OtherName();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(otherNameRc3, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.OtherName);
    }
    
    @Test
    public void testOtherNameMapping_rc4_to_v2() {
        org.orcid.jaxb.model.record_rc4.OtherName otherNameRc4 = new org.orcid.jaxb.model.record_rc4.OtherName();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(otherNameRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.OtherName);
    }
    //ADDRESSES
    @Test
    public void testAddressMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record_rc2.Address addressRc2 = new org.orcid.jaxb.model.record_rc2.Address();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(addressRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.Address);
    }
    
    @Test
    public void testAddressMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record_rc2.Address addressRc2 = new org.orcid.jaxb.model.record_rc2.Address();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(addressRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Address);
    }
    
    @Test
    public void testAddressMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record_rc2.Address addressRc2 = new org.orcid.jaxb.model.record_rc2.Address();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(addressRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Address);
    }
    
    @Test
    public void testAddressMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record_rc3.Address addressRc3 = new org.orcid.jaxb.model.record_rc3.Address();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(addressRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.Address);
    }
    
    @Test
    public void testAddressMapping_rc3_to_v2() {
        org.orcid.jaxb.model.record_rc3.Address addressRc3 = new org.orcid.jaxb.model.record_rc3.Address();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(addressRc3, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Address);
    }
    
    @Test
    public void testAddressMapping_rc4_to_v2() {
        org.orcid.jaxb.model.record_rc4.Address addressRc4 = new org.orcid.jaxb.model.record_rc4.Address();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(addressRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.Address);
    }
    //RESEARCHER URLS
    @Test
    public void testResearcherUrlMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record_rc2.ResearcherUrl researcherUrlRc2 = new org.orcid.jaxb.model.record_rc2.ResearcherUrl();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(researcherUrlRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.ResearcherUrl);
    }
    
    @Test
    public void testResearcherUrlMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record_rc2.ResearcherUrl researcherUrlRc2 = new org.orcid.jaxb.model.record_rc2.ResearcherUrl();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(researcherUrlRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.ResearcherUrl);
    }
      
    @Test
    public void testResearcherUrlMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record_rc2.ResearcherUrl researcherUrlRc2 = new org.orcid.jaxb.model.record_rc2.ResearcherUrl();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(researcherUrlRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.ResearcherUrl);
    }
    
    @Test
    public void testResearcherUrlMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record_rc3.ResearcherUrl researcherUrlRc3 = new org.orcid.jaxb.model.record_rc3.ResearcherUrl();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(researcherUrlRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.ResearcherUrl);
    }
    
    @Test
    public void testResearcherUrlMapping_rc3_to_v2() {
        org.orcid.jaxb.model.record_rc3.ResearcherUrl researcherUrlRc3 = new org.orcid.jaxb.model.record_rc3.ResearcherUrl();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(researcherUrlRc3, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.ResearcherUrl);
    }
    
    @Test
    public void testResearcherUrlMapping_rc4_to_v2() {
        org.orcid.jaxb.model.record_rc4.ResearcherUrl researcherUrlRc4 = new org.orcid.jaxb.model.record_rc4.ResearcherUrl();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(researcherUrlRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.ResearcherUrl);
    }
    
    //EXTERNAL IDENTIFIERS
    @Test
    public void testPersonExternalIdentifierMapping_rc2_to_rc3() {
        org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier extIdRc2 = new org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(extIdRc2, "2.0_rc3");
        assertNotNull(result);
        assertTrue("Result should be rc3", result instanceof org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier);
    }
    
    @Test
    public void testPersonExternalIdentifierMapping_rc2_to_rc4() {
        org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier extIdRc2 = new org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(extIdRc2, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier);
    }
    
    @Test
    public void testPersonExternalIdentifierMapping_rc2_to_v2() {
        org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier extIdRc2 = new org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(extIdRc2, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.PersonExternalIdentifier);
    }
    
    @Test
    public void testPersonExternalIdentifierMapping_rc3_to_rc4() {
        org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier extIdRc3 = new org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(extIdRc3, "2.0_rc4");
        assertNotNull(result);
        assertTrue("Result should be rc4", result instanceof org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier);
    }
    
    @Test
    public void testPersonExternalIdentifierMapping_rc3_to_v2() {
        org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier extIdRc3 = new org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(extIdRc3, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.PersonExternalIdentifier);
    }
    
    @Test
    public void testPersonExternalIdentifierMapping_rc4_to_v2() {
        org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier extIdRc4 = new org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier();        
        Object result = v2VersionObjectFactory.createEquivalentInstance(extIdRc4, "2.0");
        assertNotNull(result);
        assertTrue("Result should be v2", result instanceof org.orcid.jaxb.model.record_v2.PersonExternalIdentifier);
    }
}
