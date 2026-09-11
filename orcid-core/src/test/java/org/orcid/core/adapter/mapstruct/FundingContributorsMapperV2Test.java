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
import org.orcid.core.contributors.roles.fundings.FundingContributorRoleConverter;
import org.orcid.jaxb.model.common_v2.ContributorOrcid;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.record_v2.FundingContributor;
import org.orcid.jaxb.model.record_v2.FundingContributorAttributes;
import org.orcid.jaxb.model.record_v2.FundingContributorRole;
import org.orcid.jaxb.model.record_v2.FundingContributors;

public class FundingContributorsMapperV2Test {

    @Mock
    private FundingContributorRoleConverter mockContributorRoleConverter;

    @InjectMocks
    private FundingContributorsMapperV2 fundingContributorsMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConvertToWithNullSource() {
        assertNull(fundingContributorsMapper.convertTo(null));
    }

    @Test
    public void testConvertFromWithNullOrEmptySource() {
        assertNull(fundingContributorsMapper.convertFrom(null));
        assertNull(fundingContributorsMapper.convertFrom(""));
        assertNull(fundingContributorsMapper.convertFrom("   "));
    }

    @Test
    public void testConvertTo() {
        FundingContributors fundingContributors = getFundingContributors();
        String json = fundingContributorsMapper.convertTo(fundingContributors);
        assertTrue(json.contains("LEAD"));
        assertTrue(json.contains("CO_LEAD"));
    }

    @Test
    public void testConvertFrom() {
        Mockito.when(mockContributorRoleConverter.toLegacyRoleName(Mockito.anyString())).thenReturn("LEAD");
        FundingContributors fundingContributors = fundingContributorsMapper.convertFrom(getFundingContributorsJson());
        assertNotNull(fundingContributors.getContributor());
        assertEquals(2, fundingContributors.getContributor().size());
        assertEquals(FundingContributorRole.LEAD, fundingContributors.getContributor().get(0).getContributorAttributes().getContributorRole());
        assertEquals(FundingContributorRole.LEAD, fundingContributors.getContributor().get(1).getContributorAttributes().getContributorRole());

        Mockito.when(mockContributorRoleConverter.toLegacyRoleName(Mockito.anyString())).thenReturn(null);
        fundingContributors = fundingContributorsMapper.convertFrom(getFundingContributorsJson());
        assertNotNull(fundingContributors.getContributor());
        assertEquals(2, fundingContributors.getContributor().size());
        assertNull(fundingContributors.getContributor().get(0).getContributorAttributes());
        assertNull(fundingContributors.getContributor().get(1).getContributorAttributes());
    }

    @Test
    public void testConvertFromWithMissingHostAndNulls() throws Exception {
        Mockito.when(mockContributorRoleConverter.toLegacyRoleName(Mockito.anyString())).thenReturn("LEAD");
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
                "\t\t\t\"contributorRole\": \"LEAD\"\n" +
                "\t\t}\n" +
                "\t}]\n" +
                "}";

        FundingContributors fundingContributors = fundingContributorsMapper.convertFrom(json);
        assertNotNull(fundingContributors);
        assertEquals(1, fundingContributors.getContributor().size());
        FundingContributor c = fundingContributors.getContributor().get(0);
        assertNotNull(c.getContributorOrcid());
        assertEquals("qa.orcid.org", c.getContributorOrcid().getHost());
        assertEquals("0009-0000-7948-587X", c.getContributorOrcid().getPath());
        assertNotNull(c.getCreditName());
        assertEquals("Test Author", c.getCreditName().getContent());
        assertNull(c.getContributorEmail());
        assertNotNull(c.getContributorAttributes());
        assertEquals(FundingContributorRole.LEAD, c.getContributorAttributes().getContributorRole());

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

        FundingContributors fundingContributors = fundingContributorsMapper.convertFrom(json);
        assertNotNull(fundingContributors);
        assertEquals(1, fundingContributors.getContributor().size());
        FundingContributor c = fundingContributors.getContributor().get(0);
        assertNull(c.getContributorOrcid());
        assertNull(c.getCreditName());
        assertNull(c.getContributorEmail());
        assertNull(c.getContributorAttributes());

        jakarta.xml.bind.JAXBContext context = jakarta.xml.bind.JAXBContext.newInstance(FundingContributors.class);
        java.io.StringWriter writer = new java.io.StringWriter();
        context.createMarshaller().marshal(fundingContributors, writer);
        assertNotNull(writer.toString());
    }

    private FundingContributors getFundingContributors() {
        FundingContributors fundingContributors = new FundingContributors();
        fundingContributors.getContributor().add(getFundingContributor(FundingContributorRole.LEAD));
        fundingContributors.getContributor().add(getFundingContributor(FundingContributorRole.CO_LEAD));
        return fundingContributors;
    }

    private FundingContributor getFundingContributor(FundingContributorRole contributorRole) {
        FundingContributor contributor = new FundingContributor();
        contributor.setContributorOrcid(new ContributorOrcid("orcid"));
        contributor.setCreditName(new CreditName("creditName"));
        FundingContributorAttributes contributorAttributes = new FundingContributorAttributes();
        contributorAttributes.setContributorRole(contributorRole);
        contributor.setContributorAttributes(contributorAttributes);
        return contributor;
    }

    private String getFundingContributorsJson() {
        return "{\"contributor\":[{\"contributorOrcid\":{\"uri\":\"https://orcid.org/0000-0001-5109-3700\",\"path\":\"0000-0001-5109-3700\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"Laure L. Haak\"},\"contributorEmail\":null,\"contributorAttributes\":{\"contributorRole\":\"LEAD\"}},{\"contributorOrcid\":{\"uri\":\"https://orcid.org/0000-0001-5109-3700\",\"path\":\"0000-0001-5109-3700\",\"host\":\"orcid.org\"},\"creditName\":{\"content\":\"Laure L. Haak\"},\"contributorEmail\":null,\"contributorAttributes\":{\"contributorRole\":\"CO_LEAD\"}}]}";
    }
}
