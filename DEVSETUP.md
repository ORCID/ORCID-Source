# Development Environment Setup

## Prerequisites 

* Install [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Add JAVA_HOME environment variable:
  * Windows - control panel -> system -> advanced system settings -> environment variables
  * Mac - create or edit .bash_profile file in home directory, add EXPORT JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home

* Install [Java JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
  --> replace local_policy.jar and US_export_policy.jar in <JAVA_HOME>/jre/lib/security with those from JCE download

* Install [Maven](http://maven.apache.org/index.html) - ensure you add maven/bin directory to PATH environment variable. Verify installation with mvn -version

* Install Postgres version 9.5.5:
  * [Windows](http://www.postgresql.org/download/) - verify with psql -U postgres in postgres installation's bin directory in command prompt
  * [Mac](http://postgresapp.com/) - add postgres bin directory to .bash_profile directory
  ```
  export PATH=/Applications/Postgres.app/Contents/Versions/latest/bin:$PATH
  ```
  
* Install [Tomcat](http://tomcat.apache.org/) and ensure it starts

* Ensure a git client is installed

## Setup Postgres DB

* Run the following commands from the command line (or use pgAdmin to run the SQL queries) to create the databases and roles.

```
psql -U postgres -c "CREATE DATABASE orcid;"
psql -U postgres -c "CREATE USER orcid WITH PASSWORD 'orcid';" 
psql -d postgres -c "GRANT ALL PRIVILEGES ON DATABASE orcid to orcid;"

psql -U postgres -c "CREATE DATABASE statistics;" 
psql -U postgres -c "CREATE USER statistics WITH PASSWORD 'statistics';" 
psql -U postgres -d postgres -c "GRANT ALL PRIVILEGES ON DATABASE statistics to statistics;"

psql -U postgres -c "CREATE USER orcidro WITH PASSWORD 'orcidro';"
psql -U postgres -c "GRANT CONNECT ON DATABASE orcid to orcidro;"
psql -U postgres -d orcid -c "GRANT SELECT ON ALL TABLES IN SCHEMA public to orcidro;"
```

* Verify user login and database exist

```
psql -U orcid -d orcid -c "\list" -h localhost
psql -U statistics -d statistics -c "\list" -h localhost
```

## Clone the git repositories

* Clone the repository

```
git clone https://github.com/ORCID/ORCID-Source.git
```

* Clone the git ORCID-Fonts-Dot-Com repository (due to licensing issues this is only available to ORCID.org employees) into the static fonts directory

```
git clone https://github.com/ORCID/ORCID-Fonts-Dot-Com.git ORCID-Source/orcid-web/src/main/webapp/static/ORCID-Fonts-Dot-Com
```

## Run Maven build

* Skip test the first time you run this

```
cd ORCID-Source
mvn clean install -Dmaven.test.skip=true
```

* If you experience the below error you can find the solution [here](http://stackoverflow.com/questions/25911623/problems-using-maven-and-ssl-behind-proxy)

```
Caused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```
   
## Create the Database Schema

```
cd ORCID-Source/orcid-core
mvn exec:java -Dexec.mainClass=org.orcid.core.cli.InitDb

cd ..
psql -U postgres -d orcid -f orcid-persistence/src/main/resources/db/updates/json-setup.sql

```

## Eclipse Setup (Spring Tool Suite Eclipse)

These instructions are for Spring Tool Suite for Eclipse. 

* Download and install Spring Tool Suite for [Eclipse](http://www.springsource.org/downloads/sts-ggts)

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

* For Windows 10 users, if all your projects shows an error "Missing artifact jdk.tools:jdk.tools:jar:1.6", it means your STS Maven plugin is looking for a Java 1.6 tools.jar library, please modify the STS.ini fileto indicate the java executable you want to use to run STS, which should be the JDK one: 

```
-vm
C:/Program Files/Java/jdk1.8.0_65/bin/javaw.exe
``` 
Do this before the '-vmargs' param

* Select Window -> Preferences -> Servers(Expand) -> Runtime Environments

* Click on Add.

* Expand the folder Apache, select Apache Tomcat and click Next.

* Browse to the directory of apache tomcat in the file system and click Finish.

* Click OK.

* Go to File -> New -> Other.

* Filter for 'Server', select and click Next.

* Expand the folder Apache, select Apache Tomcat.

* Field 'Server Runtime Environment' should point to the newly added server runtime for tomcat.

* Click Next and Finish.

* Select Window -> Show View -> Servers

* Double Click "Apache Tomcat Server"

* Select Open launch configuration

* Select Arguments 

* In VM Arguments add the following (changing the /Users/rcpeters/git/ORCID-Source path to your repo checkout)

```
-Dsolr.solr.home=/Users/rcpeters/git/ORCID-Source/orcid-solr-web/src/main/webapp/solr -Dorg.orcid.config.file=classpath:staging-persistence.properties -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true -XX:MaxPermSize=1024m -Dcom.mailgun.testmode=no -Dorg.orcid.message-listener.properties=classpath:message-listener.properties -Dorg.orcid.message-listener.development_mode=true -Dorg.orcid.activemq.config.file=classpath:orcid-activemq.properties
```
* Click Ok

* In Timeouts, increase the time limit of Start to 600 seconds and Stop to 100.

* Save and close the server configuration view.

* Right click on "Apache Tomcat Server".

* Select "Add and Remove" Add orcid-api-web, orcid-pub-web, orcid-scheduler-web, orcid-solr-web and orcid-web

### Setting up Eclipse to use ORCID formatting rules

* Select Eclipse (or Spring Tool Suit) -> Preferences -> Java -> Code style -> Formatter -> Import

  * Navigate to ORCID-Source and select eclipse_formatter.xml

  * Click "Apply"

* Select Eclipse (or Spring Tool Suit) -> Preferences -> JavaScript -> Code style -> Formatter -> Import

  * Navigate to ORCID-Source and select eclipse_javascript_formatter.xml

  * Click "Apply"

### Disabling JPA facet for orcid-persistence

* Select Eclipse (or Spring Tool Suit) -> Preferences -> Validation 

  * Uncheck the JPA validatior checkboxes

  * Click "Ok"

### Enabling https

We should enable our server to run over SSL/https using the ORCID configuration, so, in the **server.xml** add the following configuration.

Find the **service** element/tag and the following connector:

```
<Connector SSLEnabled="true" clientAuth="want" keystoreFile="[ROOT_PATH]/orcid-api-web/src/test/resources/orcid-server-keystore.jks" keystorePass="changeit" maxThreads="150" port="8443" protocol="HTTP/1.1" scheme="https" secure="true" sslProtocol="TLS" truststoreFile="[ROOT_PATH]/orcid-api-web/src/test/resources/orcid-server-truststore.jks" truststorePass="changeit"/> 
```

Please notice that you should update the path on "*keystoreFile*" and "*truststoreFile*"; that path should point to the root path where you have the ORCID code. 

When this it is done, restart the server.

### Testing your set up

* Right click on "Apache Tomcat Server"

* Select Debug

* Point your browser to https://localhost:8443/orcid-web/signin

* You should see a login page.

* Click OK.

## Updating

* Get latest version

```
cd ORCID-Source
git checkout master
git pull
```

* In Spring Tool Suite go to Package Explorer

* Select all items

* Right click, and select refresh

# Testing

## Automated Testing

See [TESTAUTO.md](TESTAUTO.md)

## Manual Testing

See [Manual Test](https://github.com/ORCID/ORCID-Source/tree/master/orcid-integration-test/src/test/manual-test)

* Finally help out by improving these instructions!    

## Updating the frontend javascript files
[Webpack setup](https://github.com/ORCID/ORCID-Source/blob/orcid-web/src/main/webapp/static/javascript/readme.md)