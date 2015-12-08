# Development Environment Setup

##Prerequisites 

1. Install [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html). Add an environment variable JAVA_HOME. (Verify Java. Go to cmd and type "java -version". It should display the version of Java)

* Install [Java JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html). [OSX](http://stackoverflow.com/questions/12245179/how-to-install-unlimited-strength-jce-for-jre-7-in-macosx) [Windows](http://help.boomi.com/atomsphere/GUID-D7FA3445-6483-45C5-85AD-60CA5BB15719.html)
    
* Install [Maven](http://maven.apache.org/index.html). Add an environment variable M2_HOME. (Verify Maven. Go to cmd and type "mvn -version". It should display the version of Maven)

* Install [Postgres](http://www.postgresql.org/download/). (Verify Postgres. Go to cmd. Navigate to /postgres/xx/bin and execute the command "psql -U postgres". Type the password entered during the installation, if prompted. It should show a postgres console.)

* Install [Tomcat 7](http://tomcat.apache.org/). (Verify Tomcat. Go to the directory /apache-tomcat-7.x/bin and run the batch "startup.bat". It should start the server and display a message "Server startup in xxxx ms".)


## Setup Postgres DB
We'll set up postgres using the default settings in 
[staging-persistence.properties](https://github.com/ORCID/ORCID-Source/blob/master/orcid-persistence/src/main/resources/staging-persistence.properties).
 Please change usernames and passwords for any production environments.

1. Become postgres user (note: your username for the superuser may differ)

    ```
    sudo su - postgres
    ```
    
* Set up database

    ```
    psql -c "CREATE DATABASE orcid;"     
    psql -c "CREATE USER orcid WITH PASSWORD 'orcid';" 
    psql -c "GRANT ALL PRIVILEGES ON DATABASE orcid to orcid;"
    
    psql -c "CREATE DATABASE statistics;" 
    psql -c "CREATE USER statistics WITH PASSWORD 'statistics';" 
    psql -c "GRANT ALL PRIVILEGES ON DATABASE statistics to statistics;"
    
    psql -c "CREATE USER orcidro WITH PASSWORD 'orcidro';"
    psql -c "GRANT CONNECT ON DATABASE orcid to orcidro;"
    psql -c "GRANT SELECT ON ALL TABLES IN SCHEMA public to orcidro;"
    ```
    
* Exit postgres user prompt
    
    ```
    exit
    ```

* Verify user login and database exist

    ```
    psql -U orcid -d orcid -c "\list" -h localhost
    psql -U statistics -d statistics -c "\list" -h localhost
    ```


## Clone the git repository

1. Make a git directory if one doesn't exist


    ```
    mkdir ~/git
    ```


* Clone the repository

	```
	cd ~/git
    git clone git@github.com:ORCID/ORCID-Source.git
    ```


## Clone the git ORCID-Fonts-Dot-Com repository
Do to licensing issues this is only available to ORCID.org employees.

1. Clone the ORCID-Fonts-Dot-Com repository into the static directory

	```
    git clone git@github.com:ORCID/ORCID-Fonts-Dot-Com.git ~/git/ORCID-Source/orcid-web/src/main/webapp/static/ORCID-Fonts-Dot-Com
    ```


## Run Maven Task - First Time Only
1. maven clean install

    ```
    cd ~/git/ORCID-Source
    mvn clean install -Dmaven.test.skip=true
    ```
    
Tip: If you experience the following error: 

    ```
    Caused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
    ```
   
You can find the solution [here](http://stackoverflow.com/questions/25911623/problems-using-maven-and-ssl-behind-proxy)    
    
Tip: use the same command for rebuilding.

## Create the Database Schema - First Time Only

Intialize the database schema (runs as orcid the first time, but then you need to run it again as postgres because some tasks require superuser).

    ```
    cd ~/git/ORCID-Source/orcid-core
    
    mvn exec:java -Dexec.mainClass=org.orcid.core.cli.InitDb
    
    cd ..
    
    sudo su - postgres
    
    psql -d orcid -f orcid-persistence/src/main/resources/db/updates/work-external-ids-as-json.sql
    
    ```

## Eclipse Setup (Spring Tool Suite Eclipse)
These instructions are for Spring Tool Suite for Eclipse. 

1. Download and install Spring Tool Suite for Eclipse:
http://www.springsource.org/downloads/sts-ggts

* Select File-> Import -> Git -> Project from Git, Click Next.

* Select "Existing local repository", Click Next

* Select Add, once ORCID-Source has been added, select it and click Next

* Select "Import as general project", click Next.

* Click Finish

* In package Explorer, right click ORCID-Source.

* Select Configure (at the bottom) -> Select "Convert to Maven Project"

* In package Explorer Right click on ORCID-Sourc 

* Select Import -> "Existing Maven Projects"

* Unselect the first pom.xml (orcid-parent)

* Select all pom.xml(s) after.

* Click Finish

* Select Window -> Preferences -> Servers(Expand) -> Runtime Environments

* Click on Add.

* Expand the folder Apache, select Apache Tomcat v7.0 and click Next.

* Browse to the directory of apache tomcat in the file system and click Finish.

* Click OK.

* Go to File -> New -> Other.

* Filter for 'Server', select and click Next.

* Expand the folder Apache, select Apache Tomcat v7.0.

* Field 'Server Runtime Environment' should point to the newly added server runtime for tomcat.

* Click Next and Finish.

* Select Window -> Show View -> Servers

* Double Click "Apache Tomcat Server 7.0"

* Select Open launch configuration

* Select Arguments 

* In VM Arguments add the following (changing the /Users/rcpeters/git/ORCID-Source path to your repo checkout)

    ```
    -Dsolr.solr.home=/Users/rcpeters/git/ORCID-Source/orcid-solr-web/src/main/webapp/solr -Dorg.orcid.config.file=classpath:staging-persistence.properties -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true -XX:MaxPermSize=1024m -Dcom.mailgun.testmode=no
    ```
* Click Ok

* In Timeouts, increase the time limit of Start to 600 seconds and Stop to 100.

* Save and close the server configuration view.

* Right click on "Apache Tomcat Server 7.0".

* Select "Add and Remove" Add orcid-api-web, orcid-pub-web, orcid-scheduler-web, orcid-solr-web, orcid-integration-test and orcid-web

* Right click on "Apache Tomcat Server 7.0"

* Select Debug

* Point your browser to http://localhost:8080/orcid-web/my-orcid

* You should see a login page.

* Click OK.

### Setting up Eclipse to use ORCID formatting rules 
1. Select Eclipse (or Spring Tool Suit) -> Preferences -> Java -> Code style -> Formatter -> Import

* Navigate to ~/git/ORCID-Source and select eclipse_formatter.xml

* Click "Apply"

2. Select Eclipse (or Spring Tool Suit) -> Preferences -> JavaScript -> Code style -> Formatter -> Import

* Navigate to ~/git/ORCID-Source and select eclipse_javascript_formatter.xml

* Click "Apply"

### Disabling JPA facet for orcid-persistence
1. Select Eclipse (or Spring Tool Suit) -> Preferences -> Validation 

* Uncheck the JPA validatior checkboxes

* Click "Ok"

# Testing
## Maven test

1. cd to [ORCID-Source]

2. run maven test ```mvn test```

## Integration tests

See [orcid-integration-test/README.md](https://github.com/ORCID/ORCID-Source/blob/master/orcid-integration-test/README.md)

Integration tests are under ```[ORCID-Source]/orcid-integraton-test/src/test/java/org/orcid/integration```.

In order to run them, you should have the ORCID project up and running.

But, before running the integration tests, there are some things that might be configured: 

1. We should enable our server to run over SSL/https using the ORCID configuration, so, in the **server.xml** add the following configuration.
Find the **service** element/tag and the following connector: 

	```
	<Connector SSLEnabled="true" clientAuth="want" keystoreFile="[ROOT_PATH]/orcid-api-web/src/test/resources/orcid-server-keystore.jks" keystorePass="changeit" maxThreads="150" port="8443" protocol="HTTP/1.1" scheme="https" secure="true" sslProtocol="TLS" truststoreFile="[ROOT_PATH]/orcid-api-web/src/test/resources/orcid-server-truststore.jks" truststorePass="changeit"/> 
	```
	Please notice that you should update the path on "*keystoreFile*" and "*truststoreFile*"; that path should point to the root path where you have the ORCID code. 
	When this id done, restart the server.

	
* We should run these tests using the ORCID persistence configuration, so, if you are using Eclipse or STS, follow these instructions: 

	* Go to the main menu and select *Run* → *Run Configurations* 
	* Select the JUnit run configurations
	* Open the "*Arguments*" tab 
	* On the "*VM arguments*" add the following arguments:
		* "-Dorg.orcid.config.file=classpath:staging-persistence.properties" 
		* "-Dorg.orcid.persistence.db.url=db connection URL e.g. jdbc:postgresql://localhost:5432/orcid"
		* "-Dorg.orcid.persistence.db.dataSource=simpleDataSource" 
		* "-Dorg.orcid.persistence.statistics.db.dataSource=statisticsSimpleDataSource" 
		* "-Dorg.orcid.web.testUser1.username=Test user's email id" 
		* "-Dorg.orcid.web.testUser1.password=Test user's password" 
		* "-Dorg.orcid.web.testUser1.orcidId=Test user's orcid id" 
		* "-Dorg.orcid.web.testUser2.username=Test user #2 email id"
		* "-Dorg.orcid.web.testUser2.password=Test user #2 password"
		* "-Dorg.orcid.web.testUser2.orcidId=Test user #2 orcid id"
		* "-Dorg.orcid.web.testClient1.redirectUri=1st test client's redirect uri" 
		* "-Dorg.orcid.web.testClient1.clientId=1st test client's Id" 
		* "-Dorg.orcid.web.testClient1.clientSecret=1st test client's secret" 
		* "-Dorg.orcid.web.testClient2.redirectUri=2nd test client's redirect uri" 
		* "-Dorg.orcid.web.testClient2.clientId=2nd test client's Id" 
		* "-Dorg.orcid.web.testClient2.clientSecret=2nd test client's secret"  
		* "-Dorg.orcid.web.adminUser.username=Test admin user's email id" 
		* "-Dorg.orcid.web.adminUser.password=Test admin user's password" 
		* "-Dorg.orcid.web.locked.member.id=Member id to lock" 
		* "-Dorg.orcid.web.locked.member.client.id=Client id that must belong to the member defined in the previous param" 
		* "-Dorg.orcid.web.locked.member.client.secret=Client secret" 
		* "-Dorg.orcid.web.locked.member.client.ruri=Client redirect URI" 
	* Click "*Apply*" 
	
* Make sure you have firefox installed so selenium can run the tests.  Note sometimes firefox is out of sync with selenium support or visa versa, so pick up the version before latest.  At time of writing latest selenium 2.45 works with firefox version 37 but not 38. Archives can be found at https://https://ftp.mozilla.org/pub/mozilla.org/firefox/releases/37.0.2/

* Run the tests: 

	* In order to run the integration tests, you should have the ORCID server up and running, so, start the server if it is not started yet.
	* Go to the main menu and select *Run* → *Run Configurations* 
	* Select JUnit
	* Click on the New button
	* Select 'Run all the tests in the selected project, package or source folder'
	* Click on Search and select orcid-integration-test
	* Select Junit 4 as the test runner
	* Go to the Arguments tab and enter the following in VM arguments.
	
	```
	-Dorg.orcid.persistence.db.dataSource=simpleDataSource
	-Dorg.orcid.persistence.statistics.db.dataSource=statisticsSimpleDataSource
	
	```
Please note that the integration tests set up test data, and **remove all other data from the DB**. So, you may want to set up another DB called orcid_test just for the integration tests, and then use the following property in both your Tomcat instance (maybe configure an additional Tomcat instance with this property, just for running the integration tests against - see above) and your integration tests run configuration.

	```
	-Dorg.orcid.persistence.db.url=jdbc:postgresql://localhost:5432/orcid_test
	````
	
	* Click Run

* Finally help out by improving these instructions!    
   
