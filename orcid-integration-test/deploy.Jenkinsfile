node {
    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '1', artifactNumToKeepStr: '2', daysToKeepStr: '', numToKeepStr: '3')), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], parameters([string(defaultValue: 'master', description: '', name: 'branch_to_build')]), pipelineTriggers([])])
    git url: 'git@github.com:ORCID/ORCID-Source.git', credentials: 'orcid-machine', branch: "${branch_to_build}"
    def tomcat_home = '/opt/tomcat/apache-tomcat-8.0.21'
    def modules_to_build = ['orcid-web','orcid-api-web','orcid-pub-web','orcid-internal-api','orcid-scheduler-web','orcid-solr-web']
    stage('Build and Pack'){
        echo "Packaging..."
        do_maven("clean install -Dmaven.test.skip=true")
    }
    stage('Copy Apps'){
        echo "Installing new *.war files..."
        for (int i = 0; i < modules_to_build.size(); i++) {    
            def module_name = modules_to_build.get(i)
            sh "cp ${module_name}/target/${module_name}.war ${tomcat_home}/webapps/"
        }
    }
    stage('Start Tomcat') {
        echo "Starting Tomcat 8..."
        sh "sh $tomcat_home/bin/startup.sh"
        sh "sleep 80"
    }
    stage('Start XVirtual Frame Buffers'){
        sh "Xvfb :1 -screen 0 1024x758x16 -fbdir /tmp/xvfb_jenkins & > /dev/null 2>&1 && echo \$! > /tmp/xvfb_jenkins.pid"
        sh "cat /tmp/xvfb_jenkins.pid"
    }
    stage('Execute Black-Box Tests'){
        try {
            do_maven("test -f orcid-integration-test/pom.xml -Dtest=org.orcid.integration.blackbox.BlackBoxTestSuite -Dorg.orcid.config.file='classpath:test-client.properties,classpath:test-web.properties' -DfailIfNoTests=false -Dorg.orcid.persistence.db.url=jdbc:postgresql://localhost:5432/orcid -Dorg.orcid.persistence.db.dataSource=simpleDataSource -Dorg.orcid.persistence.statistics.db.dataSource=statisticsSimpleDataSource -Dwebdriver.firefox.bin=/opt/firefox/firefox-bin")
        } catch(Exception err) {
            def err_msg = err.getMessage()
            echo "Tests problem: $err_msg"
            throw err
        } finally {
            echo "Stoping tomcat and xvfb..."
            sh "sh $tomcat_home/bin/shutdown.sh"
            sh "XVFB_PID=\$(cat /tmp/xvfb_jenkins.pid) ; kill \$XVFB_PID"
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
