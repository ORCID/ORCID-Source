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
    def EHCACHE_LOCATION="${WORKSPACE}/tmp/ehcache_${env.BRANCH_NAME}_$BUILD_NUMBER"

    stage('MODEL AND TEST') {
        try {
            sh "mkdir -p $EHCACHE_LOCATION"
            do_maven("clean")
            do_maven("clean install test -D branchVersion=${BUILD_NUMBER}-${env.BRANCH_NAME} -f orcid-test/pom.xml")
            do_maven("clean install test -D branchVersion=${BUILD_NUMBER}-${env.BRANCH_NAME} -f orcid-model/pom.xml")
        } catch(Exception err) {
            orcid_notify("Fetch Code and Build ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]: $err", 'ERROR')
            report_and_clean()
            throw err
        }
    }
    stage('ALL FROM PARENT') {
        try {
            do_maven("clean compile test -D branchVersion=${BUILD_NUMBER}-${env.BRANCH_NAME}")
        } catch(Exception err) {
            orcid_notify("Packaging ORCID web ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]: $err", 'ERROR')
            report_and_clean()
            throw err
        }
        orcid_notify("Pipeline ${env.BRANCH_NAME}#$BUILD_NUMBER workflow completed [${JOB_URL}]", 'SUCCESS')
    }
}

def report_and_clean(){
    junit '**/target/surefire-reports/*.xml'
    deleteDir()
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
    def EHCACHE_LOCATION="${WORKSPACE}/tmp/ehcache_${env.BRANCH_NAME}_$BUILD_NUMBER"
    try{
        sh "export MAVEN_OPTS='-Xms2048m -Xmx2048m -XX:+HeapDumpOnOutOfMemoryError'"
        sh "$MAVEN/bin/mvn -Djava.io.tmpdir=$EHCACHE_LOCATION $mvn_task"
        // Push to Artifact storage
        //archive '**/target/**/*.war'
    } catch(Exception err) {
        throw err
    }
}
