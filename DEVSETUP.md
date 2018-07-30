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

psql -U postgres -c "CREATE DATABASE features;"
psql -d postgres -c "GRANT ALL PRIVILEGES ON DATABASE features to orcid;"

psql -U postgres -c "CREATE DATABASE message_listener;"
psql -d postgres -c "GRANT ALL PRIVILEGES ON DATABASE message_listener to orcid;"
```

Verify user login and database exist

    psql -U orcid -d orcid -c "\list" -h localhost
    psql -U statistics -d statistics -c "\list" -h localhost

## Clone the git repositories

Clone the repository

    git clone https://github.com/ORCID/ORCID-Source.git

## Run Maven build

Skip test the first time you run this

    cd ORCID-Source
    mvn clean install -Dmaven.test.skip=true -Dlicense.skip=true -f orcid-test/pom.xml && \
    mvn clean install -Dmaven.test.skip=true -Dlicense.skip=true -f orcid-model/pom.xml && \
    mvn clean package -Dmaven.test.skip=true -Dlicense.skip=true

>If you experience the below error you can find the solution [here](http://stackoverflow.com/questions/25911623/problems-using-maven-and-ssl-behind-proxy)

    Caused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target

## Verify you have the DigiCertGlobalRootG2.crt

This is an annoying issue where Java's cacerts file lags behind listing common Certificate Authorities. Mailgun uses DigiCertGlobalRootG2 and you'll need verify the cert is installed.

Run the following with administrator privileges(windows users will have have to use an admin account and remove sudo command) while in the project root.

1. Make sure everything is compiled

        mvn compile;

2. Run command as adminstrator  

Linux:

    sudo mvn exec:java -pl orcid-utils -Dexec.mainClass="org.orcid.utils.AddCacertsUtil" -Dexec.args="--keystore_password changeit" 

Windows:

    mvn exec:java -pl orcid-utils -Dexec.mainClass="org.orcid.utils.AddCacertsUtil" -Dexec.args="--keystore_password changeit"

>Note: the java keystore password is usefully 'changeit' this can be different if you've changed it.

## Create the Database Schema

    cd ORCID-Source/orcid-core
    mvn exec:java -Dexec.mainClass=org.orcid.core.cli.InitDb

    cd ..
    psql -U postgres -d orcid -f orcid-persistence/src/main/resources/db/updates/json-setup.sql


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

* Wait for build to finish then right-click orcid-api-common project and select Build Path->Configure Build path. Click the 'add folder' button and add "orcid-api-common/target/generated-sources/jena".

* For Windows 10 users, if all your projects shows an error "Missing artifact jdk.tools:jdk.tools:jar:1.6", it means your STS Maven plugin is looking for a Java 1.6 tools.jar library, please modify the STS.ini fileto indicate the java executable you want to use to run STS, which should be the JDK one: 

        -vm
        C:/Program Files/Java/jdk1.8.0_65/bin/javaw.exe
Do this before the '-vmargs' param

### Tomcat Setup

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

        -Dsolr.solr.home=/Users/rcpeters/git/ORCID-Source/orcid-solr-web/src/main/webapp/solr -Dorg.orcid.config.file=classpath:staging-persistence.properties -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true -XX:MaxPermSize=1024m -Dcom.mailgun.testmode=no -Dorg.orcid.message-listener.properties=classpath:message-listener.properties -Dorg.orcid.message-listener.development_mode=true -Dorg.orcid.activemq.config.file=classpath:orcid-activemq.properties

* Click Ok

* Under **Server Locations** select **User custom location** set and **Server Path** to `/tmp-tomcat-orcid-web`.
      * You also need to make a directory under `/tmp-tomcat-orcid-web`. In a linux shell:
  `sudo mkdir /tmp-tomcat-orcid-web; sudo chown -R $(whoami) /tmp-tomcat-orcid-web;`

* Under `Server Options` make sure everything is unchecked.

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

### Add Typescript support

* Follow instructions at [https://github.com/palantir/eclipse-typescript](https://github.com/palantir/eclipse-typescript)

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

### Configure Message Listener

* Create a directory to be used as the message store directory for the ActiveMQ broker (changing the /Users/rcpeters/git/ path to a path on your machine)

        mkdir /Users/rcpeters/git/mq

* Go to File -> New -> Other.

* Filter for 'Server', select and click Next.

* Expand the folder Apache, select Apache Tomcat.

* Choose the same version as you selected in Tomcat Setup above

* Change Server name field to Message Listener

* Click Next and Finish.

* Select Window -> Show View -> Servers

* Double Click "Message Listener"

* Select Open launch configuration

* Select Arguments 

* In VM Arguments add the following (changing org.orcid.persistence.path to the path to the directory you created above):

        -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true -XX:MaxPermSize=1024m -Dcom.mailgun.testmode=no -Dorg.orcid.message-listener.properties=classpath:message-listener.properties -Dorg.orcid.message-listener.development_mode=true -Dorg.orcid.activemq.config.file=classpath:orcid-activemq.properties -Dorg.orcid.persistence.solr.url=http://localhost:8080/orcid-solr-web -Dorg.orcid.persistence.solr.read.only.url=http://localhost:8080/orcid-solr-web -Dorg.orcid.persistence.messaging.enabled=true -Dorg.orcid.persistence.path="/Users/rcpeters/git/mq"

* Click Ok

* Save and close the server configuration view.

* In the Servers tab, right click on "Message Listener".

* Select "Add and Remove." Add orcid-activemq and orcid-message-listener

* In[orcid-message-listener.properties](https://github.com/ORCID/ORCID-Source/blob/master/orcid-message-listener/src/main/resources/message-listener.properties#L47), change the value of org.orcid.message-listener.api.read_public_access_token to a valid /read-public access token. See [Basic tutorial: Searching data using the ORCID API](https://members.orcid.org/api/tutorial/search-orcid-registry for instructions) for steps to generate a token.

* In the Servers tab, right click on "Message Listener" and click Start.

### Configure frontend (Angular2)

Follow next instructions in order to generate the core javascript file.

See [How to produce angular_orcid_generated.js](https://github.com/ORCID/ORCID-Source/blob/master/orcid-nodejs/README.md). 
For background about webpack see [Webpack setup](https://github.com/ORCID/ORCID-Source/tree/master/orcid-web/src/main/webapp/static/javascript)
.


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

## Eclipse tips
[ECLIPSE_TIPS.md](ECLIPSE_TIPS.md)
