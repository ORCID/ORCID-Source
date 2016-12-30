# Development Environment Setup

## Prerequisites 

* Install [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html). Add an environment variable JAVA_HOME. (Verify Java. Go to cmd and type "java -version". It should display the version of Java)

* Install [Java JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html). see: [OSX](http://stackoverflow.com/questions/12245179/how-to-install-unlimited-strength-jce-for-jre-7-in-macosx) or [Windows](http://help.boomi.com/atomsphere/GUID-D7FA3445-6483-45C5-85AD-60CA5BB15719.html)

* Java / JCE installation on MAC
Follow intructions at [Oracle Install Overview](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html#CJAGAACB)

* Create JAVA_HOME pointing to /Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk

Extract the contents of UnlimitedJCEPolicyJDK7.zip into /Users/jeffrey/Sites/UnlimitedJCEPolicy/

```    
mkdir /Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home/jre/lib/security/old
mv /Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home/jre/lib/security/US_export_policy.jar /Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home/jre/lib/security/local_policy.jar /Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home/jre/lib/security/old
cp /Users/jeffrey/Sites/UnlimitedJCEPolicy/*.jar /Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home/jre/lib/security/
```

* Install [Maven](http://maven.apache.org/index.html). Add an environment variable M2_HOME. (Verify Maven. Go to cmd and type "mvn -version". It should display the version of Maven)

NOTE: In the case of Windows, don't create an environment variable named M2_HOME. instead add the path to the bin folder of Maven (i.e. C:\apache-maven-3.3.9\bin) to the PATH variable. If the PAth variable doesn't exists, create it, if it does, ensure you separate the new value by a semi-colon(;) 

* Install [Postgres] Windows: (http://www.postgresql.org/download/) version 9.3.x. (Verify Postgres. Go to cmd. Navigate to /postgres/xx/bin and execute the command "psql -U postgres". Type the password entered during the installation, if prompted. It should show a postgres console.)

* Install [Postgres] Mac: install postgres following the directions at http://postgresapp.com/ and add the postgres path to your bash profile

```
nano .bash_profile
```
add a new line 
```
export PATH=$PATH:/Applications/Postgres.app/Contents/Versions/latest/bin
```
save and exit
activate your changes with 
```
source .bash_profile
```

* Install [Tomcat](http://tomcat.apache.org/). (Verify Tomcat. Go to the directory /apache-tomcat-xx/bin and run the batch "startup.bat". It should start the server and display a message "Server startup in xxxx ms".)


## Setup Postgres DB

We'll set up postgres using the default settings in [staging-persistence.properties](https://github.com/ORCID/ORCID-Source/blob/master/orcid-persistence/src/main/resources/staging-persistence.properties). Please change usernames and passwords for any production environments.

*  Become postgres user

```
sudo su - postgres
```
or if using postgresapp
```
psql -U postgres
```
    
* Set up database

```
psql -c "CREATE DATABASE orcid;"     
psql -c "CREATE USER orcid WITH PASSWORD 'orcid';" 
psql -d postgres -c "GRANT ALL PRIVILEGES ON DATABASE orcid to orcid;"

psql -c "CREATE DATABASE statistics;" 
psql -c "CREATE USER statistics WITH PASSWORD 'statistics';" 
psql -d postgres -c "GRANT ALL PRIVILEGES ON DATABASE statistics to statistics;"

psql -c "CREATE USER orcidro WITH PASSWORD 'orcidro';"
psql -c "GRANT CONNECT ON DATABASE orcid to orcidro;"
psql -d orcid -c "GRANT SELECT ON ALL TABLES IN SCHEMA public to orcidro;"
```

* Exit postgres user prompt

    
```
exit
```

* Set up database using pgAdmin III

Under "databases" select "postgres". This will enable the SQL query editor. Click on it.

Run the following queries:

```
CREATE DATABASE orcid;    
CREATE USER orcid WITH PASSWORD 'orcid';
GRANT ALL PRIVILEGES ON DATABASE orcid to orcid;

CREATE DATABASE statistics;
CREATE USER statistics WITH PASSWORD 'statistics';
GRANT ALL PRIVILEGES ON DATABASE statistics to statistics;

CREATE USER orcidro WITH PASSWORD 'orcidro';
GRANT CONNECT ON DATABASE orcid to orcidro;
GRANT SELECT ON ALL TABLES IN SCHEMA public to orcidro;
```

* Verify user login and database exist

```
psql -U orcid -d orcid -c "\list" -h localhost
psql -U statistics -d statistics -c "\list" -h localhost
```

> NOTE: When testing this, if the console doesn't return anything, the databases weren't created suscesfully. You can try using the GUI 

## Setup Maven & Tomcat (OSX)

Extract next files into ~/Bin folder (Create if it does not exist)

* apache-maven-3.3.9-bin.tar.gz
* apache-tomcat-8.0.37.tar.gz

```
SJO-WS2555:~ jperez$ JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk
SJO-WS2555:~ jperez$ ~/Bin/apache-maven-3.3.9/bin/mvn -version
Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-10T10:41:47-06:00)
Maven home: /Users/jeffrey/Bin/apache-maven-3.3.9
Java version: 1.8.0_92, vendor: Oracle Corporation
Java home: /Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home/jre
Default locale: en_US, platform encoding: UTF-8OS name: "mac os x", version: "10.11.3", arch: "x86_64", family: "mac"
SJO-WS2555:~ jperez$
```

## Clone the git repository

* Make a git directory if one doesn't exist

```
mkdir ~/git
```

* Clone the repository

```
cd ~/git
git clone git@github.com:ORCID/ORCID-Source.git
```

## Clone the git ORCID-Fonts-Dot-Com repository

Due to licensing issues this is only available to ORCID.org employees.

* Clone the ORCID-Fonts-Dot-Com repository into the static directory

```
git clone git@github.com:ORCID/ORCID-Fonts-Dot-Com.git ~/git/ORCID-Source/orcid-web/src/main/webapp/static/ORCID-Fonts-Dot-Com
```

* Alternatively create a symbolic link inside static folder

```
cd ~/Sites    
git clone git@github.com:ORCID/ORCID-Source.git
git clone git@github.com:ORCID/ORCID-Fonts-Dot-Com.git
ln -s ORCID-Fonts-Dot-Com/ ORCID-Source/orcid-web/src/main/webapp/static/   
```

## Run Maven Task - First Time Only

* maven clean install

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

Intialize the database schema

```
cd ~/git/ORCID-Source/orcid-core

mvn exec:java -Dexec.mainClass=org.orcid.core.cli.InitDb

cd ..

sudo su - postgres

psql -d orcid -f orcid-persistence/src/main/resources/db/updates/json-setup.sql

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

  * Navigate to ~/git/ORCID-Source and select eclipse_formatter.xml

  * Click "Apply"

* Select Eclipse (or Spring Tool Suit) -> Preferences -> JavaScript -> Code style -> Formatter -> Import

  * Navigate to ~/git/ORCID-Source and select eclipse_javascript_formatter.xml

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
cd ~/git/ORCID-Source
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

