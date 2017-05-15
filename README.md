# akka-seeds

## Build

Create docker needs

```bash
sbt compile
sbt cpJarsForDocker
```

There are the docker artifacts.

```
target/docker/
├── Dockerfile
├── app
│   └── akka-seeds_2.12-0.1.0.jar
├── libs
│   ├── aeron-client-1.2.5.jar
│   ├── aeron-driver-1.2.5.jar
│   ├── agrona-0.9.5.jar
│   ├── akka-actor_2.12-2.5.1.jar
│   ├── akka-cluster-metrics_2.12-2.5.1.jar
│   ├── akka-cluster_2.12-2.5.1.jar
│   ├── akka-protobuf_2.12-2.5.1.jar
│   ├── akka-remote_2.12-2.5.1.jar
│   ├── akka-slf4j_2.12-2.5.1.jar
│   ├── akka-stream_2.12-2.5.1.jar
│   ├── config-1.3.1.jar
│   ├── logback-classic-1.1.3.jar
│   ├── logback-core-1.1.3.jar
│   ├── netty-3.10.6.Final.jar
│   ├── reactive-streams-1.0.0.jar
│   ├── scala-java8-compat_2.12-0.8.0.jar
│   ├── scala-library-2.12.2.jar
│   ├── scala-parser-combinators_2.12-1.0.4.jar
│   ├── slf4j-api-1.7.23.jar
│   └── ssl-config-core_2.12-0.2.1.jar
├── mainClass
└── tag

```
## How to use
ENV values
- CLUSTER_NAME=cluster1
- SEED_PORT=2551
- HOST_NAME=host1
- SEED_NODES=192.168.1.10,host1

```bash
docker run --rm [image] -e SEED_PORT=2552
```