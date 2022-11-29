#!/bin/bash
ORG="$1"
NAME="entando-k8s-$2"
TAG="$3"
BASE_DIR=$3
echo "basedir: $BASE_DIR"
echo "image: $ORG/$NAME:$TAG"
docker build -f Dockerfile.jvm -t "$ORG/$NAME:$TAG" .
