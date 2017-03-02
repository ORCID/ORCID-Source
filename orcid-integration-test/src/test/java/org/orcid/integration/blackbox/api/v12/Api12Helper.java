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
package org.orcid.integration.blackbox.api.v12;

import static org.junit.Assert.assertEquals;

import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Amount;
import org.orcid.jaxb.model.message.ContributorEmail;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingContributor;
import org.orcid.jaxb.model.message.FundingContributorAttributes;
import org.orcid.jaxb.model.message.FundingContributorRole;
import org.orcid.jaxb.model.message.FundingContributors;
import org.orcid.jaxb.model.message.FundingExternalIdentifier;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.FundingExternalIdentifiers;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkType;

import com.sun.jersey.api.client.ClientResponse;

public class Api12Helper {    

    protected static void addWork(String userOrcid, String token, String title, T2OAuthAPIService<ClientResponse> oauthT2Client) {
        String id = String.valueOf(System.currentTimeMillis());
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        OrcidWork orcidWork = new OrcidWork();
        orcidWorks.getOrcidWork().add(orcidWork);
        orcidWork.setWorkType(WorkType.JOURNAL_ARTICLE);
        orcidWork.setVisibility(Visibility.LIMITED);
        WorkTitle workTitle = new WorkTitle();
        orcidWork.setWorkTitle(workTitle);
        WorkExternalIdentifier wei = new WorkExternalIdentifier();
        wei.setWorkExternalIdentifierId(new WorkExternalIdentifierId(id));
        wei.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        WorkExternalIdentifiers extIds = new WorkExternalIdentifiers();
        extIds.getWorkExternalIdentifier().add(wei);
        orcidWork.setWorkExternalIdentifiers(extIds);
        workTitle.setTitle(new Title(title));
        ClientResponse clientResponse = oauthT2Client.addWorksXml(userOrcid, orcidMessage, token);
        assertEquals(201, clientResponse.getStatus());        
    }
    
    protected static void addFunding(String userOrcid, String token, String title, T2OAuthAPIService<ClientResponse> oauthT2Client) {        
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);

        FundingList fundings = new FundingList();
        Funding funding = new Funding();
        funding.setVisibility(Visibility.LIMITED);
        FundingTitle fundingTitle = new FundingTitle();
        fundingTitle.setTitle(new Title(title));
        funding.setTitle(fundingTitle);
        funding.setType(FundingType.SALARY_AWARD);        
        Amount amount = new Amount();
        amount.setCurrencyCode("CRC");
        amount.setContent("1,250,000");
        funding.setAmount(amount);
        funding.setStartDate(new FuzzyDate(2010, 1, 1));
        funding.setEndDate(new FuzzyDate(2013, 1, 1));
        funding.setDescription("My Grant description");
        funding.setUrl(new Url("http://url.com"));
        Organization org = new Organization();
        org.setName("Orcid Integration Test Org");
        OrganizationAddress add = new OrganizationAddress();
        add.setCity("My City");
        add.setCountry(Iso3166Country.CR);
        org.setAddress(add);
        funding.setOrganization(org);
        FundingExternalIdentifier extIdentifier = new FundingExternalIdentifier();
        extIdentifier.setType(FundingExternalIdentifierType.fromValue("grant_number"));
        extIdentifier.setUrl(new Url("http://url.com"));
        extIdentifier.setValue("My value");
        FundingExternalIdentifiers extIdentifiers = new FundingExternalIdentifiers();
        extIdentifiers.getFundingExternalIdentifier().add(extIdentifier);
        funding.setFundingExternalIdentifiers(extIdentifiers);
        FundingContributors contributors = new FundingContributors();
        FundingContributor contributor = new FundingContributor();
        contributor.setCreditName(new CreditName("My Credit Name"));
        contributor.setContributorEmail(new ContributorEmail("my.email@orcid-integration-test.com"));
        FundingContributorAttributes attributes = new FundingContributorAttributes();
        attributes.setContributorRole(FundingContributorRole.LEAD);
        contributor.setContributorAttributes(attributes);
        contributors.getContributor().add(contributor);
        funding.setFundingContributors(contributors);
        fundings.getFundings().add(funding);
        orcidMessage.getOrcidProfile().getOrcidActivities().setFundings(fundings);
        
        ClientResponse clientResponse = oauthT2Client.addFundingXml(userOrcid, orcidMessage, token);
        assertEquals(201, clientResponse.getStatus());
    }
    
    protected static void addAffiliation(String userOrcid, String token, String orgName, T2OAuthAPIService<ClientResponse> oauthT2Client) {
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);
        Affiliation affiliation = new Affiliation();
        affiliation.setVisibility(Visibility.LIMITED);
        affiliations.getAffiliation().add(affiliation);
        affiliation.setType(AffiliationType.EDUCATION);
        Organization organization = new Organization();
        affiliation.setOrganization(organization);
        organization.setName(orgName);
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization.setAddress(organizationAddress);
        organizationAddress.setCity("Edinburgh");
        organizationAddress.setCountry(Iso3166Country.GB);
        
        ClientResponse clientResponse = oauthT2Client.addAffiliationsXml(userOrcid, orcidMessage, token);
        assertEquals(201, clientResponse.getStatus());        
    }    
    
    protected static String createRecord(String token, OrcidMessage newRecord, T2OAuthAPIService<ClientResponse> oauthT2Client) throws Exception {
        ClientResponse response = oauthT2Client.createProfileXML(newRecord, token);
        // assign orcid any time it's created for use in tear-down
        String orcidFromLocation = response.getLocation().getPath();
        orcidFromLocation = orcidFromLocation.replace("/orcid-profile", "");
        orcidFromLocation = orcidFromLocation.substring(orcidFromLocation.lastIndexOf("/") + 1);
        return orcidFromLocation;        
    }
}
