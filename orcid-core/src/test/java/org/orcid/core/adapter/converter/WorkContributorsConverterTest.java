package org.orcid.core.adapter.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.contributors.roles.ContributorRoleConverter;
import org.orcid.jaxb.model.common_v2.Contributor;
import org.orcid.jaxb.model.common_v2.ContributorAttributes;
import org.orcid.jaxb.model.common_v2.ContributorEmail;
import org.orcid.jaxb.model.common_v2.ContributorOrcid;
import org.orcid.jaxb.model.common_v2.ContributorRole;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.record_v2.SequenceType;
import org.orcid.jaxb.model.record_v2.WorkContributors;

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
        WorkContributors workContributors = getWorkContributors();
        String json = workContributorsConverter.convertTo(workContributors, null);
        assertTrue(json.contains("AUTHOR"));
        assertTrue(json.contains("CO_INVENTOR"));
        
        // role converter not needed for this operation
        Mockito.verify(mockContributorRoleConverter, Mockito.never()).toDBRole(Mockito.anyString());
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
        Mockito.when(mockContributorRoleConverter.toLegacyRoleName(Mockito.anyString())).thenReturn("AUTHOR");
        WorkContributors workContributors = workContributorsConverter.convertFrom(getWorkContributorsJson(), null);
        assertNotNull(workContributors.getContributor());
        assertEquals(2, workContributors.getContributor().size());
        assertEquals(ContributorRole.AUTHOR, workContributors.getContributor().get(0).getContributorAttributes().getContributorRole());
        assertEquals(ContributorRole.AUTHOR, workContributors.getContributor().get(1).getContributorAttributes().getContributorRole());
        
        // imagine all roles not convertible
        Mockito.when(mockContributorRoleConverter.toLegacyRoleName(Mockito.anyString())).thenReturn(null);
        workContributors = workContributorsConverter.convertFrom(getWorkContributorsJson(), null);
        assertNotNull(workContributors.getContributor());
        assertEquals(2, workContributors.getContributor().size());
        assertNull(workContributors.getContributor().get(0).getContributorAttributes().getContributorRole());
        assertNull(workContributors.getContributor().get(1).getContributorAttributes().getContributorRole());
    }
    
    private WorkContributors getWorkContributors() {
        WorkContributors workContributors = new WorkContributors();
        workContributors.getContributor().add(getContributor(ContributorRole.AUTHOR));
        workContributors.getContributor().add(getContributor(ContributorRole.CO_INVENTOR));
        return workContributors;
    }
    
    private Contributor getContributor(ContributorRole contributorRole) {
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
