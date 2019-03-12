#!/bin/bash
set -e

echo "Updating MikTex package repository..."
mpm --update-db
echo "Updating MikTex package repository done"

# if environment variable INSTALL_PACKAGES is set, install those packages already at startup
if [[ ! -z "$INSTALL_PACKAGES" ]]; then
  INSTALL_PACKAGES_ARRAY=(${INSTALL_PACKAGES})
  for PACKAGE in "${INSTALL_PACKAGES_ARRAY[@]}"
    do
      echo "Installing package $PACKAGE..."
      mpm --require $PACKAGE
      echo "Installing package $PACKAGE done."
    done
fi

echo "Starting microservice..."
exec java -jar /app/microservice.jar