#!/usr/bin/env bash
# exit on errors
set -o errexit -o errtrace -o nounset -o functrace -o pipefail
shopt -s inherit_errexit 2>/dev/null || true
trap 'sk-catch --exit_code $? --line $LINENO --linecallfunc "$BASH_COMMAND" --funcstack $(printf "::%s" ${FUNCNAME[@]}) -o stdout ' ERR

# import shellkit functions
source shellkit_bootstrap.sh

# defaults
current_dir=`pwd`
checkout_name=$(basename `pwd`)
NAME="$(basename "${0}")"
build_envs="prod sandbox qa int"
tag="v2.0.1"
current_dir=`pwd`

#
# functions
#

reset_poms(){
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


usage(){
I_USAGE="

  Usage: ${NAME} [OPTIONS]

  Description:

    Build orcid-source war files for each project and then deploy to and artifact repo

    NOTE: credentials for the artifact repo are sourced from aws secrets but you still need your aws api access to be configured
          secretid is stored in shellkit.conf

  General usage:

    ${NAME} -t vx.x.x

  Required options:
      -t | --tag         )  tag ($tag)

"
  echo "$I_USAGE"
  exit

}

#
# args
#

while :
do
  case ${1-default} in
      --*help|-h         )  usage ; exit 0 ;;
      -t | --tag         )  tag=$2; shift 2 ;;
      -b | --build_envs   )  build_envs=$2; shift 2 ;;
      -v | --verbose )       verbose_arg='-v' VERBOSE=$((VERBOSE+1)); shift ;;
      --) shift ; break ;;
      -*) echo "WARN: Unknown option (ignored): $1" >&2 ; shift ;;
      *)  break ;;
    esac
done

sk-arg-check tag

tag_numeric=$(echo "$tag" | tr -dc '[:digit:].')
echo_log "building for: $tag_numeric"

#
# setup build environment from .tool-versions
#
echo_log "configure build environment for $checkout_name $tag_numeric"

sk-asdf-install-tool-versions
# set JAVA_HOME
. ~/.asdf/plugins/java/set-java-home.bash
_asdf_java_update_java_home

sk-dir-make ~/log
sk-dir-make ~/.m2/orcid-source-repo/

#
# cleanup
#
sleep 2

echo_log "cleanup for $checkout_name $tag_numeric"

reset_poms

build_log_general_file=~/log/orcid-source-tests-${tag_numeric}.log
echo_log "general log file: $build_log_general_file"

mvn clean -l $build_log_general_file
rm -Rf ~/.m2/orcid-source-repo/org/orcid

#
# build and version bump
#

echo_log "bumping versions installing build dependencies into custom local maven repo ~/.m2/orcid-source-repo"

# bump the tagged version in the poms tied to the parent pom
mvn versions:set -DnewVersion=$tag_numeric -DgenerateBackupPoms=false --settings settings-custom.xml -l $build_log_general_file

# bump the tagged version in the poms of projects not tied to the parent pom
mvn versions:set -DnewVersion=$tag_numeric -DgenerateBackupPoms=false --projects orcid-test --settings settings-custom.xml -l $build_log_general_file


sleep 2
# install orcid-parent into our local maven repo because the builds depend a version tagged release
mvn --non-recursive clean install -DskipTests --settings settings-custom.xml -l $build_log_general_file

# install orcid-test into our local maven repo because the builds depend a version tagged release
mvn --projects orcid-test clean install -DskipTests --settings settings-custom.xml -l $build_log_general_file

# install orcid-utils into our local maven repo because the builds depend a version tagged release
mvn --projects orcid-utils clean install -DskipTests --settings settings-custom.xml -l $build_log_general_file

# install orcid-persistence into our local maven repo because orcid-core depends on it
mvn --projects orcid-persistence clean install -DskipTests --settings settings-custom.xml -l $build_log_general_file

# install orcid-core into our local maven repo because the builds depend a version tagged release
mvn --projects orcid-core clean install -DskipTests --settings settings-custom.xml -l $build_log_general_file

# install orcid-api-common into our local maven repo because orcid-web deploy depends a version tagged release
mvn --projects orcid-api-common clean install -DskipTests --settings settings-custom.xml -l $build_log_general_file


find ~/.m2/orcid-source-repo/ -name 'orcid*' | tee -a $build_log_general_file

sleep 2

for project in orcid-message-listener orcid-activemq orcid-api-web orcid-internal-api orcid-pub-web orcid-scheduler-web orcid-web;do
  build_log_file=~/log/orcid-source-test-${project}-${tag_numeric}.log
  echo_log "project build log: $build_log_file"
  mvnd test \
    --settings settings-custom.xml \
    --projects $project -l $build_log_file
done

sk-time-spent

