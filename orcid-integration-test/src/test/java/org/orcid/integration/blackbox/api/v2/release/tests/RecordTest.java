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
package org.orcid.integration.blackbox.api.v2.release.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.integration.blackbox.api.v2.release.MemberV2ApiClientImpl;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record.summary_v2.FundingGroup;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.Fundings;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviews;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class RecordTest extends BlackBoxBaseV2Release {    
    @Resource(name = "memberV2ApiClient")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient")
    private PublicV2ApiClientImpl publicV2ApiClient;
        
    @Test
    public void testViewRecordFromMemberAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient.viewRecord(getUser1OrcidId(), accessToken);
        assertNotNull(response);        
        assertEquals("invalid " + response,200,response.getStatus());        
        Record record = response.getEntity(Record.class);
        assertNotNull(record);
        assertNotNull(record.getOrcidIdentifier());
        assertEquals(getUser1OrcidId(), record.getOrcidIdentifier().getPath());               
        //Check the visibility of every activity that exists
        if(record.getActivitiesSummary() != null) {
            //Educations
            if(record.getActivitiesSummary().getEducations() != null) {
                Educations e = record.getActivitiesSummary().getEducations();
                if(e.getSummaries() != null) {
                    for(EducationSummary s : e.getSummaries()) {
                        assertNotNull(s.getSource());
                        assertNotNull(s.getVisibility());
                        Visibility v = s.getVisibility();
                        //If the visibility is PRIVATE the client should be the owner
                        if(Visibility.PRIVATE.equals(v)) {                            
                            assertEquals(getClient1ClientId(), s.getSource().retrieveSourcePath());
                        }
                    }
                }
            }
            //Employments
            if(record.getActivitiesSummary().getEmployments() != null) {
                Employments e = record.getActivitiesSummary().getEmployments();
                if(e.getSummaries() != null) {
                    for(EmploymentSummary s : e.getSummaries()) {
                        assertNotNull(s.getSource());
                        assertNotNull(s.getVisibility());
                        Visibility v = s.getVisibility();
                        //If the visibility is PRIVATE the client should be the owner
                        if(Visibility.PRIVATE.equals(v)) {                            
                            assertEquals(getClient1ClientId(), s.getSource().retrieveSourcePath());
                        }
                    }
                }
            }
            //Fundings
            if(record.getActivitiesSummary().getFundings() != null) {
                Fundings f = record.getActivitiesSummary().getFundings();
                List<FundingGroup> groups = f.getFundingGroup();
                if(groups != null) {
                    for(FundingGroup fGroup : groups) {
                        List<FundingSummary> summaries = fGroup.getFundingSummary();
                        if(summaries != null) {
                            for(FundingSummary s : summaries) {
                                assertNotNull(s.getSource());
                                assertNotNull(s.getVisibility());
                                Visibility v = s.getVisibility();
                                //If the visibility is PRIVATE the client should be the owner
                                if(Visibility.PRIVATE.equals(v)) {                            
                                    assertEquals(getClient1ClientId(), s.getSource().retrieveSourcePath());
                                }
                            }
                        }
                    }
                }
            }
            //PeerReviews
            if(record.getActivitiesSummary().getPeerReviews() != null) {
                PeerReviews p = record.getActivitiesSummary().getPeerReviews();
                List<PeerReviewGroup> groups = p.getPeerReviewGroup();
                if(groups != null) {
                    for(PeerReviewGroup pGroup : groups) {
                        List<PeerReviewSummary> summaries = pGroup.getPeerReviewSummary();
                        if(summaries != null) {
                            for(PeerReviewSummary s : summaries) {
                                assertNotNull(s.getSource());
                                assertNotNull(s.getVisibility());
                                Visibility v = s.getVisibility();
                                //If the visibility is PRIVATE the client should be the owner
                                if(Visibility.PRIVATE.equals(v)) {                            
                                    assertEquals(getClient1ClientId(), s.getSource().retrieveSourcePath());
                                }
                            }
                        }
                    }
                }
            }
            //Works
            if(record.getActivitiesSummary().getWorks() != null) {
                Works w = record.getActivitiesSummary().getWorks();
                List<WorkGroup> groups = w.getWorkGroup();
                if(groups != null) {
                    for(WorkGroup wGroup : groups) {
                        List<WorkSummary> summaries = wGroup.getWorkSummary();
                        if(summaries != null) {
                            for(WorkSummary s : summaries) {
                                assertNotNull(s.getSource());
                                assertNotNull(s.getVisibility());
                                Visibility v = s.getVisibility();
                                //If the visibility is PRIVATE the client should be the owner
                                if(Visibility.PRIVATE.equals(v)) {                            
                                    assertEquals(getClient1ClientId(), s.getSource().retrieveSourcePath());
                                }
                            }
                        }
                    }
                }
            }
        }
                        
        //Check the visibility of every biography elements that exists
        if(record.getPerson() != null) {
            //Address
            if(record.getPerson().getAddresses() != null) {
                Addresses addresses = record.getPerson().getAddresses();
                List<Address> list = addresses.getAddress();
                if(list != null) {
                    for(Address o : list) {
                        assertNotNull(o.getSource());
                        assertNotNull(o.getVisibility());
                        Visibility v = o.getVisibility();
                        //If the visibility is PRIVATE the client should be the owner
                        if(Visibility.PRIVATE.equals(v)) {                            
                            assertEquals(getClient1ClientId(), o.getSource().retrieveSourcePath());
                        }
                    }
                }
            }
            //Biography
            if(record.getPerson().getBiography() != null) {
                Biography b = record.getPerson().getBiography();
                if(b != null) {
                    assertNotNull(b.getVisibility());
                    if(Visibility.PRIVATE.equals(b.getVisibility())) {
                        fail("Visibility is private");
                    }
                }
            }
            //Emails
            if(record.getPerson().getEmails() != null) {
                Emails emails = record.getPerson().getEmails();
                List<Email> list = emails.getEmails();
                if(list != null) {
                    for(Email e : list) {
                        assertNotNull(e.getVisibility());
                        if(Visibility.PRIVATE.equals(e.getVisibility())) {
                            fail("Email " + e.getEmail() + " is private");
                        }
                    }
                }
            }
            //External identifiers
            if(record.getPerson().getExternalIdentifiers() != null) {
                PersonExternalIdentifiers extIds = record.getPerson().getExternalIdentifiers();
                List<PersonExternalIdentifier> list = extIds.getExternalIdentifiers();
                if(list != null) {
                    for(PersonExternalIdentifier e : list) {
                        assertNotNull(e.getVisibility());
                        if(Visibility.PRIVATE.equals(e.getVisibility())) {
                            assertEquals(getClient1ClientId(), e.getSource().retrieveSourcePath());
                        }
                    }
                }
            }
            //Keywords
            if(record.getPerson().getKeywords() != null) {
                Keywords keywords = record.getPerson().getKeywords();
                List<Keyword> list = keywords.getKeywords();
                if(list != null) {
                    for(Keyword e : list) {
                        assertNotNull(e.getVisibility());
                        if(Visibility.PRIVATE.equals(e.getVisibility())) {
                            assertEquals(getClient1ClientId(), e.getSource().retrieveSourcePath());
                        }
                    }
                }
            }
            //Name
            if(record.getPerson().getName() != null) {
               Name name =  record.getPerson().getName();
               if(Visibility.PRIVATE.equals(name.getVisibility())) {
                  fail("Name is private"); 
               }
            }
            //Other names
            if(record.getPerson().getOtherNames() != null) {
                OtherNames otherNames = record.getPerson().getOtherNames();
                List<OtherName> list = otherNames.getOtherNames();
                if(list != null) {
                    for(OtherName e : list) {
                        assertNotNull(e.getVisibility());
                        if(Visibility.PRIVATE.equals(e.getVisibility())) {
                            assertEquals(getClient1ClientId(), e.getSource().retrieveSourcePath());
                        }
                    }
                }
            }
            //Researcher urls
            if(record.getPerson().getResearcherUrls() != null) {
                ResearcherUrls rUrls = record.getPerson().getResearcherUrls();
                List<ResearcherUrl> list = rUrls.getResearcherUrls();
                if(list != null) {
                    for(ResearcherUrl e : list) {
                        assertNotNull(e.getVisibility());
                        if(Visibility.PRIVATE.equals(e.getVisibility())) {
                            assertEquals(getClient1ClientId(), e.getSource().retrieveSourcePath());
                        }
                    }
                }
            }            
        }
    }    
    
    @Test
    public void testViewRecordFromPublicAPI() {
        ClientResponse response = publicV2ApiClient.viewRecordXML(getUser1OrcidId());
        assertNotNull(response);        
        assertEquals("invalid " + response,200,response.getStatus());        
        Record record = response.getEntity(Record.class);
        assertNotNull(record);
        assertNotNull(record.getOrcidIdentifier());
        assertEquals(getUser1OrcidId(), record.getOrcidIdentifier().getPath());               
        //Check the visibility of every activity that exists
        if(record.getActivitiesSummary() != null) {
            if(record.getActivitiesSummary() != null) {
                //Educations
                if(record.getActivitiesSummary().getEducations() != null) {
                    Educations e = record.getActivitiesSummary().getEducations();
                    if(e.getSummaries() != null) {
                        for(EducationSummary s : e.getSummaries()) {
                            assertNotNull(s.getSource());                            
                            assertEquals(Visibility.PUBLIC, s.getVisibility());                            
                        }
                    }
                }
                //Employments
                if(record.getActivitiesSummary().getEmployments() != null) {
                    Employments e = record.getActivitiesSummary().getEmployments();
                    if(e.getSummaries() != null) {
                        for(EmploymentSummary s : e.getSummaries()) {
                            assertNotNull(s.getSource());
                            assertEquals(Visibility.PUBLIC, s.getVisibility());
                        }
                    }
                }
                //Fundings
                if(record.getActivitiesSummary().getFundings() != null) {
                    Fundings f = record.getActivitiesSummary().getFundings();
                    List<FundingGroup> groups = f.getFundingGroup();
                    if(groups != null) {
                        for(FundingGroup fGroup : groups) {
                            List<FundingSummary> summaries = fGroup.getFundingSummary();
                            if(summaries != null) {
                                for(FundingSummary s : summaries) {
                                    assertNotNull(s.getSource());
                                    assertEquals(Visibility.PUBLIC, s.getVisibility());
                                }
                            }
                        }
                    }
                }
                //PeerReviews
                if(record.getActivitiesSummary().getPeerReviews() != null) {
                    PeerReviews p = record.getActivitiesSummary().getPeerReviews();
                    List<PeerReviewGroup> groups = p.getPeerReviewGroup();
                    if(groups != null) {
                        for(PeerReviewGroup pGroup : groups) {
                            List<PeerReviewSummary> summaries = pGroup.getPeerReviewSummary();
                            if(summaries != null) {
                                for(PeerReviewSummary s : summaries) {
                                    assertNotNull(s.getSource());
                                    assertEquals(Visibility.PUBLIC, s.getVisibility());
                                }
                            }
                        }
                    }
                }
                //Works
                if(record.getActivitiesSummary().getWorks() != null) {
                    Works w = record.getActivitiesSummary().getWorks();
                    List<WorkGroup> groups = w.getWorkGroup();
                    if(groups != null) {
                        for(WorkGroup wGroup : groups) {
                            List<WorkSummary> summaries = wGroup.getWorkSummary();
                            if(summaries != null) {
                                for(WorkSummary s : summaries) {
                                    assertNotNull(s.getSource());
                                    assertEquals(Visibility.PUBLIC, s.getVisibility());
                                }
                            }
                        }
                    }
                }
            }
        }
                        
        //Check the visibility of every biography elements that exists
        if(record.getPerson() != null) {
          //Address
            if(record.getPerson().getAddresses() != null) {
                Addresses addresses = record.getPerson().getAddresses();
                List<Address> list = addresses.getAddress();
                if(list != null) {
                    for(Address o : list) {
                        assertNotNull(o.getSource());
                        assertEquals(Visibility.PUBLIC, o.getVisibility());
                    }
                }
            }
            //Biography
            if(record.getPerson().getBiography() != null) {
                Biography b = record.getPerson().getBiography();
                if(b != null) {
                    assertNotNull(b.getVisibility());
                    assertEquals(Visibility.PUBLIC, b.getVisibility());
                }
            }
            //Emails
            if(record.getPerson().getEmails() != null) {
                Emails emails = record.getPerson().getEmails();
                List<Email> list = emails.getEmails();
                if(list != null) {
                    for(Email e : list) {
                        assertNotNull(e.getVisibility());
                        assertEquals(Visibility.PUBLIC, e.getVisibility());
                    }
                }
            }
            //External identifiers
            if(record.getPerson().getExternalIdentifiers() != null) {
                PersonExternalIdentifiers extIds = record.getPerson().getExternalIdentifiers();
                List<PersonExternalIdentifier> list = extIds.getExternalIdentifiers();
                if(list != null) {
                    for(PersonExternalIdentifier e : list) {
                        assertEquals(Visibility.PUBLIC, e.getVisibility());
                    }
                }
            }
            //Keywords
            if(record.getPerson().getKeywords() != null) {
                Keywords keywords = record.getPerson().getKeywords();
                List<Keyword> list = keywords.getKeywords();
                if(list != null) {
                    for(Keyword e : list) {
                        assertEquals(Visibility.PUBLIC, e.getVisibility());
                    }
                }
            }
            //Name
            if(record.getPerson().getName() != null) {
               Name name =  record.getPerson().getName();
               assertEquals(Visibility.PUBLIC, name.getVisibility());
            }
            //Other names
            if(record.getPerson().getOtherNames() != null) {
                OtherNames otherNames = record.getPerson().getOtherNames();
                List<OtherName> list = otherNames.getOtherNames();
                if(list != null) {
                    for(OtherName e : list) {
                        assertEquals(Visibility.PUBLIC, e.getVisibility());
                    }
                }
            }
            //Researcher urls
            if(record.getPerson().getResearcherUrls() != null) {
                ResearcherUrls rUrls = record.getPerson().getResearcherUrls();
                List<ResearcherUrl> list = rUrls.getResearcherUrls();
                if(list != null) {
                    for(ResearcherUrl e : list) {
                        assertEquals(Visibility.PUBLIC, e.getVisibility());
                    }
                }
            } 
        }
    }
    
    public String getAccessToken() throws InterruptedException, JSONException {
        return getAccessToken(getScopes(ScopePathType.READ_LIMITED));
    }
}
