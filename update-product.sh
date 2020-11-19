#!/bin/bash

set -e

if [ "$#" -ne 3 ]; then
  echo "Usage: $0 <url> <edit-token> <changelog.md>" >&2; exit 1
fi

URL=$1
TOKEN=$2
CHANGELOG=$(<$3)

# https://stackoverflow.com/a/13466143
json_escape () {
  printf '%s' "$1" | python -c 'import json,sys; print(json.dumps(sys.stdin.read()))'
}

ESCAPED_CHANGELOG=`json_escape "$CHANGELOG"`

JSON="{\"changelog\":$ESCAPED_CHANGELOG}"

# https://stackoverflow.com/a/34887246
STATUS=`curl \
  -X PATCH "${URL}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  --data "@-" \
  --fail \
  --silent \
  --write-out "%{http_code}\n" \
  --output /dev/null \
  <<<"$JSON"`
# require HTTP status 2xx
# (consider 3xx an error as well)
if [ "$STATUS" -lt 200 ] || [ "$STATUS" -ge 300 ]; then
  echo "Received unexpected HTTP status $STATUS" >&2; exit 2
fi
