package org.orcid.core.adapter;

import org.junit.Before;
import org.junit.Test;
import org.orcid.jaxb.model.common.CitationType;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.common.*;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.Visibility;
import org.orcid.pojo.ajaxForm.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class WorkFormTest {

    private DatatypeFactory datatypeFactory = null;

    private int maxContributorsForUI = 50;

    @Before
    public void before() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            // We're in serious trouble and can't carry on
            throw new IllegalStateException("Cannot create new DatatypeFactory");
        }
    }

    @Test
    public void equalsTest() {
        WorkForm form1 = getWorkForm();
        WorkForm form2 = getWorkForm();
        assertEquals(form1, form2);
        form1.setPutCode(Text.valueOf(String.valueOf(System.currentTimeMillis())));
        assertFalse(form1.equals(form2));
    }

    @Test
    public void toWorkTest() {
        WorkForm form = getWorkForm();
        Work work = form.toWork();
        assertEquals(getWork(), work);
    }

    @Test
    public void toWorkFormTest() {
        Work work = getWork();
        WorkForm form = WorkForm.valueOf(work, maxContributorsForUI);
        assertEquals(getWorkForm(), form);
    }

    private Work getWork() {
        Work work = new Work();
        work.setCountry(new Country(Iso3166Country.US));
        work.setJournalTitle(new Title("Journal title"));
        work.setLanguageCode("en");
        work.setPutCode(Long.valueOf("1"));
        work.setShortDescription("Short description");
        work.setSource(new org.orcid.jaxb.model.v3.release.common.Source("0000-0000-0000-0000"));
        work.setUrl(new Url("http://myurl.com"));
        work.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC);
        org.orcid.jaxb.model.v3.release.record.Citation citation = new org.orcid.jaxb.model.v3.release.record.Citation();
        citation.setCitation("Citation");
        citation.setWorkCitationType(CitationType.FORMATTED_UNSPECIFIED);
        work.setWorkCitation(citation);
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("Title"));
        title.setTranslatedTitle(new org.orcid.jaxb.model.v3.release.common.TranslatedTitle("Translated Title", "es"));
        title.setSubtitle(new Subtitle("Subtitle"));
        work.setWorkTitle(title);
        work.setWorkType(WorkType.ARTISTIC_PERFORMANCE);
        Date date = new Date();
        date.setDay("1");
        date.setMonth("1");
        date.setYear("2015");
        GregorianCalendar calendar = date.toCalendar();
        work.setCreatedDate(new CreatedDate(datatypeFactory.newXMLGregorianCalendar(calendar)));
        date = new Date();
        date.setDay("2");
        date.setMonth("2");
        date.setYear("2015");
        calendar = date.toCalendar();
        work.setLastModifiedDate(new LastModifiedDate(datatypeFactory.newXMLGregorianCalendar(calendar)));
        work.setPublicationDate(new PublicationDate(new Year(2015), new Month(3), new Day(3)));
        org.orcid.jaxb.model.v3.release.record.WorkContributors contributors = new org.orcid.jaxb.model.v3.release.record.WorkContributors();
        org.orcid.jaxb.model.v3.release.common.Contributor contributor = new org.orcid.jaxb.model.v3.release.common.Contributor();
        org.orcid.jaxb.model.v3.release.common.ContributorAttributes attributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        attributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.CO_INVENTOR.value());
        attributes.setContributorSequence(org.orcid.jaxb.model.common.SequenceType.FIRST);
        contributor.setContributorAttributes(attributes);
        contributor.setContributorEmail(null);      
        ContributorOrcid contributorOrcid = new ContributorOrcid("Contributor orcid");
        contributorOrcid.setUri("Contributor uri");
        contributor.setContributorOrcid(contributorOrcid);
        CreditName creditName = new CreditName("Contributor credit name");
        contributor.setCreditName(creditName);
        contributors.getContributor().add(contributor);
        work.setWorkContributors(contributors);
        ExternalIDs externalIdentifiers = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setValue("External Identifier ID");
        extId.setType(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ASIN.value());
        extId.setRelationship(Relationship.SELF);
        externalIdentifiers.getExternalIdentifier().add(extId);
        work.setWorkExternalIdentifiers(externalIdentifiers);
        return work;
    }

    private WorkForm getWorkForm() {
        WorkForm form = new WorkForm();
        form.setCitation(new Citation("Citation", "formatted-unspecified"));        
        List<Contributor> çontributors = new ArrayList<Contributor>();
        Contributor contributor = new Contributor();
        contributor.setContributorRole(Text.valueOf("co-inventor"));
        contributor.setContributorSequence(Text.valueOf("first"));
        contributor.setCreditName(Text.valueOf("Contributor credit name"));
        contributor.setEmail(null);
        contributor.setOrcid(Text.valueOf("Contributor orcid"));
        contributor.setUri(Text.valueOf("Contributor uri"));
        çontributors.add(contributor);
        form.setContributors(çontributors);
        form.setCountryCode(Text.valueOf("US"));        
        Date createdDate = new Date();
        createdDate.setDay("1");
        createdDate.setMonth("1");
        createdDate.setYear("2015");
        form.setCreatedDate(createdDate);        
        form.setJournalTitle(Text.valueOf("Journal title"));
        form.setLanguageCode(Text.valueOf("en"));
        Date lastModifiedDate = new Date();
        lastModifiedDate.setDay("2");
        lastModifiedDate.setMonth("2");
        lastModifiedDate.setYear("2015");
        form.setLastModified(lastModifiedDate);
        Date publicationDate = new Date();
        publicationDate.setDay("03");
        publicationDate.setMonth("03");
        publicationDate.setYear("2015");
        form.setPublicationDate(publicationDate);
        form.setDateSortString(PojoUtil.createDateSortString(null, FuzzyDate.valueOf(2015, 3, 3)));
        form.setPutCode(Text.valueOf("1"));
        form.setShortDescription(Text.valueOf("Short description"));
        form.setSource("0000-0000-0000-0000");        
        form.setSubtitle(Text.valueOf("Subtitle"));
        form.setTitle(Text.valueOf("Title"));
        form.setTranslatedTitle(new TranslatedTitleForm("Translated Title", "es"));
        form.setUrl(Text.valueOf("http://myurl.com"));
        form.setVisibility(Visibility.valueOf(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC));        
        List<ActivityExternalIdentifier> extIds = new ArrayList<ActivityExternalIdentifier>();
        ActivityExternalIdentifier extId = new ActivityExternalIdentifier();
        extId.setExternalIdentifierId(Text.valueOf("External Identifier ID"));
        extId.setExternalIdentifierType(Text.valueOf("asin"));
        extId.setRelationship(Text.valueOf(Relationship.SELF.value()));
        extIds.add(extId);
        form.setWorkExternalIdentifiers(extIds);
        form.setWorkType(Text.valueOf("artistic-performance"));
        return form;
    }
}
