# Development Environment Setup

##Prerequisites 

1. Install [Java SDK](http://www.oracle.com/technetwork/java/javaee/downloads/index.html)
    
* Install [Maven](http://maven.apache.org/index.html)

* Install [Postgres](http://www.postgresql.org/download/)

* Install [Tomcat 7](http://tomcat.apache.org/)

## Setup Postgres DB
We'll set up postgres using the default settings in 
[staging-persistence.properties](https://github.com/ORCID/ORCID-Source/blob/master/orcid-persistence/src/main/resources/staging-persistence.properties).
 Please change usernames and passwords for any production environments.

1. Become postgres user

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
    mvn clean install
    ```
    
Tip: use the same command for rebuilding.

## Create the Database Schema - First Time Only

Intialize the database schema (runs as orcid the first time, but then you need to run it again as postgres because some tasks require superuser).

    ```
    cd ~/git/ORCID-Source/orcid-core
    
    mvn exec:java -Dexec.mainClass=org.orcid.core.cli.InitDb
    
    mvn exec:java -Dexec.mainClass=org.orcid.core.cli.InitDb -Dorg.orcid.persistence.db.username=postgres -Dorg.orcid.persistence.db.password=postgres
    
    ```

## Eclipse Setup (Spring Tool Suite Eclipse)
These instructions are for Spring Tool Suite for Eclipse. 

1. Download and install Spring Tool Suite for Eclipse:
http://www.springsource.org/downloads/sts-ggts

* Select File Import "Project from Git", Click Next.

* Select Local, Click Next

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

* Select Window -> Show View -> Servers

* Double Click "VMWare vFabric tc Server"

* Select Open launch configuration

* Select Arguments 

* In VM Arguments add the following (changing the /Users/rcpeters/git/ORCID-Source path to your repo checkout)

    ```
    -Dsolr.solr.home=/Users/rcpeters/git/ORCID-Source/orcid-solr-web/src/main/webapp/solr -Dorg.orcid.config.file=classpath:staging-persistence.properties -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true
    ```

* Click Ok

* Right click on "VMWare vFabric tc Server"

* Select "Add and Remove" Add orcid-api-web, orcid-pub, orcid-scheduler-web, orcid-solr-web and orcid-web

* Right click on "VMWare vFabric tc Server"

* Select Debug

* Point your browser to http://localhost:8080/orcid-web/my-orcid

* You should see a login page.

* Click OK.

### Setting up Eclipse to use ORCID formatting rules 
1. Select Eclipse (or Spring Tool Suit) -> Preferences -> Java -> Code style -> Formatter -> Import

* Navigate to ~/git/ORCID-Source and select eclipse_formatter.xml

Finally help out by improving these instructions! 

# Testing
## Maven test

1. cd to [ORCID-Source]

2. run maven test ```mvn test```

## Integration tests

Integration tests are under ```[ORCID-Source]/orcid-core/src/test/java/org/orcid/core/integration```.

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
	* On the "*VM arguments*" add "-Dorg.orcid.config.file=classpath:staging-persistence.properties" 
	* Click "*Apply*" 

* Run the tests: 

	* In order to run the integration tests, you should have the ORCID server up and running, so, start the server if it is not started yet.
	* Go to "```/orcid-api-web/src/test/java/org/orcid/api/t2/integration```"
	* Right click over the package
	* Select “*Run As*” → “*JUnit Test*”

* Finally help out by improving these instructions!    
   
