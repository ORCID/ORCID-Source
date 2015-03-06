package org.orcid.integration.blackbox.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.publicV2.PublicV2ApiClientImpl;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.Activity;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.summary.EducationSummary;
import org.orcid.jaxb.model.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.record.summary.FundingSummary;
import org.orcid.jaxb.model.record.summary.WorkSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Monenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class PublicV2Test {
    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String redirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;
    @Value("${org.orcid.web.testClient2.clientId}")
    public String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    public String client2ClientSecret;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String testUser1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;
    
    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> t2OAuthClient;

    @Resource
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Resource
    private PublicV2ApiClientImpl publicV2ApiClient;
    
    private WebDriver webDriver;

    private WebDriverHelper webDriverHelper;

    @Resource
    private OauthHelper oauthHelper;

    @Before
    public void before() {
        webDriver = new FirefoxDriver();
        webDriverHelper = new WebDriverHelper(webDriver, webBaseUrl, redirectUri);
        oauthHelper.setWebDriverHelper(webDriverHelper);
    }

    @After
    public void after() {
        webDriver.quit();
    }
    
    @Test
    public void testViewWorkAndWorkSummary() throws JSONException, InterruptedException, URISyntaxException {
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PUBLIC);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(testUser1OrcidId, workToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());
        ClientResponse getWorkResponse = publicV2ApiClient.viewWorkXml(testUser1OrcidId, putCode);
        assertNotNull(getWorkResponse);
        Work work = getWorkResponse.getEntity(Work.class);
        assertNotNull(work);
        assertEquals("common:title", work.getWorkTitle().getTitle().getContent());
        
        ClientResponse getWorkSummaryResponse = publicV2ApiClient.viewWorkSummaryXml(testUser1OrcidId, putCode);
        assertNotNull(getWorkSummaryResponse);
        WorkSummary summary = getWorkSummaryResponse.getEntity(WorkSummary.class);
        assertNotNull(summary);
        assertEquals("common:title", summary.getTitle().getTitle().getContent());
    }
    
    @Test
    public void testViewFundingAndFundingSummary() throws JSONException, InterruptedException, URISyntaxException {
        Funding fundingToCreate = (Funding) unmarshallFromPath("/record_2.0_rc1/samples/funding-2.0_rc1.xml", Funding.class);
        fundingToCreate.setPutCode(null);
        fundingToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PUBLIC);
        
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createFundingXml(testUser1OrcidId, fundingToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());
        
        ClientResponse getFundingResponse = publicV2ApiClient.viewFundingXml(testUser1OrcidId, putCode);
        assertNotNull(getFundingResponse);
        Funding funding = getFundingResponse.getEntity(Funding.class);
        assertNotNull(funding);
        assertEquals("common:title", funding.getTitle().getTitle().getContent());        
        
        ClientResponse getFundingSummaryResponse = publicV2ApiClient.viewFundingSummaryXml(testUser1OrcidId, putCode);
        assertNotNull(getFundingSummaryResponse);
        FundingSummary summary = getFundingSummaryResponse.getEntity(FundingSummary.class);
        assertNotNull(summary);
        assertEquals("common:title", summary.getTitle().getTitle().getContent());
    }
    
    @Test
    public void testViewEmploymentAndEmploymentSummary() throws JSONException, InterruptedException, URISyntaxException {
        Employment employmentToCreate = (Employment) unmarshallFromPath("/record_2.0_rc1/samples/employment-2.0_rc1.xml", Employment.class);
        employmentToCreate.setPutCode(null);
        employmentToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PUBLIC);
        
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEmploymentXml(testUser1OrcidId, employmentToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());
        
        ClientResponse getEmploymentResponse = publicV2ApiClient.viewEmploymentXml(testUser1OrcidId, putCode);
        assertNotNull(getEmploymentResponse);
        Employment employment= getEmploymentResponse.getEntity(Employment.class);
        assertNotNull(employment);
        assertEquals("affiliation:department-name", employment.getDepartmentName());        
        
        ClientResponse getEmploymentSummaryResponse = publicV2ApiClient.viewEmploymentSummaryXml(testUser1OrcidId, putCode);
        assertNotNull(getEmploymentSummaryResponse);
        EmploymentSummary summary = getEmploymentSummaryResponse.getEntity(EmploymentSummary.class);
        assertNotNull(summary);
        assertEquals("affiliation:department-name", summary.getDepartmentName()); 
    }
    
    @Test
    public void testViewEducationAndEducationSummary() throws JSONException, InterruptedException, URISyntaxException {
        Education educationToCreate = (Education) unmarshallFromPath("/record_2.0_rc1/samples/education-2.0_rc1.xml", Education.class);
        educationToCreate.setPutCode(null);
        educationToCreate.setVisibility(org.orcid.jaxb.model.record.Visibility.PUBLIC);
        
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createEducationXml(testUser1OrcidId, educationToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String path = postResponse.getLocation().getPath();
        String putCode = path.substring(path.lastIndexOf('/') + 1, path.length());
        
        ClientResponse getEducationResponse = publicV2ApiClient.viewEducationXml(testUser1OrcidId, putCode);
        assertNotNull(getEducationResponse);
        Education education= getEducationResponse.getEntity(Education.class);
        assertNotNull(education);
        assertEquals("education:department-name", education.getDepartmentName());        
        
        ClientResponse getEducationSummaryResponse = publicV2ApiClient.viewEducationSummaryXml(testUser1OrcidId, putCode);
        assertNotNull(getEducationSummaryResponse);
        EducationSummary summary = getEducationSummaryResponse.getEntity(EducationSummary.class);
        assertNotNull(summary);
        assertEquals("education:department-name", summary.getDepartmentName()); 
    }
    
    private String getAccessToken() throws InterruptedException, JSONException {
        return oauthHelper.obtainAccessToken(client1ClientId, client1ClientSecret, ScopePathType.ACTIVITIES_UPDATE.value(), user1UserName, user1Password, redirectUri);
    }

    public Activity unmarshallFromPath(String path, Class<? extends Activity> type) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            Object obj = unmarshall(reader, type);
            Activity result = null;
            if (Education.class.equals(type)) {
                result = (Education) obj;
            } else if (Employment.class.equals(type)) {
                result = (Employment) obj;
            } else if (Funding.class.equals(type)) {
                result = (Funding) obj;
            } else if (Work.class.equals(type)) {
                result = (Work) obj;
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error reading notification from classpath", e);
        }
    }

    public Object unmarshall(Reader reader, Class<? extends Activity> type) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
    }

}
