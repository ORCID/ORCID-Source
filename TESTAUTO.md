# ORCID-Integration-Test

## Blackbox Tests

Blackbox tests are in [ORICD-Source/orcid-integration-test/src/test/java/org/orcid/integration/blackbox](https://github.com/ORCID/ORCID-Source/tree/master/orcid-integration-test/src/test/java/org/orcid/integration/blackbox)

### Prerequisites
1. Complete the ORCID [Development Environment Setup](https://github.com/ORCID/ORCID-Source/blob/master/DEVSETUP.md)
2. Install [Firefox 52 ESR](https://www.mozilla.org/en-US/firefox/organizations/all/), we suggest putting this into your `~/bin` as to not overwrite the default firefox. 
3. Verify Firefox installation and locate installation directory

        find / -name firefox 2>/dev/null
    
4. Install the latest [geckodriver](https://github.com/mozilla/geckodriver/releases). We suggest putting this into your `~/bin`.

### Set up the test data

Test data is set up using a whitebox test located at [src/test/java/org/orcid/integration/whitebox/SetUpClientsAndUsers.java](https://github.com/ORCID/ORCID-Source/blob/master/orcid-integration-test/src/test/java/org/orcid/integration/whitebox/SetUpClientsAndUsers.java)

The default test data is in the following config files:

* Users: [src/test/resources/test-web.properties](https://github.com/ORCID/ORCID-Source/blob/master/orcid-integration-test/src/test/resources/test-web.properties)
* Members/clients: [src/test/resources/test-client.properties](https://github.com/ORCID/ORCID-Source/blob/master/orcid-integration-test/src/test/resources/test-client.properties)

#### Eclipse

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

This should setup the test data and then run a test that verifies the data was persisted in the database. If this process succeeds, run the blackbox test as follows.

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

6. In the Arguments tab, set the following VM arguments (note that you need to insert your `webdriver.firefox.bin` and `webdriver.gecko.driver` paths)

        -Xmx2g
        -Dorg.orcid.persistence.db.url=jdbc:postgresql://localhost:5432/orcid
        -Dorg.orcid.config.file=classpath:test-web.properties,classpath:test-client.properties
        -Dorg.orcid.persistence.db.dataSource=simpleDataSource
        -Dorg.orcid.persistence.statistics.db.dataSource=statisticsSimpleDataSource
        -Dwebdriver.firefox.bin=[path to firefox executable]
        -Dwebdriver.gecko.driver=[path to geckodriver executable]

7. Click Apply, then click Run

####Command Line

1. Goto project directory

        cd ~/git/ORCID-Source

2. Run the test with the following arguments (note that you need to insert your `webdriver.firefox.bin` and `webdriver.gecko.driver` paths)

        export MAVEN_OPTS="-Xmx2g";
        mvn test -DfailIfNoTests=false \
        -Dtest=org.orcid.integration.blackbox.BlackBoxTestSuite \
        -Dorg.orcid.config.file='classpath:test-web.properties,classpath:test-client.properties' \
        -Dorg.orcid.persistence.db.url=jdbc:postgresql://localhost:5432/orcid \
        -Dorg.orcid.persistence.db.dataSource=simpleDataSource \
        -Dorg.orcid.persistence.statistics.db.dataSource=statisticsSimpleDataSource \
        -Dwebdriver.firefox.bin=[path to firefox executable] \
        -Dwebdriver.gecko.driver=[path to geckodriver executable]

VM Argument notes:

* For best results, use [Firefox 45 ESR](https://www.mozilla.org/en-US/firefox/organizations/all/)
* Common Firefox paths:
Win: ```C:\Program Files (x86)\Mozilla Firefox\firefox.exe```
Mac: ```/Applications/Firefox.app/Contents/MacOS/firefox-bin```
* To run tests with NGINX, adjust the base URIs in the properties files

    [src/test/resources/test-web.properties](https://github.com/ORCID/ORCID-Source/blob/master/orcid-integration-test/src/test/resources/test-web.properties)

    [src/test/resources/test-client.properties](https://github.com/ORCID/ORCID-Source/blob/master/orcid-integration-test/src/test/resources/test-client.properties)

# License
See [LICENSE.md](https://github.com/ORCID/ORCID-Source/blob/master/LICENSE.md)

