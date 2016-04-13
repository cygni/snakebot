# snakebot
[![Build Status](http://jenkins.snake.cygni.se/buildStatus/icon?job=Snake)](http://jenkins.snake.cygni.se/job/Snake/)

A multi player, server based snake game for computer bots as players

Clients connect via a websocket through which events and commands are sent bidirectionally.
Several client implementations exist, check here: [Cygni Snake implementations](https://github.com/cygni/snakebot-clients)

To clean and build:
```
> ./gradlew clean build
```

To run server locally:
```
> ./gradlew bootRun
```

To generate Spring Boot self contained artifact:
```
> ./gradlew clean bootRepackage
```

If you change the client code/api and want to test locally, you need to publish your new snapshot locally before the code in the snake clients project can see your changes:
```
> ./gradlew publishToMavenLocal
```
