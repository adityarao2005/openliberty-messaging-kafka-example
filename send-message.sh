#!/bin/bash

# Script to send a message to the Kafka publisher and check consumer values

# Check if a name was provided
if [ -z "$1" ]; then
    echo "Usage: ./send-message.sh <name>"
    echo "Example: ./send-message.sh Alice"
    exit 1
fi

NAME="$1"
PUBLISHER_URL="http://localhost:9080/api/hello"
CONSUMER_A_URL="http://localhost:9081/api/hello/current"
CONSUMER_B_URL="http://localhost:9082/api/hello/current"

echo "================================================"
echo "Sending message with name: $NAME"
echo "================================================"

# Send POST request to publisher
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$PUBLISHER_URL" \
    -H "Content-Type: application/json" \
    -d "{\"name\": \"$NAME\"}")

HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 204 ] || [ "$HTTP_CODE" -eq 200 ]; then
    echo "✓ Message sent successfully (HTTP $HTTP_CODE)"
else
    echo "✗ Failed to send message (HTTP $HTTP_CODE)"
    echo "Response: $BODY"
    exit 1
fi
