#!/bin/bash

# Check for pending changes
if ! git diff-index --quiet HEAD --; then
  echo "Error: There are pending changes. Please commit or stash them before running the script."
  exit 1
fi

# Function to process each .properties file
process_file() {
  local file=$1
  echo "Processing file: $file"
  sed -i.bak -e 's/[[:space:]]*$//' -e 's/[[:space:]]*=[[:space:]]*/=/' "$file"
  rm "${file}.bak"
  echo "Processed file: $file"
}

export -f process_file

# Function to handle tx pull, rename, and delete operations
tx_operations() {
  local lang=$1
  local ext=$2
  
  echo ">>>>>>>>>>>>>>>>>>>>> Pulling translations for $lang"
  tx pull --force -l "$lang"
  wait
  find . -type f -name "*_${lang}.properties" -exec sh -c '
    for file; do
      mv "$file" "${file%_'$lang'.properties}_'$ext'.properties"
    done
  ' sh {} +
  echo ">>>>>>>>>>>>>>>>>>>>> Finished processing $lang files."
}

# Perform git operations
echo "Checking out to main and pulling latest changes..."
git checkout main
git pull

echo "Checking out to transifex and pulling latest changes..."
git checkout transifex
git pull

echo "Merging transifex branch with main branch..."
git merge main

echo ">>>>>>>>>>>>>>>>>>>>> Pulling general translations..."
tx pull --force --all
wait

# Perform tx pull operations for specified languages
tx_operations "tr_TR" "tr"
tx_operations "pl_PL" "pl"

# Find all .properties files and process them
# find . -type f -name "*.properties" -exec bash -c 'process_file "$0"' {} \;

# Delete all ca.properties and uk.properties files
find . -type f -name "*_ca.properties" -delete
find . -type f -name "*_uk.properties" -delete

echo ">>>>>>>>>>>>>>>>>>>>> Finished processing general files"
