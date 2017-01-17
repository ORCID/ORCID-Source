node {

    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '1', artifactNumToKeepStr: '1', daysToKeepStr: '1', numToKeepStr: '1')), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], parameters([string(defaultValue: 'master', description: 'build specific branch by name', name: 'branch_to_build'), string(defaultValue: 'orcid-core', description: '', name: 'module_to_build'), string(defaultValue: 'org.orcid.core.manager.IdentifierTypeManagerTest', description: '', name: 'test_name')]), pipelineTriggers([])])
    
    git url: 'https://github.com/ORCID/ORCID-Source.git', branch: "${branch_to_build}"
    
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
            do_maven("test -f ${module_to_build}/pom.xml -Dtest=${test_name}")
            
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
    //slackSend color: "$color", failOnError: true, message: "$message", teamDomain: 'orcid'
    try{
        slackSend color: "$color", failOnError: true, message: "$message", teamDomain: 'orcid'
    } catch(Exception err) {
        echo err.toString()
    }    
}
