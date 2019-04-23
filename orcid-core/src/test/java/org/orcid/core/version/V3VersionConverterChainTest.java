package org.orcid.core.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.GregorianCalendar;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.ResourceType;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class V3VersionConverterChainTest {

    @Resource
    private V3VersionConverterChain v3VersionConverterChain;

    @Test
    public void upgradeRC1ToRC2Test() {
        XMLGregorianCalendar gc1 = DateUtils.convertToXMLGregorianCalendar(new GregorianCalendar(2018, 1, 1));
        XMLGregorianCalendar gc2 = DateUtils.convertToXMLGregorianCalendar(new GregorianCalendar(2019, 1, 1));

        // Work test
        org.orcid.jaxb.model.v3.rc1.record.Work rc1Work = new org.orcid.jaxb.model.v3.rc1.record.Work();
        rc1Work.setCountry(new org.orcid.jaxb.model.v3.rc1.common.Country(org.orcid.jaxb.model.v3.rc1.common.Iso3166Country.US));
        rc1Work.setCreatedDate(new org.orcid.jaxb.model.v3.rc1.common.CreatedDate(gc1));

        rc1Work.setJournalTitle(new org.orcid.jaxb.model.v3.rc1.common.Title("Journal title"));
        rc1Work.setLanguageCode("en");
        rc1Work.setLastModifiedDate(new org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate(gc2));
        rc1Work.setPath("/0000-0000-0000-0000/rcX/work/123");

        rc1Work.setPublicationDate(new org.orcid.jaxb.model.v3.rc1.common.PublicationDate(new org.orcid.jaxb.model.v3.rc1.common.Year(2018),
                new org.orcid.jaxb.model.v3.rc1.common.Month(1), new org.orcid.jaxb.model.v3.rc1.common.Day(1)));

        rc1Work.setPutCode(123L);
        rc1Work.setShortDescription("Short description");
        rc1Work.setSource(new org.orcid.jaxb.model.v3.rc1.common.Source("0000-0000-0000-0000"));
        rc1Work.setUrl(new org.orcid.jaxb.model.v3.rc1.common.Url("http://www.orcid.org"));
        rc1Work.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED);

        rc1Work.setWorkCitation(
                new org.orcid.jaxb.model.v3.rc1.record.Citation("This is the citation", org.orcid.jaxb.model.v3.rc1.record.CitationType.FORMATTED_UNSPECIFIED));
        org.orcid.jaxb.model.v3.rc1.common.Contributor c = new org.orcid.jaxb.model.v3.rc1.common.Contributor();
        org.orcid.jaxb.model.v3.rc1.common.ContributorAttributes ca = new org.orcid.jaxb.model.v3.rc1.common.ContributorAttributes();
        ca.setContributorRole(org.orcid.jaxb.model.v3.rc1.common.ContributorRole.ASSIGNEE);
        ca.setContributorSequence(org.orcid.jaxb.model.v3.rc1.record.SequenceType.ADDITIONAL);
        c.setContributorAttributes(ca);
        c.setContributorEmail(new org.orcid.jaxb.model.v3.rc1.common.ContributorEmail("contributor@orcid.org"));
        c.setContributorOrcid(new org.orcid.jaxb.model.v3.rc1.common.ContributorOrcid("0000-0000-0000-0000"));
        c.setCreditName(new org.orcid.jaxb.model.v3.rc1.common.CreditName("Credit Name"));
        org.orcid.jaxb.model.v3.rc1.record.WorkContributors wc = new org.orcid.jaxb.model.v3.rc1.record.WorkContributors();
        wc.getContributor().add(c);
        rc1Work.setWorkContributors(wc);
        org.orcid.jaxb.model.v3.rc1.record.ExternalIDs extIds = new org.orcid.jaxb.model.v3.rc1.record.ExternalIDs();
        org.orcid.jaxb.model.v3.rc1.record.ExternalID extId = new org.orcid.jaxb.model.v3.rc1.record.ExternalID();
        extId.setRelationship(org.orcid.jaxb.model.v3.rc1.record.Relationship.SELF);
        extId.setType("type");
        extId.setUrl(new org.orcid.jaxb.model.v3.rc1.common.Url("http://www.orcid.org"));
        extId.setValue("extId1");
        extIds.getExternalIdentifier().add(extId);
        rc1Work.setWorkExternalIdentifiers(extIds);
        org.orcid.jaxb.model.v3.rc1.record.WorkTitle title = new org.orcid.jaxb.model.v3.rc1.record.WorkTitle();
        title.setSubtitle(new org.orcid.jaxb.model.v3.rc1.common.Subtitle("The subtitle"));
        title.setTitle(new org.orcid.jaxb.model.v3.rc1.common.Title("The title"));
        title.setTranslatedTitle(new org.orcid.jaxb.model.v3.rc1.common.TranslatedTitle("Translated", "en"));
        rc1Work.setWorkTitle(title);
        rc1Work.setWorkType(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION);

        // Map and test
        org.orcid.jaxb.model.v3.rc2.record.Work rc2Work = (org.orcid.jaxb.model.v3.rc2.record.Work) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc1Work, "3.0_rc1"), "3.0_rc2")).getObjectToConvert();

        assertNotNull(rc2Work);
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, rc2Work.getCountry().getValue());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.CreatedDate(gc1), rc2Work.getCreatedDate());
        assertEquals("Journal title", rc2Work.getJournalTitle().getContent());
        assertEquals("en", rc2Work.getLanguageCode());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate(gc2), rc2Work.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0000/rcX/work/123", rc2Work.getPath());
        assertEquals("2018", rc2Work.getPublicationDate().getYear().getValue());
        assertEquals("01", rc2Work.getPublicationDate().getMonth().getValue());
        assertEquals("01", rc2Work.getPublicationDate().getDay().getValue());
        assertEquals(Long.valueOf(123), rc2Work.getPutCode());
        assertEquals("Short description", rc2Work.getShortDescription());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.Source("0000-0000-0000-0000"), rc2Work.getSource());
        assertEquals("http://www.orcid.org", rc2Work.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED, rc2Work.getVisibility());
        assertEquals("This is the citation", rc2Work.getWorkCitation().getCitation());
        assertEquals(org.orcid.jaxb.model.common.CitationType.FORMATTED_UNSPECIFIED, rc2Work.getWorkCitation().getWorkCitationType());
        assertEquals(1, rc2Work.getWorkContributors().getContributor().size());

        assertEquals(org.orcid.jaxb.model.common.ContributorRole.ASSIGNEE,
                rc2Work.getWorkContributors().getContributor().get(0).getContributorAttributes().getContributorRole());
        assertEquals(org.orcid.jaxb.model.common.SequenceType.ADDITIONAL,
                rc2Work.getWorkContributors().getContributor().get(0).getContributorAttributes().getContributorSequence());
        assertEquals("contributor@orcid.org", rc2Work.getWorkContributors().getContributor().get(0).getContributorEmail().getValue());
        assertEquals("0000-0000-0000-0000", rc2Work.getWorkContributors().getContributor().get(0).getContributorOrcid().getPath());
        assertEquals("Credit Name", rc2Work.getWorkContributors().getContributor().get(0).getCreditName().getContent());
        assertEquals(1, rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(org.orcid.jaxb.model.common.Relationship.SELF, rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("type", rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://www.orcid.org", rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("The subtitle", rc2Work.getWorkTitle().getSubtitle().getContent());
        assertEquals("The title", rc2Work.getWorkTitle().getTitle().getContent());
        assertEquals("Translated", rc2Work.getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("en", rc2Work.getWorkTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS, rc2Work.getWorkType());

        rc2Work = null;

        // Work summary test
        org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary rc1WorkSummary = new org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary();
        rc1WorkSummary.setCreatedDate(new org.orcid.jaxb.model.v3.rc1.common.CreatedDate(gc1));
        rc1WorkSummary.setDisplayIndex("1");
        rc1WorkSummary.setExternalIdentifiers(extIds);
        rc1WorkSummary.setJournalTitle(new org.orcid.jaxb.model.v3.rc1.common.Title("Journal title"));
        rc1WorkSummary.setLastModifiedDate(new org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate(gc2));
        rc1WorkSummary.setPath("/0000-0000-0000-0000/rcX/work/123");
        rc1WorkSummary.setPublicationDate(new org.orcid.jaxb.model.v3.rc1.common.PublicationDate(new org.orcid.jaxb.model.v3.rc1.common.Year(2018),
                new org.orcid.jaxb.model.v3.rc1.common.Month(1), new org.orcid.jaxb.model.v3.rc1.common.Day(1)));
        rc1WorkSummary.setPutCode(123L);
        rc1WorkSummary.setSource(new org.orcid.jaxb.model.v3.rc1.common.Source("0000-0000-0000-0000"));
        rc1WorkSummary.setTitle(title);
        rc1WorkSummary.setType(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION);
        rc1WorkSummary.setUrl(new org.orcid.jaxb.model.v3.rc1.common.Url("http://www.orcid.org"));
        rc1WorkSummary.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED);

        org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary rc2WorkSummary = (org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc1WorkSummary, "3.0_rc1"), "3.0_rc2")).getObjectToConvert();

        assertNotNull(rc2WorkSummary);
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.CreatedDate(gc1), rc2WorkSummary.getCreatedDate());
        assertEquals("1", rc2WorkSummary.getDisplayIndex());
        assertEquals(1, rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(org.orcid.jaxb.model.common.Relationship.SELF, rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("type", rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://www.orcid.org", rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("Journal title", rc2WorkSummary.getJournalTitle().getContent());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate(gc2), rc2WorkSummary.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0000/rcX/work/123", rc2WorkSummary.getPath());
        assertEquals("2018", rc2WorkSummary.getPublicationDate().getYear().getValue());
        assertEquals("01", rc2WorkSummary.getPublicationDate().getMonth().getValue());
        assertEquals("01", rc2WorkSummary.getPublicationDate().getDay().getValue());
        assertEquals(Long.valueOf(123), rc2WorkSummary.getPutCode());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.Source("0000-0000-0000-0000"), rc2WorkSummary.getSource());
        assertEquals("The subtitle", rc2WorkSummary.getTitle().getSubtitle().getContent());
        assertEquals("The title", rc2WorkSummary.getTitle().getTitle().getContent());
        assertEquals("Translated", rc2WorkSummary.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", rc2WorkSummary.getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS, rc2WorkSummary.getType());
        assertEquals("http://www.orcid.org", rc2WorkSummary.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED, rc2WorkSummary.getVisibility());
    }

    @Test
    public void downgradeRC2ToRC1ResearchResourceAndResearchResourceSummaryTest() {
        // Research resource
        org.orcid.jaxb.model.v3.rc2.record.ResearchResource rrRc2 = new org.orcid.jaxb.model.v3.rc2.record.ResearchResource();
        org.orcid.jaxb.model.v3.rc2.record.ResearchResourceProposal rp = new org.orcid.jaxb.model.v3.rc2.record.ResearchResourceProposal();
        rrRc2.setProposal(rp);
        rp.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://orcid.org"));
        rp.setTitle(new org.orcid.jaxb.model.v3.rc2.record.ResearchResourceTitle());
        rp.getTitle().setTitle(new org.orcid.jaxb.model.v3.rc2.common.Title("Research resource"));
        rp.getTitle().setTranslatedTitle(new org.orcid.jaxb.model.v3.rc2.common.TranslatedTitle("translatedTitle","en"));
        org.orcid.jaxb.model.v3.rc2.record.ExternalIDs extIds = new org.orcid.jaxb.model.v3.rc2.record.ExternalIDs();
        org.orcid.jaxb.model.v3.rc2.record.ExternalID extId1 = new org.orcid.jaxb.model.v3.rc2.record.ExternalID();
        extId1.setRelationship(Relationship.SELF);
        extId1.setType("doi");
        extId1.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://orcid.org"));        
        extId1.setValue("extId1");               
        extIds.getExternalIdentifier().add(extId1);
        org.orcid.jaxb.model.v3.rc2.record.ExternalID extId2 = new org.orcid.jaxb.model.v3.rc2.record.ExternalID();
        extId2.setRelationship(Relationship.VERSION_OF);
        extId2.setType("doi");
        extId2.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://orcid.org"));        
        extId2.setValue("extId2");               
        extIds.getExternalIdentifier().add(extId2);
        org.orcid.jaxb.model.v3.rc2.record.ExternalID extId3 = new org.orcid.jaxb.model.v3.rc2.record.ExternalID();
        extId3.setRelationship(Relationship.PART_OF);
        extId3.setType("doi");
        extId3.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://orcid.org"));        
        extId3.setValue("extId3");               
        extIds.getExternalIdentifier().add(extId3);
        rp.setExternalIdentifiers(extIds);
        rp.setEndDate(new org.orcid.jaxb.model.v3.rc2.common.FuzzyDate(new org.orcid.jaxb.model.v3.rc2.common.Year(2012),new org.orcid.jaxb.model.v3.rc2.common.Month(1),new org.orcid.jaxb.model.v3.rc2.common.Day(1)));
        rp.setStartDate(new org.orcid.jaxb.model.v3.rc2.common.FuzzyDate(new org.orcid.jaxb.model.v3.rc2.common.Year(2011),new org.orcid.jaxb.model.v3.rc2.common.Month(1),new org.orcid.jaxb.model.v3.rc2.common.Day(1)));
        org.orcid.jaxb.model.v3.rc2.common.Organization org1 = new org.orcid.jaxb.model.v3.rc2.common.Organization();
        org1.setName("orgName1");
        org.orcid.jaxb.model.v3.rc2.common.OrganizationAddress address = new org.orcid.jaxb.model.v3.rc2.common.OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org1.setAddress(address);
        org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization disambiguatedOrg = new org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("def456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        org1.setDisambiguatedOrganization(disambiguatedOrg);
        org.orcid.jaxb.model.v3.rc2.common.Organization org2 = new org.orcid.jaxb.model.v3.rc2.common.Organization();
        org2.setName("orgName2");
        org2.setAddress(address);
        org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization disambiguatedOrg2 = new org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization();
        disambiguatedOrg2.setDisambiguatedOrganizationIdentifier("def456");
        disambiguatedOrg2.setDisambiguationSource("WDB");
        org2.setDisambiguatedOrganization(disambiguatedOrg2);
        rp.getHosts().getOrganization().add(org1);
        rp.getHosts().getOrganization().add(org2);
        rrRc2.getResourceItems().add(generateResearchResourceItem("item1"));
        
        // Map and test
        org.orcid.jaxb.model.v3.rc1.record.ResearchResource rrRc1 = (org.orcid.jaxb.model.v3.rc1.record.ResearchResource) (v3VersionConverterChain
                .downgrade(new V3Convertible(rrRc2, "3.0_rc2"), "3.0_rc1")).getObjectToConvert();
        assertNotNull(rrRc1);
        assertNull(rrRc1.getPath());
        assertNotNull(rrRc1.getProposal());
        assertNotNull(rrRc1.getProposal().getEndDate());
        assertEquals("01", rrRc1.getProposal().getEndDate().getDay().getValue());
        assertEquals("01", rrRc1.getProposal().getEndDate().getMonth().getValue());
        assertEquals("2012", rrRc1.getProposal().getEndDate().getYear().getValue());
        assertNotNull(rrRc1.getProposal().getExternalIdentifiers());
        assertEquals(2, rrRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().size());        
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.SELF, rrRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("doi", rrRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://orcid.org", rrRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", rrRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getValue());        
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.PART_OF, rrRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());
        assertEquals("doi", rrRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("http://orcid.org", rrRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("extId3", rrRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(1).getValue());
        assertNotNull(rrRc1.getProposal().getHosts());
        assertEquals(2, rrRc1.getProposal().getHosts().getOrganization().size());
        assertEquals("city", rrRc1.getProposal().getHosts().getOrganization().get(0).getAddress().getCity());
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Iso3166Country.US, rrRc1.getProposal().getHosts().getOrganization().get(0).getAddress().getCountry());
        assertNotNull(rrRc1.getProposal().getHosts().getOrganization().get(0).getDisambiguatedOrganization());
        assertEquals("def456", rrRc1.getProposal().getHosts().getOrganization().get(0).getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
        assertEquals("WDB", rrRc1.getProposal().getHosts().getOrganization().get(0).getDisambiguatedOrganization().getDisambiguationSource());
        assertEquals("orgName1", rrRc1.getProposal().getHosts().getOrganization().get(0).getName());
        assertEquals("city", rrRc1.getProposal().getHosts().getOrganization().get(1).getAddress().getCity());
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Iso3166Country.US, rrRc1.getProposal().getHosts().getOrganization().get(1).getAddress().getCountry());
        assertNotNull(rrRc1.getProposal().getHosts().getOrganization().get(1).getDisambiguatedOrganization());
        assertEquals("def456", rrRc1.getProposal().getHosts().getOrganization().get(1).getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
        assertEquals("WDB", rrRc1.getProposal().getHosts().getOrganization().get(1).getDisambiguatedOrganization().getDisambiguationSource());
        assertEquals("orgName2", rrRc1.getProposal().getHosts().getOrganization().get(1).getName());        
        assertNotNull(rrRc1.getProposal().getStartDate());
        assertEquals("01", rrRc1.getProposal().getStartDate().getDay().getValue());
        assertEquals("01", rrRc1.getProposal().getStartDate().getMonth().getValue());
        assertEquals("2011", rrRc1.getProposal().getStartDate().getYear().getValue());
        assertNotNull(rrRc1.getProposal().getTitle());
        assertNotNull(rrRc1.getProposal().getTitle().getTitle());
        assertNotNull(rrRc1.getProposal().getTitle().getTranslatedTitle());
        assertEquals("Research resource", rrRc1.getProposal().getTitle().getTitle().getContent());
        assertEquals("translatedTitle", rrRc1.getProposal().getTitle().getTranslatedTitle().getContent());
        assertEquals("en", rrRc1.getProposal().getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("http://orcid.org", rrRc1.getProposal().getUrl().getValue());
        assertNotNull(rrRc1.getResourceItems());
        assertEquals(1, rrRc1.getResourceItems().size());
        assertEquals(2, rrRc1.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.SELF, rrRc1.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("doi", rrRc1.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://orcid.org", rrRc1.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", rrRc1.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.PART_OF, rrRc1.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());
        assertEquals("doi", rrRc1.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("http://orcid.org", rrRc1.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("extId3", rrRc1.getResourceItems().get(0).getExternalIdentifiers().getExternalIdentifier().get(1).getValue());
        assertNotNull(rrRc1.getResourceItems().get(0).getHosts());
        assertEquals(2, rrRc1.getResourceItems().get(0).getHosts().getOrganization().size());        
        assertEquals("city", rrRc1.getResourceItems().get(0).getHosts().getOrganization().get(0).getAddress().getCity());
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Iso3166Country.US, rrRc1.getResourceItems().get(0).getHosts().getOrganization().get(0).getAddress().getCountry());
        assertEquals("region", rrRc1.getResourceItems().get(0).getHosts().getOrganization().get(0).getAddress().getRegion());
        assertEquals("def456", rrRc1.getResourceItems().get(0).getHosts().getOrganization().get(0).getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
        assertEquals("WDB", rrRc1.getResourceItems().get(0).getHosts().getOrganization().get(0).getDisambiguatedOrganization().getDisambiguationSource());
        assertEquals("orgName", rrRc1.getResourceItems().get(0).getHosts().getOrganization().get(0).getName());        
        assertEquals("city", rrRc1.getResourceItems().get(0).getHosts().getOrganization().get(1).getAddress().getCity());
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Iso3166Country.US, rrRc1.getResourceItems().get(0).getHosts().getOrganization().get(1).getAddress().getCountry());
        assertEquals("region", rrRc1.getResourceItems().get(0).getHosts().getOrganization().get(1).getAddress().getRegion());
        assertEquals("def4567", rrRc1.getResourceItems().get(0).getHosts().getOrganization().get(1).getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
        assertEquals("WDBA", rrRc1.getResourceItems().get(0).getHosts().getOrganization().get(1).getDisambiguatedOrganization().getDisambiguationSource());
        assertEquals("orgName2", rrRc1.getResourceItems().get(0).getHosts().getOrganization().get(1).getName());        
        assertEquals("item1", rrRc1.getResourceItems().get(0).getResourceName());
        assertEquals("infrastructures", rrRc1.getResourceItems().get(0).getResourceType());
        assertEquals("http://orcid.org", rrRc1.getResourceItems().get(0).getUrl().getValue());         
        
        
        // Summary
        org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceSummary rrsRc2 = new org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceSummary();
        rrsRc2.setProposal(rp);
        
        // Map and test
        org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary rrsRc1 = (org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary) (v3VersionConverterChain
                .downgrade(new V3Convertible(rrsRc2, "3.0_rc2"), "3.0_rc1")).getObjectToConvert();
        
        assertNotNull(rrsRc1);
        assertNull(rrsRc1.getPath());
        assertNotNull(rrsRc1.getProposal());
        assertNotNull(rrsRc1.getProposal().getEndDate());
        assertEquals("01", rrsRc1.getProposal().getEndDate().getDay().getValue());
        assertEquals("01", rrsRc1.getProposal().getEndDate().getMonth().getValue());
        assertEquals("2012", rrsRc1.getProposal().getEndDate().getYear().getValue());
        assertNotNull(rrsRc1.getProposal().getExternalIdentifiers());
        assertEquals(2, rrsRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().size());        
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.SELF, rrsRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("doi", rrsRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://orcid.org", rrsRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", rrsRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getValue());        
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.PART_OF, rrsRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());
        assertEquals("doi", rrsRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("http://orcid.org", rrsRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("extId3", rrsRc1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(1).getValue());
        assertNotNull(rrsRc1.getProposal().getHosts());
        assertEquals(2, rrsRc1.getProposal().getHosts().getOrganization().size());
        assertEquals("city", rrsRc1.getProposal().getHosts().getOrganization().get(0).getAddress().getCity());
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Iso3166Country.US, rrsRc1.getProposal().getHosts().getOrganization().get(0).getAddress().getCountry());
        assertNotNull(rrsRc1.getProposal().getHosts().getOrganization().get(0).getDisambiguatedOrganization());
        assertEquals("def456", rrsRc1.getProposal().getHosts().getOrganization().get(0).getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
        assertEquals("WDB", rrsRc1.getProposal().getHosts().getOrganization().get(0).getDisambiguatedOrganization().getDisambiguationSource());
        assertEquals("orgName1", rrsRc1.getProposal().getHosts().getOrganization().get(0).getName());
        assertEquals("city", rrsRc1.getProposal().getHosts().getOrganization().get(1).getAddress().getCity());
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Iso3166Country.US, rrsRc1.getProposal().getHosts().getOrganization().get(1).getAddress().getCountry());
        assertNotNull(rrsRc1.getProposal().getHosts().getOrganization().get(1).getDisambiguatedOrganization());
        assertEquals("def456", rrsRc1.getProposal().getHosts().getOrganization().get(1).getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
        assertEquals("WDB", rrsRc1.getProposal().getHosts().getOrganization().get(1).getDisambiguatedOrganization().getDisambiguationSource());
        assertEquals("orgName2", rrsRc1.getProposal().getHosts().getOrganization().get(1).getName());        
        assertNotNull(rrsRc1.getProposal().getStartDate());
        assertEquals("01", rrsRc1.getProposal().getStartDate().getDay().getValue());
        assertEquals("01", rrsRc1.getProposal().getStartDate().getMonth().getValue());
        assertEquals("2011", rrsRc1.getProposal().getStartDate().getYear().getValue());
        assertNotNull(rrsRc1.getProposal().getTitle());
        assertNotNull(rrsRc1.getProposal().getTitle().getTitle());
        assertNotNull(rrsRc1.getProposal().getTitle().getTranslatedTitle());
        assertEquals("Research resource", rrsRc1.getProposal().getTitle().getTitle().getContent());
        assertEquals("translatedTitle", rrsRc1.getProposal().getTitle().getTranslatedTitle().getContent());
        assertEquals("en", rrsRc1.getProposal().getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("http://orcid.org", rrsRc1.getProposal().getUrl().getValue());
    }
    
    private org.orcid.jaxb.model.v3.rc2.record.ResearchResourceItem generateResearchResourceItem(String title){
        org.orcid.jaxb.model.v3.rc2.record.ResearchResourceItem ri1 = new org.orcid.jaxb.model.v3.rc2.record.ResearchResourceItem();
        ri1.setResourceName(title);
        ri1.setResourceType(ResourceType.valueOf("infrastructures"));
        ri1.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://orcid.org")); 
        
        org.orcid.jaxb.model.v3.rc2.common.Organization org1 = new org.orcid.jaxb.model.v3.rc2.common.Organization();
        org1.setName("orgName");
        org.orcid.jaxb.model.v3.rc2.common.OrganizationAddress address = new org.orcid.jaxb.model.v3.rc2.common.OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        address.setRegion("region");
        org1.setAddress(address);
        org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization disambiguatedOrg = new org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("def456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        org1.setDisambiguatedOrganization(disambiguatedOrg);
        org.orcid.jaxb.model.v3.rc2.common.Organization org2 = new org.orcid.jaxb.model.v3.rc2.common.Organization();
        org2.setName("orgName2");
        org2.setAddress(address);
        org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization disambiguatedOrg2 = new org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization();
        disambiguatedOrg2.setDisambiguatedOrganizationIdentifier("def4567");
        disambiguatedOrg2.setDisambiguationSource("WDBA");
        org2.setDisambiguatedOrganization(disambiguatedOrg2);
        ri1.getHosts().getOrganization().add(org1);
        ri1.getHosts().getOrganization().add(org2);
        
        org.orcid.jaxb.model.v3.rc2.record.ExternalIDs extIds = new org.orcid.jaxb.model.v3.rc2.record.ExternalIDs();
        org.orcid.jaxb.model.v3.rc2.record.ExternalID extId1 = new org.orcid.jaxb.model.v3.rc2.record.ExternalID();
        extId1.setRelationship(Relationship.SELF);
        extId1.setType("doi");
        extId1.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://orcid.org"));        
        extId1.setValue("extId1");               
        extIds.getExternalIdentifier().add(extId1);
        
        org.orcid.jaxb.model.v3.rc2.record.ExternalID extId2 = new org.orcid.jaxb.model.v3.rc2.record.ExternalID();
        extId2.setRelationship(Relationship.VERSION_OF);
        extId2.setType("doi");
        extId2.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://orcid.org"));        
        extId2.setValue("extId2");               
        extIds.getExternalIdentifier().add(extId2);
        
        org.orcid.jaxb.model.v3.rc2.record.ExternalID extId3 = new org.orcid.jaxb.model.v3.rc2.record.ExternalID();
        extId3.setRelationship(Relationship.PART_OF);
        extId3.setType("doi");
        extId3.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://orcid.org"));        
        extId3.setValue("extId3");               
        extIds.getExternalIdentifier().add(extId3);
        ri1.setExternalIdentifiers(extIds);
        
        return ri1;
    }
    
    @Test
    public void downgradeRC2ToRC1WorksAndWorkSummaryTest() {
        XMLGregorianCalendar gc1 = DateUtils.convertToXMLGregorianCalendar(new GregorianCalendar(2018, 1, 1));
        XMLGregorianCalendar gc2 = DateUtils.convertToXMLGregorianCalendar(new GregorianCalendar(2019, 1, 1));

        // Work test
        org.orcid.jaxb.model.v3.rc2.record.Work rc2Work = new org.orcid.jaxb.model.v3.rc2.record.Work();
        rc2Work.setCountry(new org.orcid.jaxb.model.v3.rc2.common.Country(org.orcid.jaxb.model.common.Iso3166Country.US));
        rc2Work.setCreatedDate(new org.orcid.jaxb.model.v3.rc2.common.CreatedDate(gc1));
        rc2Work.setJournalTitle(new org.orcid.jaxb.model.v3.rc2.common.Title("Journal title"));
        rc2Work.setLanguageCode("en");
        rc2Work.setLastModifiedDate(new org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate(gc2));
        rc2Work.setPath("/0000-0000-0000-0000/rcX/work/123");
        rc2Work.setPublicationDate(new org.orcid.jaxb.model.v3.rc2.common.PublicationDate(new org.orcid.jaxb.model.v3.rc2.common.Year(2018),
                new org.orcid.jaxb.model.v3.rc2.common.Month(1), new org.orcid.jaxb.model.v3.rc2.common.Day(1)));
        rc2Work.setPutCode(123L);
        rc2Work.setShortDescription("Short description");
        rc2Work.setSource(new org.orcid.jaxb.model.v3.rc2.common.Source("0000-0000-0000-0000"));
        rc2Work.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://www.orcid.org"));
        rc2Work.setVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED);
        rc2Work.setWorkCitation(new org.orcid.jaxb.model.v3.rc2.record.Citation("This is the citation", org.orcid.jaxb.model.common.CitationType.FORMATTED_UNSPECIFIED));
        org.orcid.jaxb.model.v3.rc2.common.Contributor c = new org.orcid.jaxb.model.v3.rc2.common.Contributor();
        org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes ca = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes();
        ca.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.ASSIGNEE);
        ca.setContributorSequence(org.orcid.jaxb.model.common.SequenceType.ADDITIONAL);
        c.setContributorAttributes(ca);
        c.setContributorEmail(new org.orcid.jaxb.model.v3.rc2.common.ContributorEmail("contributor@orcid.org"));
        c.setContributorOrcid(new org.orcid.jaxb.model.v3.rc2.common.ContributorOrcid("0000-0000-0000-0000"));
        c.setCreditName(new org.orcid.jaxb.model.v3.rc2.common.CreditName("Credit Name"));
        org.orcid.jaxb.model.v3.rc2.record.WorkContributors wc = new org.orcid.jaxb.model.v3.rc2.record.WorkContributors();
        wc.getContributor().add(c);
        rc2Work.setWorkContributors(wc);
        rc2Work.setWorkExternalIdentifiers(getExternalIdentifiers());        
        
        org.orcid.jaxb.model.v3.rc2.record.WorkTitle title = new org.orcid.jaxb.model.v3.rc2.record.WorkTitle();
        title.setSubtitle(new org.orcid.jaxb.model.v3.rc2.common.Subtitle("The subtitle"));
        title.setTitle(new org.orcid.jaxb.model.v3.rc2.common.Title("The title"));
        title.setTranslatedTitle(new org.orcid.jaxb.model.v3.rc2.common.TranslatedTitle("Translated", "en"));
        rc2Work.setWorkTitle(title);
        rc2Work.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS);

        // Map and test
        org.orcid.jaxb.model.v3.rc1.record.Work rc1Work = (org.orcid.jaxb.model.v3.rc1.record.Work) (v3VersionConverterChain
                .downgrade(new V3Convertible(rc2Work, "3.0_rc2"), "3.0_rc1")).getObjectToConvert();

        assertNotNull(rc1Work);
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Iso3166Country.US, rc1Work.getCountry().getValue());
        assertEquals(new org.orcid.jaxb.model.v3.rc1.common.CreatedDate(gc1), rc1Work.getCreatedDate());
        assertEquals("Journal title", rc1Work.getJournalTitle().getContent());
        assertEquals("en", rc1Work.getLanguageCode());
        assertEquals(new org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate(gc2), rc1Work.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0000/rcX/work/123", rc1Work.getPath());
        assertEquals("2018", rc1Work.getPublicationDate().getYear().getValue());
        assertEquals("01", rc1Work.getPublicationDate().getMonth().getValue());
        assertEquals("01", rc1Work.getPublicationDate().getDay().getValue());
        assertEquals(Long.valueOf(123), rc1Work.getPutCode());
        assertEquals("Short description", rc1Work.getShortDescription());
        assertEquals(new org.orcid.jaxb.model.v3.rc1.common.Source("0000-0000-0000-0000"), rc1Work.getSource());
        assertEquals("http://www.orcid.org", rc1Work.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED, rc1Work.getVisibility());
        assertEquals("This is the citation", rc1Work.getWorkCitation().getCitation());
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.CitationType.FORMATTED_UNSPECIFIED, rc1Work.getWorkCitation().getWorkCitationType());
        assertEquals(1, rc1Work.getWorkContributors().getContributor().size());

        assertEquals(org.orcid.jaxb.model.v3.rc1.common.ContributorRole.ASSIGNEE,
                rc1Work.getWorkContributors().getContributor().get(0).getContributorAttributes().getContributorRole());
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.SequenceType.ADDITIONAL,
                rc1Work.getWorkContributors().getContributor().get(0).getContributorAttributes().getContributorSequence());

        assertEquals("contributor@orcid.org", rc1Work.getWorkContributors().getContributor().get(0).getContributorEmail().getValue());
        assertEquals("0000-0000-0000-0000", rc1Work.getWorkContributors().getContributor().get(0).getContributorOrcid().getPath());
        assertEquals("Credit Name", rc1Work.getWorkContributors().getContributor().get(0).getCreditName().getContent());
        assertEquals(2, rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.SELF, rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("type", rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://www.orcid.org", rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getValue());

        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.PART_OF, rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());
        assertEquals("type", rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("http://www.orcid.org", rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("extId3", rc1Work.getWorkExternalIdentifiers().getExternalIdentifier().get(1).getValue());

        assertEquals("The subtitle", rc1Work.getWorkTitle().getSubtitle().getContent());
        assertEquals("The title", rc1Work.getWorkTitle().getTitle().getContent());
        assertEquals("Translated", rc1Work.getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("en", rc1Work.getWorkTitle().getTranslatedTitle().getLanguageCode());

        assertEquals(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION, rc1Work.getWorkType());

        // Work summary test
        org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary rc2WorkSummary = new org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary();
        rc2WorkSummary.setCreatedDate(new org.orcid.jaxb.model.v3.rc2.common.CreatedDate(gc1));
        rc2WorkSummary.setDisplayIndex("1");
        rc2WorkSummary.setExternalIdentifiers(getExternalIdentifiers());
        rc2WorkSummary.setJournalTitle(new org.orcid.jaxb.model.v3.rc2.common.Title("Journal title"));
        rc2WorkSummary.setLastModifiedDate(new org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate(gc2));
        rc2WorkSummary.setPath("/0000-0000-0000-0000/rcX/work/123");
        rc2WorkSummary.setPublicationDate(new org.orcid.jaxb.model.v3.rc2.common.PublicationDate(new org.orcid.jaxb.model.v3.rc2.common.Year(2018),
                new org.orcid.jaxb.model.v3.rc2.common.Month(1), new org.orcid.jaxb.model.v3.rc2.common.Day(1)));
        rc2WorkSummary.setPutCode(123L);
        rc2WorkSummary.setSource(new org.orcid.jaxb.model.v3.rc2.common.Source("0000-0000-0000-0000"));
        rc2WorkSummary.setTitle(title);
        rc2WorkSummary.setType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS);
        rc2WorkSummary.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://www.orcid.org"));
        rc2WorkSummary.setVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED);

        org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary rc1WorkSummary = (org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary) (v3VersionConverterChain
                .downgrade(new V3Convertible(rc2WorkSummary, "3.0_rc2"), "3.0_rc1")).getObjectToConvert();
        assertNotNull(rc1WorkSummary);
        assertEquals(new org.orcid.jaxb.model.v3.rc1.common.CreatedDate(gc1), rc1WorkSummary.getCreatedDate());
        assertEquals("1", rc1WorkSummary.getDisplayIndex());
        assertEquals(2, rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.SELF, rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("type", rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://www.orcid.org", rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.Relationship.PART_OF, rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());
        assertEquals("type", rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("http://www.orcid.org", rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("extId3", rc1WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(1).getValue());
        
        assertEquals("Journal title", rc1WorkSummary.getJournalTitle().getContent());
        assertEquals(new org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate(gc2), rc1WorkSummary.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0000/rcX/work/123", rc1WorkSummary.getPath());
        assertEquals("2018", rc1WorkSummary.getPublicationDate().getYear().getValue());
        assertEquals("01", rc1WorkSummary.getPublicationDate().getMonth().getValue());
        assertEquals("01", rc1WorkSummary.getPublicationDate().getDay().getValue());
        assertEquals(Long.valueOf(123), rc1WorkSummary.getPutCode());
        assertEquals(new org.orcid.jaxb.model.v3.rc1.common.Source("0000-0000-0000-0000"), rc1WorkSummary.getSource());
        assertEquals("The subtitle", rc1WorkSummary.getTitle().getSubtitle().getContent());
        assertEquals("The title", rc1WorkSummary.getTitle().getTitle().getContent());
        assertEquals("Translated", rc1WorkSummary.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", rc1WorkSummary.getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(org.orcid.jaxb.model.v3.rc1.record.WorkType.DISSERTATION, rc1WorkSummary.getType());
        assertEquals("http://www.orcid.org", rc1WorkSummary.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.rc1.common.Visibility.LIMITED, rc1WorkSummary.getVisibility());
    }

    private org.orcid.jaxb.model.v3.rc2.record.ExternalIDs getExternalIdentifiers() {
        org.orcid.jaxb.model.v3.rc2.record.ExternalIDs extIds = new org.orcid.jaxb.model.v3.rc2.record.ExternalIDs();
        org.orcid.jaxb.model.v3.rc2.record.ExternalID extId1 = new org.orcid.jaxb.model.v3.rc2.record.ExternalID();
        extId1.setRelationship(org.orcid.jaxb.model.common.Relationship.SELF);
        extId1.setType("type");
        extId1.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://www.orcid.org"));
        extId1.setValue("extId1");
        extIds.getExternalIdentifier().add(extId1);
        org.orcid.jaxb.model.v3.rc2.record.ExternalID extId2 = new org.orcid.jaxb.model.v3.rc2.record.ExternalID();
        extId2.setRelationship(org.orcid.jaxb.model.common.Relationship.VERSION_OF);
        extId2.setType("type");
        extId2.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://www.orcid.org"));
        extId2.setValue("extId2");
        extIds.getExternalIdentifier().add(extId2);
        org.orcid.jaxb.model.v3.rc2.record.ExternalID extId3 = new org.orcid.jaxb.model.v3.rc2.record.ExternalID();
        extId3.setRelationship(org.orcid.jaxb.model.common.Relationship.PART_OF);
        extId3.setType("type");
        extId3.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://www.orcid.org"));
        extId3.setValue("extId3");
        extIds.getExternalIdentifier().add(extId3);
        return extIds;
    }
    
    @Test
    public void upgradeRC2ToReleaseTest() {
        XMLGregorianCalendar gc1 = DateUtils.convertToXMLGregorianCalendar(new GregorianCalendar(2018, 1, 1));
        XMLGregorianCalendar gc2 = DateUtils.convertToXMLGregorianCalendar(new GregorianCalendar(2019, 1, 1));

        // Work test
        org.orcid.jaxb.model.v3.rc2.record.Work rc2Work = new org.orcid.jaxb.model.v3.rc2.record.Work();
        rc2Work.setCountry(new org.orcid.jaxb.model.v3.rc2.common.Country(org.orcid.jaxb.model.common.Iso3166Country.US));
        rc2Work.setCreatedDate(new org.orcid.jaxb.model.v3.rc2.common.CreatedDate(gc1));

        rc2Work.setJournalTitle(new org.orcid.jaxb.model.v3.rc2.common.Title("Journal title"));
        rc2Work.setLanguageCode("en");
        rc2Work.setLastModifiedDate(new org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate(gc2));
        rc2Work.setPath("/0000-0000-0000-0000/rcX/work/123");

        rc2Work.setPublicationDate(new org.orcid.jaxb.model.v3.rc2.common.PublicationDate(new org.orcid.jaxb.model.v3.rc2.common.Year(2018),
                new org.orcid.jaxb.model.v3.rc2.common.Month(1), new org.orcid.jaxb.model.v3.rc2.common.Day(1)));

        rc2Work.setPutCode(123L);
        rc2Work.setShortDescription("Short description");
        rc2Work.setSource(new org.orcid.jaxb.model.v3.rc2.common.Source("0000-0000-0000-0000"));
        rc2Work.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://www.orcid.org"));
        rc2Work.setVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED);

        rc2Work.setWorkCitation(new org.orcid.jaxb.model.v3.rc2.record.Citation("This is the citation", org.orcid.jaxb.model.common.CitationType.FORMATTED_UNSPECIFIED));
        org.orcid.jaxb.model.v3.rc2.common.Contributor c = new org.orcid.jaxb.model.v3.rc2.common.Contributor();
        org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes ca = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes();
        ca.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.ASSIGNEE);
        ca.setContributorSequence(org.orcid.jaxb.model.common.SequenceType.ADDITIONAL);
        c.setContributorAttributes(ca);
        c.setContributorEmail(new org.orcid.jaxb.model.v3.rc2.common.ContributorEmail("contributor@orcid.org"));
        c.setContributorOrcid(new org.orcid.jaxb.model.v3.rc2.common.ContributorOrcid("0000-0000-0000-0000"));
        c.setCreditName(new org.orcid.jaxb.model.v3.rc2.common.CreditName("Credit Name"));
        org.orcid.jaxb.model.v3.rc2.record.WorkContributors wc = new org.orcid.jaxb.model.v3.rc2.record.WorkContributors();
        wc.getContributor().add(c);
        rc2Work.setWorkContributors(wc);
        org.orcid.jaxb.model.v3.rc2.record.ExternalIDs extIds = new org.orcid.jaxb.model.v3.rc2.record.ExternalIDs();
        org.orcid.jaxb.model.v3.rc2.record.ExternalID extId = new org.orcid.jaxb.model.v3.rc2.record.ExternalID();
        extId.setRelationship(org.orcid.jaxb.model.common.Relationship.SELF);
        extId.setType("type");
        extId.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://www.orcid.org"));
        extId.setValue("extId1");
        extIds.getExternalIdentifier().add(extId);
        rc2Work.setWorkExternalIdentifiers(extIds);
        org.orcid.jaxb.model.v3.rc2.record.WorkTitle title = new org.orcid.jaxb.model.v3.rc2.record.WorkTitle();
        title.setSubtitle(new org.orcid.jaxb.model.v3.rc2.common.Subtitle("The subtitle"));
        title.setTitle(new org.orcid.jaxb.model.v3.rc2.common.Title("The title"));
        title.setTranslatedTitle(new org.orcid.jaxb.model.v3.rc2.common.TranslatedTitle("Translated", "en"));
        rc2Work.setWorkTitle(title);
        rc2Work.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS);

        // Map and test
        org.orcid.jaxb.model.v3.release.record.Work releaseWork = (org.orcid.jaxb.model.v3.release.record.Work) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2Work, "3.0_rc2"), "3.0")).getObjectToConvert();

        assertNotNull(releaseWork);
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, releaseWork.getCountry().getValue());
        assertEquals(new org.orcid.jaxb.model.v3.release.common.CreatedDate(gc1), releaseWork.getCreatedDate());
        assertEquals("Journal title", releaseWork.getJournalTitle().getContent());
        assertEquals("en", releaseWork.getLanguageCode());
        assertEquals(new org.orcid.jaxb.model.v3.release.common.LastModifiedDate(gc2), releaseWork.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0000/rcX/work/123", releaseWork.getPath());
        assertEquals("2018", releaseWork.getPublicationDate().getYear().getValue());
        assertEquals("01", releaseWork.getPublicationDate().getMonth().getValue());
        assertEquals("01", releaseWork.getPublicationDate().getDay().getValue());
        assertEquals(Long.valueOf(123), releaseWork.getPutCode());
        assertEquals("Short description", releaseWork.getShortDescription());
        assertEquals(new org.orcid.jaxb.model.v3.release.common.Source("0000-0000-0000-0000"), releaseWork.getSource());
        assertEquals("http://www.orcid.org", releaseWork.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.release.common.Visibility.LIMITED, releaseWork.getVisibility());
        assertEquals("This is the citation", releaseWork.getWorkCitation().getCitation());
        assertEquals(org.orcid.jaxb.model.common.CitationType.FORMATTED_UNSPECIFIED, releaseWork.getWorkCitation().getWorkCitationType());
        assertEquals(1, releaseWork.getWorkContributors().getContributor().size());

        assertEquals(org.orcid.jaxb.model.common.ContributorRole.ASSIGNEE,
                releaseWork.getWorkContributors().getContributor().get(0).getContributorAttributes().getContributorRole());
        assertEquals(org.orcid.jaxb.model.common.SequenceType.ADDITIONAL,
                releaseWork.getWorkContributors().getContributor().get(0).getContributorAttributes().getContributorSequence());
        assertEquals("contributor@orcid.org", releaseWork.getWorkContributors().getContributor().get(0).getContributorEmail().getValue());
        assertEquals("0000-0000-0000-0000", releaseWork.getWorkContributors().getContributor().get(0).getContributorOrcid().getPath());
        assertEquals("Credit Name", releaseWork.getWorkContributors().getContributor().get(0).getCreditName().getContent());
        assertEquals(1, releaseWork.getWorkExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(org.orcid.jaxb.model.common.Relationship.SELF, releaseWork.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("type", releaseWork.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://www.orcid.org", releaseWork.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", releaseWork.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("The subtitle", releaseWork.getWorkTitle().getSubtitle().getContent());
        assertEquals("The title", releaseWork.getWorkTitle().getTitle().getContent());
        assertEquals("Translated", releaseWork.getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("en", releaseWork.getWorkTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS, releaseWork.getWorkType());

        releaseWork = null;

        // Work summary test
        org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary rc2WorkSummary = new org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary();
        rc2WorkSummary.setCreatedDate(new org.orcid.jaxb.model.v3.rc2.common.CreatedDate(gc1));
        rc2WorkSummary.setDisplayIndex("1");
        rc2WorkSummary.setExternalIdentifiers(extIds);
        rc2WorkSummary.setJournalTitle(new org.orcid.jaxb.model.v3.rc2.common.Title("Journal title"));
        rc2WorkSummary.setLastModifiedDate(new org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate(gc2));
        rc2WorkSummary.setPath("/0000-0000-0000-0000/rcX/work/123");
        rc2WorkSummary.setPublicationDate(new org.orcid.jaxb.model.v3.rc2.common.PublicationDate(new org.orcid.jaxb.model.v3.rc2.common.Year(2018),
                new org.orcid.jaxb.model.v3.rc2.common.Month(1), new org.orcid.jaxb.model.v3.rc2.common.Day(1)));
        rc2WorkSummary.setPutCode(123L);
        rc2WorkSummary.setSource(new org.orcid.jaxb.model.v3.rc2.common.Source("0000-0000-0000-0000"));
        rc2WorkSummary.setTitle(title);
        rc2WorkSummary.setType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS);
        rc2WorkSummary.setUrl(new org.orcid.jaxb.model.v3.rc2.common.Url("http://www.orcid.org"));
        rc2WorkSummary.setVisibility(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED);

        org.orcid.jaxb.model.v3.release.record.summary.WorkSummary releaseWorkSummary = (org.orcid.jaxb.model.v3.release.record.summary.WorkSummary) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2WorkSummary, "3.0_rc2"), "3.0")).getObjectToConvert();

        assertNotNull(releaseWorkSummary);
        assertEquals(new org.orcid.jaxb.model.v3.release.common.CreatedDate(gc1), releaseWorkSummary.getCreatedDate());
        assertEquals("1", releaseWorkSummary.getDisplayIndex());
        assertEquals(1, releaseWorkSummary.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(org.orcid.jaxb.model.common.Relationship.SELF, releaseWorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("type", releaseWorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://www.orcid.org", releaseWorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", releaseWorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("Journal title", releaseWorkSummary.getJournalTitle().getContent());
        assertEquals(new org.orcid.jaxb.model.v3.release.common.LastModifiedDate(gc2), releaseWorkSummary.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0000/rcX/work/123", releaseWorkSummary.getPath());
        assertEquals("2018", releaseWorkSummary.getPublicationDate().getYear().getValue());
        assertEquals("01", releaseWorkSummary.getPublicationDate().getMonth().getValue());
        assertEquals("01", releaseWorkSummary.getPublicationDate().getDay().getValue());
        assertEquals(Long.valueOf(123), releaseWorkSummary.getPutCode());
        assertEquals(new org.orcid.jaxb.model.v3.release.common.Source("0000-0000-0000-0000"), releaseWorkSummary.getSource());
        assertEquals("The subtitle", releaseWorkSummary.getTitle().getSubtitle().getContent());
        assertEquals("The title", releaseWorkSummary.getTitle().getTitle().getContent());
        assertEquals("Translated", releaseWorkSummary.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", releaseWorkSummary.getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS, releaseWorkSummary.getType());
        assertEquals("http://www.orcid.org", releaseWorkSummary.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.release.common.Visibility.LIMITED, releaseWorkSummary.getVisibility());
    }

    @Test
    public void downgradeReleaseToRC2Test() {
        XMLGregorianCalendar gc1 = DateUtils.convertToXMLGregorianCalendar(new GregorianCalendar(2018, 1, 1));
        XMLGregorianCalendar gc2 = DateUtils.convertToXMLGregorianCalendar(new GregorianCalendar(2019, 1, 1));

        // Work test
        org.orcid.jaxb.model.v3.release.record.Work releaseWork = new org.orcid.jaxb.model.v3.release.record.Work();
        releaseWork.setCountry(new org.orcid.jaxb.model.v3.release.common.Country(org.orcid.jaxb.model.common.Iso3166Country.US));
        releaseWork.setCreatedDate(new org.orcid.jaxb.model.v3.release.common.CreatedDate(gc1));
        releaseWork.setJournalTitle(new org.orcid.jaxb.model.v3.release.common.Title("Journal title"));
        releaseWork.setLanguageCode("en");
        releaseWork.setLastModifiedDate(new org.orcid.jaxb.model.v3.release.common.LastModifiedDate(gc2));
        releaseWork.setPath("/0000-0000-0000-0000/rcX/work/123");
        releaseWork.setPublicationDate(new org.orcid.jaxb.model.v3.release.common.PublicationDate(new org.orcid.jaxb.model.v3.release.common.Year(2018),
                new org.orcid.jaxb.model.v3.release.common.Month(1), new org.orcid.jaxb.model.v3.release.common.Day(1)));
        releaseWork.setPutCode(123L);
        releaseWork.setShortDescription("Short description");
        releaseWork.setSource(new org.orcid.jaxb.model.v3.release.common.Source("0000-0000-0000-0000"));
        releaseWork.setUrl(new org.orcid.jaxb.model.v3.release.common.Url("http://www.orcid.org"));
        releaseWork.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.LIMITED);
        releaseWork.setWorkCitation(
                new org.orcid.jaxb.model.v3.release.record.Citation("This is the citation", org.orcid.jaxb.model.common.CitationType.FORMATTED_UNSPECIFIED));
        org.orcid.jaxb.model.v3.release.common.Contributor c = new org.orcid.jaxb.model.v3.release.common.Contributor();
        org.orcid.jaxb.model.v3.release.common.ContributorAttributes ca = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        ca.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.ASSIGNEE);
        ca.setContributorSequence(org.orcid.jaxb.model.common.SequenceType.ADDITIONAL);
        c.setContributorAttributes(ca);
        c.setContributorEmail(new org.orcid.jaxb.model.v3.release.common.ContributorEmail("contributor@orcid.org"));
        c.setContributorOrcid(new org.orcid.jaxb.model.v3.release.common.ContributorOrcid("0000-0000-0000-0000"));
        c.setCreditName(new org.orcid.jaxb.model.v3.release.common.CreditName("Credit Name"));
        org.orcid.jaxb.model.v3.release.record.WorkContributors wc = new org.orcid.jaxb.model.v3.release.record.WorkContributors();
        wc.getContributor().add(c);
        releaseWork.setWorkContributors(wc);
        org.orcid.jaxb.model.v3.release.record.ExternalIDs extIds = new org.orcid.jaxb.model.v3.release.record.ExternalIDs();
        org.orcid.jaxb.model.v3.release.record.ExternalID extId1 = new org.orcid.jaxb.model.v3.release.record.ExternalID();
        extId1.setRelationship(org.orcid.jaxb.model.common.Relationship.SELF);
        extId1.setType("type");
        extId1.setUrl(new org.orcid.jaxb.model.v3.release.common.Url("http://www.orcid.org"));
        extId1.setValue("extId1");
        extIds.getExternalIdentifier().add(extId1);
        org.orcid.jaxb.model.v3.release.record.ExternalID extId2 = new org.orcid.jaxb.model.v3.release.record.ExternalID();
        extId2.setRelationship(org.orcid.jaxb.model.common.Relationship.PART_OF);
        extId2.setType("type");
        extId2.setUrl(new org.orcid.jaxb.model.v3.release.common.Url("http://www.orcid.org"));
        extId2.setValue("extId2");
        extIds.getExternalIdentifier().add(extId2);
        releaseWork.setWorkExternalIdentifiers(extIds);
        org.orcid.jaxb.model.v3.release.record.WorkTitle title = new org.orcid.jaxb.model.v3.release.record.WorkTitle();
        title.setSubtitle(new org.orcid.jaxb.model.v3.release.common.Subtitle("The subtitle"));
        title.setTitle(new org.orcid.jaxb.model.v3.release.common.Title("The title"));
        title.setTranslatedTitle(new org.orcid.jaxb.model.v3.release.common.TranslatedTitle("Translated", "en"));
        releaseWork.setWorkTitle(title);
        releaseWork.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS);

        // Map and test
        org.orcid.jaxb.model.v3.rc2.record.Work rc2Work = (org.orcid.jaxb.model.v3.rc2.record.Work) (v3VersionConverterChain
                .downgrade(new V3Convertible(releaseWork, "3.0"), "3.0_rc2")).getObjectToConvert();

        assertNotNull(rc2Work);
        assertEquals(org.orcid.jaxb.model.common.Iso3166Country.US, rc2Work.getCountry().getValue());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.CreatedDate(gc1), rc2Work.getCreatedDate());
        assertEquals("Journal title", rc2Work.getJournalTitle().getContent());
        assertEquals("en", rc2Work.getLanguageCode());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate(gc2), rc2Work.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0000/rcX/work/123", rc2Work.getPath());
        assertEquals("2018", rc2Work.getPublicationDate().getYear().getValue());
        assertEquals("01", rc2Work.getPublicationDate().getMonth().getValue());
        assertEquals("01", rc2Work.getPublicationDate().getDay().getValue());
        assertEquals(Long.valueOf(123), rc2Work.getPutCode());
        assertEquals("Short description", rc2Work.getShortDescription());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.Source("0000-0000-0000-0000"), rc2Work.getSource());
        assertEquals("http://www.orcid.org", rc2Work.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED, rc2Work.getVisibility());
        assertEquals("This is the citation", rc2Work.getWorkCitation().getCitation());
        assertEquals(org.orcid.jaxb.model.common.CitationType.FORMATTED_UNSPECIFIED, rc2Work.getWorkCitation().getWorkCitationType());
        assertEquals(1, rc2Work.getWorkContributors().getContributor().size());

        assertEquals(org.orcid.jaxb.model.common.ContributorRole.ASSIGNEE,
                rc2Work.getWorkContributors().getContributor().get(0).getContributorAttributes().getContributorRole());
        assertEquals(org.orcid.jaxb.model.common.SequenceType.ADDITIONAL,
                rc2Work.getWorkContributors().getContributor().get(0).getContributorAttributes().getContributorSequence());

        assertEquals("contributor@orcid.org", rc2Work.getWorkContributors().getContributor().get(0).getContributorEmail().getValue());
        assertEquals("0000-0000-0000-0000", rc2Work.getWorkContributors().getContributor().get(0).getContributorOrcid().getPath());
        assertEquals("Credit Name", rc2Work.getWorkContributors().getContributor().get(0).getCreditName().getContent());
        assertEquals(2, rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(org.orcid.jaxb.model.common.Relationship.SELF, rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("type", rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://www.orcid.org", rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(0).getValue());

        assertEquals(org.orcid.jaxb.model.common.Relationship.PART_OF, rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());
        assertEquals("type", rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("http://www.orcid.org", rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("extId2", rc2Work.getWorkExternalIdentifiers().getExternalIdentifier().get(1).getValue());

        assertEquals("The subtitle", rc2Work.getWorkTitle().getSubtitle().getContent());
        assertEquals("The title", rc2Work.getWorkTitle().getTitle().getContent());
        assertEquals("Translated", rc2Work.getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("en", rc2Work.getWorkTitle().getTranslatedTitle().getLanguageCode());

        assertEquals(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS, rc2Work.getWorkType());

        // Work summary test
        org.orcid.jaxb.model.v3.release.record.summary.WorkSummary releaseWorkSummary = new org.orcid.jaxb.model.v3.release.record.summary.WorkSummary();
        releaseWorkSummary.setCreatedDate(new org.orcid.jaxb.model.v3.release.common.CreatedDate(gc1));
        releaseWorkSummary.setDisplayIndex("1");
        releaseWorkSummary.setExternalIdentifiers(extIds);
        releaseWorkSummary.setJournalTitle(new org.orcid.jaxb.model.v3.release.common.Title("Journal title"));
        releaseWorkSummary.setLastModifiedDate(new org.orcid.jaxb.model.v3.release.common.LastModifiedDate(gc2));
        releaseWorkSummary.setPath("/0000-0000-0000-0000/rcX/work/123");
        releaseWorkSummary.setPublicationDate(new org.orcid.jaxb.model.v3.release.common.PublicationDate(new org.orcid.jaxb.model.v3.release.common.Year(2018),
                new org.orcid.jaxb.model.v3.release.common.Month(1), new org.orcid.jaxb.model.v3.release.common.Day(1)));
        releaseWorkSummary.setPutCode(123L);
        releaseWorkSummary.setSource(new org.orcid.jaxb.model.v3.release.common.Source("0000-0000-0000-0000"));
        releaseWorkSummary.setTitle(title);
        releaseWorkSummary.setType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS);
        releaseWorkSummary.setUrl(new org.orcid.jaxb.model.v3.release.common.Url("http://www.orcid.org"));
        releaseWorkSummary.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.LIMITED);

        org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary rc2WorkSummary = (org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary) (v3VersionConverterChain
                .downgrade(new V3Convertible(releaseWorkSummary, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2WorkSummary);
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.CreatedDate(gc1), rc2WorkSummary.getCreatedDate());
        assertEquals("1", rc2WorkSummary.getDisplayIndex());
        assertEquals(2, rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(org.orcid.jaxb.model.common.Relationship.SELF, rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("type", rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://www.orcid.org", rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("extId1", rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());

        assertEquals(org.orcid.jaxb.model.common.Relationship.PART_OF, rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());
        assertEquals("type", rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("http://www.orcid.org", rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("extId2", rc2WorkSummary.getExternalIdentifiers().getExternalIdentifier().get(1).getValue());

        assertEquals("Journal title", rc2WorkSummary.getJournalTitle().getContent());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate(gc2), rc2WorkSummary.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0000/rcX/work/123", rc2WorkSummary.getPath());
        assertEquals("2018", rc2WorkSummary.getPublicationDate().getYear().getValue());
        assertEquals("01", rc2WorkSummary.getPublicationDate().getMonth().getValue());
        assertEquals("01", rc2WorkSummary.getPublicationDate().getDay().getValue());
        assertEquals(Long.valueOf(123), rc2WorkSummary.getPutCode());
        assertEquals(new org.orcid.jaxb.model.v3.rc2.common.Source("0000-0000-0000-0000"), rc2WorkSummary.getSource());
        assertEquals("The subtitle", rc2WorkSummary.getTitle().getSubtitle().getContent());
        assertEquals("The title", rc2WorkSummary.getTitle().getTitle().getContent());
        assertEquals("Translated", rc2WorkSummary.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", rc2WorkSummary.getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS, rc2WorkSummary.getType());
        assertEquals("http://www.orcid.org", rc2WorkSummary.getUrl().getValue());
        assertEquals(org.orcid.jaxb.model.v3.rc2.common.Visibility.LIMITED, rc2WorkSummary.getVisibility());
    }
}