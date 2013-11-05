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

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidProfileManagerContributorVisibilityTest extends BaseTest {

    private static final String TEST_ORCID = "4444-4444-4444-4446";

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/ProfileEntityData.xml"), null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(
                Arrays.asList("/data/EmptyEntityData.xml", "/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"), null);
    }

    @Ignore
    @Test
    @Transactional
    public void emailProvidedButDoesNotExistInDb() {
        // Unmarshall message containing contributor email that does not exist
        // in DB
        OrcidMessage orcidMessage = unmarshallOrcidMessage("new_work_with_contributor_email_and_name.xml");

        // Add the work
        orcidProfileManager.addOrcidWorks(orcidMessage.getOrcidProfile());

        // Get it back from the DB
        OrcidWork retrievedWork = retrieveAddedWork();
        Contributor workContributor = retrievedWork.getWorkContributors().getContributor().get(0);

        // Check that the contributor name is included in the resulting work
        assertEquals("Test Contributor Name", workContributor.getCreditName().getContent());

        // Check that the email is not included in the resulting work
        assertNull(workContributor.getContributorEmail());
    }

    @Test
    @Transactional
    public void emailProvidedAndDoesExistInDbAndCreditNameAndEmailArePublic() {
        // Unmarshall message containing contributor email
        OrcidMessage orcidMessage = unmarshallOrcidMessage("new_work_with_contributor_email_and_name.xml");
        // Change email to one that exists in DB
        orcidMessage.getOrcidProfile().retrieveOrcidWorks().getOrcidWork().get(0).getWorkContributors().getContributor().get(0).getContributorEmail()
                .setValue("user@email.com");

        // Add the work
        orcidProfileManager.addOrcidWorks(orcidMessage.getOrcidProfile());

        // Get it back from the DB
        OrcidWork retrievedWork = retrieveAddedWork();
        Contributor workContributor = retrievedWork.getWorkContributors().getContributor().get(0);

        // Check that the contributor name is included in the resulting work
        assertEquals("U. Name", workContributor.getCreditName().getContent());

        // Check that the email is also included in the resulting work
        assertEquals("user@email.com", workContributor.getContributorEmail().getValue());
    }

    @Ignore
    @Test
    @Transactional
    public void emailProvidedAndDoesExistInDbButCreditNameAndEmailArePrivate() {
        // Unmarshall message containing contributor email
        OrcidMessage orcidMessage = unmarshallOrcidMessage("new_work_with_contributor_email_and_name.xml");
        // Change email to one that exists in DB
        orcidMessage.getOrcidProfile().retrieveOrcidWorks().getOrcidWork().get(0).getWorkContributors().getContributor().get(0).getContributorEmail()
                .setValue("otis@reading.com");

        // Add the work
        orcidProfileManager.addOrcidWorks(orcidMessage.getOrcidProfile());

        // Get it back from the DB
        OrcidWork retrievedWork = retrieveAddedWork();
        Contributor workContributor = retrievedWork.getWorkContributors().getContributor().get(0);

        // Check that the contributor name is not included in the resulting
        // work, because it is private
        assertNull(workContributor.getCreditName());

        // Check that the email is not included in the resulting work, because
        // it is private
        assertNull(workContributor.getContributorEmail());
    }

    @Test
    @Transactional
    public void orcidProvidedButDoesNotExistInDb() {
        // Unmarshall message containing contributor ORCID that does not exist
        // in DB
        OrcidMessage orcidMessage = unmarshallOrcidMessage("new_work_with_contributor_orcid.xml");

        // Add the work
        orcidProfileManager.addOrcidWorks(orcidMessage.getOrcidProfile());

        // Get it back from the DB
        OrcidWork retrievedWork = retrieveAddedWork();
        Contributor workContributor = retrievedWork.getWorkContributors().getContributor().get(0);

        // Check that the contributor name is not included in the resulting
        // work, because the ORCID does not exist
        assertNull(workContributor.getCreditName());

        // Check that the email is not included in the resulting work, because
        // the ORCID does not exist
        assertNull(workContributor.getContributorEmail());
    }

    @Test
    @Transactional
    public void orcidProvidedAndDoesExistInDbAndCreditNameAndEmailArePublic() {
        // Unmarshall message containing contributor email
        OrcidMessage orcidMessage = unmarshallOrcidMessage("new_work_with_contributor_orcid.xml");
        // Change email to one that exists in DB
        orcidMessage.getOrcidProfile().retrieveOrcidWorks().getOrcidWork().get(0).getWorkContributors().getContributor().get(0).getContributorOrcid()
                .setValue("4444-4444-4444-444X");

        // Add the work
        orcidProfileManager.addOrcidWorks(orcidMessage.getOrcidProfile());

        // Get it back from the DB
        OrcidWork retrievedWork = retrieveAddedWork();
        Contributor workContributor = retrievedWork.getWorkContributors().getContributor().get(0);

        // Check that the contributor name is included in the resulting work
        assertEquals("U. Name", workContributor.getCreditName().getContent());

        // Check that the email is also included in the resulting work
        assertEquals("user@email.com", workContributor.getContributorEmail().getValue());
    }

    @Ignore
    @Test
    @Transactional
    public void orcidProvidedAndDoesExistInDbButCreditNameAndEmailArePrivate() {
        // Unmarshall message containing contributor email
        OrcidMessage orcidMessage = unmarshallOrcidMessage("new_work_with_contributor_orcid.xml");
        // Change email to one that exists in DB
        orcidMessage.getOrcidProfile().retrieveOrcidWorks().getOrcidWork().get(0).getWorkContributors().getContributor().get(0).getContributorOrcid()
                .setValue("4444-4444-4444-4447");

        // Add the work
        orcidProfileManager.addOrcidWorks(orcidMessage.getOrcidProfile());

        // Get it back from the DB
        OrcidWork retrievedWork = retrieveAddedWork();
        Contributor workContributor = retrievedWork.getWorkContributors().getContributor().get(0);

        // Check that the contributor name is not included in the resulting
        // work, because it is private
        assertNull(workContributor.getCreditName());

        // Check that the email is not included in the resulting work, because
        // it is private
        assertNull(workContributor.getContributorEmail());
    }

    @Test
    @Transactional
    public void onlyNameProvided() {
        // Unmarshall message containing contributor name only
        OrcidMessage orcidMessage = unmarshallOrcidMessage("new_work_with_contributor_name.xml");

        // Add the work
        orcidProfileManager.addOrcidWorks(orcidMessage.getOrcidProfile());

        // Get it back from the DB
        OrcidWork retrievedWork = retrieveAddedWork();
        Contributor workContributor = retrievedWork.getWorkContributors().getContributor().get(0);

        // Check that the contributor name is included in the resulting work
        assertEquals("Test Contributor Name", workContributor.getCreditName().getContent());

        // Check that the email is not included in the resulting work, because
        // it was not provided
        assertNull(workContributor.getContributorEmail());
    }

    private OrcidMessage unmarshallOrcidMessage(String resourceName) {
        return OrcidMessage.unmarshall(new InputStreamReader(getClass().getResourceAsStream(resourceName)));
    }

    private OrcidWork retrieveAddedWork() {
        OrcidProfile retrieved = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        OrcidWorks orcidWorks = retrieved.retrieveOrcidWorks();
        assertNotNull(orcidWorks);
        List<OrcidWork> orcidWorkList = orcidWorks.getOrcidWork();
        assertEquals(1, orcidWorkList.size());
        return orcidWorkList.get(0);
    }

}
