# Entando Kubernetes Operator

This project produces the Entando Kubernetes Operator images.

# How to build

## Compile

```
mvn clean package -Pjvm
```

## Image build

From project base dir:

```
cd app-controller && docker build . -f Dockerfile.jvm -t {image}
cd ../app-plugin-link-controller && docker build . -f Dockerfile.jvm -t {image}
cd ../database-service-controller && docker build . -f Dockerfile.jvm -t {image}
cd ../dbjob && docker build . -f Dockerfile.jvm -t {image}
cd ../keycloak-controller && docker build . -f Dockerfile.jvm -t {image}
cd ../plugin-controller && docker build . -f Dockerfile.jvm -t {image}
cd ../controller-coordinator && docker build . -f Dockerfile.jvm -t {image}
```
# How to test

```
ENTANDO_DEFAULT_ROUTING_SUFFIX=apps.autotest.eng-entando.com   mvn -DpreDeploymentTestGroups=unit  -Ppre-deployment-verification,jvm clean test
```

