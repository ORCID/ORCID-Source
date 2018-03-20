package org.orcid.frontend.forms.registration.test;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * Webdriver test to go through the process of verifying a registration and
 * checking that the page displays validation messages and flows as expected
 * Tests currently set to a @Ignore but included in codebase as a starting point
 * 
 * @author jamesb
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-frontend-web-servlet.xml" })
public class VerifyRegistrationITest {

    // convenience alias...
    private String baseUrl;
    private WebDriver webDriver;

    @After
    public void tearDown() throws Exception {
        webDriver.close();
    }

    @Test
    @Ignore("Ignore for now as broken by Dan's new UI stuff")
    public void testRegistration() {

        // Create a new instance of the html unit driver
        // Notice that the remainder of the code relies on the interface,
        // not the implementation.

        // And now use this to visit Google
        webDriver.get(baseUrl + "/orcid-frontend-web/register");

        // Find the text input element by its name
        WebElement element = webDriver.findElement(By.id("homeRegLink"));
        element.click();
        // driver.findElement(By.id("givenNames")).sendKeys("John");
        // fail();

    }

}
