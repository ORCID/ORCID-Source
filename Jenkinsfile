node {

    properties([
        buildDiscarder(
            logRotator(
                artifactDaysToKeepStr: '',
                artifactNumToKeepStr: '',
                daysToKeepStr: '3',
                numToKeepStr: '3'
            )
        ),
        disableConcurrentBuilds(),
        [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], 
        pipelineTriggers([])
    ])

    git url: 'https://github.com/ORCID/ORCID-Source.git', branch: env.BRANCH_NAME

    stage('Fetch Code and Build') {
        try {
            do_maven("clean")
            parallel(
                model:       {do_maven("clean install test  -f orcid-model/pom.xml")},
                utils:       {do_maven("clean install test  -f orcid-utils/pom.xml")},
                test:        {do_maven("clean install test  -f orcid-test/pom.xml")},
                solrweb:     {do_maven("clean install test  -f orcid-solr-web/pom.xml")}
            )
        } catch(Exception err) {
            orcid_notify("Fetch Code and Build ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            deleteDir()
            throw err
        }
    }
    stage('Creating Persistence Package') {
        try {
            parallel(
                persistence: {do_maven("clean install test  -f orcid-persistence/pom.xml")},
                mq:          {do_maven("clean install test  -f orcid-activemq/pom.xml")}
            )
        } catch(Exception err) {
            orcid_notify("Creating Persistence Package ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            deleteDir()
            throw err
        }
    }
    stage('Building Core') {
        try {
            do_maven("clean install test  -f orcid-core/pom.xml")
        } catch(Exception err) {
            orcid_notify("Building Core ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            deleteDir()
            throw err
        }
    }
    stage('Packaging ORCID web') {
        try {
            parallel(
                web:        {do_maven("clean install test  -f orcid-web/pom.xml")},
                apiweb:     {do_maven("clean install test  -f orcid-api-web/pom.xml")},
                pubweb:     {do_maven("clean install test  -f orcid-pub-web/pom.xml")},
                scheduler:  {do_maven("clean install test  -f orcid-scheduler-web/pom.xml")},
                intapi:     {do_maven("clean install test  -f orcid-internal-api/pom.xml")},
                listener:   {do_maven("clean install test  -f orcid-message-listener/pom.xml")},
                apicommon:  {do_maven("clean install test  -f orcid-api-common/pom.xml")},
                indeptests: {do_maven("clean install test  -f orcid-integration-test/pom.xml")}
            )
            // Push to Artifact storage
            //archive '**/target/**/*.war'
        } catch(Exception err) {
            orcid_notify("Packaging ORCID web ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            deleteDir()
            throw err
        }
    }
    stage('Collect Tests Reports') {
        try {
            junit '**/target/surefire-reports/*.xml'
        } catch(Exception err) {
            orcid_notify("Collect Tests Reports ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            throw err
        } finally {
            deleteDir()
        }
        orcid_notify("Pipeline ${env.BRANCH_NAME}#$BUILD_NUMBER workflow completed [${JOB_URL}]", 'SUCCESS')
    }
}

def orcid_notify(message, level){
    def color = "#d00000"
    if(level == 'SUCCESS'){
        color = "#36a64f"
    }
    try{
        slackSend color: "$color", failOnError: true, message: "$message", teamDomain: 'orcid'
    } catch(Exception err) {
        echo err.toString()
    }
}

def do_maven(mvn_task){
    def MAVEN = tool 'ORCID_MAVEN'
    try{
        sh "export MAVEN_OPTS='-Xms2048m -Xmx2048m -XX:MaxPermSize=2048m -XX:+HeapDumpOnOutOfMemoryError'"
        sh "$MAVEN/bin/mvn $mvn_task"
    } catch(Exception err) {
        throw err
    }
}
