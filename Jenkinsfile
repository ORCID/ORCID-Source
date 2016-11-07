node {

    git url: 'git@github.com:ORCID/ORCID-Source.git', credentials: 'orcid-machine'
    
    properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '3']]])
    
    stage('Fetch Code') {
        echo "triggered by modification on ${BRANCH_NAME} ---------------------------------------------------------------------------"
        // move maven install to next stages
        //do_maven("clean install -Dmaven.test.skip=true")
    }
    
    stage('Build Dependencies') {
        echo "Lets build the core"
        // TODO if any module is required before next builds
    }
    stage('Build & Test') {
        do_maven("clean install test")
    }
    stage('Save Tests Results') {
        //archive 'orcid-web/target/**/*.war'
        //archive 'orcid-pub-web/target/**/*.war'
        //archive 'orcid-api-web/target/**/*.war'
        //archive 'orcid-solr-web/target/**/*.war'
        //archive 'orcid-scheduler-web/target/**/*.war'
        //archive 'orcid-internal-api/target/**/*.war'
        //archive 'orcid-message-listener/target/**/*.war'
        junit '**/target/surefire-reports/*.xml'        
    }
    stage('DeployToTomcat') {
        echo "Ready to send to server"
        // cp *.war tomcat/webapps
        //or
        // mvn tomcat7:deploy 
    }
    stage('IntegrationTests') {
        echo "Running selenium blackbox test"
        // sh "export DISPLAY=:1.0"
        // sh "Xvfb :1 -screen 0 1024x758x16 -fbdir /tmp/xvfb_jenkins &"
        // stop Xvfb server
        // mvn test -DfailIfNoTests=false -Dtest=org.orcid.integration.blackbox.BlackBoxTestSuite
    }
    stage('Clean & Free resources'){
        // TODO check orphan process and MEM usage
        echo "All done."
        properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '3']]])
    }
    stage('Notify Completed'){
        orcid_notify("Build #$BUILD_NUMBER SUCCESS [${JOB_URL}]", 'SUCCESS')
    }
}

def do_maven(mvn_task){
    def MAVEN = tool 'ORCID_MAVEN'
    try{
        sh "$MAVEN/bin/mvn $mvn_task"
    } catch(Exception err){
        def err_msg = err.getMessage()
        orcid_notify("Build #$BUILD_NUMBER FAILED [${JOB_URL}]: $err_msg", 'ERROR')
    }
}

def orcid_notify(message, level){
    def color = "#d00000"
    if(level == 'SUCCESS'){
        color = "#36a64f"
    }
    slackSend color: "$color", failOnError: true, message: "$message", teamDomain: 'orcid'
}

