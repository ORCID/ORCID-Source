#!/usr/bin/env bash

# Use this to test and initdb script sql file changes
#
docker compose down --volumes postgres

volume_name=$(basename `pwd` | tr '[:upper:]' '[:lower:]')_postgres_data

docker volume rm $volume_name -f

