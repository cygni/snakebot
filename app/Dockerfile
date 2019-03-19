FROM openjdk:11.0.2-oraclelinux7

EXPOSE 8080

COPY build/libs/snakebot-app-*.jar /snakebot-app.jar

CMD java -jar -Xmx4096m -Dspring.profiles.active=production /snakebot-app.jar
