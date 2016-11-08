node {

    git url: 'git@github.com:ORCID/ORCID-Source.git', credentials: 'orcid-machine'
    
    properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '3']]])
    
    stage('Fetch Code') {
        echo "triggered by modification on ${BRANCH_NAME} ---------------------------------------------------------------------------"
    }
    
    stage('Build Dependencies') {
        echo "Lets build the core"
        // TODO if any module is required before next builds
    }
    stage('Build & Test') {
        do_maven("clean install test")
    }
    stage('Save Tests Results') {
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
        // TODO implement virtual screens
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
        orcid_notify("Pipeline #$BUILD_NUMBER completed [${JOB_URL}]", 'SUCCESS')
    }
}

def do_maven(mvn_task){
    def MAVEN = tool 'ORCID_MAVEN'
    try{
        sh "$MAVEN/bin/mvn $mvn_task"
    } catch(Exception err){
        def err_msg = err.getCause()
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
