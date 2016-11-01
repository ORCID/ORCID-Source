node {
    echo("> ${BRANCH_NAME} triggered")
    git url: 'git@github.com:ORCID/ORCID-Source.git', credentials: 'orcid-machine (orcid-machine-personal-token)'
    // properties([pipelineTriggers([[$class: 'GitHubPushTrigger']])])
    stage('FetchFreshCode') {
        def MAVEN = tool 'ORCID_MAVEN'
        sh "${MAVEN}/bin/mvn clean install -Dmaven.test.skip=true"
    }
    stage('BuildDependencies') {
        echo "Lets build the core"
        // build only modified files
    }    
    stage('ExecuteTests') {
        echo "Lets build the core"
        def MAVEN = tool 'ORCID_MAVEN'
        sh "${MAVEN}/bin/mvn clean test"
    }
    stage('DeployToTomcat') {
        echo "Ready to send to server"
    }
    stage('IntegrationTests') {
        echo "Ready to send to server"
    }    
}
