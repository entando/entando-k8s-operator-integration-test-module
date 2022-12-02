# Entando Kubernetes Operator build tools

This project contains the Entando Kubernetes Operator build tools and utilities.

# Tools list

## Checkstyle

This project contains the checkstyle configurations used to build a jar artifact used from other modules [Checkstyle Multi Module Guide](https://maven.apache.org/plugins/maven-checkstyle-plugin/examples/multi-module-config.html)

## Build pom for snyk scan

```
mvn -Pbuild-pom-snyk install
```
