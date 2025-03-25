# dependencies docker build

# match version from .tool-versions
FROM maven:3.6.3-jdk-11 AS maven

ARG tag_numeric

WORKDIR /build

# copy only poms for max cachability of just dependency downloads
COPY pom.xml .
COPY orcid-core/pom.xml orcid-core/pom.xml
COPY orcid-persistence/pom.xml orcid-persistence/pom.xml
COPY orcid-utils/pom.xml orcid-utils/pom.xml
COPY orcid-test/pom.xml orcid-test/pom.xml
COPY orcid-api-common/pom.xml orcid-api-common/pom.xml
COPY orcid-scheduler-web/pom.xml orcid-scheduler-web/pom.xml
COPY orcid-api-web/pom.xml orcid-api-web/pom.xml
COPY orcid-message-listener/pom.xml orcid-message-listener/pom.xml
COPY orcid-core/pom.xml orcid-core/pom.xml
COPY orcid-web/pom.xml orcid-web/pom.xml
COPY orcid-internal-api/pom.xml orcid-internal-api/pom.xml
COPY orcid-pub-web/pom.xml orcid-pub-web/pom.xml
COPY orcid-activemq/pom.xml orcid-activemq/pom.xml

# FIXME: these dont seem required?
#COPY orcid-web-frontend/pom.xml orcid-web-frontend/pom.xml
#COPY orcid-activities-indexer/pom.xml orcid-activities-indexer/pom.xml
#COPY orcid-nodejs/pom.xml orcid-nodejs/pom.xml

# download maven dependencies and ignore that some components will fail
RUN mvn -T 1C --batch-mode dependency:resolve --fail-never -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

# install orcid-parent into our local maven repo because the builds depend a version tagged release
RUN mvn -T 1C --batch-mode --non-recursive clean install -DskipTests \
-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

# install orcid-utils into our local maven repo because the builds depend a version tagged release
COPY orcid-utils/src orcid-utils/src
RUN mvn -T 1C --batch-mode --projects orcid-utils clean install -DskipTests \
-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

# install orcid-test into our local maven repo because orcid-persistence depends on it
COPY orcid-test/src orcid-test/src
RUN mvn -T 1C --batch-mode --projects orcid-test clean install -DskipTests \
-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

# install orcid-persistence into our local maven repo because orcid-core depends on it
COPY orcid-persistence/src orcid-persistence/src
RUN mvn -T 1C --batch-mode --projects orcid-persistence clean install -DskipTests \
-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

# install orcid-core into our local maven repo because the builds depend a version tagged release
COPY orcid-core/src orcid-core/src
RUN mvn -T 1C --batch-mode --projects orcid-core clean install -DskipTests \
-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

# install orcid-api-common into our local maven repo because orcid-web deploy depends a version tagged release
COPY orcid-api-common/src orcid-api-common/src
RUN mvn -T 1C --batch-mode --projects orcid-api-common clean install -DskipTests \
-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

##################################################################################
COPY orcid-web/src orcid-web/src
RUN mvn -T 1C --batch-mode -DgenerateBackupPoms=false \
--projects orcid-web -am package -DskipTests \
-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

# For Java 11 and Tomcat 9
FROM tomcat:9.0.93-jdk11-temurin-jammy

# Focal has no j2cli support
# FROM tomcat:9.0.91-jdk11-temurin-focal

# copy jar file from build
COPY --from=maven /build/*/target/*.war /usr/local/tomcat/webapps/orcid-web.war

RUN mkdir -p /usr/local/tomcat/newrelic
COPY newrelic.yml /usr/local/tomcat/newrelic/newrelic.yml
RUN curl -L -s https://download.newrelic.com/newrelic/java-agent/newrelic-agent/8.13.0/newrelic-agent-8.13.0.jar -o /usr/local/tomcat/newrelic/newrelic.jar

RUN apt-get update
RUN apt-get install -y j2cli

# add orcid ca to allow Java application to trust other containers
ADD certs/cacerts /opt/java/openjdk/lib/security/cacerts

# add orcid ca to system to allow curl healthchecks to work
ADD certs/ca-certificates.crt /etc/ssl/certs/ca-certificates.crt


COPY entrypoint.sh .
RUN chmod +x ./entrypoint.sh

COPY orcid-web/*.j2 .

COPY orcid-web/log4j2.xml .

ENTRYPOINT ./entrypoint.sh

