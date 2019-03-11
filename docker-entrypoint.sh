#!/bin/bash
set -e

echo "Updating MikTex package repository..."
mpm --update-db
echo "Updating MikTex package repository done"

echo "Starting microservice..."
exec java -jar /app/microservice.jar