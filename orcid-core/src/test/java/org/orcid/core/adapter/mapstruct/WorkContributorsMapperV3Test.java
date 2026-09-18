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
import org.orcid.core.adapter.mapstruct.WorkContributorsMapperV3;

public class WorkContributorsMapperV3Test {

    @Mock
    private ContributorRoleConverter mockContributorRoleConverter;

    @InjectMocks
    private WorkContributorsMapperV3 workContributorsMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testConvertToWithNullSource() {
        assertNull(workContributorsMapper.convertTo(null));
    }

    @Test
    public void testConvertFromWithNullOrEmptySource() {
        assertNull(workContributorsMapper.convertFrom(null));
        assertNull(workContributorsMapper.convertFrom(""));
        assertNull(workContributorsMapper.convertFrom("   "));
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
        String json = workContributorsMapper.convertTo(workContributors);
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
        workContributorsMapper.convertTo(workContributors);
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
        WorkContributors workContributors = workContributorsMapper.convertFrom(getWorkContributorsJson());
        assertNotNull(workContributors.getContributor());
        assertEquals(2, workContributors.getContributor().size());
        assertEquals("some-value", workContributors.getContributor().get(0).getContributorAttributes().getContributorRole());
        assertEquals("some-value", workContributors.getContributor().get(1).getContributorAttributes().getContributorRole());
        
        Mockito.verify(mockContributorRoleConverter, Mockito.times(2)).toRoleValue(Mockito.anyString());
    }

    @Test
    public void testConvertFromWithMissingHostAndNulls() throws Exception {
        Mockito.when(mockContributorRoleConverter.toRoleValue(Mockito.anyString())).thenReturn("http://credit.niso.org/contributor-roles/data-curation/");
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
                "\t\t\t\"contributorSequence\": null,\n" +
                "\t\t\t\"contributorRole\": \"http://credit.niso.org/contributor-roles/data-curation/\"\n" +
                "\t\t}\n" +
                "\t}]\n" +
                "}";

        WorkContributors workContributors = workContributorsMapper.convertFrom(json);
        assertNotNull(workContributors);
        assertEquals(1, workContributors.getContributor().size());
        Contributor c = workContributors.getContributor().get(0);
        assertNotNull(c.getContributorOrcid());
        assertEquals("qa.orcid.org", c.getContributorOrcid().getHost());
        assertEquals("0009-0000-7948-587X", c.getContributorOrcid().getPath());
        assertEquals("https://qa.orcid.org/0009-0000-7948-587X", c.getContributorOrcid().getUri());
        assertNotNull(c.getCreditName());
        assertEquals("Test Author", c.getCreditName().getContent());
        assertNull(c.getContributorEmail());
        assertNotNull(c.getContributorAttributes());
        assertEquals("http://credit.niso.org/contributor-roles/data-curation/", c.getContributorAttributes().getContributorRole());
        assertNull(c.getContributorAttributes().getContributorSequence());

        // Verify XML marshalling succeeds
        jakarta.xml.bind.JAXBContext context = jakarta.xml.bind.JAXBContext.newInstance(WorkContributors.class);
        java.io.StringWriter writer = new java.io.StringWriter();
        context.createMarshaller().marshal(workContributors, writer);
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
                "\t\t\t\"contributorSequence\": null,\n" +
                "\t\t\t\"contributorRole\": null\n" +
                "\t\t}\n" +
                "\t}]\n" +
                "}";

        WorkContributors workContributors = workContributorsMapper.convertFrom(json);
        assertNotNull(workContributors);
        assertEquals(1, workContributors.getContributor().size());
        Contributor c = workContributors.getContributor().get(0);
        assertNull(c.getContributorOrcid());
        assertNull(c.getCreditName());
        assertNull(c.getContributorEmail());
        assertNull(c.getContributorAttributes());

        // Verify XML marshalling succeeds without AccessorException
        jakarta.xml.bind.JAXBContext context = jakarta.xml.bind.JAXBContext.newInstance(WorkContributors.class);
        java.io.StringWriter writer = new java.io.StringWriter();
        context.createMarshaller().marshal(workContributors, writer);
        assertNotNull(writer.toString());
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
