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
            do_maven("clean -Dmaven.test.skip=true")
            parallel(
                model:       {do_maven("install -f orcid-model/pom.xml -Dmaven.test.skip=true")},
                utils:       {do_maven("install -f orcid-utils/pom.xml -Dmaven.test.skip=true")},
                test:        {do_maven("install -f orcid-test/pom.xml -Dmaven.test.skip=true")},
                solrweb:     {do_maven("install -f orcid-solr-web/pom.xml -Dmaven.test.skip=true")}
            )
        } catch(Exception err) {
            orcid_notify("Fetch Code and Build ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            throw err
        }
    }
    stage('Creating Persistence Package') {
        try {
            do_maven("clean -Dmaven.test.skip=true")
            parallel(
                persistence: {do_maven("install -f orcid-persistence/pom.xml -Dmaven.test.skip=true")},
                mq:          {do_maven("install -f orcid-activemq/pom.xml -Dmaven.test.skip=true")}
            )
        } catch(Exception err) {
            orcid_notify("Creating Persistence Package ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            throw err
        }
    }
    stage('Building Core') {
        try {
            do_maven("install -f orcid-core/pom.xml -Dmaven.test.skip=true")
        } catch(Exception err) {
            orcid_notify("Building Core ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            throw err
        }
    }
    stage('Packaging ORCID web') {
        try {
            parallel(
                web:        {do_maven("install -f orcid-web/pom.xml -Dmaven.test.skip=true")},
                apiweb:     {do_maven("install -f orcid-api-web/pom.xml -Dmaven.test.skip=true")},
                pubweb:     {do_maven("install -f orcid-api-web/pom.xml -Dmaven.test.skip=true")},
                scheduler:  {do_maven("install -f orcid-scheduler-web/pom.xml -Dmaven.test.skip=true")},
                intapi:     {do_maven("install -f orcid-internal-api/pom.xml -Dmaven.test.skip=true")},
                listener:   {do_maven("install -f orcid-message-listener/pom.xml -Dmaven.test.skip=true")},
                apicommon:  {do_maven("install -f orcid-api-common/pom.xml -Dmaven.test.skip=true")},
                indeptests: {do_maven("install -f orcid-integration-test/pom.xml -Dmaven.test.skip=true")}
            )
            // Push to Artifact storage
            //archive '**/target/**/*.war'
        } catch(Exception err) {
            orcid_notify("Packaging ORCID web ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            throw err
        }
    }
    stage('Run Unit Tests') {
        try {
            parallel(
                model:       {do_maven("test -f orcid-model/pom.xml")},
                utils:       {do_maven("test -f orcid-utils/pom.xml")},
                test:        {do_maven("test -f orcid-test/pom.xml")},
                persistence: {do_maven("test -f orcid-persistence/pom.xml")},
                core:        {do_maven("test -f orcid-core/pom.xml")},
                mq:          {do_maven("test -f orcid-activemq/pom.xml")},
                solrweb:     {do_maven("test -f orcid-solr-web/pom.xml")},
                web:         {do_maven("test -f orcid-web/pom.xml")},
                apiweb:      {do_maven("test -f orcid-api-web/pom.xml")},
                pubweb:      {do_maven("test -f orcid-api-web/pom.xml")},
                scheduler:   {do_maven("test -f orcid-scheduler-web/pom.xml")},
                intapi:      {do_maven("test -f orcid-internal-api/pom.xml")},
                listener:    {do_maven("test -f orcid-message-listener/pom.xml")},
                apicommon:   {do_maven("test -f orcid-api-common/pom.xml")},
                indeptests:  {do_maven("test -f orcid-integration-test/pom.xml")}
            )
        } catch(Exception err) {
            orcid_notify("Run Unit Tests ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            throw err
        } finally {
            junit '**/target/surefire-reports/*.xml'
        }
        orcid_notify("Pipeline ${env.BRANCH_NAME}#$BUILD_NUMBER workflow completed [${JOB_URL}]", 'SUCCESS')
        deleteDir()
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
