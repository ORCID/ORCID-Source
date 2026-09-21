#!/usr/bin/env bash
set -e

mkdir -p /opt/docker/logs/reg-ui

docker compose --profile db up -d --remove-orphans

echo "Waiting for postgres to be ready and schema to be loaded..."

MAX_ATTEMPTS=30
ATTEMPT=0
while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
  ATTEMPT=$((ATTEMPT + 1))

  # Check if postgres is accepting connections AND the schema has loaded
  if docker compose exec -T postgres psql -U orcid -d orcid -c "SELECT 1 FROM identifier_type LIMIT 1" >/dev/null 2>&1; then
    echo "Postgres ready with schema loaded (${ATTEMPT}s)"
    break
  fi

  if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    echo "ERROR: Postgres did not become ready with schema within ${MAX_ATTEMPTS}s"
    echo "Check: docker compose logs postgres"
    exit 1
  fi

  sleep 1
done

docker compose --profile dev up -d --remove-orphans
