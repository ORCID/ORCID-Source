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
import org.orcid.jaxb.model.common.SequenceType;
import org.orcid.jaxb.model.v3.release.common.Contributor;
import org.orcid.jaxb.model.v3.release.common.ContributorAttributes;
import org.orcid.jaxb.model.v3.release.common.ContributorEmail;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.record.WorkContributors;

public class WorkContributorsConverterTest {

    @Mock
    private ContributorRoleConverter mockContributorRoleConverter;

    @InjectMocks
    private WorkContributorsConverter workContributorsConverter;

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
        
        WorkContributors workContributors = getWorkContributors();
        String json = workContributorsConverter.convertTo(workContributors, null);
        assertTrue(json.contains("SOME-VALUE"));
        assertTrue(json.contains("SOME-VALUE"));
        
        // role converter needed to turn role names to upper case enum names 
        Mockito.verify(mockContributorRoleConverter, Mockito.times(2)).toDBRole(Mockito.anyString());
    }
    
    /**
     * Test to check invalid roles converted to null don't get into the db
     */
    @Test(expected = InvalidContributorRoleException.class)
    public void testConvertToWithErroneousValue() {
        Mockito.when(mockContributorRoleConverter.toDBRole(Mockito.anyString())).thenReturn(null);
        
        WorkContributors workContributors = getWorkContributors();
        workContributorsConverter.convertTo(workContributors, null);
    }

    /**
     * WorkContributors model object should only contain legacy roles but this
     * conversion process should be able to handle CRediT roles in the json and
     * either convert them to legacy roles or remove them, depending on the
     * mapping process of the ContributorRoleConverter.
     */
    @Test
    public void testConvertFrom() {
        // imagine all roles converted to author
        Mockito.when(mockContributorRoleConverter.toRoleValue(Mockito.anyString())).thenReturn("some-value");
        WorkContributors workContributors = workContributorsConverter.convertFrom(getWorkContributorsJson(), null);
        assertNotNull(workContributors.getContributor());
        assertEquals(2, workContributors.getContributor().size());
        assertEquals("some-value", workContributors.getContributor().get(0).getContributorAttributes().getContributorRole());
        assertEquals("some-value", workContributors.getContributor().get(1).getContributorAttributes().getContributorRole());
        
        Mockito.verify(mockContributorRoleConverter, Mockito.times(2)).toRoleValue(Mockito.anyString());
    }
    
    private WorkContributors getWorkContributors() {
        WorkContributors workContributors = new WorkContributors();
        workContributors.getContributor().add(getContributor(CreditRole.FUNDING_ACQUISITION.value()));
        workContributors.getContributor().add(getContributor(CreditRole.FORMAL_ANALYSIS.value()));
        return workContributors;
    }
    
    private Contributor getContributor(String contributorRole) {
        Contributor contributor = new Contributor();
        contributor.setContributorOrcid(new ContributorOrcid("orcid"));
        contributor.setContributorEmail(new ContributorEmail("email"));
        contributor.setCreditName(new CreditName("creditName"));
        ContributorAttributes contributorAttributes = new ContributorAttributes();
        contributorAttributes.setContributorSequence(SequenceType.FIRST);
        contributorAttributes.setContributorRole(contributorRole);
        contributor.setContributorAttributes(contributorAttributes);
        return contributor;
    }
    
    private String getWorkContributorsJson() {
        return "{\"contributor\":[{\"contributorOrcid\":{\"uri\":\"https://orcid.org/0000-0001-5109-3700\",\"path\":\"0000-0001-5109-3700\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"Laure L. Haak\"},\"contributorEmail\":null,\"contributorAttributes\":{\"contributorSequence\":\"FIRST\",\"contributorRole\":\"WRITING_ORIGINAL_DRAFT\"}},{\"contributorOrcid\":{\"uri\":\"https://orcid.org/0000-0001-5109-3700\",\"path\":\"0000-0001-5109-3700\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"Laure L. Haak\"},\"contributorEmail\":null,\"contributorAttributes\":{\"contributorSequence\":\"FIRST\",\"contributorRole\":\"METHODOLOGY\"}}]}";
    }
    
}
