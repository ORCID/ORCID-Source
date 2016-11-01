node {
    echo("> ${BRANCH_NAME} triggered")
    git url: 'git@github.com:ORCID/ORCID-Source.git', credentials: 'orcid-machine (orcid-machine-personal-token)'
    stage('FetchFreshCode') {
        def MAVEN = tool 'ORCID_MAVEN'
        sh "${MAVEN}/bin/mvn clean install -Dmaven.test.skip=true"
    }
    stage('BuildDependencies') {
        echo "Lets build the core"
        // build only modified files
    }    
    stage('ExecuteTests') {
        echo "Running unit test"
        def MAVEN = tool 'ORCID_MAVEN'
        sh "${MAVEN}/bin/mvn clean test"
    }
    stage('DeployToTomcat') {
        echo "Ready to send to server"
    }
    stage('IntegrationTests') {
        echo "Running selenium blackbox test"
    }    
}
