package org.orcid.core.adapter.v3.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.contributors.roles.ContributorRoleConverter;
import org.orcid.core.contributors.roles.InvalidContributorRoleException;
import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.jaxb.model.v3.release.common.ContributorEmail;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.record.FundingContributor;
import org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes;
import org.orcid.jaxb.model.v3.release.record.FundingContributors;

public class FundingContributorsConverterTest {

    @Mock
    private ContributorRoleConverter mockContributorRoleConverter;

    @InjectMocks
    private FundingContributorsConverter fundingContributorsConverter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    /**
     * Test conversion to json string that will be stored in the DB.
     * 
     * Roles should be in upper case.
     * 
     */
    @Test
    public void testConvertTo() {
        Mockito.when(mockContributorRoleConverter.toDBRole(Mockito.anyString())).thenReturn("SOME-VALUE");
        
        FundingContributors fundingContributors = getFundingContributors();
        String json = fundingContributorsConverter.convertTo(fundingContributors, null);
        assertTrue(json.contains("SOME-VALUE"));
        assertTrue(json.contains("SOME-VALUE"));
        
        // role converter needed to turn role names to upper case enum names 
        Mockito.verify(mockContributorRoleConverter, Mockito.times(1)).toDBRole(Mockito.anyString());
    }
    
    /**
     * Test to check invalid roles converted to null don't get into the db
     */
    @Test(expected = InvalidContributorRoleException.class)
    public void testConvertToWithErroneousValue() {
        Mockito.when(mockContributorRoleConverter.toDBRole(Mockito.anyString())).thenReturn(null);
        
        FundingContributors fundingContributors = getFundingContributors();
        fundingContributorsConverter.convertTo(fundingContributors, null);
    }

    @Test
    public void testConvertFrom() {
        // imagine all roles converted to author
        Mockito.when(mockContributorRoleConverter.toRoleValue(Mockito.anyString())).thenReturn("some-value");
        FundingContributors fundingContributors = fundingContributorsConverter.convertFrom(getFundingContributorsJson(), null);
        assertNotNull(fundingContributors.getContributor());
        assertEquals(1, fundingContributors.getContributor().size());
        assertEquals("some-value", fundingContributors.getContributor().get(0).getContributorAttributes().getContributorRole());
        
        Mockito.verify(mockContributorRoleConverter, Mockito.times(1)).toRoleValue(Mockito.anyString());
    }
    
    private FundingContributors getFundingContributors() {
        FundingContributors fundingContributors = new FundingContributors();
        fundingContributors.getContributor().add(getFundingContributor(CreditRole.SUPERVISION.value()));
        return fundingContributors;
    }
    
    private FundingContributor getFundingContributor(String contributorRole) {
        FundingContributor contributor = new FundingContributor();
        contributor.setContributorOrcid(new ContributorOrcid("orcid"));
        contributor.setContributorEmail(new ContributorEmail("email"));
        contributor.setCreditName(new CreditName("creditName"));
        FundingContributorAttributes contributorAttributes = new FundingContributorAttributes();
        contributorAttributes.setContributorRole(contributorRole);
        contributor.setContributorAttributes(contributorAttributes);
        return contributor;
    }
    
    private String getFundingContributorsJson() {
        return "{\"contributor\":[{\"contributorOrcid\":{\"uri\":\"https://orcid.org/0000-0001-5109-3700\",\"path\":\"0000-0001-5109-3700\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"Laure L. Haak\"},\"contributorEmail\":null,\"contributorAttributes\":{\"contributorRole\":\"SUPERVISION\"}}]}";
    }
    
}
