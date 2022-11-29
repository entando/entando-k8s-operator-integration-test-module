# Entando Kubernetes Operator

This project produces the Entando Kubernetes Operator images.

# How to build

## Compile

```
mvn clean package -Pjvm,license
```

**Notes:**
* `jvm` is the standard profile
* you can omit `license` profile if you want just compile or test, it is jus needed by image build

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

You can use the following command **preferably** in an environment **without** kubeconfig or similar
```
 ENTANDO_DEFAULT_ROUTING_SUFFIX={hostname}
  \ mvn
  \ -DpreDeploymentTestGroups=unit,in-process
  \ -Ppre-deployment-verification,jvm 
  \ clean test
```

**Notes:**
* To activate mvn test plugin you need to use a specific profile
* the profile `pre-deployment-verification` is used to execute test without deploy artifacts, but for some test you need a working kubeconfig
* the profile `post-deployment-verification` is used to execute integration tests which need a deployed artificats
* environment varible ENTANDO_DEFAULT_ROUTING_SUFFIX to use a selected hostname as prefix in differente test about ingress and other components
