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
echo_log "configure build environment for orcid-angular $tag_numeric"

sk-asdf-install-tool-versions
# set JAVA_HOME
. ~/.asdf/plugins/java/set-java-home.bash
_asdf_java_update_java_home

sk-dir-make ~/log

echo $AWS_SECRET_ID
# source the secrets for the artifact uploads
sk-aws-secret-source $AWS_SECRET_ID

echo ${ARTIFACT_URL}${ARTIFACT_REPO_PATH}

export ARTIFACT_USER=$ARTIFACT_USER
export ARTIFACT_PASSWORD=$ARTIFACT_PASSWORD

for project in orcid-message-listener orcid-activemq orcid-api-web orcid-internal-api orcid-pub-web orcid-scheduler-web orcid-web;do

  build_log_file=~/log/orcid-source-${project}-${tag_numeric}.log

  mvn --batch-mode \
      --settings settings-custom-deploy.xml \
      --file "${project}/pom.xml" \
      -Dmaven.test.skip \
      -DaltReleaseDeploymentRepository=github::${ARTIFACT_URL}${ARTIFACT_REPO_PATH} \
      deploy -Dmaven.test.skip -l $build_log_file
done

