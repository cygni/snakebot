## Users

If you are a user who **only** wants to code your own bot, then simply head to the [snakebot client respository](https://github.com/cygni/snakebot-client-js) and follow the instructions there. There will be a _docker-compose_ file there to easily get your own server and webclient running as containers without the need to clone them from here.

#

# SNAKEBOT

A multi player, server based snake game for computer bots as players

Clients connect via a websocket through which events and commands are sent bidirectionally.
Following client implementations are up to date:

<!-- - [Java](https://github.com/cygni/snakebot-client-java)
- [.NET](https://github.com/cygni/snakebot-client-dotnet) -->

- [JavaScript](https://github.com/cygni/snakebot-client-js)
<!-- - [C++](https://github.com/cygni/snakebot-client-cpp)
- [Rust](https://github.com/cygni/snakebot-client-rust)
- [ClojureScript](https://github.com/cygni/snakebot-client-clojurescript)
- [Go](https://github.com/cygni/snakebot-client-golang) -->

#

## To run locally

<br>

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

#

### **Contact information for DockerHub and DigitalOcean access**

emil.breding@cygni.se

a.johansson@cygni.se

#

### **Updates and Docker**

**IMPORTANT**: Commits on the **main** branch will launch an action that builds and **overrides** the docker image tagged as the latest on [DockerHub](https://hub.docker.com/r/cygni/snakebot/tags). Therefore it is important to **ONLY** push changes to **main** that works and have been tested, to ensure that latest image works for anyone who wants to use it. If a commit is deemed **stable** you can also add a **tag** to that commit to ensure it remains on [DockerHub](https://hub.docker.com/r/cygni/snakebot/tags) without getting overwritten. E.g creating a release with a tag will both push the newly build docker image with the tag latest **AND** the tag name given as long as it follows the standard **X.X.X** name.

Because of what mentioned above, when adding a new feature or changing some behavior, make sure to work on a **different branch** first before pushing to **main**.

### **Production**

**IMPORTANT**: Commits to the main branch will also act as commits towards production. Rebuilding the images on DockerHub through commits from main will cause the production server to reboot with the updated version of the image.

#

### **Hosting and Version tracking**

This system is hosted on the **_digitalocean_** platform. As mentioned above, commits to the main branch will automatically cause a forced rebuild and deployment of the updated image that is created. What version of the system that is currently running can be identified by a date and hashcode for that release. This can be seen in the app's name in the digitalocean repository. The hashcode is retrieved from the latest commit, allowing us to overview what version and changes that are made on the current version.

To change the version/tag of the docker image that is run on production, we have to modify this manually. Head over to the [digitalocean app](https://cloud.digitalocean.com/apps/389bcc1a-15cb-4512-89f2-cdc0d52aa3ea/overview) and navigate to the webclient component via the link on the overview page. Press the edit button for the "Source setting". New releases will automatically have the tag 'latest'. If you've added a custom tag for a commit and want to run this version on production, simply change the input in this field to the corresponding tag name.

![image info](./digitaloceanimg.png)

<!-- To run packaged server with overridden setting for game link:

```
> ./gradlew clean bootRepackage
> java -Xmx4096m -Dsnakebot.view.url=http://<your-ip>:8090/#/viewgame/ -jar app/build/libs/snakebot-app-0.1.18.jar
``` -->

<!-- To generate Spring Boot self contained artifact:

```
> ./gradlew clean bootRepackage
``` -->

<!-- To publish maven artifacts to repository:

```
> export mvn_snake_user='the_user'
> export mvn_snake_pwd='the_pwd'
> ./gradlew upload
``` -->

<!-- We're using a small java-app running on GCP: https://github.com/renaudcerrato/appengine-maven-repository

If you change the client code/api and want to test locally, you need to publish your new snapshot locally before the code in the snake clients project can see your changes:

```
> ./gradlew publishToMavenLocal
``` -->

<!-- ## To test production-like environment locally

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

Create the Elasticsearch indexes by following these [instructions](app/docs/elasticsearch.md) -->
