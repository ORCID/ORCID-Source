package org.orcid.integration.blackbox.web;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.blackbox.api.BlackBoxBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class SigninTest extends BlackBoxBase {    

    @Value("${org.orcid.web.baseUri}")
    public String baseUri;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;

    @After
    public void after() {
        signout();
    }

    @Test
    public void signinTest() {
        webDriver.get(baseUri + "/userStatus.json?logUserOut=true");
        webDriver.get(baseUri + "/my-orcid");
        signIn(webDriver, user1UserName, user1Password);
        dismissVerifyEmailModal();
    }
    
    @Test
    public void signinVariousUsernameFormats() {
    	String user1FrmtSpaces = user1OrcidId.replace('-', ' ');
    	verifySignIn(user1FrmtSpaces);
    	String user1FrmtNoSpNoHyp = user1OrcidId.replace("-", "");
    	verifySignIn(user1FrmtNoSpNoHyp);
    	String user1FrmtProfLink = new StringBuffer(baseUri).append("/").append(user1OrcidId).toString();
    	verifySignIn(user1FrmtProfLink);
    }
    
    private void verifySignIn(String userName) {
    	webDriver.get(baseUri + "/userStatus.json?logUserOut=true");
        webDriver.get(baseUri + "/my-orcid");
        signIn(webDriver, userName, user1Password);
        dismissVerifyEmailModal();
    }

}
