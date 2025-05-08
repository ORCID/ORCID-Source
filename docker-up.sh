#!/usr/bin/env bash
set -e

mkdir -p /opt/docker/logs/reg-ui
mkdir -p /opt/docker/logs/reg-papi
mkdir -p /opt/docker/logs/reg-mapi

docker compose --profile db up -d --remove-orphans

echo "sleeping 5s"
sleep 5

docker compose --profile dev up -d --remove-orphans
