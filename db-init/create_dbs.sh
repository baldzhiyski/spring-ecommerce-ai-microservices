#!/bin/sh
set -e

echo "Creating extra databases if missing..."

# function to create a DB if it doesn't exist
create_db_if_missing() {
  DB="$1"
  psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname='${DB}'" \
    | grep -q 1 || psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d postgres -c "CREATE DATABASE ${DB};"
}

create_db_if_missing products
create_db_if_missing customers
create_db_if_missing orders

echo "DB creation script finished."
