node {

    properties([
        buildDiscarder(
            logRotator(artifactDaysToKeepStr: '1', artifactNumToKeepStr: '2', daysToKeepStr: '', numToKeepStr: '3')
        ),
        parameters([ 
            string(name: 'tomcat_task'      , description: 'either startup|shutdown or any *.sh executable on bin dir', defaultValue: 'startup'),
            string(name: 'tomcat_home'      , description: 'tomcat full path', defaultValue: '/opt/tomcat/apache-tomcat-8.0.21')
        ])
    ])
    
    def modules_to_build = ['orcid-web','orcid-api-web','orcid-pub-web','orcid-internal-api','orcid-scheduler-web','orcid-solr-web']

    stage('Tomcat Service') {
        echo "Tomcat 8...> $tomcat_task"
        sh "sh $tomcat_home/bin/${tomcat_task}.sh"
    }
    
    stage('XVirtual Frame Buffers'){
        if("$tomcat_task" == 'startup') {
            sh "Xvfb :1 -screen 0 1024x758x16 -fbdir /tmp/xvfb_jenkins & > /dev/null 2>&1 && echo \$! > /tmp/xvfb_jenkins.pid"
            sh "cat /tmp/xvfb_jenkins.pid"
        } else {
            echo "Stoping tomcat and xvfb..."
            sh "sh $tomcat_home/bin/shutdown.sh"
            sh "XVFB_PID=\$(cat /tmp/xvfb_jenkins.pid) ; kill \$XVFB_PID"
            //clean_tomcat($tomcat_home,$modules_to_build)
        }
    }
}

def clean_tomcat(tomcat_home,modules_to_build){
    sh "rm -rf $tomcat_home/webapps/*.war"        
    for (int i = 0; i < modules_to_build.size(); i++) {
        def module_name = modules_to_build.get(i)
        sh "rm -rf $tomcat_home/webapps/${module_name}"
    }   
}