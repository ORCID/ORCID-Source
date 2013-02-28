#!/bin/bash
#
# =============================================================================
#
# ORCID (R) Open Source
# http://orcid.org
#
# Copyright (c) 2012-2013 ORCID, Inc.
# Licensed under an MIT-Style License (MIT)
# http://orcid.org/open-source-license
#
# This copyright and license information (including a link to the full license)
# shall be included in its entirety in all copies or substantial portion of
# the software.
#
# =============================================================================
#


#
# Script to deploy fonts from GitHub repo to frontend web app dir.
#
# You will need an ssh key set up (as the user that you are running
# this script as) with access to GitHub, unless the fonts already
# exist in the ORCID home directory.

WEBAPP_ROOT_DIR=/opt/semantico/slot/orcid-frontend/1/sitecode/webapps/ROOT/static
FONTS_SRC_DIR=~orcid/ORCID-Fonts-Dot-Com
FONTS_DEPLOY_DIR=${WEBAPP_ROOT_DIR}/ORCID-Fonts-Dot-Com
SCRIPT_NAME=`basename $0`
TMP_DIR=/tmp/${SCRIPT_NAME%.sh}_$$

mkdir $TMP_DIR

echo "Deploying fonts..."
while true
do
  if [ -d $WEBAPP_ROOT_DIR ]
  then
    break
  else
    echo "Waiting for the static content directory to appear..."
    sleep 3
  fi
done

if [ ! -d $FONTS_SRC_DIR ]
then
  echo "Cloning fonts from GitHub..."
  cd $TMP_DIR
  git clone 'git@github.com:ORCID/ORCID-Fonts-Dot-Com.git'
  chmod -R 777 $TMP_DIR
  sudo -u orcid cp -r $TMP_DIR/* $FONTS_SRC_DIR
fi

if [ ! -d $FONTS_DEPLOY_DIR ]
then
  echo "Copying fonts to web app dir..."
  sudo -u orcid cp -r $FONTS_SRC_DIR $FONTS_DEPLOY_DIR
fi

rm -r $TMP_DIR

echo "Finished deploying fonts"
