terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "5.19.0"
    }
  }
}

provider "google" {
  credentials = file("account.json")

  project = "snakebot-416414" # GCP project ID
  region  = "europe-north1"
  zone    = "europe-north1-a"
}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

resource "google_container_cluster" "gke_cluster" {
  name     = "snakebot-cluster"
  location = "europe-north1"

  deletion_protection = false

  node_pool {
    name               = "default-pool"
    initial_node_count = 1
  }

  provisioner "local-exec" {
    # Run the `gcloud` command to authenticate against the GKE cluster.
    # This will set kubectl's current-context to the GKE cluster and allow
    # Terraform to manage the cluster.
    command = "gcloud container clusters get-credentials snakebot-cluster --region europe-north1"
  }
}

resource "kubernetes_deployment" "snakebot_server" {
  metadata {
    name = "snakebot-server"
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "snakebot-server"
      }
    }

    template {
      metadata {
        labels = {
          app = "snakebot-server"
        }
      }

      spec {
        container {
          name  = "snakebot-server"
          image = "cygni/snakebot:latest"

          args = [
            "java",
            "-jar",
            "-Xms1G",
            "snakebot.jar",
            "--snakebot.redirect.url=http://${kubernetes_service.snakebot_web_service.status.0.load_balancer.0.ingress.0.ip}:8090/",
            "--snakebot.view.url=http://${kubernetes_service.snakebot_web_service.status.0.load_balancer.0.ingress.0.ip}:8090/viewgame/"
          ]
        }
      }
    }
  }
}

resource "kubernetes_service" "snakebot_server_service" {
  metadata {
    name = "snakebot-server-service"
  }

  spec {
    selector = {
      app = "snakebot-server"
    }

    port {
      port        = 8080
      target_port = 8080
    }

    type = "LoadBalancer"
  }
}

resource "kubernetes_deployment" "snakebot_web" {
  metadata {
    name = "snakebot-web"
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "snakebot-web"
      }
    }

    template {
      metadata {
        labels = {
          app = "snakebot-web"
        }
      }

      spec {
        container {
          name  = "snakebot-web"
          image = "cygni/snakebot-reactclient:latest"

          env {
            name  = "API_URL"
            value = "http://${kubernetes_service.snakebot_server_service.status.0.load_balancer.0.ingress.0.ip}:8080"
          }

          env {
            name  = "NODE_ENV"
            value = "production"
          }

          port {
            container_port = 80
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "snakebot_web_service" {
  metadata {
    name = "snakebot-web"
  }

  spec {
    selector = {
      app = "snakebot-web"
    }

    port {
      port        = 8090
      target_port = 80
    }

    type = "LoadBalancer"
  }
}


output "cluster_name" {
  value = google_container_cluster.gke_cluster.name
}

output "cluster_endpoint" {
  value = google_container_cluster.gke_cluster.endpoint
}

output "web_service_ip" {
  value = kubernetes_service.snakebot_web_service.status.0.load_balancer.0.ingress.0.ip
}

output "server_service_ip" {
  value = kubernetes_service.snakebot_server_service.status.0.load_balancer.0.ingress.0.ip
}
