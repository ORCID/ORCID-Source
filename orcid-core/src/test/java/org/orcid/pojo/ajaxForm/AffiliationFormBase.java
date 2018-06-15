package org.orcid.pojo.ajaxForm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

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
import org.orcid.utils.DateUtils;

public class AffiliationFormBase {
    XMLGregorianCalendar created;
    XMLGregorianCalendar lastModified;
        
    {
        Long now = System.currentTimeMillis();
        Long then = now + 1000;
        created = DateUtils.convertToXMLGregorianCalendar(now);
        lastModified = DateUtils.convertToXMLGregorianCalendar(then);               
    }
    
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
        Affiliation aff = new Distinction();
        aff.setUrl(new Url("https://test.orcid.org"));
        aff.setCreatedDate(new CreatedDate(created));
        aff.setLastModifiedDate(new LastModifiedDate(lastModified));
        aff.setPutCode(1L);
        aff.setPath("/distinction/1");
        
        aff.setDepartmentName("department-name");
        aff.setDisplayIndex("0");
        aff.setEndDate(new FuzzyDate(new Year(2018), new Month(1), new Day(1)));
        
        aff.setRoleTitle("role-title");
        
        aff.setStartDate(new FuzzyDate(new Year(2019), new Month(12), new Day(31)));
        aff.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.PRIVATE);
        
        Source source = new Source();
        source.setSourceOrcid(new SourceOrcid("0000-0000-0000-0000"));
        aff.setSource(source);
        
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
        
        aff.setExternalIDs(extIds);
        
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        address.setRegion("region");
        Organization org = new Organization();
        org.setAddress(address);
        org.setName("org-1");
        
        aff.setOrganization(org);
        return aff;
    }
    
    protected AffiliationSummary getAffiliationSummary() {
        AffiliationSummary aff = new DistinctionSummary();
        aff.setCreatedDate(new CreatedDate(created));
        aff.setLastModifiedDate(new LastModifiedDate(lastModified));
        aff.setPutCode(1L);
        aff.setPath("/distinction/1");
        
        aff.setDepartmentName("department-name");
        aff.setDisplayIndex("0");
        aff.setEndDate(new FuzzyDate(new Year(2018), new Month(1), new Day(1)));
        
        aff.setRoleTitle("role-title");
        
        aff.setStartDate(new FuzzyDate(new Year(2019), new Month(12), new Day(31)));
        aff.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.PRIVATE);
        
        Source source = new Source();
        source.setSourceOrcid(new SourceOrcid("0000-0000-0000-0000"));
        aff.setSource(source);
        
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
        
        aff.setExternalIDs(extIds);
        
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        address.setRegion("region");
        Organization org = new Organization();
        org.setAddress(address);
        org.setName("org-1");
        
        aff.setOrganization(org);
        
        return aff;
    }
}
