### How to use nodejs maven plugin

This plugin is provided by [eirslett](https://github.com/eirslett/frontend-maven-plugin)

#### Ready to use from ORCID-Source

Simply download code via git clone then start tomcat with your orcid-web modules.

#### Start listening for changes on your orcid.js file

Open a terminal at your root project and run

    mvn -f orcid-nodejs/pom.xml \
    -Dnodejs.workingDirectory='${project.parent.basedir}/orcid-web/src/main/webapp/static/javascript/ng1Orcid' \
    -DwebpackConfig=tmp-tomcat-orcid-web \
    clean install

where **nodejs.workingDirectory** must be replaced with full path to folder containing _package.json_ and **webpackConfig** is a prefix of PREFIX.webpack.config.js file

ensure webpack config has proper values for output path and filename it should be pointing to the tomcat installation + folder containing the _angular_orcid_generated.js_

e.g.

* C:/Documents and Settings/%USER%/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/orcid-web/static/javascript
* ~/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/orcid-web/static/javascript
* /opt/apache-tomcat-8.0.21/webapps/orcid-web/static/javascript/ng1Orcid/

so _localhost.webpack.config.js_

    ...
    output: {
        path: "/Users/jperez/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/orcid-web/static/javascript",
        filename: "angular_orcid_generated.js"
    },
    ...

#### Test changes are updated live

edit file at _orcid-web/src/main/webapp/static/javascript/ng1Orcid/angularOrcidOriginal.js_
you could simply append next line at the top of file.

    alert('It works')
    
then, browse to [localhost](https://localhost:8443/orcid-web/) and confirm _It works_ message comes up.

