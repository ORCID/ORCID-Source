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

JVM_OPTS=" -Dorg.apache.jasper.runtime.BodyContentImpl.LIMIT_BUFFER=true  -Djdk.tls.ephemeralDHKeySize=2048  -Djava.protocol.handler.pkgs=org.apache.catalina.webresources  -Dorg.apache.catalina.security.SecurityListener.UMASK=0027  -Dfile.encoding=utf-8  -Djdk.module.illegalAccess=warn  -Djdk.attach.allowAttachSelf=true  --add-opens=java.base/java.lang=ALL-UNNAMED  --add-opens=java.base/java.io=ALL-UNNAMED  --add-opens=java.base/java.util=ALL-UNNAMED  --add-opens=java.base/java.util.concurrent=ALL-UNNAMED  --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED  --enable-native-access=ALL-UNNAMED "

JMX_OPTS=" -Dcom.sun.management.jmxremote  -Dcom.sun.management.jmxremote.port=8081  -Dcom.sun.management.jmxremote.rmi.port=8082 -Dcom.sun.management.jmxremote.authenticate=false  -Dcom.sun.management.jmxremote.ssl=false "

MEM_OPTS=" -Xmx2G "

JAVA_AGENT_OPTS="-javaagent:${CATALINA_HOME}/newrelic/newrelic.jar"
# === Jolokia JVM agent (conditional) ===
if [ "${ENABLE_JOLOKIA:-false}" = "true" ]; then
    if [ ! -f ${CATALINA_HOME}/jolokia/jolokia-agent-jvm.jar ]; then
        echo "Jolokia agent requested but ${CATALINA_HOME}/jolokia/jolokia-agent-jvm.jar was not found." >&2
        exit 1
    fi

    JOLOKIA_HOST=${JOLOKIA_HOST:-127.0.0.1}
    JOLOKIA_PORT=${JOLOKIA_PORT:-8778}
    JOLOKIA_CONFIG="host=${JOLOKIA_HOST},port=${JOLOKIA_PORT}"

    if [ -n "${JOLOKIA_OPTS:-discoveryEnabled=false}" ]; then
        JOLOKIA_CONFIG="${JOLOKIA_CONFIG},${JOLOKIA_OPTS:-discoveryEnabled=false}"
    fi

    JAVA_AGENT_OPTS="${JAVA_AGENT_OPTS} -javaagent:${CATALINA_HOME}/jolokia/jolokia-agent-jvm.jar=${JOLOKIA_CONFIG}"
    echo "Jolokia agent enabled on ${JOLOKIA_HOST}:${JOLOKIA_PORT}."
else
    echo "Jolokia agent disabled."
fi


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
