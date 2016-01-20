/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package orcid.pojo.ajaxForm;

import java.util.Date;

import net.sf.ehcache.util.MemoryEfficientByteArrayOutputStream;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;
import org.orcid.jaxb.model.common_rc2.ContributorAttributes;
import org.orcid.jaxb.model.common_rc2.ContributorOrcid;
import org.orcid.jaxb.model.common_rc2.ContributorRole;
import org.orcid.jaxb.model.common_rc2.Country;
import org.orcid.jaxb.model.common_rc2.CreatedDate;
import org.orcid.jaxb.model.common_rc2.CreditName;
import org.orcid.jaxb.model.common_rc2.Day;
import org.orcid.jaxb.model.common_rc2.Iso3166Country;
import org.orcid.jaxb.model.common_rc2.Month;
import org.orcid.jaxb.model.common_rc2.PublicationDate;
import org.orcid.jaxb.model.common_rc2.Subtitle;
import org.orcid.jaxb.model.common_rc2.Title;
import org.orcid.jaxb.model.common_rc2.TranslatedTitle;
import org.orcid.jaxb.model.common_rc2.Url;
import org.orcid.jaxb.model.common_rc2.Year;
import org.orcid.jaxb.model.record_rc2.CitationType;
import org.orcid.jaxb.model.record_rc2.Relationship;
import org.orcid.jaxb.model.record_rc2.SequenceType;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.jaxb.model.record_rc2.WorkContributors;
import org.orcid.jaxb.model.record_rc2.WorkExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.WorkExternalIdentifierId;
import org.orcid.jaxb.model.record_rc2.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_rc2.WorkExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.WorkTitle;
import org.orcid.jaxb.model.record_rc2.WorkType;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.orcid.utils.DateUtils;

public class WorkFormTest extends XMLTestCase {
    
    @Test
    public void testValueOfAndBack() throws Exception {  
        Work work = getWork();
        WorkForm workForm = WorkForm.valueOf(work);
        Work backToWork = workForm.toWork();        
        assertEquals(work, backToWork);        
    }
    
    @Test
    public void testSerializeWork() throws Exception {
        Work work = getWork();
        WorkForm workForm =  WorkForm.valueOf(work);
        MemoryEfficientByteArrayOutputStream.serialize(workForm);
    }
    
    @Test
    public void testEmptyTranslatedTitleDontGetIntoTheWork() {
        WorkForm form = new WorkForm();
        form.setTitle(Text.valueOf("The title"));
        org.orcid.pojo.ajaxForm.TranslatedTitle translatedTitle = new org.orcid.pojo.ajaxForm.TranslatedTitle();
        
        //Test with empty values
        translatedTitle.setContent(" ");
        translatedTitle.setLanguageCode(" ");
        form.setTranslatedTitle(translatedTitle);
        
        Work work = form.toWork();
        assertNotNull(work);
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("The title", work.getWorkTitle().getTitle().getContent());
        assertNull(work.getWorkTitle().getTranslatedTitle());
        
        //Test with empty content
        translatedTitle.setContent(" ");        
        translatedTitle.setLanguageCode("en");
        form.setTranslatedTitle(translatedTitle);
        
        work = form.toWork();
        assertNotNull(work);
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("The title", work.getWorkTitle().getTitle().getContent());
        assertNull(work.getWorkTitle().getTranslatedTitle());
        
        //Test with empty strings
        translatedTitle.setContent("");
        translatedTitle.setLanguageCode("");
        form.setTranslatedTitle(translatedTitle);
        
        work = form.toWork();
        assertNotNull(work);
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("The title", work.getWorkTitle().getTitle().getContent());
        assertNull(work.getWorkTitle().getTranslatedTitle());
        
        //Test with null values
        translatedTitle.setContent(null);
        translatedTitle.setLanguageCode(null);
        form.setTranslatedTitle(translatedTitle);
        
        work = form.toWork();
        assertNotNull(work);
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("The title", work.getWorkTitle().getTitle().getContent());
        assertNull(work.getWorkTitle().getTranslatedTitle());
        
        //Test with a both values
        translatedTitle.setContent("Translated title");
        translatedTitle.setLanguageCode("en");
        form.setTranslatedTitle(translatedTitle);
        
        work = form.toWork();
        assertNotNull(work);
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("The title", work.getWorkTitle().getTitle().getContent());
        assertNotNull(work.getWorkTitle().getTranslatedTitle());
        assertEquals("Translated title", work.getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("en", work.getWorkTitle().getTranslatedTitle().getLanguageCode());
    }

    public static Work getWork() {
        Work work = new Work();
        work.setCountry(new Country(Iso3166Country.US));        
        Date date = new Date();                        
        work.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(date)));
        work.setJournalTitle(new Title("Journal Title"));
        work.setLanguageCode("EN");
        work.setPublicationDate(new PublicationDate(new Year(2015), new Month(1), new Day(1)));
        work.setPutCode(Long.valueOf("12345"));
        work.setShortDescription("Short description");
        work.setUrl(new Url("http://test.com"));
        work.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED);
        work.setWorkCitation(new org.orcid.jaxb.model.record_rc2.Citation("Citation", CitationType.BIBTEX));
        WorkContributors contributors = new WorkContributors();
        org.orcid.jaxb.model.common_rc2.Contributor contributor = new org.orcid.jaxb.model.common_rc2.Contributor();
        contributor.setCreditName(new CreditName("Credit name"));
        contributor.setContributorOrcid(new ContributorOrcid("0000-0000-0000-0000"));
        ContributorAttributes att = new ContributorAttributes();
        att.setContributorRole(ContributorRole.ASSIGNEE);
        att.setContributorSequence(SequenceType.FIRST);
        contributor.setContributorAttributes(att);
        contributors.getContributor().add(contributor);
        work.setWorkContributors(contributors);        
        WorkExternalIdentifiers weis = new WorkExternalIdentifiers();
        WorkExternalIdentifier wei = new WorkExternalIdentifier();
        wei.setRelationship(Relationship.SELF);
        wei.setUrl(new Url("http://test.com"));
        wei.setWorkExternalIdentifierId(new WorkExternalIdentifierId("ID"));
        wei.setWorkExternalIdentifierType(WorkExternalIdentifierType.AGR);
        weis.getWorkExternalIdentifier().add(wei);
        work.setWorkExternalIdentifiers(weis);        
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("Work Title"));
        workTitle.setSubtitle(new Subtitle("Subtitle"));
        TranslatedTitle translated = new TranslatedTitle("Translated", "US");
        workTitle.setTranslatedTitle(translated);
        work.setWorkTitle(workTitle);
        work.setWorkType(WorkType.ARTISTIC_PERFORMANCE);
        return work;
    }
}
