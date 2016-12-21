node {

    git url: 'https://github.com/ORCID/ORCID-Source.git', branch: "${env.BRANCH_NAME}"
    
    properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '3']]])
    
    stage('Fetch Code and Build') {
        echo "triggered by modification on ${env.BRANCH_NAME} ---------------------------------------------------------------------------"
        echo "Lets build the core"
        try {
            do_maven("clean install -Dmaven.test.skip=true")
        } catch(Exception err) {
            orcid_notify("Compilation ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            throw err
        }
    }
    stage('Execute Tests') {
        try {
            do_maven("test")
            junit '**/target/surefire-reports/*.xml'
        } catch(Exception err) {
            junit '**/target/surefire-reports/*.xml'            
            orcid_notify("Build ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            throw err
        }
        orcid_notify("Pipeline ${env.BRANCH_NAME}#$BUILD_NUMBER workflow completed [${JOB_URL}]", 'SUCCESS')
        deleteDir()        
    }
}

def do_maven(mvn_task){
    def MAVEN = tool 'ORCID_MAVEN'
    try{
        sh "export MAVEN_OPTS='-XX:MaxPermSize=2048m -Xms128m -Xmx4096m -XX:+HeapDumpOnOutOfMemoryError'"
        sh "$MAVEN/bin/mvn -Dorg.orcid.config.file=classpath:staging-persistence.properties -Dorg.orcid.persistence.db.dataSource=simpleDataSource -Dorg.orcid.persistence.statistics.db.dataSource=statisticsSimpleDataSource -Dcom.mailgun.testmode=yes $mvn_task"
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
