# Order Service

This service manages the orders and items for a simple web store.

# Getting Started

These instructions will help you getting the project up and running on your local machine for development and testing purposes.


### Prerequisites

- Java 21

### Building the project

Navigate to the project directory:

```bash
cd order-service

#### Unix based system

Build the project using the Maven wrapper script:

```
./mvnw clean install
```

#### Windows

On Windows, use the mvnw.cmd script instead:

```
mvnw.cmd clean install
```

* Note: If we don't have the specified Maven in the wrapper properties, it'll be downloaded and installed in the folder
  $USER_HOME/.m2/wrapper/dists of the system.

### Building the project with integration tests

To build the project and run the integration tests, use the following command:

```
./mvnw clean test-compile failsafe:integration-test
```

### Running the project

#### Using Spring-Boot

Run the project using the Spring Boot Maven plugin:

```
./mvnw spring-boot:run
```

* By default, the application will run on port 8080. You can specify a different port with the --server.port argument:

```
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8000
```

#### Swagger
You access the swagger documentation by the following URL

```
http://127.0.1.1:8080/order-service/swagger

```

### Spring boot version needs to be updated to 3.2.4 to mitigate CVE-2024-22259, this version is supposed to be released by March 21, 2024
https://github.com/spring-projects/spring-boot/milestone/338

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

