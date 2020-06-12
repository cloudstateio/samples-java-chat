# Cloudstate samples

## Friends service - Java implementation

This is an implementation of the friends service, which is part of Lightbend Cloudstate Chat sample.

The user code is written in Java and uses a 100% Java toolchain (no Scala tools needed).

### Preparing to build

Install Maven

http://maven.apache.org/download.cgi

### Build the package and Docker image

```
mvn package
mvn io.fabric8:docker-maven-plugin:0.26.1:build
```

### Push the image

```
mvn io.fabric8:docker-maven-plugin:0.26.1:push
```

### Basic Testing

One way to test your service is to run it in a Docker container, and have it connect to the Cloudstate side-car (also known as Cloudstate proxy). The following steps will allow you test the friends service.

In a terminal run the Cloudstate side-car:

`docker run -it --rm --network mynetwork --name cloudstate -p 9000:9000 cloudstateio/cloudstate-proxy-dev-mode -Dcloudstate.proxy.user-function-port=8080 -Dcloudstate.proxy.user-function-interface=java-friends`

Open a second terminal to run the friends user function container (built as above).

`docker run -it --rm --name java-friends --network mynetwork justinhj/java-friends`