#!/bin/bash
set -e
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE DATABASE cashew_test_db;
  GRANT ALL PRIVILEGES ON DATABASE cashew_test_db TO $POSTGRES_USER;
EOSQL
