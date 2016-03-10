# snakebot
[![Build Status](http://jenkins.snake.cygni.se/buildStatus/icon?job=Snake)](http://jenkins.snake.cygni.se/job/Snake/)

A multi player, server based snake game for computer bots as players

Clients connect via a websocket through which events and commands are sent bidirectionally.
Several client imlementations exists, check here: [Cygni Snake implementations](https://github.com/cygni/snakebot-clients)

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

