![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
[![Build Status](https://app.travis-ci.com/DouglasLiebl/Library-API.svg?branch=main)](https://app.travis-ci.com/github/DouglasLiebl/Library-API) 
[![codecov](https://codecov.io/gh/DouglasLiebl/Library-API/graph/badge.svg?token=8XMRBH9EOM)](https://codecov.io/gh/DouglasLiebl/Library-API)

###
# Library-API 
This is a project that takes care of the management of books and loans in a library using the TDD (Test Driven Development) technique.
Uses Java 17 and Spring Boot 3.1.3 and PostgreSQL as database despite using the H2 database due to TravisCI
## Installation

1. Clone the repository:

```bash
git clone https://github.com/DouglasLiebl/Library-API.git
```

2. Install dependencies with Maven

3. (Optional) This project is using H2 database but you can change it. Install [PostgresSQL](https://www.postgresql.org/) and put those lines on application.yaml:
```bash
  jpa:
    database: postgresql
    hibernate:
     ddl-auto: update
    show-sql: true
    properties:
      hibernate:
       jdbc:
          lab:
            non_contextual_creation: true
    generate-ddl: true

  datasource:
    url: jdbc:postgresql://localhost:5432/ # Your database name
    username: # Your postgres username
    password: # Your postgres password
    driver-class-name: org.postgresql.Driver
```

## Usage

1. Just start the application.
2. (Using PostgreSQL) Make sure your PostgreSQL is running on your machine.

## Usage with docker

1. Open the command prompt in the project folder and type the commands bellow:
```bash
docker build -t preferred-name-for-image .
```
   then
```bash
docker run --name name-for-conteiner -p 8080:8080 image-name
```

2. If you wanna use PostgreSQL put this different part on your application.yaml:
```bash
 datasource:
    url: jdbc:postgresql://postgresql:5432/library-api
```
  and type this command:
```bash
docker compose up --build
```
