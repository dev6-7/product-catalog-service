# product-catalog-service
Technical interview test: Kotlin/Spring Boot Products Catalog API

Java version: 21  
Kotlin version: 2

1) git clone <repo-url> && cd <project-dir>

2) local run (profile dev)  
./gradlew bootRun --args='--spring.profiles.active=dev'

Swagger UI: http://localhost:8080/swagger-ui

# Docker

./gradlew bootJar
docker compose up --build
and after open http://localhost:8080/swagger-ui

# Tests
./gradlew test -Pit
