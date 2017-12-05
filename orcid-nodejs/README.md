### How to use nodejs maven plugin

This plugin is provided by [eirslett](https://github.com/eirslett/frontend-maven-plugin)

#### Ready to use from ORCID-Source

Simply download code via `git clone` and start tomcat with your orcid-web modules.

#### Software Requirements

* Java 8+
* Tomcat 8+
* Maven 3.3+
* git

#### Deploy angular_orcid_generated.js file

Next configuration assumes you are working inside a specific folder workspace called *~/tmp-tomcat-orcid-web*, you can

Open a terminal at your workspace and run
    
    mkdir ~/tmp-tomcat-orcid-web
    cd ~/tmp-tomcat-orcid-web
    git clone https://github.com/ORCID/ORCID-Source.git
    cd orcid-nodejs
    mvn -P tmp-tomcat-orcid-web clean install

review **ORCID-Source/orcid-nodejs/pom.xml** for available profiles/configurations

e.g. nodejs workspace

* C:/Documents and Settings/%USER%/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/orcid-web/static/javascript/ng1Orcid
* ~/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/orcid-web/static/javascript/ng1Orcid
* /home/orcid_tomcat/apache-tomcat-8.0.21/webapps/orcid-web/static/javascript/ng1Orcid
* /home/developer1/git/ORCID-Source/orcid-web/src/main/webapp/static/javascript/ng1Orcid

#### Test changes are updated live

Edit file at _orcid-web/src/main/webapp/static/javascript/ng1Orcid/angularOrcidOriginal.js_
you could simply append next line at the top of file.

    alert('It works')
    
then, browse to [localhost](https://localhost:8443/orcid-web/) and confirm _It works_ message comes up.

