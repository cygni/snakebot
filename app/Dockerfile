FROM docker.snake.cygni.se:5000/java8:latest
MAINTAINER emil.breding@bisnode.com

EXPOSE 8080

COPY build/libs/snakebot-app-*.jar /snakebot-app.jar

CMD java -jar -Xmx4096m -Dspring.profiles.active=production /snakebot-app.jar
