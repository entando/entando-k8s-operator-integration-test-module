# Entando Kubernetes App Controller

This project produces the Entando App Controller image. It is a run-to-completion style image
that processes a single EntandoApp custom resource and deploys a the app image and supporting
images as specified.

For more information on how to configure this image, please consult the documentation in the [entando-k8s-operator-common](https://github.com/entando-k8s/entando-k8s-operator-common) project


# Build the artifact

```bash
mvn clean -B test \
    org.jacoco:jacoco-maven-plugin:prepare-agent \
    org.jacoco:jacoco-maven-plugin:report \
    -Ppre-deployment-verification -Ppost-deployment-verification
```

# Build the docker image

```bash
mvn package -Pjvm
docker build . -f Dockerfile.jvm --no-cache -t {{image-full-name}}
```

# Notes

 - The variable `ENTANDO_DEPLOYMENT_PARALLELISM` defines how many deployments to start in parallel. The accepted range is 1-3 and the default value is 3.
