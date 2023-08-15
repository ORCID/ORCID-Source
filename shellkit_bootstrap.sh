# test if we can have a key to checkout shellkit
source shellkit.conf

_git_clone_or_fetch_local(){
  # skip any git operations
  if [[ "$SHELLKIT_GIT_CHECKOUT" -eq 0 ]];then
    return
  fi

  # basic check to see if repo already exists
  if [[ -d ${SHELLKIT_LOCAL_PATH}/.git ]];then
    git -C $SHELLKIT_LOCAL_PATH reset --hard
    # fetch new branches
    git -C $SHELLKIT_LOCAL_PATH fetch --all
    git -C $SHELLKIT_LOCAL_PATH checkout $SHELLKIT_TAG
    git -C $SHELLKIT_LOCAL_PATH pull --no-edit
  else
    git clone $SHELLKIT_GIT_URL $SHELLKIT_LOCAL_PATH
    git -C $SHELLKIT_LOCAL_PATH checkout $SHELLKIT_TAG
  fi
}

_shellkit_source(){

  for shellkit in $SHELLKIT_PATHS;do
    if [[ -d $shellkit ]];then
      source $shellkit/profile.d/shellkit.sh
      break
    fi
  done
}

fatal(){ echo "failed to load shellkit" ; exit 1; }

###################################################################

_git_clone_or_fetch_local

_shellkit_source

# test function that is part of shellkit, if it fails to run we exit
sk-test-true > /dev/null || fatal
