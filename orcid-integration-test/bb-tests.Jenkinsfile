node {

    properties([
        buildDiscarder(
            logRotator(artifactDaysToKeepStr: '1', artifactNumToKeepStr: '2', daysToKeepStr: '', numToKeepStr: '3')
        ),
        [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], 
        parameters([
            string(defaultValue: 'master', description: 'Branch to checkout and build', name: 'branch_to_build'),
            string(defaultValue: 'file:///opt/tomcat/orcid-ci2.properties', description: 'Persistence properties file for Settting up clients and users', name: 'setup_properties_file'),
            string(defaultValue: 'classpath:test-client.properties,classpath:test-web.properties', description: 'Persistence properties file', name: 'test_properties_file')
        ]),
        disableConcurrentBuilds(),
        pipelineTriggers([
            cron('0 H/4 * * *')
        ])
    ])
    
    git url: 'https://github.com/ORCID/ORCID-Source.git', branch: "${branch_to_build}"
    
    def tomcat_home = '/opt/tomcat/apache-tomcat-8.0.21'
    
    def modules_to_build = ['orcid-web','orcid-api-web','orcid-pub-web','orcid-internal-api','orcid-solr-web']
   
    def firefox_home = '/usr/bin/firefox'
    
    def gecko_home = '/usr/local/bin/geckodriver'
    
    stage('Build and Pack'){
        echo "Packaging..."
        do_maven("clean install -U -Dmaven.test.skip=true")
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
        sh "sleep 120"
    }
    
    stage('Setup Clients and Users'){
        // or try postgres@ci-3:~$ psql -f ~/orcid.setup.db
        def setup_users = false
        try {
            timeout(time:30,unit:'SECONDS'){
                setup_users = input message: 'Would you like to STOP setup clients and users ?', 
                                         ok: 'Skip',
                                 parameters: [booleanParam(defaultValue: false, description: '', name: 'Skip ?')]
            }
        } catch(err){
            echo err.toString()
        }
        if (setup_users) {
            echo "Skiping users setup."
        } else {
            echo "Installing required users for blackbox tests"
            do_maven("test -f orcid-integration-test/pom.xml -Dtest=org.orcid.integration.whitebox.SetUpClientsAndUsers -DfailIfNoTests=false -Dorg.orcid.config.file='$setup_properties_file'")
        }
    }
    
    stage('Execute Black-Box Tests'){
        try {
            do_maven("test -f orcid-integration-test/pom.xml -Dtest=org.orcid.integration.blackbox.BlackBoxTestSuite -Dorg.orcid.config.file=$test_properties_file -DfailIfNoTests=false -Dorg.orcid.persistence.db.url=jdbc:postgresql://localhost:5432/orcid -Dorg.orcid.persistence.db.dataSource=simpleDataSource -Dorg.orcid.persistence.statistics.db.dataSource=statisticsSimpleDataSource -Dwebdriver.firefox.bin=$firefox_home -Dwebdriver.gecko.driver=$gecko_home")
            orcid_notify("BlackBoxTestSuite ${branch_to_build}#$BUILD_NUMBER OK [${JOB_URL}]", 'SUCCESS')
        } catch(Exception err) {
            def err_msg = err.getMessage()
            echo "Tests problem: $err_msg"
            orcid_notify("BlackBoxTestSuite ${branch_to_build}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            throw err
        } finally {
            echo "Saving tests results"
            junit '**/target/surefire-reports/*.xml'
            build([
                job: 'ORCID-tomcat',
                parameters: [
                    string(name: 'tomcat_task', value: 'shutdown')
                ],
                wait: false
            ])
        }
    }
    stage('Clean up test data'){
        sh "psql -U jenkins -d orcid -f ~/clean_all_tables.sql"
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
def orcid_notify(message, level){
    def color = "#d00000"
    if(level == 'SUCCESS'){
        color = "#36a64f"
    }
    try{
        slackSend color: "$color", failOnError: true, message: "$message", teamDomain: 'orcid', channel: '#tech-ci-blackbox'
    } catch(Exception err) {
        echo err.toString()
    }
}
