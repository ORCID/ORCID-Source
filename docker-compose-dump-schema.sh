#!/usr/bin/env bash
set -e

NAME="$(basename "${0}")"

usage(){
  echo "
  Usage: ${NAME} [OPTIONS]

  Description:
    Generate a fresh schema dump from a running PostgreSQL database for use
    in Docker dev environment. Outputs to docker-entrypoint-initdb.d/4-orcid-schema.sql

    The dump includes schema only (no data), with ownership statements preserved
    so the 'orcid' user owns all objects when loaded in Docker.

  Examples:
    ${NAME} -h qa-db.orcid.org -U orcid           # dump from QA
    ${NAME} -h localhost -p 5432 -U orcid          # dump from local
    ${NAME} --docker                                # dump from running Docker postgres container
    ${NAME} --process my-raw-dump.sql               # normalize an existing dump file in place

  Options:
    -h | --host       Database host (default: localhost)
    -p | --port       Database port (default: 5432)
    -U | --user       Database user (default: orcid)
    -d | --database   Database name (default: orcid)
    --docker          Dump from the running Docker postgres container
    -f | --process    Skip the dump and only apply the post-processing steps
                      (\\c orcid, idempotent schema, strip version headers) to an
                      existing pg_dump file, in place
    --help            Show this help
"
  exit 0
}

host="localhost"
port="5432"
user="orcid"
database="orcid"
use_docker=false
process_file=""

while :; do
  case ${1-} in
    --help)      usage ;;
    -h|--host)   host="$2"; shift 2 ;;
    -p|--port)   port="$2"; shift 2 ;;
    -U|--user)   user="$2"; shift 2 ;;
    -d|--database) database="$2"; shift 2 ;;
    --docker)    use_docker=true; shift ;;
    -f|--process) process_file="$2"; shift 2 ;;
    --)          shift; break ;;
    -*)          echo "Unknown option: $1" >&2; exit 1 ;;
    *)           break ;;
  esac
done

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ -n "$process_file" ]; then
  if [ ! -f "$process_file" ]; then
    echo "ERROR: File to process not found: ${process_file}" >&2
    exit 1
  fi
  OUTPUT_FILE="$process_file"
  echo "Processing existing dump file..."
  echo "  Source: ${OUTPUT_FILE} (no database dump)"
else
  OUTPUT_FILE="${SCRIPT_DIR}/docker-entrypoint-initdb.d/4-orcid-schema.sql"

  echo "Generating schema dump..."

  if [ "$use_docker" = true ]; then
    container=$(docker compose ps -q postgres 2>/dev/null)
    if [ -z "$container" ]; then
      echo "ERROR: No running postgres container found. Start with: docker compose --profile db up -d" >&2
      exit 1
    fi
    echo "  Source: Docker container ${container:0:12}"
    docker exec "$container" pg_dump --schema-only --schema=public --no-comments -U "$user" "$database" > "$OUTPUT_FILE"
  else
    echo "  Source: ${host}:${port}/${database} (user: ${user})"
    pg_dump --schema-only --schema=public --no-comments -h "$host" -p "$port" -U "$user" "$database" > "$OUTPUT_FILE"
  fi
fi

# Prepend \c orcid after the header comments so the script switches to the orcid database
# (Docker entrypoint runs init scripts against the 'postgres' database by default)
if ! grep -q '\\c orcid' "$OUTPUT_FILE"; then
  sed -i.bak '/^SET statement_timeout/i\
\\c orcid\
' "$OUTPUT_FILE"
  rm -f "${OUTPUT_FILE}.bak"
fi

# Fix CREATE SCHEMA public to be idempotent (schema already exists in fresh postgres)
sed -i.bak 's/^CREATE SCHEMA public;/CREATE SCHEMA IF NOT EXISTS public;/' "$OUTPUT_FILE"
rm -f "${OUTPUT_FILE}.bak"

# Strip pg_dump version header lines so re-dumps from different patch versions don't churn the diff
sed -i.bak '/^-- Dumped /d' "$OUTPUT_FILE"
rm -f "${OUTPUT_FILE}.bak"

echo "  Output: ${OUTPUT_FILE}"
echo "  Lines:  $(wc -l < "$OUTPUT_FILE")"
echo ""
echo "Done. To apply, run:"
echo "  ./docker-down.sh && ./docker-compose-cleandb.sh && ./docker-up.sh"
