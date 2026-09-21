package org.orcid.core.adapter.mapstruct;

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
import org.orcid.core.adapter.mapstruct.FundingContributorsMapperV3;
import org.orcid.core.contributors.roles.ContributorRoleConverter;
import org.orcid.core.contributors.roles.InvalidContributorRoleException;
import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.jaxb.model.v3.release.common.ContributorEmail;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.record.FundingContributor;
import org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes;
import org.orcid.jaxb.model.v3.release.record.FundingContributors;

public class FundingContributorsMapperV3Test {

    @Mock
    private ContributorRoleConverter mockContributorRoleConverter;

    private FundingContributorsMapperV3 fundingContributorsConverter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fundingContributorsConverter = new FundingContributorsMapperV3(mockContributorRoleConverter);
    }
    
    @Test
    public void testConvertToWithNullSource() {
        assertNull(fundingContributorsConverter.convertTo(null));
    }

    @Test
    public void testConvertFromWithNullOrEmptySource() {
        assertNull(fundingContributorsConverter.convertFrom(null));
        assertNull(fundingContributorsConverter.convertFrom(""));
        assertNull(fundingContributorsConverter.convertFrom("   "));
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
        String json = fundingContributorsConverter.convertTo(fundingContributors);
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
        fundingContributorsConverter.convertTo(fundingContributors);
    }

    @Test
    public void testConvertFrom() {
        // imagine all roles converted to author
        Mockito.when(mockContributorRoleConverter.toRoleValue(Mockito.anyString())).thenReturn("some-value");
        FundingContributors fundingContributors = fundingContributorsConverter.convertFrom(getFundingContributorsJson());
        assertNotNull(fundingContributors.getContributor());
        assertEquals(1, fundingContributors.getContributor().size());
        assertEquals("some-value", fundingContributors.getContributor().get(0).getContributorAttributes().getContributorRole());
        
        Mockito.verify(mockContributorRoleConverter, Mockito.times(1)).toRoleValue(Mockito.anyString());
    }

    @Test
    public void testConvertFromWithMissingHostAndNulls() throws Exception {
        Mockito.when(mockContributorRoleConverter.toRoleValue(Mockito.anyString())).thenReturn("SUPERVISION");
        String json = "{\n" +
                "\t\"contributor\": [{\n" +
                "\t\t\"contributorOrcid\": {\n" +
                "\t\t\t\"uri\": \"https://qa.orcid.org/0009-0000-7948-587X\",\n" +
                "\t\t\t\"path\": \"0009-0000-7948-587X\",\n" +
                "\t\t\t\"host\": null\n" +
                "\t\t},\n" +
                "\t\t\"creditName\": {\n" +
                "\t\t\t\"content\": \"Test Author\"\n" +
                "\t\t},\n" +
                "\t\t\"contributorEmail\": null,\n" +
                "\t\t\"contributorAttributes\": {\n" +
                "\t\t\t\"contributorRole\": \"SUPERVISION\"\n" +
                "\t\t}\n" +
                "\t}]\n" +
                "}";

        FundingContributors fundingContributors = fundingContributorsConverter.convertFrom(json);
        assertNotNull(fundingContributors);
        assertEquals(1, fundingContributors.getContributor().size());
        FundingContributor c = fundingContributors.getContributor().get(0);
        assertNotNull(c.getContributorOrcid());
        assertEquals("qa.orcid.org", c.getContributorOrcid().getHost());
        assertEquals("0009-0000-7948-587X", c.getContributorOrcid().getPath());
        assertEquals("https://qa.orcid.org/0009-0000-7948-587X", c.getContributorOrcid().getUri());
        assertNotNull(c.getCreditName());
        assertEquals("Test Author", c.getCreditName().getContent());
        assertNull(c.getContributorEmail());
        assertNotNull(c.getContributorAttributes());
        assertEquals("SUPERVISION", c.getContributorAttributes().getContributorRole());

        // Verify XML marshalling succeeds
        jakarta.xml.bind.JAXBContext context = jakarta.xml.bind.JAXBContext.newInstance(FundingContributors.class);
        java.io.StringWriter writer = new java.io.StringWriter();
        context.createMarshaller().marshal(fundingContributors, writer);
        assertTrue(writer.toString().contains("qa.orcid.org"));
    }

    @Test
    public void testConvertFromCleansEmptyXmlValueFields() throws Exception {
        String json = "{\n" +
                "\t\"contributor\": [{\n" +
                "\t\t\"contributorOrcid\": {\n" +
                "\t\t\t\"uri\": null,\n" +
                "\t\t\t\"path\": null,\n" +
                "\t\t\t\"host\": null\n" +
                "\t\t},\n" +
                "\t\t\"creditName\": {\n" +
                "\t\t\t\"content\": \"   \"\n" +
                "\t\t},\n" +
                "\t\t\"contributorEmail\": {\n" +
                "\t\t\t\"value\": null\n" +
                "\t\t},\n" +
                "\t\t\"contributorAttributes\": {\n" +
                "\t\t\t\"contributorRole\": null\n" +
                "\t\t}\n" +
                "\t}]\n" +
                "}";

        FundingContributors fundingContributors = fundingContributorsConverter.convertFrom(json);
        assertNotNull(fundingContributors);
        assertEquals(1, fundingContributors.getContributor().size());
        FundingContributor c = fundingContributors.getContributor().get(0);
        assertNull(c.getContributorOrcid());
        assertNull(c.getCreditName());
        assertNull(c.getContributorEmail());
        assertNull(c.getContributorAttributes());

        // Verify XML marshalling succeeds without AccessorException
        jakarta.xml.bind.JAXBContext context = jakarta.xml.bind.JAXBContext.newInstance(FundingContributors.class);
        java.io.StringWriter writer = new java.io.StringWriter();
        context.createMarshaller().marshal(fundingContributors, writer);
        assertNotNull(writer.toString());
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
