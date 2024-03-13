## Users

If you are a user who **only** wants to code your own bot, then simply head to the [snakebot client respository](https://github.com/cygni/snakebot-client-js) and follow the instructions there. There will be a _docker-compose_ file there to easily get your own server and webclient running as containers without the need to clone them from here.

#

# SNAKEBOT

A multi player, server based snake game for computer bots as players

Clients connect via a websocket through which events and commands are sent bidirectionally.
Following client implementations are up to date:

<!-- - [Java](https://github.com/cygni/snakebot-client-java)
- [.NET](https://github.com/cygni/snakebot-client-dotnet) -->

- [TypeScript/JavaScript](https://github.com/cygni/snakebot-client-js)
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

## Production

Snakebot can be deployed on-demand to production on GCP as a Kubernetes cluster (GKE) using Terraform.

### Prerequisites

You need the following CLI tools installed:

- terraform
- gcloud (for authenticating to GCP)
- kubectl (optional if you want to control the cluster through CLI)

### Deployment

First navigate into the directory:

```bash
cd terraform
```

Initalize the terraform modules and providers:

```bash
terraform init
```

Authenticate with GCP:

```bash
gcloud auth application-default login
```

Deploy the cluster:

```bash
terraform apply
```

(Optional) If you want to use kubectl, make gcloud set the context to use the newly created cluster:

```bash
gcloud container clusters get-credentials snakebot-cluster --region europe-north1
```

### Destroying/deprovisioning

After you no longer need the cluster, you can deprovision it by running:

```bash
terraform destroy
```

**NOTE**: this requires the state files created from running `terraform apply`. If you didn't provision the cluster from the current computer you need to run `terraform apply` first.

#

### **Contact information for DockerHub and GCP access**

emil.breding@cygni.se

a.johansson@cygni.se

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
