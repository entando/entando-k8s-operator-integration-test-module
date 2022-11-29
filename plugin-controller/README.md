# Entando Kubernetes Plugin Controller

This project produces the Entando Plugin Controller image. It is a run-to-completion style image
that processes a single EntandoPlugin custom resource and deploys a the plugin image and supporting
images as specified.

# How to build

```
mvn clean package -Pjvm
docker build . -f Dockerfile.jvm -t {image}
```

# Further info

For more information on how to configure this image, please consult the documentation in the [entando-k8s-operator-common](https://github.com/entando-k8s/entando-k8s-operator-common) project

