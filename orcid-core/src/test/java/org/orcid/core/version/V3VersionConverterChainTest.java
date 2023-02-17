package org.orcid.core.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.GregorianCalendar;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.jaxb.model.common.FundingContributorRole;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.ResourceType;
import org.orcid.jaxb.model.common.SequenceType;
import org.orcid.jaxb.model.v3.release.common.TransientError;
import org.orcid.jaxb.model.v3.release.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.core.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
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

        assertEquals("assignee",
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
        ca.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.ASSIGNEE.value());
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
    
    @Test
    public void upgradeWorkContributorAttributesTest() {
        // Test default contributor roles
        org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes rc2ContributorAttributes = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes(); 
        rc2ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        rc2ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.ASSIGNEE);

        org.orcid.jaxb.model.v3.release.common.ContributorAttributes v3ContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.common.ContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2ContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(v3ContributorAttributes);
        assertEquals(SequenceType.FIRST, v3ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.ASSIGNEE.value(), v3ContributorAttributes.getContributorRole());
        
        rc2ContributorAttributes = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes(); 
        rc2ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        rc2ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.AUTHOR);

        v3ContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.common.ContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2ContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(v3ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, v3ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.AUTHOR.value(), v3ContributorAttributes.getContributorRole());
        
        rc2ContributorAttributes = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes(); 
        rc2ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        rc2ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.CHAIR_OR_TRANSLATOR);

        v3ContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.common.ContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2ContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(v3ContributorAttributes);
        assertEquals(SequenceType.FIRST, v3ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.CHAIR_OR_TRANSLATOR.value(), v3ContributorAttributes.getContributorRole());
        
        rc2ContributorAttributes = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes(); 
        rc2ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        rc2ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.CO_INVENTOR);

        v3ContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.common.ContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2ContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(v3ContributorAttributes);
        assertEquals(SequenceType.FIRST, v3ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.CO_INVENTOR.value(), v3ContributorAttributes.getContributorRole());
        
        rc2ContributorAttributes = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes(); 
        rc2ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        rc2ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.CO_INVESTIGATOR);

        v3ContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.common.ContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2ContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(v3ContributorAttributes);
        assertEquals(SequenceType.FIRST, v3ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.CO_INVESTIGATOR.value(), v3ContributorAttributes.getContributorRole());
        
        rc2ContributorAttributes = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes(); 
        rc2ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        rc2ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.EDITOR);

        v3ContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.common.ContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2ContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(v3ContributorAttributes);
        assertEquals(SequenceType.FIRST, v3ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.EDITOR.value(), v3ContributorAttributes.getContributorRole());
        
        rc2ContributorAttributes = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes(); 
        rc2ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        rc2ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.GRADUATE_STUDENT);

        v3ContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.common.ContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2ContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(v3ContributorAttributes);
        assertEquals(SequenceType.FIRST, v3ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.GRADUATE_STUDENT.value(), v3ContributorAttributes.getContributorRole());
        
        rc2ContributorAttributes = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes(); 
        rc2ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        rc2ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.OTHER_INVENTOR);

        v3ContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.common.ContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2ContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(v3ContributorAttributes);
        assertEquals(SequenceType.FIRST, v3ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.OTHER_INVENTOR.value(), v3ContributorAttributes.getContributorRole());
        
        rc2ContributorAttributes = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes(); 
        rc2ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        rc2ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.POSTDOCTORAL_RESEARCHER);

        v3ContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.common.ContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2ContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(v3ContributorAttributes);
        assertEquals(SequenceType.FIRST, v3ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.POSTDOCTORAL_RESEARCHER.value(), v3ContributorAttributes.getContributorRole());
        
        rc2ContributorAttributes = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes(); 
        rc2ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        rc2ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.PRINCIPAL_INVESTIGATOR);

        v3ContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.common.ContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2ContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(v3ContributorAttributes);
        assertEquals(SequenceType.FIRST, v3ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.PRINCIPAL_INVESTIGATOR.value(), v3ContributorAttributes.getContributorRole());
        
        rc2ContributorAttributes = new org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes(); 
        rc2ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        rc2ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.SUPPORT_STAFF);

        v3ContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.common.ContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2ContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(v3ContributorAttributes);
        assertEquals(SequenceType.FIRST, v3ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.SUPPORT_STAFF.value(), v3ContributorAttributes.getContributorRole());               
    }
    
    @Test
    public void downgradeWorkContributorAttributesTest() {
        // Test default contributor roles
        org.orcid.jaxb.model.v3.release.common.ContributorAttributes v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        v3ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.AUTHOR.value());
        
        org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.FIRST, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.AUTHOR, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.ASSIGNEE.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.ASSIGNEE, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.CHAIR_OR_TRANSLATOR.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.CHAIR_OR_TRANSLATOR, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.CO_INVENTOR.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.CO_INVENTOR, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.CO_INVESTIGATOR.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.CO_INVESTIGATOR, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.EDITOR.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.EDITOR, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.GRADUATE_STUDENT.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.GRADUATE_STUDENT, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.OTHER_INVENTOR.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.OTHER_INVENTOR, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.POSTDOCTORAL_RESEARCHER.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.POSTDOCTORAL_RESEARCHER, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.PRINCIPAL_INVESTIGATOR.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.PRINCIPAL_INVESTIGATOR, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.SUPPORT_STAFF.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.SUPPORT_STAFF, rc2ContributorAttributes.getContributorRole());
        
        // Test credit contributor roles
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        v3ContributorAttributes.setContributorRole(CreditRole.WRITING_ORIGINAL_DRAFT.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.FIRST, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.AUTHOR, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        v3ContributorAttributes.setContributorRole(CreditRole.WRITING_REVIEW_EDITING.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.FIRST, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.EDITOR, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        v3ContributorAttributes.setContributorRole(CreditRole.INVESTIGATION.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.FIRST, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.CO_INVESTIGATOR, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.FIRST);
        v3ContributorAttributes.setContributorRole(CreditRole.SUPERVISION.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.FIRST, rc2ContributorAttributes.getContributorSequence());
        assertEquals(org.orcid.jaxb.model.common.ContributorRole.PRINCIPAL_INVESTIGATOR, rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(CreditRole.CONCEPTUALIZATION.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertNull(rc2ContributorAttributes.getContributorRole());                
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(CreditRole.DATA_CURATION.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertNull(rc2ContributorAttributes.getContributorRole());                
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(CreditRole.FORMAL_ANALYSIS.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertNull(rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(CreditRole.FUNDING_ACQUISITION.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertNull(rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(CreditRole.METHODOLOGY.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertNull(rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(CreditRole.RESOURCES.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertNull(rc2ContributorAttributes.getContributorRole());
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(CreditRole.SOFTWARE.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertNull(rc2ContributorAttributes.getContributorRole());        
        
        v3ContributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        v3ContributorAttributes.setContributorSequence(SequenceType.ADDITIONAL);
        v3ContributorAttributes.setContributorRole(CreditRole.VALIDATION.value());
        
        rc2ContributorAttributes = (org.orcid.jaxb.model.v3.rc2.common.ContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3ContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        assertNotNull(rc2ContributorAttributes);
        assertEquals(SequenceType.ADDITIONAL, rc2ContributorAttributes.getContributorSequence());
        assertNull(rc2ContributorAttributes.getContributorRole());        
    }
    
    @Test
    public void upgradeFundingContributorAttributesTest() {
        org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes rc2FundingContributorAttributes = new org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes();  
        rc2FundingContributorAttributes.setContributorRole(FundingContributorRole.CO_LEAD);
        
        org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes v3FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2FundingContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertEquals(FundingContributorRole.CO_LEAD.value(), v3FundingContributorAttributes.getContributorRole());
        
        rc2FundingContributorAttributes = new org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes();  
        rc2FundingContributorAttributes.setContributorRole(FundingContributorRole.LEAD);
        
        v3FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2FundingContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertEquals(FundingContributorRole.LEAD.value(), v3FundingContributorAttributes.getContributorRole());
        
        rc2FundingContributorAttributes = new org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes();  
        rc2FundingContributorAttributes.setContributorRole(FundingContributorRole.OTHER_CONTRIBUTION);
        
        v3FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2FundingContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertEquals(FundingContributorRole.OTHER_CONTRIBUTION.value(), v3FundingContributorAttributes.getContributorRole());
        
        rc2FundingContributorAttributes = new org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes();  
        rc2FundingContributorAttributes.setContributorRole(FundingContributorRole.SUPPORTED_BY);
        
        v3FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes) (v3VersionConverterChain
                .upgrade(new V3Convertible(rc2FundingContributorAttributes, "3.0_rc2"), "3.0")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertEquals(FundingContributorRole.SUPPORTED_BY.value(), v3FundingContributorAttributes.getContributorRole());
    }
    
    @Test
    public void downgradeFundingContributorAttributesTest() {        
        // Test default contributor roles
        org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(FundingContributorRole.CO_LEAD.value());
        
        org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertEquals(FundingContributorRole.CO_LEAD, rc2FundingContributorAttributes.getContributorRole());
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(FundingContributorRole.LEAD.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertEquals(FundingContributorRole.LEAD, rc2FundingContributorAttributes.getContributorRole());
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(FundingContributorRole.OTHER_CONTRIBUTION.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertEquals(FundingContributorRole.OTHER_CONTRIBUTION, rc2FundingContributorAttributes.getContributorRole());        
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(FundingContributorRole.SUPPORTED_BY.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertEquals(FundingContributorRole.SUPPORTED_BY, rc2FundingContributorAttributes.getContributorRole());        
        
        // Test credit contributor roles
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.SUPERVISION.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertEquals(FundingContributorRole.LEAD, rc2FundingContributorAttributes.getContributorRole());
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.CONCEPTUALIZATION.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertNull(rc2FundingContributorAttributes.getContributorRole());
        
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.DATA_CURATION.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertNull(rc2FundingContributorAttributes.getContributorRole());                
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.FORMAL_ANALYSIS.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertNull(rc2FundingContributorAttributes.getContributorRole());
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.FUNDING_ACQUISITION.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertNull(rc2FundingContributorAttributes.getContributorRole());
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.INVESTIGATION.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertNull(rc2FundingContributorAttributes.getContributorRole());
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.METHODOLOGY.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertNull(rc2FundingContributorAttributes.getContributorRole());
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.PROJECT_ADMINISTRATION.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertNull(rc2FundingContributorAttributes.getContributorRole());
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.RESOURCES.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertNull(rc2FundingContributorAttributes.getContributorRole());
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.SOFTWARE.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertNull(rc2FundingContributorAttributes.getContributorRole());
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.VALIDATION.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertNull(rc2FundingContributorAttributes.getContributorRole());
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.VISUALIZATION.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertNull(rc2FundingContributorAttributes.getContributorRole());
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.WRITING_ORIGINAL_DRAFT.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertNull(rc2FundingContributorAttributes.getContributorRole());
        
        v3FundingContributorAttributes = new org.orcid.jaxb.model.v3.release.record.FundingContributorAttributes();
        v3FundingContributorAttributes.setContributorRole(CreditRole.WRITING_REVIEW_EDITING.value());
        
        rc2FundingContributorAttributes =         
                (org.orcid.jaxb.model.v3.rc2.record.FundingContributorAttributes) (v3VersionConverterChain
                .downgrade(new V3Convertible(v3FundingContributorAttributes, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2FundingContributorAttributes);
        assertNull(rc2FundingContributorAttributes.getContributorRole());
    }   
    
    @Test
    public void externalIdsFilterFundedByIdsOnRCsTest() {
        ExternalIDs extIds = new ExternalIDs();
        ExternalID e1Self = new ExternalID();
        e1Self.setRelationship(Relationship.SELF);
        e1Self.setType("type-self");
        e1Self.setUrl(new Url("https://qa.orcid.org/0000-0000-0000-0000"));
        e1Self.setValue("ext-id-self");        
        extIds.getExternalIdentifier().add(e1Self);
        
        ExternalID e2PartOf = new ExternalID();
        e2PartOf.setRelationship(Relationship.PART_OF);
        e2PartOf.setType("type-part-of");
        e2PartOf.setUrl(new Url("https://qa.orcid.org/0000-0000-0000-0000"));
        e2PartOf.setValue("ext-id-part-of");
        extIds.getExternalIdentifier().add(e2PartOf);
        
        ExternalID e3VersionOf = new ExternalID();
        e3VersionOf.setRelationship(Relationship.VERSION_OF);
        e3VersionOf.setType("type-version-of");
        e3VersionOf.setUrl(new Url("https://qa.orcid.org/0000-0000-0000-0000"));
        e3VersionOf.setValue("ext-id-version-of");
        extIds.getExternalIdentifier().add(e3VersionOf);
        
        ExternalID e4FundedBy = new ExternalID();
        e4FundedBy.setRelationship(Relationship.FUNDED_BY);
        e4FundedBy.setType("type-funded-by");
        e4FundedBy.setUrl(new Url("https://qa.orcid.org/0000-0000-0000-0000"));
        e4FundedBy.setValue("ext-id-funded-by");
        extIds.getExternalIdentifier().add(e4FundedBy);
        
        boolean foundSelf = false;
        boolean foundPartOf = false;
        boolean foundVersionOf = false;
        
        // Verify a list with all four types of relationships filter just the FOUNDED_BY one
        org.orcid.jaxb.model.v3.rc2.record.ExternalIDs rc2ExtIds =         
                (org.orcid.jaxb.model.v3.rc2.record.ExternalIDs) (v3VersionConverterChain
                .downgrade(new V3Convertible(extIds, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2ExtIds);
        assertEquals(3, rc2ExtIds.getExternalIdentifier().size());
        
        for(org.orcid.jaxb.model.v3.rc2.record.ExternalID rc2ExtId : rc2ExtIds.getExternalIdentifier()) {
            if(Relationship.SELF.equals(rc2ExtId.getRelationship())) {
                foundSelf = true;
            } else if(Relationship.PART_OF.equals(rc2ExtId.getRelationship())) {
                foundPartOf = true;
            } else if(Relationship.VERSION_OF.equals(rc2ExtId.getRelationship())) {
                foundVersionOf = true;
            } else {
                fail("Invalid external id found: " + rc2ExtId.getValue() + " -> " + rc2ExtId.getRelationship());
            }
        }
        
        assertTrue(foundSelf);
        assertTrue(foundPartOf);
        assertTrue(foundVersionOf);
        
        // Verify a list with only one element returns empty just when the FOUNDED_BY is found
        // Clear the list first
        extIds.getExternalIdentifier().clear();
        
        extIds.getExternalIdentifier().add(e1Self);
        
        rc2ExtIds = (org.orcid.jaxb.model.v3.rc2.record.ExternalIDs) (v3VersionConverterChain
                .downgrade(new V3Convertible(extIds, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2ExtIds);
        assertEquals(1, rc2ExtIds.getExternalIdentifier().size());
        assertEquals(e1Self.getRelationship(), rc2ExtIds.getExternalIdentifier().get(0).getRelationship());
        assertEquals(e1Self.getType(), rc2ExtIds.getExternalIdentifier().get(0).getType());
        assertEquals(e1Self.getUrl().getValue(), rc2ExtIds.getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals(e1Self.getValue(), rc2ExtIds.getExternalIdentifier().get(0).getValue());
        
        // Clear again
        extIds.getExternalIdentifier().clear();
        
        extIds.getExternalIdentifier().add(e2PartOf);
        
        rc2ExtIds = (org.orcid.jaxb.model.v3.rc2.record.ExternalIDs) (v3VersionConverterChain
                .downgrade(new V3Convertible(extIds, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2ExtIds);
        assertEquals(1, rc2ExtIds.getExternalIdentifier().size());
        assertEquals(e2PartOf.getRelationship(), rc2ExtIds.getExternalIdentifier().get(0).getRelationship());
        assertEquals(e2PartOf.getType(), rc2ExtIds.getExternalIdentifier().get(0).getType());
        assertEquals(e2PartOf.getUrl().getValue(), rc2ExtIds.getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals(e2PartOf.getValue(), rc2ExtIds.getExternalIdentifier().get(0).getValue());
        
        // Clear again
        extIds.getExternalIdentifier().clear();
        
        extIds.getExternalIdentifier().add(e3VersionOf);
        
        rc2ExtIds = (org.orcid.jaxb.model.v3.rc2.record.ExternalIDs) (v3VersionConverterChain
                .downgrade(new V3Convertible(extIds, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2ExtIds);
        assertEquals(1, rc2ExtIds.getExternalIdentifier().size());
        assertEquals(e3VersionOf.getRelationship(), rc2ExtIds.getExternalIdentifier().get(0).getRelationship());
        assertEquals(e3VersionOf.getType(), rc2ExtIds.getExternalIdentifier().get(0).getType());
        assertEquals(e3VersionOf.getUrl().getValue(), rc2ExtIds.getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals(e3VersionOf.getValue(), rc2ExtIds.getExternalIdentifier().get(0).getValue());
        
        // Clear again
        extIds.getExternalIdentifier().clear();
        
        extIds.getExternalIdentifier().add(e4FundedBy);
        
        rc2ExtIds = (org.orcid.jaxb.model.v3.rc2.record.ExternalIDs) (v3VersionConverterChain
                .downgrade(new V3Convertible(extIds, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2ExtIds);
        assertTrue(rc2ExtIds.getExternalIdentifier().isEmpty());
        
    }
    
    @Test
    public void externalIdDowngradeTest() {        
        ExternalID e = new ExternalID();
        e.setRelationship(Relationship.SELF);
        e.setType("type-self");
        e.setUrl(new Url("https://qa.orcid.org/0000-0000-0000-0000"));
        e.setValue("ext-id-self");
        e.setNormalized(new TransientNonEmptyString("non-empty-string-1"));
        e.setNormalizedError(new TransientError("01", "m01"));
        e.setNormalizedUrl(new TransientNonEmptyString("non-empty-string-2"));
        e.setNormalizedUrlError(new TransientError("02", "m02"));
        
        org.orcid.jaxb.model.v3.rc2.record.ExternalID rc2ExtId = (org.orcid.jaxb.model.v3.rc2.record.ExternalID) (v3VersionConverterChain
                .downgrade(new V3Convertible(e, "3.0"), "3.0_rc2")).getObjectToConvert();
        
        assertNotNull(rc2ExtId);
        assertEquals(Relationship.SELF, rc2ExtId.getRelationship());
        assertEquals("type-self", rc2ExtId.getType());
        assertEquals("https://qa.orcid.org/0000-0000-0000-0000", rc2ExtId.getUrl().getValue());
        assertEquals("ext-id-self", rc2ExtId.getValue());
        assertEquals("non-empty-string-1", rc2ExtId.getNormalized().getValue());
        assertEquals("01", rc2ExtId.getNormalizedError().getErrorCode());
        assertEquals("m01", rc2ExtId.getNormalizedError().getErrorMessage());
        assertEquals("non-empty-string-2", rc2ExtId.getNormalizedUrl().getValue());
        assertEquals("02", rc2ExtId.getNormalizedUrlError().getErrorCode());
        assertEquals("m02", rc2ExtId.getNormalizedUrlError().getErrorMessage());        
    }
}
