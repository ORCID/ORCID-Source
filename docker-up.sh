#!/usr/bin/env bash

docker compose --profile db up -d

sleep 20

docker compose --profile dev up -d
