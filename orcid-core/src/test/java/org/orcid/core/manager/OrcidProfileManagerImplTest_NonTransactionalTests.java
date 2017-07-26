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
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;

/**
 * @author Will Simpson
 */
public class OrcidProfileManagerImplTest_NonTransactionalTests extends OrcidProfileManagerBaseTest {

    private static final String TEST_ORCID = "0000-0000-0000-0001";

    @After
    public void after() {
        orcidProfileManager.deleteProfile(TEST_ORCID);
    }

    @Test
    public void testUpdateProfileButRemoveWorkExternalIdentifier() {
        OrcidProfile profile1 = createBasicProfile();
        // Change the orcid identifier
        profile1.setOrcidIdentifier(TEST_ORCID);
        // Change the email address
        profile1.getOrcidBio().getContactDetails().retrievePrimaryEmail().setValue(TEST_ORCID + "@test.com");
        profile1 = orcidProfileManager.createOrcidProfile(profile1, false, false);

        List<WorkExternalIdentifier> workExternalIdentifiers = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkExternalIdentifiers()
                .getWorkExternalIdentifier();
        assertEquals(3, workExternalIdentifiers.size());
        Iterator<WorkExternalIdentifier> workExternalIdentifiersIterator = workExternalIdentifiers.iterator();
        while (workExternalIdentifiersIterator.hasNext()) {
            if (WorkExternalIdentifierType.PMID.equals(workExternalIdentifiersIterator.next().getWorkExternalIdentifierType())) {
                workExternalIdentifiersIterator.remove();
            }
        }

        orcidProfileManager.updateOrcidProfile(profile1);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(2, resultProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkExternalIdentifiers().getWorkExternalIdentifier().size());
    }

    @Test
    public void testUpdateProfileButRemoveWorkContributor() {
        OrcidProfile profile1 = createBasicProfile();
        // Change the orcid identifier
        profile1.setOrcidIdentifier(TEST_ORCID);

        // Change the email address
        profile1.getOrcidBio().getContactDetails().retrievePrimaryEmail().setValue(TEST_ORCID + "@test.com");
        profile1 = orcidProfileManager.createOrcidProfile(profile1, false, false);

        List<Contributor> contributors = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkContributors().getContributor();
        assertEquals(2, contributors.size());
        Iterator<Contributor> contributorsIterator = contributors.iterator();
        while (contributorsIterator.hasNext()) {
            if (SequenceType.ADDITIONAL.equals(contributorsIterator.next().getContributorAttributes().getContributorSequence())) {
                contributorsIterator.remove();
            }
        }

        orcidProfileManager.updateOrcidProfile(profile1);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(1, resultProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkContributors().getContributor().size());
    }
}
