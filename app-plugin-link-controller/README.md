# Entando Kubernetes App/Plugin Link Controller

This project produces the Entando App/Plugin Link Controller image. It is a run-to-completion style image
that processes a single EntandoAppPluginLink custom resource and sets up connectivity between a previously
deployed Entando App and Entando Plugin.

# How to build

```
mvn clean package -Pjvm
docker build . -f Dockerfile.jvm -t {image-version}:[image-version]
```

