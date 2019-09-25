# entando-k8s-custom-model
Entando's Custom Resource Definition model for Kubernetes. Used in our custom controllers as well as our Kubernetes 
infrastructure. Use this project when integrating Entando and Kubernetes.

Generally this project adheres to a similar builder pattern as the one used in the Fabric8 Kubernetes/Java client. 

# Usage

## Step 1: resolving the correct CustomResourceOperation

The first step in reading and/or manipulating Entando's custom resources, you need to resolve the 
CustomResourceOperation in the correct state. This object is required to use the Fabric8 Java client to
interact with custom resources in Kubernetes in a type safe manner.  
In order to do this, you may need to first deploy the correct 
custom resource definition. Here is a code snippet to 'lazily' deploy the custom resource definition for the 
EntandoApp custom resource, and then to resolve the correct CustomResourceOperation

```java
        entandoAppCrd = client.customResourceDefinitions().withName(EntandoApp.CRD_NAME).get();
        if (entandoAppCrd == null) {
            List<HasMetadata> list = client.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("crd/EntandoAppCRD.yaml")).get();
            entandoAppCrd = (CustomResourceDefinition) list.get(0);
            // see issue https://github.com/fabric8io/kubernetes-client/issues/1486
            entandoAppCrd.getSpec().getValidation().getOpenAPIV3Schema().setDependencies(null);
            client.customResourceDefinitions().create(entandoAppCrd);
        }
        return (CustomResourceOperationsImpl<EntandoApp, EntandoAppList, DoneableEntandoApp>) client
            .customResources(entandoAppCrd, EntandoApp.class, EntandoAppList.class, DoneableEntandoApp.class);

```

## Step 2: Building and creating a custom resource against the custom resource definition

There are different approaches one can use to create a new instance of custom resource. one approach is to use
the 'builder' class of that custom resource. This class can be identified by the name of the custom resource 
definition, e.g. `EntandoApp`, suffixed with the word `Builder`, e.g. `EntandoAppBuilder`. Here is an example
of how to build and create an `EntandoApp`

```java
        CustomResourceOperationsImpl<EntandoApp, EntandoAppList, DoneableEntandoApp> entandoApps=...
        EntandoApp entandoApp = new EntandoAppBuilder()
                .withNewMetadata().withName(MY_APP)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsImageVendor.MYSQL)
                .withEntandoImageVersion(ENTANDO_IMAGE_VERSION)
                .withJeeServer(JeeServer.WILDFLY)
                .withReplicas(5)
                .withTlsEnabled(true)
                .withIngressHostName(MYINGRESS_COM)
                .withKeycloakServer(MYKEYCLOAKNAMESPACE, MY_KEYCLOAK)
                .endSpec()
                .build();
        entandoApps.inNamespace(MY_NAMESPACE).create(entandoApp);

```

## Step 3: Retrieving and editing a custom resource against the custom resource definition

The Fabric8 Kubernetes Java client requires a class that exposes the 'builder' for the custom resource, but with
a callback to update the resource when the `done` method is invoked. The resulting fluent syntax looks very similar
to the traditional builder, but also allows for the retrieved model object to be updated on completion, e.g. 

```java
        entandoApps.inNamespace(MY_NAMESPACE).withName(MY_APP).edit()
                .editMetadata()
                    .addToLabels(MY_LABEL, MY_VALUE)
                .endMetadata()
                .editSpec()
                    .withDbms(DbmsImageVendor.MYSQL)
                    .withEntandoImageVersion(ENTANDO_IMAGE_VERSION)
                    .withJeeServer(JeeServer.WILDFLY)
                    .withReplicas(5)
                    .withTlsEnabled(true)
                    .withIngressHostName(MYINGRESS_COM)
                    .withKeycloakServer(MYKEYCLOAKNAMESPACE, MY_KEYCLOAK)
                .endSpec()
                .withStatus(new WebServerStatus("some-qualifier"))
                .withStatus(new DbServerStatus("another-qualifier"))
                .withPhase(EntandoDeploymentPhase.STARTED)
                .done();

```

# Known issues

## High parameter count in constructors

Please note that this pattern leads to constructors with an excessive parameter count. This constructor is declared
at package scope and is intended for internal use only. If you find yourself 
using one of these  constructors, please rather look at the builder class associated with the model class in 
question and build the model object from there

## Old version of Fabric8 Kubernetes Java client

Entando's operators are currently limited to the use of version 4.1.2 of the Fabric8 Kubernetes Java client. This
constraint is a result of using the Microbeans operators framework. We are actively looking at migrating to the
more active JVM Operators framework.

## HTTP 409 and 422 errors on 'edit'

During development of the Entando Kubernetes controllers, it was found that the `metadata.resourceVersion` of 
a custom resource can at times go out of sync with what it needs to be for an updated. This tends to happen
when an `HTTP PATCH` (e.g. using `DoneableResource.done()`) operation was issued on the resource in 
question, but with no differences compared  to the original resource. If you do encounter an 
`HTTP 409` or `HTTP 422` error code from Kubernetes, please  inspect previous edits to the 
resource to verify that the state has indeed changed. If no state changes can be found, 
put the necessary checks in place in your code to ensure that it does not issue the obsolete `HTTP PATCH` 
to the Kubernetes server

## HTTP 404 errors on 'edit' running against the Fabric8 Kubernetes Mock server


During our testing efforts, we have encountered a bug in the Fabric8 Kubernetes Mock server. When running an
`edit` operation against a custom resource whilst using the Mock server, it does not seem to find to resource
in question. It can however be found using the `list` operation. Currently there is no workaround for this problem.

