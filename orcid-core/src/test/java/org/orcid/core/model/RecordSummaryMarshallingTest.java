package org.orcid.core.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.Day;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.Month;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.common.Year;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.utils.DateUtils;

public class RecordSummaryMarshallingTest {

    @Test
    public void marshallingTest() throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(new Class[] { RecordSummary.class });
        Marshaller marshaller = context.createMarshaller();        
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("summary-3.0.xml"), StandardCharsets.UTF_8);
        RecordSummary recordSummary = getRecordSummary();
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(recordSummary, stringWriter);
        String realText = stringWriter.toString();
        
        assertEquals(expectedText, realText);
    }
    
    private RecordSummary getRecordSummary() {
        RecordSummary record = new RecordSummary();
        record.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar("2024-01-01T12:00:00")));
        record.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar("2024-01-01T12:00:00")));
        record.setCreditName("User credited name");
        OrcidIdentifier oid = new OrcidIdentifier();
        oid.setHost("orcid.org");
        oid.setPath("8888-8888-8888-8880");
        oid.setUri("https://orcid.org/8888-8888-8888-8880");
        record.setOrcidIdentifier(oid);
        ExternalIdentifiers externalIdentifiers = new ExternalIdentifiers();
        externalIdentifiers.setExternalIdentifiers(new ArrayList<>());
        externalIdentifiers.getExternalIdentifiers().add(getExternalIdentifier("Scopus", 1, true));
        externalIdentifiers.getExternalIdentifiers().add(getExternalIdentifier("ResearcherID", 2, true));
        externalIdentifiers.getExternalIdentifiers().add(getExternalIdentifier("Other", 3, true));
        
        // Set the external identifiers
        record.setExternalIdentifiers(externalIdentifiers);
        
        Employments employments = new Employments();
        employments.setCount(5);
        employments.setEmployments(new ArrayList<>());
        employments.getEmployments().add(getEmployment(1, "Org # 1", "Fake role title 1", "https://test.orcid.org/1", false));
        employments.getEmployments().add(getEmployment(2, "Org # 2", "Fake role title 2", "https://test.orcid.org/2", false));
        employments.getEmployments().add(getEmployment(3, "Org # 3", "Fake role title 3", "https://test.orcid.org/3", true));
        
        // Set the employments
        record.setEmployments(employments);
        
        ProfessionalActivities professionalActivities = new ProfessionalActivities();
        professionalActivities.setCount(5);
        professionalActivities.setProfessionalActivities(new ArrayList<>());
        professionalActivities.getProfessionalActivities().add(getProfessionalActivity(1, "Org # 1", "Fake role title", "https://test.orcid.org/", "distinction", false));
        
        // Set the professional activities
        record.setProfessionalActivities(professionalActivities);
        
        Fundings fundings = new Fundings();
        fundings.setSelfAssertedCount(0);
        fundings.setValidatedCount(1);
        record.setFundings(fundings);
        
        
        
        Works works = new Works();
        works.setSelfAssertedCount(0);
        works.setValidatedCount(1);
        record.setWorks(works);
        
        PeerReviews peerReviews = new PeerReviews();
        peerReviews.setPeerReviewPublicationGrants(6);
        peerReviews.setSelfAssertedCount(0);
        peerReviews.setTotal(6);
        record.setPeerReviews(peerReviews);
        
        //Set email domains
        EmailDomains emailDomains = new EmailDomains();
        emailDomains.setCount(4);
        emailDomains.setEmailDomains(new ArrayList<EmailDomain>());
        emailDomains.getEmailDomains().add(getEmailDomain("sometrusted.org"));
        record.setEmailDomains(emailDomains);
        
        //Set education/qualifications
        EducationQualifications educationQualifications = new EducationQualifications();
        educationQualifications.setCount(6);
        educationQualifications.setEducationQualifications(new ArrayList<EducationQualification>());
        educationQualifications.getEducationQualifications().add(getEducationQualification(1, "Org # 1", "Fake role title", "https://test.orcid.org/", "education", false));
        record.setEducationQualifications(educationQualifications);
        //Set research resources
        ResearchResources researchResources = new ResearchResources();
        researchResources.setSelfAssertedCount(0);
        researchResources.setValidatedCount(1);
        record.setResearchResources(researchResources);   

        return record;
    }
    
    private ExternalIdentifier getExternalIdentifier(String type, int putCode, boolean validated) {
        ExternalIdentifier ei1 = new ExternalIdentifier();
        ei1.setPutCode(Long.valueOf(putCode));
        ei1.setExternalIdType(type);
        ei1.setExternalIdValue(String.valueOf(putCode));
        ei1.setExternalIdUrl("https://test.orcid.org/" + putCode);
        ei1.setValidated(validated);
        return ei1;
    }
    
    private Employment getEmployment(int putCode, String role, String org, String url, boolean validated) {
        Employment e = new Employment();
        e.setPutCode(Long.valueOf(putCode));
        e.setEndDate(getEndDate());
        e.setStartDate(getStartDate());
        e.setOrganizationName(org);
        e.setRole(role);        
        e.setUrl(url);
        e.setValidated(validated);
        return e;
    }
    
    private ProfessionalActivity getProfessionalActivity(int putCode, String role, String org, String url, String type, boolean validated) {
        ProfessionalActivity pa = new ProfessionalActivity();
        pa.setPutCode(Long.valueOf(putCode));
        pa.setEndDate(getEndDate());
        pa.setStartDate(getStartDate());
        pa.setOrganizationName(org);
        pa.setRole(role);        
        pa.setUrl(url);
        pa.setValidated(validated);
        pa.setType(type);        
        return pa;
    }
    
    private EducationQualification getEducationQualification(int putCode, String role, String org, String url, String type, boolean validated) {
        EducationQualification eq = new EducationQualification();
        eq.setPutCode(Long.valueOf(putCode));
        eq.setEndDate(getEndDate());
        eq.setStartDate(getStartDate());
        eq.setOrganizationName(org);
        eq.setRole(role);        
        eq.setUrl(url);
        eq.setValidated(validated);
        eq.setType(type);        
        return eq;
    }
    
    private EmailDomain getEmailDomain(String domainValue) {
        EmailDomain emailDomain = new EmailDomain();
        emailDomain.setValue(domainValue);
        //emailDomain.setCreatedDate(created);
        //emailDomain.setLastModified(modified);
        return emailDomain;
    }
    
    private FuzzyDate getEndDate() {
        return new FuzzyDate(new Year(2024), new Month(12), new Day(31));
    }
    
    private FuzzyDate getStartDate() {
        return new FuzzyDate(new Year(2020), new Month(1), new Day(1));
    }
    
    
    private Date getEmailDomainCreatedDate() {
        return Date.valueOf(new FuzzyDate(new Year(2020), new Month(1), new Day(1)));
    }
    
    private Date getEmailDomainLastModified() {
        return Date.valueOf(new FuzzyDate(new Year(2020), new Month(1), new Day(1)));
    }
}
