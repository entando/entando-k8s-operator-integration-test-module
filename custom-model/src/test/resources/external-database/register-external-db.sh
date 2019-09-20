#!/usr/bin/env bash
export DATABASE_SERVICE_NAME=myoracle
export ADMIN_USER=sys
export ADMIN_PASSWORD=Oradoc_db1
export DATABASE_SERVICE_HOST=192.168.0.100
export DATABASE_SERVICE_PORT=1526
kubectl create -f - <<EOF
apiVersion: v1
kind: Secret
metadata:
  name: ${DATABASE_SERVICE_NAME}-secret
type: Opaque
stringData:
  user: $ADMIN_USER
  password: $ADMIN_PASSWORD
EOF