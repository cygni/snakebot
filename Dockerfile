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

# To run in digitalocean profile, send in env variable PROFILE_FLAG=-Dspring.profiles.active=digitalocean
# Note: There is a problem with the way this is setup. Ctrl+C will not work.
# Using Spring Boot's environment variable did not work when we tried to use it.
EXPOSE 8080
ENV PROFILE development
CMD java -jar app-0.1.21.jar --spring.profiles.active=${PROFILE}
