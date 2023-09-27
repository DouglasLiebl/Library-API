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

3. (Optional) Install [PostgresSQL](https://www.postgresql.org/) and put those lines on application.yaml:
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
```
