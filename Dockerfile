# Build
FROM gradle:7.4.2-jdk17
WORKDIR /app
COPY . .
ENV PORT=8080
EXPOSE 8080
RUN gradle clean build

# Run
FROM amazoncorretto:17.0.4
WORKDIR /app
COPY --from=0 /app/app/build/libs/app-*.jar ./snakebot.jar

# To run in digitalocean profile, send in env variable PROFILE_FLAG=-Dspring.profiles.active=digitalocean
# Note: There is a problem with the way this is setup. Ctrl+C will not work.
# Using Spring Boot's environment variable did not work when we tried to use it.
EXPOSE 8080
CMD java -jar -Xms1G snakebot.jar
