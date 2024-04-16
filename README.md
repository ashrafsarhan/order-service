# Order Service

This service manages the orders and items for a simple web store.

## Getting Started

These instructions will help you to get the project up and running on your local machine for development and testing purposes.


### Prerequisites

- Java 21

### Building the project

Navigate to the project directory:

```bash
cd order-service
```

#### Unix based system

Build the project using the Maven wrapper script:

```bash
./mvnw clean install
```

#### Windows

On Windows, use the mvnw.cmd script instead:

```bash
mvnw.cmd clean install
```

* Note: If we don't have the specified Maven in the wrapper properties, it'll be downloaded and installed in the folder
  $USER_HOME/.m2/wrapper/dists of the system.

### Building the project with integration tests

To build the project and run the integration tests, use the following command:

```bash
./mvnw clean test-compile failsafe:integration-test
```

### Running the project

#### Using Spring-Boot

Run the project using the Spring Boot Maven plugin:

```bash
./mvnw spring-boot:run
```

* By default, the application will run on port 8080. You can specify a different port with the --server.port argument:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8000
```

#### Using Docker

* Docker build

```bash
docker build -t order-service .
```

* Docker run with embedded H2 database

```bash
docker run -p 8080:8080 order-service
```

* Docker run with Postgres database
* Create a network
```bash
docker network create --driver bridge order-svc-net
```
̈́* Run the Postgres container
```bash
docker run --name postgresql -e POSTGRES_USER=order_service -e POSTGRES_PASSWORD=order_service -p 5432:5432 --network order-svc-net -d postgres 
```
* Run the order-service container
```bash
docker run --name order_service -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=postgres" --network order-svc-net -d order-service
```

#### Swagger
You access the swagger documentation by the following URL:
http://127.0.1.1:8080/order-service/swagger

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.3/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.3/maven-plugin/reference/html/#build-image)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.2.3/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Liquibase Migration](https://docs.spring.io/spring-boot/docs/3.2.3/reference/htmlsingle/index.html#howto.data-initialization.migration-tool.liquibase)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.2.3/reference/htmlsingle/index.html#web)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

