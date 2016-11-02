node {
    git url: 'git@github.com:ORCID/ORCID-Source.git', credentials: 'orcid-machine'
    def MAVEN = tool 'ORCID_MAVEN'
    stage('Fetch Code') {
        echo "triggered by modification on ${BRANCH_NAME}"
        sh "${MAVEN}/bin/mvn clean install -Dmaven.test.skip=true"
    }
    stage('Build Dependencies') {
        echo "Lets build the core"
        // TODO if any module is required before next builds
    }
    stage('Build & Test') {
        parallel activemq: {
            sh "$MAVEN/bin/mvn -f orcid-activemq/pom.xml test"
        },utils: {
            sh "$MAVEN/bin/mvn -f orcid-utils/pom.xml test"
        },core: {
            sh "$MAVEN/bin/mvn -f orcid-core/pom.xml test"
        },model: {
            sh "$MAVEN/bin/mvn -f orcid-model/pom.xml test"
        },persistence: {
            sh "$MAVEN/bin/mvn -f orcid-persistence/pom.xml test"
        },apicommon: {
            sh "$MAVEN/bin/mvn -f orcid-api-common/pom.xml test"
        },web: {
            sh "$MAVEN/bin/mvn -f orcid-web/pom.xml test"
        },pubweb: {
            sh "$MAVEN/bin/mvn -f orcid-pub-web/pom.xml test"
        },apiweb: {
            sh "$MAVEN/bin/mvn -f orcid-api-web/pom.xml test"
        },solr: {
            sh "$MAVEN/bin/mvn -f orcid-solr-web/pom.xml test"
        },scheduler: {
            sh "$MAVEN/bin/mvn -f orcid-scheduler-web/pom.xml test"
        },internalapi: {
            sh "$MAVEN/bin/mvn -f orcid-internal-api/pom.xml test"
        },messagelistener: {
            sh "$MAVEN/bin/mvn -f orcid-message-listener/pom.xml test"
        }
    }
    stage('Save Tests Results') {
        archive 'orcid-web/target/**/*.war'
        archive 'orcid-pub-web/target/**/*.war'
        archive 'orcid-api-web/target/**/*.war'
        archive 'orcid-solr-web/target/**/*.war'
        archive 'orcid-scheduler-web/target/**/*.war'
        archive 'orcid-internal-api/target/**/*.war'
        archive 'orcid-message-listener/target/**/*.war'
        junit '**/target/surefire-reports/*.xml'        
    }
    stage('DeployToTomcat') {
        echo "Ready to send to server"
        // cp *.war tomcat/webapps
        //or
        // mvn tomcat7:deploy 
    }
    stage('IntegrationTests') {
        echo "Running selenium blackbox test"
        // sh "export DISPLAY=:1.0"
        // sh "Xvfb :1 -screen 0 1024x758x16 -fbdir /tmp/xvfb_jenkins &"
        // stop Xvfb server
        // mvn test -DfailIfNoTests=false -Dtest=org.orcid.integration.blackbox.BlackBoxTestSuite
    }
    stage('Clean & Free resources'){
        // TODO check orphan process and MEM usage
        echo "All done."
    }
    stage('Notify Results'){
        slackSend channel: '#tech-ci-notifications', color: '#36a64f', failOnError: true, message: "Build #$JOB_NAME-$BUILD_NUMBER completed: $BUILD_URL/testReport ", teamDomain: 'orcid'
    }
}
