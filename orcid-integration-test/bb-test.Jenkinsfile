node {

    properties([
        buildDiscarder(
            logRotator(artifactDaysToKeepStr: '1', artifactNumToKeepStr: '2', daysToKeepStr: '', numToKeepStr: '3')), 
            [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], 
            parameters([
                string(name: 'branch_to_build', defaultValue: 'master'                                       , description: 'Run maven on specific branch'),
                string(name: 'bb_test_name'   , defaultValue: 'org.orcid.integration.blackbox.web.SigninTest', description: ''),
                string(name: 'tomcat_home'    , defaultValue: '/opt/tomcat/apache-tomcat-8.0.21'             , description: 'tomcat full path')
            ]),
            pipelineTriggers([])
    ])
    
    git url: 'https://github.com/ORCID/ORCID-Source.git', branch: "${branch_to_build}"
    
    def modules_to_build = ['orcid-web'] //,'orcid-api-web','orcid-pub-web','orcid-internal-api','orcid-scheduler-web','orcid-solr-web'
    
    def firefox_home = '/usr/bin/firefox'
    
    stage('Build and Pack'){
        echo "Packaging..."
        do_maven("clean install -Dmaven.test.skip=true")
    }
    
    stage('Copy Apps and Start Tomcat') {
        echo "Installing new *.war files..."
        sh "rm -rf $tomcat_home/webapps/*.war"
        for (int i = 0; i < modules_to_build.size(); i++) {
            def module_name = modules_to_build.get(i)
            sh "rm -rf $tomcat_home/webapps/${module_name}"
            sh "cp ${module_name}/target/${module_name}.war ${tomcat_home}/webapps/"
        }
        build([
            job: 'ORCID-tomcat', 
            parameters: [
                string(name: 'tomcat_task', value: 'startup')
            ], 
            wait: true
        ])
    }
    
    stage('Setup Client and Users'){
        // try postgres@ci-3:~$ psql -d orcid -f /tmp/orcid_clean.sql 
        def setup_users = false
        try {
            timeout(time:30,unit:'SECONDS'){
                setup_users = input message: 'Would you like to setup clients and users ?', 
                                         ok: 'Continue',
                                 parameters: [booleanParam(defaultValue: true, description: '', name: 'install')]
            }
        } catch(err){
            echo err.toString()
        }
        if (setup_users) {                
            echo "Installing required users for blackbox tests"
            do_maven("test -f orcid-integration-test/pom.xml -Dtest=org.orcid.integration.whitebox.SetUpClientsAndUsers -DfailIfNoTests=false -Dorg.orcid.config.file='file:///opt/tomcat/orcid-ci2.properties'")                
        } else {
            echo "Skiping users setup."
        }
    }
    
    stage('Execute Black-Box Tests'){
        try {
            do_maven("test -f orcid-integration-test/pom.xml -Dtest=${bb_test_name} -Dorg.orcid.config.file='classpath:test-client.properties,classpath:test-web.properties' -DfailIfNoTests=false -Dorg.orcid.persistence.db.url=jdbc:postgresql://localhost:5432/orcid -Dorg.orcid.persistence.db.dataSource=simpleDataSource -Dorg.orcid.persistence.statistics.db.dataSource=statisticsSimpleDataSource -Dwebdriver.firefox.bin=$firefox_home")
        } catch(Exception err) {
            def err_msg = err.getMessage()
            echo "Tests problem: $err_msg"
            throw err
        } finally {
            echo "Stoping tomcat and xvfb..."
            //sh "sh $tomcat_home/bin/shutdown.sh"
            //sh "XVFB_PID=\$(cat /tmp/xvfb_jenkins.pid) ; kill \$XVFB_PID"
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
def do_maven(mvn_task){
    def MAVEN = tool 'ORCID_MAVEN'
    try{
        sh "export MAVEN_OPTS=' -Xms32m -Xmx2048m' ; export DISPLAY=:1.0 ; $MAVEN/bin/mvn $mvn_task"
    } catch(Exception err) {
        throw err
    }
}
// https://github.com/ORCID/ORCID-Source.git
// orcid-integration-test/bb-test.Jenkinsfile
