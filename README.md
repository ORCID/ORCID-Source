# ORCID-Source

Welcome to ORCID Source. Here are some resources to get you started.

* [What is ORCID?](http://orcid.org/about/what-is-orcid)

* [ORCID Source Technical Community](http://orcid.org/about/community/orcid-technical-community)

* [ORCID Source Wiki](https://github.com/ORCID/ORCID-Source/wiki)

* [ORCID's Public API](https://github.com/ORCID/ORCID-Source/tree/master/orcid-pub-web)

* [ORCID's Member API](https://github.com/ORCID/ORCID-Source/tree/master/orcid-api-web)


# Getting support

If you are experience problems using ORCID you can check our [help page](http://orcid.org/help). 

# General Application Stack

ORCID Source is set of web apps and libraries built in [Java](http://en.wikipedia.org/wiki/Java_(programming_language)) with [Spring Web MVC](http://www.springsource.org/) and persistence provided by [Postgres Database](http://www.postgresql.org/).  

Frontend Technologies (brief version):
On the client side we utilize [HTML](http://www.w3schools.com/html/default.asp), [AJAX](http://en.wikipedia.org/wiki/Ajax_(programming)), [JQuery](http://jquery.com/) and [AngularJS](http://angularjs.org/).  Server side we use [FreeMarker](http://freemarker.sourceforge.net/) for view rendering.

Backend Technologies (brief version):
[Spring Web MVC](http://www.springsource.org/) is our web framework. For security we use [Spring Security](http://www.springsource.org/). Our restful services are built with [Jersey](http://jersey.java.net/) and [JAXB](http://jaxb.java.net/). Finally we use [JPA](http://www.oracle.com/technetwork/java/javaee/tech/persistence-jsp-140049.html)/[Hibernate](http://www.hibernate.org/) to persist models to a [Postgres Database](http://www.postgresql.org/) database.  

The above is just a brief introduction. Best way to see everything used is to dig into the code, but baring that please browse our [PROJECTS](https://github.com/ORCID/ORCID-Source/blob/master/PROJECTS.md) page.


# Contributing
Pull requests are welcome; we are working on contributor guidelines. Until then please
reach out to the [OST] (https://github.com/organizations/ORCID/teams/208575) members directly.

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
    ```
    
* Exit postgres user prompt
    
    ```
    exit
    ```

* Verify user login and database exist

    ```
    psql -U orcid -d orcid -c "\list" -h localhost
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
<!--
1. Clear or backup the contents of your local maven repo:  

    ```
     cp -r ~/.m2/repository ~/.m2/repository_bak;
     rm ~/.m2/repository/*;
    ```

* Download custom 
  [Spring OAuth Jar](https://github.com/ORCID/ORCID-Work-in-Progress/raw/master/docs/spring-security-oauth2-1.0.0.SEMANTICO-BUILD-RC1.jar) This is not availble in the cental maven
  repo, and is (hopefully) destined to become a Spring 3rd party jar 
  (when this step wil then become redundant).

* Install a custom Semantico OAuth jar. 
     
    ```
    mvn install:install-file -Dfile=spring-security-oauth2-1.0.0.SEMANTICO-BUILD-RC1.jar -DgroupId=org.springframework.security.oauth -DartifactId=spring-security-oauth2 -Dversion=1.0.0.SEMANTICO-BUILD-RC1 -Dpackaging=jar
    ```

*  Once this is done, all artifacts can be built and installed to your local maven repo:
    
    ```
    mvn clean install
    ```
    
    Tip: use the same command for rebuilding.    
 -->

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
    -Dsolr.solr.home=/Users/rcpeters/git/ORCID-Source/orcid-solr-web/src/main/webapp/solr -Dorg.orcid.config.file=classpath:staging-persistence.properties
    ```

* Click Ok

* Right click on "VMWare vFabric tc Server"

* Select "Add and Remove" Add orcid-api-web, orcid-pub, orcid-scheduler-web, orcid-solr-web and orcid-web

* Right click on "VMWare vFabric tc Server"

* Select Debug

* Point your browser to http://localhost:8080/orcid-web/my-orcid

* You should see a login page.

* Finally help out by improving these instructions! 


<!--
## Eclipse Setup

Once the maven build has been run, 

1. Cd into root of git repository

    ```
    cd ORCID-Work-in-Progress
    ```

* Set up eclipse environment

    ```
    mvn eclipse:eclipse
    ``` 
    
* Configure your workspace to use your local maven repository

    ```
    mvn -Declipse.workspace="/Users/rcpeters/Dev/workspace" eclipse:configure-workspace
    ```

* Startup your eclipse

* Import existing maven project

    ```
    File -> Import -> Maven -> Existing Projects
    ```
    
* Synch resources from build inside the ide:

    ```
    Inside your Eclipse IDE, select all your projects on the file system and refresh (F5) them.
    ```
    
* Set persistence project to point to the Postgres instance 
    When running a maven build, with associated tests, we want to use an in-memory (HSQL) database, but when we run against 
    the deployed app we obviously want to keep data between restarts. To this end if you set your eclipse Maven project to run with a profile
    of 'staging' it will point to the Postgres instance.

    ```
	orcid-persistence -> properties -> maven and enter 'staging' inside the Active Maven Profiles text field
    ```

    However when you run a mvn clean install at the 'parent' level (i.e.) <ORCID-Work-in-Progress dir> you will run against an HSQl database.

*  Add a server config using Eclipse
	
    ```
    File -> New -> Other... Server -> Server     
    ```
    
    Then select a Tomcat Server - point the config to the actual Tomcat installation you made earlier.
    
    ```
    Apache -> Tomcat v 7.0 Server
    ```
   
* Add additional tomcat args

    ```
    Servers -> Double Click Tomcat Server -> In editor window click "Open launch configuration" -> Click Arguments  
    ```
    
    In VM args add 
    
    ```
    -Dorg.orcid.config.file=classpath:staging-persistence.properties 
    -Dsolr.solr.home=[ORCID-Work-in-Progress dir]\orcid-solr-web\src\main\webapp\solr 
    -Xmx256m -XX:MaxPermSize=256M	
    ```
    
    Complete args would looks like this:
    
    ```
    -Dcatalina.base="/Users/rcpeters/Dev/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0" 
    -Dcatalina.home="/usr/local/apache-tomcat-7.0.32" -Dwtp.deploy="/Users/rcpeters/Dev/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps" 
    -Djava.endorsed.dirs="/usr/local/apache-tomcat-7.0.32/endorsed" 
    -Dsolr.solr.home=/Users/rcpeters/Dev/ORCID-Work-in-Progress/orcid-solr-web/src/main/webapp/solr 
    -Xmx256m -XX:MaxPermSize=256M
    ```
    (you can pass the same args to the command line if you're so inclined, omitting the IDE args)

* Add your projects to the server

	```
	Select Sever -> Right Click and Tomcat -> Select Add/Remove
    ```
   
    Add all the orcid resources or at a bare minimum, you'll need orcid-web, orcid-scheduler-web and orcid-solr-web.
   
* Increase the tomcat timeout.
    
    ```
    Servers -> Double Click Tomcat Server -> In editor window click "Timeout"   
    ```    
    Increase the "Start" timeout to 180 seconds
    
* Start tomcat
    
    ```
    Servers -> Right Click on "Tomcat" -> Select "Debug"
    ```
* Test the webapp [http://localhost:8080/orcid-web/signin](http://localhost:8080/orcid-web/signin)

* Finally help out by improving these instructions! 

-->

## Testing

**Unit tests**: 

To run the full set of unit test, go to the command line and:


1. CD into the ORCID-Source folder (The folder where you downloaded the ORCID code)
* Run the command "mvn test"

**Integration tests**:


Integration tests are under ```[ORCID-Source]/orcid-core/src/test/java/org/orcid/core/integration```.

In order to run them, you should have the ORCID project up and running.

But, before running the integration tests, there are some things that might be configured: 

1. We should enable our server to run over SSL using the ORCID configuration, so, on the ```server.xml``` add the following configuration:

    * Inside the "service" tag, add the following connector tag to allow the application to run over https: 

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

## Troubleshooting:
  
**Problem**: 
   
I get problems starting up my server with class not found, properties not found exceptions
   

**Solution**:
  
What has most likely happened is that the compiled source (following a command-line build) is out of step with the IDE meta data, so the IDE 'dance' is required.
  
* Stop the server.
* Select and refresh all projects (F5).
* Within the Server view, select the tomcat instance, right click and select 'Clean'.
* Start the server.
   
This should be enough to get the IDE and compiled code in synch again
  

***

  
**Problem**: 
That didn't work
  
    
**Solution**:
You'll need to re-import the projects from the file system (don't worry this doesn't involve deleting anything)
  
* Stop the server.
* Remove all projects from the server
* In the Project Explorer/Navigator view, right-click any of the projects that are causing problems.
* Select Delete.. then OK, making sure that the 'Delete contents on disk' option remains unchecked.
* Go to File --> Import --> Maven--> Existing Maven Projects to pull the resources back in from the file system. 
* Add projects back to server.
* Start the server.    
  

NB if you deleted the persistence project (you shouldn't need to as it's not a WAR project), don't forget to add the 'staging' profile as described earlier.

# License
See [LICENSE.md](https://github.com/ORCID/ORCID-Source/blob/master/LICENSE.md)

# Contributors
See [CREDITS.md](https://github.com/ORCID/ORCID-Source/blob/master/CREDITS.md)

# Projects
See [PROJECTS.md](https://github.com/ORCID/ORCID-Source/blob/master/PROJECTS.md)
