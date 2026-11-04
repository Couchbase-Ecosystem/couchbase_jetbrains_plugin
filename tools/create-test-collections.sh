#!/usr/bin/env bash
# Creates N collections (default 100) in a bucket/scope through the collections REST API,
# so large-scope paging, search and favorites can be tried against a local cluster.
#
# Usage: tools/create-test-collections.sh [count] [bucket] [scope] [host]
# Env:   CB_USER (default Administrator), CB_PASSWORD (default password)
set -euo pipefail

COUNT="${1:-100}"
BUCKET="${2:-travel-sample}"
SCOPE="${3:-inventory}"
HOST="${4:-http://localhost:8091}"
USER="${CB_USER:-Administrator}"
PASSWORD="${CB_PASSWORD:-password}"

created=0; existing=0
for i in $(seq -f "%03g" 1 "$COUNT"); do
  name="test_col_$i"
  status=$(curl -s -o /tmp/create-collection.out -w '%{http_code}' -u "$USER:$PASSWORD" \
    -X POST "$HOST/pools/default/buckets/$BUCKET/scopes/$SCOPE/collections" -d "name=$name")
  case "$status" in
    200) created=$((created + 1)) ;;
    400) if grep -q "already exists" /tmp/create-collection.out; then existing=$((existing + 1));
         else echo "Failed to create $name: $(cat /tmp/create-collection.out)"; exit 1; fi ;;
    *)   echo "Failed to create $name (HTTP $status): $(cat /tmp/create-collection.out)"; exit 1 ;;
  esac
done
echo "Created $created collections in $BUCKET.$SCOPE ($existing already existed)."
