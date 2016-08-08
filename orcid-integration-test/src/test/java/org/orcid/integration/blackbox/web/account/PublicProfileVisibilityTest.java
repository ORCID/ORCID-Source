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
package org.orcid.integration.blackbox.web.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.blackbox.api.BBBUtil;
import org.orcid.integration.blackbox.api.v2.rc2.BlackBoxBaseRC2;
import org.orcid.jaxb.model.common_rc2.Country;
import org.orcid.jaxb.model.common_rc2.Iso3166Country;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.Relationship;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Shobhit Tyagi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class PublicProfileVisibilityTest extends BlackBoxBaseRC2 {
    @Before
    public void before() {
        signin();
    }

    @After
    public void after() {
        signout();
    }

    @Test
    public void emailPrivacyTest() throws InterruptedException {
        //Add a public email
        String emailValue = "added.email." + System.currentTimeMillis() + "@test.com";
        showAccountSettingsPage();
        openEditEmailsSectionOnAccountSettingsPage();
        addEmail(emailValue, Visibility.PRIVATE);
        
        showPublicProfilePage(getUser1OrcidId());
        try {
            //Verify it doesn't appear in the public page
            emailAppearsInPublicPage(emailValue);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility to limited
        showAccountSettingsPage();
        openEditEmailsSectionOnAccountSettingsPage();
        updateEmailVisibility(emailValue, Visibility.LIMITED);
        showPublicProfilePage(getUser1OrcidId());
        try {
            //Verify it doesn't appear in the public page
            emailAppearsInPublicPage(emailValue);
            fail();
        } catch(Exception e) {
            
        }        
        
        //Change visibility to public
        showAccountSettingsPage();
        openEditEmailsSectionOnAccountSettingsPage();
        updateEmailVisibility(emailValue, Visibility.PUBLIC);
        //Verify it appears in the public page
        showPublicProfilePage(getUser1OrcidId());
        emailAppearsInPublicPage(emailValue);
        
        //Delete the new email
        showAccountSettingsPage();
        openEditEmailsSectionOnAccountSettingsPage();
        removeEmail(emailValue);
    }

    @Test
    public void otherNamesPrivacyTest() throws InterruptedException, JSONException {
        String otherNameValue = "added-other-name-" + System.currentTimeMillis();
                        
        //Create a new other name and set it to public        
        showMyOrcidPage();
        openEditOtherNamesModal();
        createOtherName(otherNameValue);
        changeOtherNamesVisibility(Visibility.PRIVATE);
        saveOtherNamesModal();
        
        //Verify it doesn't appear in the public page
        try {
            showPublicProfilePage(getUser1OrcidId());
            otherNamesAppearsInPublicPage(otherNameValue);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility to limited
        showMyOrcidPage();
        openEditOtherNamesModal();        
        changeOtherNamesVisibility(Visibility.LIMITED);
        saveOtherNamesModal();
        
        //Verify it doesn't appear in the public page
        try {
            showPublicProfilePage(getUser1OrcidId());
            otherNamesAppearsInPublicPage(otherNameValue);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility again to public
        showMyOrcidPage();
        openEditOtherNamesModal();        
        changeOtherNamesVisibility(Visibility.PUBLIC);
        saveOtherNamesModal();
        
        //Verify it appears again in the public page
        showPublicProfilePage(getUser1OrcidId());
        otherNamesAppearsInPublicPage(otherNameValue);
        
        //Delete it
        showMyOrcidPage();
        openEditOtherNamesModal();
        deleteOtherNames(otherNameValue);
        saveOtherNamesModal();
    }    
        
    @Test
    public void countryPrivacyTest() throws InterruptedException, JSONException {
        showMyOrcidPage();
        openEditAddressModal();
        deleteAddresses();
        createAddress(Iso3166Country.ZW.name());
        changeAddressVisibility(Visibility.PRIVATE);  
        saveEditAddressModal();
      
        //Verify it doesn't appears again in the public page
        try {
            showPublicProfilePage(getUser1OrcidId());
            addressAppearsInPublicPage("Zimbabwe");
            fail();
        } catch(Exception e) {
            
        }
                
        //Change visibility to limited
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(Visibility.LIMITED);
        saveEditAddressModal();
        
        //Verify it doesn't appears again in the public page
        try {
            showPublicProfilePage(getUser1OrcidId());
            addressAppearsInPublicPage("Zimbabwe");
            fail();
        } catch(Exception e) {
            
        }
                
        //Change visibility to public again
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(Visibility.PUBLIC);
        saveEditAddressModal();
        
        //Verify it appears again in the public page
        showPublicProfilePage(getUser1OrcidId());
        addressAppearsInPublicPage("Zimbabwe");
        
        showMyOrcidPage();
        openEditAddressModal();
        deleteAddresses();
        saveEditAddressModal();
    }
    
    @Test
    public void keywordPrivacyTest() throws InterruptedException, JSONException {
        String keywordValue = "added-keyword-" + System.currentTimeMillis();
        String accessToken = getAccessToken(getScopes(ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE));
        //Create a new other name and set it to public
        Long otherNamePutcode = createKeyword(keywordValue, getUser1OrcidId(), accessToken);
        showMyOrcidPage();
        openEditKeywordsModal();
        changeKeywordsVisibility(Visibility.PRIVATE);
        
        //Verify it doesn't appear in the public page
        try {
            showPublicProfilePage(getUser1OrcidId());
            keywordsAppearsInPublicPage(keywordValue);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility to limited
        showMyOrcidPage();
        openEditKeywordsModal();        
        changeKeywordsVisibility(Visibility.LIMITED);
        
        //Verify it doesn't appear in the public page
        try {
            showPublicProfilePage(getUser1OrcidId());
            keywordsAppearsInPublicPage(keywordValue);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility again to public
        showMyOrcidPage();
        openEditKeywordsModal();
        changeKeywordsVisibility(Visibility.PUBLIC);
        
        //Verify it appears again in the public page
        showPublicProfilePage(getUser1OrcidId());
        keywordsAppearsInPublicPage(keywordValue);
        
        //Delete it
        deleteKeyword(getUser1OrcidId(), otherNamePutcode, accessToken);
    }

    @Test
    public void websitesPrivacyTest() throws InterruptedException, JSONException {
        String rUrl = "http://test.orcid.org/" + System.currentTimeMillis();
        String accessToken = getAccessToken(getScopes(ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE));
        //Create a new other name and set it to public
        Long putCode = createResearcherUrl(rUrl, getUser1OrcidId(), accessToken);
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(Visibility.PRIVATE);
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            researcherUrlAppearsInPublicPage(rUrl);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility to limited
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(Visibility.LIMITED);
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            researcherUrlAppearsInPublicPage(rUrl);
            fail();
        } catch(Exception e) {
            
        }
          
        //Change visibility to public
        showMyOrcidPage();
        openEditResearcherUrlsModal();
        changeResearcherUrlsVisibility(Visibility.PUBLIC);
        
        //Verify it appears again in the public page
        showPublicProfilePage(getUser1OrcidId());
        researcherUrlAppearsInPublicPage(rUrl);
        
        deleteResearcherUrl(getUser1OrcidId(), putCode, accessToken);
    }
    
    @Test
    public void externalIdentifiersPrivacyTest() throws InterruptedException, JSONException {
        String extId = "added-ext-id-" + System.currentTimeMillis();
        String accessToken = getAccessToken(getScopes(ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE));
        //Create a new external identifier and set it to public
        Long putCode = createExternalIdentifier(extId, getUser1OrcidId(), accessToken);
        showMyOrcidPage();
        openEditExternalIdentifiersModal();
        changeExternalIdentifiersVisibility(Visibility.PRIVATE);
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            externalIdentifiersAppearsInPublicPage(extId);
            fail();
        } catch(Exception e) {
            
        }
        
        //Change visibility to limited
        showMyOrcidPage();
        openEditExternalIdentifiersModal();
        changeExternalIdentifiersVisibility(Visibility.LIMITED);
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            externalIdentifiersAppearsInPublicPage(extId);
            fail();
        } catch(Exception e) {
            
        }              
        
        //Change visibility back to public
        showMyOrcidPage();
        openEditExternalIdentifiersModal();
        changeExternalIdentifiersVisibility(Visibility.PUBLIC);
        
        //Verify it appears again in the public page
        showPublicProfilePage(getUser1OrcidId());
        externalIdentifiersAppearsInPublicPage(extId);
        
        deleteExternalIdentifier(getUser1OrcidId(), putCode, accessToken);
    }

    @Test
    public void workPrivacyTest() throws InterruptedException, JSONException {
        String workTitle = "added-work-" + System.currentTimeMillis();
        String accessToken = getAccessToken(getScopes(ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE));
        
        Long putCode = createWork(workTitle, getUser1OrcidId(), accessToken);
    
        showMyOrcidPage();
        changeWorksVisibility(workTitle, Visibility.PRIVATE);
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            workAppearsInPublicPage(workTitle);
            fail();
        } catch(Exception e) {
            
        }    
    
        //Change visibility to limited
        showMyOrcidPage();
        changeWorksVisibility(workTitle, Visibility.LIMITED);
        
        try {
            //Verify it doesn't appear in the public page
            showPublicProfilePage(getUser1OrcidId());
            workAppearsInPublicPage(workTitle);
            fail();
        } catch(Exception e) {
            
        }
        
        showMyOrcidPage();
        changeWorksVisibility(workTitle, Visibility.PUBLIC);
        
        //Verify it appear in the public page
        showPublicProfilePage(getUser1OrcidId());
        workAppearsInPublicPage(workTitle);
        
        deleteWork(getUser1OrcidId(), putCode, accessToken);        
    }
    
    @Test
    public void educationPrivacyTest() {
        //TODO: refactor to match pattern used in bio sections
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(ById.id("add-education-container")), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(ById.id("add-education")), webDriver);
        WebElement container = webDriver.findElement(By.id("add-education-container"));
        BBBUtil.ngAwareClick(container, webDriver);
        BBBUtil.ngAwareClick(container.findElement(By.id("add-education")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.cboxComplete(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ById.id("affiliationName")), webDriver);
        String educationName = "Education" + System.currentTimeMillis();
        BBBUtil.ngAwareSendKeys(educationName,"affiliationName", webDriver);
        BBBUtil.ngAwareSendKeys("New Delhi","city", webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        Select selectBox = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editAffiliation.country.value']")));
        selectBox.selectByVisibleText("India");
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        //wait for angular to register that values have been typed.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath("//button[@id='save-education']")), webDriver);
        BBBUtil.noSpinners(webDriver);
        BBBUtil.noCboxOverlay(webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        

        // Set Private Visibility
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]")), webDriver);
        WebElement educationElement = webDriver.findElement(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]"));
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]/descendant::div[@id='privacy-bar']/ul/li[3]/a")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]/descendant::div[@id='privacy-bar']/ul/li[3]/a")), webDriver);

        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]")), webDriver);

        // Verify
        showPublicProfilePage(getUser1OrcidId());
        try {
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + educationName + "')]")));
            fail();
        } catch (Exception e) {

        }

        // Set Public Visibility
        showMyOrcidPage();
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]")), webDriver);
        educationElement = webDriver.findElement(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]"));
        BBBUtil.ngAwareClick(educationElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[1]/a")), webDriver);

        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]")), webDriver);

        // Verify
        showPublicProfilePage(getUser1OrcidId());
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + educationName + "')]")), webDriver);

        // Rollback changes
        showMyOrcidPage();
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]")), webDriver);
        educationElement = webDriver.findElement(By.xpath("//li[@education-put-code and descendant::span[text() = '" + educationName + "']]"));
        String putCode = educationElement.getAttribute("education-put-code");
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("delete-affiliation_" + putCode)), webDriver);


        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ById.id("confirm_delete_affiliation")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(ById.id("confirm_delete_affiliation")), webDriver);

    }

    @Test
    public void employmentPrivacyTest() {
        //TODO: refactor to match pattern used in bio sections
        BBBUtil.noSpinners(webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ById.id("add-employment-container")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("add-employment-container")), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ById.id("add-employment")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("add-employment")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.cboxComplete(), webDriver);
        BBBUtil.noSpinners(webDriver);
        
        
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ById.id("affiliationName")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        String employmentName = "Employment" + System.currentTimeMillis();
        BBBUtil.ngAwareSendKeys(employmentName,"affiliationName", webDriver);        
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.ngAwareSendKeys("New Delhi","city", webDriver);        

        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        
        Select selectBox = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editAffiliation.country.value']")));
        selectBox.selectByVisibleText("India");
        //wait for angular to register that values have been typed.
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath("//button[@id='save-education']")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);
        BBBUtil.noCboxOverlay(webDriver);
        
        // Set Private Visibility
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]")), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]/descendant::div[@id='privacy-bar']/ul/li[3]/a")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]/descendant::div[@id='privacy-bar']/ul/li[3]/a")), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]")), webDriver);

        // Verify
        showPublicProfilePage(getUser1OrcidId());
        BBBUtil.noSpinners(webDriver);
        try {
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS))
            .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + employmentName + "')]")));
            fail();
        } catch (Exception e) {

        }

        // Set Public Visibility
        showMyOrcidPage();
        BBBUtil.noSpinners(webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]")), webDriver);
        WebElement educationElement = webDriver.findElement(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]"));
        BBBUtil.ngAwareClick(educationElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[1]/a")), webDriver);

        
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]")), webDriver);

        // Verify
        showPublicProfilePage(getUser1OrcidId());
        BBBUtil.noSpinners(webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + employmentName + "')]")), webDriver);

        // Rollback changes
        showMyOrcidPage();
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]")), webDriver);
        educationElement = webDriver.findElement(By.xpath("//li[@employment-put-code and descendant::span[text() = '" + employmentName + "']]"));
        String putCode = educationElement.getAttribute("employment-put-code");
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("delete-affiliation_" + putCode)), webDriver);
        BBBUtil.noSpinners(webDriver);
        
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ById.id("confirm_delete_affiliation")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(ById.id("confirm_delete_affiliation")), webDriver);

    }

    @Test
    public void fundingPrivacyTest() throws InterruptedException {
        //TODO: refactor to match pattern used in bio sections
        BBBUtil.noSpinners(webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ById.id("add-funding-container")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("add-funding-container")), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ById.id("add-funding")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("add-funding")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.cboxComplete(), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ById.id("fundingType")), webDriver);
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        long time = System.currentTimeMillis();
        BBBUtil.ngAwareSendKeys("award","fundingType", webDriver);
        String fundingTitle = "Funding Title " + time;
        BBBUtil.ngAwareSendKeys(fundingTitle,"fundingTitle", webDriver);
        BBBUtil.ngAwareSendKeys("Name " + time,"fundingName", webDriver);
        BBBUtil.ngAwareSendKeys("San Jose","city", webDriver);
        Select selectBox = new Select(webDriver.findElement(By.xpath("//select[@ng-model='editFunding.country.value']")));
        selectBox.selectByVisibleText("United States");
        //wait for angular to register that values have been typed.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BBBUtil.ngAwareClick(webDriver.findElement(By.id("save-funding")), webDriver);
        BBBUtil.noSpinners(webDriver);
        
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]")), webDriver);

        // Change to private
        WebElement fundingElement = webDriver.findElement(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]"));
        BBBUtil.ngAwareClick(fundingElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[3]/a")), webDriver);
        
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]")), webDriver);

        // Check public page
        showPublicProfilePage(getUser1OrcidId());
        BBBUtil.noSpinners(webDriver);
        try {
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + fundingTitle + "')]")));
            fail();
        } catch (Exception e) {

        }

        // Change to public
        showMyOrcidPage();
        BBBUtil.noSpinners(webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]")), webDriver);
        fundingElement = webDriver.findElement(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]"));
        BBBUtil.ngAwareClick(fundingElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[1]/a")), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]")), webDriver);

        // Check public page
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'" + fundingTitle + "')]")), webDriver);

        // Rollback changes
        showMyOrcidPage();
        BBBUtil.noSpinners(webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]")), webDriver);
        fundingElement = webDriver.findElement(By.xpath("//li[@funding-put-code and descendant::span[text() = '" + fundingTitle + "']]"));
        String putCode = fundingElement.getAttribute("funding-put-code");
        BBBUtil.ngAwareClick(fundingElement.findElement(By.id("delete-funding_" + putCode)), webDriver);
        
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(ById.id("confirm-delete-funding")), webDriver);
        BBBUtil.ngAwareClick(webDriver.findElement(ById.id("confirm-delete-funding")), webDriver);

    }

    
    

    
    
    
    
    
    
    
    @Test
    public void peerReviewPrivacyTest() throws InterruptedException, JSONException, URISyntaxException {
        //TODO: refactor to match pattern used in bio sections
        // Create peer review group               
        String accessToken = getAccessToken(getScopes(ScopePathType.ACTIVITIES_UPDATE, ScopePathType.ACTIVITIES_READ_LIMITED));
        GroupIdRecord g1 = super.createGroupIdRecord();

        // Create peer review
        long time = System.currentTimeMillis();
        PeerReview peerReview = (PeerReview) unmarshallFromPath("/record_2.0_rc2/samples/peer-review-2.0_rc2.xml", PeerReview.class);
        peerReview.setPutCode(null);
        peerReview.setGroupId(g1.getGroupId());
        peerReview.getExternalIdentifiers().getExternalIdentifier().clear();
        ExternalID wExtId = new ExternalID();
        wExtId.setValue("Work Id " + time);
        wExtId.setType(WorkExternalIdentifierType.AGR.value());
        wExtId.setRelationship(Relationship.SELF);
        peerReview.getExternalIdentifiers().getExternalIdentifier().add(wExtId);

        ClientResponse postResponse = memberV2ApiClient.createPeerReviewXml(this.getUser1OrcidId(), peerReview, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        peerReview = getResponse.getEntity(PeerReview.class);

        // Set it private
        showMyOrcidPage();
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]")), webDriver);
        WebElement peerReviewElement = webDriver.findElement(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]"));
        BBBUtil.ngAwareClick(peerReviewElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[3]/a")), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]")), webDriver);

        // Check the public page
        showPublicProfilePage(getUser1OrcidId());
        try {
            (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS))
            .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]")));
            fail();
        } catch (Exception e) {

        }

        // Set it public
        showMyOrcidPage();
        BBBUtil.extremeWaitFor(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]")), webDriver);
        peerReviewElement = webDriver.findElement(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]"));
        BBBUtil.ngAwareClick(peerReviewElement.findElement(By.xpath(".//div[@id='privacy-bar']/ul/li[1]/a")), webDriver);
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]")), webDriver);

        // Check the public page
        showPublicProfilePage(getUser1OrcidId());
        BBBUtil.extremeWaitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@orcid-put-code and descendant::span[text() = '" + g1.getName() + "']]")), webDriver);

        // Rollback
        ClientResponse deleteResponse = memberV2ApiClient.deletePeerReviewXml(this.getUser1OrcidId(), peerReview.getPutCode(), accessToken);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
}
