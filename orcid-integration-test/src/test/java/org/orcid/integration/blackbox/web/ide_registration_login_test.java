import junit.framework.Test;
import junit.framework.TestSuite;

public class Ide_registration_login_test {

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(Sign-in website.class);
    suite.addTestSuite(Sign-in Oauth.class);
    suite.addTestSuite(Registration website.class);
    suite.addTestSuite(Registration OAuth.class);
    suite.addTestSuite(Sign-in Deny.class);
    suite.addTestSuite(Signin_oauth_deactivated.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
