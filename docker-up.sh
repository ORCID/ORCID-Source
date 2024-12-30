#!/usr/bin/env bash

docker compose --profile db up -d --remove-orphans

sleep 20

docker compose --profile dev up -d --remove-orphans
