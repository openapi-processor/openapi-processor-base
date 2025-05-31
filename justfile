default:
  @just --list --unsorted

# update gradle wrapper
wrapper version="8.14.1":
    ./gradlew wrapper --gradle-version={{version}}

insight:
    ./gradlew openapi-processor-core:dependencyInsight --configuration runtimeClasspath --dependency com.google.guava
