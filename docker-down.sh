#!/usr/bin/env bash
set -e

docker compose --profile db down  --remove-orphans

echo "sleeping 5s"
sleep 5

docker compose --profile dev down  --remove-orphans
