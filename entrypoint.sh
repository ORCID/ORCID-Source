#!/usr/bin/env bash

cd /usr/local/tomcat

# template any properties files
for j2_file in *.j2;do
  j2 $j2_file -o $(basename $j2_file .j2) --undefined
done

CATALINA_HOME=/usr/local/tomcat
JAVA_ENDORSED_DIRS=${CATALINA_HOME}/endorced
CATALINA_BASE=/usr/local/tomcat
CATALINA_TMPDIR=/usr/local/tomcat/temp/
CLASSPATH=/usr/local/tomcat/bin/bootstrap.jar:/usr/local/tomcat/bin/tomcat-juli.jar

CATALINA_OPTS=" -Dorg.orcid.config.file=file://${CATALINA_HOME}/orcid.properties  -Dlog4j.configurationFile=file://${CATALINA_HOME}/log4j2.xml  -Dlog4j2.formatMsgNoLookups=True "
GC_OPTS=" -XX:+UseG1GC  -XX:+UseStringDeduplication  -XX:+UseAdaptiveSizePolicy  -Xlog:gc*,safepoint=info:file=${CATALINA_HOME}/logs/gc.log:time,uptime:filecount=10,filesize=2M "
NETWORK_OPTS=" -Dsun.net.inetaddr.ttl=60  -Djava.net.preferIPv4Stack=true  -Djdk.tls.ephemeralDHKeySize=2048 "

JVM_OPTS=" -Dorg.apache.jasper.runtime.BodyContentImpl.LIMIT_BUFFER=true  -Djdk.tls.ephemeralDHKeySize=2048  -Djava.protocol.handler.pkgs=org.apache.catalina.webresources  -Dorg.apache.catalina.security.SecurityListener.UMASK=0027  -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true  -Dfile.encoding=utf-8  -Djdk.module.illegalAccess=warn  -Djdk.attach.allowAttachSelf=true "

JMX_OPTS=" -Dcom.sun.management.jmxremote  -Dcom.sun.management.jmxremote.port=8081  -Dcom.sun.management.jmxremote.rmi.port=8082 -Dcom.sun.management.jmxremote.authenticate=false  -Dcom.sun.management.jmxremote.ssl=false "

MEM_OPTS=" -Xmx2G "

JAVA_AGENT_OPTS="-javaagent:${CATALINA_HOME}/newrelic/newrelic.jar"


/usr/bin/env java \
$GC_OPTS $JAVA_AGENT_OPTS $NETWORK_OPTS $JVM_OPTS $JMX_OPTS $MEM_OPTS  $CATALINA_OPTS  \
-classpath ${CLASSPATH} \
-Dcatalina.base=${CATALINA_BASE} \
-Dcatalina.home=${CATALINA_HOME} \
-Djava.io.tmpdir=${CATALINA_TMPDIR} \
-Djava.util.logging.config.file=${CATALINA_BASE}/conf/logging.properties \
-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager \
org.apache.catalina.startup.Bootstrap \
start


# /usr/local/tomcat/bin/catalina.sh run
