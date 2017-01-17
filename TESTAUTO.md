# ORCID-Integration-Test

There are two types of test in the project.

## New Style Blackbox Tests

The new style blackbox tests are in [ORICD-Source/orcid-integration-test/src/test/java/org/orcid/integration/blackbox](https://github.com/ORCID/ORCID-Source/tree/master/orcid-integration-test/src/test/java/org/orcid/integration/blackbox)

The new style tests are different from the old style integration tests because they use a set of test data configured before running the tests that is *not* removed from the database when the tests are run.

###Prerequisites
1. Complete the ORCID [Development Environment Setup](https://github.com/ORCID/ORCID-Source/blob/master/DEVSETUP.md)
2. Install [Firefox 45 ESR](https://www.mozilla.org/en-US/firefox/organizations/all/), we suggest putting this into your `~/bin` as to not overwrite the default firefox. 
3. Verify Firefox installation and locate installation directory

        find / -name firefox 2>/dev/null
    
4. Install the latest [geckodriver](https://github.com/mozilla/geckodriver/releases).

###Set up the test data

Test data is set up using a whitebox test located at [src/test/java/org/orcid/integration/whitebox/SetUpClientsAndUsers.java](https://github.com/ORCID/ORCID-Source/blob/master/orcid-integration-test/src/test/java/org/orcid/integration/whitebox/SetUpClientsAndUsers.java)

The default test data is in the following config files:

* Users: [src/test/resources/test-web.properties](https://github.com/ORCID/ORCID-Source/blob/master/orcid-integration-test/src/test/resources/test-web.properties)
* Members/clients: [src/test/resources/test-client.properties](https://github.com/ORCID/ORCID-Source/blob/master/orcid-integration-test/src/test/resources/test-client.properties)

####Eclipse

1. Select Run > Run Configurations
2. Right click JUnit and select New
3. In the Test tab, set the following options:
    
    * Run a single test: ```True```
    * Project: ```orcid-integration-test```
    * Test class: ```org.orcid.integration.whitebox.SetUpClientsAndUsers```
    * Test runner: ```JUnit 4```

4. In the Arguments tab, set the following VM arguments

         -Xmx2g
         -Dorg.orcid.config.file=classpath:staging-persistence.properties

5. Click Apply, then click Run

####Command Line

1. Goto project directory

        cd ~/git/ORCID-Source

2. Run the test with the following arguments

        export MAVEN_OPTS="-Xmx2g";
        mvn test \
        -Dtest=org.orcid.integration.whitebox.SetUpClientsAndUsers \
        -DfailIfNoTests=false \
        -Dorg.orcid.config.file='classpath:staging-persistence.properties';

This should setup the test data and then run a test that verifies the data persisted in the database. If this process succeeds, run the blackbox test as follows.

###Run the Blackbox tests

**Note:** Test data setup above must be run before each Blackbox test run, so that the data is in the correct state to start the Black box test.

####Eclipse

1. Make sure that the following modules are added to Tomcat (stop Tomcat before adding modules):

        orcid-api-web
        orcid-internal-api
        orcid-pub-web
        orcid-scheduler-web
        orcid-solr-web
        orcid-web

2. Start Tomcat and wait for it to be up
3. Select Run > Run Configurations
4. Right click JUnit and select New
5. In the Test tab, set the following options:

    * Run a single test: ```True```
    * Project: ```orcid-integration-test```
    * Test class: ```org.orcid.integration.blackbox.BlackBoxTestSuite```
    * Test runner: ```JUnit 4```

6. In the Arguments tab, set the following VM arguments (note that you need to insert your `webdriver.gecko.driver` path)

        -Xmx2g
        -Dorg.orcid.persistence.db.url=jdbc:postgresql://localhost:5432/orcid
        -Dorg.orcid.config.file=classpath:test-web.properties,classpath:test-client.properties
        -Dorg.orcid.persistence.db.dataSource=simpleDataSource
        -Dorg.orcid.persistence.statistics.db.dataSource=statisticsSimpleDataSource
        -Dwebdriver.gecko.driver=[path to geckodriver executable]

7. Click Apply, then click Run

####Command Line

1. Goto project directory

        cd ~/git/ORCID-Source

2. Run the test with the following arguments (note that you need to insert your `webdriver.gecko.driver` path)

        export MAVEN_OPTS="-Xmx2g";
        mvn test -DfailIfNoTests=false \
        -Dtest=org.orcid.integration.blackbox.BlackBoxTestSuite \
        -Dorg.orcid.config.file='classpath:test-web.properties,classpath:test-client.properties' \
        -Dorg.orcid.persistence.db.url=jdbc:postgresql://localhost:5432/orcid \
        -Dorg.orcid.persistence.db.dataSource=simpleDataSource \
        -Dorg.orcid.persistence.statistics.db.dataSource=statisticsSimpleDataSource \
        -Dwebdriver.gecko.driver=[path to geckodriver executable]

VM Argument notes:

* For best results, use [Firefox 45 ESR](https://www.mozilla.org/en-US/firefox/organizations/all/)
* Common Firefox paths:
Win: ```C:\Program Files (x86)\Mozilla Firefox\firefox.exe```
Mac: ```/Applications/Firefox.app/Contents/MacOS/firefox-bin```
* To run tests with NGINX, adjust the base URIs in the properties files

    [src/test/resources/test-web.properties](https://github.com/ORCID/ORCID-Source/blob/master/orcid-integration-test/src/test/resources/test-web.properties)

    [src/test/resources/test-client.properties](https://github.com/ORCID/ORCID-Source/blob/master/orcid-integration-test/src/test/resources/test-client.properties)

## Old Style Integration Tests

The old style integration tests are under ```[ORCID-Source]/orcid-integration-test/src/test/java/org/orcid/integration/api```.

Please note that the old style integration tests set up test data, and **remove all other data from the DB**. See below for more details.

In order to run them, you should have the ORCID project up and running.

But, before running the integration tests, there are some things that need to be configured: 

1. We should enable our server to run over SSL/https using the ORCID configuration, so, in the **server.xml** add the following configuration.

    Find the **service** element/tag and the following connector:

    ```
    <Connector SSLEnabled="true" clientAuth="want" keystoreFile="[ROOT_PATH]/orcid-api-web/src/test/resources/orcid-server-keystore.jks" keystorePass="changeit" maxThreads="150" port="8443" protocol="HTTP/1.1" scheme="https" secure="true" sslProtocol="TLS" truststoreFile="[ROOT_PATH]/orcid-api-web/src/test/resources/orcid-server-truststore.jks" truststorePass="changeit"/> 
    ```

    Please notice that you should update the path on "*keystoreFile*" and "*truststoreFile*"; that path should point to the root path where you have the ORCID code. When this id done, restart the server.

* We should run these tests using the ORCID persistence configuration, so, if you are using Eclipse or STS, follow these instructions:

* Run the tests:

    * In order to run the integration tests, you should have the ORCID server up and running, so, start the server if it is not started yet.
    * Go to the main menu and select *Run* → *Run Configurations*
    * Select JUnit
    * Click on the New button
    * Select 'Browse' and choose the orcid-integration-test project
    * Enter org.orcid.integration.api.IntegrationTestSuite as the test class
    * Select Junit 4 as the test runner
    * Go to the Arguments tab and enter the following in VM arguments.

    ```
    -Dorg.orcid.config.file=classpath:staging-persistence.properties
    -Dorg.orcid.persistence.db.dataSource=simpleDataSource
    -Dorg.orcid.persistence.statistics.db.dataSource=statisticsSimpleDataSource

    ```

    Please note that the old style integration tests set up test data, and **remove all other data from the DB**. So, you may want to set up another DB called orcid_test just for the integration tests, and then use the following property in both your Tomcat instance (maybe configure an additional Tomcat instance with this property, just for running the integration tests against - see above) and your integration tests run configuration.

    ```
    -Dorg.orcid.persistence.db.url=jdbc:postgresql://localhost:5432/orcid_test
    ````

    * Click Run
    
# License
See [LICENSE.md](https://github.com/ORCID/ORCID-Source/blob/master/LICENSE.md)

