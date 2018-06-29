package org.orcid.pojo.ajaxForm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.orcid.jaxb.model.v3.rc1.common.CreatedDate;
import org.orcid.jaxb.model.v3.rc1.common.Day;
import org.orcid.jaxb.model.v3.rc1.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc1.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc1.common.Month;
import org.orcid.jaxb.model.v3.rc1.common.Organization;
import org.orcid.jaxb.model.v3.rc1.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.rc1.common.Source;
import org.orcid.jaxb.model.v3.rc1.common.SourceOrcid;
import org.orcid.jaxb.model.v3.rc1.common.Url;
import org.orcid.jaxb.model.v3.rc1.common.Year;
import org.orcid.jaxb.model.v3.rc1.record.Affiliation;
import org.orcid.jaxb.model.v3.rc1.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc1.record.Distinction;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.DistinctionSummary;

public class AffiliationFormTest extends AffiliationFormTestBase {    
    
    protected AffiliationForm getAffiliationForm() {
        AffiliationForm form = new AffiliationForm();
        form.setDateSortString(PojoUtil.createDateSortString(getAffiliationSummary()));
        form.setAffiliationType(Text.valueOf(AffiliationType.DISTINCTION.value()));
        form.setUrl(Text.valueOf("https://test.orcid.org"));
        Date created = new Date();
        created.setDay(String.valueOf(this.created.getDay()));
        created.setMonth(String.valueOf(this.created.getMonth()));
        created.setYear(String.valueOf(this.created.getYear()));
        form.setCreatedDate(created);
        
        Date lastModified = new Date();
        lastModified.setDay(String.valueOf(this.lastModified.getDay()));
        lastModified.setMonth(String.valueOf(this.lastModified.getMonth()));
        lastModified.setYear(String.valueOf(this.lastModified.getYear()));
        form.setLastModified(lastModified);
        
        form.setPutCode(Text.valueOf("1"));
        
        form.setDepartmentName(Text.valueOf("department-name"));
        
        form.setRoleTitle(Text.valueOf("role-title"));
        
        Date endDate = new Date();
        endDate.setDay("01");
        endDate.setMonth("01");
        endDate.setYear("2018");
        form.setEndDate(endDate);
        
        Date startDate = new Date();
        startDate.setDay("31");
        startDate.setMonth("12");
        startDate.setYear("2019");
        form.setStartDate(startDate);
        
        Visibility v = new Visibility();
        v.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.PRIVATE);
        form.setVisibility(v);
        
        form.setSource("0000-0000-0000-0000");
                
        ActivityExternalIdentifier e1 = new ActivityExternalIdentifier();
        e1.setExternalIdentifierId(Text.valueOf("00001"));
        e1.setExternalIdentifierType(Text.valueOf("t1"));
        e1.setRelationship(Text.valueOf("self"));
        e1.setUrl(Text.valueOf("https://test.orcid.org"));
        
        ActivityExternalIdentifier e2 = new ActivityExternalIdentifier();
        e2.setExternalIdentifierId(Text.valueOf("00002"));
        e2.setExternalIdentifierType(Text.valueOf("t2"));
        e2.setRelationship(Text.valueOf("self"));
        e2.setUrl(Text.valueOf("https://test.orcid.org"));
                
        List<ActivityExternalIdentifier> extIds = new ArrayList<ActivityExternalIdentifier>();
        extIds.add(e1);
        extIds.add(e2);
        form.setAffiliationExternalIdentifiers(extIds);
        
        form.setCity(Text.valueOf("city"));
        form.setCountry(Text.valueOf("US"));
        form.setRegion(Text.valueOf("region"));
        form.setAffiliationName(Text.valueOf("org-1"));
    
        return form;
    }
    
    protected Affiliation getAffiliation() {
        Affiliation affiliation = new Distinction();
        affiliation.setDisplayIndex("0");
        affiliation.setUrl(new Url("https://test.orcid.org"));
        affiliation.setCreatedDate(new CreatedDate(created));
        affiliation.setLastModifiedDate(new LastModifiedDate(lastModified));
        affiliation.setPutCode(1L);
        affiliation.setPath("/distinction/1");
        
        affiliation.setDepartmentName("department-name");
        affiliation.setEndDate(new FuzzyDate(new Year(2018), new Month(1), new Day(1)));
        affiliation.setRoleTitle("role-title");
        
        affiliation.setStartDate(new FuzzyDate(new Year(2019), new Month(12), new Day(31)));
        affiliation.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.PRIVATE);
        
        Source source = new Source();
        source.setSourceOrcid(new SourceOrcid("0000-0000-0000-0000"));
        affiliation.setSource(source);
        
        ExternalID e1 = new ExternalID();
        e1.setRelationship(Relationship.SELF);
        e1.setType("t1");
        e1.setUrl(new Url("https://test.orcid.org"));
        e1.setValue("00001");
        
        ExternalID e2 = new ExternalID();
        e2.setRelationship(Relationship.SELF);
        e2.setType("t2");
        e2.setUrl(new Url("https://test.orcid.org"));
        e2.setValue("00002");        
        
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(e1);
        extIds.getExternalIdentifier().add(e2);
        
        affiliation.setExternalIDs(extIds);
        
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        address.setRegion("region");
        Organization org = new Organization();
        org.setAddress(address);
        org.setName("org-1");
        
        affiliation.setOrganization(org);
        return affiliation;
    }
    
    protected AffiliationSummary getAffiliationSummary() {
        AffiliationSummary affiliationSummary = new DistinctionSummary();
        affiliationSummary.setCreatedDate(new CreatedDate(created));
        affiliationSummary.setLastModifiedDate(new LastModifiedDate(lastModified));
        affiliationSummary.setPutCode(1L);
        affiliationSummary.setPath("/distinction/1");
        
        affiliationSummary.setDepartmentName("department-name");
        affiliationSummary.setDisplayIndex("0");
        affiliationSummary.setEndDate(new FuzzyDate(new Year(2018), new Month(1), new Day(1)));
        
        affiliationSummary.setRoleTitle("role-title");
        
        affiliationSummary.setStartDate(new FuzzyDate(new Year(2019), new Month(12), new Day(31)));
        affiliationSummary.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.PRIVATE);
        
        Source source = new Source();
        source.setSourceOrcid(new SourceOrcid("0000-0000-0000-0000"));
        affiliationSummary.setSource(source);
        
        ExternalID e1 = new ExternalID();
        e1.setRelationship(Relationship.SELF);
        e1.setType("t1");
        e1.setUrl(new Url("https://test.orcid.org"));
        e1.setValue("00001");
        
        ExternalID e2 = new ExternalID();
        e2.setRelationship(Relationship.SELF);
        e2.setType("t2");
        e2.setUrl(new Url("https://test.orcid.org"));
        e2.setValue("00002");
        
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(e1);
        extIds.getExternalIdentifier().add(e2);
        
        affiliationSummary.setExternalIDs(extIds);
        
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        address.setRegion("region");
        Organization org = new Organization();
        org.setAddress(address);
        org.setName("org-1");
        
        affiliationSummary.setOrganization(org);
        
        return affiliationSummary;
    }
    
    @Test
    public void equalsTest() {
        AffiliationForm f1 = getAffiliationForm();
        AffiliationForm f2 = getAffiliationForm();
        assertTrue(f1.equals(f2));
    }
    
    @Test
    public void fromAffiliationSummaryTest() {
        AffiliationForm f1 = getAffiliationForm();
        AffiliationSummary s1 = getAffiliationSummary();
        // Summary doesn't have url
        f1.setUrl(null);
        assertEquals(f1, AffiliationForm.valueOf(s1));
    }
    
    @Test
    public void fromAffiliationTest() {
        AffiliationForm f1 = getAffiliationForm();
        Affiliation aff = getAffiliation();
        assertEquals(f1, AffiliationForm.valueOf(aff));
    }
    
    @Test
    public void toAffiliationTest() {
        AffiliationForm f1 = getAffiliationForm();
        Affiliation aff = getAffiliation();
        assertEquals(aff, f1.toAffiliation());
    }        
}
