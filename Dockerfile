# Build
FROM gradle:7.4.2-jdk17
WORKDIR /app
COPY . .
ENV PORT=8080
EXPOSE 8080
RUN gradle clean build

# Run
FROM azul/zulu-openjdk:17-jre
WORKDIR /app
COPY --from=0 /app/app/build/libs/app-0.1.21.jar .
CMD ["java", "-jar", "app-0.1.21.jar"]
