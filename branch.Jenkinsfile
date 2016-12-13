node {

    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '1', artifactNumToKeepStr: '1', daysToKeepStr: '1', numToKeepStr: '1')), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], parameters([string(defaultValue: 'master', description: 'build specific branch by name', name: 'branch_to_build')]), pipelineTriggers([])])
    
    git url: 'git@github.com:ORCID/ORCID-Source.git', credentials: 'orcid-machine', branch: "${branch_to_build}"
    
    stage('Fetch Code') {
        echo "triggered by modification on ${branch_to_build} ---------------------------------------------------------------------------"
    }
    
    stage('Build Dependencies') {
        echo "Lets build the core"
        try {
            do_maven("clean install -Dmaven.test.skip=true")
        } catch(Exception err) {
            orcid_notify("Compilation ${branch_to_build}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            throw err
        }
    }
    stage('Build & Test') {
        try {
            parallel activemq: {
                do_maven(" -f orcid-activemq/pom.xml test")
            },utils: {
                do_maven(" -f orcid-utils/pom.xml test")
            },core: {
                do_maven(" -f orcid-core/pom.xml test")
            },model: {
                do_maven(" -f orcid-model/pom.xml test")
            },persistence: {
                do_maven(" -f orcid-persistence/pom.xml test")
            },apicommon: {
                do_maven(" -f orcid-api-common/pom.xml test")
            },web: {
                do_maven(" -f orcid-web/pom.xml test")
            },pubweb: {
                do_maven(" -f orcid-pub-web/pom.xml test")
            },apiweb: {
                do_maven(" -f orcid-api-web/pom.xml test")
            },solr: {
                do_maven(" -f orcid-solr-web/pom.xml test")
            },scheduler: {
                do_maven(" -f orcid-scheduler-web/pom.xml test")
            },internalapi: {
                do_maven(" -f orcid-internal-api/pom.xml test")
            },messagelistener: {
                do_maven(" -f orcid-message-listener/pom.xml test")
            }
            junit '**/target/surefire-reports/*.xml'
        } catch(Exception err) {
            junit '**/target/surefire-reports/*.xml'            
            orcid_notify("Build ${branch_to_build}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            throw err
        }
    }
    stage('Notify Completed'){
        orcid_notify("Pipeline ${branch_to_build}#$BUILD_NUMBER workflow completed [${JOB_URL}]", 'SUCCESS')
        deleteDir()
    }
}

def do_maven(mvn_task){
    def MAVEN = tool 'ORCID_MAVEN'
    try{
        sh "export MAVEN_OPTS='-XX:MaxPermSize=512m -Xms32m -Xmx2048m -XX:+HeapDumpOnOutOfMemoryError'"
        sh "$MAVEN/bin/mvn $mvn_task"
    } catch(Exception err) {
        throw err
    }
}

def orcid_notify(message, level){
    def color = "#d00000"
    if(level == 'SUCCESS'){
        color = "#36a64f"
    }
    slackSend color: "$color", failOnError: true, message: "$message", teamDomain: 'orcid'
}
