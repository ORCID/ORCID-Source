# ORCID-Work-in-Progress

This is the current active development repository. In the future we will be sharing the 
contents of this repository with the public. **Anything that shouldn't be shared with the 
public should be migrated/refactored to the 
[ORCID-internal](https://github.com/ORCID/ORCID-Internal)**. We will create a build that 
will composite the two github directories to create production builds.

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
    psql -U orcid -d orcid -c "\list"
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
See [LICENSE.md](https://github.com/ORCID/ORCID-Work-in-Progress/blob/master/LICENSE.md)

# Contributors
See [CREDITS.md](https://github.com/ORCID/ORCID-Work-in-Progress/blob/master/CREDITS.md)

# Projects
See [PROJECTS.md](https://github.com/ORCID/ORCID-Work-in-Progress/blob/master/PROJECTS.md)
