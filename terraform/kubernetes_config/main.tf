terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "2.27.0"
    }
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
            "--snakebot.redirect.url=http://${var.load_balancer_ip}/",
            "--snakebot.view.url=http://${var.load_balancer_ip}/viewgame/"
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

    type             = "LoadBalancer"
    load_balancer_ip = var.load_balancer_ip
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
            value = "http://${var.load_balancer_ip}:8080"
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
      port        = 80
      target_port = 80
    }

    type             = "LoadBalancer"
    load_balancer_ip = var.load_balancer_ip
  }
}
