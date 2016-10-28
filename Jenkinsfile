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
    }
    stage('Deploy') {
        echo "Ready to send to server"
    }
}
