# RadarCOVID Authentication Service

<p align="center">
    <a href="https://github.com/RadarCOVID/radar-covid-backend-authentication-server/commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/RadarCOVID/radar-covid-backend-authentication-server?style=flat"></a>
    <a href="https://github.com/RadarCOVID/radar-covid-backend-authentication-server/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/RadarCOVID/radar-covid-backend-authentication-server?style=flat"></a>
    <a href="https://github.com/RadarCOVID/radar-covid-backend-authentication-server/blob/master/LICENSE" title="License"><img src="https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg?style=flat"></a>
</p>

## Introduction

Authentication Service in terms of the Radar COVID project enables:

## Prerequisites

These are the frameworks and tools used to develop the solution:

- [Java 11](https://openjdk.java.net/).
- [Maven](https://maven.apache.org/).
- [Spring Boot](https://spring.io/projects/spring-boot) version 2.3.
- [Lombok](https://projectlombok.org/), to help programmer. Developers have to include the IDE plugin to support Lombok features (ie, for Eclipse based IDE, go [here](https://projectlombok.org/setup/eclipse)).
- [ArchUnit](https://www.archunit.org/) is used to check Java architecture.
- [PostgreSQL](https://www.postgresql.org/).
- Testing:
    - [Spock Framework](http://spockframework.org/).
    - [Docker](https://www.docker.com/), because of using Testcontainers.
    - [Testcontainers](https://www.testcontainers.org/).
- Monitoring:
    - [Micrometer](https://micrometer.io/).

## Installation and Getting Started

### Building from Source

To build the project, you need to run this command:

```shell
mvn clean package -P <environment>
```

Where `<environment>` has these possible values:

- `local-env`. To run the application from local (eg, from IDE o from Maven using `mvn spring-boot:run`). It is the default profile, using [`application.yml`](./authentication-server-boot/src/main/resources/application.yml) configuration file. If any properties need to be modified, you can create application-local.yml configuration file.
- `docker-env`. To run the application in a Docker container with `docker-compose`, using [`application.yml`](./authentication-server-boot/src/main/resources/application.yml) configuration file. If any properties need to be modified, you can create application-docker.yml configuration file.
- `pre-env`. To run the application in the Preproduction environment. Preproduction environment properties are configured in the infrastructure.
- `pro-env`. To run the application in the Production environment. Production environment properties are configured in the infrastructure

The project also uses Maven profile `aws-env` to include dependencies when it is running on AWS environment, so the compilation command for Preproduction and Production environments would be:

```shell
mvn clean package -P pre-env,aws-env
mvn clean package -P pro-env,aws-env
```

All profiles will load the default [configuration](./authentication-server-boot/src/main/resources/application.yml).

### Running the Project

Depends on the environment you selected when you built the project, you can run the project:

- From the IDE, if you selected `local-env` environment (or you didn't select any Maven profile).
- From Docker. Once you build the project, you will have in `authentication-server-boot/target/docker` the files you would need to run the application from a container (`Dockerfile` and the Spring Boot fat-jar).

If you want to run the application inside a Docker container in local, once you built it, you should run:

```shell
docker-compose up -d smtp
docker-compose up -d postgres
docker-compose up -d backend
```

#### Database

This project doesn't use either [Liquibase](https://www.liquibase.org/) or [Flyway](https://flywaydb.org/) because:

1. DB-Admins should only have database privileges to maintain the database model ([DDL](https://en.wikipedia.org/wiki/Data_definition_language)).
2. Applications should only have privileges to maintain the data ([DML](https://en.wikipedia.org/wiki/Data_manipulation_language)).

Because of this, there are two scripts:

- [`01-AUTHENTICATION-DDL.sql`](./sql/01-AUTHENTICATION-DDL.sql). Script to create the model.
- [`02-AUTHENTICATION-DML.sql`](./sql/02-AUTHENTICATION-DML.sql). Script with inserts.

### API Documentation

Along with the application there comes with [OpenAPI Specification](https://www.openapis.org/), which you can access in your web browser when the Verification Service is running (unless in Production environment, where it is inactive by default):

```shell
<base-url>/openapi/api-docs
```
You can download the YAML version in `/openapi/api-docs.yaml`

If running in local, you can get:
- OpenAPI: http://localhost:8080/openapi/api-docs
- Swagger UI: http://localhost:8080/openapi/ui

#### Keys generation

This service uses [Elliptic Curve (EC)](https://en.wikipedia.org/wiki/Elliptic-curve_cryptography) keys to allow users to login and manage users with admin role.

To generate the keys you can use these commands ([OpenSSL](https://www.openssl.org/) tool is required):

1. Generate private key:
    ```shell
    openssl ecparam -name secp521r1 -genkey -noout -out generated_private.pem
    ```
2. Converse private key to new PEM format:
    ```shell
    openssl pkcs8 -topk8 -inform pem -in generated_private.pem -outform pem -nocrypt -out generated_private_new.pem
    ```
3. Get Base64 from private key:
    ```shell
    openssl base64 -in generated_private_new.pem > generated_private_base64.pem
    ```
4. Generate public key:
    ```shell
    openssl ec -in generated_private_new.pem -pubout -out generated_pub.pem
    ```
5. Get Base64 from public key:
    ```shell
    openssl base64 -in generated_pub.pem > generated_pub_base64.pem
    ```

### Modules

Authentication Service has four modules:

- `authentication-server-parent`. Parent Maven project to define dependencies and plugins.
- `authentication-server-api`. [DTOs](https://en.wikipedia.org/wiki/Data_transfer_object) exposed.
- `authentication-server-boot`. Main application, global configurations and properties. This module also has integration tests and Java architecture tests with ArchUnit.
- `authentication-server-service`. Business and data layers.

## Support and Feedback

The following channels are available for discussions, feedback, and support requests:

| Type       | Channel                                                |
| ---------- | ------------------------------------------------------ |
| **Issues** | <a href="https://github.com/RadarCOVID/radar-covid-backend-authentication-server/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/RadarCOVID/radar-covid-backend-authentication-server?style=flat"></a> |

## Contribute

If you want to contribute with this exciting project follow the steps in [How to create a Pull Request in GitHub](https://opensource.com/article/19/7/create-pull-request-github).

More details in [CONTRIBUTING.md](./CONTRIBUTING.md).

## License

This Source Code Form is subject to the terms of the [Mozilla Public License, v. 2.0](https://www.mozilla.org/en-US/MPL/2.0/).
