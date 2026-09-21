#!/usr/bin/env bash
set -e

NAME="$(basename "${0}")"

usage(){
  echo "
  Usage: ${NAME} [OPTIONS] [SERVICE...]

  Description:
    Build docker images for orcid-source services.
    If no services are specified, builds all dev profile services.

  Examples:
    ${NAME}                  # build all
    ${NAME} ui               # build just the ui (orcid-web)
    ${NAME} papi             # build just the papi (orcid-pub-web)
    ${NAME} ui papi          # build ui and papi
    ${NAME} --no-cache ui    # build ui without docker cache

  Options:
    --no-cache    Do not use docker layer cache
    -h | --help   Show this help
"
  exit 0
}

no_cache=""
services=()

while :; do
  case ${1-} in
    --help|-h)   usage ;;
    --no-cache)  no_cache="--no-cache"; shift ;;
    --)          shift; break ;;
    -*)          echo "WARN: Unknown option (ignored): $1" >&2; shift ;;
    *)           break ;;
  esac
done

services=("$@")

if [ ${#services[@]} -eq 0 ]; then
  echo "Building all dev profile images..."
  docker compose --profile dev build $no_cache
else
  echo "Building: ${services[*]}"
  docker compose build $no_cache "${services[@]}"
fi
