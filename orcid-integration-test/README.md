# ORCID-Integration-Test

There are two types of test in the project.

## New Style Blackbox Tests

The new style blackbox tests are under ```[ORCID-Source]/orcid-integration-test/src/test/java/org/orcid/integration/blackbox```.

The new style blackbox are different from the old style integration tests, because they do not require the database to be populated with a known set of test data. Instead you configure the test suite with user and client details that you have set up by hand in your own database. The advantage is that existing data in the database are *not* removed when the tests run.

* Run the tests:

    * In order to run the integration tests, you should have the ORCID server up and running, so, start the server if it is not started yet.
    * Go to the main menu and select *Run* → *Run Configurations*
    * Select JUnit
    * Click on the New button
    * Select 'Browse' and choose the orcid-integration-test project
    * Enter org.orcid.integration.api.BlackBoxTestSuite as the test class
    * Select Junit 4 as the test runner
    * Go to the Arguments tab and enter the following in VM arguments, but *change the values to users and clients that exist in your database*.

    ```
    -Dorg.orcid.web.testUser1.username=orcid_intergration_1@mailinator.com
    -Dorg.orcid.web.testUser1.password=orcid_intergration_1@mailinator.com
    -Dorg.orcid.web.testUser1.orcidId=4444-4444-4444-4441
    -Dorg.orcid.web.testClient1.clientId=4444-4444-4444-4445
    -Dorg.orcid.web.testClient1.clientSecret=client-secret
    -Dorg.orcid.web.testClient1.redirectUri=http://localhost:8080/orcid-web/oauth/playground
    -Dorg.orcid.web.testClient2.clientId=APP-5555555555555555
    -Dorg.orcid.web.testClient2.clientSecret=client-secret
    -Dorg.orcid.web.testClient2.redirectUri=http://localhost:8080/orcid-web/oauth/playground
    -Dorg.orcid.web.publicClient1.clientId=4444-4444-4444-4498
    -Dorg.orcid.web.publicClient1.clientSecret=client-secret
    ```

    For more details of the properties to override and their meanings see the following.

    [src/test/resources/test-web.properties](https://github.com/ORCID/ORCID-Source/blob/master/src/test/resources/test-web.properties)

    [src/test/resources/test-client.properties](https://github.com/ORCID/ORCID-Source/blob/master/src/test/resources/test-client.properties)

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

