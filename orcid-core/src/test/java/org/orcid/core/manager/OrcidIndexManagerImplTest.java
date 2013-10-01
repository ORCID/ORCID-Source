/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.common.SolrDocument;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.impl.OrcidIndexManagerImpl;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationAddress;
import org.orcid.jaxb.model.message.AffiliationCountry;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdOrcid;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.GrantNumber;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidGrant;
import org.orcid.jaxb.model.message.OrcidGrants;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidPatent;
import org.orcid.jaxb.model.message.OrcidPatents;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PatentNumber;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.persistence.solr.entities.OrcidSolrDocument;
import org.springframework.test.annotation.Rollback;

/**
 * Tests for the invocation of Solr persistence. This class isn't required to
 * have a Solr instance running as it uses Mockito. The purpose of these tests
 * are to check the inner mappings of the index manager pass off to Solr with a
 * correctly populated OrcidSolrDocument.
 * 
 * @see SolrDao
 * @see OrcidSolrDocument
 * @see SolrDocument
 * @author jamesb
 */
public class OrcidIndexManagerImplTest extends BaseTest {

    @Resource
    private OrcidIndexManagerImpl orcidIndexManager;

    @Resource
    private VisibilityFilter visibilityFilter;

    @Mock
    private SolrDao solrDao;

    @Before
    public void initMocks() {
        orcidIndexManager.setSolrDao(solrDao);
    }

    @Test
    @Rollback
    public void fullyPopulatedOrcidPersistsAllSolrDocumentFields() throws Exception {

        orcidIndexManager.persistProfileInformationForIndexing(getStandardOrcid());
        verify(solrDao).persist(eq(fullyPopulatedSolrDocumentForPersistence()));

    }

    @Test
    @Rollback
    public void checkSubtitlesPersisted() throws Exception {

        OrcidProfile subtitledWorksProfile = getOrcidWithSubtitledWork();
        OrcidSolrDocument standardWorkListing = solrDocWithAdditionalSubtitles();
        orcidIndexManager.persistProfileInformationForIndexing(subtitledWorksProfile);
        verify(solrDao).persist(eq(standardWorkListing));

    }

    @Test
    @Rollback
    public void checkGrantsPersisted() throws Exception {

        OrcidProfile grantsProfileListing = getOrcidWithGrants();
        OrcidSolrDocument grantsListing = solrDocWithGrantNumbers();
        orcidIndexManager.persistProfileInformationForIndexing(grantsProfileListing);
        verify(solrDao).persist(eq(grantsListing));

    }

    @Test
    @Rollback
    public void checkPatentsPersisted() throws Exception {

        OrcidProfile patentsProfileListing = orcidProfileAllLimitedVisibilityPatents();
        OrcidSolrDocument patentsListing = solrDocWithPatentNumbers();
        orcidIndexManager.persistProfileInformationForIndexing(patentsProfileListing);
        verify(solrDao).persist(eq(patentsListing));

    }

    @Test
    @Rollback
    public void onlyDoiPersistedFromOrcidWorks() {

        OrcidProfile orcidProfileWithDOI = getStandardOrcidWithDoiInformation();
        OrcidSolrDocument doiListings = solrDocumentLimitedtoVisibleDoi();

        // check that the limited profiles or non doi identifiers aren't
        // included
        orcidIndexManager.persistProfileInformationForIndexing(orcidProfileWithDOI);
        verify(solrDao).persist(eq(doiListings));

        // now check null values aren't persisted when either the type or value
        // are missing

        OrcidWork orcidWork1 = orcidProfileWithDOI.retrieveOrcidWorks().getOrcidWork().get(0);
        orcidWork1.getWorkExternalIdentifiers().getWorkExternalIdentifier().get(0).setWorkExternalIdentifierType(null);

        OrcidWork orcidWork2 = orcidProfileWithDOI.retrieveOrcidWorks().getOrcidWork().get(1);
        orcidWork2.getWorkExternalIdentifiers().getWorkExternalIdentifier().get(0).setWorkExternalIdentifierId(null);

        // so this should leave only the second doi
        doiListings.setDigitalObjectIds(Arrays.asList(new String[] { "work2-doi2" }));
        OrcidMessage orcidMessage = createFilteredOrcidMessage(orcidProfileWithDOI);
        doiListings.setPublicProfileMessage(orcidMessage.toString());

        orcidIndexManager.persistProfileInformationForIndexing(orcidProfileWithDOI);
        verify(solrDao).persist(eq(doiListings));

    }

    @Test
    @Rollback
    public void mandatoryOnlyFieldsPersistCorrectly() throws Exception {

        orcidIndexManager.persistProfileInformationForIndexing(getOrcidProfileMandatoryOnly());
        verify(solrDao).persist(eq(mandatoryDBFieldsSolrDocumentForPersistence()));

    }

    @Test
    @Rollback
    public void visibilityConstraintsAppliedToSolr() throws Exception {

        OrcidProfile hiddenNamesOrcid = orcidProfileLimitedVisiblityCreditNameAndOtherNames();
        orcidIndexManager.persistProfileInformationForIndexing(hiddenNamesOrcid);
        // check that limited fields are hidden from solr indexing
        OrcidSolrDocument namesHiddenSolrDoc = solrDocFilteredByNameVisibility();
        verify(solrDao).persist(eq(namesHiddenSolrDoc));

        // reset orcid test data and check affilations
        OrcidProfile limitedOrcid = orcidProfileLimitedVisiblityAffiliations();
        orcidIndexManager.persistProfileInformationForIndexing(limitedOrcid);
        OrcidSolrDocument hiddenPastAffiliations = solrDocFilteredByAffilliationVisibility();
        verify(solrDao).persist(eq(hiddenPastAffiliations));

        OrcidProfile orcidAllWorksPrivate = orcidProfileAllLimitedVisibilityWorks();
        orcidIndexManager.persistProfileInformationForIndexing(orcidAllWorksPrivate);
        OrcidSolrDocument hiddenWorks = solrDocFilteredByAffilliationVisibility();
        verify(solrDao).persist(eq(hiddenWorks));

    }

    private OrcidProfile orcidProfileLimitedVisiblityCreditNameAndOtherNames() {
        OrcidProfile limitedOrcid = getStandardOrcid();
        // hide other names fields
        limitedOrcid.getOrcidBio().getPersonalDetails().getOtherNames().setVisibility(Visibility.LIMITED);
        limitedOrcid.getOrcidBio().getPersonalDetails().getCreditName().setVisibility(Visibility.LIMITED);
        return limitedOrcid;
    }

    private OrcidProfile orcidProfileAllLimitedVisibilityPatents() {
        OrcidProfile fullOrcidPatents = getStandardOrcid();

        OrcidPatents orcidPatents = new OrcidPatents();
        OrcidPatent orcidPatent1 = new OrcidPatent();
        orcidPatent1.setVisibility(Visibility.PUBLIC);
        orcidPatent1.setPatentNumber(new PatentNumber("patent 1"));

        OrcidPatent orcidPatent2 = new OrcidPatent();
        orcidPatent2.setVisibility(Visibility.PUBLIC);
        orcidPatent2.setPatentNumber(new PatentNumber("patent 2"));

        OrcidPatent orcidPatent3 = new OrcidPatent();
        orcidPatent3.setVisibility(Visibility.LIMITED);
        orcidPatent3.setPatentNumber(new PatentNumber("patent 3"));

        OrcidPatent orcidPatent4 = new OrcidPatent();
        orcidPatent4.setVisibility(Visibility.PUBLIC);

        orcidPatents.getOrcidPatent().addAll(Arrays.asList(new OrcidPatent[] { orcidPatent1, orcidPatent2, orcidPatent3, orcidPatent4 }));
        fullOrcidPatents.setOrcidPatents(orcidPatents);

        return fullOrcidPatents;
    }

    private OrcidProfile orcidProfileAllLimitedVisibilityWorks() {
        OrcidProfile fullOrcidAllWorksPrivate = getStandardOrcid();
        // hide other names fields
        for (OrcidWork work : fullOrcidAllWorksPrivate.retrieveOrcidWorks().getOrcidWork()) {
            work.setVisibility(Visibility.LIMITED);
        }
        return fullOrcidAllWorksPrivate;
    }

    private OrcidProfile orcidProfileLimitedVisiblityAffiliations() {
        OrcidProfile limitedOrcid = getStandardOrcid();
        List<Affiliation> affiliations = limitedOrcid.getOrcidActivities().getAffiliations().getAffiliation();
        for (Affiliation affiliation : affiliations) {
            affiliation.setVisibility(Visibility.LIMITED);
        }

        return limitedOrcid;
    }

    /**
     * According to the validation rules on the web front end, these fields are
     * mandatory. Ultimately they may or may not make it into SOLR due to
     * visibility restrictions
     * 
     * @return OrcidProfile with only mandatory fields populated.
     */
    private OrcidProfile getOrcidProfileMandatoryOnly() {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcid("5678");
        OrcidBio orcidBio = new OrcidBio();
        orcidProfile.setOrcidBio(orcidBio);
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new Email("stan@test.com"));
        orcidBio.setContactDetails(contactDetails);
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setFamilyName(new FamilyName("Logan"));
        personalDetails.setGivenNames(new GivenNames("Donald Edward"));
        orcidBio.setPersonalDetails(personalDetails);

        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);
        Affiliation affiliation = createPrimaryAffiliation("University of Portsmouth");
        affiliations.getAffiliation().add(affiliation);

        return orcidProfile;
    }

    /**
     * According to the current schema - these fields are required by the DB -
     * they may or may not make it into SOLR due to visibility restrictions
     * 
     * @return
     */
    private OrcidSolrDocument mandatoryDBFieldsSolrDocumentForPersistence() {
        OrcidSolrDocument orcidSolrDocument = new OrcidSolrDocument();
        orcidSolrDocument.setOrcid("5678");
        orcidSolrDocument.setFamilyName("Logan");
        orcidSolrDocument.setGivenNames("Donald Edward");
        orcidSolrDocument.setAffiliatePrimaryInstitutionNames(Arrays.asList(new String[] { "University of Portsmouth" }));
        OrcidProfile orcidProfile = getOrcidProfileMandatoryOnly();
        OrcidMessage orcidMessage = createFilteredOrcidMessage(orcidProfile);
        orcidSolrDocument.setPublicProfileMessage(orcidMessage.toString());
        return orcidSolrDocument;
    }

    private OrcidProfile getOrcidWithSubtitledWork() {
        OrcidProfile orcidProfile = getStandardOrcid();

        OrcidWork orcidWork1 = orcidProfile.retrieveOrcidWorks().getOrcidWork().get(0);
        OrcidWork orcidWork2 = orcidProfile.retrieveOrcidWorks().getOrcidWork().get(1);
        OrcidWork orcidWork3 = orcidProfile.retrieveOrcidWorks().getOrcidWork().get(2);

        WorkTitle workTitle1 = new WorkTitle();
        Subtitle subTitle1 = new Subtitle("Subtitle 1");
        workTitle1.setSubtitle(subTitle1);
        workTitle1.setTitle(new Title("Work title 1"));
        orcidWork1.setWorkTitle(workTitle1);

        WorkTitle workTitle2 = orcidWork2.getWorkTitle();
        Subtitle subTitle2 = new Subtitle("Subtitle 2");
        workTitle2.setSubtitle(subTitle2);
        workTitle2.setTitle(new Title("Work title 2"));
        orcidWork2.setWorkTitle(workTitle2);

        WorkTitle workTitle3 = orcidWork3.getWorkTitle();
        Subtitle subTitle3 = new Subtitle("Subtitle 3");
        workTitle3.setSubtitle(subTitle3);
        workTitle3.setTitle(new Title("Work title 3"));
        orcidWork3.setWorkTitle(workTitle3);

        return orcidProfile;
    }

    private OrcidProfile getOrcidWithGrants() {
        OrcidProfile orcidWithGrants = getStandardOrcid();
        OrcidGrants orcidGrants = new OrcidGrants();
        OrcidGrant orcidGrant1 = new OrcidGrant();
        orcidGrant1.setVisibility(Visibility.PUBLIC);
        orcidGrant1.setGrantNumber(new GrantNumber("grant 1"));

        OrcidGrant orcidGrant2 = new OrcidGrant();
        orcidGrant2.setVisibility(Visibility.PUBLIC);
        orcidGrant2.setGrantNumber(new GrantNumber("grant 2"));

        OrcidGrant orcidGrant3 = new OrcidGrant();
        orcidGrant3.setVisibility(Visibility.LIMITED);
        orcidGrant3.setGrantNumber(new GrantNumber("grant 3"));

        OrcidGrant orcidGrant4 = new OrcidGrant();
        orcidGrant4.setVisibility(Visibility.PUBLIC);

        orcidGrants.getOrcidGrant().addAll(Arrays.asList(new OrcidGrant[] { orcidGrant1, orcidGrant2, orcidGrant3, orcidGrant4 }));
        orcidWithGrants.setOrcidGrants(orcidGrants);
        return orcidWithGrants;
    }

    private OrcidProfile getStandardOrcidWithDoiInformation() {
        OrcidProfile orcidProfile = getStandardOrcid();

        OrcidWork orcidWork1 = orcidProfile.retrieveOrcidWorks().getOrcidWork().get(0);
        OrcidWork orcidWork2 = orcidProfile.retrieveOrcidWorks().getOrcidWork().get(1);
        OrcidWork orcidWork3 = orcidProfile.retrieveOrcidWorks().getOrcidWork().get(2);

        WorkExternalIdentifiers work1ExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier work1ExternalIdentifier1 = new WorkExternalIdentifier();
        work1ExternalIdentifier1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        work1ExternalIdentifier1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work1-doi1"));
        WorkExternalIdentifier work1ExternalIdentifier2 = new WorkExternalIdentifier();
        work1ExternalIdentifier2.setWorkExternalIdentifierType(WorkExternalIdentifierType.PMID);
        work1ExternalIdentifier2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work1-pmid"));
        work1ExternalIdentifiers.getWorkExternalIdentifier().add(work1ExternalIdentifier1);
        work1ExternalIdentifiers.getWorkExternalIdentifier().add(work1ExternalIdentifier2);
        orcidWork1.setWorkExternalIdentifiers(work1ExternalIdentifiers);

        WorkExternalIdentifiers work2ExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier work2ExternalIdentifier1 = new WorkExternalIdentifier();
        work2ExternalIdentifier1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        work2ExternalIdentifier1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work2-doi1"));
        WorkExternalIdentifier work2ExternalIdentifier2 = new WorkExternalIdentifier();
        work2ExternalIdentifier2.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        work2ExternalIdentifier2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work2-doi2"));
        work2ExternalIdentifiers.getWorkExternalIdentifier().add(work2ExternalIdentifier1);
        work2ExternalIdentifiers.getWorkExternalIdentifier().add(work2ExternalIdentifier2);
        orcidWork2.setWorkExternalIdentifiers(work2ExternalIdentifiers);

        WorkExternalIdentifiers work3ExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier work3ExternalIdentifier1 = new WorkExternalIdentifier();
        work3ExternalIdentifier1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        work3ExternalIdentifier1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work3-doi1"));
        WorkExternalIdentifier work3ExternalIdentifier2 = new WorkExternalIdentifier();
        work3ExternalIdentifier2.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        work3ExternalIdentifier2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work3-doi2"));
        work3ExternalIdentifiers.getWorkExternalIdentifier().add(work3ExternalIdentifier1);
        work3ExternalIdentifiers.getWorkExternalIdentifier().add(work3ExternalIdentifier2);
        orcidWork3.setWorkExternalIdentifiers(work3ExternalIdentifiers);

        return orcidProfile;
    }

    private OrcidProfile getStandardOrcid() {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcid("1234");

        OrcidBio orcidBio = new OrcidBio();
        ContactDetails contactDetails = new ContactDetails();
        Email email = new Email("email");
        email.setVisibility(Visibility.PUBLIC);
        contactDetails.addOrReplacePrimaryEmail(email);
        orcidBio.setContactDetails(contactDetails);

        Keywords bioKeywords = new Keywords();
        bioKeywords.getKeyword().add(new Keyword("Pavement Studies"));
        bioKeywords.getKeyword().add(new Keyword("Advanced Tea Making"));
        bioKeywords.setVisibility(Visibility.PUBLIC);
        orcidBio.setKeywords(bioKeywords);

        PersonalDetails personalDetails = new PersonalDetails();
        CreditName creditName = new CreditName("credit name");
        creditName.setVisibility(Visibility.PUBLIC);
        personalDetails.setCreditName(creditName);

        personalDetails.setFamilyName(new FamilyName("familyName"));
        OtherNames otherNames = new OtherNames();
        otherNames.setVisibility(Visibility.PUBLIC);
        otherNames.getOtherName().add(new OtherName("Other 1"));
        otherNames.getOtherName().add(new OtherName("Other 2"));
        personalDetails.setOtherNames(otherNames);
        personalDetails.setGivenNames(new GivenNames("givenNames"));
        orcidBio.setPersonalDetails(personalDetails);

        ExternalIdentifiers externalIdentifiers = new ExternalIdentifiers();
        externalIdentifiers.setVisibility(Visibility.PUBLIC);
        orcidBio.setExternalIdentifiers(externalIdentifiers);
        ExternalIdentifier externalIdentifier1 = createExternalIdentifier("45678", "defghi");
        externalIdentifiers.getExternalIdentifier().add(externalIdentifier1);
        ExternalIdentifier externalIdentifier2 = createExternalIdentifier("54321", "abc123");
        externalIdentifiers.getExternalIdentifier().add(externalIdentifier2);

        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);

        Affiliation pastInst1 = createPastAffiliation("Past Inst 1");
        Affiliation pastInst2 = createPastAffiliation("Past Inst 2");
        Affiliation primaryInst1 = createPrimaryAffiliation("Primary Inst1");

        Affiliation currentAffiliation1 = createCurrentNonPrimaryAffiliation("Current Inst1");
        currentAffiliation1.setVisibility(Visibility.LIMITED);
        Affiliation currentAffiliation2 = createCurrentNonPrimaryAffiliation("Current Inst2");

        affiliations.getAffiliation().add(pastInst1);
        affiliations.getAffiliation().add(pastInst2);
        affiliations.getAffiliation().add(primaryInst1);
        affiliations.getAffiliation().add(currentAffiliation1);
        affiliations.getAffiliation().add(currentAffiliation2);

        OrcidWorks orcidWorks = new OrcidWorks();
        OrcidWork orcidWork1 = new OrcidWork();
        orcidWork1.setVisibility(Visibility.PUBLIC);

        OrcidWork orcidWork2 = new OrcidWork();
        orcidWork2.setVisibility(Visibility.PUBLIC);
        OrcidWork orcidWork3 = new OrcidWork();
        orcidWork3.setVisibility(Visibility.LIMITED);

        WorkTitle workTitle1 = new WorkTitle();
        Title title1 = new Title("Work title 1");
        workTitle1.setTitle(title1);
        workTitle1.setSubtitle(null);
        orcidWork1.setWorkTitle(workTitle1);

        WorkTitle workTitle2 = new WorkTitle();
        Title title2 = new Title("Work title 2");
        workTitle2.setSubtitle(null);
        workTitle2.setTitle(title2);
        orcidWork2.setWorkTitle(workTitle2);

        WorkTitle workTitle3 = new WorkTitle();
        Title title3 = new Title("Work Title 3");
        workTitle3.setSubtitle(null);
        workTitle3.setTitle(title3);
        orcidWork3.setWorkTitle(workTitle3);

        orcidWorks.setOrcidWork(new ArrayList<OrcidWork>(Arrays.asList(new OrcidWork[] { orcidWork1, orcidWork2, orcidWork3 })));

        orcidProfile.setOrcidWorks(orcidWorks);
        orcidProfile.setOrcidBio(orcidBio);
        return orcidProfile;
    }

    private OrcidSolrDocument solrDocFilteredByNameVisibility() {
        OrcidSolrDocument orcidSolrDocument = fullyPopulatedSolrDocumentForPersistence();
        orcidSolrDocument.setCreditName(null);
        orcidSolrDocument.setOtherNames(null);
        OrcidProfile orcidProfile = orcidProfileLimitedVisiblityCreditNameAndOtherNames();
        OrcidMessage orcidMessage = createFilteredOrcidMessage(orcidProfile);
        orcidSolrDocument.setPublicProfileMessage(orcidMessage.toString());
        return orcidSolrDocument;
    }

    private OrcidSolrDocument solrDocWithAdditionalSubtitles() {
        OrcidSolrDocument orcidSolrDocument = fullyPopulatedSolrDocumentForPersistence();
        orcidSolrDocument.setWorkTitles(Arrays.asList(new String[] { "Work title 1", "Subtitle 1", "Work title 2", "Subtitle 2" }));
        OrcidProfile orcidProfile = getOrcidWithSubtitledWork();
        OrcidMessage orcidMessage = createFilteredOrcidMessage(orcidProfile);
        orcidSolrDocument.setPublicProfileMessage(orcidMessage.toString());
        return orcidSolrDocument;
    }

    private OrcidSolrDocument solrDocWithGrantNumbers() {
        OrcidSolrDocument orcidSolrDocument = fullyPopulatedSolrDocumentForPersistence();
        orcidSolrDocument.setGrantNumbers(Arrays.asList(new String[] { "grant 1", "grant 2" }));
        OrcidProfile orcidProfile = getOrcidWithGrants();
        OrcidMessage orcidMessage = createFilteredOrcidMessage(orcidProfile);
        orcidSolrDocument.setPublicProfileMessage(orcidMessage.toString());
        return orcidSolrDocument;
    }

    private OrcidSolrDocument solrDocWithPatentNumbers() {
        OrcidSolrDocument orcidSolrDocument = fullyPopulatedSolrDocumentForPersistence();
        orcidSolrDocument.setPatentNumbers(Arrays.asList(new String[] { "patent 1", "patent 2" }));
        OrcidProfile orcidProfile = orcidProfileAllLimitedVisibilityPatents();
        OrcidMessage orcidMessage = createFilteredOrcidMessage(orcidProfile);
        orcidSolrDocument.setPublicProfileMessage(orcidMessage.toString());
        return orcidSolrDocument;
    }

    private OrcidSolrDocument solrDocFilteredByAffilliationVisibility() {
        OrcidSolrDocument orcidSolrDocument = fullyPopulatedSolrDocumentForPersistence();
        orcidSolrDocument.setAffiliatePastInstitutionNames(null);
        orcidSolrDocument.setAffiliatePrimaryInstitutionNames(null);
        orcidSolrDocument.setAffiliateInstitutionNames(null);
        OrcidProfile orcidProfile = orcidProfileLimitedVisiblityAffiliations();
        OrcidMessage orcidMessage = createFilteredOrcidMessage(orcidProfile);
        orcidSolrDocument.setPublicProfileMessage(orcidMessage.toString());
        return orcidSolrDocument;
    }

    private OrcidSolrDocument fullyPopulatedSolrDocumentForPersistence() {
        OrcidSolrDocument orcidSolrDocument = new OrcidSolrDocument();
        orcidSolrDocument.setOrcid("1234");
        orcidSolrDocument.setCreditName("credit name");
        orcidSolrDocument.setDigitalObjectIds(new ArrayList<String>());
        orcidSolrDocument.setFamilyName("familyName");
        orcidSolrDocument.setGivenNames("givenNames");
        orcidSolrDocument.addEmailAddress("email");
        orcidSolrDocument.setAffiliatePrimaryInstitutionNames(Arrays.asList(new String[] { "Primary Inst1" }));
        orcidSolrDocument.setAffiliateInstitutionNames(Arrays.asList(new String[] { "Current Inst2" }));
        orcidSolrDocument.setOtherNames(Arrays.asList(new String[] { "Other 1", "Other 2" }));
        orcidSolrDocument.setExternalIdOrcids(Arrays.asList(new String[] { "45678", "54321" }));
        orcidSolrDocument.setExternalIdReferences(Arrays.asList(new String[] { "defghi", "abc123" }));
        orcidSolrDocument.setExternalIdOrcidsAndReferences(Arrays.asList(new String[] { "45678=defghi", "54321=abc123" }));
        orcidSolrDocument.setPastInstitutionNames(Arrays.asList(new String[] { "Past Inst 1", "Past Inst 2" }));
        orcidSolrDocument.setWorkTitles(Arrays.asList(new String[] { "Work title 1", "Work title 2" }));
        orcidSolrDocument.setKeywords(Arrays.asList(new String[] { "Pavement Studies", "Advanced Tea Making" }));
        OrcidProfile orcidProfile = getStandardOrcid();
        OrcidMessage orcidMessage = createFilteredOrcidMessage(orcidProfile);
        orcidSolrDocument.setPublicProfileMessage(orcidMessage.toString());
        return orcidSolrDocument;
    }

    private OrcidMessage createFilteredOrcidMessage(OrcidProfile orcidProfile) {
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        orcidMessage.setOrcidProfile(orcidProfile);
        visibilityFilter.filter(orcidMessage, Visibility.PUBLIC);
        return orcidMessage;
    }

    private OrcidSolrDocument solrDocumentLimitedtoVisibleDoi() {
        OrcidSolrDocument orcidSolrDocument = fullyPopulatedSolrDocumentForPersistence();
        orcidSolrDocument.setDigitalObjectIds((Arrays.asList(new String[] { "work1-doi1", "work2-doi1", "work2-doi2" })));
        OrcidProfile orcidProfile = getStandardOrcidWithDoiInformation();
        OrcidMessage orcidMessage = createFilteredOrcidMessage(orcidProfile);
        orcidSolrDocument.setPublicProfileMessage(orcidMessage.toString());
        return orcidSolrDocument;
    }

    private Affiliation createPastAffiliation(String instName) {
        return createAffiliation(instName, AffiliationType.PAST_INSTITUTION);
    }

    private Affiliation createCurrentNonPrimaryAffiliation(String instName) {
        return createAffiliation(instName, AffiliationType.CURRENT_INSTITUTION);
    }

    private Affiliation createPrimaryAffiliation(String instName) {
        return createAffiliation(instName, AffiliationType.CURRENT_PRIMARY_INSTITUTION);
    }

    private Affiliation createAffiliation(String instName, AffiliationType affiliationType) {
        Affiliation affiliation = new Affiliation();
        affiliation.setAffiliationType(affiliationType);
        affiliation.setAffiliationName(instName);
        affiliation.setRoleTitle("A Role");
        AffiliationAddress address = new AffiliationAddress();
        address.setAffiliationCountry(new AffiliationCountry(Iso3166Country.GB));
        affiliation.setAffiliationAddress(address);
        affiliation.setVisibility(Visibility.PUBLIC);
        return affiliation;
    }

    private ExternalIdentifier createExternalIdentifier(String orcid, String reference) {
        ExternalIdentifier externalIdentifier1 = new ExternalIdentifier();
        externalIdentifier1.setExternalIdOrcid(new ExternalIdOrcid(orcid));
        externalIdentifier1.setExternalIdReference(new ExternalIdReference(reference));
        return externalIdentifier1;
    }

}
