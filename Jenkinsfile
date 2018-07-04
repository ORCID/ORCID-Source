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
    
    stage('Build and Test') {
        try {
            build([
                job: 'orcid-source-build-step1',
                parameters: [
                    string(
                        name: BRANCH_NAME, 
                        value: env.BRANCH_NAME
                    )
                ],
                wait: true
            ])
        } catch(Exception err) {
            orcid_notify("Build ${env.BRANCH_NAME}#$BUILD_NUMBER FAILED [${JOB_URL}]", 'ERROR')
            throw err
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
