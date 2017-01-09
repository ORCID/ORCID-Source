node {

    properties([
        buildDiscarder(
            logRotator(artifactDaysToKeepStr: '1', artifactNumToKeepStr: '2', daysToKeepStr: '', numToKeepStr: '3')
        ),
        [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], 
        parameters([
            string(defaultValue: 'master', description: '', name: 'branch_to_build')
        ]), 
        pipelineTriggers([])
    ])
    
    git url: 'https://github.com/ORCID/ORCID-Source.git', branch: "${branch_to_build}"
    
    def tomcat_home = '/opt/tomcat/apache-tomcat-8.0.21'
    
    def modules_to_build = ['orcid-web','orcid-api-web','orcid-pub-web','orcid-internal-api','orcid-scheduler-web','orcid-solr-web']
   
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
        sh "sleep 120"
    }
    
    stage('Execute Black-Box Tests'){
        try {
            do_maven("test -f orcid-integration-test/pom.xml -Dtest=org.orcid.integration.blackbox.BlackBoxTestSuite -Dorg.orcid.config.file='classpath:test-client.properties,classpath:test-web.properties' -DfailIfNoTests=false -Dorg.orcid.persistence.db.url=jdbc:postgresql://localhost:5432/orcid -Dorg.orcid.persistence.db.dataSource=simpleDataSource -Dorg.orcid.persistence.statistics.db.dataSource=statisticsSimpleDataSource -Dwebdriver.firefox.bin=$firefox_home")
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
                wait: true
            ])            
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
