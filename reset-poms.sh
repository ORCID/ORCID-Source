#!/usr/bin/env bash
clean(){

  git checkout orcid-activemq/pom.xml
  git checkout orcid-api-common/pom.xml
  git checkout orcid-api-web/pom.xml
  git checkout orcid-core/pom.xml
  git checkout orcid-internal-api/pom.xml
  git checkout orcid-message-listener/pom.xml
  git checkout orcid-persistence/pom.xml
  git checkout orcid-pub-web/pom.xml
  git checkout orcid-scheduler-web/pom.xml
  git checkout orcid-test/pom.xml
  git checkout orcid-utils/pom.xml
  git checkout orcid-web/pom.xml
  git checkout pom.xml

}

clean

