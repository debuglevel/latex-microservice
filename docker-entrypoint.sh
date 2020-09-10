#!/bin/bash
set -e

echo "Updating MikTex package repository..."
mpm --update-db
RC=$?
echo "Updated MikTex package repository (return code: $RC)"
if [ $retVal -ne 0 ]; then
    echo "mpm returned with an error."
    echo "Printing /miktex/.miktex/texmfs/data/miktex/log/mpmcli.log which should contain some logs:"
    echo "==========="
    cat /miktex/.miktex/texmfs/data/miktex/log/mpmcli.log
    echo "==========="
    echo "Printed /miktex/.miktex/texmfs/data/miktex/log/mpmcli.log"
    echo "Exiting docker-entrypoint.sh with return code $RC"
    exit $RC
fi

# if environment variable INSTALL_PACKAGES is set, install those packages already at startup
if [[ ! -z "$INSTALL_PACKAGES" ]]; then
  INSTALL_PACKAGES_ARRAY=(${INSTALL_PACKAGES})
  for PACKAGE in "${INSTALL_PACKAGES_ARRAY[@]}"
    do
      echo "Installing package $PACKAGE..."
      mpm --require $PACKAGE
      RC=$?
      echo "Installed package $PACKAGE done (return code: $RC)"
    done
fi

echo "Starting microservice..."
exec java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -noverify -XX:TieredStopAtLevel=1 -Dcom.sun.management.jmxremote -jar /app/microservice.jar