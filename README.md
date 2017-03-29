# snakebot
[![Build Status](http://jenkins.snake.cygni.se/buildStatus/icon?job=snakebot)](http://jenkins.snake.cygni.se/job/snakebot/)

A multi player, server based snake game for computer bots as players

Clients connect via a websocket through which events and commands are sent bidirectionally.
Several client implementations exist:

* [Java](https://github.com/cygni/snakebot-client-java)
* [.NET](https://github.com/cygni/snakebot-client-dotnet)
* [JavaScript](https://github.com/cygni/snakebot-client-js)
* [C++](https://github.com/cygni/snakebot-client-cpp)
* [Rust](https://github.com/cygni/snakebot-client-rust)
* [ClojureScript](https://github.com/cygni/snakebot-client-clojurescript)
* [Go](https://github.com/cygni/snakebot-client-golang)

**Note:** you need JavaFX for the server to build properly. So if you haven't already installed it, do so now.

To clean and build:
```
> ./gradlew clean build
```

To run server locally:
```
> ./gradlew bootRun
```

To run server locally with increased memory:
```
> export JAVA_OPTS="-Xmx4096m" && ./gradlew bootRun
```


To generate Spring Boot self contained artifact:
```
> ./gradlew clean bootRepackage
```

If you change the client code/api and want to test locally, you need to publish your new snapshot locally before the code in the snake clients project can see your changes:
```
> ./gradlew publishToMavenLocal
```

## To test production-like environment locally
Start ElasticSearch:
```
> docker run -d -p 9200:9200 -p 9300:9300 -v ~/tmp/es-config:/usr/share/elasticsearch/config -v ~/tmp/es-data:/usr/share/elasticsearch/data --name=es elasticsearch:2.4 -Des.network.host=0.0.0.0
```

Start Kibana:
```
> docker run --name kibana --link es:elasticsearch -p 5601:5601 kibana
```

Update local host file:
```
> sudo echo "127.0.0.1    elasticsearch" >> /etc/hosts
```

Start the application from your IDE with production profile:
```
-Dspring.profiles.active=production
```

Create the Elasticsearch indexes by following these [instructions](app/docs/elasticsearch.md)
