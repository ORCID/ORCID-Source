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
package org.orcid.test.helper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.orcid.jaxb.model.common_rc4.Country;
import org.orcid.jaxb.model.common_rc4.Filterable;
import org.orcid.jaxb.model.common_rc4.Iso3166Country;
import org.orcid.jaxb.model.common_rc4.LastModifiedDate;
import org.orcid.jaxb.model.common_rc4.Organization;
import org.orcid.jaxb.model.common_rc4.OrganizationAddress;
import org.orcid.jaxb.model.common_rc4.Title;
import org.orcid.jaxb.model.common_rc4.Url;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.common_rc4.VisibilityType;
import org.orcid.jaxb.model.groupid_rc4.GroupIdRecord;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_rc4.ActivitiesContainer;
import org.orcid.jaxb.model.record_rc4.Activity;
import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.jaxb.model.record_rc4.Addresses;
import org.orcid.jaxb.model.record_rc4.Education;
import org.orcid.jaxb.model.record_rc4.Email;
import org.orcid.jaxb.model.record_rc4.Emails;
import org.orcid.jaxb.model.record_rc4.Employment;
import org.orcid.jaxb.model.record_rc4.ExternalID;
import org.orcid.jaxb.model.record_rc4.ExternalIDs;
import org.orcid.jaxb.model.record_rc4.Funding;
import org.orcid.jaxb.model.record_rc4.FundingTitle;
import org.orcid.jaxb.model.record_rc4.FundingType;
import org.orcid.jaxb.model.record_rc4.Keyword;
import org.orcid.jaxb.model.record_rc4.Keywords;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.jaxb.model.record_rc4.PeerReview;
import org.orcid.jaxb.model.record_rc4.PeerReviewType;
import org.orcid.jaxb.model.record_rc4.Person;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc4.PersonalDetails;
import org.orcid.jaxb.model.record_rc4.Relationship;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;
import org.orcid.jaxb.model.record_rc4.Role;
import org.orcid.jaxb.model.record_rc4.Work;
import org.orcid.jaxb.model.record_rc4.WorkTitle;
import org.orcid.jaxb.model.record_rc4.WorkType;

public class Utils {
	public static void assertIsPublicOrSource(VisibilityType v, String sourceId) {
		if(v instanceof Filterable) {
			Filterable f = (Filterable) v;
			if(f.retrieveSourcePath().equals(sourceId)) {
				return;
			}
		}
		
		if(!Visibility.PUBLIC.equals(v.getVisibility())) {
			fail("Not public nor source");			
		}    	
    }
    
    public static void assertIsPublicOrSource(ActivitiesContainer c, String sourceId) {
    	Collection<? extends Activity> activities = c.retrieveActivities();
    	for(Activity a : activities) {
    		assertIsPublicOrSource(a, sourceId);
    	}
    }
    
    public static void assertIsPublicOrSource(Addresses elements, String sourceId) {
    	if(elements == null || elements.getAddress() == null) {
    		return;    		
    	}
    	
    	for(Address e : elements.getAddress()) {
    		assertIsPublicOrSource(e, sourceId);
    	}
    }
    
	public static void assertIsPublicOrSource(Keywords elements, String sourceId) {
		if(elements == null || elements.getKeywords() == null) {
    		return;    		
    	}
    	
    	for(Keyword e : elements.getKeywords()) {
    		assertIsPublicOrSource(e, sourceId);
    	}
	}
	
	public static void assertIsPublicOrSource(ResearcherUrls elements, String sourceId) {
		if(elements == null || elements.getResearcherUrls() == null) {
    		return;    		
    	}
    	
    	for(ResearcherUrl e : elements.getResearcherUrls()) {
    		assertIsPublicOrSource(e, sourceId);
    	}
	}
	
	public static void assertIsPublicOrSource(PersonExternalIdentifiers elements, String sourceId) {
		if(elements == null || elements.getExternalIdentifiers() == null) {
    		return;    		
    	}
    	
    	for(PersonExternalIdentifier e : elements.getExternalIdentifiers()) {
    		assertIsPublicOrSource(e, sourceId);
    	}
	}
	
	public static void assertIsPublicOrSource(Emails elements, String sourceId) {
		if (elements == null || elements.getEmails() == null) {
			return;
		}

		for (Email e : elements.getEmails()) {
			assertIsPublicOrSource(e, sourceId);
		}
	}

	public static void assertIsPublicOrSource(OtherNames elements, String sourceId) {
		if (elements == null || elements.getOtherNames() == null) {
			return;
		}

		for (OtherName e : elements.getOtherNames()) {
			assertIsPublicOrSource(e, sourceId);
		}
	}

	public static void assertIsPublicOrSource(PersonalDetails p, String sourceId) {
		if (p == null) {
			return;
		}

		assertIsPublicOrSource(p.getBiography(), sourceId);
		assertIsPublicOrSource(p.getOtherNames(), sourceId);
		assertIsPublicOrSource(p.getName(), sourceId);
	}

	public static void assertIsPublicOrSource(Person p, String sourceId) {
		if (p == null) {
			return;
		}
		assertIsPublicOrSource(p.getAddresses(), sourceId);
		assertIsPublicOrSource(p.getBiography(), sourceId);
		assertIsPublicOrSource(p.getEmails(), sourceId);
		assertIsPublicOrSource(p.getExternalIdentifiers(), sourceId);
		assertIsPublicOrSource(p.getKeywords(), sourceId);
		assertIsPublicOrSource(p.getName(), sourceId);
		assertIsPublicOrSource(p.getOtherNames(), sourceId);
		assertIsPublicOrSource(p.getResearcherUrls(), sourceId);
	}
    
    public static void verifyLastModified(LastModifiedDate l) {
    	assertNotNull(l);
    	assertNotNull(l.getValue());
    }
    
    public static Address getAddress() {
        Address address = new Address();
        address.setVisibility(Visibility.PUBLIC);
        address.setCountry(new Country(Iso3166Country.ES));
        return address;
    }    
    
    public static Education getEducation() {
        Education education = new Education();
        education.setDepartmentName("My department name");
        education.setRoleTitle("My Role");
        education.setOrganization(getOrganization());
        return education;
    }
    
    public static Employment getEmployment() {
        Employment employment = new Employment();
        employment.setDepartmentName("My department name");
        employment.setRoleTitle("My Role");
        employment.setOrganization(getOrganization());
        return employment;
    }
    
    public static Work getWork(String title) {
        Work work = new Work();
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        work.setWorkTitle(workTitle);
        work.setWorkType(WorkType.BOOK);
        work.setVisibility(Visibility.PUBLIC);
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.PART_OF);
        extId.setType(WorkExternalIdentifierType.AGR.value());
        extId.setValue("ext-id-" + System.currentTimeMillis());
        extId.setUrl(new Url("http://thisIsANewUrl.com"));
        extIds.getExternalIdentifier().add(extId);
        work.setWorkExternalIdentifiers(extIds);
        return work;
    }            
    
    public static PeerReview getPeerReview() {
        PeerReview peerReview = new PeerReview();
        ExternalIDs weis = new ExternalIDs();
        ExternalID wei1 = new ExternalID();
        wei1.setRelationship(Relationship.PART_OF);
        wei1.setUrl(new Url("http://myUrl.com"));
        wei1.setValue("work-external-identifier-id");
        wei1.setType(WorkExternalIdentifierType.DOI.value());
        weis.getExternalIdentifier().add(wei1);
        peerReview.setExternalIdentifiers(weis);
        peerReview.setGroupId("issn:0000003");
        peerReview.setOrganization(getOrganization());
        peerReview.setRole(Role.CHAIR);
        peerReview.setSubjectContainerName(new Title("subject-container-name"));
        peerReview.setSubjectExternalIdentifier(wei1);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("work-title"));
        peerReview.setSubjectName(workTitle);
        peerReview.setSubjectType(WorkType.DATA_SET);
        peerReview.setType(PeerReviewType.EVALUATION);
        return peerReview;
    }
    
    public static Funding getFunding(){
        Funding newFunding = new Funding();
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title("Public Funding # 2"));
        newFunding.setTitle(title);
        newFunding.setType(FundingType.AWARD);
        ExternalID fExtId = new ExternalID();
        fExtId.setRelationship(Relationship.PART_OF);
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        fExtId.setUrl(new Url("http://fundingExtId.com"));
        fExtId.setValue("new-funding-ext-id");
        ExternalIDs fExtIds = new ExternalIDs();
        fExtIds.getExternalIdentifier().add(fExtId);
        newFunding.setExternalIdentifiers(fExtIds);
        newFunding.setOrganization(getOrganization());
        return newFunding;
    }
    
    public static Organization getOrganization() {
        Organization org = new Organization();
        org.setName("Org Name");
        OrganizationAddress add = new OrganizationAddress();
        add.setCity("city");
        add.setCountry(Iso3166Country.TT);
        org.setAddress(add);
        return org;
    }
    
    public static PersonExternalIdentifier getPersonExternalIdentifier() {
        PersonExternalIdentifier newExtId = new PersonExternalIdentifier();
        newExtId.setType("new-common-name");
        newExtId.setValue("new-reference");
        newExtId.setUrl(new Url("http://newUrl.com"));
        newExtId.setVisibility(Visibility.LIMITED);
        return newExtId;
    }
    
    public static Keyword getKeyword() {
        Keyword keyword = new Keyword();
        keyword.setContent("New keyword");
        keyword.setVisibility(Visibility.LIMITED);
        return keyword;
    }
    
    public static OtherName getOtherName() {
        OtherName otherName = new OtherName();
        otherName.setContent("New Other Name");
        otherName.setVisibility(Visibility.LIMITED);
        return otherName;
    }
    
    public static ResearcherUrl getResearcherUrl() {
        ResearcherUrl rUrl = new ResearcherUrl();
        rUrl.setUrl(new Url("http://www.myRUrl.com"));
        rUrl.setUrlName("My researcher Url");
        rUrl.setVisibility(Visibility.LIMITED);
        return rUrl;
    }
    
    public static GroupIdRecord getGroupIdRecord() {
        GroupIdRecord newRecord = new GroupIdRecord();
        newRecord.setGroupId("issn:0000005");
        newRecord.setName("TestGroup5");
        newRecord.setDescription("TestDescription5");
        newRecord.setType("publisher");
        return newRecord;
    }    
    
    public static Long getPutCode(Response response) {
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        return Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
    }
}
