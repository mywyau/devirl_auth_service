#!/bin/bash
set -e

echo "Checking if shared_db exists..."
DB_SHARED_EXISTS=$(psql -U "$POSTGRES_USER" -d postgres -tc "SELECT 1 FROM pg_database WHERE datname = 'shared_db';" | xargs)

if [ "$DB_SHARED_EXISTS" != "1" ]; then
  echo "Creating shared_db..."
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE shared_db;
    GRANT ALL PRIVILEGES ON DATABASE shared_db TO $POSTGRES_USER;
EOSQL
else
  echo "Database shared_db already exists. Skipping creation."
fi

echo "Checking if shared_test_db exists..."
DB_TEST_EXISTS=$(psql -U "$POSTGRES_USER" -d postgres -tc "SELECT 1 FROM pg_database WHERE datname = 'shared_test_db';" | xargs)

if [ "$DB_TEST_EXISTS" != "1" ]; then
  echo "Creating shared_test_db..."
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE shared_test_db;
    GRANT ALL PRIVILEGES ON DATABASE shared_test_db TO $POSTGRES_USER;
EOSQL
else
  echo "Database shared_test_db already exists. Skipping creation."
fi
