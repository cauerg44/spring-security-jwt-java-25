FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar cache de dependências
COPY pom.xml /app

# Baixa dependências (será cacheado se pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Agora copia o código fonte
COPY src /app/src

# Compila a aplicação
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre-alpine

# Cria usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/api-0.0.1-SNAPSHOT.jar /app/app.jar

WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]